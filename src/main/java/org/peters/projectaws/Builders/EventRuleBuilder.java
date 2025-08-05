package org.peters.projectaws.Builders;

import org.peters.projectaws.Components.EventBridge.EventRule;
import org.peters.projectaws.Components.EventBridge.EventTarget;
import org.peters.projectaws.Core.AWSObject;

public class EventRuleBuilder {
    public EventRule build(String source, String details, EventTarget target) {
        return new EventRule(source, details, target);
    }
}
