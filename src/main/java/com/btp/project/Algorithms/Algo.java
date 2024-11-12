package com.btp.project.Algorithms;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import com.btp.project.Components.utils.Graph;
import com.btp.project.Components.utils.Pair;

public class Algo {
        public static int shortestPath(int from, int to, Graph graph) {
        int n = graph.getVertices();
        List<List<Pair<Integer, Integer>>> adj = graph.getAdj();
        
        // Initialize distances array
        int[] distances = new int[n];
        Arrays.fill(distances, Integer.MAX_VALUE);
        distances[from] = 0;

        // Min-heap to select the vertex with the smallest distance
        PriorityQueue<Pair<Integer, Integer>> pq = new PriorityQueue<>(Comparator.comparingInt(Pair::getSecond));
        pq.add(new Pair<>(from, 0));

        while (!pq.isEmpty()) {
            Pair<Integer, Integer> current = pq.poll();
            int u = current.getFirst();
            int distU = current.getSecond();

            // If we reached the target node, return the distance
            if (u == to) {
                return distU;
            }

            // If the distance is outdated, skip it
            if (distU > distances[u]) {
                continue;
            }

            // Explore neighbors
            for (Pair<Integer, Integer> neighbor : adj.get(u)) {
                int v = neighbor.getFirst();
                int weight = neighbor.getSecond();
                int newDist = distances[u] + weight;

                // If a shorter path is found, update and push to the queue
                if (newDist < distances[v]) {
                    distances[v] = newDist;
                    pq.add(new Pair<>(v, newDist));
                }
            }
        }

        // If the target node is unreachable, return -1 or any other indication
        return distances[to] == Integer.MAX_VALUE ? -1 : distances[to];
    }
}
