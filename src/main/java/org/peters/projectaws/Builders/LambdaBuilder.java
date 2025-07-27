package org.peters.projectaws.Builders;

import org.peters.projectaws.Components.Lambda.Lambda;
import org.peters.projectaws.Interfaces.Function.FunctionInterface;
import org.peters.projectaws.Core.AWSBuilderObject;

public class LambdaBuilder extends AWSBuilderObject<Lambda> {
    FunctionInterface functionalInterface;
    
    public LambdaBuilder(FunctionInterface functionalInterface) {
        this.functionalInterface = functionalInterface;
    }

    @Override
    public Lambda build() {
        return new Lambda(functionalInterface);
    }
}
