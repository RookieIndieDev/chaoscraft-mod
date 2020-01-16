package com.schematical.chaoscraft.client;

import com.schematical.chaoscraft.ChaosCraft;
import com.schematical.chaoscraft.ai.CCObservableAttributeManager;
import com.schematical.chaoscraft.client.gui.ChaosDebugOverlayGui;
import com.schematical.chaoscraft.entities.OrgEntity;
import com.schematical.chaoscraft.fitness.EntityFitnessManager;
import com.schematical.chaoscraft.network.ChaosNetworkManager;
import com.schematical.chaoscraft.network.packets.CCClientSpawnPacket;
import com.schematical.chaoscraft.network.packets.CCServerEntitySpawnedPacket;
import com.schematical.chaoscraft.network.packets.ClientAuthPacket;
import com.schematical.chaoscraft.network.packets.ServerIntroInfoPacket;
import com.schematical.chaosnet.model.ChaosNetException;
import com.schematical.chaosnet.model.Organism;
import com.schematical.chaosnet.model.TrainingRoomSessionNextResponse;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ChaosCraftClient {

    public TrainingRoomSessionNextResponse lastResponse;
    private int ticksSinceLastSpawn;

    public void displayTest(OrgEntity orgEntity) {
        ChaosDebugOverlayGui screen = new ChaosDebugOverlayGui(orgEntity);
        Minecraft.getInstance().displayGuiScreen(screen);
        ChaosCraft.LOGGER.info("Displaying test");
    }


    public enum State{
        Uninitiated,
        Authed
    }
    protected State state = State.Uninitiated;
    protected String trainingRoomNamespace;
    protected String trainingRoomUsernameNamespace;
    protected String sessionNamespace;

    public int consecutiveErrorCount = 0;
    public List<Organism> orgsToSpawn = new ArrayList<Organism>();
    public HashMap<String, Organism> orgsQueuedToSpawn = new HashMap<String, Organism>();
    public List<OrgEntity> orgsToReport = new ArrayList<OrgEntity>();
    public HashMap<String, OrgEntity> myOrganisims = new HashMap<String, OrgEntity>();
    public Thread thread;

    public void setTrainingRoomInfo(ServerIntroInfoPacket serverInfo) {
        trainingRoomNamespace = serverInfo.getTrainingRoomNamespace();
        trainingRoomUsernameNamespace = serverInfo.getTrainingRoomUsernameNamespace();
        sessionNamespace = serverInfo.getSessionNamespace();
        ChaosCraft.LOGGER.info("TrainingRoomInfo Set: " + trainingRoomNamespace + ", " + trainingRoomUsernameNamespace + ", " + sessionNamespace);
    }

    public State getState() {
        return state;
    }
    public String getTrainingRoomNamespace(){
        return trainingRoomNamespace;
    }
    public String getTrainingRoomUsernameNamespace(){
        return trainingRoomUsernameNamespace;
    }
    public String getSessionNamespace(){
        return sessionNamespace;
    }


    public void init(){

        ChaosCraft.LOGGER.info("Client Sending Auth!!");
        ChaosNetworkManager.sendToServer(new ClientAuthPacket(ChaosCraft.config.accessToken));
        state = State.Authed;
        //Get info on what the server is running


    }
    public void attachOrgToEntity(String orgNamespace, int entityId) {
        OrgEntity orgEntity = (OrgEntity)Minecraft.getInstance().world.getEntityByID(entityId);
        if(orgEntity == null){
            return;
        }
        Iterator<Organism> iterator = orgsToSpawn.iterator();
        Organism organism = null;
        while (iterator.hasNext()) {
            Organism testOrganism = iterator.next();
            if(testOrganism.getNamespace().equals(orgNamespace)){
                organism = testOrganism;
                orgEntity.attachOrganism(organism);
                orgEntity.attachNNetRaw(organism.getNNetRaw());
                orgEntity.observableAttributeManager = new CCObservableAttributeManager(organism);

            }
        }
        if(organism == null){
            ChaosCraft.LOGGER.error("Could not link to: " + orgNamespace);
        }else{
            myOrganisims.put(orgEntity.getCCNamespace(), orgEntity);
            orgsToSpawn.remove(organism);
        }

    }
    public void tick(){
        if(state.equals(State.Uninitiated)){
            return;
        }
        startSpawnOrgs();
        int liveOrgCount = getLiveOrgCount();
        ticksSinceLastSpawn += 1;
        if (
            ticksSinceLastSpawn < (20 * 20)
        ) {
            if(thread != null){
                return;
            }
            ticksSinceLastSpawn = 0;

            thread = new Thread(new ChaosClientThread(), "ChaosClientThread");
            thread.start();
        }




        if (
                ticksSinceLastSpawn < (20 * 20) ||
                liveOrgCount >= ChaosCraft.config.maxBotCount
        ) {


            List<OrgEntity> deadOrgs = getDeadOrgs();
            reportOrgs(deadOrgs);
        }

        if(consecutiveErrorCount > 5){
            throw new ChaosNetException("ChaosCraft.consecutiveErrorCount > 5");
        }



    }

    public void updateObservers(){
        /*if(myOrganisims.size() > 0) {
            for (EntityPlayerMP observingPlayer : observingPlayers) {
                Entity entity = observingPlayer.getSpectatingEntity();

                if (
                        entity.equals(observingPlayer) ||
                                entity == null ||
                                entity.isDead
                ) {
                    if(
                            entity != null &&
                                    entity instanceof OrgEntity
                    ){
                        ((OrgEntity) entity).setObserving(null);
                    }
                    int index = (int) Math.floor(myOrganisims.size() * Math.random());
                    OrgEntity orgToObserve = myOrganisims.get(index);
                    orgToObserve.setObserving(observingPlayer);
                    observingPlayer.setSpectatingEntity(orgToObserve);
                }
            }
        }*/
    }
    public List<OrgEntity> getDeadOrgs(){
        List<OrgEntity> deadOrgs = new ArrayList<OrgEntity>();
        Iterator<OrgEntity> iterator = myOrganisims.values().iterator();

        while (iterator.hasNext()) {
            OrgEntity organism = iterator.next();
            if (!organism.isAlive()) {
                if (
                        organism.getCCNamespace() != null// &&
                        //organism.getSpawnHash() == ChaosCraft.spawnHash &&
                        //!organism.getDebug()//Dont report Adam-0
                ) {
                    deadOrgs.add(organism);
                }
                iterator.remove();

            }
        }
        return deadOrgs;
    }
    private int getLiveOrgCount() {

        Iterator<OrgEntity> iterator = myOrganisims.values().iterator();
        int liveOrgCount = 0;
        while (iterator.hasNext()) {
            OrgEntity organism = iterator.next();
            organism.manualUpdateCheck();
            if (
                organism.getOrganism() == null// ||
                //organism.getSpawnHash() != ChaosCraft.spawnHash
            ) {
                organism.setHealth(-1);
                iterator.remove();
                //ChaosCraft.logger.info("Setting Dead: " + organism.getName() + " - Has no `Organism` record");
            }
            if (organism.isAlive()) {
                liveOrgCount += 1;
            }
        }
        return liveOrgCount;

    }

    private void startSpawnOrgs() {
        if(orgsToSpawn.size() > 0){
            Iterator<Organism> iterator = orgsToSpawn.iterator();

            while (iterator.hasNext()) {
                Organism organism = iterator.next();
                if(!orgsQueuedToSpawn.containsKey(organism.getNamespace())) {
                    CCClientSpawnPacket packet = new CCClientSpawnPacket(
                            organism.getNamespace()
                    );

                    orgsQueuedToSpawn.put(organism.getNamespace(), organism);
                    ChaosNetworkManager.sendToServer(packet);
                }
            }

        }
    }

    public void reportOrgs(List<OrgEntity> _orgsToReport){
        _orgsToReport.forEach((OrgEntity organism)->{

            orgsToReport.add(organism);
        });
        if(thread != null){
            return;
        }
        ticksSinceLastSpawn = 0;

        thread = new Thread(new ChaosClientThread(), "ChaosClientThread");
        thread.start();
    }
}