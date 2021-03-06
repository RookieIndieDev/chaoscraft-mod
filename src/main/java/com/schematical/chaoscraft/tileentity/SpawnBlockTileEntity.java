package com.schematical.chaoscraft.tileentity;
import java.util.ArrayList;
import java.util.Iterator;
import javax.annotation.Nullable;

import com.schematical.chaoscraft.blocks.ChaosBlocks;
import com.schematical.chaoscraft.server.ServerOrgManager;
import com.schematical.chaosnet.model.ChaosNetException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;


public class SpawnBlockTileEntity  extends TileEntity implements ITickableTileEntity {

    private String spawnPointId = "default";
    private int maxLivingEntities = 1;
    private int livingEntityCount = 0;
    private String matchId = "default";
    protected ArrayList<ServerOrgManager> entities = new ArrayList<ServerOrgManager>();
    public SpawnBlockTileEntity() {
            super(ChaosTileEntity.SPAWN_TILE.get());
        }

    @Override
    public void tick() {
        if(!ChaosBlocks.spawnBlocks.contains(this.getPos())) {
            ChaosBlocks.spawnBlocks.add(this.getPos());
            //ChaosCraft.LOGGER.info("SpawnBlock at " + this.getPos().getX() + ", " + this.getPos().getY() + ", " + this.getPos().getZ());
        }
        if(maxLivingEntities < 0){

            return;//Just keep on spawning
        }
        livingEntityCount = 0;
        Iterator<ServerOrgManager> iterator = entities.iterator();
        while (iterator.hasNext()) {
            ServerOrgManager serverOrgManager = iterator.next();
            if(serverOrgManager.getEntity().isAlive()){
                livingEntityCount += 1;
            }else{
                iterator.remove();
            }
        }


    }
    public int getMaxLivingEntities(){
        return maxLivingEntities;
    }
    public void setMaxLivingEntities(int maxLivingEntities){
        this.maxLivingEntities = maxLivingEntities;
        this.markForUpdate();
    }
    public boolean canSpawn(){
         if(maxLivingEntities < 0){
            return true;
        }
        if(maxLivingEntities > livingEntityCount){
            return true;
        }
        return false;
    }


    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        if (compound.contains("spawnPointId")) {
           spawnPointId = compound.getString("spawnPointId");
        }
        if (compound.contains("maxLivingEntities")) {
            maxLivingEntities = compound.getInt("maxLivingEntities");
        }

    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        compound.putString("spawnPointId", this.spawnPointId);
        compound.putInt("maxLivingEntities", this.maxLivingEntities);
        return compound;
    }

    @Override
    public void onDataPacket(net.minecraft.network.NetworkManager net, net.minecraft.network.play.server.SUpdateTileEntityPacket pkt){
        //super.onDataPacket(net, pkt);
        this.read(pkt.getNbtCompound());
    }

    /**
     * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For
     * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
     */
    @Nullable
   // @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        //return new SUpdateTileEntityPacket(this.pos, 99, this.getUpdateTag());
        return new SUpdateTileEntityPacket(pos, 99, this.write(new CompoundNBT()));
    }

    /**
     * Get an NBT compound to sync to the client with SPacketChunkData, used for initial loading of the chunk or when
     * many blocks change at once. This compound comes back to you clientside in
     */
    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }


    public String getSpawnPointId() {
        return spawnPointId;
    }
    public String getMatchId(){
        return matchId;
    }
    public void setMatchId(String matchId){
        this.matchId = matchId;
        this.markForUpdate();
    }
    public void setSpawnPointId(String spawnPointId){
        setSpawnPointId(spawnPointId, false);
    }
    public void setSpawnPointId(String spawnPointId, boolean setSiblings) {
        this.spawnPointId = spawnPointId;
        this.markForUpdate();
        if(setSiblings){
            for(int x = -1; x <= 1; x += 2){
                for(int z = -1; z <= 1; z += 2) {
                   /* BlockPos blockPos = new BlockPos(
                    this.getPos().getX() + x,
                       this.getPos().getY(),
                    this.getPos().getZ() + z
                    );*/
                    BlockPos blockPos = this.getPos().add(
                        new Vec3i(
                            x,
                            0,
                            z
                        )
                    );
                    TileEntity tileentity = getWorld().getTileEntity(blockPos);
                    if(
                        tileentity != null &&
                        tileentity instanceof SpawnBlockTileEntity
                    ){
                        SpawnBlockTileEntity spawnBlockTileEntity = (SpawnBlockTileEntity) tileentity;
                        if(!spawnBlockTileEntity.getSpawnPointId().equals(spawnPointId)){
                            spawnBlockTileEntity.setSpawnPointId(spawnPointId, setSiblings);
                        }
                    }
                }
            }
        }
    }
    private void markForUpdate() {
        this.markDirty();
        this.getWorld().notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    public void addSpawnedEntity(ServerOrgManager serverOrgManager) {
        if(
                maxLivingEntities > 0 &&
                entities.size() >= maxLivingEntities
        ){
            throw new ChaosNetException("Invalid amount of entities added: " + entities.size() + " >= " + maxLivingEntities);
        }
        entities.add(serverOrgManager);
        livingEntityCount += 1;
    }
}
