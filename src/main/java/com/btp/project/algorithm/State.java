package com.btp.project.algorithm;

import java.util.List;
import java.util.Set;

// State for tracking: (vertex, distance, fuel, visited, path)
public class State {
    public int vertex;
    public int distance;
    public int fuel;
    public Set<Integer> visited;
    public boolean refueled;
    public List<Integer> path;

    State(int vertex, int distance, int fuel, Set<Integer> visited, boolean refueled,
            List<Integer> path) {
        this.vertex = vertex;
        this.distance = distance;
        this.fuel = fuel;
        this.visited = visited;
        this.refueled = refueled;
        this.path = path;
    }
}
