package com.btp.project.requestBody;

public class CaliberateParams {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Caliberate Params recieved: " + name + "\n";
    }
}
