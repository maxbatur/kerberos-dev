package com.krb.restwithkerberos.server.controller;

import java.io.Serializable;

public class Foo implements Serializable {

    private String name;
    private String message;
    private String principalName;

    public Foo(String name, String message, String principalName) {
        this.name = name;
        this.message = message;
        this.principalName = principalName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }
}
