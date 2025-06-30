package org.peters.projectaws.Interfaces.Function;

import org.peters.projectaws.dtos.Response.Response;

@FunctionalInterface
public interface FunctionInterface {
    Response execute(String data);
}
