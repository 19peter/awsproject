package org.peters.projectaws.Builders;

import org.peters.projectaws.Components.LoadBalancer.LoadBalancer;
import org.peters.projectaws.Core.AWSBuilderObject;

public class LoadBalancerBuilder extends AWSBuilderObject<LoadBalancer> {
    String name;
    
    public LoadBalancerBuilder(String name) {
        this.name = name;
    }
    
    @Override
    public LoadBalancer build() {
        return new LoadBalancer(name);
    }
}
