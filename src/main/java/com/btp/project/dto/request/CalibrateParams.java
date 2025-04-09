package com.btp.project.dto.request;

public class CalibrateParams {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Calibrate Params received: " + name + "\n";
    }
}
