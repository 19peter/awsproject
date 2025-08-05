package org.peters.projectaws.Components.S3;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.peters.projectaws.Components.S3.Bucket.Bucket;
import org.peters.projectaws.Main;
import org.peters.projectaws.dtos.Request.Request;
import org.peters.projectaws.dtos.Response.Response;
import org.peters.projectaws.Core.AWSObject;
import org.peters.projectaws.Interfaces.Integration.ApiGateway.ApiGatewayIntegration;

import java.util.*;

public class S3 extends AWSObject 
implements ApiGatewayIntegration {
    private static final Logger logger = LogManager.getLogger(Main.class);
    static Map<String, Bucket> buckets;

    private S3() {
        S3.buckets = new HashMap<>();
        logger.info("<S3>: S3 created with id: " + this.getId());
    }

    private static class SingletonHolder{
        private static final S3 INSTANCE = new S3();
    }

    public static S3 getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public boolean addBucket(String name, String info) {
        if (S3.buckets.containsKey(name)) return false;
        Bucket bucket = new Bucket(name, info);
        S3.buckets.put(name, bucket);
        return true;
    }

    public static Bucket getBucket(String bucketName) {
        return S3.buckets.get(bucketName);
    }


    public static Response getFromBucket(String requestPath) {
        HashMap<String, String> details = extractBucketAndKey(requestPath);
        String bucketName = details.get("BUCKETNAME");
        String key = details.get("KEY");
        if (bucketName == null || bucketName.isEmpty() || key == null || key.isEmpty()){
            logger.error("<S3>: Error: Bucket name or key is missing.");
            return null;
        }
        Bucket bucket = S3.getBucket(bucketName);
        return bucket.getValue(key);
    }

    public static Response addToBucket(String requestPath) {
        HashMap<String, String> details = extractBucketAndKey(requestPath);

        String bucketName = details.get("BUCKETNAME");
        String key = details.get("KEY");
        String value = details.get("VALUE");


        if (bucketName == null || bucketName.isEmpty() || key == null || key.isEmpty() || value == null || value.isEmpty()){
            logger.error("<S3>: Error: Bucket name or key or value is missing.");

            return new Response("400");
        }

        Bucket bucket = S3.getBucket(bucketName);


        if (bucket.addData(key, value)) {
            logger.info("<S3>: Adding To Bucket: " + bucketName + " Key: " + key + " And Value: " + value);
            return new Response("200");

        } else  {
            logger.error("<S3>: Failed To Add To Bucket: " + bucketName + " Key: " + key + " And Value: " + value);
            return new Response("400");

        }

    }

    @Override
    public Response receiveFromGateway(Request request) {
        String method = request.getMethod();
        String path = request.getPath();
        String data = request.getData();
        String bucketName = path.split("/")[2];
        if (S3.buckets.containsKey(bucketName)) {
            Bucket bucket = S3.getBucket(bucketName);
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
