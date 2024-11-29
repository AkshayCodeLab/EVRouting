package com.btp.project.graph;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.btp.project.graph.model.Graph;

@Configuration
public class GraphConfig {

    @Bean
    public Graph graph() {
        Graph graph = new Graph();
        return graph;
    }
}
