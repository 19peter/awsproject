package org.peters.projectaws.Interfaces.Integration.LoadBalancer.TargetInterfaces;

import org.peters.projectaws.enums.TargetState;
import org.peters.projectaws.Core.AWSObject;

public interface TargetStateObserverInterface<T extends AWSObject> {
    void onTargetStateChanged(T target, TargetState oldState, TargetState newState);
    void onRunningRequestsChanged(T target, int runningRequests);
}
