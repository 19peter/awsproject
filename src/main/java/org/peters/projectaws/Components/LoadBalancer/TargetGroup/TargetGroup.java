package org.peters.projectaws.Components.LoadBalancer.TargetGroup;

import org.peters.projectaws.Core.AWSObject;
import org.peters.projectaws.Interfaces.IntegrationInterfaces.LoadBalancer.TargetIntegrationInterface;
import org.peters.projectaws.Interfaces.IntegrationInterfaces.LoadBalancer.TargetInterfaces.TargetStateObserverInterface;
import org.peters.projectaws.enums.TargetState;


import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class TargetGroup <T extends AWSObject>
extends AWSObject
implements TargetIntegrationInterface, TargetStateObserverInterface<T> {
    String path;
    CopyOnWriteArrayList<T> targetsList;

    public TargetGroup(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public abstract void onTargetStateChanged(T target, TargetState newState);

    public abstract void onRunningRequestsChanged(T target);

    abstract Optional<T> getAvailableInstance();
    
    public abstract void addTarget(T target);
    public abstract void removeTarget(T target);

}
