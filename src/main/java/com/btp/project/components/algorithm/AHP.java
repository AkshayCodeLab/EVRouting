package com.btp.project.components.algorithm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

/**
 * AHP algorithm implementation to automate the multi-parameter optimization of constant coefficients
 * based on user preference.
 **/
public class AHP {
    private static final Logger logger = LogManager.getLogger(AHP.class);
    private final double[][] pairwiseMatrix;
    private double[] weights;
    private boolean isConsistent;

    public AHP(double[][] pairwiseMatrix) {
        this.pairwiseMatrix = pairwiseMatrix;
        calculateWeights();
    }

    private void calculateWeights() {
        int n = pairwiseMatrix.length;
        double[] geoMeans = new double[n];
        double sumGeo = 0.0;

        // Calculate geometric means
        for (int i = 0; i < n; i++) {
            double prod = 1.0;
            for (int j = 0; j < n; j++) {
                prod *= pairwiseMatrix[i][j];
            }
            geoMeans[i] = Math.pow(prod, 1.0 / n);
            sumGeo += geoMeans[i];
        }

        // Normalize to get weights
        weights = new double[n];
        for (int i = 0; i < n; i++) {
            weights[i] = geoMeans[i] / sumGeo;
        }

        logger.info("AHP weights computed: {}", Arrays.toString(weights));
        checkConsistency();
    }

    private void checkConsistency() {

        int n = pairwiseMatrix.length;
        double[] lambdaMax = new double[n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                lambdaMax[i] += pairwiseMatrix[i][j] * weights[j];
            }
            lambdaMax[i] /= weights[i];
        }

        double lambdaMaxAvg = Arrays.stream(lambdaMax).average().orElse(0);
        double consistencyIndex = (lambdaMaxAvg - n) / (n - 1);
        double[] randomIndices = {0, 0, 0.58, 0.9, 1.12, 1.24, 1.32, 1.41, 1.45, 1.49};
        double randomIndex = n - 1 < randomIndices.length ? randomIndices[n - 1] : 1.49;
        double consistencyRatio = consistencyIndex / randomIndex;

        isConsistent = consistencyRatio < 0.1;
        logger.info("AHP Consistency Check: CR={} {}", consistencyRatio, isConsistent ? "consistent" : "inconsistent");
    }

    public double[] getWeights() {
        if (!isConsistent) {
            logger.info("Weights may not be reliable due to inconsistent pairwise comparisons");
        }
        return weights;
    }

}
