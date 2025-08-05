package org.peters.projectaws.Components.App;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.peters.projectaws.Components.API.Api;
import org.peters.projectaws.Components.EC2.EC2;
import org.peters.projectaws.dtos.Request.Request;
import org.peters.projectaws.dtos.Response.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class App {
    private static final Logger logger = LogManager.getLogger(App.class);
    private String name;
    private int port;
    private List<Api> apis;


    public App(String name, int port) {
        this.name = name;
        this.port = port;
        this.apis = new ArrayList<>();
    }

    public void setApis(Api api) {
        apis.add(api);
    }

    public void setApis(List<Api> apis) {
        this.apis = apis;
    }

    public int getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    public Response executeApi(Request request)  {
        String method = request.getMethod();
        String path = request.getPath();
        String data = request.getData();
        int port = request.getPort();

        if(port != this.port) {
            logger.info("<App>: Port does not match: Method: " + method + " Path: " + path);
            return new Response("403");
        }   
        
        Optional<Api> apiCheck = apis.stream()
                .filter(api -> api.getPath().equals(request.getPath()) && api.getType().equals(request.getMethod()))
                .findFirst();

        if (apiCheck.isEmpty()) {
            logger.info("<App>: API does not exist: Method: " + method + " Path: " + path);
            return null;
        }

        Response apiResponse = apiCheck.get().getFn().execute(data);    
        return apiResponse;
    }
}
