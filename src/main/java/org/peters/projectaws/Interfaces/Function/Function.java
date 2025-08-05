package org.peters.projectaws.Interfaces.Function;

import org.peters.projectaws.dtos.Response.Response;

@FunctionalInterface
public interface Function {
    Response execute(String data);
}
