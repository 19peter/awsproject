package org.peters.projectaws.Interfaces.IntegrationInterfaces.ApiGateway;

import java.util.concurrent.ExecutionException;

import org.peters.projectaws.Interfaces.AWSObjectInterface.AWSObjectInterface;
import org.peters.projectaws.dtos.Request.Request;
import org.peters.projectaws.dtos.Response.Response;

public interface ApiGatewayIntegrationInterface extends AWSObjectInterface {
    Response receiveFromGateway(Request request) throws InterruptedException, ExecutionException;
}
