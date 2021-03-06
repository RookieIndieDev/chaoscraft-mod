package com.schematical.chaoscraft.server;

import com.schematical.chaoscraft.BaseOrgManager;
import com.schematical.chaoscraft.ChaosCraft;
import com.schematical.chaoscraft.TrainingRoomRoleHolder;
import com.schematical.chaoscraft.ai.CCObservableAttributeManager;
import com.schematical.chaoscraft.ai.OutputNeuron;
import com.schematical.chaoscraft.ai.action.ActionBase;
import com.schematical.chaoscraft.ai.outputs.rawnav.RawOutputNeuron;
import com.schematical.chaoscraft.entities.OrgEntity;
import com.schematical.chaoscraft.events.CCWorldEvent;
import com.schematical.chaoscraft.fitness.managers.EntityDiscoveryFitnessManager;
import com.schematical.chaoscraft.fitness.managers.EntityRuleFitnessManager;
import com.schematical.chaoscraft.fitness.managers.FitnessManagerBase;
import com.schematical.chaoscraft.network.ChaosNetworkManager;
import com.schematical.chaoscraft.network.packets.CCClientOrgUpdatePacket;
import com.schematical.chaoscraft.network.packets.CCClientOutputNeuronActionPacket;
import com.schematical.chaoscraft.network.packets.CCInventoryResyncEventPacket;
import com.schematical.chaoscraft.tickables.BaseChaosEventListener;
import com.schematical.chaoscraft.tickables.BuildyManager;
import com.schematical.chaoscraft.tickables.ChaosTeamTracker;
import com.schematical.chaoscraft.tickables.OrgPositionManager;
import com.schematical.chaoscraft.util.ChaosSettings;
import com.schematical.chaoscraft.util.SettingsMap;
import com.schematical.chaosnet.ChaosNet;
import com.schematical.chaosnet.model.ChaosNetException;
import com.schematical.chaosnet.model.Organism;
import com.schematical.chaosnet.model.TrainingRoomRole;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ServerOrgManager extends BaseOrgManager {
    protected DebugState debugState = DebugState.Off;
    protected String tmpNamespace;
    protected State state = State.Uninitialized;
    protected ServerPlayerEntity serverPlayerEntity;
    protected long spawnTime = 0;
    public ArrayList<CCClientOutputNeuronActionPacket> neuronActions = new ArrayList<CCClientOutputNeuronActionPacket>();
    private float maxLifeSeconds = 15;
    private int respawnCount = 0;
    private int longTicksSinceStateChange = 0;
    private FitnessManagerBase entityFitnessManager;
    public ChunkPos currChunkPos;
    private HashMap<String, RawOutputNeuron> rawOutputNeurons = new HashMap();

    private int ticksSinceRawOutputReceived = 1000;

    public ServerOrgManager(){

        this.attatchEventListener(new OrgPositionManager());
        //this.attatchEventListener(new ChaosTeamTracker());
        //this.attatchEventListener(new OrgDeathListener());
    }
    public void setTmpNamespace(String _tmpNamespace){
        tmpNamespace = _tmpNamespace;
    }

    public String getTmpNamespace(){
       return tmpNamespace;
    }
    @Override
    public void attachOrganism(Organism organism){
        if(!state.equals(State.PlayerAttached)){
            ChaosCraft.LOGGER.error(getCCNamespace() + " - has invalid state: " + state);
            return;
        }

        super.attachOrganism(organism);
        TrainingRoomRoleHolder trainingRoomRoleHolder = ChaosCraft.getServer().trainingRoomRoles.get(this.organism.getTrainingRoomRoleNamespace());
        roleSettings = new SettingsMap(trainingRoomRoleHolder.trainingRoomRole.getSettings());
        maxLifeSeconds = roleSettings.getInt(ChaosSettings.BASE_LIFE_SECONDS);
        setState(State.OrgAttached);
    }
    @Override
    public void attachOrgEntity(OrgEntity orgEntity){
        if(!state.equals(State.QueuedForSpawn)){
            ChaosCraft.LOGGER.error(getCCNamespace() + " - has invalid state: " + state);
            return;
        }
        super.attachOrgEntity(orgEntity);
        this.orgEntity.attachSeverOrgManager(this);
        //this.orgEntity.attachNNetRaw(this.organism.getNNetRaw());

        //entityFitnessManager = new EntityDiscoveryFitnessManager(this);
        entityFitnessManager = new EntityRuleFitnessManager( this);

        orgEntity.observableAttributeManager = new CCObservableAttributeManager(organism);
        orgEntity.setCustomName(new TranslationTextComponent(getCCNamespace()));
        orgEntity.setSpawnHash(ChaosCraft.getServer().spawnHash);
        spawnTime = orgEntity.world.getGameTime();

        setState(State.Spawned);
        triggerOnSpawned();
        this.orgEntity.addTag("role-" + this.organism.getTrainingRoomRoleNamespace());

    }

    public void setPlayerEntity(ServerPlayerEntity serverPlayerEntity){
        if(!state.equals(State.Uninitialized)){
            ChaosCraft.LOGGER.error(getCCNamespace() + " - has invalid state: " + state);
            return;
        }
        this.serverPlayerEntity = serverPlayerEntity;

        setState(State.PlayerAttached);
    }
    public void triggerOnSpawned(){
        for (BaseChaosEventListener eventListener : getEventListeners()) {
            eventListener.onServerSpawn(this);
        }
    }



    public ServerPlayerEntity getServerPlayerEntity(){
        return this.serverPlayerEntity;
    }
    public void queueOutputNeuronAction(CCClientOutputNeuronActionPacket message) {
        neuronActions.add(message);
    }
    public State getState(){
        return state;
    }
    public void queueForSpawn() {
        if(!state.equals(State.OrgAttached)){
            ChaosCraft.LOGGER.error("ServerOrgManager.state != " + State.OrgAttached);
            return;
        }

        setState(State.QueuedForSpawn);
    }
    public float getAgeSeconds(){
        return (this.orgEntity.world.getGameTime() - spawnTime)  / 20;
    }

    public void longServerTick(){
        longTicksSinceStateChange += 1;
        if(longTicksSinceStateChange > 2){

        }
        //checkChunk();
    }
    public void checkChunk(){
        if(orgEntity == null){
            return;
        }
        BlockPos orgPos = orgEntity.getPosition();


        ChunkPos newChunkPos = new ChunkPos(orgPos);
        if(
            currChunkPos == null ||
            currChunkPos.equals(newChunkPos)
        ){
            ChaosCraft.getServer().forceLoadChunk(currChunkPos, newChunkPos);
            currChunkPos = newChunkPos;

        }
    }
    public void resync(){

        CCInventoryResyncEventPacket packet = new CCInventoryResyncEventPacket(
                orgEntity.getCCNamespace(),
                orgEntity.getSelectedItemIndex(),
                orgEntity.getItemHandler()
        );
        ChaosNetworkManager.sendTo(packet, serverPlayerEntity);
    }
    public void tickOrg(){
        if( this.orgEntity.getBoundingBox() == null){
            return;
        }

       /* if (
            this.getEntity().getNNet() == null ||
            this.getEntity().getNNet().neurons == null
        ){
            ChaosCraft.LOGGER.error("Missing Entity or NNEt or something");
            return;
        }


       // checkChunk();

        if(this.neuronActions.size() > 0){
            //Iterate through and find output neurons
            List<OutputNeuron> outputs = new ArrayList<OutputNeuron>();
            Iterator<CCClientOutputNeuronActionPacket> keyIterator = this.neuronActions.iterator();
            while(keyIterator.hasNext()) {
                CCClientOutputNeuronActionPacket neuronAction = keyIterator.next();
                if (!this.getEntity().getNNet().neurons.containsKey(neuronAction.getNeuronId())) {
                    throw new ChaosNetException(this.getCCNamespace() + " is missing an OutputNeuron: " + neuronAction.getNeuronId());
                }
                if (!(this.getEntity().getNNet().neurons.get(neuronAction.getNeuronId()) instanceof  OutputNeuron)) {
                    throw new ChaosNetException(this.getCCNamespace() + " is found but not an instanceof OutputNeuron: " + neuronAction.getNeuronId());
                }
                OutputNeuron outputNeuron = (OutputNeuron)this.getEntity().getNNet().neurons.get(neuronAction.getNeuronId());
                outputNeuron.setCurrentValue(neuronAction.getValue());
                outputs.add(outputNeuron);
            }
            Iterator<OutputNeuron> iterator = outputs.iterator();
            while(iterator.hasNext()) {
                OutputNeuron outputNeuron = iterator.next();
                outputNeuron.execute();
            }
            this.neuronActions.clear();


        }*/
        if(
            this.ticksSinceRawOutputReceived < 20
        ) {
            this.getEntity().getNavigator().clearPath();
            if (state.equals(State.Spawned)) {
                initInventory();
                markTicking();
            }

            for (RawOutputNeuron rawOutputNeuron : this.rawOutputNeurons.values()) {
                if (rawOutputNeuron.getServerValue() != null) {
                    rawOutputNeuron.execute();
                    rawOutputNeuron.markExecuted();
                }
            }
        }else {
            if( this.ticksSinceRawOutputReceived == 20){
                this.getActionBuffer().resumeAction();

            }
            this.getActionBuffer().execute();
        }
        for (BaseChaosEventListener eventListener : getEventListeners()) {
            eventListener.onServerTick(this);
        }
        this.ticksSinceRawOutputReceived += 1;
    }
    public void setState(State newState){
        if(state.equals(newState)){
            throw new ChaosNetException("State is already: " + state.toString());
        }
        longTicksSinceStateChange = 0;
        state = newState;
    }

    public void markTicking() {
        if(!state.equals(ServerOrgManager.State.Spawned)){
            ChaosCraft.LOGGER.error(getCCNamespace() + " - has invalid state: " + state);
            return;
        }
        setState(State.Ticking);
    }
    public void markDead() {
        if(!state.equals(ServerOrgManager.State.Ticking)){
            ChaosCraft.LOGGER.error(getCCNamespace() + " - has invalid state: " + state);
            return;
        }
        setState(State.Dead);
        for (BaseChaosEventListener eventListener : getEventListeners()) {
            eventListener.onServerDeath(this);
        }
    }
    public float getMaxLife(){
        return maxLifeSeconds;
    }
    public void adjustMaxLife(int life) {
        maxLifeSeconds += life;
    }
    public boolean checkStatus() {
        if (
            //this.organism == null ||

            (
                !debugState.equals(DebugState.On) &&
                getAgeSeconds() > maxLifeSeconds
            )

        ) {
            //this.dropInventory();
            this.orgEntity.setHealth(-1);
            return true;
        }
        return false;
    }

    public void setDebugState(DebugState debugState) {
        this.debugState = debugState;
    }

    public void test(CCWorldEvent worldEvent) {
        this.entityFitnessManager.test(worldEvent);
    }

    public FitnessManagerBase getEntityFitnessManager() {
        return entityFitnessManager;
    }

    public RawOutputNeuron getRawOutputNeuron(String id) {
        if(!rawOutputNeurons.containsKey(id)){
            return null;
        }
        return this.rawOutputNeurons.get(id);
    }
    public void addRawOutputNeuron(RawOutputNeuron rawOutputNeuron){
        rawOutputNeuron.rawAttach(this);
        this.rawOutputNeurons.put(rawOutputNeuron.id, rawOutputNeuron);
    }

    public SettingsMap getRoleSettings() {
        return this.roleSettings;
    }

    public void handleClientOrgUpdatePacket(CCClientOrgUpdatePacket message) {
        if(CCClientOrgUpdatePacket.Action.UpdateLifeEnd.equals(message.action)){
            maxLifeSeconds += message.value;
        }else{
            throw new ChaosNetException("Invalid `CCClientOrgUpdatePacket.Action`: " + message.action);
        }
    }

    public void triggerServerActionComplete(ActionBase actionBase) {
        for (BaseChaosEventListener eventListener : getEventListeners()) {
            eventListener.onServerActionComplete(this, actionBase);
        }

    }

    public void resetTicksSinceRawOutputReceived() {
        this.ticksSinceRawOutputReceived = 0;
    }


    public enum State{
        Uninitialized,
        OrgAttached,
        Spawned,
        //ClientAttached//Dont think we need this any more
        Ticking,
        Dead,
        QueuedForSpawn,
        PlayerAttached,
    }
    public enum DebugState{
        Off,
        On
    }
}
