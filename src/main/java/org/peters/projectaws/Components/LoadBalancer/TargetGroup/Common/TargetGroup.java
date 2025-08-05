package org.peters.projectaws.Components.LoadBalancer.TargetGroup.Common;

import org.peters.projectaws.Core.AWSObject;
import org.peters.projectaws.Interfaces.Integration.LoadBalancer.TargetIntegration;
import org.peters.projectaws.Interfaces.Integration.LoadBalancer.TargetInterfaces.TargetStateObserverInterface;
import org.peters.projectaws.enums.TargetState;
import org.peters.projectaws.dtos.TargetGroupDetails.TargetGroupDetails;

import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class TargetGroup <T extends AWSObject>
extends AWSObject
implements TargetIntegration, TargetStateObserverInterface<T> {
    String path;
    protected CopyOnWriteArrayList<T> targetsList;
    protected TargetGroupDetails targetGroupDetails;

    public TargetGroup(String path) {
        this.path = path;
        this.targetGroupDetails = new TargetGroupDetails();
    }

    public String getPath() {
        return path;
    }

    
    public abstract void onTargetStateChanged(T target, TargetState oldState, TargetState newState);
    public abstract void onRunningRequestsChanged(T target, int runningRequests);
    public abstract Optional<T> getAvailableInstance();
    public abstract void addTarget(T target);
    public abstract void removeTarget(T target);

    
}
