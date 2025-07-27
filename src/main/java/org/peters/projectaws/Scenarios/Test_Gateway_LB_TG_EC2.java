package org.peters.projectaws.Scenarios;

import org.peters.projectaws.Builders.ApiBuilder;
import org.peters.projectaws.Builders.ApiGatewayBuilder;
import org.peters.projectaws.Builders.EC2Builder;
import org.peters.projectaws.Builders.EC2TargetGroupBuilder;
import org.peters.projectaws.Builders.LoadBalancerBuilder;
import org.peters.projectaws.Builders.S3Builder;
import org.peters.projectaws.Components.API.Api;
import org.peters.projectaws.Components.ApiGateway.ApiGateway;
import org.peters.projectaws.Components.EC2.EC2;
import org.peters.projectaws.Components.LoadBalancer.LoadBalancer;
import org.peters.projectaws.Components.LoadBalancer.TargetGroup.Common.TargetGroup;
import org.peters.projectaws.Components.S3.S3;
import org.peters.projectaws.Components.S3.Bucket.Bucket;
import org.peters.projectaws.Helpers.Helpers;
import org.peters.projectaws.dtos.Request.Request;

public class Test_Gateway_LB_TG_EC2 {

    public static void test() throws InterruptedException {
        ApiGatewayBuilder gatewayBuilder = new ApiGatewayBuilder("gateway-ONE");
        LoadBalancerBuilder loadBalancerBuilder = new LoadBalancerBuilder("loadBalancer-ONE");
        EC2TargetGroupBuilder targetGroupBuilder = new EC2TargetGroupBuilder("/ec2/data");
        EC2Builder ec2Builder = new EC2Builder("ec2-ONE", 1);
        ApiBuilder apiBuilder = new ApiBuilder();
        
        ApiGateway apiGateway = gatewayBuilder.build();
        LoadBalancer loadBalancer = loadBalancerBuilder.build();
        EC2 ec2 = ec2Builder.build();
        EC2 ec2_2 = ec2Builder.build();
        S3 s3 = S3Builder.s3;

        s3.addBucket("data", "data-info");
        Bucket dataBucket = S3.getBucket("data");
        dataBucket.addData("data-key", "data-value");
        dataBucket.addData("data-key2", "data-value2");


        apiGateway.addRule("/ec2/data", ec2);
        apiGateway.addRule("/lb/ec2/data", loadBalancer);
        apiGateway.addRule("/lb/ec2/another-data", loadBalancer);

        Api getDataFromEC2 = apiBuilder.createGetApi("getDataFromEC2", "/lb/ec2/data", S3::getFromBucket);
        ec2.setApis(getDataFromEC2);
        ec2_2.setApis(getDataFromEC2);

        ec2.initialize();
        ec2_2.initialize();

        TargetGroup<EC2> targetGroup = targetGroupBuilder.build();
        targetGroup.addTarget(ec2);
        targetGroup.addTarget(ec2_2);

        loadBalancer.initialize();
        loadBalancer.addTargetGroup("/lb/ec2/data", targetGroup);


        // apiGateway.routeAsync(new Request("GET", "/lb/ec2/data", "/S3/data/data-key", null));
        // apiGateway.routeAsync(new Request("GET", "/lb/ec2/data", "/S3/data/new-data-key", null));
    
        // Thread.sleep(Helpers.delayDuration + 1000);

        ec2.shutdown();

        apiGateway.routeAsync(new Request("GET", "/lb/ec2/data", "/S3/data/data-key", null));
        apiGateway.routeAsync(new Request("GET", "/lb/ec2/data", "/S3/data/new-data-key", null));
        apiGateway.routeAsync(new Request("GET", "/lb/ec2/data", "/S3/data/new-data-key", null));
        apiGateway.routeAsync(new Request("GET", "/lb/ec2/data", "/S3/data/new-data-key", null));
        apiGateway.routeAsync(new Request("GET", "/lb/ec2/data", "/S3/data/new-data-key", null));

        // loadBalancer.shutdown();
        
    }
}
