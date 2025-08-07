package org.peters.projectaws.Components.EC2;

import org.peters.projectaws.enums.EC2Types;

public class EC2SizeAllocator {
    public static int getEC2Size(EC2Types type) {
        switch (type) {
            case T3_MICRO:
                return 1;
            case T3_SMALL:
                return 2;
            case T3_MEDIUM:
                return 4;
            case T3_LARGE:
                return 8;
            case T3_XLARGE:
                return 16;
            case T3_2XLARGE:
                return 32;
            default:
                return 1;
        }
    }
}
