package org.peters.projectaws.Interfaces.Integration.EC2;

public interface EC2Integration {

    void receiveFromEC2(String path, String data);
}
