package org.peters.projectaws.Builders;

import org.peters.projectaws.Components.Lambda.Lambda;
import org.peters.projectaws.Interfaces.Function.FunctionInterface;

public class LambdaBuilder {
    public Lambda build(FunctionInterface functionalInterface) {
        return new Lambda(functionalInterface);
    }
}
