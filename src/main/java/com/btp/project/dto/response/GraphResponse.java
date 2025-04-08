package com.btp.project.dto.response;

import com.btp.project.components.graph.model.Pair;

import java.util.List;
import java.util.Set;

public class GraphResponse {
    private List<List<Pair<Integer, Integer>>> adjacencyList;
    private int vertices;
    private Set<Integer> chargingStations;

    public GraphResponse(List<List<Pair<Integer, Integer>>> adjacencyList, int vertices, Set<Integer> chargingStations) {
        this.adjacencyList = adjacencyList;
        this.vertices = vertices;
        this.chargingStations = chargingStations;
    }

    public List<List<Pair<Integer, Integer>>> getAdjacencyList() {
        return adjacencyList;
    }

    public void setAdjacencyList(List<List<Pair<Integer, Integer>>> adjacencyList) {
        this.adjacencyList = adjacencyList;
    }

    public int getVertices() {
        return vertices;
    }

    public void setVertices(int vertices) {
        this.vertices = vertices;
    }

    public Set<Integer> getChargingStations() {
        return chargingStations;
    }

    public void setChargingStations(Set<Integer> chargingStations) {
        this.chargingStations = chargingStations;
    }
}
