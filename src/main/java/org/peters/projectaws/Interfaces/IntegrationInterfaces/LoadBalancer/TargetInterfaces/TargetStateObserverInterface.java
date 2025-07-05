package org.peters.projectaws.Interfaces.IntegrationInterfaces.LoadBalancer.TargetInterfaces;

import org.peters.projectaws.Components.Monitors.TargetMonitor;
import org.peters.projectaws.enums.TargetState;

public interface TargetStateObserverInterface {
    void onTargetStateChanged(TargetMonitor target, TargetState newState);
    void onRunningRequestsChanged(TargetMonitor target);
}
