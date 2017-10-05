package com.netatmo.ylu.markableimage.beans;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class MarkableImg {


    private final String path;
    private String name;
    private Set<BasicTag> tags;

    public MarkableImg(final String path) {
        this.path = path;
        tags = new HashSet<>();
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Set<BasicTag> getTags() {
        return tags;
    }

    public void addTags(final BasicTag tag) {
        tags.add(tag);
    }

    public void removeTag(final BasicTag tag){
        tags.remove(tag);
    }


}
