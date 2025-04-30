package com.btp.project.service;

import java.io.InputStream;
import java.util.List;

import com.btp.project.dto.response.GraphResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import com.btp.project.Repository.VehicleRepository;
import com.btp.project.components.algorithm.Algo;
import com.btp.project.components.algorithm.AlgoParams;
import com.btp.project.components.graph.model.Graph;
import com.btp.project.components.graph.model.Link;
import com.btp.project.components.graph.model.Pair;
import com.btp.project.components.vehicle.Vehicle;
import com.btp.project.exception.GraphConstructionException;
import com.btp.project.exception.GraphProcessingException;
import com.btp.project.dto.request.CalibrateParams;
import com.btp.project.dto.request.GraphData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;


// Contains all the business logic for the graph controller
@Service
public class GraphService {
    private static final Logger logger = LogManager.getLogger(GraphService.class);

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    private final VehicleRepository vehicleRepository;
    private final Graph graph;

    @Autowired
    public GraphService(VehicleRepository vehicleRepository, Graph graph, ObjectMapper objectMapper,
            ResourceLoader resourceLoader) {
        this.vehicleRepository = vehicleRepository;
        this.graph = graph;
        this.objectMapper = objectMapper;
        this.resourceLoader = resourceLoader;
    }

    public List<List<Pair<Integer, Integer>>> loadGraphFromFile() {
        logger.info("Loading graph from the static data");
        try {
            // Load the graph.json file
            Resource resource = resourceLoader.getResource("classpath:static/graph.json");

            try (InputStream inputStream = resource.getInputStream()) {
                GraphData data = objectMapper.readValue(inputStream, GraphData.class);

                // Create new graph instance
                Graph graph = Graph.builder().vertices(data.getN()).edges(data.getEdges()).build();

                return graph.getAdjacencyList();
            }

        } catch (Exception e) {
            logger.error("Error loading graph data", e);
            throw new GraphProcessingException("Cannot load graph data", e);
        }
    }

    /**
     * Create graph with provided data
     * 
     * @param data Graph configuration data
     * @return Graph's adjacency list
     */
    public GraphResponse createGraph(GraphData data)
            throws GraphConstructionException {
        logger.info("Creating graph with data: {}", data);

        graph.setVertices(data.getN())
                .setEdges(data.getEdges())
                .setChargingStations(data.getChargingStations());

        logger.info("Successfully created the Graph: " + graph);
        return new GraphResponse(graph.getAdjacencyList(), graph.getVertices(), graph.getChargingStations());
    }

    /**
     * Find shortest path with fuel constraints
     * 
     * @param params Algorithm parameters
     * @return Shortest path details
     */
    public Pair<Double, List<Integer>> findShortestPath(AlgoParams params) {

        logger.info("Finding shortest path: from {} to {}, fuel: {}", params.getFrom(),
                params.getTo(), params.getFuel());

        int capacity = 20;
        double thresholdPenalty = 0.3;
        int detourPenaltyFactor = 30;
        int refuelCostPerUnit = 1;

        return Algo.shortestPathWithFuel(params.getFrom(), params.getTo(), graph, params.getFuel(), capacity
        , thresholdPenalty, detourPenaltyFactor, refuelCostPerUnit);
    }

    /**
     * Calibrate graph based on vehicle efficiency
     * 
     * @param params Calibration parameters
     * @return Calibrated graph links
     */
    public List<Link> calibrateGraph(CalibrateParams params) {
        logger.info("Calibrating graph for vehicle: {}", params.getName());

        Vehicle vehicle = vehicleRepository.findByName(params.getName());

        if (vehicle == null) {
            logger.error("Vehicle not found: {}", params.getName());
            throw new GraphProcessingException("Vehicle not found: " + params.getName());
        }

        graph.calibrate(vehicle.getEfficiency() * 1.60934f);
        return graph.getD3Links();
    }

}
