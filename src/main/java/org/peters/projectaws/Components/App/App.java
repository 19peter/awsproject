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
    List<Api> apis;
    private static final Logger logger = LogManager.getLogger(App.class);


    public App() {
        this.apis = new ArrayList<>();
    }

    public App(List<Api> apis) {
        this.apis = apis;
    }

    public void setApis(Api api) {
        apis.add(api);
    }

    public Response executeApi(Request request)  {
        String method = request.getMethod();
        String path = request.getPath();
        String data = request.getData();
        
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
