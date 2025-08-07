package org.peters.projectaws;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URISyntaxException;

import org.peters.projectaws.Scenarios.Test_Gateway_LB_TG_EC2;
import org.peters.projectaws.Scenarios.Test_LB_Lambda_Benchmark;
import org.peters.projectaws.Components.ComponentRegistry.ComponentRegistry;
import org.peters.projectaws.Components.DnsRegisterar.DnsRegisterar;
import org.peters.projectaws.Components.EventBridge.EventBridge;
import org.peters.projectaws.Scenarios.Test_AutoScalingGroups;
import org.peters.projectaws.Scenarios.Test_EventBridge;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException, URISyntaxException {
        EventBridge.getInstance();
        DnsRegisterar.getInstance();
        ComponentRegistry.getInstance();
        // Test_Gateway_LB_TG_EC2.test();
        // Test_LB_Lambda_Benchmark.test();
        // Test_AutoScalingGroups.test();
        Test_EventBridge.test();

    }

}