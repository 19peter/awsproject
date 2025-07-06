package org.peters.projectaws.Components.API;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.peters.projectaws.Interfaces.Function.FunctionInterface;
import org.peters.projectaws.dtos.Response.Response;

public class PostApi extends Api {
    private static final Logger logger = LogManager.getLogger(PostApi.class);

    public PostApi(String name,
            String path,
            FunctionInterface fn) {
        super(name, path, "POST", fn);
        logger.info("<PostApi>: PostApi created: " + this.getId());
        this.fn = fn;
        this.type = "POST";
    }

    @Override
    Response invoke(String data) {
        return fn.execute(data);
    }
}
