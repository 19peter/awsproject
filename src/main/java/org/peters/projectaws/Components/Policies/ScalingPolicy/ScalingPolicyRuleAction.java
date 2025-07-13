package org.peters.projectaws.Components.Policies.ScalingPolicy;

import java.math.BigDecimal;

import org.peters.projectaws.enums.ScalingPolicyActions;
import org.peters.projectaws.enums.ScalingPolicyRules;

public class ScalingPolicyRuleAction {
    String name;
    ScalingPolicyRules rule;
    // double value;
    int ruleValue;
    ScalingPolicyActions action;
    int instances;
    boolean enabled;

    public ScalingPolicyRuleAction(String name) {
        this.name = name;
    }

    

    public void setMinOrMax(ScalingPolicyRules rule, int instances) {
        if (this.enabled) throw new IllegalArgumentException("<ScalingPolicyRuleAction>: Rule Action " + this.name + " already defined");

        if(rule == ScalingPolicyRules.MIN_INSTANCE ||
           rule == ScalingPolicyRules.MAX_INSTANCE) {
            this.rule = rule;
            this.instances = instances;
        } else {
            throw new IllegalArgumentException("<ScalingPolicyRuleAction>: RuleAction " + this.name + " must be MIN_INSTANCE or MAX_INSTANCE");
        }
    }

    public void setConditionalRule(ScalingPolicyRules rule, int value, ScalingPolicyActions action, int instances) {
        if (this.enabled) throw new IllegalArgumentException("<ScalingPolicyRuleAction>: Rule Action " + this.name + " already defined");
        if (value < 0) throw new IllegalArgumentException("Value must be a positive integer");
        if (instances < 0) throw new IllegalArgumentException("Instances must be a positive integer");        
        if (rule == ScalingPolicyRules.MAX_INSTANCE || rule == ScalingPolicyRules.MIN_INSTANCE) throw new IllegalArgumentException("RuleAction " + this.name + " must be ON_OVERLOAD, ON_UNHEALTHY, ON_OFFLINE or ON_HEALTHY");
        // if (!isValidSingleDecimal(String.valueOf(value))) throw new IllegalArgumentException("Value must be a single decimal between 0.1 and 0.9");
        
        this.rule = rule;
        this.ruleValue = value;
        this.action = action;
        this.instances = instances;
        this.enabled = true;
    }


    public String getName() {
        return name;
    }

    public ScalingPolicyRules getRule() {
        return rule;
    }

    public int getRuleValue() {
        return ruleValue;
    }

    public ScalingPolicyActions getAction() {
        return action;
    }

    public int getInstances() {
        return instances;
    }
    
    private boolean isValidSingleDecimal(String input) {
        try {
            BigDecimal bd = new BigDecimal(input);
            return bd.compareTo(new BigDecimal("0.1")) >= 0 &&
                    bd.compareTo(new BigDecimal("0.9")) <= 0 &&
                    bd.scale() == 1;
        } catch (NumberFormatException e) {
            return false;
        }
    }


}
