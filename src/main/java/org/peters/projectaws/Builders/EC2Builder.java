package org.peters.projectaws.Builders;

import org.peters.projectaws.Components.EC2.EC2;
import org.peters.projectaws.Components.EC2.EC2SizeAllocator;
import org.peters.projectaws.Core.AWSBuilderObject;
import org.peters.projectaws.enums.EC2Types;

public class EC2Builder extends AWSBuilderObject<EC2> {
    String name;
    EC2Types type;
    
    public EC2Builder(String name, EC2Types type) {
        this.name = name;
        this.type = type;
    }

    @Override
    protected EC2 buildProcess() {
        return new EC2(EC2SizeAllocator.getEC2Size(type), name, type);
    }

}
