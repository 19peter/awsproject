package org.peters.projectaws.Scenarios;

import org.peters.projectaws.Components.ComponentRegistry.ComponentRegistry;
import org.peters.projectaws.Components.DnsRegisterar.DnsRegisterar;
import org.peters.projectaws.Components.EC2.EC2Types;
import org.peters.projectaws.Components.EventBridge.EventBridge;
import org.peters.projectaws.Components.EventBridge.EventRule;
import org.peters.projectaws.Components.EventBridge.EventTarget;
import org.peters.projectaws.Builders.EC2Builder;
import org.peters.projectaws.Builders.EventRuleBuilder;

public class Test_EventBridge {
    public static void test() {
        DnsRegisterar dnsRegisterar = new DnsRegisterar();
        ComponentRegistry componentRegisterar = new ComponentRegistry();

        EventTarget eventTarget = new EventTarget();
        eventTarget.addListener(componentRegisterar);
        eventTarget.addListener(dnsRegisterar);

        EventRuleBuilder eventRuleBuilder = new EventRuleBuilder();
        EventRule eventRule = eventRuleBuilder.build("EC2", "CREATED", eventTarget);
        EventBridge.addRuleToDefaultBus(eventRule);
        EC2Builder ec2Builder = new EC2Builder("test", EC2Types.T3_MICRO);
        ec2Builder.build();
        
    }
}
