package com.btp.project.graph.utils;

import java.util.List;
import com.btp.project.exception.GraphConstructionException;

public class GraphValidator {
    public static void validate(int vertices, List<List<Integer>> edges) {

        if (vertices <= 0) {
            throw new GraphConstructionException("Number of vertices must be positive");
        }

        if (edges == null) {
            return;
        }

        for (List<Integer> edge : edges) {
            if (edge == null || edge.size() < 3) {
                throw new GraphConstructionException("Invalid edge format");
            }

            if (edge.get(0) < 0 || edge.get(0) >= vertices || edge.get(1) < 0
                    || edge.get(1) >= vertices) {
                throw new GraphConstructionException("Vertex index out of bounds");
            }
        }

    }
}
