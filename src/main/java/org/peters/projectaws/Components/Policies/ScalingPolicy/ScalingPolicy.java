package org.peters.projectaws.Components.Policies.ScalingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.peters.projectaws.Core.AWSPolicyObject;
import org.peters.projectaws.enums.ScalingPolicyActions;
import org.peters.projectaws.enums.ScalingPolicyRules;

public class ScalingPolicy extends AWSPolicyObject {
    private List<ScalingPolicyRuleAction> ruleActions ;
    
    public ScalingPolicy(String policyName) {
        super(policyName);
        this.ruleActions = new ArrayList<>();
    }

    public void addRuleAction(ScalingPolicyRuleAction ruleAction) {
        if (this.ruleActions.contains(ruleAction)) throw new IllegalArgumentException("<ScalingPolicy>: Rule " + ruleAction.getRule() + " already added");
        validateMinMax(ruleAction);
        this.ruleActions.add(ruleAction);
    }

    public void removeRuleAction(ScalingPolicyRuleAction ruleAction) {
        this.ruleActions.remove(ruleAction);
    }

    public void removeRuleAction(String name) {
        this.ruleActions.removeIf(rule -> rule.getName().equals(name));
    }

    public ScalingPolicyRuleAction getRule(ScalingPolicyRules rule) {
        return this.ruleActions.stream()
                .filter(ruleAction -> ruleAction.getRule() == rule)
                .findFirst()
                .orElse(null);
    }

    private void validateMinMax(ScalingPolicyRuleAction ruleAction) {
        if (ruleAction.getRule() == ScalingPolicyRules.MIN_INSTANCE) {
                ruleActions.stream()
                .filter(rule -> rule.getRule() == ScalingPolicyRules.MAX_INSTANCE)
                .findFirst()
                .ifPresent(rule -> {
                    if (ruleAction.getInstances() > rule.getInstances()) {
                        throw new IllegalArgumentException("<ScalingPolicy>: Min Instance " + ruleAction.getInstances() + " is greater than Max Instance " + rule.getInstances());
                    }
                });
        } else if (ruleAction.getRule() == ScalingPolicyRules.MAX_INSTANCE) {
            ruleActions.stream()
            .filter(rule -> rule.getRule() == ScalingPolicyRules.MIN_INSTANCE)
            .findFirst()
            .ifPresent(rule -> {
                if (ruleAction.getInstances() < rule.getInstances()) {
                    throw new IllegalArgumentException("<ScalingPolicy>: Max Instance " + ruleAction.getInstances() + " is less than Min Instance " + rule.getInstances());
                }
            });
        }
    }

    public String getRuleActionsNames() {
        return ruleActions.stream()
                .map(rule -> rule.getName())
                .collect(Collectors.joining(", "));
    }
    
}
