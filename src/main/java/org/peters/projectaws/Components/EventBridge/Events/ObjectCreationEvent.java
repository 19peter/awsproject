package org.peters.projectaws.Components.EventBridge.Events;

import org.peters.projectaws.Core.AWSEvent;
import org.peters.projectaws.Core.AWSObject;
import org.peters.projectaws.enums.EventBridgeDefaultEvents;

public class ObjectCreationEvent extends AWSEvent {

    public ObjectCreationEvent(AWSObject object, String state) {
        super(EventBridgeDefaultEvents.ObjectCreationEvent.name(), object, state);
    }
    
}
