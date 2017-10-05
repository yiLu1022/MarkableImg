package com.netatmo.ylu.markableimage.beans;


public class BasicTag {
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(final int instanceId) {
        this.instanceId = instanceId;
    }

    private String name;
    private int instanceId;

}
