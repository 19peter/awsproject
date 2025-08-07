package org.peters.projectaws.Core;

import org.peters.projectaws.Components.EventBridge.Events.ObjectCreationEvent;
import org.peters.projectaws.Components.ComponentRegistry.ComponentRegistry;
import org.peters.projectaws.Components.EventBridge.EventBridge;

public abstract class AWSBuilderObject<T extends AWSObject> {
    protected abstract T buildProcess();

    private void notifyComponentCreation(T component) {
        EventBridge.publishEvent(new ObjectCreationEvent(component, "CREATED"), "DEFAULT");
    }

    public T build() {
        T component = buildProcess();
        ComponentRegistry.registerComponent(component);
        notifyComponentCreation(component);
        return component;
    };


}
