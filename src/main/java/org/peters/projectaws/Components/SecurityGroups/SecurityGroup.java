package org.peters.projectaws.Components.SecurityGroups;

import java.util.ArrayList;
import java.util.List;

import org.peters.projectaws.Components.SecurityGroups.SecurityRules.InboundRule;
import org.peters.projectaws.Components.SecurityGroups.SecurityRules.OutboundRule;
import org.peters.projectaws.Components.SecurityGroups.SecurityRules.SecurityProtocols;
import org.peters.projectaws.Core.AWSObject;

public class SecurityGroup extends AWSObject {
    List<InboundRule> inboundRules;
    List<OutboundRule> outboundRules;

    public SecurityGroup() {
        this.inboundRules = new ArrayList<>();
        this.outboundRules = new ArrayList<>();
    }

    public void addInboundRule(InboundRule rule) {
        if(rule == null) throw new IllegalArgumentException("InboundRule cannot be null");
        this.inboundRules.stream().filter(r -> r.getPort() == rule.getPort() && r.getProtocol() == rule.getProtocol()).findAny().ifPresent(r -> {
            return;
        });
        this.inboundRules.add(rule);
    }

    public void addOutboundRule(OutboundRule rule) {
        if(rule == null) throw new IllegalArgumentException("OutboundRule cannot be null");
        this.outboundRules.stream().filter(r -> r.getPort() == rule.getPort() && r.getProtocol() == rule.getProtocol()).findAny().ifPresent(r -> {
            return;
        });
        this.outboundRules.add(rule);
    }

    public List<InboundRule> getInboundRules() {
        return this.inboundRules;
    }

    public List<OutboundRule> getOutboundRules() {
        return this.outboundRules;
    }

    public boolean isInboundPortAndProtocolAllowed(int port, SecurityProtocols protocol) {
        if(port < 0 || port > 65535) throw new IllegalArgumentException("Port must be between 0 and 65535");
        if( protocol == null) throw new IllegalArgumentException("Protocol cannot be null");
        if (inboundRules.size() == 0) return false;
        return this.inboundRules.stream().anyMatch(r -> r.getPort() == port && r.getProtocol() == protocol.toString())
        ||  this.outboundRules.stream().anyMatch(r -> r.getPort() == port && r.getProtocol() == protocol.toString());
    }

    public boolean isOutboundPortAndProtocolAllowed(int port, SecurityProtocols protocol) {
        if(port < 0 || port > 65535) throw new IllegalArgumentException("Port must be between 0 and 65535");
        if( protocol == null) throw new IllegalArgumentException("Protocol cannot be null");
        if (outboundRules.size() == 0) return true;
        return this.outboundRules.stream().anyMatch(r -> r.getPort() == port && r.getProtocol() == protocol.toString());
    }
}

