package org.peters.projectaws.Components.API;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.peters.projectaws.Interfaces.Function.Function;
import org.peters.projectaws.dtos.Response.Response;

public class GetApi extends Api{
    private static final Logger logger = LogManager.getLogger(GetApi.class);

    public GetApi(String name,
                   String path,
                  Function fn){
        super(name, path, "GET", fn);
        logger.info("<GetApi>: GetApi created: " + this.getId());
    }


    @Override
    Response invoke(String data) {
        return fn.execute(data);
    }
}
