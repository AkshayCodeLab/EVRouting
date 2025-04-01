package com.btp.project.components.graph.model;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.btp.project.components.graph.utils.GraphValidator;
import com.btp.project.exception.GraphConstructionException;

public class Graph {
    private static final Logger logger = LogManager.getLogger(Graph.class);

    private int vertices;
    private List<List<Pair<Integer, Integer>>> adjacencyList;
    private List<List<Pair<Integer, Integer>>> originalAdjacencyList;
    private Set<Integer> chargingStations = new HashSet<>();

    // Public constructor required by spring to initialize graph Bean
    public Graph() {

    }

    // Private constructor to enforce builder pattern
    private Graph(GraphBuilder builder) {
        GraphValidator.validate(builder.vertices, builder.edges);

        this.vertices = builder.vertices;
        this.adjacencyList = initializeAdjacencyList(builder.vertices, builder.edges);
        this.originalAdjacencyList = deepCopyAdjacencyList(this.adjacencyList);
    }

    // Static nested builder class
    public static class GraphBuilder {
        private int vertices;
        private List<List<Integer>> edges = new ArrayList<>();
        private Set<Integer> chargingStations = new HashSet<>();

        public GraphBuilder vertices(int vertices) {
            this.vertices = vertices;
            return this;
        }

        public GraphBuilder edges(List<List<Integer>> edges) {
            this.edges = edges != null ? new ArrayList<>(edges) : new ArrayList<>();
            return this;
        }

        public GraphBuilder chargingStations(Set<Integer> stations) {
            this.chargingStations = new HashSet<>(stations);
            return this;
        }

        public Graph build() {
            Graph graph = new Graph(this);
            graph.chargingStations = this.chargingStations;  // Assign charging stations
            return graph;
        }
    }

    // Factory method for easy instantiation
    public static GraphBuilder builder() {
        return new GraphBuilder();
    }

    private List<List<Pair<Integer, Integer>>> initializeAdjacencyList(int vertices,
            List<List<Integer>> edges) {

        List<List<Pair<Integer, Integer>>> adjList =
                new ArrayList<>(Collections.nCopies(vertices, null));

        for (int i = 0; i < vertices; i++) {
            adjList.set(i, new ArrayList<>());
        }

        for (List<Integer> edge : edges) {
            if (edge.size() < 3) {
                logger.warn("Skipping invalid edge: " + edge);
                continue;
            }

            int u = edge.get(0);
            int v = edge.get(1);
            int weight = edge.get(2);

            // Add edges for undirected graph
            addEdge(adjList, u, v, weight);
            addEdge(adjList, v, u, weight);
        }

        return adjList;
    }

    private void addEdge(List<List<Pair<Integer, Integer>>> adjList, int source, int destination,
            int weight) {
        if (source < 0 || source >= vertices || destination < 0 || destination >= vertices) {
            throw new GraphConstructionException(
                    "Invalid vertex: " + source + " or " + destination);
        }
        adjList.get(source).add(new Pair<>(destination, weight));
    }

    private List<List<Pair<Integer, Integer>>> deepCopyAdjacencyList(
            List<List<Pair<Integer, Integer>>> original) {
        List<List<Pair<Integer, Integer>>> copy = new ArrayList<>(original.size());

        for (List<Pair<Integer, Integer>> innerList : original) {
            List<Pair<Integer, Integer>> innerCopy = new ArrayList<>();
            for (Pair<Integer, Integer> pair : innerList) {
                innerCopy.add(new Pair<>(pair.getFirst(), pair.getSecond()));
            }
            copy.add(innerCopy);
        }

        return copy;
    }

    public void calibrate(float efficiency) {
        if (efficiency <= 0) {
            throw new IllegalArgumentException("Efficiency must be positive");
        }

        List<List<Pair<Integer, Integer>>> workingList =
                deepCopyAdjacencyList(originalAdjacencyList);

        for (List<Pair<Integer, Integer>> vertexEdges : workingList) {
            for (Pair<Integer, Integer> edge : vertexEdges) {
                int fuelValue = (int) Math.ceil(edge.getSecond() / efficiency);
                edge.setSecond(fuelValue);
            }
        }

        this.adjacencyList = workingList;
    }

    public boolean isChargingStation(int u) {
        if (u < 0 || u >= vertices) {
            throw new IllegalArgumentException("Vertex out of bounds: " + u);
        }
        return chargingStations.contains(u);
    }

    public List<List<Pair<Integer, Integer>>> getAdjacencyList() {
        return deepCopyAdjacencyList(adjacencyList);
    }

    public int getVertices() {
        return vertices;
    }

    public List<Link> getD3Links() {
        List<Link> links = new ArrayList<Link>();

        for (int i = 0; i < adjacencyList.size(); i++) {
            for (Pair<Integer, Integer> it : adjacencyList.get(i)) {
                links.add(new Link(i, it.getFirst(), it.getSecond()));
            }
        }
        return links;
    }

    public Graph setVertices(int vertices) {
        this.vertices = vertices;
        return this;
    }

    public Graph setEdges(List<List<Integer>> edges) {
        this.adjacencyList = initializeAdjacencyList(this.vertices, edges);
        this.originalAdjacencyList = deepCopyAdjacencyList(this.adjacencyList);

        return this;
    }

}
