package org.peters.projectaws.Scenarios;

import org.peters.projectaws.Interfaces.Function.FunctionInterface;
import org.peters.projectaws.Builders.ApiGatewayBuilder;
import org.peters.projectaws.Builders.LambdaBuilder;
import org.peters.projectaws.Builders.LoadBalancerBuilder;
import org.peters.projectaws.Builders.TargetGroupBuilder;
import org.peters.projectaws.Components.ApiGateway.ApiGateway;
import org.peters.projectaws.Components.Lambda.Lambda;
import org.peters.projectaws.Components.Lambda.LambdaExecutionContext;
import org.peters.projectaws.Components.LoadBalancer.LoadBalancer;
import org.peters.projectaws.Components.LoadBalancer.TargetGroup.Common.TargetGroup;
import org.peters.projectaws.dtos.Request.Request;

public class Test_LB_Lambda_Benchmark {

    public static void test() {
        FunctionInterface functionalInterface = (data) -> {
            try {
            
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Lambda invoked");
            return null;
        };

        LambdaBuilder lambdaBuilder = new LambdaBuilder();
        ApiGatewayBuilder gatewayBuilder = new ApiGatewayBuilder();
        LoadBalancerBuilder loadBalancerBuilder = new LoadBalancerBuilder();
        TargetGroupBuilder targetGroupBuilder = new TargetGroupBuilder();
        
        
        Lambda lambda = lambdaBuilder.build(functionalInterface);
        ApiGateway apiGateway = gatewayBuilder.createGateway();
        LoadBalancer loadBalancer = loadBalancerBuilder.createLoadBalancer();
        TargetGroup<LambdaExecutionContext> targetGroup = targetGroupBuilder.createLambdaTargetGroup("/lb/lambda", lambda);

        loadBalancer.initialize();
        loadBalancer.addTargetGroup("/lb/lambda", targetGroup);
        apiGateway.addRule("/lb/lambda", loadBalancer);

        apiGateway.routeAsync(new Request("GET", "/lb/lambda", null));
        apiGateway.routeAsync(new Request("GET", "/lb/lambda", null));
    }
}
