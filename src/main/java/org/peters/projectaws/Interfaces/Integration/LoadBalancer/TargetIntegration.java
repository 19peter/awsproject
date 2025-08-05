package org.peters.projectaws.Interfaces.Integration.LoadBalancer;

import org.peters.projectaws.dtos.Request.Request;
import org.peters.projectaws.dtos.Response.Response;

public interface TargetIntegration {
    public Response receiveFromLoadBalancer(Request request) throws InterruptedException;
}

