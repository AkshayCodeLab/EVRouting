package com.btp.project.components.algorithm;

import java.util.*;

import com.btp.project.components.graph.model.Graph;
import com.btp.project.components.graph.model.Pair;

public class Algo {

    public static Pair<Integer, List<Integer>> shortestPathWithFuel(
            int from, int to, Graph graph, int initialFuel,
            int capacity, double thresholdPenalty, int detourPenaltyFactor, int refuelCostPerUnit
    ) {


        List<List<Pair<Integer, Integer>>> adj = graph.getAdjacencyList();
        int n = adj.size();

        // Precompute the shortest paths for detour penalty
        int[] shortestPathFromVToTo = computeShortestPathEnergy(to, adj, n);
        int shortestPathStartToTo = shortestPathFromVToTo[from];

        // Priority queue for Dijkstra's algorithm with fuel state
        PriorityQueue<State> pq = new PriorityQueue<>(Comparator
                .comparingInt((State s) -> s.energyCost)
                .thenComparingInt(s -> -s.fuel));

        int[][] dp = new int[n][capacity + 1];
        for (int[] row : dp) Arrays.fill(row, Integer.MAX_VALUE);
        dp[from][initialFuel] = 0;

        // Start with initial state
        pq.offer(new State(from, 0, 0, initialFuel, null, false));

        // Track best path
        State bestState = null;
        int bestEnergy = Integer.MAX_VALUE;

        int threshold = (int) (0.2 * capacity); // 20% threshold

        while (!pq.isEmpty()) {
            State cur = pq.poll();
            int u = cur.vertex;
            int currEnergyCost = cur.energyCost; // Total energy including penalties
            int currPathEnergy = cur.pathEnergy; // Raw Energy without penalties to find detour
            int currFuel = cur.fuel;
            boolean hasChargedHere = cur.hasChargedHere;

            // Reached target
            if (u == to && currEnergyCost < bestEnergy) {
                bestEnergy = currEnergyCost;
                bestState = cur;
                continue; // Keep processing for potential better paths
            }

            if (currEnergyCost > dp[u][currFuel]) continue;

            // Move to adjacent nodes without refuel
            for (Pair<Integer, Integer> edge : adj.get(u)) {
                int v = edge.getFirst();
                int energyConsumed = edge.getSecond();

                // Not sufficient fuel to reach without refueling
                if (currFuel < energyConsumed) continue;

                int newFuel = currFuel - energyConsumed;
                int newPathEnergy = currPathEnergy + energyConsumed;

                // Find detour penalty
                int estimatedTotalEnergy = newPathEnergy + shortestPathFromVToTo[v];
                int detourExcess = estimatedTotalEnergy - shortestPathStartToTo;
                if (detourExcess < 0) detourExcess = 0;

                int newEnergy = currEnergyCost + energyConsumed + (detourExcess * detourPenaltyFactor);

                // Apply threshold penalty
                if (newFuel < threshold) {
                    newEnergy += (int) ((threshold - newFuel) * thresholdPenalty);
                }

                if (newEnergy < dp[v][newFuel]) {
                    dp[v][newFuel] = newEnergy;
                    pq.offer(new State(v, newEnergy, newPathEnergy, newFuel, cur, false));
                }

            }

            // Charging logic
            if (graph.isChargingStation(u) && !hasChargedHere){

                int step = Math.max(1, capacity / 10);
                for (int charge = step; currFuel + charge <= capacity; charge += step) {
                    int newFuel = currFuel + charge;
                    int refuelCost = charge * refuelCostPerUnit; // Assuming refuel cost is 1 per unit
                    int newEnergy = currEnergyCost + refuelCost;

                    if (newEnergy < dp[u][newFuel]) {
                        dp[u][newFuel] = newEnergy;
                        pq.offer(new State(u, newEnergy, currPathEnergy, newFuel, cur, true));
                    }
                }
            }

        }

        // Reconstruct path
        List<Integer> path = new ArrayList<>();
        for (State s = bestState; s != null; s = s.predecessor)
            path.add(s.vertex);
        Collections.reverse(path);

        // If target is unreachable, return MAX_VALUE for distance and empty path
        return bestEnergy == Integer.MAX_VALUE ?
                new Pair<>(Integer.MAX_VALUE, Collections.emptyList()) :
                new Pair<>(bestEnergy, path);

    }

    public static int[] computeShortestPathEnergy(int to, List<List<Pair<Integer, Integer>>> adj, int n){

        // Reverse the adjacency list to compute paths TO 'to'
        List<List<Pair<Integer, Integer>>> reversedAdj = new ArrayList<>();
        for (int i = 0; i < n; i++) reversedAdj.add(new ArrayList<>());

        for (int u = 0; u < adj.size(); u++) {
            for (Pair<Integer, Integer> edge : adj.get(u)) {
                int v = edge.getFirst();
                int cost = edge.getSecond();
                reversedAdj.get(v).add(new Pair<>(u, cost)); // Reverse edge direction
            }
        }

        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[to] = 0;
        PriorityQueue<Pair<Integer, Integer>> pq = new PriorityQueue<>(Comparator.comparingInt(Pair::getSecond));
        pq.offer(new Pair<>(to, 0));

        while (!pq.isEmpty()) {
            Pair<Integer, Integer> cur = pq.poll();
            int u = cur.getFirst();
            int d = cur.getSecond();
            if (d > dist[u]) continue;
            for (Pair<Integer, Integer> edge : reversedAdj.get(u)) {
                int v = edge.getFirst();
                int newDist = d + edge.getSecond();
                if (newDist < dist[v]) {
                    dist[v] = newDist;
                    pq.offer(new Pair<>(v, newDist));
                }
            }
        }
        return dist;
    }

}
