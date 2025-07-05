package org.peters.projectaws.Components.LoadBalancer.TargetGroup;

import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.peters.projectaws.Builders.EC2Builder;
import org.peters.projectaws.Components.EC2.EC2;
import org.peters.projectaws.Components.Monitors.TargetMonitor;
import org.peters.projectaws.dtos.Request.Request;
import org.peters.projectaws.dtos.Response.Response;
import org.peters.projectaws.enums.TargetState;

//Interacts with the Target Monitor Class of the EC2 Instance
public class EC2TargetGroup extends TargetGroup<EC2> {

    private static final Logger logger = LogManager.getLogger(EC2TargetGroup.class);
    
    public EC2TargetGroup(String path) {
        super(path);
        targetsList = new ArrayList<>();
        logger.info("EC2TargetGroup created with path: " + path + " and Id: " + this.getId());
    }

    @Override
    public Response receiveFromLoadBalancer(Request request) throws InterruptedException {
        logger.info("TargetGroup " + this.getPath() + " received request: " + request.getPath() + " "
                + request.getMethod() + " " + request.getData());
        
        EC2 target = getAvailableInstance();

        if (target == null) {
            logger.info("TargetGroup " + this.getPath() + " couldn't find a target");
            return null;
        }

        logger.info("TargetGroup " + this.getPath() + " found target: " + target.getId());

        return processRequest(target, request);
    }

    private int currentTargetIndex = 0;

    @Override
    public synchronized EC2 getAvailableInstance() {
        if (targetsList.isEmpty()) {
            return null;
        }

        // Find the next healthy target in a round-robin fashion
        do {
            currentTargetIndex = (currentTargetIndex + 1) % targetsList.size();
            EC2 target = targetsList.get(currentTargetIndex);
            
            if (target.targetMonitor.getState() == TargetState.HEALTHY) {
                try {
                    target.targetMonitor.addRunningRequest();
                    return target;
                } catch (InterruptedException e) {
                    continue; // Try next target if this one is overloaded
                }
            }
        } while (currentTargetIndex != 0); // Loop until we've tried all targetsList

        return null; // No healthy targetsList available
    }

    @Override
    public void addTarget(EC2 target) {
        if (target instanceof EC2) {
            target.addObserver(this);
            targetsList.add(target);
            logger.info("TargetGroup " + this.getPath() + " added target: " + target.getId());
        } else
            logger.error("Invalid Instance Type");
    }

    @Override
    public void onTargetStateChanged(TargetMonitor target, TargetState newState) {
        logger.info("Target " + target.getId() + " state changed to " + newState);
        if (newState == TargetState.UNHEALTHY) {
            targetsList.remove(target);
            addTarget(createHealthyTarget(target.getMaxConnections()));
            logger.info("Target " + target.getId() + " removed from target group");
        }   
    }

    @Override
    public void onRunningRequestsChanged(TargetMonitor target) {
        logger.info("Target " + target.getId() + " running requests changed to " + target.getRunningRequests());
    }

    private EC2 createHealthyTarget(int maxConn) {
        EC2Builder builder = new EC2Builder();
        EC2 target = builder.createEc2(maxConn);
        if (target.targetMonitor.getState() == TargetState.HEALTHY) {
            return target;
        } else {
            return createHealthyTarget(maxConn);
        }
    }

    private Response processRequest(EC2 target, Request request) {
        try {
            logger.info("Target " + target.getId() + " added running request");
            Response response = target.executeApi(request);
            return response;

        } catch (Exception e) {
            logger.info("Target " + target.getId() + " removed running request");
            throw e;
        } finally {
            target.targetMonitor.removeRunningRequest();
            logger.info("Target " + target.getId() + " removed running request");
        }
    }

   

}
