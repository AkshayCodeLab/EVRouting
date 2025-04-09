package com.btp.project.components.algorithm;

import java.util.List;
import java.util.Set;

// State for tracking: (vertex, distance, fuel, visited, path)
public class State {
     int vertex;
     int energyCost;
     int pathEnergy;  // Raw energy of the path (sum of edge energies)
     int fuel;
     State predecessor;
     boolean hasChargedHere;

    public State(int vertex, int energyCost, int pathEnergy, int fuel, State predecessor, boolean hasChargedHere) {
        this.vertex = vertex;
        this.energyCost = energyCost;
        this.pathEnergy = pathEnergy;
        this.fuel = fuel;
        this.predecessor = predecessor;
        this.hasChargedHere = hasChargedHere;
    }

    @Override
    public String toString() {
        return "State{" +
                "vertex=" + vertex +
                ", energyCost=" + energyCost +
                ", pathEnergy=" + pathEnergy +
                ", fuel=" + fuel +
                ", predecessor=" + (predecessor != null ? predecessor.vertex : "null") +
                ", hasChargedHere=" + hasChargedHere +
                '}';
    }
}
