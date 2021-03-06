package com.schematical.chaoscraft.client.gui;

import com.schematical.chaoscraft.ChaosCraft;
import com.schematical.chaoscraft.network.ChaosNetworkManager;
import com.schematical.chaoscraft.network.packets.CCClientObserveStateChangePacket;
import com.schematical.chaoscraft.server.ChaosCraftServerPlayerInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChaosDebugMenuOverlayGui extends Screen {

    public ChaosDebugMenuOverlayGui() {
        super(new TranslationTextComponent("chaoscraft.gui.mainmenu.title"));

    }

    @Override
    protected void init() {
        super.init();
        int y = 0;
        this.addButton(new Button(this.width / 2 - 100, y, 200, 20, I18n.format("chaoscraft.gui.debugmenu.stir"), (p_214266_1_) -> {
            ChaosCraft.getClient().stir();
        }));
        y += 20;
/*
        this.addButton(new Button(this.width / 2 - 100, y, 200, 20, I18n.format("chaoscraft.gui.mainmenu.networkinfo"), (p_214266_1_) -> {
            ChaosNetworkInfoOverlayGui screen = new ChaosNetworkInfoOverlayGui();
            Minecraft.getInstance().displayGuiScreen(screen);
        }));
        y += 20;
        this.addButton(new Button(this.width / 2 - 100, y, 200, 20, I18n.format("chaoscraft.gui.mainmenu.species"), (p_214266_1_) -> {
            ChaosSpeciesListOverlayGui screen = new ChaosSpeciesListOverlayGui();
            Minecraft.getInstance().displayGuiScreen(screen);
        }));
        y += 20;
        this.addButton(new Button(this.width / 2 - 100, y, 200, 20, I18n.format("chaoscraft.gui.mainmenu.trainingroom"), (p_214266_1_) -> {
            ChaosTrainingRoomSelectionOverlayGui screen = new ChaosTrainingRoomSelectionOverlayGui();
            Minecraft.getInstance().displayGuiScreen(screen);
        }));
        y += 20;


        this.addButton(new Button(this.width / 2 - 100, y, 200, 20, I18n.format(getObserverBtnText()), (button) -> {
            ChaosCraftServerPlayerInfo.State state = ChaosCraft.getClient().getObservationState();
            if(!state.equals(ChaosCraftServerPlayerInfo.State.None)){
                state = ChaosCraftServerPlayerInfo.State.None;
            }else{
                state = ChaosCraftServerPlayerInfo.State.ObservingPassive;
            }
            CCClientObserveStateChangePacket pkt = new CCClientObserveStateChangePacket(state, null);
            ChaosNetworkManager.sendToServer(pkt);
            ChaosCraft.getClient().setObservationState(state);
            Minecraft.getInstance().displayGuiScreen(null);
            button.setMessage(getObserverBtnText());

        }));
        y += 20;

        this.addButton(new Button(this.width / 2 - 100, y, 200, 20, I18n.format("chaoscraft.gui.mainmenu.show-debug-neuron-view"), (p_214266_1_) -> {
            Minecraft.getInstance().displayGuiScreen(null);
            ChaosCraft.getClient().showPlayerNeuronTestScreen();
        }));
        y += 20;*/
    }
    private String getObserverBtnText(){
        String text = "chaoscraft.gui.mainmenu.observer-mode-start";

        ChaosCraftServerPlayerInfo.State state = ChaosCraft.getClient().getObservationState();
        if(!state.equals(ChaosCraftServerPlayerInfo.State.None)){
            text = "chaoscraft.gui.mainmenu.observer-mode-stop";
        }
        return text;
    }
    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 70, 16777215);
        int i = 90;

       /* for(String s : this.field_201553_i) {
            this.drawCenteredString(this.font, s, this.width / 2, i, 16777215);
            i += 9;
        }*/
        this.drawCenteredString(this.font, "Something else goes here", this.width / 2, i, 16777215);
        super.render(p_render_1_, p_render_2_, p_render_3_);
    }

    public void minAll() {
        for (Widget button : buttons) {
            ((ChaosNeuronButton) button).min();
        }
    }
    public void drawLine(int startX, int startY, int endX, int endY, int r, int g, int b, int a) {

    }
}
