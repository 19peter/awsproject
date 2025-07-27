package org.peters.projectaws.Components.LoadBalancer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.peters.projectaws.Interfaces.IntegrationInterfaces.ApiGateway.ApiGatewayIntegrationInterface;
import org.peters.projectaws.Interfaces.IntegrationInterfaces.LoadBalancer.TargetIntegrationInterface;
import org.peters.projectaws.Interfaces.Lifecycle.LifecycleManager;
import org.peters.projectaws.dtos.Request.Request;
import org.peters.projectaws.dtos.Response.Response;
import org.peters.projectaws.Components.LoadBalancer.TargetGroup.Common.TargetGroup;
import org.peters.projectaws.Core.AWSObject;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoadBalancer 
extends AWSObject
implements ApiGatewayIntegrationInterface, LifecycleManager {
    private static final Logger logger = LogManager.getLogger(LoadBalancer.class);
    private ExecutorService executorService;
    private ConcurrentHashMap<String, TargetIntegrationInterface> targetGroupsRoutingRules;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final int TIMEOUT = 30000;

    public LoadBalancer() {
        this.targetGroupsRoutingRules = new ConcurrentHashMap<>();
        logger.info("<LoadBalancer>: LoadBalancer created with {} thread pool and Id: {}", executorService, this.getId());
    }

    @Override
    public void initialize() {
        if (!isRunning.get()) {
            this.executorService = Executors.newFixedThreadPool(10);
            isRunning.set(true);
            logger.info("<LoadBalancer>: LoadBalancer initialized with {} thread pool", executorService);
        } else {
            logger.warn("<LoadBalancer>: LoadBalancer is already initialized ");
        }
   }

    @Override
    public void shutdown() {
        if (!isRunning.get()) {
            logger.warn("<LoadBalancer>: LoadBalancer is not running");
            return;
        }

        logger.info("<LoadBalancer>: Initiating LoadBalancer shutdown");
    
        // Shutdown thread pool gracefully
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                    logger.error("<LoadBalancer>: ExecutorService did not terminate");
                }
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }

        // Clear routing rules
        targetGroupsRoutingRules.clear();
        
        isRunning.set(false);
        logger.info("<LoadBalancer>: LoadBalancer shutdown complete");
    }

    @Override
    public boolean isRunning() {
        logger.info("<LoadBalancer>: LoadBalancer isRunning: " + isRunning.get());
        return isRunning.get();
    }

    public void addTargetGroup(String path, TargetGroup targetGroup) {
        targetGroupsRoutingRules.put(path, targetGroup);
    }

    @Override
    public Response receiveFromGateway(Request request) throws InterruptedException, ExecutionException {
        if (!isRunning.get()) throw new IllegalStateException("LoadBalancer is not running");  
       
        String rule = request.getPath();
        if(!targetGroupsRoutingRules.containsKey(rule)) throw new IllegalArgumentException("Invalid path");

        logger.info("<LoadBalancer>: LoadBalancer received request: {} {}", request.getPath(), request.getMethod());
        TargetIntegrationInterface target = targetGroupsRoutingRules.get(rule);
        
        Future<Response> future = executorService.submit(
            () -> target.receiveFromLoadBalancer(request));

            try {
                return future.get(TIMEOUT, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                future.cancel(true);
                logger.warn("<LoadBalancer>: Request {} {} timed out", request.getPath(), request.getMethod());
                throw new RuntimeException("Request timed out");    
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
    }


}
