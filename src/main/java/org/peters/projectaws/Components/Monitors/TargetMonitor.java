package org.peters.projectaws.Components.Monitors;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.peters.projectaws.Core.AWSObject;
import org.peters.projectaws.Interfaces.IntegrationInterfaces.LoadBalancer.TargetInterfaces.TargetStateObserverInterface;
import org.peters.projectaws.enums.TargetState;

public abstract class TargetMonitor<T extends AWSObject> extends AWSObject {

    protected final Object lock = new Object();
    protected final CopyOnWriteArrayList<TargetStateObserverInterface<T>> observers = new CopyOnWriteArrayList<>();
    protected final AtomicInteger runningRequests = new AtomicInteger(0);
    protected final AtomicInteger MAXCONN;
    protected final AtomicBoolean isRunning = new AtomicBoolean(false);
    protected TargetState state = TargetState.HEALTHY;
    
    public TargetMonitor(int maxConn) {
        this.MAXCONN = new AtomicInteger(maxConn);
    }

    public TargetMonitor(int maxConn, String name) {
        super(name);
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

    public final synchronized void setState(TargetState state) {
        doSetState(state);
    }
    

    public final synchronized void setTargetUnhealthy() {
        doSetTargetUnhealthy();
    }

    public final synchronized int getRunningRequests() {
        return doGetRunningRequests();
    }

    public final synchronized boolean addRunningRequest() throws InterruptedException {
        return doAddRunningRequest();
    }

    public final synchronized boolean removeRunningRequest() {
        return doRemoveRunningRequest();
    }

    public final synchronized void addObserver(TargetStateObserverInterface<T> observer) {
        doAddObserver(observer);
    }

    public final synchronized void removeObserver(TargetStateObserverInterface<T> observer) {
        doRemoveObserver(observer);
    }

    public final synchronized void notifyObserversOfStateChange(TargetState oldState, TargetState newState) {
        doNotifyObserversOfStateChange(oldState, newState);
    }

    public final synchronized void notifyObserversOfRunningRequestsChange() {
        doNotifyObserversOfRunningRequestsChange();
    }

    public final synchronized void shutdown() {
        doShutdown();
    }

    public final synchronized void initialize() {
        doInitialize();
    }

    // Abstract hook methods that must be implemented by subclasses
    protected abstract TargetState doGetState();
    protected abstract void doSetState(TargetState state);
    protected abstract void doSetTargetUnhealthy();
    protected abstract int doGetRunningRequests();
    protected abstract boolean doAddRunningRequest() throws InterruptedException;
    protected abstract boolean doRemoveRunningRequest();
    protected abstract void doAddObserver(TargetStateObserverInterface<T> observer);
    protected abstract void doRemoveObserver(TargetStateObserverInterface<T> observer);
    protected abstract void doNotifyObserversOfStateChange(TargetState oldState, TargetState newState);
    protected abstract void doNotifyObserversOfRunningRequestsChange();
    protected abstract void doShutdown();
    protected abstract void doInitialize();
}
