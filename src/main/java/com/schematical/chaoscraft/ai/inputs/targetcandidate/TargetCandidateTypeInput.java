package com.schematical.chaoscraft.ai.inputs.targetcandidate;

import com.schematical.chaoscraft.ai.CCAttributeId;
import com.schematical.chaoscraft.ai.InputNeuron;
import com.schematical.chaoscraft.ai.memory.BlockStateMemoryBufferSlot;
import com.schematical.chaoscraft.entities.OrgEntity;
import com.schematical.chaoscraft.services.targetnet.ScanEntry;
import com.schematical.chaoscraft.services.targetnet.ScanManager;
import com.schematical.chaosnet.model.ChaosNetException;
import org.json.simple.JSONObject;

/**
 * Created by user1a on 12/8/18.
 */
public class TargetCandidateTypeInput extends InputNeuron {
    public String attributeId;
    public String attributeValue;


    @Override
    public float evaluate(){
        ScanManager scanManager =  ((OrgEntity)this.getEntity()).getClientOrgManager().getScanManager();
        ScanEntry scanEntry = scanManager.getFocusedScanEntry();

        switch(attributeId) {
            case (CCAttributeId.BLOCK_ID):
                if (
                        scanEntry.atts.resourceId.equals(attributeValue)
                ) {
                    setCurrentValue(1);
                }
                break;
            case (CCAttributeId.ENTITY_ID):
                if (
                    scanEntry.atts.resourceId.equals(attributeValue)
                ) {
                    setCurrentValue(1);
                }
                break;
            case(CCAttributeId.OWNER_ENTITY):
                if(scanEntry.getTargetBlockPos() != null){
                    BlockStateMemoryBufferSlot blockStateMemoryBufferSlot = this.getEntity().getClientOrgManager().getBlockStateMemory().get(scanEntry.getTargetBlockPos());
                    if(blockStateMemoryBufferSlot != null){
                        if(blockStateMemoryBufferSlot.ownerEntityId == this.getEntity().getEntityId()){
                            setCurrentValue(1);
                        }
                    }

                }
                break;
        }


                return getCurrentValue();
    }
    public void parseData(JSONObject jsonObject){
        super.parseData(jsonObject);

        attributeId = jsonObject.get("attributeId").toString();
        attributeValue = jsonObject.get("attributeValue").toString();


    }
    public String toLongString(){
        String response = super.toLongString();
        response += " " + this.attributeId + " " + this.attributeValue + "=> " + this.getPrettyCurrValue();
        return response;

    }


}
