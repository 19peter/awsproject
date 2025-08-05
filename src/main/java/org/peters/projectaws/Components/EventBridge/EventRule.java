package org.peters.projectaws.Components.EventBridge;

import org.peters.projectaws.Core.AWSEvent;
import org.peters.projectaws.Core.AWSObject;

public class EventRule {
    String source;
    String detailState;
    EventTarget target;

    public EventRule(String source, String detailState, EventTarget target) {
        this.source = source;
        this.detailState = detailState;
        this.target = target;
    }

    public boolean matches(AWSEvent event) {
        System.out.println(event.getSource());
        System.out.println(this.source);
        System.out.println(event.getState());
        System.out.println(this.detailState);
        return event.getSource().equals(this.source) && event.getState().equals(this.detailState);
    }

    public EventTarget getTarget() {
        return target;
    }
}
