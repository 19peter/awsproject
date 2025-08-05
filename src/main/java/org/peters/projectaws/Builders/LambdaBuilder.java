package org.peters.projectaws.Builders;

import org.peters.projectaws.Components.Lambda.Lambda;
import org.peters.projectaws.Interfaces.Function.Function;
import org.peters.projectaws.Core.AWSBuilderObject;

public class LambdaBuilder extends AWSBuilderObject<Lambda> {
    Function functionalInterface;
    
    public LambdaBuilder(Function functionalInterface) {
        this.functionalInterface = functionalInterface;
    }

    @Override
    public Lambda buildProcess() {
        return new Lambda(functionalInterface);
    }
}
