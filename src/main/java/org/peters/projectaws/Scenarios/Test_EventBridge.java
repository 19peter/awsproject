package org.peters.projectaws.Scenarios;


import org.peters.projectaws.enums.EC2Types;
import org.peters.projectaws.Builders.EC2Builder;


public class Test_EventBridge {
    public static void test() {

        EC2Builder ec2Builder = new EC2Builder("test", EC2Types.T3_MICRO);
        ec2Builder.build();
        
    }
}
