package org.peters.projectaws.Builders;

import org.peters.projectaws.Components.LoadBalancer.LoadBalancer;
import org.peters.projectaws.Core.AWSBuilderObject;

public class LoadBalancerBuilder extends AWSBuilderObject {
    public LoadBalancer createLoadBalancer() {
        return new LoadBalancer();
    }
}
