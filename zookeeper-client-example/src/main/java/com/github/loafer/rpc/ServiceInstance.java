package com.github.loafer.rpc;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author zhaojh.
 */
public class ServiceInstance {
    private String name;
    private Class classType;
    private String description;
    private Object instance;
    private String address;
    private int port;

    public ServiceInstance(String name, Object instance) {
        this.name = name;
        this.instance = instance;
    }

    public String getName() {
        return name;
    }

    public Class getClassType() {
        return classType;
    }

    public void setClassType(Class classType) {
        this.classType = classType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonIgnore
    public Object getInstance() {
        return instance;
    }
}
