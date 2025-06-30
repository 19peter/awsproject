package org.peters.projectaws.Components.API;

import org.peters.projectaws.Interfaces.Function.FunctionInterface;
import org.peters.projectaws.dtos.Response.Response;
import java.util.logging.Logger;

public class PostApi extends Api{
    private static final Logger logger = Logger.getLogger(PostApi.class.getName());
    public PostApi(String name,
                   String path,
                   FunctionInterface fn){
        super(name, path, "POST", fn);
        logger.info("PostApi created: " + this.getId());
        this.fn = fn;
        this.type = "POST";
    }


    @Override
    Response invoke(String data) {
        return fn.execute(data);
    }
}
