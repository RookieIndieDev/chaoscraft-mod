package com.schematical.chaoscraft.tickables;

import com.schematical.chaoscraft.ChaosCraft;
import com.schematical.chaoscraft.events.CCWorldEvent;
import com.schematical.chaoscraft.events.EntityFitnessScoreEvent;
import com.schematical.chaoscraft.server.ServerOrgManager;
import com.schematical.chaoscraft.tileentity.BuildAreaMarkerTileEntity;
import com.schematical.chaoscraft.util.BuildArea;

public class OrgDeathListener extends BaseChaosEventListener {

    private CCWorldEvent buildEvent;
    private BuildArea orgBuildArea;

    public OrgDeathListener(){
        this.buildEvent = new CCWorldEvent(CCWorldEvent.Type.BUILD_COMPLETE);
    }

    public void onServerDeath(ServerOrgManager baseOrgManager) {
        if (ChaosCraft.buildAreas.size() > 0) {
            for (BuildArea buildArea : ChaosCraft.buildAreas) {
                if (buildArea.getCurrentServerOrgManager() != null) {
                    if (buildArea.getCurrentServerOrgManager().getCCNamespace().equals(baseOrgManager.getCCNamespace())) {
                        orgBuildArea = buildArea;
                        break;
                    }
                }
            }

            buildEvent.entity = baseOrgManager.getEntity();
            orgBuildArea.getBlocks(orgBuildArea.getBuildAreaEntity().getPos());
            buildEvent.amount = (int) orgBuildArea.getScore();
            buildEvent.eventType = CCWorldEvent.Type.BUILD_COMPLETE;
            baseOrgManager.test(buildEvent);
            orgBuildArea.resetScore();
            orgBuildArea.resetBlockPlacedCount();
            BuildAreaMarkerTileEntity.resetBuildArea(orgBuildArea.getBuildAreaEntity().getPos(), orgBuildArea.getBuildAreaEntity().getWorld());
        }
    }
}
