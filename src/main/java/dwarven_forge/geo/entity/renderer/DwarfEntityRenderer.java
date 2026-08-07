package dwarven_forge.geo.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dwarven_forge.DwarvenForgeMain;
import dwarven_forge.entity.DwarfEntity;
import dwarven_forge.geo.entity.model.DwarfEntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;
import software.bernie.geckolib.renderer.specialty.DynamicGeoEntityRenderer;

import java.util.Objects;

public class DwarfEntityRenderer extends DynamicGeoEntityRenderer<DwarfEntity> {
    private static final String LEFT_HAND = "left_hand";
    private static final String RIGHT_HAND = "right_hand";
    protected ItemStack mainHandItem;
    protected ItemStack offhandItem;

    public DwarfEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DwarfEntityModel());

        this.shadowRadius = 0.5f;

        //Item on hand renderer
        addRenderLayer(new BlockAndItemGeoLayer<>(this) {
            @Nullable
            @Override
            protected ItemStack getStackForBone(GeoBone bone, DwarfEntity animatable) {
                // Retrieve the items in the entity's hands for the relevant bone
                return switch (bone.getName()) {
                    case LEFT_HAND -> animatable.isLeftHanded() ?
                            DwarfEntityRenderer.this.mainHandItem : DwarfEntityRenderer.this.offhandItem;
                    case RIGHT_HAND -> animatable.isLeftHanded() ?
                            DwarfEntityRenderer.this.offhandItem : DwarfEntityRenderer.this.mainHandItem;
                    default -> null;
                };
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, DwarfEntity animatable) {
                // Apply the camera transform for the given hand
                return switch (bone.getName()) {
                    case LEFT_HAND, RIGHT_HAND -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                    default -> ItemDisplayContext.NONE;
                };
            }

            // Do some quick render modifications depending on what the item is
            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, DwarfEntity dwarf,
                                              MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                if(dwarf.isSleeping()){
                    return;
                }


                if (stack == DwarfEntityRenderer.this.mainHandItem) {
                    poseStack.mulPose(Axis.XP.rotationDegrees(-90f));

                    if (stack.getItem() instanceof ShieldItem)
                        poseStack.translate(0, 0.125, -0.25);
                }
                else if (stack == DwarfEntityRenderer.this.offhandItem) {
                    poseStack.mulPose(Axis.XP.rotationDegrees(-90f));

                    if (stack.getItem() instanceof ShieldItem) {
                        poseStack.translate(0, 0.125, 0.25);
                        poseStack.mulPose(Axis.YP.rotationDegrees(180));
                    }
                }

                super.renderStackForBone(poseStack, bone, stack, dwarf, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
    }


    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, DwarfEntity dwarf, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        if (!isReRender && (widthScale != 1.0F || heightScale != 1.0F)) {
            poseStack.scale(widthScale, heightScale, widthScale);
        }

        float normal = 1F;
        if (dwarf.isBaby()) {
            normal *= 0.5F;
            this.shadowRadius = 0.25F;
        } else {
            this.shadowRadius = 0.5F;
        }

        poseStack.scale(normal, normal, normal);
    }

    @Override
    public void preRender(PoseStack poseStack, DwarfEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);

        this.mainHandItem = animatable.getMainHandItem();
        this.offhandItem = animatable.getOffhandItem();
    }

    protected @Nullable ResourceLocation getTextureOverrideForBone(GeoBone bone, DwarfEntity animatable, float partialTick) {

        //Clothes
        if((bone.getName().equals("clothes") || bone.getName().equals("helmet")
                || bone.getName().equals("left_sleeve") || bone.getName().equals("right_sleeve")
                || bone.getName().equals("right_leg_swing") || bone.getName().equals("left_leg_swing"))){
            if(animatable.isWarrior())
                return createLocationForWarrior();
            else if(!animatable.getVillagerData().getProfession().equals(VillagerProfession.NONE))
                return createLocationForProfession(animatable.getVillagerData().getProfession());
        }

        //Beard/Hair
        if((bone.getName().equals("beard") || bone.getName().equals("hair"))){
            return animatable.isBaby()? null: createLocationForBeard(animatable.getBeardId());
        }

        if((bone.getName().equals("level")) && !animatable.isWarrior()) {
            return createLocationForLevel(animatable.getVillagerData().getLevel());
        }

        return null;
    }

    private static ResourceLocation createLocationForWarrior(){
        return ResourceLocation.fromNamespaceAndPath(DwarvenForgeMain.MOD_ID,
                "textures/entity/dwarf/profession/"+ "warrior" +".png");
    }

    private static ResourceLocation createLocationForProfession(VillagerProfession profession){
        return ResourceLocation.fromNamespaceAndPath(DwarvenForgeMain.MOD_ID,
                "textures/entity/dwarf/profession/"+profession.name()+".png");
    }

    private static ResourceLocation createLocationForBeard(String id){
        if(Objects.equals(id, "black") || Objects.equals(id, "red") || Objects.equals(id, "blonde") || Objects.equals(id, "brown"))
            return ResourceLocation.fromNamespaceAndPath(DwarvenForgeMain.MOD_ID,
                "textures/entity/dwarf/beard/"+id+".png");

        //Default
        return ResourceLocation.fromNamespaceAndPath(DwarvenForgeMain.MOD_ID,
                "textures/entity/dwarf/beard/"+"black"+".png");
    }

    private static ResourceLocation createLocationForLevel(int level){

        String id =
                switch (level) {
                    case 1 -> "stone";
                    case 2 -> "iron";
                    case 3 -> "gold";
                    case 4 -> "emerald";
                    case 5 -> "diamond";
                    default -> "stone";
                };


        return ResourceLocation.fromNamespaceAndPath(DwarvenForgeMain.MOD_ID,
                "textures/entity/dwarf/profession_level/"+id+".png");
    }


}
