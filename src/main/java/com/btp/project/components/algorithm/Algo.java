package com.btp.project.components.algorithm;

import java.util.*;

import com.btp.project.components.graph.model.Graph;
import com.btp.project.components.graph.model.Pair;
import com.btp.project.components.graph.utils.Normalizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Algo {

    private static final Logger logger = LogManager.getLogger(Algo.class);

    public static Pair<Double, List<Integer>> shortestPathWithFuel(
            int from, int to, Graph graph, int initialFuel,
            double[][] pairWiseComparisonMatrix,
            int capacity) {

        logger.info("Starting shortestPathWithFuel: from={}, to={}, initialFuel={}, capacity={}, matrix : {}",
                from, to, initialFuel, capacity, pairWiseComparisonMatrix);

        List<List<Pair<Integer, Integer>>> adj = graph.getAdjacencyList();
        int n = adj.size();

        // Initialize AHP with default pairwise comparison matrix
//        double[][] defaultPairwiseMatrix = {
//                // Energy, Detour, Recharge, Threshold
//                {1, 3, 5, 5},     // Energy is moderately more important than Detour, strongly more than others
//                {1/3.0, 1, 3, 3}, // Detour is moderately more important than Recharge and Threshold
//                {1/5.0, 1/3.0, 1, 1}, // Recharge and Threshold are equally important
//                {1/5.0, 1/3.0, 1, 1}
//        };

        AHP ahp = new AHP(pairWiseComparisonMatrix);
        double[] weights = ahp.getWeights();
        double energyWeight = weights[0];
        double detourWeight = weights[1];
        double rechargeWeight = weights[2];
        double thresholdWeight = weights[3];

        logger.info("AHP weights - Energy: {}, Detour: {}, Recharge: {}, Threshold: {}",
                energyWeight, detourWeight, rechargeWeight, thresholdWeight);

        // Precompute the shortest paths for detour penalty
        int[] shortestPathFromVToTo = computeShortestPathEnergy(to, adj, n);
        int shortestPathStartToTo = shortestPathFromVToTo[from];
        logger.info("Precomputed detour costs: start vertex distance to target = {}", shortestPathStartToTo);

        Normalizer normalizer = new Normalizer(graph, from, to, capacity, shortestPathFromVToTo, shortestPathStartToTo);

        logger.info("Normalizer initialized with maxFuel={}, maxDetour={}, maxDistanceFromThreshold={}, maxRefuel={}.",
                normalizer.getMaxFuel(), normalizer.getMaxDetour(), normalizer.getMaxDistanceFromThreshold(), normalizer.getMaxRefuel());

        // Priority queue for Dijkstra's algorithm with fuel state
        PriorityQueue<State> pq = new PriorityQueue<>(Comparator
                .comparingDouble((State s) -> s.energyCost)
                .thenComparingInt(s -> -s.fuel));

        double[][] dp = new double[n][capacity + 1];
        for (double[] row : dp) Arrays.fill(row, Double.POSITIVE_INFINITY);
        dp[from][initialFuel] = 0.0;

        // Initial state
        State initialState = new State(from, 0.0, 0.0,
                initialFuel, null, false);

        pq.offer(initialState);
        logger.info("Initial state added to PQ: {}", initialState);

        // Track best path
        State bestState = null;
        double bestEnergy = Double.POSITIVE_INFINITY;
        int threshold = (int) (0.2 * capacity); // 20% threshold
        logger.info("Fuel threshold set at {} (20% of capacity)", threshold);

        while (!pq.isEmpty()) {
            State cur = pq.poll();
            int u = cur.vertex;
            double currEnergyCost = cur.energyCost;
            double currPathEnergy = cur.pathEnergy;
            int currFuel = cur.fuel;
            boolean hasChargedHere = cur.hasChargedHere;

            logger.info("[PROCESS] state: {}", cur);

            if (currEnergyCost > dp[u][currFuel]) {
                logger.trace("State outdated: dp[{}][{}] = {} < currentCost {}", u, currFuel, dp[u][currFuel], currEnergyCost);
                continue;
            }
            if (isDominated(u, currFuel, currEnergyCost, capacity, dp)) {
                logger.trace("State dominated at vertex {} with fuel {} and cost {}", u, currFuel, currEnergyCost);
                continue;
            }

            // Reached target
            if (u == to && currEnergyCost < bestEnergy) {
                bestEnergy = currEnergyCost;
                bestState = cur;
                logger.info("[TARGET] reached {} with improved cost {}", to, bestEnergy);
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
                double newPathEnergy = currPathEnergy + energyConsumed;

                // Calculate normalized fuel term
                double fuelTerm = (energyConsumed / normalizer.getMaxFuel()) * energyWeight;

                // Compute the estimated total energy including detour penalty
                int estimatedTotalEnergy = (int) (newPathEnergy + shortestPathFromVToTo[v]);
                int detourExcess = Math.max(0, estimatedTotalEnergy - shortestPathStartToTo);
                double detourTerm = (detourExcess / normalizer.getMaxDetour()) * detourWeight;

                double newEnergy = currEnergyCost + fuelTerm + detourTerm;

                // Threshold penalty
                if (newFuel < threshold) {
                    double distanceFromThreshold = threshold - newFuel;
                    double thresholdTerm = (distanceFromThreshold / normalizer.getMaxDistanceFromThreshold()) * thresholdWeight;
                    newEnergy += thresholdTerm;
                    logger.info("Threshold penalty applied: distance={}, term={}", distanceFromThreshold, thresholdTerm);
                }

                logger.info("Evaluate move {}->{} | fuel {}, newFuel {}, energyCost {}", u, v, currFuel, newFuel, newEnergy);

                // Check if new state is dominated
                if (isDominated(v, newFuel, newEnergy, capacity, dp)) {
                    logger.info("Dominated move to {} with fuel {} and cost {}", v, newFuel, newEnergy);
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
                    double refuelTerm = (charge / normalizer.getMaxRefuel()) * rechargeWeight;
                    double newEnergy = currEnergyCost + refuelTerm;

                    // Check if new charging state is dominated
                    if (isDominated(u, newFuel, newEnergy, capacity, dp)) {
                        logger.info("Dominated charging option: fuel {} cost {}", newFuel, newEnergy);
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

        if (bestEnergy == Double.POSITIVE_INFINITY) {
            logger.info("No feasible path found from {} to {}", from, to);
            return new Pair<>(Double.POSITIVE_INFINITY, Collections.emptyList());
        } else {
            logger.info("[END] Path found: cost={}, path={} (length={})", bestEnergy, path, path.size());
            return new Pair<>(bestEnergy, path);
        }
    }

    public static int[] computeShortestPathEnergy(int to, List<List<Pair<Integer, Integer>>> adj, int n) {
        logger.info("[START] computeShortestPathEnergy toward {} on graph of size {}", to, n);

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
    private static boolean isDominated(int node, int fuel, double cost, int capacity, double[][] dp) {
        // Check fuel levels STRICTLY GREATER than the current fuel
        for (int f = fuel + 1; f <= capacity; f++) {
            if (dp[node][f] <= cost) {
                return true;
            }
        }
        return false;
    }
}

