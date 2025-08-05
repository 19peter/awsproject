package org.peters.projectaws.Components.Route53;

import java.util.HashMap;
import java.util.Map;

import org.peters.projectaws.Core.AWSObject;

public class Route53 extends AWSObject {
    private Map<String, String> DNS;
    
    private Route53() {
        super("Route53");
        DNS = new HashMap<>();
    }

    private static class SingletonHolder{
        private static final Route53 INSTANCE = new Route53();
    }

    public static Route53 getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public boolean addDNS(String name, String ip) {
        if (DNS.containsKey(name)) return false;
        DNS.put(name, ip);
        return true;
    }
}
