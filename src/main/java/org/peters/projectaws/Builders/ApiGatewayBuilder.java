package org.peters.projectaws.Builders;


import org.peters.projectaws.Components.ApiGateway.ApiGateway;
import org.peters.projectaws.Core.AWSBuilderObject;

public class ApiGatewayBuilder extends AWSBuilderObject<ApiGateway> {
    String name;

    public ApiGatewayBuilder(String name) {
        this.name = name;
    }

    @Override
    public ApiGateway build() {
        return new ApiGateway(name);

    }
}
