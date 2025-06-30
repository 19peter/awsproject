package org.peters.projectaws.Components.API;

import org.peters.projectaws.Interfaces.Function.FunctionInterface;
import org.peters.projectaws.dtos.Response.Response;
import java.util.logging.Logger;

public class GetApi extends Api{
    private static final Logger logger = Logger.getLogger(GetApi.class.getName());
    public GetApi(String name,
                   String path,
                  FunctionInterface fn){
        super(name, path, "GET", fn);
        logger.info("GetApi created: " + this.getId());
    }


    @Override
    Response invoke(String data) {
        return fn.execute(data);
    }
}
