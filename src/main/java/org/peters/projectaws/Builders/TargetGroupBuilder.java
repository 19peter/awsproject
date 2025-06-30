package org.peters.projectaws.Builders;

import org.peters.projectaws.Components.LoadBalancer.TargetGroup.EC2TargetGroup;
import org.peters.projectaws.Components.LoadBalancer.TargetGroup.TargetGroup;
import org.peters.projectaws.Components.Monitors.EC2TargetMonitor;
import org.peters.projectaws.Components.Monitors.LambdaTargetMonitor;

public class TargetGroupBuilder {
    public TargetGroup<EC2TargetMonitor> createEC2TargetGroup(String path) {
        return new EC2TargetGroup(path);
    }

    // public TargetGroup<LambdaTargetMonitor> createLambdaTargetGroup(String path) {
    //     return null;
    // }
}
