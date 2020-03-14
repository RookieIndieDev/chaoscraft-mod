package com.schematical.chaoscraft.util;

import java.util.HashSet;

public final class NoveltyHelper {
    private NoveltyHelper(){}
    private static HashSet<Integer> normsArchive = new HashSet<>();
    private static int highestNovelty;
    private static String orgName;

    public static void addToArchive(int value){
        if(normsArchive!=null){
            normsArchive.add(value);
        }

    }

    public static int getNovelty(int norm){
        int sum = 0;
        for(double archive : normsArchive){
            sum += Math.abs(norm - archive);
        }
        return (sum/normsArchive.size());
    }

    public static int getAverage(){
        int sum = 0;
        int average = 0;

        for(double archive: normsArchive){
            sum += archive;
        }

        if(normsArchive.size() > 0){
            average = sum/normsArchive.size();
        }
        return average;
    }

    public static void setHighestNovelty(int value){
        highestNovelty = value;
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
}
