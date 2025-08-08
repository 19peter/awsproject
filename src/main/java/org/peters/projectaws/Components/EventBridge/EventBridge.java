package org.peters.projectaws.Components.EventBridge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.peters.projectaws.Builders.EventBuilder;
import org.peters.projectaws.Builders.EventRuleBuilder;
import org.peters.projectaws.Components.DnsRegisterar.DnsRegisterar;
import org.peters.projectaws.Core.AWSEvent;

public class EventBridge {
    private static final Logger logger = LogManager.getLogger(EventBridge.class);
    private static final HashMap<String, List<EventRule>> rules = new HashMap<>(Map.of("DEFAULT", new ArrayList<>()));
    
    EventBridge() {
        EventTarget creationEventTargets = new EventTarget();
        creationEventTargets.addListener(DnsRegisterar.getInstance());
        EventRule ec2CreationEventRule = EventRuleBuilder.build("EC2", "CREATED", creationEventTargets);
        EventRule lbCreationEventRule = EventRuleBuilder.build("LoadBalancer", "CREATED", creationEventTargets);
        EventRule apiGatewayCreationEventRule = EventRuleBuilder.build("ApiGateway", "CREATED", creationEventTargets);
        EventBridge.addRuleToDefaultBus(ec2CreationEventRule);
        EventBridge.addRuleToDefaultBus(lbCreationEventRule);
        EventBridge.addRuleToDefaultBus(apiGatewayCreationEventRule);
    }

    private static class InstanceHolder{
        private static final EventBridge instance = new EventBridge();
    }

    public static EventBridge getInstance() {
        return InstanceHolder.instance;
    }

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
        if (!rules.containsKey(busName)) {
            logger.error("<EventBridge>: Bus " + busName + " does not exist");
            return;
        }
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
