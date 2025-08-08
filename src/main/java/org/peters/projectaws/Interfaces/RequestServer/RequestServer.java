package org.peters.projectaws.Interfaces.RequestServer;

import java.util.concurrent.ExecutionException;

import org.peters.projectaws.dtos.Request.Request;
import org.peters.projectaws.dtos.Response.Response;

public interface RequestServer {
    Response serve(Request request);
}
