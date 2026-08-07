package dwarven_forge;

import dwarven_forge.item.RuneItem;
import dwarven_forge.registry.DwarvenForge_Items;
import dwarven_forge.utils.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// Game-bus (NeoForge.EVENT_BUS) handlers, kept separate from DwarvenForgeMain's mod-bus-only
// setEntitiesAttributes - see the comment on DwarvenForgeMain for why they can't share a class.
@EventBusSubscriber(modid = DwarvenForgeMain.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class DwarvenForgeEvents {

    @SubscribeEvent
    public static void onAttributeModifierEvent(ItemAttributeModifierEvent event)
    {
        if(event.getItemStack().getItem() instanceof ArmorItem armor){
            CompoundTag customTag = event.getItemStack().getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();

            if(customTag.contains("Runes")){
                setRuneAttributes(event, armor, customTag.getCompound("Runes"));
            }
        }
    }

    public static void setRuneAttributes(ItemAttributeModifierEvent event, ArmorItem armor, CompoundTag runesTag){
        // Runes used to only apply when Forge's ItemAttributeModifierEvent fired for the armor's own
        // slot (event.getSlotType() == armor.getEquipmentSlot()). That per-slot firing is gone in
        // NeoForge 1.21.1 - instead, addModifier is told explicitly which EquipmentSlotGroup the
        // modifier belongs to, which achieves the same "only active while worn in its own slot" result.
        EquipmentSlotGroup slotGroup = EquipmentSlotGroup.bySlot(armor.getEquipmentSlot());

        for(String runeName: runesTag.getAllKeys()){
            CompoundTag runeTag = runesTag.getCompound(runeName);
            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(runeTag.getString("Item")));

            if(item instanceof RuneItem rune){
                // The stored per-application UUID keeps each rune instance's modifier unique,
                // same role it played as the AttributeModifier's identity in 1.20.1.
                ResourceLocation modifierId = ResourceLocation.fromNamespaceAndPath(DwarvenForgeMain.MOD_ID, "rune_" + runeTag.getUUID("UUID"));

                AttributeModifier modifier = new AttributeModifier(modifierId, rune.modifier.amount(), rune.modifier.operation());

                event.addModifier(rune.attribute, modifier, slotGroup);
            }
        }
    }


    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event)
    {

    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event){

        //Magic Resistance
        if(event.getSource().is(DamageTypes.MAGIC) || event.getSource().is(DamageTypes.WITHER) || event.getSource().is(DamageTypes.DRAGON_BREATH) ||
                event.getSource().is(DamageTypes.INDIRECT_MAGIC) || event.getSource().is(DamageTypes.WITHER_SKULL) || event.getSource().is(DamageTypes.THORNS)){

            event.setNewDamage(event.getNewDamage()*(1.0f- MiscUtil.getArmorMagicResistance(event.getEntity())));
        }

        //Explosion Resistance
        if(event.getSource().is(DamageTypes.EXPLOSION) || event.getSource().is(DamageTypes.PLAYER_EXPLOSION)){

            event.setNewDamage(event.getNewDamage()*(1.0f- MiscUtil.getArmorExplosionResistance(event.getEntity())));
        }
    }

    @SubscribeEvent
    public static void onInteractEvent(BlockEvent.BlockToolModificationEvent event){


        if(event.getItemAbility().equals(ItemAbilities.HOE_TILL) && event.getHeldItemStack().is(DwarvenForge_Items.DWARVEN_HOE.get())){
            BlockPos blockPos =event.getPos();
            Player player =event.getPlayer();
            LevelAccessor world =event.getLevel();
            UseOnContext context =event.getContext();


            if(player!=null && !player.isShiftKeyDown()){
//                useOn(context);

            }



        }



    }

    @SubscribeEvent
    public static void onBreakEvent(BlockEvent.BreakEvent event){

        if(event.getPlayer().getMainHandItem().is(DwarvenForge_Items.DWARVEN_PICKAXE.get())){
            breakBlockBelow(event, BlockTags.MINEABLE_WITH_PICKAXE);
        }

        if(event.getPlayer().getMainHandItem().is(DwarvenForge_Items.DWARVEN_SHOVEL.get())){
            breakBlockBelow(event, BlockTags.MINEABLE_WITH_SHOVEL);
        }

    }

    //To destroy the block below it
    public static void breakBlockBelow(BlockEvent.BreakEvent event, TagKey<Block> tag){
        BlockPos blockPos =event.getPos();
        Player player =event.getPlayer();
        LevelAccessor world =event.getLevel();

        if(!player.isCreative() && !player.isShiftKeyDown()){

            if(world.getBlockState(blockPos).is(tag) && world.getBlockState(blockPos.below()).is(tag)){
                world.destroyBlock(event.getPos().below(), true);
            }
        }
    }
}
