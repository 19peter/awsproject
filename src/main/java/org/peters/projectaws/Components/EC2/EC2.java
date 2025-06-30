package org.peters.projectaws.Components.EC2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.peters.projectaws.Components.API.Api;
import org.peters.projectaws.Components.Monitors.EC2TargetMonitor;
import org.peters.projectaws.Components.Monitors.TargetState;
import org.peters.projectaws.Helpers.Helpers;
import org.peters.projectaws.Interfaces.IntegrationInterfaces.ApiGateway.ApiGatewayIntegrationInterface;
import org.peters.projectaws.Main;
import org.peters.projectaws.dtos.Request.Request;
import org.peters.projectaws.dtos.Response.Response;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
    import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class EC2
        extends EC2TargetMonitor
        implements ApiGatewayIntegrationInterface {

    private static final Logger logger = LogManager.getLogger(Main.class);
    private final List<Api> apis;

    public EC2(int maxConn) {
        super(maxConn, ThreadLocalRandom.current().nextInt());
        this.apis = new ArrayList<>();
        logger.info("EC2 instance created with maxConn: " + maxConn + " and id: " + this.getId());
    }
    public EC2(List<Api> apis, int maxConn) {
        super(maxConn, ThreadLocalRandom.current().nextInt());
        this.apis = apis;
        logger.info("EC2 instance created with maxConn: " + maxConn + " and id: " + this.getId());
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
            // Create a Future for the async execution
            Future<Response> future = executor.submit(() -> {
                try {
                    logger.info("Executing API in EC2:" + this.getId() + 
                        " with method: " + method +
                        " And params: " + data + 
                        " And FnName: " + apiCheck.get().getName());

                    Response api = apiCheck.get().getFn().execute(data);
                    Thread.sleep(Helpers.delayDuration);                    
                    logger.info("Api Executed: Returned Code: " + api.getCode());
                    return api;
                } catch (Exception e) {
                    setTargetUnhealthy();
                    throw new RuntimeException(e);
                }
            });

            // Wait for the result (with timeout)
            return future.get(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Request interrupted", e);
        } catch (Exception e) {
            setTargetUnhealthy();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Response receiveFromGateway(Request request) throws InterruptedException {
        return executeApi(request);
    }

}
