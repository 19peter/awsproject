package org.peters.projectaws.Components.EC2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.peters.projectaws.Components.API.Api;
import org.peters.projectaws.Components.Monitors.EC2TargetMonitorDecorator;
import org.peters.projectaws.Core.AWSObject;
import org.peters.projectaws.Helpers.Helpers;
import org.peters.projectaws.Interfaces.IntegrationInterfaces.ApiGateway.ApiGatewayIntegrationInterface;
import org.peters.projectaws.Interfaces.IntegrationInterfaces.LoadBalancer.TargetInterfaces.TargetStateObserverInterface;
import org.peters.projectaws.Interfaces.Lifecycle.LifecycleManager;
import org.peters.projectaws.Main;
import org.peters.projectaws.dtos.Request.Request;
import org.peters.projectaws.dtos.Response.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class EC2
        extends AWSObject
        implements ApiGatewayIntegrationInterface, LifecycleManager {

    private static final Logger logger = LogManager.getLogger(Main.class);
    private List<Api> apis;
    private EC2TargetMonitorDecorator targetMonitor;
    private ExecutorService executor;
    private int maxConn;

    public EC2(int maxConn) {
        this.maxConn = maxConn;
        logger.info("EC2 instance created with maxConn: " + maxConn + " and id: " + this.getId());
    }

    public EC2(List<Api> apis, int maxConn) {
        this.apis = apis;
        this.maxConn = maxConn;
        logger.info("EC2 instance created with maxConn: " + maxConn + " and id: " + this.getId());
    }

    public void addObserver(TargetStateObserverInterface observer) {
        this.targetMonitor.addObserver(observer);
    }

    public void removeObserver(TargetStateObserverInterface observer) {
        this.targetMonitor.removeObserver(observer);
    }

    public void setApis(Api api) {
        apis.add(api);
    }

    public Response executeApi(Request request) {
        String method = request.getMethod();
        String path = request.getPath();
        String data = request.getData();

        Optional<Api> apiCheck = apis.stream()
                .filter(api -> api.getPath().equals(path) && api.getType().equals(method))
                .findFirst();

        if (apiCheck.isEmpty()) {
            System.out.println("API does not exist: Method: " + method + " Path: " + path);
            return null;
        }

        try {
            Future<Response> future = executor.submit(() -> {
                try {
                    this.targetMonitor.addRunningRequest();
                    logger.info("Executing API in EC2:" + this.getId() +
                            " with method: " + method +
                            " And params: " + data +
                            " And FnName: " + apiCheck.get().getName());

                    Response api = apiCheck.get().getFn().execute(data);
                    Thread.sleep(Helpers.delayDuration);
                    logger.info("Api Executed: Returned Code: " + api.getCode());
                    return api;
                } catch (Exception e) {
                    targetMonitor.setTargetUnhealthy();
                    throw new RuntimeException(e);
                } finally {
                    this.targetMonitor.removeRunningRequest();
                }
            });

            // Wait for the result (with timeout)
            return future.get(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Request interrupted", e);
        } catch (Exception e) {
            targetMonitor.setTargetUnhealthy();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Response receiveFromGateway(Request request) throws InterruptedException {
        return executeApi(request);
    }

    @Override
    public void initialize() {
        if (!this.isRunning()) {
            this.executor = Executors.newFixedThreadPool(maxConn);
            this.targetMonitor = new EC2TargetMonitorDecorator(maxConn);
            if (this.apis == null) this.apis = new ArrayList<>();
            this.setRunning(true);
            logger.info("EC2 instance " + this.getId() + " initialized");
        } else {
            logger.info("EC2 instance " + this.getId() + " is already running");
        }
    }

    @Override
    public void shutdown() {
        if (this.isRunning()) {
            executor.shutdown();
            executor = null;
            targetMonitor = null;
            this.setRunning(false);
            logger.info("EC2 instance " + this.getId() + " shutdown");
        }
    }

    @Override
    public boolean isRunning() {
        logger.info("EC2 instance " + this.getId() + " isRunning: " + this.isRunning());
        return this.isRunning();
    }

}
