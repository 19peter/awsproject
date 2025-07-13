package org.peters.projectaws.Core;

import java.util.Random;

public abstract class AWSPolicyObject {
    private long id;
    private String policyName;

    public AWSPolicyObject() {
        this.id = new Random().nextLong();
    }

    public AWSPolicyObject(String policyName) {
        this.policyName = policyName;
        this.id = new Random().nextLong();
    }
    
    public long getId() {
        return id;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }
}
