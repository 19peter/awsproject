package org.peters.projectaws.Core;

public abstract class AWSEvent<T extends AWSObject> {
    String name;
    T source;
    String state;

    public AWSEvent(String name, T source, String state) {
        this.name = name;
        this.source = source;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public T getSourceObject() {
        return source;
    }

    public String getSource() {
        return source.getClass().getSimpleName();
    }

    public String getState() {
        return state;
    }
}
