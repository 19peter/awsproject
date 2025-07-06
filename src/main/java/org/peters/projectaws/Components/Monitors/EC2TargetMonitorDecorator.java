package org.peters.projectaws.Components.Monitors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.peters.projectaws.Components.EC2.EC2;
import org.peters.projectaws.Interfaces.IntegrationInterfaces.LoadBalancer.TargetInterfaces.TargetStateObserverInterface;
import org.peters.projectaws.enums.TargetState;

public class EC2TargetMonitorDecorator extends TargetMonitor<EC2> {
    private static final Logger logger = LogManager.getLogger(EC2TargetMonitorDecorator.class);
    private final EC2 ec2Instance;

    public EC2TargetMonitorDecorator(EC2 ec2Instance, int maxConn) {
        super(maxConn);
        this.ec2Instance = ec2Instance;
        logger.info("<EC2TargetMonitorDecorator>: EC2TargetMonitor created with maxConn: " + maxConn + " and id: " + this.getId());
    }

    @Override
    protected void doNotifyObserversOfStateChange(TargetState newState) {
        for (TargetStateObserverInterface<EC2> observer : observers) {
            observer.onTargetStateChanged(ec2Instance, newState);
        }
    }

    @Override
    protected void doNotifyObserversOfRunningRequestsChange() {
        for (TargetStateObserverInterface<EC2> observer : observers) {
            observer.onRunningRequestsChanged(ec2Instance);
        }
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
    protected boolean doAddRunningRequest() throws InterruptedException {
        if (runningRequests.get() < MAXCONN.get()) {
            runningRequests.incrementAndGet();
        } else {
            logger.info("<EC2TargetMonitorDecorator>: Monitor " + this.getId() + " is overloaded");
            return false;
        }
        
        if (runningRequests.get() == MAXCONN.get()) {
            this.state = TargetState.OVERLOADED;
            notifyObserversOfStateChange(TargetState.OVERLOADED);
        }
        return true;
    }

    @Override
    protected boolean doRemoveRunningRequest() {
        if (runningRequests.get() > 0) {
            runningRequests.decrementAndGet();
        } else {
            logger.info("<EC2TargetMonitorDecorator>: Monitor " + this.getId() + " has no running requests");
            return false;
        }
        
        if (runningRequests.get() < MAXCONN.get()) {
            state = TargetState.HEALTHY;
            notifyObserversOfRunningRequestsChange();
        }
        return true;
    }

    @Override
    protected void doAddObserver(TargetStateObserverInterface<EC2> observer) {
        observers.add(observer);
    }

    @Override
    protected void doRemoveObserver(TargetStateObserverInterface<EC2> observer) {
        observers.remove(observer);
    }

    @Override
    protected void doShutdown() {
        notifyObserversOfStateChange(TargetState.OFFLINE);
    }

    @Override
    protected void doInitialize() {
        notifyObserversOfStateChange(TargetState.HEALTHY);
    }

    
}
