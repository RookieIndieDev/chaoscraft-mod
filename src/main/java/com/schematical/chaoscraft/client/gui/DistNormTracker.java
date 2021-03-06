package com.schematical.chaoscraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.schematical.chaoscraft.ChaosCraft;
import com.schematical.chaoscraft.blocks.ChaosBlocks;
import com.schematical.chaoscraft.util.NoveltyHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;

public class DistNormTracker extends AbstractGui {
    private FontRenderer fontrenderer;
    public DistNormTracker(){
        this.fontrenderer = Minecraft.getInstance().fontRenderer;
    }

    public void render(){
        RenderSystem.pushMatrix();
        if(ChaosBlocks.markerBlocks.size() > 0){
            /*
            for(int i = 0; i < 12; i++){
                for(int j = 0; j < 12; j++){
                    fontrenderer.drawString(""+ChaosCraft.buildAreas.get(0).areaMatrices[0].getData()[i][j],15 * j, i * 15, 14737632);
                }
            }
            */
            /*
            for(int i = 0; i < ChaosBlocks.markerBlocks.size(); i++)
            {
                fontrenderer.drawString("Score :" + ChaosCraft.buildAreas.get(i).getScore(), 15, (25 * i) + 25, 14737632);
                fontrenderer.drawString("Norm of Area " + (i + 1) + " " + ChaosCraft.buildAreas.get(i).getAreamatrixNorm(), 15, (35 * i) + 35, 14737632);
            }

            fontrenderer.drawString("Average Norm: " + NoveltyHelper.getAverage(), (float)((Minecraft.getInstance().getMainWindow().getScaledWidth() / 2) + 20), (float)((Minecraft.getInstance().getMainWindow().getScaledHeight() / 2) + 20), 14737632);
            fontrenderer.drawString("Highest Novelty: " + NoveltyHelper.getHighestNovelty(), (float)((Minecraft.getInstance().getMainWindow().getScaledWidth() / 2) + 20), (float)((Minecraft.getInstance().getMainWindow().getScaledHeight() / 2) + 30), 14737632);
            fontrenderer.drawString("Novelty Archive Average: " + NoveltyHelper.getAverageNovelty(), (float)((Minecraft.getInstance().getMainWindow().getScaledWidth() / 2) + 20), (float)((Minecraft.getInstance().getMainWindow().getScaledHeight() / 2) + 40), 14737632);
            fontrenderer.drawString("Archive Size " + NoveltyHelper.getArchiveSize(), (float)((Minecraft.getInstance().getMainWindow().getScaledWidth() / 2) + 20), (float)((Minecraft.getInstance().getMainWindow().getScaledHeight() / 2) + 50), 14737632);
            fontrenderer.drawString("Target Norm: " + ChaosCraft.buildAreas.get(0).getTargetNorm(), (float)((Minecraft.getInstance().getMainWindow().getScaledWidth() / 2) + 20), (float)((Minecraft.getInstance().getMainWindow().getScaledHeight() / 2) + 60), 14737632);
            fontrenderer.drawString("Current Norms Size: " + NoveltyHelper.getCurrentNormsSize(), (float)((Minecraft.getInstance().getMainWindow().getScaledWidth() / 2) + 20), (float)((Minecraft.getInstance().getMainWindow().getScaledHeight() / 2) + 70), 14737632);        
             */
            /*
            int lowestDiffValue = ChaosCraft.buildAreas.get(0).getLowestDiffValue();
            for(int i = 0; i < ChaosCraft.buildAreas.size(); i++){
                int diff = ChaosCraft.buildAreas.get(i).getLowestDiffValue();
                if(diff < lowestDiffValue && diff != 0){
                    lowestDiffValue = diff;
                }
            }
             */

            fontrenderer.drawString("Lowest Diff: " + ChaosCraft.buildAreas.get(0).getLowestDiffValue(), (float)((Minecraft.getInstance().getMainWindow().getScaledWidth() / 2) + 20), (float)((Minecraft.getInstance().getMainWindow().getScaledHeight() / 2) + 20), 14737632);
        }
        RenderSystem.popMatrix();
    }
}
