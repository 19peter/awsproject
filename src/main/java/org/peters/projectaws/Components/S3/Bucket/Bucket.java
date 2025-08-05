package org.peters.projectaws.Components.S3.Bucket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.peters.projectaws.Core.AWSObject;
import org.peters.projectaws.Helpers.Helpers;
import org.peters.projectaws.dtos.Response.Response;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Bucket extends AWSObject {
    private static final Logger logger = LogManager.getLogger(Bucket.class);
    private String info;
    private ConcurrentHashMap<String, String> data;

    public Bucket(String name, String info) {
        super(name);
        this.info = info;
        this.data = new ConcurrentHashMap<>();
        logger.info("Bucket created with name: " + name + " and Id: " + this.getId());
    }

    public String getName() {
        return this.getName();
    }

    public Map<String, String> getData() {
        return this.data;
    }

    public Response getValue(String key) {
        var dataCheck = this.data.get(key);
        var response = new Response("400");

        if (dataCheck != null && !dataCheck.isEmpty()) {
            response = Helpers.populateResponse("200", dataCheck, true);
        } else {
            response = Helpers.populateResponse("400", null, false);
        }

        return response;
    }

    public boolean addData(String key, String value) {
        if (data.containsKey(key)) return false;
        else data.putIfAbsent(key, value);
        return true;
    }
}
