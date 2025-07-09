package org.peters.projectaws.Interfaces.Lambda;

import org.peters.projectaws.Components.Lambda.LambdaExecutionContext;

public interface LambdaHandler<I, O> {
    O handleRequest(I input, LambdaExecutionContext context);
}
