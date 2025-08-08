package org.peters.projectaws.Components.ApiGateway;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.peters.projectaws.Core.AWSObject;
import org.peters.projectaws.Interfaces.Integration.ApiGateway.ApiGatewayIntegration;
import org.peters.projectaws.Interfaces.RequestServer.RequestServer;
import org.peters.projectaws.dtos.Request.Request;
import org.peters.projectaws.dtos.Response.Response;

import java.util.concurrent.*;

public class ApiGateway 
extends AWSObject
implements RequestServer {

    private static final Logger logger = LogManager.getLogger(ApiGateway.class);

    ConcurrentHashMap<String, ApiGatewayIntegration> routingRules;
    final ExecutorService executorService = Executors.newFixedThreadPool(10);


    public ApiGateway(String name) {
        super(name);
        this.routingRules = new ConcurrentHashMap<>();
        logger.info("<ApiGateway>: ApiGateway created: " + this.getId());
    }

    public boolean addRule(String rule, ApiGatewayIntegration target) {
        if(routingRules.containsKey(rule)) return false;
        routingRules.put(rule, target);
        return true;
    }

    public boolean removeRule(String rule) {
        if (!routingRules.containsKey(rule)) return false;
        routingRules.remove(rule);
        return true;
    }

    @Override
    public Response serve(Request request) {
        String rule = request.getPath();
        if(!routingRules.containsKey(rule)) return null;
        ApiGatewayIntegration target = routingRules.get(rule);
        Response response = new Response("404", "Request Server Not Found");
        executorService.submit(() -> {
            try {
                return target.receiveFromGateway(request);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        return response;
    }

    public void shutdownAndAwait() {
        executorService.shutdown(); // Stop accepting new tasks
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow(); // Force shutdown if not finished
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }


}
