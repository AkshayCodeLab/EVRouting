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

    public void caliberate(float efficiency){
        for (List<Pair<Integer, Integer>> it : adj){
            for (Pair<Integer, Integer> x: it){
                x.setSecond(Math.round(x.getSecond()/efficiency));
            }
        }
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

    public List<Link> getD3Links(){
        List<Link> links = new ArrayList<Link>();
     
        for (int i = 0; i<adj.size(); i++){
            for (Pair<Integer, Integer> it: adj.get(i)){
                links.add(new Link(i, it.getFirst(), it.getSecond()));
            }
        }
        return links;

    }
}