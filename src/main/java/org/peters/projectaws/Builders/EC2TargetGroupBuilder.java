package org.peters.projectaws.Builders;

import org.peters.projectaws.Components.EC2.EC2;
import org.peters.projectaws.Components.LoadBalancer.TargetGroup.EC2TargetGroup;
import org.peters.projectaws.Components.Lambda.Lambda;
import org.peters.projectaws.Components.Lambda.LambdaExecutionContext;
import org.peters.projectaws.Components.LoadBalancer.TargetGroup.LambdaTargetGroup;
import org.peters.projectaws.Components.LoadBalancer.TargetGroup.Common.TargetGroup;
import org.peters.projectaws.Core.AWSBuilderObject;

public class EC2TargetGroupBuilder extends AWSBuilderObject<EC2TargetGroup> {
    String path;
    
    public EC2TargetGroupBuilder(String path) {
        this.path = path;
    }

    @Override
    public EC2TargetGroup build() {
        return new EC2TargetGroup(path);
    }

}
