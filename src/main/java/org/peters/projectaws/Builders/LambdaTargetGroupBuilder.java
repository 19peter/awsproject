package org.peters.projectaws.Builders;

import org.peters.projectaws.Components.Lambda.Lambda;
import org.peters.projectaws.Components.LoadBalancer.TargetGroup.LambdaTargetGroup;
import org.peters.projectaws.Core.AWSBuilderObject;

public class LambdaTargetGroupBuilder extends AWSBuilderObject<LambdaTargetGroup> {
    String path;
    Lambda lambda;
    
    public LambdaTargetGroupBuilder(String path, Lambda lambda) {
        this.path = path;
        this.lambda = lambda;
    }

    public LambdaTargetGroup build() {
        return new LambdaTargetGroup(path, lambda);
    }
}
