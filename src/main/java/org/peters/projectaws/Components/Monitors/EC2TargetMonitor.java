package org.peters.projectaws.Components.Monitors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.peters.projectaws.Components.EC2.EC2;
import org.peters.projectaws.Interfaces.Integration.LoadBalancer.TargetInterfaces.TargetStateObserverInterface;
import org.peters.projectaws.enums.TargetState;

public class EC2TargetMonitor extends TargetMonitor<EC2> {
    private static final Logger logger = LogManager.getLogger(EC2TargetMonitor.class);
    private final EC2 ec2Instance;

    public EC2TargetMonitor(EC2 ec2Instance, int maxConn, String name) {
        super(maxConn, name);
        this.ec2Instance = ec2Instance;
        logger.info("<EC2TargetMonitor>: EC2TargetMonitor created with maxConn: " + maxConn + " and name: " + this.getName() + " and id: " + this.getId());
    }

    @Override
    protected void doNotifyObserversOfStateChange(TargetState oldState, TargetState newState) {
        for (TargetStateObserverInterface<EC2> observer : observers) {
            observer.onTargetStateChanged(ec2Instance, oldState, newState);
        }
    }

    @Override
    protected void doNotifyObserversOfRunningRequestsChange() {
        for (TargetStateObserverInterface<EC2> observer : observers) {
            observer.onRunningRequestsChanged(ec2Instance, runningRequests.get());
        }
    }

    @Override
    protected TargetState doGetState() {
        return state;
    }

    @Override
    protected void doSetState(TargetState state) {
        TargetState oldState = this.state;
        this.state = state;
        notifyObserversOfStateChange(oldState, state);
    }

    @Override
    protected void doSetTargetUnhealthy() {
        TargetState oldState = this.state;
        this.state = TargetState.UNHEALTHY;
        notifyObserversOfStateChange(oldState, TargetState.UNHEALTHY);
    }

    @Override
    protected int doGetRunningRequests() {
        return runningRequests.get();
    }

    @Override
    protected boolean doAddRunningRequest() throws InterruptedException {
        if (runningRequests.get() < MAXCONN.get()) {
            runningRequests.incrementAndGet();
            logger.info("<EC2TargetMonitor>: Monitor " + this.getName() + " added running request");
        } else {
            logger.info("<EC2TargetMonitor>: Monitor " + this.getName() + " is overloaded");
            return false;
        }
        
        if (runningRequests.get() == MAXCONN.get()) {
            TargetState oldState = this.state;
            this.state = TargetState.OVERLOADED;
            notifyObserversOfStateChange(oldState, TargetState.OVERLOADED);
        }
        return true;
    }

    @Override
    protected boolean doRemoveRunningRequest() {
        if (runningRequests.get() > 0) {
            runningRequests.decrementAndGet();
            logger.info("<EC2TargetMonitor>: Monitor " + this.getName() + " removed running request");
        } else {
            logger.info("<EC2TargetMonitor>: Monitor " + this.getName() + " has no running requests");
            return false;
        }
        
        if (runningRequests.get() < MAXCONN.get()) {
            TargetState oldState = this.state;
            this.state = TargetState.HEALTHY;
            notifyObserversOfRunningRequestsChange();
            notifyObserversOfStateChange(oldState, TargetState.HEALTHY);
        }

        if (runningRequests.get() == 0) {
            TargetState oldState = this.state;
            this.state = TargetState.IDLE;
            notifyObserversOfRunningRequestsChange();
            notifyObserversOfStateChange(oldState, TargetState.IDLE);
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
        TargetState oldState = this.state;
        this.state = TargetState.OFFLINE;
        notifyObserversOfStateChange(oldState, TargetState.OFFLINE);
    }

    @Override
    protected void doInitialize() {
        notifyObserversOfStateChange(null, TargetState.HEALTHY);
    }

    
}
