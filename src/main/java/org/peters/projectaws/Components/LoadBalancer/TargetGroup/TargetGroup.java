package org.peters.projectaws.Components.LoadBalancer.TargetGroup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.peters.projectaws.Components.Monitors.TargetMonitor;
import org.peters.projectaws.Core.AWSObject;
import org.peters.projectaws.Interfaces.IntegrationInterfaces.LoadBalancer.TargetIntegrationInterface;
import org.peters.projectaws.Interfaces.IntegrationInterfaces.LoadBalancer.TargetInterfaces.TargetStateObserverInterface;
import org.peters.projectaws.dtos.Request.Request;
import org.peters.projectaws.enums.TargetState;

import java.util.List;

public abstract class TargetGroup <T extends AWSObject>
extends AWSObject
implements TargetIntegrationInterface, TargetStateObserverInterface {
    private static final Logger logger = LogManager.getLogger(TargetGroup.class);
    String path;
    List<T> targetsList;

    public TargetGroup(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public abstract void onTargetStateChanged(TargetMonitor target, TargetState newState);

    public abstract void onRunningRequestsChanged(TargetMonitor target);

    abstract T getAvailableInstance();
    public abstract void addTarget(T target);

}
