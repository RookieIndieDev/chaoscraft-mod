package com.schematical.chaoscraft.util;

import com.schematical.chaoscraft.ChaosCraft;
import it.unimi.dsi.fastutil.Hash;
import net.minecraftforge.common.util.Lazy;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.util.FastMath;
import org.apache.logging.log4j.spi.LoggerRegistry;

import java.util.*;

import static com.schematical.chaoscraft.ChaosCraft.LOGGER;

public final class NoveltyHelper {
    private NoveltyHelper(){}
    private static HashSet<Integer> archiveSet = new HashSet<>();
    private static HashSet<Integer> currentNormSet = new HashSet<>();
    private static List<Norm> archive = new ArrayList<>();
    private static List<Norm> currentNorms = new ArrayList<>();
    private static int highestNovelty;
    private static String orgName;
    private static int noveltyThreshold;
    private static KMeansPlusPlusClusterer<Norm> kMeansPlusPlusClusterer = new KMeansPlusPlusClusterer<Norm>(5);

    public static void addToArchive(int value){
        if(archive!=null){
                if(archiveSet.add(value) && getNovelty(value) >= noveltyThreshold){
                    Norm newNorm = new Norm(value);
                    archive.add(newNorm);
                }
        }
        addToCurrentNorms(value);
    }

    private static void addToCurrentNorms(int value){
        if(currentNorms != null){
            if(currentNormSet.add(value)){
                Norm newNorm = new Norm(value);
                currentNorms.add(newNorm);
            }
        }
    }

    public static int getNovelty(int norm){
        return getAverageDist(norm, currentNorms) + getAverageDist(norm, archive);
    }

    private static int getAverageDist(int value, List<Norm> values){
        int avgDist = 0;
        int clusterSize = 1;
        if(values.size() > 0) {
            if (values.size() >= kMeansPlusPlusClusterer.getK()) {
                List<CentroidCluster<Norm>> clusters = kMeansPlusPlusClusterer.cluster(values);
                outerLoop:
                for (CentroidCluster<Norm> cluster : clusters) {
                    List<Norm> points = cluster.getPoints();
                    for (Norm item : points) {
                        if (item.getPoint()[0] == value) {
                            for (Norm point : points) {
                                avgDist += FastMath.abs(value - point.getPoint()[0]);
                            }
                            clusterSize = points.size();
                            break outerLoop;
                        }
                    }
                }
            }
        }
        return avgDist/clusterSize;
    }

    public static int getAverage(){
        int sum = 0;
        int average = 0;

        for(int value: currentNormSet){
            sum += value;
        }

        if(currentNorms.size() > 0){
            average = sum/currentNormSet.size();
        }

        return average;
    }

    public static void setHighestNovelty(int value){

        highestNovelty = value;
        setNoveltyThreshold();
    }

    public static int getHighestNovelty(){
        return highestNovelty;
    }

    public static String getMostNovelOrg(){
        if(orgName != null){
            return orgName;
        }
        return " ";
    }

    public static void setMostNovelOrg(String name){
        orgName = name;
    }

    private static void setNoveltyThreshold(){
        noveltyThreshold = (int)(getHighestNovelty() * 0.40);
    }

    public static int getNoveltyThreshold(){
        return noveltyThreshold;
    }
}
