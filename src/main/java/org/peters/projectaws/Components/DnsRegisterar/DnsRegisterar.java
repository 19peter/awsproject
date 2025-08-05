package org.peters.projectaws.Components.DnsRegisterar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.peters.projectaws.Core.AWSEvent;
import org.peters.projectaws.Interfaces.EventBridgeListener.EventBridgeListener;

public class DnsRegisterar implements EventBridgeListener {
    private static Map<String, String> dnsMap = new HashMap<>();
    private static final String DNS_DOMAIN = "awsproject.dns";
    private static final Logger logger = LogManager.getLogger(DnsRegisterar.class);
    private List<String> allowedComponents = new ArrayList<>(List.of("EC2",
            "Lambda",
            "S3",
            "LoadBalancer",
            "ApiGateway"));

    public static void register(String componentName) {
        String dnsName = generateDnsName(componentName);
        dnsMap.put(componentName, dnsName);
        logger.info("<DNS>: Registered component: " + componentName + " with DNS name: " + dnsName);
    }

    public static void unregister(String componentName) {
        dnsMap.remove(componentName);
        logger.info("<DNS>: Unregistered component: " + componentName);
    }

    public static String getIp(String componentName) {
        return dnsMap.get(componentName);
    }

    private static String generateDnsName(String componentName) {
        if (!dnsMap.containsKey(componentName)) {
            return componentName + "-global." + DNS_DOMAIN;
        }
        logger.error("<DNS>: Component name already exists: " + componentName);
        throw new IllegalArgumentException("Component name already exists");
    }

    @Override
    public void onEvent(AWSEvent event) {
        if (event.getName().equals("ObjectCreationEvent")) {
            if (allowedComponents.contains(event.getSource())) {
                register(event.getSourceObject().getName());
            }
        }
    }
}
