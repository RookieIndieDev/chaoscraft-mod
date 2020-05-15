package com.schematical.chaoscraft.util;

import com.schematical.chaoscraft.ChaosCraft;
import com.schematical.chaoscraft.server.ServerOrgManager;
import com.schematical.chaoscraft.tileentity.BuildAreaMarkerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MinecraftGame;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.util.FastMath;

import static com.schematical.chaoscraft.ChaosCraft.LOGGER;

public class BuildArea{
    public Array2DRowRealMatrix[] templates = new Array2DRowRealMatrix[4];
    public Array2DRowRealMatrix[] areaMatrices = new Array2DRowRealMatrix[4];
    private BuildAreaMarkerTileEntity buildAreaEntity;
    private ServerOrgManager currentServerOrgManager;
    private double score;
    private int blockPlacedCount = 0;
    private static int lowestDiffValue = -1;

    public void resetBlockPlacedCount(){
        blockPlacedCount = 0;
    }

    public void getBlocks(BlockPos pos){
        pos = pos.add(1, -2, -1);
        BlockPos currentBlock;
        Block block;

        for(int k = 0; k < templates.length; k++){
            for(int i = 0; i < 12; i++) {
                for (int j = 0; j < 12; j++) {
                    if (buildAreaEntity.getWorld() != null) {
                        currentBlock = pos.add(i, k, -j);
                        block = buildAreaEntity.getWorld().getBlockState(currentBlock).getBlock();
                        updateMatrix(i, j, block.toString(), k);
                    }
                }
            }
        }
    }

    public void assignBuildAreaTileEntity(BuildAreaMarkerTileEntity tileEntity){
        this.buildAreaEntity = tileEntity;
    }

    public void assignCurrentOrgManager(ServerOrgManager orgManager){
        this.currentServerOrgManager = orgManager;
    }

    public ServerOrgManager getCurrentServerOrgManager(){
        return this.currentServerOrgManager;
    }
    public BuildAreaMarkerTileEntity getBuildAreaEntity(){
        return this.buildAreaEntity;
    }

    public void createMatrices(){
        double[][] intialValues = new double[12][12];
            for(int i = 0; i < 12; i++){
                for(int j = 0; j < 12; j++){
                    intialValues[i][j] = 2.0;
                }
            }
            for(int i = 0; i < 4; i++){
                areaMatrices[i] = (Array2DRowRealMatrix)MatrixUtils.createRealMatrix(intialValues);
            }
        LOGGER.info("Area Matrix created");
    }

    public void updateMatrix(int row, int col, String block, int areaMatrixIndex){
        int novelty = 0;
        switch(block){
                case "Block{minecraft:oak_planks}":
                    areaMatrices[areaMatrixIndex].getDataRef()[row][col] = 8.0;
                    blockPlacedCount += 1;
                    break;

                case "Block{minecraft:air}":
                    areaMatrices[areaMatrixIndex].getDataRef()[row][col] = 2.0;
                    break;

                case "Block{minecraft:oak_door}":
                    areaMatrices[areaMatrixIndex].getDataRef()[row][col] = 10.0;
                    blockPlacedCount += 1;
                    break;
        }
    }

    public void buildTemplates(){
        double[][] pattern = new double[12][12];
        for(int i = 0; i < 12; i++){
            for(int j = 0; j < 12; j++){
                pattern[i][j] = 2.0;
            }
        }

        pattern[4][4] = 8.0;
        pattern[4][5] = 8.0;
        pattern[4][6] = 8.0;
        pattern[4][7] = 8.0;

        pattern[5][4] = 8.0;
        pattern[5][5] = 2.0;
        pattern[5][6] = 2.0;
        pattern[5][7] = 8.0;

        pattern[6][4] = 8.0;
        pattern[6][5] = 2.0;
        pattern[6][6] = 2.0;
        pattern[6][7] = 8.0;

        pattern[7][4] = 8.0;
        pattern[7][5] = 10.0;
        pattern[7][6] = 8.0;
        pattern[7][7] = 8.0;

        for(int i = 0; i < 4; i++){
            templates[i] = (Array2DRowRealMatrix) MatrixUtils.createRealMatrix(pattern);
        }

        templates[3].getDataRef()[5][4] = 8.0;
        templates[3].getDataRef()[5][5] = 8.0;
        templates[3].getDataRef()[5][6] = 8.0;
        templates[3].getDataRef()[5][7] = 8.0;

        templates[3].getDataRef()[6][4] = 8.0;
        templates[3].getDataRef()[6][5] = 8.0;
        templates[3].getDataRef()[6][6] = 8.0;
        templates[3].getDataRef()[6][7] = 8.0;

        templates[3].getDataRef()[7][4] = 8.0;
        templates[3].getDataRef()[7][5] = 8.0;
        templates[3].getDataRef()[7][6] = 8.0;
        templates[3].getDataRef()[7][7] = 8.0;

        templates[2].getDataRef()[7][6] = 8.0;
    }

    private double calculateDifference(Array2DRowRealMatrix template, Array2DRowRealMatrix area){
        double diffNorm = 0.0;
        diffNorm = FastMath.abs(template.subtract(area).getNorm());
        return diffNorm;
    }

    public double getScore(){
        int novelty = 0;

        /*
        if(blockPlacedCount > 0){
            totalNorm = getAreaNorms();
            NoveltyHelper.addToArchive(totalNorm);
            novelty = NoveltyHelper.getNovelty(totalNorm);
            if(novelty > NoveltyHelper.getHighestNovelty()){
                NoveltyHelper.setHighestNovelty(novelty);
                score = (novelty * 10);
            }
        }
         */

        for(int i = 0; i < templates.length; i++){
            double diff = calculateDifference(templates[i], areaMatrices[i]);
            LOGGER.info("Diff " + i + ": " + diff);
            if(diff == 0){
                score += 1000;
            }
            else if (diff <= 8 && diff > 0) {
                score += 750;
            }
            /*
            NoveltyHelper.addToArchive((int)diff);
            novelty = NoveltyHelper.getNovelty((int)diff);
            if(novelty > NoveltyHelper.getHighestNovelty()){
                NoveltyHelper.setHighestNovelty(novelty);
                score = (novelty * 10);
            }
             */
            if(lowestDiffValue == -1){
                lowestDiffValue = (int)diff;
            }
            else if (diff < lowestDiffValue){
                score += (FastMath.abs(diff - lowestDiffValue)) * 10;
                lowestDiffValue = (int)diff;
            }
        }
        return score;
    }
    
    public int getLowestDiffValue(){
        return lowestDiffValue;
    }

    public int getAreaNorms(){
        int norm = 0;
        for(Array2DRowRealMatrix area : areaMatrices){
            norm += area.getNorm();
        }
        return  norm;
    }

    public int getTargetNorm(){
        int norm = 0;
        for(Array2DRowRealMatrix area : templates){
            norm += area.getNorm();
        }
        return  norm;
    }

    public void resetScore(){
        score = 0.0;
    }
}
