package org.peters.projectaws;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.peters.projectaws.Scenarios.Test_Gateway_LB_TG_EC2;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {
        Test_Gateway_LB_TG_EC2.test();

    }

}