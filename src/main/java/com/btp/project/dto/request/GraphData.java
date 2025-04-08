package com.btp.project.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Set;

public class GraphData {

    @JsonProperty("vertices")
    private int n;

    @JsonProperty("edges")
    private List<List<Integer>> edges;

    @JsonProperty("chargingStations")
    private Set<Integer> chargingStations;

    public GraphData() {}

    public Set<Integer> getChargingStations() {
        return chargingStations;
    }

    public void setChargingStations(Set<Integer> chargingStations) {
        this.chargingStations = chargingStations;
    }

    public GraphData(int n, List<List<Integer>> edges, Set<Integer> chargingStations) {
        this.n = n;
        this.edges = edges;
        this.chargingStations = chargingStations;
    }

    public List<List<Integer>> getEdges() {
        return edges;
    }

    public int getN() {
        return n;
    }


    public void setEdges(List<List<Integer>> edges) {
        this.edges = edges;
    }

    public void setN(int n) {
        this.n = n;
    }

    @Override
    public String toString() {
        return "Vertices: " + n + "\nEdges: " + edges + "\nCharging Stations: " + chargingStations;
    }

}
