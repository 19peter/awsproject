package org.peters.projectaws.Builders;

import org.peters.projectaws.Components.API.Api;
import org.peters.projectaws.Components.API.GetApi;
import org.peters.projectaws.Components.App.App;

import java.util.Optional;

public class AppBuilder {
    public String getAllData( String data) {
        System.out.println(data);
        return data;
    }

//    public void createApp()  {
//        Api getAllData = new GetApi(
//                "getAllData",
//                "/api/all",
//                data -> Optional.ofNullable(getAllData(data)));
//
//        App app = new App();
//        app.setApis(getAllData);
//        app.executeApi("/api/all", "input data");
//    }


}