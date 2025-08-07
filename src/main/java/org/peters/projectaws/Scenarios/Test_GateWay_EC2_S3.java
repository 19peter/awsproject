package org.peters.projectaws.Scenarios;

import java.net.URISyntaxException;

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
import org.peters.projectaws.Main;
import org.peters.projectaws.dtos.Request.Request;
import org.peters.projectaws.enums.EC2Types;

public class Test_GateWay_EC2_S3 {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void test() throws InterruptedException, URISyntaxException {
        ApiGatewayBuilder gatewayBuilder = new ApiGatewayBuilder("gateway-ONE");
        EC2Builder ec2Builder = new EC2Builder("ec2-ONE", EC2Types.T3_MICRO);
        ApiBuilder apiBuilder = new ApiBuilder();
        S3Builder s3Builder = new S3Builder();

        ApiGateway apiGateway = gatewayBuilder.build();
        EC2 ec2 = ec2Builder.build();
        S3 s3 = S3.getInstance();


        s3.addBucket("data", "data-info");
        Bucket dataBucket = S3.getBucket("data");
        dataBucket.addData("data-key", "data-value");
        dataBucket.addData("data-key2", "data-value2");


        apiGateway.addRule("/ec2/data", ec2);
        apiGateway.addRule("/ec2/another-data", ec2);


        Api getDataFromEC2 = apiBuilder.createGetApi("getDataFromEC2", "/ec2/data", S3::getFromBucket);
        ec2.setApis(getDataFromEC2);

        Api getAnotherDataFromEC2 = apiBuilder.createGetApi("getAnotherDataFromEC2", "/ec2/another-data", S3::getFromBucket);
        ec2.setApis(getAnotherDataFromEC2);

        Api addDataToEC2 = apiBuilder.createPostApi("addDataToEC2", "/ec2/data", S3::addToBucket);
        ec2.setApis(addDataToEC2);


        apiGateway.routeAsync(new Request("POST", "/ec2/data", "/S3/data/new-data-key/new-data-value"));
        apiGateway.routeAsync(new Request("POST", "/ec2/data", "/S3/data/new-data-key/data-version-2"));
        Thread.sleep(3000);
        apiGateway.routeAsync(new Request("GET", "/ec2/data", "/S3/data/new-data-key"));

        logger.info("Shutting down components");
        apiGateway.shutdownAndAwait();
        ec2.shutdown();
        logger.info("Application shutdown complete");
    }
}
