package org.peters.projectaws.Builders;


import org.peters.projectaws.Components.API.Api;
import org.peters.projectaws.Components.API.GetApi;
import org.peters.projectaws.Components.API.PostApi;
import org.peters.projectaws.Interfaces.Function.Function;


public class ApiBuilder  {

    public Api createPostApi(String name, String path, Function fn) {
       return new PostApi(name, path, fn);
    }

    public Api createGetApi(String name, String path, Function fn) {
        return new GetApi(name, path, fn);
    }
}
