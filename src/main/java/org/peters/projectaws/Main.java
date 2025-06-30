package org.peters.projectaws;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.peters.projectaws.Builders.ApiBuilder;
import org.peters.projectaws.Builders.ApiGatewayBuilder;
import org.peters.projectaws.Builders.EC2Builder;
import org.peters.projectaws.Builders.S3Builder;
import org.peters.projectaws.Components.API.Api;
import org.peters.projectaws.Components.ApiGateway.ApiGateway;
import org.peters.projectaws.Components.EC2.EC2;
import org.peters.projectaws.Components.S3.Bucket.Bucket;
import org.peters.projectaws.Components.S3.S3;
import org.peters.projectaws.Scenarios.Test_Gateway_LB_TG_EC2;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {
        Test_Gateway_LB_TG_EC2.test();

    }

}