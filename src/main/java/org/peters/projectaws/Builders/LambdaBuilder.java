package org.peters.projectaws.Builders;

import org.peters.projectaws.Components.Lambda.Lambda;
import org.peters.projectaws.Interfaces.Function.FunctionInterface;
import org.peters.projectaws.Core.AWSBuilderObject;

public class LambdaBuilder extends AWSBuilderObject {
    public Lambda build(FunctionInterface functionalInterface) {
        return new Lambda(functionalInterface);
    }
}
