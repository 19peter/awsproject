package org.peters.projectaws.Interfaces.Integration.ApiGateway;

import java.util.concurrent.ExecutionException;

import org.peters.projectaws.dtos.Request.Request;
import org.peters.projectaws.dtos.Response.Response;

public interface ApiGatewayIntegration {
    Response receiveFromGateway(Request request) throws InterruptedException, ExecutionException;
}
