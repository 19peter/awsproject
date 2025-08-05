package org.peters.projectaws.Builders;

import org.peters.projectaws.Components.S3.S3;
import org.peters.projectaws.Core.AWSBuilderObject;

public class S3Builder extends AWSBuilderObject<S3> {

    public S3Builder() {
        
    }

    @Override
    public S3 buildProcess() {
        S3 s3 = S3.getInstance();
        return s3;
    }

}
