package org.peters.projectaws.Interfaces.Lifecycle;

/**
 * Interface for managing the lifecycle of components
 */
public interface LifecycleManager {
    /**
     * Initialize the component
     */
    void initialize();
    
    /**
     * Shutdown the component gracefully
     */
    void shutdown();
    
    /**
     * Check if the component is running
     */
    boolean isRunning();
}
