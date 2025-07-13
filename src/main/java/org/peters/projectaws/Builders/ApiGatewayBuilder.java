package org.peters.projectaws.Builders;


import org.peters.projectaws.Components.ApiGateway.ApiGateway;
import org.peters.projectaws.Core.AWSBuilderObject;

public class ApiGatewayBuilder extends AWSBuilderObject {
    public ApiGateway createGateway() {
        return new ApiGateway();
    }
}
