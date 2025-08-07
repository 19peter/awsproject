package org.peters.projectaws.Scenarios;

import java.net.URISyntaxException;

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
import org.peters.projectaws.Components.LoadBalancer.AutoScalingGroup.EC2AutoScalingGroup;
import org.peters.projectaws.Components.LoadBalancer.TargetGroup.EC2TargetGroup;
import org.peters.projectaws.Components.Policies.ScalingPolicy.ScalingPolicy;
import org.peters.projectaws.Components.Policies.ScalingPolicy.ScalingPolicyRuleAction;
import org.peters.projectaws.Components.S3.S3;
import org.peters.projectaws.Components.S3.Bucket.Bucket;
import org.peters.projectaws.Helpers.AppMethods;
import org.peters.projectaws.dtos.Request.Request;
import org.peters.projectaws.enums.EC2Types;
import org.peters.projectaws.enums.ScalingPolicyActions;
import org.peters.projectaws.enums.ScalingPolicyRules;

public class Test_AutoScalingGroups {
    public static void test() throws URISyntaxException {
        ApiGatewayBuilder gatewayBuilder = new ApiGatewayBuilder("gateway-ONE");
        LoadBalancerBuilder loadBalancerBuilder = new LoadBalancerBuilder("loadBalancer-ONE");
        EC2TargetGroupBuilder targetGroupBuilder = new EC2TargetGroupBuilder("/ec2/data");
        EC2Builder ec2Builder = new EC2Builder("ec2-ONE", EC2Types.T3_MICRO);
        ApiBuilder apiBuilder = new ApiBuilder();

        ApiGateway apiGateway = gatewayBuilder.build();
        LoadBalancer loadBalancer = loadBalancerBuilder.build();
        EC2 ec2 = ec2Builder.build();
        S3 s3 = S3.getInstance();

        s3.addBucket("data", "data-info");
        Bucket dataBucket = S3.getBucket("data");
        dataBucket.addData("data-key", "data-value");
        dataBucket.addData("data-key2", "data-value2");

        apiGateway.addRule("/ec2/data", ec2);
        apiGateway.addRule("/lb/ec2/data", loadBalancer);
        apiGateway.addRule("/lb/ec2/another-data", loadBalancer);

        Api getDataFromEC2 = apiBuilder.createGetApi("getDataFromEC2", "/lb/ec2/data", S3::getFromBucket);

        ec2.setApis(getDataFromEC2);

        ec2.initialize();

        ScalingPolicy scalingPolicy = new ScalingPolicy("ScalingPolicy");
        ScalingPolicyRuleAction maxInst = new ScalingPolicyRuleAction("MaxInstance");
        ScalingPolicyRuleAction minInst = new ScalingPolicyRuleAction("MinInstance");
        ScalingPolicyRuleAction overloadedPolicy = new ScalingPolicyRuleAction("OverloadedPolicy");
        maxInst.setMinOrMax(ScalingPolicyRules.MAX_INSTANCE, 4);
        minInst.setMinOrMax(ScalingPolicyRules.MIN_INSTANCE, 1);
        overloadedPolicy.setConditionalRule(ScalingPolicyRules.OVERLOADED, 1, ScalingPolicyActions.SCALE_UP, 1);
        scalingPolicy.addRuleAction(maxInst);
        scalingPolicy.addRuleAction(minInst);
        scalingPolicy.addRuleAction(overloadedPolicy);

        EC2AutoScalingGroup autoScalingGroup = new EC2AutoScalingGroup(scalingPolicy, "/ec2/data", ec2);

        loadBalancer.initialize();
        loadBalancer.addTargetGroup("/lb/ec2/data", autoScalingGroup);

        apiGateway.routeAsync(new Request("GET", "/lb/ec2/data", "/S3/data/data-key", null));
        apiGateway.routeAsync(new Request("GET", "/lb/ec2/data", "/S3/data/new-data-key", null));
        apiGateway.routeAsync(new Request("GET", "/lb/ec2/data", "/S3/data/new-data-key", null));
        apiGateway.routeAsync(new Request("GET", "/lb/ec2/data", "/S3/data/new-data-key", null));
        apiGateway.routeAsync(new Request("GET", "/lb/ec2/data", "/S3/data/new-data-key", null));

    }
}
