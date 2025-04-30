package com.btp.project.components.graph.utils;

import com.btp.project.components.graph.model.Graph;
import com.btp.project.components.graph.model.Pair;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class Normalizer {
    private final double maxFuel;
    private final double maxRefuel;
    private final double maxDistanceFromThreshold;
    private final double maxDetour;

    public Normalizer(Graph graph, int from, int to, int capacity, int[] shortestPathsFromVToTo, int shortestPathStartToTo) {
        this.maxFuel = computeMaxFuel(graph);
        this.maxRefuel = capacity;
        this.maxDistanceFromThreshold = 0.2 * capacity;
        this.maxDetour = computeMaxDetour(graph, from, shortestPathsFromVToTo, shortestPathStartToTo);
    }

    private double computeMaxFuel(Graph graph) {
        double max = 0;
        for (List<Pair<Integer, Integer>> edges : graph.getAdjacencyList()) {
            for (Pair<Integer, Integer> edge : edges) {
                max = Math.max(max, edge.getSecond());
            }
        }
        return max;
    }

    private double computeMaxDetour(Graph graph, int from, int[] shortestPathsFromVToTo, int shortestPathStartToTo) {
        double maxDetour = 0;
        int n = graph.getAdjacencyList().size();
        int[] shortestPathsFromStart = computeShortestPathFromStart(from, n, graph.getAdjacencyList());

        for (int u = 0; u < n; u++) {
            for (Pair<Integer, Integer> edge : graph.getAdjacencyList().get(u)) {
                int v = edge.getFirst();
                int e = edge.getSecond();
                int pathEnergyToU = shortestPathsFromStart[u];
                int remainingPath = shortestPathsFromVToTo[v];
                int detourExcess = Math.max(0, (pathEnergyToU + e + remainingPath) - shortestPathStartToTo);
                maxDetour = Math.max(maxDetour, detourExcess);
            }
        }
        return maxDetour;
    }

    private int[] computeShortestPathFromStart(int start, int n, List<List<Pair<Integer, Integer>>> adj) {
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[start] = 0;

        PriorityQueue<Pair<Integer, Integer>> pq = new PriorityQueue<>(Comparator.comparingInt(Pair::getSecond));
        pq.offer(new Pair<>(start, 0));

        while (!pq.isEmpty()) {
            Pair<Integer, Integer> current = pq.poll();
            int u = current.getFirst();
            int d = current.getSecond();
            if (d > dist[u]) continue;
            for (Pair<Integer, Integer> edge : adj.get(u)) {
                int v = edge.getFirst();
                int w = edge.getSecond();
                if (dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    pq.offer(new Pair<>(v, dist[v]));
                }
            }
        }
        return dist;
    }

    // Getters
    public double getMaxFuel() { return maxFuel; }
    public double getMaxRefuel() { return maxRefuel; }
    public double getMaxDistanceFromThreshold() { return maxDistanceFromThreshold; }
    public double getMaxDetour() { return maxDetour; }
}
