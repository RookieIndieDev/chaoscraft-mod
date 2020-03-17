package com.schematical.chaoscraft.util;

import org.apache.commons.math3.ml.clustering.Clusterable;

public class Norm implements Clusterable {
    double[] norm;

    public Norm(int value){
        norm = new double[]{value};
    }

    @Override
    public double[] getPoint() {
        return norm;
    }
}
