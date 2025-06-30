package org.peters.projectaws.Interfaces.IntegrationInterfaces.LoadBalancer;

import org.peters.projectaws.dtos.Request.Request;
import org.peters.projectaws.dtos.Response.Response;

public interface TargetIntegrationInterface {
    public Response receiveFromLoadBalancer(Request request) throws InterruptedException;
}

