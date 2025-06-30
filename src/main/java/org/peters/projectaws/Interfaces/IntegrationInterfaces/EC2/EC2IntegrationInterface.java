package org.peters.projectaws.Interfaces.IntegrationInterfaces.EC2;

public interface EC2IntegrationInterface {

    void receiveFromEC2(String path, String data);
}
