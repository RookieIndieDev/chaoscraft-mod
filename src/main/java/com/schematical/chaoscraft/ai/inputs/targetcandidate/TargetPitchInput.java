package com.schematical.chaoscraft.ai.inputs.targetcandidate;

import com.schematical.chaoscraft.ChaosCraft;
import com.schematical.chaoscraft.ai.InputNeuron;
import com.schematical.chaoscraft.ai.biology.TargetSlot;
import com.schematical.chaoscraft.ai.inputs.BaseTargetInputNeuron;
import com.schematical.chaoscraft.entities.OrgEntity;
import com.schematical.chaoscraft.events.CCWorldEvent;
import net.minecraft.util.math.Vec3d;
import org.json.simple.JSONObject;

/**
 * Created by user1a on 12/8/18.
 */
public class TargetPitchInput extends BaseTargetInputNeuron {
    private static final float  PITCH_DEGREES = 180f;

    @Override
    public float evaluate(){


        Double degrees = getTarget().getPitchDelta();
        if(degrees != null) {
            setCurrentValue( degrees.floatValue() / PITCH_DEGREES);
        }

        return getCurrentValue();
    }


}
