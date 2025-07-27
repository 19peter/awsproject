package org.peters.projectaws.Scenarios;

import org.peters.projectaws.Interfaces.Function.FunctionInterface;
import org.peters.projectaws.Builders.ApiGatewayBuilder;
import org.peters.projectaws.Builders.EC2TargetGroupBuilder;
import org.peters.projectaws.Builders.LambdaBuilder;
import org.peters.projectaws.Builders.LoadBalancerBuilder;
import org.peters.projectaws.Builders.LambdaTargetGroupBuilder;
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

        LambdaBuilder lambdaBuilder = new LambdaBuilder(functionalInterface);
        ApiGatewayBuilder gatewayBuilder = new ApiGatewayBuilder("gateway-ONE");
        LoadBalancerBuilder loadBalancerBuilder = new LoadBalancerBuilder("loadBalancer-ONE");
        Lambda lambda = lambdaBuilder.build();
        LambdaTargetGroupBuilder targetGroupBuilder = new LambdaTargetGroupBuilder("/ec2/data", lambda);
        
        
        ApiGateway apiGateway = gatewayBuilder.build();
        LoadBalancer loadBalancer = loadBalancerBuilder.build();
        TargetGroup<LambdaExecutionContext> targetGroup = targetGroupBuilder.build();

        loadBalancer.initialize();
        loadBalancer.addTargetGroup("/lb/lambda", targetGroup);
        apiGateway.addRule("/lb/lambda", loadBalancer);

        apiGateway.routeAsync(new Request("GET", "/lb/lambda", null));
        apiGateway.routeAsync(new Request("GET", "/lb/lambda", null));
    }
}
