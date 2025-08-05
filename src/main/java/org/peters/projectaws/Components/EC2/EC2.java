package org.peters.projectaws.Components.EC2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.peters.projectaws.Components.API.Api;
import org.peters.projectaws.Components.App.App;
import org.peters.projectaws.Components.SecurityGroups.SecurityGroup;
import org.peters.projectaws.Components.SecurityGroups.SecurityRules.SecurityProtocols;
import org.peters.projectaws.Components.Monitors.EC2TargetMonitor;
import org.peters.projectaws.Core.AWSObject;
import org.peters.projectaws.Interfaces.Integration.ApiGateway.ApiGatewayIntegration;
import org.peters.projectaws.Interfaces.Integration.LoadBalancer.TargetInterfaces.TargetStateObserverInterface;
import org.peters.projectaws.Interfaces.Lifecycle.LifecycleManager;
import org.peters.projectaws.dtos.Request.Request;
import org.peters.projectaws.dtos.Response.Response;
import org.peters.projectaws.enums.TargetState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

//Create EC2, Create TargetGroup and add EC2 to TargetGroup's targetList
//Then add the TargetGroup as an observer to the EC2's targetMonitor
public class EC2
        extends AWSObject
        implements ApiGatewayIntegration, LifecycleManager {

    private static final Logger logger = LogManager.getLogger(EC2.class);
    private EC2TargetMonitor targetMonitor;
    private List<Api> apis;
    private ExecutorService executor;
    private EC2Types type;
    private int allocatedCPU;
    private List<App> apps;
    private List<SecurityGroup> securityGroups;

    public EC2(int allocatedCPU, String name, EC2Types type) {
        super(name);
        this.allocatedCPU = allocatedCPU;
        this.type = type;
        logger.info("<EC2>: EC2 instance created with allocatedCPU: " + allocatedCPU + " and name: " + this.getName()
                + " and id: " + this.getId());
    }

    /**
     * @deprecated
     *             EC2 instances will now accept an App instance OR an API Gateway
     *             API Resources instead of a List of api
     *             API Gateway API resources will be available after version 2.0.0
     */
    @Deprecated()
    public EC2(List<Api> apis, int allocatedCPU, String name, EC2Types type) {
        super(name);
        this.apis = apis;
        this.allocatedCPU = allocatedCPU;
        this.type = type;
        logger.info("<EC2>: EC2 instance created with allocatedCPU: " + allocatedCPU + " and name: " + this.getName()
                + " and id: " + this.getId());
    }

    public EC2(EC2 ec2) {
        this.allocatedCPU = ec2.getMaxConn();
        this.apps = ec2.getApps();
        this.setName("COPY::" + ec2.getName());
        logger.info("<EC2>: EC2 Copy instance created with allocatedCPU: " + allocatedCPU + " and name: " + this.getName()
                + " and id: " + this.getId());
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

    public void attachSecurityGroup(SecurityGroup securityGroup) {
        if (securityGroup == null) throw new IllegalArgumentException("SecurityGroup cannot be null");
        if ((this.securityGroups.stream().filter(sg -> sg.getId() == securityGroup.getId()).findAny().isPresent())) {
            logger.warn("<EC2>: CAN'T ATTACH SECURITY GROUP: Security Group " + securityGroup.getId() + " is already attached to EC2 " + this.getName());
            return;
        }
        this.securityGroups.add(securityGroup);
    }

    public void detachSecurityGroup(SecurityGroup securityGroup) {
        if (securityGroup == null) throw new IllegalArgumentException("SecurityGroup cannot be null");
        if (!this.securityGroups.stream().filter(sg -> sg.getId() == securityGroup.getId()).findAny().isPresent()) {
            logger.warn("<EC2>: CAN'T DETACH SECURITY GROUP: Security Group " + securityGroup.getId() + " is not attached to EC2 " + this.getName());
            return;
        }
        this.securityGroups.remove(securityGroup);
    }

    public List<SecurityGroup> getSecurityGroups() {
        return this.securityGroups;
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

    public List<App> getApps() {
        return this.apps;
    }

    public int getMaxConn() {
        return allocatedCPU;
    }

    public EC2Types getEC2Type() {
        return type;
    }

    public Response executeApi(Request request) {
        if (executor == null || executor.isShutdown()) {
            logger.error("Executor is not initialized or has been shutdown");
            return null;
        }

        App app = apps.get(request.getPort());
        if (app == null) {
            logger.error("App not found on Port: " + request.getPort());
            return new Response("404");
        }

        if (!this.securityGroups.stream().anyMatch(sg -> sg.isInboundPortAndProtocolAllowed(request.getPort(), SecurityProtocols.TCP))) {
            logger.error("Port " + request.getPort() + " is not allowed");
            return new Response("403", "Port " + request.getPort() + " is not allowed");
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
            if (this.securityGroups == null) {
                logger.warn("<EC2>: CAN'T INITIALIZE EC2: SecurityGroups is null");
                return;
            }
                
            logger.info("<EC2> -----INITIALIZING INSTANCE-----: " + this.getId());
            this.executor = Executors.newFixedThreadPool(allocatedCPU);
            this.targetMonitor = new EC2TargetMonitor(this, allocatedCPU, "MONITOR::" + this.getName());
            this.targetMonitor.initialize();
            if (this.apis == null)
                this.apis = new ArrayList<>();
            this.setRunning(true);
            logger.info("<EC2> -----INITIALIZED INSTANCE-----: " + this.getName() + " with allocatedCPU: " + allocatedCPU);
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
