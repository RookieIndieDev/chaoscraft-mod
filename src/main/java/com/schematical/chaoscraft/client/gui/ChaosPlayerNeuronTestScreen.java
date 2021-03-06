package com.schematical.chaoscraft.client.gui;

import com.google.common.base.Strings;
import com.mojang.blaze3d.systems.RenderSystem;
import com.schematical.chaoscraft.ai.NeuronBase;
import com.schematical.chaoscraft.ai.inputs.BaseTargetInputNeuron;
import com.schematical.chaoscraft.ai.inputs.targetcandidate.TargetDistanceInput;
import com.schematical.chaoscraft.ai.inputs.targetcandidate.TargetPitchInput;
import com.schematical.chaoscraft.ai.inputs.targetcandidate.TargetYawInput;
import com.schematical.chaoscraft.client.ClientOrgManager;
import com.schematical.chaoscraft.network.packets.CCServerObserverOrgChangeEventPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;

import java.util.ArrayList;

public class ChaosPlayerNeuronTestScreen extends AbstractGui {
    private final Minecraft mc;
    private final FontRenderer fontRenderer;
    private ClientPlayerEntity playerEntity;
    private ArrayList<NeuronBase> debugNeurons = new ArrayList<NeuronBase>();

    public ChaosPlayerNeuronTestScreen(Minecraft mc) {
        this.mc = mc;
        this.fontRenderer = mc.fontRenderer;
        playerEntity = mc.player;


        BaseTargetInputNeuron neuronBase = new TargetDistanceInput();
        neuronBase.setDebugEntity(playerEntity);

        debugNeurons.add(neuronBase);

        neuronBase = new TargetPitchInput();
        neuronBase.setDebugEntity(playerEntity);

        debugNeurons.add(neuronBase);

        neuronBase = new TargetYawInput();
        neuronBase.setDebugEntity(playerEntity);

        debugNeurons.add(neuronBase);

    }
    public void render(){
        RenderSystem.pushMatrix();
        ArrayList<String> list = new ArrayList<String>();
        list.add("");

        for (NeuronBase debugNeuron : debugNeurons) {
            debugNeuron.reset();
            debugNeuron.evaluate();
            list.add(debugNeuron.toLongString());
        }
      /*  Double dist = targetHelper.getDist(debugTargetHolder);
        if (dist != null){
            list.add("Target Dist: " + dist);
        }
        Double pitch = targetHelper.getPitchDelta(debugTargetHolder);
        if(pitch != null){
            list.add("Target Pitch: " + pitch);
        }
        Double yaw = targetHelper.getYawDelta(debugTargetHolder);
        if(yaw != null){
            list.add("Target Yaw: " + yaw);
        }
*/
        for(int i = 0; i < list.size(); ++i) {
            String s = list.get(i);
            if (!Strings.isNullOrEmpty(s)) {
                int j = 9;
                int k = this.fontRenderer.getStringWidth(s);
                int x = this.mc.getMainWindow().getScaledWidth() - 200;
                int y = 2 + j * i;
                fill(x  - 1, y- 1, x + k + 1, y + j - 1, -1873784752);
                this.fontRenderer.drawString(s, (float)x, (float)y, 14737632);
            }
        }
        RenderSystem.popMatrix();
    }

    public void setObservedEntity(CCServerObserverOrgChangeEventPacket message, ClientOrgManager clientOrgManager) {
        if(clientOrgManager == null){
            return;
        }
      
        for (NeuronBase debugNeuron : debugNeurons) {
            debugNeuron.setDebugEntity(clientOrgManager.getEntity());
        }
    }


}
