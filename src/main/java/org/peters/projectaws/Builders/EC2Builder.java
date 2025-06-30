package org.peters.projectaws.Builders;


import org.peters.projectaws.Components.EC2.EC2;

public class EC2Builder {

    public EC2 createEc2(int maxConn) {
        return new EC2(maxConn);
    }
}
