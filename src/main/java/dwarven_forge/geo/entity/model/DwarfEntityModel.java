package dwarven_forge.geo.entity.model;

import dwarven_forge.DwarvenForgeMain;
import dwarven_forge.entity.DwarfEntity;
import dwarven_forge.utils.GeoControllersUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Mob;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class DwarfEntityModel extends GeoModel<DwarfEntity> {
    @Override
    public ResourceLocation getModelResource(DwarfEntity geoAnimatable) {
        return ResourceLocation.fromNamespaceAndPath(DwarvenForgeMain.MOD_ID, "geo/entity/dwarf.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(DwarfEntity geoAnimatable) {
        return ResourceLocation.fromNamespaceAndPath(DwarvenForgeMain.MOD_ID, "textures/entity/dwarf/dwarf.png");
    }

    @Override
    public ResourceLocation getAnimationResource(DwarfEntity geoAnimatable) {
        return ResourceLocation.fromNamespaceAndPath(DwarvenForgeMain.MOD_ID, "animations/entity/dwarf.animation.json");
    }

    protected float getLegsSpeed(DwarfEntity entity){
        return 0.5f;
    }

    protected float getLegsAmount(DwarfEntity entity){
        return 0.4f;
    }

    protected float getArmsSpeed(DwarfEntity entity){
        return getLegsSpeed(entity);
    }

    protected float getArmsAmount(DwarfEntity entity){
        return 0.6f;
    }

    @Override
    public void setCustomAnimations(DwarfEntity dwarf, long instanceId, AnimationState<DwarfEntity> animationState) {

        GeoBone head = getAnimationProcessor().getBone("head");
        GeoBone left_leg =  getAnimationProcessor().getBone("left_leg_swing");
        GeoBone right_leg = getAnimationProcessor().getBone("right_leg_swing");
        GeoBone left_arm =  getAnimationProcessor().getBone("left_arm_swing");
        GeoBone right_arm = getAnimationProcessor().getBone("right_arm_swing");
        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);



        if (head != null) {
            head.setRotX((entityData.headPitch()) * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);

            head.setRotZ(0);
        }


        if(left_arm!=null && right_arm!=null && left_leg!=null && right_leg!=null){
            left_leg.setRotX((float)   -(Math.sin(animationState.getLimbSwing()*getLegsSpeed(dwarf))*(animationState.getLimbSwingAmount()*getLegsAmount(dwarf))));
            right_leg.setRotX((float)   (Math.sin(animationState.getLimbSwing()*getLegsSpeed(dwarf))*(animationState.getLimbSwingAmount()*getLegsAmount(dwarf))));

            if(dwarf.isAggressive()){
                if(dwarf.getMainArm() == HumanoidArm.LEFT){
                    right_arm.setRotX(GeoControllersUtil.getLimbSwing(animationState, -0.5f, 0.9f, getArmsSpeed(dwarf), getArmsAmount(dwarf),true));
                    left_arm.setRotX(GeoControllersUtil.getLimbSwing(animationState,  -0.3f, 0.2f, getArmsSpeed(dwarf), getArmsAmount(dwarf),false) + 1.5f);
                } else {
                    right_arm.setRotX(GeoControllersUtil.getLimbSwing(animationState, -0.2f, 0.3f, getArmsSpeed(dwarf), getArmsAmount(dwarf),true) + 1.5f);
                    left_arm.setRotX(GeoControllersUtil.getLimbSwing(animationState,  -0.9f, 0.5f, getArmsSpeed(dwarf), getArmsAmount(dwarf),false));
                }


            } else {
                left_arm.setRotX(GeoControllersUtil.getLimbSwing(animationState,  -0.9f, 0.5f, getArmsSpeed(dwarf), getArmsAmount(dwarf),false) + (dwarf.getMainArm() == HumanoidArm.LEFT && dwarf.isAggressive()? 1: 0));
                right_arm.setRotX(GeoControllersUtil.getLimbSwing(animationState, -0.5f, 0.9f, getArmsSpeed(dwarf), getArmsAmount(dwarf),true) + (dwarf.getMainArm() == HumanoidArm.RIGHT && dwarf.isAggressive()? 1: 0));
            }

            right_arm.setRotZ(0);
            left_arm.setRotZ(0);
        }


    }

    public static <T extends Mob> void swingWeaponDown(GeoBone arm1, GeoBone arm2, DwarfEntity dwarf, float v1, float v2) {
        float sin1 = Mth.sin(v1 * (float)Math.PI);
        float sin2 = Mth.sin((1.0F - (1.0F - v1) * (1.0F - v1)) * (float)Math.PI);
        arm1.setRotZ(0.0F);
        arm2.setRotZ(0.0F);
        arm1.setRotY(0.15707964F);
        arm2.setRotY(-0.15707964F);
        if (dwarf.getMainArm() == HumanoidArm.RIGHT) {
            arm1.setRotX(-1.8849558F + Mth.cos(v2 * 0.09F) * 0.15F);;
            arm2.setRotX(-0.0F + Mth.cos(v2 * 0.19F) * 0.5F);
            arm1.setRotX(arm1.getRotX() +(sin1 * 2.2F - sin2 * 0.4F));
            arm2.setRotX(arm2.getRotX()+(sin1 * 1.2F - sin2 * 0.4F));
        } else {
            arm1.setRotX(-0.0F + Mth.cos(v2 * 0.19F) * 0.5F);
            arm2.setRotX(-1.8849558F + Mth.cos(v2 * 0.09F) * 0.15F);
            arm1.setRotX(arm1.getRotX()+(sin1 * 1.2F - sin2 * 0.4F));
            arm2.setRotX(arm2.getRotX()+(sin1 * 2.2F - sin2 * 0.4F));
        }

        bobArms(arm1, arm2, v2);
    }


    public static void bobArms(GeoBone arm1, GeoBone arm2, float v) {
        bobModelPart(arm1, v, 1.0F);
        bobModelPart(arm2, v, -1.0F);
    }

    public static void bobModelPart(GeoBone part, float v1, float v2) {
        part.setRotZ(part.getRotZ()+(v2 * (Mth.cos(v1 * 0.09F) * 0.05F + 0.05F)));
        part.setRotX(part.getRotX()+(v2 * Mth.sin(v1 * 0.067F) * 0.05F));
    }

}
