package com.btp.project.Components.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class Graph {
    private int vertices;
    private List<List<Pair<Integer, Integer>>> adj;

    public Graph(){

    }
    public Graph(int vertices, List<List<Integer>> edges) {
        this.vertices = vertices;
        this.adj = new ArrayList<>(vertices);

        // Initialize each adjacency list for each vertex
        for (int i = 0; i < vertices; i++) {
            adj.add(new ArrayList<>());
        }

        generateAdjList(edges);
    }

    public Graph setVertices(int n){
        this.vertices = n;
        return this;
    }

    public Graph setEdges(List<List<Integer>> edges){
        this.adj = new ArrayList<>(vertices);

        // Initialize each adjacency list for each vertex
        for (int i = 0; i < vertices; i++) {
            adj.add(new ArrayList<>());
        }

        generateAdjList(edges);
        return this;
    }

    private void generateAdjList(List<List<Integer>> edges) {
        for (List<Integer> it : edges) {
            int u = it.get(0); // Source vertex
            int v = it.get(1); // Destination vertex
            int weight = it.get(2); // Weight of the edge

            // Add the edge to the adjacency list
            adj.get(u).add(new Pair<>(v, weight));
            adj.get(v).add(new Pair<>(u, weight));
        }
    }

    public List<List<Pair<Integer, Integer>>> getAdj() {return adj;}
    public int getVertices() { return vertices;}
}