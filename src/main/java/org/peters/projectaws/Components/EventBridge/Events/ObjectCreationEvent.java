package org.peters.projectaws.Components.EventBridge.Events;

import org.peters.projectaws.Core.AWSEvent;
import org.peters.projectaws.Core.AWSObject;

public class ObjectCreationEvent extends AWSEvent {

    public ObjectCreationEvent(AWSObject object, String state) {
        super("ObjectCreationEvent", object, state);
    }
    
}
