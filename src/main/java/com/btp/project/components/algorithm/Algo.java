package com.btp.project.components.algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import com.btp.project.components.graph.model.Graph;
import com.btp.project.components.graph.model.Pair;

public class Algo {
    public static Pair<Integer, List<Integer>> shortestPathWithFuel(int from, int to, Graph graph,
            int initialFuel) {
        List<List<Pair<Integer, Integer>>> adj = graph.getAdjacencyList();

        // Comparator to prioritize by distance
        Comparator<State> stateComparator = Comparator.comparingInt((State s) -> s.distance)
                .thenComparingInt((State s) -> -s.fuel);

        // Priority queue for Dijkstra's algorithm with fuel state
        PriorityQueue<State> pq = new PriorityQueue<>(stateComparator);

        // Initial visited set and path
        Set<Integer> initialVisited = new HashSet<>();
        initialVisited.add(from);
        List<Integer> initialPath = new ArrayList<>();
        initialPath.add(from);

        // Start with initial state
        pq.offer(new State(from, 0, initialFuel, initialVisited, false, initialPath));

        // Track best path
        int bestDistance = Integer.MAX_VALUE;
        List<Integer> bestPath = new ArrayList<>();

        while (!pq.isEmpty()) {
            State current = pq.poll();
            int u = current.vertex;
            int currentDist = current.distance;
            int currentFuel = current.fuel;
            Set<Integer> currentVisited = current.visited;
            boolean hasRefueled = current.refueled;
            List<Integer> currentPath = current.path;

            // Reached target
            if (u == to) {
                if (currentDist < bestDistance) {
                    bestDistance = currentDist;
                    bestPath = new ArrayList<>(currentPath);
                }
                continue;
            }

            // Explore neighbors
            for (Pair<Integer, Integer> neighborEdge : adj.get(u)) {
                int v = neighborEdge.getFirst();
                int edgeWeight = neighborEdge.getSecond();

                // Skip if vertex already visited
                if (currentVisited.contains(v))
                    continue;

                // Prepare new visited set and path
                Set<Integer> newVisited = new HashSet<>(currentVisited);
                newVisited.add(v);
                List<Integer> newPath = new ArrayList<>(currentPath);
                newPath.add(v);

                // Option 1: Move without refueling
                if (currentFuel >= edgeWeight) {
                    int newFuel = currentFuel - edgeWeight;
                    int newDist = currentDist + edgeWeight;

                    pq.offer(new State(v, newDist, newFuel, newVisited, hasRefueled, newPath));
                }

                // Option 2: Refuel at current node (if not already refueled)
                if (!hasRefueled && u != from) {
                    for (int refuelAmount = 1; refuelAmount <= (10 - currentFuel); refuelAmount++) {
                        int newFuel = Math.min(10, currentFuel + refuelAmount);

                        // Check if can move after refueling
                        if (newFuel >= edgeWeight) {
                            int newDist = currentDist + refuelAmount + edgeWeight;

                            pq.offer(new State(v, newDist, newFuel - edgeWeight, newVisited, true,
                                    newPath));
                        }
                    }
                }
            }
        }

        // If target is unreachable, return MAX_VALUE for distance and empty path
        return bestDistance == Integer.MAX_VALUE ? new Pair<>(Integer.MAX_VALUE, new ArrayList<>())
                : new Pair<>(bestDistance, bestPath);
    }

}
