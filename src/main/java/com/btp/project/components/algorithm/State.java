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

    public State(int vertex, int energyCost, int pathEnergy, int fuel, State predecessor) {
        this.vertex = vertex;
        this.energyCost = energyCost;
        this.pathEnergy = pathEnergy;
        this.fuel = fuel;
        this.predecessor = predecessor;
    }
}
