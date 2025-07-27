package org.peters.projectaws.Builders;


import java.util.List;

import org.peters.projectaws.Components.API.Api;
import org.peters.projectaws.Components.EC2.EC2;
import org.peters.projectaws.Core.AWSBuilderObject;

public class EC2Builder extends AWSBuilderObject<EC2> {
    String name;
    int maxConn;
    
    public EC2Builder(String name, int maxConn) {
        this.name = name;
        this.maxConn = maxConn;
    }

    @Override
    public EC2 build() {
        return new EC2(maxConn, name);
    }

}
