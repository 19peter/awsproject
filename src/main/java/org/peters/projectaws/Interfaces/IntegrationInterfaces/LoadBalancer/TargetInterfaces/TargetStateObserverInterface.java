package org.peters.projectaws.Interfaces.IntegrationInterfaces.LoadBalancer.TargetInterfaces;

import org.peters.projectaws.enums.TargetState;
import org.peters.projectaws.Core.AWSObject;

public interface TargetStateObserverInterface<T extends AWSObject> {
    void onTargetStateChanged(T target, TargetState newState);
    void onRunningRequestsChanged(T target);
}
