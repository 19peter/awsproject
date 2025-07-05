package org.peters.projectaws.Components.Monitors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.peters.projectaws.Interfaces.IntegrationInterfaces.LoadBalancer.TargetInterfaces.TargetStateObserverInterface;

public class EC2TargetMonitorDecorator extends TargetMonitor {
    private static final Logger logger = LogManager.getLogger(EC2TargetMonitorDecorator.class);

    public EC2TargetMonitorDecorator(int maxConn) {
        super(maxConn);
        logger.info("EC2TargetMonitor created with maxConn: " + maxConn + " and id: " + this.getId());
    }

    @Override
    protected TargetState doGetState() {
        return state;
    }

    @Override
    protected void doSetTargetUnhealthy() {
        this.state = TargetState.UNHEALTHY;
        notifyObserversOfStateChange(TargetState.UNHEALTHY);
    }

    @Override
    protected int doGetRunningRequests() {
        return runningRequests.get();
    }

    @Override
    protected void doAddRunningRequest() throws InterruptedException {
        if (runningRequests.get() < MAXCONN.get()) {
            runningRequests.incrementAndGet();
        } else {
            logger.info("Target " + this.getId() + " is overloaded");
            throw new InterruptedException("Target " + this.getId() + " is overloaded");
        }
        
        if (runningRequests.get() == MAXCONN.get()) {
            this.state = TargetState.OVERLOADED;
            notifyObserversOfStateChange(TargetState.OVERLOADED);
        }
    }

    @Override
    protected void doRemoveRunningRequest() {
        if (runningRequests.get() > 0) {
            runningRequests.decrementAndGet();
        }
        
        if (runningRequests.get() < MAXCONN.get()) {
            state = TargetState.HEALTHY;
            notifyObserversOfRunningRequestsChange();
        }
    }

    @Override
    protected void doAddObserver(TargetStateObserverInterface observer) {
        observers.add(observer);
    }

    @Override
    protected void doRemoveObserver(TargetStateObserverInterface observer) {
        observers.remove(observer);
    }

    @Override
    protected void doNotifyObserversOfStateChange(TargetState newState) {
        for (TargetStateObserverInterface observer : observers) {
            observer.onTargetStateChanged(this, newState);
        }
    }

    @Override
    protected void doNotifyObserversOfRunningRequestsChange() {
        for (TargetStateObserverInterface observer : observers) {
            observer.onRunningRequestsChanged(this);
        }
    }
}
