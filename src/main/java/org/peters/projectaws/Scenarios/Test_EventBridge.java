package org.peters.projectaws.Scenarios;

import org.peters.projectaws.enums.EC2Types;

import java.net.URISyntaxException;

import org.peters.projectaws.Builders.ApiBuilder;
import org.peters.projectaws.Builders.ApiGatewayBuilder;
import org.peters.projectaws.Builders.EC2Builder;
import org.peters.projectaws.Builders.LoadBalancerBuilder;
import org.peters.projectaws.Components.API.Api;
import org.peters.projectaws.Components.App.App;
import org.peters.projectaws.Components.DnsRegisterar.DnsRegisterar;
import org.peters.projectaws.Components.EC2.EC2;
import org.peters.projectaws.Components.LoadBalancer.LoadBalancer;
import org.peters.projectaws.Components.LoadBalancer.TargetGroup.EC2TargetGroup;
import org.peters.projectaws.Components.LoadBalancer.TargetGroup.Common.TargetGroup;
import org.peters.projectaws.Components.S3.S3;
import org.peters.projectaws.Components.SecurityGroups.SecurityGroup;
import org.peters.projectaws.Components.SecurityGroups.SecurityRules.InboundRule;
import org.peters.projectaws.Components.SecurityGroups.SecurityRules.OutboundRule;
import org.peters.projectaws.Helpers.AppMethods;
import org.peters.projectaws.dtos.Request.Request;
import org.peters.projectaws.dtos.Response.Response;

public class Test_EventBridge {
    public static void test() throws URISyntaxException {
        InboundRule inboundRule = new InboundRule(80, "TCP");
        OutboundRule outboundRule = new OutboundRule(80, "TCP");
        SecurityGroup sg = new SecurityGroup();
        sg.addInboundRule(inboundRule);
        sg.addOutboundRule(outboundRule);

        App app_ONE = new App("app_ONE", 80);
        App app_TWO = new App("app_TWO", 80);
        ApiBuilder apiBuilder = new ApiBuilder();
        Api getDataFromEC2 = apiBuilder.createGetApi("getDataFromEC2", "/ec2/data:80", AppMethods::runMethodOne);
        Api runMethodTwo = apiBuilder.createGetApi("runMethodTwo", "/run-method-two:80", AppMethods::runMethodTwo);
        
        app_ONE.setApis(getDataFromEC2);
        app_TWO.setApis(runMethodTwo);

        EC2Builder ec2Builder = new EC2Builder("test-one", EC2Types.T3_MICRO);
        EC2 ec2_ONE = ec2Builder.build();
        ec2_ONE.attachSecurityGroup(sg);
        ec2_ONE.attachApp(app_ONE);
        

        EC2Builder ec2Builder2 = new EC2Builder("test-two", EC2Types.T3_MICRO);
        EC2 ec2_TWO = ec2Builder2.build();
        ec2_TWO.attachSecurityGroup(sg);
        ec2_TWO.attachApp(app_TWO);

        LoadBalancerBuilder loadBalancerBuilder = new LoadBalancerBuilder("test-lb");
        LoadBalancer lb = loadBalancerBuilder.build();

        TargetGroup tg1 = new EC2TargetGroup("/ec2/data:80");
        lb.addTargetGroup("/ec2/data:80", tg1);

        ApiGatewayBuilder apiGatewayBuilder = new ApiGatewayBuilder("test-gateway");
        apiGatewayBuilder.build();

        lb.initialize();
        ec2_ONE.initialize();
        ec2_TWO.initialize();
        tg1.addTarget(ec2_ONE);

        String lbDns = DnsRegisterar.getObjDnsName(lb);
        String ec2Dns = DnsRegisterar.getObjDnsName(ec2_TWO);
        
        Response lbRes = DnsRegisterar.serve(lbDns, new Request("GET", "/ec2/data:80", "s3://data/data-key", null));
        Response ec2Res = DnsRegisterar.serve(ec2Dns, new Request("GET", "/run-method-two:80", "s3://data/data-key", null));
        System.out.println("<Test_EventBridge>: LoadBalancer response: " + lbRes.getCode());
        System.out.println("<Test_EventBridge>: EC2 response: " + ec2Res.getCode());
    }
}
