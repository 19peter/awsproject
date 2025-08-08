package org.peters.projectaws.Components.DnsRegisterar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.peters.projectaws.Core.AWSEvent;
import org.peters.projectaws.Interfaces.EventBridgeListener.EventBridgeListener;
import org.peters.projectaws.Interfaces.RequestServer.RequestServer;
import org.peters.projectaws.enums.EventBridgeDefaultEvents;
import org.peters.projectaws.dtos.Request.Request;
import org.peters.projectaws.dtos.Response.Response;
import org.peters.projectaws.Core.AWSObject;

public class DnsRegisterar implements EventBridgeListener {
    private static Map<String, AWSObject> dnsMap = new HashMap<>();
    private static final String DNS_DOMAIN = "awsproject.dns";
    private static final Logger logger = LogManager.getLogger(DnsRegisterar.class);
    private List<String> allowedComponents = new ArrayList<>(List.of("EC2",
            "Lambda",
            "S3",
            "LoadBalancer",
            "ApiGateway"));
    
    private static class InstanceHolder{
        private static final DnsRegisterar instance = new DnsRegisterar();
    }

    public static DnsRegisterar getInstance() {
        return InstanceHolder.instance;
    }

    private static void register(String componentName, AWSObject component) {
        String dnsName = generateDnsName(componentName);
        dnsMap.put(dnsName, component);
        logger.info("<DNS>: Registered component: " + componentName + " with DNS name: " + dnsName);
    }

    private static void unregister(String dnsName) {
        dnsMap.remove(dnsName);
        logger.info("<DNS>: Unregistered component: " + dnsName);
    }

    private static String generateDnsName(String componentName) {
        if (!dnsMap.containsKey(componentName)) {
            return componentName + "-global." + DNS_DOMAIN;
        }
        logger.error("<DNS>: Component name already exists: " + componentName);
        throw new IllegalArgumentException("Component name already exists");
    }

    public static String getObjDnsName(AWSObject obj) {
        return generateDnsName(obj.getName());
    }

    public static Response serve(String dnsName, Request request) {
        if (!dnsMap.containsKey(dnsName)) {
            logger.error("<DNS>: DNS name not found: " + dnsName);
            throw new IllegalArgumentException("DNS name not found");
        }
        logger.info("<DNS>: Serving request: " + request.getPath() + " to " + dnsName);
        return ((RequestServer)dnsMap.get(dnsName)).serve(request);
    }

    @Override
    public void onEvent(AWSEvent event) {
        if (event.getName().equals(EventBridgeDefaultEvents.ObjectCreationEvent.name())) {
            logger.info("<DNS>: Received event: " + event.getName() + " from " + event.getSource());
            if (allowedComponents.contains(event.getSource())) {
                register(event.getSourceObject().getName(), event.getSourceObject());
            }
        }
    }
}
