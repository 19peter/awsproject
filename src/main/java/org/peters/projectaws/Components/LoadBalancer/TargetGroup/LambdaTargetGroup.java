package org.peters.projectaws.Components.LoadBalancer.TargetGroup;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.peters.projectaws.Annotations.Benchmark.BenchmarkProxy;
import org.peters.projectaws.Components.Lambda.Lambda;
import org.peters.projectaws.Components.Lambda.LambdaExecutionContext;
import org.peters.projectaws.Interfaces.Lambda.LambdaHandler;
import org.peters.projectaws.dtos.Request.Request;
import org.peters.projectaws.dtos.Response.Response;
import org.peters.projectaws.enums.TargetState;
import java.util.concurrent.CopyOnWriteArrayList;

public class LambdaTargetGroup extends TargetGroup<LambdaExecutionContext> {

    private static final Logger logger = LogManager.getLogger(LambdaTargetGroup.class);
    Lambda lambdaFunction;
    ExecutorService executor;

    public LambdaTargetGroup(String path, Lambda lambdaFunction) {
        super(path);
        targetsList = new CopyOnWriteArrayList<>();
        this.lambdaFunction = lambdaFunction;
        this.executor = Executors.newFixedThreadPool(5);
    }

    @Override
    public Response receiveFromLoadBalancer(Request request) throws InterruptedException {
        logger.info(
                "<LambdaTargetGroup>: TargetGroup " + this.getPath() + " received request: " + request.getPath() + " "
                        + request.getMethod() + " " + request.getData());

        Optional<LambdaExecutionContext> executionContext = getAvailableInstance();

        if (executionContext.isEmpty()) {
            logger.info("<LambdaTargetGroup>: TargetGroup " + this.getPath() + " couldn't find a target");
            return null;
        }

        return processRequest(executionContext.get(), request);
    }

    @Override
    public void onTargetStateChanged(LambdaExecutionContext target, TargetState newState) {
        if (newState != TargetState.OVERLOADED) {
            this.executor.submit(() -> {
                try {
                    Thread.sleep(5000);
                    if (target.targetMonitor.getState() != TargetState.OVERLOADED) {
                        targetsList.remove(target);
                        logger.info("<LambdaTargetGroup>: TargetGroup " + this.getPath() + " removed target: "
                                + target.getId());
                    }
                } catch (Exception e) {
                    logger.error("<LambdaTargetGroup>: TargetGroup " + this.getPath() + " " + e.getMessage());
                }
            });
        }
    }

    @Override
    public void onRunningRequestsChanged(LambdaExecutionContext target) {
    }

    @Override
    Optional<LambdaExecutionContext> getAvailableInstance() {
        if (targetsList.isEmpty()) {
            logger.info("<LambdaTargetGroup>: TargetGroup " + this.getPath() + " is empty");
            LambdaExecutionContext lambdaExecutionContext = new LambdaExecutionContext("", 0, 0);
            targetsList.add(lambdaExecutionContext);
            return Optional.of(lambdaExecutionContext);
        
        } else {

            Optional<LambdaExecutionContext> contextTarget = Optional.empty();
            for (LambdaExecutionContext target : targetsList) {
                if (target.targetMonitor.getState() == TargetState.HEALTHY) {
                    contextTarget = Optional.of(target);
                }
            }

            if (contextTarget.isEmpty()) {
                LambdaExecutionContext lambdaExecutionContext = new LambdaExecutionContext("", 0, 0);
                targetsList.add(lambdaExecutionContext);
                contextTarget = Optional.of(lambdaExecutionContext);
            }

            return contextTarget;
        }
    }

    @Override
    public void addTarget(LambdaExecutionContext target) {
        if (targetsList.size() == 1) {
            logger.info("<LambdaTargetGroup>: TargetGroup " + this.getPath() + " already has a target");
            return;
        }
        targetsList.add(target);
        logger.info("<LambdaTargetGroup>: TargetGroup " + this.getPath() + " added target: " + target.getId());
    }

    @Override
    public void removeTarget(LambdaExecutionContext target) {
        if (targetsList.size() == 0) {
            logger.info("<LambdaTargetGroup>: TargetGroup " + this.getPath() + " doesn't have a target");
            return;
        }
        targetsList.remove(target);
        logger.info("<LambdaTargetGroup>: TargetGroup " + this.getPath() + " removed target: " + target.getId());
    }

    private Response processRequest(LambdaExecutionContext lambdaExecutionContext, Request request) {
        try {
            var response = invoke(lambdaExecutionContext, request);
            logger.info("<LambdaTargetGroup>: TargetGroup " + this.getPath() + " processed request: " + request.getPath());
            return (Response) response;
        } catch (Exception e) {
            logger.error("<LambdaTargetGroup>: Exception while processing request " + request.getPath() + " "
                    + e.getMessage());
            throw new RuntimeException(e);
        } finally {
        }
    }

    private <I, O> O invoke(LambdaExecutionContext lambdaExecutionContext, I input) {
        LambdaHandler<I, O> benchmarkHandler = BenchmarkProxy.createProxy(this.lambdaFunction, LambdaHandler.class);
        lambdaExecutionContext.initialize();
        logger.info("<LambdaTargetGroup>: lambda execution context initialized");
        O result = benchmarkHandler.handleRequest(input, lambdaExecutionContext);
        lambdaExecutionContext.shutdown();
        logger.info("<LambdaTargetGroup>: lambda execution context shutdown");
        return result;
    }

}
