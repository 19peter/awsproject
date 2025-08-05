package org.peters.projectaws.Interfaces.Integration.LoadBalancer;

import org.peters.projectaws.Components.Policies.ScalingPolicy.ScalingPolicyRuleAction;
import org.peters.projectaws.enums.TargetState;

public interface AutoScalingGroupIntegration {
    void applyPolicyIfApplicable(TargetState newState);
    void applyPolicy(ScalingPolicyRuleAction ruleAction);
    void requestScaleUp(int instances);
    void requestScaleDown(int instances);
    ScalingPolicyRuleAction getRuleByState(TargetState state);
    void applyPolicyMinInstance();
    void applyPolicyMaxInstance();
    void applyConditionalPolicy(ScalingPolicyRuleAction ruleAction);
}
