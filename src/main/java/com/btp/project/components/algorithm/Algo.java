package com.btp.project.components.algorithm;

import java.util.*;

import com.btp.project.components.graph.model.Graph;
import com.btp.project.components.graph.model.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Algo {

    private static final Logger logger = LogManager.getLogger(Algo.class);

    public static Pair<Integer, List<Integer>> shortestPathWithFuel(
            int from, int to, Graph graph, int initialFuel,
            int capacity, double thresholdPenalty, int detourPenaltyFactor, int refuelCostPerUnit
    ) {

        logger.info("Starting shortestPathWithFuel: from={}, to={}, initialFuel={}, capacity={}, thresholdPenalty={}, detourPenaltyFactor={}, refuelCostPerUnit={}",
                from, to, initialFuel, capacity, thresholdPenalty, detourPenaltyFactor, refuelCostPerUnit);

        List<List<Pair<Integer, Integer>>> adj = graph.getAdjacencyList();
        int n = adj.size();

        // Precompute the shortest paths for detour penalty
        int[] shortestPathFromVToTo = computeShortestPathEnergy(to, adj, n);
        int shortestPathStartToTo = shortestPathFromVToTo[from];
        logger.info("Precomputed detour costs: start vertex distance to target = {}", shortestPathStartToTo);

        // Priority queue for Dijkstra's algorithm with fuel state
        PriorityQueue<State> pq = new PriorityQueue<>(Comparator
                .comparingInt((State s) -> s.energyCost)
                .thenComparingInt(s -> -s.fuel));

        int[][] dp = new int[n][capacity + 1];
        for (int[] row : dp) Arrays.fill(row, Integer.MAX_VALUE);
        dp[from][initialFuel] = 0;

        // Start with initial state
        State initialState = new State(from, 0, 0, initialFuel, null, false);
        pq.offer(initialState);
        logger.info("Initial state added to PQ: {}", initialState);

        // Track best path
        State bestState = null;
        int bestEnergy = Integer.MAX_VALUE;

        int threshold = (int) (0.2 * capacity); // 20% threshold

        while (!pq.isEmpty()) {
            State cur = pq.poll();
            int u = cur.vertex;
            int currEnergyCost = cur.energyCost; // Total energy including penalties
            int currPathEnergy = cur.pathEnergy; // Raw Energy without penalties to find detour
            int currFuel = cur.fuel;
            boolean hasChargedHere = cur.hasChargedHere;

            logger.info("Processing state: vertex={}, energyCost={}, pathEnergy={}, fuel={}, hasChargedHere={}",
                    u, currEnergyCost, currPathEnergy, currFuel, hasChargedHere);

            if (currEnergyCost > dp[u][currFuel] || isDominated(u, currFuel, currEnergyCost, capacity, dp)) {
                logger.info("Skipping state at vertex {} (fuel {}) as it is outdated or dominated", u, currFuel);
                continue;
            }

            // Reached target
            if (u == to && currEnergyCost < bestEnergy) {
                bestEnergy = currEnergyCost;
                bestState = cur;
                logger.info("Reached target {} with improved energyCost={}", to, bestEnergy);
                // Continue processing as we might still find better solutions
                continue;
            }

            // Move to adjacent nodes without refuel
            for (Pair<Integer, Integer> edge : adj.get(u)) {
                int v = edge.getFirst();
                int energyConsumed = edge.getSecond();

                if (cur.visited.get(v)) {
                    logger.info("Skipping already visited node {} from vertex {}", v, u);
                    continue;
                }
                if (currFuel < energyConsumed) {
                    logger.info("Cannot move from vertex {} to {}: insufficient fuel (needs {}, has {})", u, v, energyConsumed, currFuel);
                    continue;
                }

                int newFuel = currFuel - energyConsumed;
                int newPathEnergy = currPathEnergy + energyConsumed;

                // Compute the estimated total energy including detour penalty
                int estimatedTotalEnergy = newPathEnergy + shortestPathFromVToTo[v];
                int detourExcess = Math.max(0, estimatedTotalEnergy - shortestPathStartToTo);
                int newEnergy = currEnergyCost + energyConsumed + (detourExcess * detourPenaltyFactor);

                // Apply threshold penalty if fuel is below threshold
                if (newFuel < threshold) {
                    int penalty = (int) ((threshold - newFuel) * thresholdPenalty);
                    newEnergy += penalty;
                    logger.info("Applied threshold penalty at vertex {}: fuel={}, penalty={}", v, newFuel, penalty);

                }

                logger.info("Edge from {} to {}: energyConsumed={}, newFuel={}, newPathEnergy={}, newEnergy={}",
                        u, v, energyConsumed, newFuel, newPathEnergy, newEnergy);

                // Check if new state is dominated
                if (isDominated(v, newFuel, newEnergy, capacity, dp)) {
                    logger.info("Skipping dominated state for vertex {} with fuel {}", v, newFuel);
                    continue;
                }

                // Update DP and enqueue
                if (newEnergy < dp[v][newFuel]) {

                    dp[v][newFuel] = newEnergy;
                    State newState = new State(v, newEnergy, newPathEnergy, newFuel, cur, false);
                    pq.offer(newState);
                    logger.info("Enqueued new state: {}", newState);
                }

            }

            // Charging logic
            if (graph.isChargingStation(u) && !hasChargedHere) {

                logger.info("At charging station at vertex {}: evaluating refueling options (current fuel={})", u, currFuel);
                int step = Math.max(1, capacity / 10);
                for (int charge = step; currFuel + charge <= capacity; charge += step) {
                    int newFuel = currFuel + charge;
                    int refuelCost = charge * refuelCostPerUnit;
                    int newEnergy = currEnergyCost + refuelCost;

                    logger.info("Charging option at {}: increment={}, newFuel={}, refuelCost={}, newEnergy={}",
                            u, charge, newFuel, refuelCost, newEnergy);
                    // Check if new charging state is dominated
                    if (isDominated(u, newFuel, newEnergy, capacity, dp)) {
                        logger.info("Skipping charging option at vertex {} (new fuel {} dominated)", u, newFuel);
                        continue;
                    }

                    // Update DP and enqueue
                    if (newEnergy < dp[u][newFuel]) {

                        dp[u][newFuel] = newEnergy;
                        State chargeState = new State(u, newEnergy, currPathEnergy, newFuel, cur.predecessor, true);
                        pq.offer(chargeState);
                        logger.info("Enqueued charging state: {}", chargeState);
                    }
                }
            }
        }

        // Reconstruct path
        List<Integer> path = new ArrayList<>();
        for (State s = bestState; s != null; s = s.predecessor) {
            path.add(s.vertex);
        }
        Collections.reverse(path);

        if (bestEnergy == Integer.MAX_VALUE) {
            logger.info("No path found from {} to {}", from, to);
            return new Pair<>(Integer.MAX_VALUE, Collections.emptyList());
        } else {
            logger.info("Path found to {}: energyCost={}, path={}", to, bestEnergy, path);
            return new Pair<>(bestEnergy, path);
        }
    }

    public static int[] computeShortestPathEnergy(int to, List<List<Pair<Integer, Integer>>> adj, int n) {
        logger.info("Starting reverse shortest path computation toward target {}", to);

        // Reverse the adjacency list to compute paths TO 'to'
        List<List<Pair<Integer, Integer>>> reversedAdj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            reversedAdj.add(new ArrayList<>());
        }

        for (int u = 0; u < adj.size(); u++) {
            for (Pair<Integer, Integer> edge : adj.get(u)) {
                int v = edge.getFirst();
                int cost = edge.getSecond();
                reversedAdj.get(v).add(new Pair<>(u, cost)); // Reverse edge direction
            }
        }
        logger.info("Reversed adjacency list computed.");

        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[to] = 0;

        PriorityQueue<Pair<Integer, Integer>> pq = new PriorityQueue<>(Comparator.comparingInt(Pair::getSecond));
        pq.offer(new Pair<>(to, 0));

        while (!pq.isEmpty()) {
            Pair<Integer, Integer> cur = pq.poll();
            int u = cur.getFirst();
            int d = cur.getSecond();
            if (d > dist[u]) continue;
            for (Pair<Integer, Integer> edge : reversedAdj.get(u)) {
                int v = edge.getFirst();
                int newDist = d + edge.getSecond();
                if (newDist < dist[v]) {
                    dist[v] = newDist;
                    pq.offer(new Pair<>(v, newDist));
                }
            }
        }
        logger.info("Completed reverse shortest path computation for target {}", to);
        return dist;
    }

    /**
     * Checks if a new state (node, fuel, cost) is dominated by any existing state
     * with fuel >= current fuel and cost <= current cost.
     */
    private static boolean isDominated(int node, int fuel, int cost, int capacity, int[][] dp) {
        // Check fuel levels STRICTLY GREATER than the current fuel
        for (int f = fuel + 1; f <= capacity; f++) {
            if (dp[node][f] <= cost) {
                return true;
            }
        }
        return false;
    }
}

