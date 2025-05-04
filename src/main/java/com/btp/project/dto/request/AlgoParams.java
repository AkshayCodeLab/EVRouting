package com.btp.project.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AlgoParams {

    @JsonProperty("to")
    private int to;

    @JsonProperty("from")
    private int from;

    @JsonProperty("fuel")
    private int fuel;

    @JsonProperty("matrix")
    private List<List<Double>> matrix;

    public int getTo() {
        return to;
    }

    public int getFrom() {
        return from;
    }

    public int getFuel() {
        return fuel;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public List<List<Double>> getMatrix() {
        return matrix;
    }

    public void setMatrix(List<List<Double>> matrix) {
        this.matrix = matrix;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public void setFuel(int fuel) {
        this.fuel = fuel;
    }
}
