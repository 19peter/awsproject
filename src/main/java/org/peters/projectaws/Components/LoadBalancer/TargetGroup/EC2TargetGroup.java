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
    private int MAX_RETRIES = 3;
    private int RETRY_DELAY_MS = 100;
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
        EC2 firstHealthyTarget = null;

        do {
            currentTargetIndex = (currentTargetIndex + 1) % targetsList.size();
            EC2 target = targetsList.get(currentTargetIndex);
            TargetState state = target.getTargetMonitorState();

            // Skip if target is not healthy or idle
            if (state != TargetState.HEALTHY && state != TargetState.IDLE) {
                continue;
            }

            // If we find an idle target, try to use it immediately
            if (state == TargetState.IDLE) {
                logger.info("<EC2TargetGroup>: TargetGroup {} found idle target: {}",
                        getPath(), target.getName());
                return Optional.of(target);

            }

            // Remember the first healthy target we find (in case we don't find any idle
            // ones)
            if (firstHealthyTarget == null && state == TargetState.HEALTHY) {
                firstHealthyTarget = target;
            }

            // If we've completed a full cycle, try to use the first healthy target we found
            if (currentTargetIndex == startIndex) {
                if (firstHealthyTarget != null) {

                    logger.info("<EC2TargetGroup>: TargetGroup {} found healthy target: {}",
                            getPath(), firstHealthyTarget.getName());
                    return Optional.of(firstHealthyTarget);

                }

                // If we couldn't get a connection, wait and retry
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
        } while (true);
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
        targetGroupDetails.removeCurrentTarget(ec2.getTargetMonitorState());
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
        logger.info("<EC2TargetGroup>: Target Monitor " + ec2.getTargetMonitorName() + " running requests changed to "
                + ec2.getRunningRequests());
    }

    private Response processRequest(EC2 target, Request request) {
        try {
            Response response = target.executeApi(request);
            return response;
        } catch (Exception e) {
            logger.error(
                    "<EC2TargetGroup>: Exception while processing request " + request.getPath() + " " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    protected void handleUnavailableTarget(EC2 ec2, TargetState oldState, TargetState newState) {
        targetsList.remove(ec2);
        targetGroupDetails.updateByState(oldState, newState);
        logger.info("<EC2TargetGroup>: Target Monitor " + ec2.getTargetMonitorName() + " removed from target group");
    }

    protected void setMaxRetriesAndMaxDelay(int maxRetries, int maxDelay) {
        if (maxRetries < 0 || maxDelay < 0) {
            logger.error("<EC2TargetGroup>: Invalid max retries or max delay");
            return;
        }

        if (maxRetries > 6 || maxDelay > 15000) {
            logger.warn("<EC2TargetGroup>: Max retries must be less than 6 and max delay must not exceed 15 seconds");
            return;
        }

        MAX_RETRIES = maxRetries;
        RETRY_DELAY_MS = maxDelay;
    }

}
