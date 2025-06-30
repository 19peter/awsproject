package org.peters.projectaws.Components.S3;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.peters.projectaws.Components.S3.Bucket.Bucket;
import org.peters.projectaws.Interfaces.IntegrationInterfaces.ApiGateway.ApiGatewayIntegrationInterface;
import org.peters.projectaws.Main;
import org.peters.projectaws.dtos.Request.Request;
import org.peters.projectaws.dtos.Response.S3.GetDataResponseDto;
import org.peters.projectaws.dtos.Response.S3.PostResponseDto;
import org.peters.projectaws.dtos.Response.Response;
import org.peters.projectaws.Core.AWSObject;

import java.util.*;

public class S3 extends AWSObject 
implements ApiGatewayIntegrationInterface {
    private static final Logger logger = LogManager.getLogger(Main.class);
    static Map<String, Bucket> buckets;
    ApiGatewayIntegrationInterface integrationInterface;


    public S3() {
        this.buckets = new HashMap<>();
        logger.info("S3 created with id: " + this.getId());
    }

    public boolean addBucket(String name, String info) {
        if (buckets.containsKey(name)) return false;
        Bucket bucket = new Bucket(name, info);
        buckets.put(name, bucket);
        return true;
    }

    public static Bucket getBucket(String bucketName) {
        return buckets.get(bucketName);
    }


    public static GetDataResponseDto getFromBucket(String requestPath) {
        HashMap<String, String> details = extractBucketAndKey(requestPath);
        String bucketName = details.get("BUCKETNAME");
        String key = details.get("KEY");
        if (bucketName == null || bucketName.isEmpty() || key == null || key.isEmpty()){
            logger.error("Error: Bucket name or key is missing.");
            return null;
        }
        Bucket bucket = getBucket(bucketName);
        return bucket.getValue(key);
    }

    public static Response addToBucket(String requestPath) {
        HashMap<String, String> details = extractBucketAndKey(requestPath);

        String bucketName = details.get("BUCKETNAME");
        String key = details.get("KEY");
        String value = details.get("VALUE");


        if (bucketName == null || bucketName.isEmpty() || key == null || key.isEmpty() || value == null || value.isEmpty()){
            logger.error("Error: Bucket name or key or value is missing.");

            return new PostResponseDto("400");
        }

        Bucket bucket = getBucket(bucketName);


        if (bucket.addData(key, value)) {
            logger.info("Adding To Bucket: " + bucketName + " Key: " + key + " And Value: " + value);
            return new PostResponseDto("200");

        } else  {
            logger.error("Failed To Add To Bucket: " + bucketName + " Key: " + key + " And Value: " + value);
            return new PostResponseDto("400");

        }

    }

    @Override
    public Response receiveFromGateway(Request request) {
        String method = request.getMethod();
        String path = request.getPath();
        String data = request.getData();
        String bucketName = path.split("/")[2];
        if (buckets.containsKey(bucketName)) {
            Bucket bucket = getBucket(bucketName);
            logger.info("S3 Received from gateway: Path: " + path + " method: " + method + " data: " + data);
        }
        else logger.error("S3: Data not found");

        return null;
    }


    private static HashMap<String, String> extractBucketAndKey(String path) {
        if (path == null || !path.startsWith("/S3/")) {
            logger.error("Invalid path format.");
            return null;
        }

        String[] parts = path.split("/");

        if (parts.length < 4) {
            logger.error("Path is too short to contain bucket and key.");
            return null;
        }

        String bucketName = parts[2].trim();
        String key = parts[3].trim();
        String value = (parts.length >= 5) ? parts[4].trim() : null;

        HashMap<String, String> response = new HashMap<>();
        response.put("BUCKETNAME", bucketName);
        response.put("KEY", key);
        response.put("VALUE", value);

        return response;

    }

}
