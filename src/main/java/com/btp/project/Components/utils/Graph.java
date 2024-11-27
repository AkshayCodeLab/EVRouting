package com.btp.project.Components.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class Graph {
    private int vertices;
    private List<List<Pair<Integer, Integer>>> adj;
    private List<List<Pair<Integer, Integer>>> adjReference;
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
        adj = deepCopy(adjReference);
        for (List<Pair<Integer, Integer>> it : adj){
            for (Pair<Integer, Integer> x: it){
                int fuelVal = (int)Math.ceil(x.getSecond()/efficiency);
                x.setSecond(fuelVal);
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
        this.adjReference = deepCopy(adj);
    }

    private List<List<Pair<Integer, Integer>>> deepCopy(List<List<Pair<Integer, Integer>>> original) {
        List<List<Pair<Integer, Integer>>> copy = new ArrayList<>();

        for (List<Pair<Integer, Integer>> innerList : original) {
            List<Pair<Integer, Integer>> innerCopy = new ArrayList<>();
            for (Pair<Integer, Integer> pair : innerList) {
                // Create a new Pair instance to ensure independence
                Pair<Integer, Integer> newPair = new Pair<>(pair.getFirst(), pair.getSecond());
                innerCopy.add(newPair);
            }
            copy.add(innerCopy);
        }

        return copy;
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