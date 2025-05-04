package com.btp.project.utils;

import java.util.List;

public class MatrixUtils {
    public static double[][] toPrimitiveMatrix(List<List<Double>> listMatrix){
        int rows = listMatrix.size();
        int cols = listMatrix.get(0).size();
        double[][] primitiveMatrix = new double[rows][cols];

        for (int i = 0;  i < rows; i++){
            for (int j = 0; j < cols; j++){
                primitiveMatrix[i][j] = listMatrix.get(i).get(j);
            }
        }

        return primitiveMatrix;
    }
}
