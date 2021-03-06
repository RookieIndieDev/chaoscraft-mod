package com.schematical.chaoscraft.ai.inputs.targetcandidate;

import com.schematical.chaoscraft.ai.inputs.BaseTargetInputNeuron;
import com.schematical.chaoscraft.entities.OrgEntity;
import com.schematical.chaoscraft.services.targetnet.ScanEntry;
import com.schematical.chaoscraft.services.targetnet.ScanManager;
import com.schematical.chaoscraft.util.TargetHelper;

/**
 * Created by user1a on 12/8/18.
 */
public class TargetCandidateDistToTargetSlotInput extends BaseTargetInputNeuron {



    @Override
    public float evaluate(){
        ScanManager scanManager =  ((OrgEntity)this.getEntity()).getClientOrgManager().getScanManager();
        ScanEntry scanEntry = scanManager.getFocusedScanEntry();
        if(!targetSlot.hasTarget()){
            return getCurrentValue();
        }

        Double dist = TargetHelper.getDistDelta(scanEntry.getPosition(),  getTarget().getTargetPositionCenter());
        if(dist != null) {
            setCurrentValue( dist.floatValue() / scanManager.getScanInstance().getRange());//This can go out of bounds(over 100%)
        }



        return getCurrentValue();
    }


}
