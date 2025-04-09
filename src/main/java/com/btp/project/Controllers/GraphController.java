package com.btp.project.controllers;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.btp.project.components.algorithm.AlgoParams;
import com.btp.project.components.graph.model.Link;
import com.btp.project.dto.request.CalibrateParams;
import com.btp.project.dto.request.GraphData;
import com.btp.project.service.GraphService;

@RestController
public class GraphController {

    private final GraphService graphService;
    private static final Logger logger = LogManager.getLogger(GraphController.class);

    @Autowired
    public GraphController(GraphService graphService) {
        this.graphService = graphService;
    }

    @GetMapping("/loadGraph")
    public ResponseEntity<?> getGraphData() {
        return ResponseEntity.ok(graphService.loadGraphFromFile());
    }

    @PostMapping("/getGraph")
    public ResponseEntity<?> getGraph(@RequestBody GraphData data) {
        logger.info("Endpoint getGraph() called");


        return ResponseEntity.ok(graphService.createGraph(data));

    }

    @PostMapping("/shortestPath")
    public ResponseEntity<?> getShortestPath(@RequestBody AlgoParams algoParams) {

        return ResponseEntity.ok(graphService.findShortestPath(algoParams));
    }

    @PostMapping("/calibrate")
    public ResponseEntity<List<Link>> calibrate(@RequestBody CalibrateParams params) {

        return ResponseEntity.ok(graphService.calibrateGraph(params));
    }
}

