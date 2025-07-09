package org.peters.projectaws.Builders;

import org.peters.projectaws.Components.EC2.EC2;
import org.peters.projectaws.Components.LoadBalancer.TargetGroup.EC2TargetGroup;
import org.peters.projectaws.Components.LoadBalancer.TargetGroup.TargetGroup;
import org.peters.projectaws.Components.Lambda.Lambda;
import org.peters.projectaws.Components.Lambda.LambdaExecutionContext;
import org.peters.projectaws.Components.LoadBalancer.TargetGroup.LambdaTargetGroup;

public class TargetGroupBuilder {
    public TargetGroup<EC2> createEC2TargetGroup(String path) {
        return new EC2TargetGroup(path);
    }

    public TargetGroup<LambdaExecutionContext> createLambdaTargetGroup(String path, Lambda lambda) {
        return new LambdaTargetGroup(path, lambda);
    }


}
