package org.peters.projectaws.Components.ApiGateway;


import org.peters.projectaws.Core.AWSObject;
import org.peters.projectaws.Interfaces.IntegrationInterfaces.ApiGateway.ApiGatewayIntegrationInterface;
import org.peters.projectaws.dtos.Request.Request;
import java.util.logging.Logger;
import java.util.concurrent.*;

public class ApiGateway extends AWSObject {

    private static final Logger logger = Logger.getLogger(ApiGateway.class.getName());
    ConcurrentHashMap<String, ApiGatewayIntegrationInterface> routingRules;
    final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public ApiGateway() {
        this.routingRules = new ConcurrentHashMap<>();
        logger.info("ApiGateway created: " + this.getId());
    }

    public boolean addRule(String rule, ApiGatewayIntegrationInterface target) {
        if(routingRules.containsKey(rule)) return false;
        routingRules.put(rule, target);
        return true;
    }

    public boolean removeRule(String rule) {
        if (!routingRules.containsKey(rule)) return false;
        routingRules.remove(rule);
        return true;
    }

    public boolean routeAsync(Request request) {
        String rule = request.getPath();
        if(!routingRules.containsKey(rule)) return false;
        ApiGatewayIntegrationInterface target = routingRules.get(rule);
        executorService.submit(() -> {
            try {
                target.receiveFromGateway(request);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        return true;
    }

    public boolean routeSync(Request request) throws InterruptedException, ExecutionException {
        String rule = request.getPath();
        if(!routingRules.containsKey(rule)) return false;
        ApiGatewayIntegrationInterface target = routingRules.get(rule);
        target.receiveFromGateway(request);
        return true;
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
