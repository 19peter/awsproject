package org.peters.projectaws.Components.EC2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.peters.projectaws.Components.API.Api;
import org.peters.projectaws.Components.App.App;
import org.peters.projectaws.Components.Monitors.EC2TargetMonitor;
import org.peters.projectaws.Components.Monitors.TargetMonitor;
import org.peters.projectaws.Core.AWSObject;
import org.peters.projectaws.Interfaces.IntegrationInterfaces.ApiGateway.ApiGatewayIntegrationInterface;
import org.peters.projectaws.Interfaces.IntegrationInterfaces.LoadBalancer.TargetInterfaces.TargetStateObserverInterface;
import org.peters.projectaws.Interfaces.Lifecycle.LifecycleManager;
import org.peters.projectaws.dtos.Request.Request;
import org.peters.projectaws.dtos.Response.Response;
import org.peters.projectaws.enums.TargetState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

//Create EC2, Create TargetGroup and add EC2 to TargetGroup's targetList
//Then add the TargetGroup as an observer to the EC2's targetMonitor
public class EC2
        extends AWSObject
        implements ApiGatewayIntegrationInterface, LifecycleManager {

    private EC2TargetMonitor targetMonitor;
    private static final Logger logger = LogManager.getLogger(EC2.class);
    private List<Api> apis;
    private ExecutorService executor;
    private int maxConn;
    private int PORT = 4000;
    private App app;

    public EC2(int maxConn, String name) {
        super(name);
        this.maxConn = maxConn;
        logger.info("<EC2>: EC2 instance created with maxConn: " + maxConn + " and name: " + this.getName()
                + " and id: " + this.getId() + " on port: " + PORT);
    }

    public EC2(int maxConn, String name, int port) {
        super(name);
        this.maxConn = maxConn;
        PORT = port;
        logger.info("<EC2>: EC2 instance created with maxConn: " + maxConn + " and name: " + this.getName()
                + " and id: " + this.getId() + " on port: " + port);
    }

    /**
     * @deprecated
     *             EC2 instances will now accept an App instance OR an API Gateway
     *             API Resources instead of a List of api
     *             API Gateway API resources will be available after version 2.0.0
     */
    @Deprecated()
    public EC2(List<Api> apis, int maxConn, String name) {
        super(name);
        this.apis = apis;
        this.maxConn = maxConn;
        logger.info("<EC2>: EC2 instance created with maxConn: " + maxConn + " and name: " + this.getName()
                + " and id: " + this.getId());
    }

    public EC2(EC2 ec2) {
        this.maxConn = ec2.getMaxConn();
        this.app = ec2.getApp();
        this.setName("COPY::" + ec2.getName());
        logger.info("<EC2>: EC2 Copy instance created with maxConn: " + maxConn + " and name: " + this.getName()
                + " and id: " + this.getId() + " on port: " + PORT);
    }

    public boolean addObserver(TargetStateObserverInterface<EC2> observer) {
        if (this.getRunning()) {
            this.targetMonitor.addObserver(observer);
            logger.info("<EC2>: EC2 " + this.getName() + " added observer");
            return true;
        } else {
            logger.warn("<EC2>: CAN'T ADD OBSERVER: EC2 instance " + this.getName() + " is not running");
            return false;
        }
    }

    public boolean removeObserver(TargetStateObserverInterface<EC2> observer) {
        if (this.getRunning()) {
            this.targetMonitor.removeObserver(observer);
            return true;
        } else {
            logger.warn("<EC2>: CAN'T REMOVE OBSERVER: EC2 instance " + this.getName() + " is not running");
            return false;
        }
    }

    @Deprecated
    public void setApis(Api api) {
        if (apis != null)
            apis.add(api);
        else
            apis = new ArrayList<>();

        apis.add(api);

    }
    @Deprecated
    public void setApis(List<Api> apis) {
        this.apis = apis;
    }
    @Deprecated
    public List<Api> getApis() {
        return apis;
    }

    public App getApp() {
        return this.app;
    }

    public int getMaxConn() {
        return maxConn;
    }

    public Response executeApi(Request request) {
        if (executor == null || executor.isShutdown()) {
            logger.error("Executor is not initialized or has been shutdown");
            return null;
        }

        try {
            Future<Response> future = executor.submit(() -> {
                try {
                    if (provisionConnection()) { 
                        Response apiResponse = app.executeApi(request);
                        // Thread.sleep(Helpers.delayDuration);
                        logger.info("<EC2>: " + this.getName() + " Api Executed: Returned Code: " + apiResponse.getCode());
                        releaseConnection();
                        return apiResponse;
                    } else {
                        logger.info("<EC2>: EC2 " + this.getName() + " has no provisioned connections");
                        return null;
                    }
                } catch (Exception e) {
                    targetMonitor.setTargetUnhealthy();
                    logger.info("<EC2>: EC2 " + this.getName() + " is unhealthy");
                    throw new RuntimeException(e);
                }
            });

            // Wait for the result (with timeout)
            return future.get(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("<EC2> " + this.getName() + " " + e.getMessage());
            throw new RuntimeException("Request interrupted", e);
        } catch (Exception e) {
            logger.error("<EC2> " + this.getName() + " " + e.getMessage());
            targetMonitor.setTargetUnhealthy();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Response receiveFromGateway(Request request) throws InterruptedException {
        if (!this.getRunning()) {
            logger.error("<EC2>: EC2 " + this.getName() + " is not running");
            return null;
        }

        if (this.getTargetMonitorState() == TargetState.HEALTHY ||
                this.getTargetMonitorState() == TargetState.IDLE) {
            return executeApi(request);
        } else {
            logger.info("<EC2>: EC2 " + this.getName() + " is unhealthy");
            return null;
        }

    }

    @Override
    public void initialize() {
        if (!this.getRunning()) {
            logger.info("<EC2> -----INITIALIZING INSTANCE-----: " + this.getId());
            this.executor = Executors.newFixedThreadPool(maxConn);
            this.targetMonitor = new EC2TargetMonitor(this, maxConn, "MONITOR::" + this.getName());
            this.targetMonitor.initialize();
            if (this.apis == null)
                this.apis = new ArrayList<>();
            this.setRunning(true);
            logger.info("<EC2> -----INITIALIZED INSTANCE-----: " + this.getName() + " with maxConn: " + maxConn);
            logger.info("<EC2> -----INITIALIZED MONITOR-----: " + this.getName() + " Monitor instance "
                    + this.targetMonitor.getName());
        } else {
            logger.info("<EC2> instance " + this.getName() + " is already running");
        }
    }

    @Override
    public void shutdown() {
        if (this.getRunning()) {
            executor.shutdown();
            this.targetMonitor.shutdown();
            executor = null;
            this.targetMonitor = null;
            this.setRunning(false);
            logger.info("<EC2> instance " + this.getName() + " shutdown");
        }
    }

    @Override
    public boolean isRunning() {
        logger.info("<EC2> instance " + this.getName() + " isRunning: " + Boolean.toString(this.getRunning()));
        return this.isRunning();
    }

    public TargetState getTargetMonitorState() {
        return targetMonitor.getState();
    }

    public String getTargetMonitorName() {
        return targetMonitor.getName();
    }

    public int getRunningRequests() {
        return targetMonitor.getRunningRequests();
    }

    public void releaseConnection() {
        targetMonitor.removeRunningRequest();
    }

    public boolean provisionConnection() throws InterruptedException {
        return targetMonitor.addRunningRequest();
    }

}
