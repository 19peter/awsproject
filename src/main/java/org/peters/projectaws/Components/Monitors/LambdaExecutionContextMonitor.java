package org.peters.projectaws.Components.Monitors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.peters.projectaws.Components.Lambda.LambdaExecutionContext;
import org.peters.projectaws.Interfaces.IntegrationInterfaces.LoadBalancer.TargetInterfaces.TargetStateObserverInterface;
import org.peters.projectaws.enums.TargetState;

public class LambdaExecutionContextMonitor extends TargetMonitor<LambdaExecutionContext> {
    private static final Logger logger = LogManager.getLogger(LambdaExecutionContextMonitor.class);
    private final LambdaExecutionContext lambdaExecutionContext;

    public LambdaExecutionContextMonitor(LambdaExecutionContext lambdaExecutionContext) {
        super(1);
        this.lambdaExecutionContext = lambdaExecutionContext;
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
            logger.info("<LambdaExecutionContextMonitor>: Monitor " + this.getId() + " added running request");
        } else {
            logger.info("<LambdaExecutionContextMonitor>: Monitor " + this.getId() + " is overloaded");
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
            logger.info("<LambdaExecutionContextMonitor>: Monitor " + this.getId() + " removed running request");
        } else {
            logger.info("<LambdaExecutionContextMonitor>: Monitor " + this.getId() + " has no running requests");
            return false;
        }
        
        if (runningRequests.get() == 0) {
            this.state = TargetState.HEALTHY;
            notifyObserversOfStateChange(TargetState.HEALTHY);
        }
        return true;
    }

    @Override
    protected void doAddObserver(TargetStateObserverInterface<LambdaExecutionContext> observer) {
        observers.add(observer);
    }

    @Override
    protected void doRemoveObserver(TargetStateObserverInterface<LambdaExecutionContext> observer) {
        observers.remove(observer);
    }

    @Override
    protected void doNotifyObserversOfStateChange(TargetState newState) {
        for (TargetStateObserverInterface<LambdaExecutionContext> observer : observers) {
            observer.onTargetStateChanged(lambdaExecutionContext, newState);
        }
    }

    @Override
    protected void doNotifyObserversOfRunningRequestsChange() {
        for (TargetStateObserverInterface<LambdaExecutionContext> observer : observers) {
            observer.onRunningRequestsChanged(lambdaExecutionContext);
        }
    }

    @Override
    protected void doShutdown() {
        doNotifyObserversOfStateChange(TargetState.OFFLINE);
    }

    @Override
    protected void doInitialize() {
        doNotifyObserversOfStateChange(TargetState.HEALTHY);
    }
}
