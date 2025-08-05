package org.peters.projectaws.Components.EventBridge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.peters.projectaws.Core.AWSEvent;

public abstract class EventBridge {
    private static final Logger logger = LogManager.getLogger(EventBridge.class);
    private static final HashMap<String, List<EventRule>> rules = new HashMap<>(Map.of("DEFAULT", new ArrayList<>()));

    public static void createCustomBus(String busName) {
        rules.put(busName, new ArrayList<>());
    }

    public static void addRuleToDefaultBus(EventRule rule) {
        rules.get("DEFAULT").add(rule);
    }

    public static void addRule(String busName, EventRule rule) {
        rules.get(busName).add(rule);
    }

    public static void publishEvent(AWSEvent event, String busName) {
        logger.info("<EventBridge>: Publishing event " + event.getName() + " to bus " + busName);
        rules.get(busName).forEach(rule ->
        {
            if (rule.matches(event))
            {
                logger.info("<EventBridge>: Event " + event.getName() + " matches rule ");
                rule.getTarget()
                .getListeners()
                .forEach(listener -> listener.onEvent(event));
            }
        });
    }
}
