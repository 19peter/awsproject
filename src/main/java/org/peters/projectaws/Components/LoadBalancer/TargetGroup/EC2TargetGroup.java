package org.peters.projectaws.Components.LoadBalancer.TargetGroup;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.peters.projectaws.Components.EC2.EC2;
import org.peters.projectaws.Components.LoadBalancer.TargetGroup.Common.TargetGroup;
import org.peters.projectaws.dtos.Request.Request;
import org.peters.projectaws.dtos.Response.Response;
import org.peters.projectaws.enums.TargetState;
import java.util.concurrent.CopyOnWriteArrayList;

//Interacts with the Target Monitor Class of the EC2 Instance
public class EC2TargetGroup extends TargetGroup<EC2> {

    private static final Logger logger = LogManager.getLogger(EC2TargetGroup.class);
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 100;
    private int currentTargetIndex = 0;

    public EC2TargetGroup(String path) {
        super(path);
        targetsList = new CopyOnWriteArrayList<>();
        logger.info("<EC2TargetGroup>: EC2TargetGroup created with path: " + path + " and Id: " + this.getId());
    }

    @Override
    public Response receiveFromLoadBalancer(Request request) throws InterruptedException {
        logger.info("<EC2TargetGroup>: TargetGroup " + this.getPath() + " received request: " + request.getPath() + " "
                + request.getMethod() + " " + request.getData());

        Optional<EC2> target = getAvailableInstance();

        if (target.isEmpty()) {
            logger.info("<EC2TargetGroup>: TargetGroup " + this.getPath() + " couldn't find a target");
            return null;
        }

        return processRequest(target.get(), request);
    }

    @Override
    public synchronized Optional<EC2> getAvailableInstance() {
        if (targetsList.isEmpty()) {
            logger.debug("<EC2TargetGroup>: TargetGroup {} is empty", getPath());
            return Optional.empty();
        }

        int attempts = 0;
        int startIndex = currentTargetIndex;
        int healthyTargets = 0;

        do {
            currentTargetIndex = (currentTargetIndex + 1) % targetsList.size();
            EC2 target = targetsList.get(currentTargetIndex);

            // Skip if target is not healthy
            if (target.targetMonitor.getState() != TargetState.HEALTHY) {
                continue;
            }

            healthyTargets++;

            // Try to get a connection slot
            try {
                if (target.targetMonitor.addRunningRequest()) {
                    logger.info("<EC2TargetGroup>: TargetGroup " + this.getPath() + " found target: " + target.getName());
                    return Optional.of(target);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("<EC2TargetGroup>: Interrupted while waiting for available target");
                return Optional.empty();
            }

            // If we've checked all targets and found some healthy but busy ones, wait and
            // retry
            if (currentTargetIndex == startIndex && healthyTargets > 0) {
                if (++attempts >= MAX_RETRIES) {
                    logger.warn("<EC2TargetGroup>: All healthy targets are busy after {} attempts", MAX_RETRIES);
                    return Optional.empty();
                }
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return Optional.empty();
                }
            }
        } while (currentTargetIndex != startIndex || healthyTargets == 0);

        logger.info("<EC2TargetGroup>: No healthy targets available in target group {}", getPath());
        return Optional.empty();
    }

    @Override
    public void addTarget(EC2 ec2) {
        if (!(ec2 instanceof EC2)) {
            logger.error("<EC2TargetGroup>: Invalid Instance Type");
            return;
        }
        if (!ec2.addObserver(this))
            return;
        targetsList.add(ec2);
        targetGroupDetails.addCurrentTarget(TargetState.HEALTHY);
        logger.info("<EC2TargetGroup>: TargetGroup " + this.getPath() + " added target: " + ec2.getName());
    }

    @Override
    public void removeTarget(EC2 ec2) {
        if (!(ec2 instanceof EC2)) {
            logger.error("<EC2TargetGroup>: Invalid Instance Type");
            return;
        }
        if (!ec2.removeObserver(this))
            return;
        targetsList.remove(ec2);
        targetGroupDetails.removeCurrentTarget(ec2.targetMonitor.getState());
        logger.info("<EC2TargetGroup>: TargetGroup " + this.getPath() + " removed target: " + ec2.getName());
    }

    @Override
    public void onTargetStateChanged(EC2 ec2, TargetState oldState, TargetState newState) {
        logger.info("<EC2TargetGroup>: Selected target {} for request", ec2.getName() + " with new state " + newState);
        if (newState == TargetState.UNHEALTHY || newState == TargetState.OFFLINE) {
            handleUnavailableTarget(ec2, oldState, newState);
        }
    }

    @Override
    public void onRunningRequestsChanged(EC2 ec2, int runningRequests) {
        logger.info("<EC2TargetGroup>: Target Monitor " + ec2.targetMonitor.getName() + " running requests changed to "
                + ec2.targetMonitor.getRunningRequests());
    }

    private Response processRequest(EC2 target, Request request) {
        try {
            Response response = target.executeApi(request);
            return response;
        } catch (Exception e) {
            logger.error(
                    "<EC2TargetGroup>: Exception while processing request " + request.getPath() + " " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            target.targetMonitor.removeRunningRequest();
        }
    }

    protected void handleUnavailableTarget(EC2 ec2, TargetState oldState, TargetState newState) {
        targetsList.remove(ec2);
        targetGroupDetails.updateByState(oldState, newState);
        logger.info("<EC2TargetGroup>: Target Monitor " + ec2.targetMonitor.getName() + " removed from target group");
    }

}
