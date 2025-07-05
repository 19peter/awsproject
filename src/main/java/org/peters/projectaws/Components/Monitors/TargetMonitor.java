package org.peters.projectaws.Components.Monitors;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.peters.projectaws.Core.AWSObject;
import org.peters.projectaws.Interfaces.IntegrationInterfaces.LoadBalancer.TargetInterfaces.TargetStateObserverInterface;

public abstract class TargetMonitor extends AWSObject {

    protected final Object lock = new Object();
    protected final CopyOnWriteArrayList<TargetStateObserverInterface> observers = new CopyOnWriteArrayList<>();
    protected final AtomicInteger runningRequests = new AtomicInteger(0);
    protected final AtomicInteger MAXCONN;
    protected final AtomicBoolean isRunning = new AtomicBoolean(false);
    protected TargetState state = TargetState.HEALTHY;
    
    public TargetMonitor(int maxConn) {
        this.MAXCONN = new AtomicInteger(maxConn);
    }

  

    public int getMaxConnections() {
        return MAXCONN.get();
    }

    public TargetState getHealthState() {
        return getState();
    }

    // Template methods that ensure synchronization in all implementations
    public final synchronized TargetState getState() {
        return doGetState();
    }

    public final synchronized void setTargetUnhealthy() {
        doSetTargetUnhealthy();
    }

    public final synchronized int getRunningRequests() {
        return doGetRunningRequests();
    }

    public final synchronized void addRunningRequest() throws InterruptedException {
        doAddRunningRequest();
    }

    public final synchronized void removeRunningRequest() {
        doRemoveRunningRequest();
    }

    public final synchronized void addObserver(TargetStateObserverInterface observer) {
        doAddObserver(observer);
    }

    protected final synchronized void removeObserver(TargetStateObserverInterface observer) {
        doRemoveObserver(observer);
    }

    protected final synchronized void notifyObserversOfStateChange(TargetState newState) {
        doNotifyObserversOfStateChange(newState);
    }

    protected final synchronized void notifyObserversOfRunningRequestsChange() {
        doNotifyObserversOfRunningRequestsChange();
    }


    // Abstract hook methods that must be implemented by subclasses
    protected abstract TargetState doGetState();
    protected abstract void doSetTargetUnhealthy();
    protected abstract int doGetRunningRequests();
    protected abstract void doAddRunningRequest() throws InterruptedException;
    protected abstract void doRemoveRunningRequest();
    protected abstract void doAddObserver(TargetStateObserverInterface observer);
    protected abstract void doRemoveObserver(TargetStateObserverInterface observer);
    protected abstract void doNotifyObserversOfStateChange(TargetState newState);
    protected abstract void doNotifyObserversOfRunningRequestsChange();

}
