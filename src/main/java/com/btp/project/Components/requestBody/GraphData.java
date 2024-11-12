package com.btp.project.Components.requestBody;

import java.util.List;

public class GraphData {
    private int to;
    private int from;
    private int n;
    List<List<Integer>> edges;


    public int getTo(){ return to;}
    public int getFrom() { return from; }
    public List<List<Integer>> getEdges() { return edges; }
    public int getN() { return n; }

    public void setTo(int to) { this.to = to;}
    public void setFrom(int from) {this.from = from;}
    public void setEdges(List<List<Integer>> edges){ this.edges = edges;}
    public void setN(int n){this.n = n;}

    @Override
    public String toString() {
        return "\nto: " + to 
        + "\nfrom: " + from
        + "\nedges" + edges;
    }

}
