package org.peters.projectaws.Builders;


import org.peters.projectaws.Components.API.Api;
import org.peters.projectaws.Components.API.GetApi;
import org.peters.projectaws.Components.API.PostApi;
import org.peters.projectaws.Interfaces.Function.FunctionInterface;

import java.util.Optional;

public class ApiBuilder {

    public Api createPostApi(String name, String path, FunctionInterface fn) {
       return new PostApi(name, path, fn);
    }

    public Api createGetApi(String name, String path, FunctionInterface fn) {
        return new GetApi(name, path, fn);
    }
}
