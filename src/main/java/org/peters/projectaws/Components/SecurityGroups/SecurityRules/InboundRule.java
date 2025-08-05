package org.peters.projectaws.Components.SecurityGroups.SecurityRules;

public class InboundRule {
    int port;
    String protocol;


    public InboundRule(int port, String protocol) {
        if(port < 0 || port > 65535) throw new IllegalArgumentException("Port must be between 0 and 65535");
        if(!protocol.equals(SecurityProtocols.TCP.toString()) 
        && !protocol.equals(SecurityProtocols.UDP.toString())
        && !protocol.equals(SecurityProtocols.ALL.toString())

        ) throw new IllegalArgumentException("Protocol must be tcp or udp or http or https");
        this.port = port;
        this.protocol = protocol;
    }

    public int getPort() {
        return port;
    }

    public String getProtocol() {
        return protocol;
    }

}

