package com.btp.project.Algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import com.btp.project.Components.utils.Graph;
import com.btp.project.Components.utils.Pair;

public class Algo {
        public static Pair<Integer, List<Integer>> shortestPath(int from, int to, Graph graph) {
    int n = graph.getVertices();
    List<List<Pair<Integer, Integer>>> adj = graph.getAdj();

    // Initialize distances array
    int[] distances = new int[n];
    Arrays.fill(distances, Integer.MAX_VALUE);
    distances[from] = 0;

    // Predecessor array to store the shortest path track
    int[] predecessor = new int[n];
    Arrays.fill(predecessor, -1); // -1 indicates no predecessor

    // Min-heap to select the vertex with the smallest distance
    PriorityQueue<Pair<Integer, Integer>> pq = new PriorityQueue<>(Comparator.comparingInt(Pair::getSecond));
    pq.add(new Pair<>(from, 0));

    while (!pq.isEmpty()) {
        Pair<Integer, Integer> current = pq.poll();
        int u = current.getFirst();
        int distU = current.getSecond();

        // If we reached the target node, break out of the loop
        if (u == to) {
            break;
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

            // If a shorter path is found, update distance and predecessor, and push to the queue
            if (newDist < distances[v]) {
                distances[v] = newDist;
                predecessor[v] = u;
                pq.add(new Pair<>(v, newDist));
            }
        }
    }

    // If the target node is unreachable, return -1 and an empty path
    if (distances[to] == Integer.MAX_VALUE) {
        return new Pair<>(-1, new ArrayList<>());
    }

    // Reconstruct the shortest path track
    List<Integer> path = new ArrayList<>();
    for (int at = to; at != -1; at = predecessor[at]) {
        path.add(at);
    }
    Collections.reverse(path); // Reverse to get the path from 'from' to 'to'

    // Return the shortest distance and the path
    return new Pair<>(distances[to], path);
}

}
