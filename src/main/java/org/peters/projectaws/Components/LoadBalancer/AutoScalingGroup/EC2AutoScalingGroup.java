package org.peters.projectaws.Components.LoadBalancer.AutoScalingGroup;

import org.peters.projectaws.Builders.EC2Builder;
import org.peters.projectaws.Components.API.Api;
import org.peters.projectaws.Components.EC2.EC2;
import org.peters.projectaws.Components.LoadBalancer.TargetGroup.EC2TargetGroup;
import org.peters.projectaws.Components.Policies.ScalingPolicy.ScalingPolicy;
import org.peters.projectaws.Components.Policies.ScalingPolicy.ScalingPolicyRuleAction;
import org.peters.projectaws.Interfaces.IntegrationInterfaces.LoadBalancer.AutoScalingGroupInterface;
import org.peters.projectaws.enums.ScalingPolicyActions;
import org.peters.projectaws.enums.ScalingPolicyRules;
import org.peters.projectaws.enums.TargetState;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EC2AutoScalingGroup 
extends EC2TargetGroup 
implements AutoScalingGroupInterface 
{
    private static final Logger logger = LogManager.getLogger(EC2AutoScalingGroup.class);
    private ScalingPolicy scalingPolicy;
    
    public EC2AutoScalingGroup(ScalingPolicy scalingPolicy, String path, EC2 ec2) {
        super(path);
        this.scalingPolicy = scalingPolicy;
        addTarget(ec2);
        applyPolicyMinInstance();
        logger.info("<EC2AutoScalingGroup>: EC2AutoScalingGroup created with path: " + path + " and Id: " + this.getId());
    }

    @Override
    public void onTargetStateChanged(EC2 target, TargetState oldState, TargetState newState) {
        if (newState == TargetState.UNHEALTHY || newState == TargetState.OFFLINE) {
            handleUnavailableTarget(target, oldState, newState);
        }
        targetGroupDetails.updateByState(oldState, newState);
        applyPolicyIfApplicable(newState);
    }

    @Override
    public void onRunningRequestsChanged(EC2 target, int runningRequests) {
        
    }

    @Override
    public void applyPolicyIfApplicable(TargetState newState) {
        logger.info("<EC2AutoScalingGroup>: Target state changed to " + newState + " Checking policy");
        ScalingPolicyRuleAction ruleAction = getRuleByState(newState);
        if (ruleAction == null)
            return;
        logger.info("<EC2AutoScalingGroup>: Policy found: " + ruleAction.getRule());
        applyPolicy(ruleAction);
    }

    @Override
    public void applyPolicy(ScalingPolicyRuleAction ruleAction) {
        switch (ruleAction.getRule()) {
            case MIN_INSTANCE:
                applyPolicyMinInstance();
                break;
            case MAX_INSTANCE:
                applyPolicyMaxInstance();
                break;
            default:
                applyConditionalPolicy(ruleAction);
                break;
        }

    }

    @Override
    public ScalingPolicyRuleAction getRuleByState(TargetState state) {
        return scalingPolicy.getRule(ScalingPolicyRules.valueOf(state.name()));
    }

    @Override
    public void applyConditionalPolicy(ScalingPolicyRuleAction ruleAction) {
        int value = ruleAction.getRuleValue();
        ScalingPolicyRules rule = ruleAction.getRule();
        ScalingPolicyActions action = ruleAction.getAction();
        int instances = ruleAction.getInstances();
        

        long count = targetsList.stream()
                .filter(target -> target.getTargetMonitorState().toString().equals(rule.toString()))
                .count();

        if (count >= value) {
            switch (action) {
                case SCALE_UP:
                    requestScaleUp(instances);
                    break;
                case SCALE_DOWN:
                    requestScaleDown(instances);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void applyPolicyMinInstance() {
        ScalingPolicyRuleAction ruleAction = scalingPolicy.getRule(ScalingPolicyRules.MIN_INSTANCE);
        if (ruleAction != null) {
            logger.info("<EC2AutoScalingGroup>: Applying policy Provision minimum instance");
            if (targetGroupDetails.getCurrentTargets() < ruleAction.getInstances()) {
                scaleUp(ruleAction.getInstances());
            }
        }
    }

    @Override
    public void applyPolicyMaxInstance() {
        ScalingPolicyRuleAction ruleAction = scalingPolicy.getRule(ScalingPolicyRules.MAX_INSTANCE);
        if (ruleAction != null) {
            logger.info("<EC2AutoScalingGroup>: Applying policy Limit maximum instance");
            if (targetGroupDetails.getCurrentTargets() > ruleAction.getInstances()) {
                scaleDown(ruleAction.getInstances());
            }
        }
    }

    @Override
    public void requestScaleUp(int instances) {
        int currentInstances = targetGroupDetails.getCurrentTargets();
        int max_instances = scalingPolicy.getRule(ScalingPolicyRules.MAX_INSTANCE).getInstances();
        if (currentInstances == max_instances) {
            logger.info("<EC2AutoScalingGroup>: MAX INSTANCES " + max_instances + " REACHED");
            return;
        } else if (currentInstances + instances >= max_instances) {
            logger.info("<EC2AutoScalingGroup>: Scaling up by: " + (max_instances - currentInstances));
            scaleUp(max_instances - currentInstances);
            return;
        } else {
            logger.info("<EC2AutoScalingGroup>: Scaling up by: " + instances);
            scaleUp(instances);
            return;
        }
    }

    @Override
    public void requestScaleDown(int instances) {
        int currentInstances = targetGroupDetails.getCurrentTargets();
        int min_instances = scalingPolicy.getRule(ScalingPolicyRules.MIN_INSTANCE).getInstances();
        if (currentInstances == min_instances) {
            logger.info("<EC2AutoScalingGroup>: MIN INSTANCES " + min_instances + " REACHED");
            return;
        } else if (currentInstances - instances < min_instances) {
            logger.info("<EC2AutoScalingGroup>: Scaling down by: " + (min_instances - currentInstances));
            scaleDown(min_instances - currentInstances);
            return;
        } else {
            logger.info("<EC2AutoScalingGroup>: Scaling down by: " + instances);
            scaleDown(instances);
            return;
        }
    }

    private void scaleUp(int instances) {
        logger.info("<EC2AutoScalingGroup>: Attempting to scale up");

        EC2 instance = targetsList.get(0); 
        if (instance == null) {
            logger.warn("<EC2AutoScalingGroup>: CAN'T SCALE UP: No healthy instances found");
            return;
        }

        for (int i = 0; i < instances; i++) {
            EC2Builder ec2Builder = new EC2Builder("ec2-" + targetsList.size() + "-SCALE-UP", instance.getMaxConn());
            // EC2 ec2 = ec2Builder.createEc2(instance.getApis(), instance.getMaxConn(), "ec2-" + targetsList.size() + "-SCALE-UP");
            EC2 ec2 = ec2Builder.build();
            ec2.initialize();
            addTarget(ec2);
            logger.info("<EC2AutoScalingGroup>: SCALING UP: Added instance " + ec2.getId());
        }
    }

    private void scaleDown(int instances) {
        logger.info("<EC2AutoScalingGroup>: Attempting to scale down");
        for (int i = 0; i < instances; i++) {
            EC2 ec2 = targetsList.stream()
                    .filter(target -> target.getTargetMonitorState() == TargetState.IDLE)
                    .findFirst()
                    .orElse(null);
            if (ec2 != null) {
                removeTarget(ec2);
                ec2.shutdown();
                targetGroupDetails.removeCurrentTarget(TargetState.IDLE);
                logger.info("<EC2AutoScalingGroup>: SCALING DOWN: Removed instance " + ec2.getId());
            }
        }
    }

    private void scaleDown(int instances, TargetState state) {
        logger.info("<EC2AutoScalingGroup>: Attempting to scale down");
        for (int i = 0; i < instances; i++) {
            EC2 ec2 = targetsList.stream()
                    .filter(target -> target.getTargetMonitorState() == state)
                    .findFirst()
                    .orElse(null);
            if (ec2 != null) {
                removeTarget(ec2);
                ec2.shutdown();
                targetGroupDetails.removeCurrentTarget(state);
                logger.info("<EC2AutoScalingGroup>: SCALING DOWN: Removed instance " + ec2.getId());
            }
        }
    }


}
