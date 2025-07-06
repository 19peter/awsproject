package org.peters.projectaws.Core;

import java.util.Random;

public abstract class AWSObject {
    private long id;
    private String name;
    private boolean isRunning = false;

    public AWSObject() {
        this.id = new Random().nextLong();
    }

    public AWSObject(String name) {
        this.name = name;
        this.id = new Random().nextLong();
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }
}
