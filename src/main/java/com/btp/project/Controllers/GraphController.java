package com.btp.project.Controllers;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.btp.project.Components.Graph;
import com.btp.project.Components.GraphData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class GraphController {

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    @Autowired
    public GraphController(ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/graph")
    public ResponseEntity<?> getGraphData() {
        try {
            // Load the graph.json file as a Resource
            Resource resource = resourceLoader.getResource("classpath:static/graph.json");
            
            // Parse the JSON file and map it to GraphData
            try (InputStream inputStream = resource.getInputStream()) {
                GraphData data = objectMapper.readValue(inputStream, GraphData.class);
                Graph graph = new Graph(data.getN(), data.getEdges());

                // Return the adjacency list or graph details as a JSON response
                return ResponseEntity.ok(graph.getAdj());
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error loading or parsing graph data.");
        }
    }

    @PostMapping("/getGraph")
    public ResponseEntity<?> getGraph(@RequestBody GraphData data){
      Graph graph = new Graph(data.getN(), data.getEdges());
      return ResponseEntity.ok(graph.getAdj());

    }
}

