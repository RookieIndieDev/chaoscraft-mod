package com.schematical.chaoscraft.ai.outputs;

import com.schematical.chaoscraft.ChaosCraft;
import com.schematical.chaoscraft.ai.OutputNeuron;
import com.schematical.chaoscraft.ai.biology.TargetSlot;
import com.schematical.chaosnet.model.ChaosNetException;
import net.minecraft.util.math.Vec3d;
import org.json.simple.JSONObject;

/**
 * Created by user1a on 12/10/18.
 */
public class LookAtTargetOutput extends OutputNeuron {

    public TargetSlot targetSlot;
    public float evaluate() {
        if (getHasBeenEvaluated()) {
            return getCurrentValue();
        }

        if(!targetSlot.hasTarget()){
            return getCurrentValue();//targetSlot.populateDebug();
        }
        return super.evaluate();
    }
    public void execute() {

        if(!targetSlot.hasTarget()){
            return;
        }

        Vec3d pos = targetSlot.getTargetPositionCenter();

        this.nNet.entity.setDesiredLookPosition(pos);




        //!!!NOT SURE IF ANYTHING BELOW THIS WORKS
        Double deltaPitch = targetSlot.getPitchDelta();

        if(deltaPitch > 5d){
            deltaPitch = 5d;
        }else if(deltaPitch < -5d){
            deltaPitch = -5d;
        }

        this.nNet.entity.setDesiredPitch(this.nNet.entity.rotationPitch + deltaPitch);

    }

    @Override
    public void parseData(JSONObject jsonObject){
        super.parseData(jsonObject);
        if(jsonObject.get("targetSlotId") != null){
            targetSlot = (TargetSlot)nNet.getBiology(jsonObject.get("targetSlotId").toString());
        }

    }
}
