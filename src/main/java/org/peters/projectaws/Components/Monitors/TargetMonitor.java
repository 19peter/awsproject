package org.peters.projectaws.Components.Monitors;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.peters.projectaws.Core.AWSObject;
import org.peters.projectaws.Interfaces.IntegrationInterfaces.LoadBalancer.TargetInterfaces.TargetStateObserverInterface;
import org.peters.projectaws.Interfaces.Lifecycle.LifecycleManager;

public abstract class TargetMonitor 
extends AWSObject 
implements LifecycleManager {

    protected final Object lock = new Object();
    protected final CopyOnWriteArrayList<TargetStateObserverInterface> observers = new CopyOnWriteArrayList<>();
    protected final AtomicInteger runningRequests = new AtomicInteger(0);
    protected final AtomicInteger MAXCONN;
    protected final AtomicBoolean isRunning = new AtomicBoolean(false);
    
    protected TargetState state = TargetState.HEALTHY;
    protected ExecutorService executor;
    private static Logger logger = Logger.getLogger(TargetMonitor.class.getName());

    public TargetMonitor(int maxConn) {
        this.MAXCONN = new AtomicInteger(maxConn);
        this.executor = Executors.newFixedThreadPool(maxConn);
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

    @Override
    public synchronized void initialize() {
        if (isRunning.get()) {
            logger.info("EC instance " + this.getId() + " is already initialized");
            return;
        }

        isRunning.set(true);
        logger.info("EC instance " + this.getId() + " initialized");
    }

    @Override
    public synchronized void shutdown() {
        executor.shutdown(); // Stop accepting new tasks
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow(); // Force shutdown if not finished
                this.isRunning.set(false);
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public synchronized boolean isRunning() {
        return this.isRunning.get();
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
