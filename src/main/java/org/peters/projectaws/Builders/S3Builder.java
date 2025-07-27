package org.peters.projectaws.Builders;

import org.peters.projectaws.Components.S3.S3;
import org.peters.projectaws.Core.AWSBuilderObject;

public class S3Builder extends AWSBuilderObject<S3> {
    public static S3 s3 = new S3();

    public S3Builder() {
        
    }

    @Override
    public S3 build() {
        return s3;
    }

//    public S3 createS3() {
//        return s3;
//    }

}
