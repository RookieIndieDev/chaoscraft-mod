package com.schematical.chaoscraft.util;

import org.apache.commons.math3.ml.clustering.Clusterable;

public class PlacedBlockPos{
    double[] position;

    public PlacedBlockPos(double x, double y, double z){
        position = new double[3];
        position[0] = x;
        position [1] = y;
        position[2] = z;
    }

    public double[] getPoint() {
        return position;
    }
}
