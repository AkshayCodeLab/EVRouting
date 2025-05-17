package com.btp.project.components.algorithm;

import java.util.BitSet;
import java.util.List;
import java.util.Set;

// State for tracking: (vertex, distance, fuel, visited, path)
public class State {
     int vertex;
     double energyCost;
     double pathEnergy;  // Raw energy of the path (sum of edge energies)
     int fuel;
     State predecessor;
     boolean hasChargedHere;
     BitSet visited;
     int rechargeAmount;

    public State(int vertex, double energyCost, double pathEnergy, int fuel, State predecessor, boolean hasChargedHere) {
        this(vertex, energyCost, pathEnergy, fuel, predecessor, hasChargedHere, 0);
    }

    public State(int vertex, double energyCost, double pathEnergy, int fuel, State predecessor, boolean hasChargedHere, int rechargeAmount){
        this.vertex = vertex;
        this.energyCost = energyCost;
        this.pathEnergy = pathEnergy;
        this.fuel = fuel;
        this.predecessor = predecessor;
        this.hasChargedHere = hasChargedHere;
        this.rechargeAmount = rechargeAmount;
        this.visited = new BitSet();
        if (predecessor != null) {
            this.visited = (BitSet) predecessor.visited.clone();
        }
        this.visited.set(vertex);
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
                ", rechargeAmount=" + rechargeAmount +
                '}';
    }
}
