package org.peters.projectaws.Components.API;

import org.peters.projectaws.Core.AWSObject;
import org.peters.projectaws.Interfaces.Function.Function;
import org.peters.projectaws.dtos.Response.Response;


public abstract class Api extends AWSObject {
    String path;
    String type;
    Function fn;

    public Api(String name, String path, String type, Function fn) {
        super(name);
        this.path = path;
        this.type = type;
        this.fn = fn;
    }

    

    abstract Response invoke(String data);

    void setFunction(Function fn) {
        this.fn = fn;
    }
    public Function getFn() {
        return fn;
    }

    public String getPath() {
        return path;
    }

    public String getType() {
        return type;
    }
}
