package com.schematical.chaoscraft.server.spawnproviders;

import com.schematical.chaoscraft.ChaosCraft;
import com.schematical.chaoscraft.blocks.ChaosBlocks;
import com.schematical.chaoscraft.blocks.SpawnBlock;
import com.schematical.chaoscraft.server.ServerOrgManager;
import com.schematical.chaoscraft.tileentity.SpawnBlockTileEntity;
import com.schematical.chaoscraft.util.BuildArea;
import com.schematical.chaoscraft.util.ChaosSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;

public class SpawnBlockPosProvider implements iServerSpawnProvider {

    @Override
    public BlockPos getSpawnPos(ServerOrgManager serverOrgManager){

        if(ChaosBlocks.spawnBlocks.size() == 0){
            ChaosCraft.LOGGER.error("Cannot find any spawnBlocks");
            return null;
        }
        ArrayList<BlockPos> spawnBlocks = new ArrayList<>();
        String spawnPointId = serverOrgManager.getRoleSettings().getString(ChaosSettings.SPAWN_POINT_ID);
        for (BlockPos pos : ChaosBlocks.spawnBlocks) {
            SpawnBlockTileEntity spawnBlockTileEntity = (SpawnBlockTileEntity)ChaosCraft.getServer().server.getWorld(DimensionType.OVERWORLD).getTileEntity(pos);
            if(
                spawnBlockTileEntity != null &&
                spawnBlockTileEntity.getSpawnPointId().equals(spawnPointId) &&
                spawnBlockTileEntity.canSpawn()
            ){
                spawnBlocks.add(pos);
            }
        }
        if(spawnBlocks.size() == 0){
            return null;
        }
        int i = (int)Math.floor(Math.random() * spawnBlocks.size());
        SpawnBlockTileEntity spawnBlockTileEntity = (SpawnBlockTileEntity)ChaosCraft.getServer().server.getWorld(DimensionType.OVERWORLD).getTileEntity(spawnBlocks.get(i));
        double closestBuildAreaDist = ChaosCraft.buildAreas.get(0).getBuildAreaEntity().getDistanceSq(spawnBlockTileEntity.getPos().getX(), spawnBlockTileEntity.getPos().getY(), spawnBlockTileEntity.getPos().getZ());
        BuildArea closestBuildArea = ChaosCraft.buildAreas.get(0);
        for(BuildArea buildArea : ChaosCraft.buildAreas){
            double dist = buildArea.getBuildAreaEntity().getDistanceSq(spawnBlockTileEntity.getPos().getX(), spawnBlockTileEntity.getPos().getY(), spawnBlockTileEntity.getPos().getZ());
            if(dist < closestBuildAreaDist) {
                closestBuildAreaDist = dist;
                closestBuildArea = buildArea;
            }
        }
        closestBuildArea.assignCurrentOrgManager(serverOrgManager);
        spawnBlockTileEntity.addSpawnedEntity(serverOrgManager);
        return spawnBlocks.get(i).add(new Vec3i(0,2,0));
    }

}
