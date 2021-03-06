package com.schematical.chaoscraft.client.gui;

import com.schematical.chaoscraft.ai.biology.BiologyBase;
import com.schematical.chaoscraft.client.ClientOrgManager;
import com.schematical.chaoscraft.network.packets.CCServerScoreEventPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;

@OnlyIn(Dist.CLIENT)
public class ChaosOrgScoreEventListOverlayGui extends Screen {

    private ClientOrgManager clientOrgManager;
    public ChaosOrgScoreEventListOverlayGui(ClientOrgManager clientOrgManager) {
        super(new TranslationTextComponent(clientOrgManager.getCCNamespace()));
        this.clientOrgManager = clientOrgManager;
    }
    @Override
    protected void init() {
        super.init();



    }
    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground();
        int y = 0;
        float secondsToLive = (clientOrgManager.getExpectedLifeEndTime() - clientOrgManager.getEntity().world.getGameTime()) /20;
        this.drawCenteredString(
                this.font,
                "Base: " + clientOrgManager.getServerScoreEventTotal() + " Life: " + secondsToLive,
                this.width / 2,
                y,
                16777215
        );
        y += 20;
        ArrayList<CCServerScoreEventPacket> scoreEvents = (ArrayList<CCServerScoreEventPacket>)clientOrgManager.getServerScoreEvents().clone();
        Collections.reverse(scoreEvents);
        for (CCServerScoreEventPacket serverScoreEventPacket : scoreEvents) {
            String message = serverScoreEventPacket.fitnessRuleId +
                    " S:" + serverScoreEventPacket.score +
                    " L:" + serverScoreEventPacket.life +
                    " M:" + serverScoreEventPacket.multiplier +
                    " T:" + serverScoreEventPacket.getAdjustedScore();
            this.drawCenteredString(
                    this.font,
                    message,
                    this.width / 2,
                    y,
                    16777215
            );
            y += 10;

        }
        super.render(p_render_1_, p_render_2_, p_render_3_);
    }
}
