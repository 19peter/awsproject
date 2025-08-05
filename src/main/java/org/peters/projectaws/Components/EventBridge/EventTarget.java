package org.peters.projectaws.Components.EventBridge;

import java.util.ArrayList;
import java.util.List;

import org.peters.projectaws.Interfaces.EventBridgeListener.EventBridgeListener;

public class EventTarget {
    List<EventBridgeListener> listeners;

    public EventTarget() {
        this.listeners = new ArrayList<>();
    }

    public void addListener(EventBridgeListener listener) {
        this.listeners.add(listener);
    }

    public List<EventBridgeListener> getListeners() {
        return listeners;
    }

}
