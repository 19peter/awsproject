package org.peters.projectaws.Interfaces.EventBridgeListener;

import org.peters.projectaws.Core.AWSEvent;

public interface EventBridgeListener {
    void onEvent(AWSEvent event);
}
