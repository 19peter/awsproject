package org.peters.projectaws.Builders;


import java.util.List;

import org.peters.projectaws.Components.API.Api;
import org.peters.projectaws.Components.EC2.EC2;
import org.peters.projectaws.Core.AWSBuilderObject;

public class EC2Builder extends AWSBuilderObject {

    public EC2 createEc2(int maxConn, String name) {
        return new EC2(maxConn, name);
    }

    public EC2 createEc2(List<Api> apis, int maxConn, String name) {
        return new EC2(apis, maxConn, name);
    }

}
