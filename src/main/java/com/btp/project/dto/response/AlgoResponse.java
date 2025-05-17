package com.btp.project.dto.response;

import java.util.List;
import java.util.Map;

public class AlgoResponse {
    private Double pathCost;
    private List<Integer> path;
    private Map<Integer, Integer> rechargeAmount;

    // Constructors
    public AlgoResponse() {}

    public AlgoResponse(Double pathCost, List<Integer> path, Map<Integer, Integer> rechargeAmount) {
        this.pathCost = pathCost;
        this.path = path;
        this.rechargeAmount = rechargeAmount;
    }

    // Getters and Setters
    public Double getPathCost() {
        return pathCost;
    }

    public void setPathCost(Double pathCost) {
        this.pathCost = pathCost;
    }

    public List<Integer> getPath() {
        return path;
    }

    public void setPath(List<Integer> path) {
        this.path = path;
    }

    public Map<Integer, Integer> getRechargeAmount() {
        return rechargeAmount;
    }

    public void setRechargeAmount(Map<Integer, Integer> rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }
}
