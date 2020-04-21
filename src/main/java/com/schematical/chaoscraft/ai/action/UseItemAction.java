package com.schematical.chaoscraft.ai.action;

import com.schematical.chaoscraft.ChaosCraft;
import com.schematical.chaoscraft.entities.OrgEntity;
import com.schematical.chaoscraft.events.CCWorldEvent;
import com.schematical.chaoscraft.server.ServerOrgManager;
import com.schematical.chaoscraft.util.ChaosTarget;
import com.schematical.chaoscraft.util.ChaosTargetItem;
import com.schematical.chaosnet.model.ChaosNetException;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;

public class UseItemAction extends NavigateToAction{
    private int heldCount = -1;
    @Override
    protected void _tick() {
        ItemStack itemStack = getOrgEntity().getHeldItem(Hand.MAIN_HAND);
        Item heldItem = itemStack.getItem();
        tickLook();



        if(heldItem instanceof ShootableItem){
            if(
                getTarget().isEntityLookingAt(getOrgEntity())
            ) {

                tickShootItem();
                return;
            }
            tickNavigate();
            return;
        }


        if(
            !getTarget().canEntityTouch(getOrgEntity()) &&
            !getTarget().isEntityLookingAt(getOrgEntity())
        ){
            tickNavigate();
            return;
        }
        tickArrived();
        if(!validateTargetItem(getOrgEntity(),  new ChaosTargetItem(getOrgEntity().getSelectedItemIndex()))){
            throw new ChaosNetException("Invalid Target");
        }
        ActionResult<ItemStack> rcResult = heldItem.onItemRightClick(
                getOrgEntity().world,
                getOrgEntity().getPlayerWrapper(),
                Hand.MAIN_HAND
        );
        if(rcResult.getType().equals(ActionResultType.SUCCESS)){
            ChaosCraft.LOGGER.debug(getOrgEntity().getCCNamespace() + " successfully rightClicked " + heldItem.getRegistryName().getNamespace());
        }else if(rcResult.getType().equals(ActionResultType.SUCCESS)){
            ChaosCraft.LOGGER.debug(getOrgEntity().getCCNamespace() + " failed to rightClick " + heldItem.getRegistryName().getNamespace());
        }

        BlockRayTraceResult rayTraceResult = getOrgEntity().rayTraceBlocks(getOrgEntity().REACH_DISTANCE);
        if(
            rayTraceResult == null
        ){
            markFailed();
            return;
        }


        if((heldItem instanceof BlockItem)){
            /*throw new ChaosNetException*/ChaosCraft.LOGGER.error("Invalid action. `heldItem` instanceof `BlockItem`: " + heldItem.getRegistryName().toString());
        }
        ItemUseContext context = new ItemUseContext(
                getOrgEntity().getPlayerWrapper(),
                Hand.MAIN_HAND,
                rayTraceResult
        );
        ActionResultType result = heldItem.onItemUse(
                context
        );
        if(result.equals(ActionResultType.SUCCESS)) {
            ChaosCraft.LOGGER.debug(getOrgEntity().getCCNamespace() + " successfully used " + heldItem.getRegistryName().toString());
        }else if(result.equals(ActionResultType.FAIL)){
            ChaosCraft.LOGGER.debug(getOrgEntity().getCCNamespace() + " failed to use " + heldItem.getRegistryName().toString());
        }
    }

    private void tickShootItem() {
        ItemStack itemStack = getOrgEntity().getHeldItem(Hand.MAIN_HAND);
        Item heldItem = itemStack.getItem();
        if(heldCount == -1) {
            ActionResult<ItemStack> rcResult = heldItem.onItemRightClick(
                    getOrgEntity().world,
                    getOrgEntity().getPlayerWrapper(),
                    Hand.MAIN_HAND
            );
            if (rcResult.getType().equals(ActionResultType.SUCCESS)) {
                ChaosCraft.LOGGER.debug(getOrgEntity().getCCNamespace() + " successfully rightClicked " + heldItem.getRegistryName().getNamespace());
            } else if (rcResult.getType().equals(ActionResultType.SUCCESS)) {
                ChaosCraft.LOGGER.debug(getOrgEntity().getCCNamespace() + " failed to rightClick " + heldItem.getRegistryName().getNamespace());
            }
        }
        heldCount += 1;
        if(heldCount > 20) {
            itemStack.onPlayerStoppedUsing(
                getOrgEntity().world,
                getOrgEntity().getPlayerWrapper(),
                heldCount
            );
            heldCount = -1;
            this.markCompleted();
            CCWorldEvent worldEvent = new CCWorldEvent(CCWorldEvent.Type.ITEM_USED);
            worldEvent.item = heldItem;
            ((ServerOrgManager)getActionBuffer().getOrgManager()).test(worldEvent);
        }
    }



    public static boolean validateTargetItem(OrgEntity orgEntity, ChaosTargetItem chaosTargetItem) {
        if(chaosTargetItem.getInventorySlot() == null){
            return false;
        }
        ItemStack itemStack = orgEntity.getItemHandler().getStackInSlot(chaosTargetItem.getInventorySlot());
        if(
            itemStack == null ||
            itemStack.isEmpty()
        ){
            return false;
        }

        if((itemStack.getItem() instanceof BlockItem)){
            return false;
        }
        return true;
    }
    public static boolean validateTargetAndItem(OrgEntity orgEntity, ChaosTarget chaosTarget, ChaosTargetItem chaosTargetItem){
        if(
                validateTarget( orgEntity, chaosTarget) &&
                        validateTargetItem( orgEntity, chaosTargetItem)
        ) {
            return true;
        }else{
            return false;
        }
    }
}