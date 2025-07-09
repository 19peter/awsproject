package org.peters.projectaws.Components.Lambda;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.peters.projectaws.Annotations.Benchmark.Benchmark;
import org.peters.projectaws.Core.AWSObject;
import org.peters.projectaws.Interfaces.Function.FunctionInterface;
import org.peters.projectaws.Interfaces.Lambda.LambdaHandler;
import org.peters.projectaws.dtos.Response.Response;

public class Lambda
extends AWSObject
implements LambdaHandler<Object , Response>
{
    private static final Logger logger = LogManager.getLogger(Lambda.class);
    private FunctionInterface functionalInterface;

    public Lambda(FunctionInterface functionalInterface) {
        this.functionalInterface = functionalInterface;
        logger.info("<Lambda>: Lambda created with id: " + this.getId());
    }

    @Benchmark
    @Override
    public Response handleRequest(Object data, LambdaExecutionContext context) {
        logger.info("<Lambda>: Lambda executed with id: " + this.getId());
        Response response = functionalInterface.execute("");
        logger.info("<Lambda>: Lambda returned with id: " + this.getId());
        return response;
    }
}
