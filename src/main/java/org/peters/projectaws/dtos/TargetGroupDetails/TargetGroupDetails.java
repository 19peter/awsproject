package org.peters.projectaws.dtos.TargetGroupDetails;

import java.util.concurrent.atomic.AtomicInteger;

import org.peters.projectaws.enums.TargetState;

public class TargetGroupDetails {
    AtomicInteger currentTargets = new AtomicInteger(0);
    AtomicInteger healthyTargets = new AtomicInteger(0);
    AtomicInteger unhealthyTargets = new AtomicInteger(0);
    AtomicInteger overLoadedTargets = new AtomicInteger(0);
    AtomicInteger offlineTargets = new AtomicInteger(0);
    AtomicInteger idleTargets = new AtomicInteger(0);


    public synchronized void addCurrentTarget(TargetState state) {
        currentTargets.incrementAndGet();
        switch (state) {
            case HEALTHY:
                healthyTargets.incrementAndGet();
                break;
            case UNHEALTHY:
                unhealthyTargets.incrementAndGet();
                break;
            case OVERLOADED:
                overLoadedTargets.incrementAndGet();
                break;
            case OFFLINE:
                offlineTargets.incrementAndGet();
                break;
            case IDLE:
                idleTargets.incrementAndGet();
                break;
            default:
                break;
        }
    }

    public synchronized void removeCurrentTarget(TargetState state) {
        currentTargets.decrementAndGet();
        switch (state) {
            case HEALTHY:
                healthyTargets.decrementAndGet();
                break;
            case UNHEALTHY:
                unhealthyTargets.decrementAndGet();
                break;
            case OVERLOADED:
                overLoadedTargets.decrementAndGet();
                break;
            case OFFLINE:
                offlineTargets.decrementAndGet();
                break;
            case IDLE:
                idleTargets.decrementAndGet();
                break;
            default:
                break;
        }
    }

    public synchronized void updateByState(TargetState oldState, TargetState newState) {
        switch (newState) {
            case HEALTHY:
                healthyTargets.incrementAndGet();
                break;
            case UNHEALTHY:
                unhealthyTargets.incrementAndGet();
                break;
            case OVERLOADED:
                overLoadedTargets.incrementAndGet();
                break;
            case OFFLINE:
                offlineTargets.incrementAndGet();
                break;
            case IDLE:
                idleTargets.incrementAndGet();
                break;
            default:
                break;
        }

        switch (oldState) {
            case HEALTHY:
                healthyTargets.decrementAndGet();
                break;
            case UNHEALTHY:
                unhealthyTargets.decrementAndGet();
                break;
            case OVERLOADED:
                overLoadedTargets.decrementAndGet();
                break;
            case OFFLINE:
                offlineTargets.decrementAndGet();
                break;
            case IDLE:
                idleTargets.decrementAndGet();
                break;
            default:
                break;
        }
    }

    public synchronized int getCurrentTargets() {
        return currentTargets.get();
    }

    public synchronized int getHealthyTargets() {
        return healthyTargets.get();
    }

    public synchronized int getUnhealthyTargets() {
        return unhealthyTargets.get();
    }

    public synchronized int getOverLoadedTargets() {
        return overLoadedTargets.get();
    }

    public synchronized int getOfflineTargets() {
        return offlineTargets.get();
    }

    public synchronized int getIdleTargets() {
        return idleTargets.get();
    }
}
