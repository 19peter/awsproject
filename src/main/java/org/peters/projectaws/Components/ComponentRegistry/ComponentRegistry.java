package org.peters.projectaws.Components.ComponentRegistry;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.peters.projectaws.Core.AWSObject;

public class ComponentRegistry  {
    private static final Logger logger = LogManager.getLogger(ComponentRegistry.class);
    private static HashMap<String, AWSObject> components = new HashMap<>();
    private static class InstanceHolder{
        private static final ComponentRegistry instance = new ComponentRegistry();
    }
    
    public static ComponentRegistry getInstance() {
        return InstanceHolder.instance;
    }
    
    public static void registerComponent(AWSObject component) {
        if (components.containsKey(component.getName())) {
            logger.error("<ComponentRegistry>: Component " + component.getName() + " already exists");
            throw new IllegalArgumentException("Component " + component.getName() + " already exists");
        }
        components.put(component.getName(), component);
        logger.info("<ComponentRegistry>: Component " + component.getName() + " registered successfully");
    }

    public static void unregisterComponent(AWSObject component) {
        if (!components.containsKey(component.getName())) {
            logger.error("<ComponentRegistry>: Component " + component.getName() + " does not exist");
            return;
        }
        components.remove(component.getName());
        logger.info("<ComponentRegistry>: Component " + component.getName() + " unregistered successfully");
    }

    public static AWSObject getComponent(String name) {
        if (!components.containsKey(name)) {
            logger.error("<ComponentRegistry>: Component " + name + " does not exist");
            return null;
        }
        return components.get(name);
    }

    public static boolean isComponentRegistered(String name) {
        return components.containsKey(name);
    }


    
}
