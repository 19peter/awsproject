package org.peters.projectaws.Components.ComponentRegistry;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.peters.projectaws.Core.AWSEvent;
import org.peters.projectaws.Core.AWSObject;
import org.peters.projectaws.Interfaces.EventBridgeListener.EventBridgeListener;

public class ComponentRegistry implements EventBridgeListener {
    private static final Logger logger = LogManager.getLogger(ComponentRegistry.class);
    private static HashMap<String, AWSObject> components = new HashMap<>();
    
    public static void registerComponent(AWSObject component) {
        if (components.containsKey(component.getName())) {
            logger.error("<ComponentRegistry>: Component " + component.getName() + " already exists");
            return;
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

    @Override
    public void onEvent(AWSEvent event) {
        if (event.getName().equals("ObjectCreationEvent")) {
            registerComponent(event.getSourceObject());
        }
    }
    
}
