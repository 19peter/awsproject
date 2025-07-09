package org.peters.projectaws.Components.Lambda;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.peters.projectaws.Interfaces.Lifecycle.LifecycleManager;
import org.peters.projectaws.Core.AWSObject;
import org.peters.projectaws.Components.Monitors.LambdaExecutionContextMonitor;

public class LambdaExecutionContext
extends AWSObject
implements LifecycleManager {
    
    public LambdaExecutionContextMonitor targetMonitor;
    private  String functionName;
    private final int memoryLimitInMB;
    private final long remainingTimeInMillis;

    private static final Logger logger = LogManager.getLogger(LambdaExecutionContext.class);
    
    public LambdaExecutionContext(String functionName, int memoryLimitInMB, long remainingTimeInMillis) {
        this.functionName = functionName;
        this.memoryLimitInMB = memoryLimitInMB;
        this.remainingTimeInMillis = remainingTimeInMillis;
    }


    @Override
    public void initialize() {
        if (!this.getRunning()) {
            logger.info("<LambdaExecutionContext>: LambdaExecutionContext initialized with id: " + this.getId());
            this.targetMonitor = new LambdaExecutionContextMonitor(this);
            this.targetMonitor.initialize();
            this.setRunning(true);
            
        } else {
            logger.info("<LambdaExecutionContext>: LambdaExecutionContext is already running");
        }
    }


    @Override
    public void shutdown() {
        if (this.getRunning()) {
            this.targetMonitor.shutdown();
            this.targetMonitor = null;
            this.setRunning(false);
            logger.info("<LambdaExecutionContext>: LambdaExecutionContext shutdown with id: " + this.getId());
        } else {
            logger.info("<LambdaExecutionContext>: LambdaExecutionContext is not running");
        }
    }


    @Override
    public boolean isRunning() {
        return this.targetMonitor.getRunning();
    }

    public int getMemoryLimitInMB() {
        return memoryLimitInMB;
    }

    public long getRemainingTimeInMillis() {
        return remainingTimeInMillis;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

}
