package org.peters.projectaws.Builders;

import org.peters.projectaws.Components.EC2.EC2;
import org.peters.projectaws.Components.LoadBalancer.TargetGroup.EC2TargetGroup;
import org.peters.projectaws.Components.LoadBalancer.TargetGroup.TargetGroup;

public class TargetGroupBuilder {
    public TargetGroup<EC2> createEC2TargetGroup(String path) {
        return new EC2TargetGroup(path);
    }

    // public TargetGroup<LambdaTargetMonitor> createLambdaTargetGroup(String path) {
    //     return null;
    // }
}
