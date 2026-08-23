package dwarven_forge.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import dwarven_forge.DwarvenForgeMain;
import dwarven_forge.ai.DwarfTrades;
import dwarven_forge.ai.DwarvenForge_Schedules;
import dwarven_forge.ai.behavior.DwarfGoalPackages;
import dwarven_forge.registry.DwarvenForge_Entities;
import dwarven_forge.registry.DwarvenForge_Items;
import dwarven_forge.registry.DwarvenForge_Sensors;
import dwarven_forge.registry.DwarvenForge_Sounds;
import dwarven_forge.utils.GeoControllersUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.npc.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DwarfEntity extends Villager implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<String> BEARD_ID = SynchedEntityData.defineId(DwarfEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> IS_WARRIOR = SynchedEntityData.defineId(DwarfEntity.class, EntityDataSerializers.BOOLEAN);


    public DwarfEntity(EntityType<? extends Villager> type, Level world) {
        super(type, world);

        this.setBeardId("black");
    }

    public static AttributeSupplier.Builder setAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.5F).add(Attributes.FOLLOW_RANGE, 48.0F).add(Attributes.ATTACK_DAMAGE, 4);
    }

    // Note: no getStandingEyeHeight override needed anymore - vanilla's default EntityDimensions
    // already computes eye height as height*0.85 for standing and uses a fixed 0.2 for sleeping,
    // which is exactly what this override used to hand-compute. Entity.getEyeHeight(Pose) and
    // LivingEntity.getDimensions(Pose) are both `final` in 1.21.1, so this hook no longer exists.

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor accessor, @NotNull DifficultyInstance difficultyInstance, @NotNull MobSpawnType type, @Nullable SpawnGroupData spawnGroupData) {
        //Chooses hair/beard randomly
        switch (random.nextIntBetweenInclusive(0,3)){
            case 0:
                this.setBeardId("black");
                break;
            case 1:
                this.setBeardId("red");
                break;
            case 2:
                this.setBeardId("blonde");
                break;
            case 3:
                this.setBeardId("brown");
                break;
            default:
                this.setBeardId("black");
        }

        //20% of probability of being assigned as warrior
        if(!this.isBaby() && !this.isWarrior() && type == MobSpawnType.STRUCTURE && this.random.nextIntBetweenInclusive(0, 100)<20){
            this.setIsWarrior(true);
        }

        if (this.isWarrior()) {
            int randomV = random.nextInt(100); // 0 to 99

            if (randomV < 15) { // 0–14 → 15%
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_AXE));
            } else if (randomV < 20) { // 15–19 → 5%
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(DwarvenForge_Items.DWARVEN_AXE.get()));
            } else if (randomV < 70) { // 20–69 → 50%
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
            } else { // 70–99 → 30%
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_AXE));
            }
        }

        return super.finalizeSpawn(accessor, difficultyInstance, type, spawnGroupData);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);

        builder.define(BEARD_ID, "black");
        builder.define(IS_WARRIOR, false);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        compoundTag.putString("Hair", this.getBeardId());

        compoundTag.putBoolean("IsWarrior", this.isWarrior());

        super.addAdditionalSaveData(compoundTag);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        setBeardId(compoundTag.getString("Hair"));

        setIsWarrior(compoundTag.getBoolean("IsWarrior"));

        super.readAdditionalSaveData(compoundTag);
    }

    public String getBeardId() {
        return this.entityData.get(BEARD_ID);
    }

    public void setBeardId(String id) {
        this.entityData.set(BEARD_ID, id);
    }

    public Boolean isWarrior() {
        return this.entityData.get(IS_WARRIOR);
    }

    public void setIsWarrior(boolean isWarrior) {
        this.entityData.set(IS_WARRIOR, isWarrior);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(GeoControllersUtil.genericIdleController(this));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    protected void convertToWarrior(){
        this.releaseWorkPois();

        this.setVillagerData(this.getVillagerData().setProfession(VillagerProfession.NONE));

        this.setOffers(new MerchantOffers());

        this.setIsWarrior(true);

        this.playSound(DwarvenForge_Sounds.DWARF_YES.get());
        this.level().broadcastEntityEvent(this, (byte)14);

        if(this.level() instanceof ServerLevel serverLevel){
            refreshBrain(serverLevel);
            this.brain.eraseMemory(MemoryModuleType.JOB_SITE);
            this.brain.eraseMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
            this.brain.eraseMemory(MemoryModuleType.SECONDARY_JOB_SITE);
            this.brain.eraseMemory(MemoryModuleType.LAST_WORKED_AT_POI);
        }


        this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
    }

    private void releaseWorkPois(){
        this.releasePoi(MemoryModuleType.JOB_SITE);
        this.releasePoi(MemoryModuleType.POTENTIAL_JOB_SITE);
    }

    private static ResourceLocation modifierId(String name){
        return ResourceLocation.fromNamespaceAndPath(DwarvenForgeMain.MOD_ID, name);
    }

    private static final AttributeModifier WARRIOR_STRENGTH_BOOST = new AttributeModifier(
            modifierId("warrior_strength"),
            4.0,
            AttributeModifier.Operation.ADD_VALUE);
    private static final AttributeModifier WARRIOR_ARMOR_BOOST = new AttributeModifier(
            modifierId("warrior_armor"),
            12.0,
            AttributeModifier.Operation.ADD_VALUE);
    private static final AttributeModifier WARRIOR_TOUGHNESS_BOOST = new AttributeModifier(
            modifierId("warrior_toughness"),
            6.0,
            AttributeModifier.Operation.ADD_VALUE);

    private static final AttributeModifier WARRIOR_KNOCKBACK_BOOST = new AttributeModifier(
            modifierId("warrior_knockback"),
            0.5,
            AttributeModifier.Operation.ADD_VALUE);

    @Override
    public void tick() {
        super.tick();


        if(this.isWarrior()){
            AttributeInstance entityAttributeInstance_attack = this.getAttribute(Attributes.ATTACK_DAMAGE);
            if(entityAttributeInstance_attack!=null) {
                entityAttributeInstance_attack.removeModifier(WARRIOR_STRENGTH_BOOST);
                entityAttributeInstance_attack.addTransientModifier(WARRIOR_STRENGTH_BOOST);
            }

            AttributeInstance entityAttributeInstance_armor = this.getAttribute(Attributes.ARMOR);
            if(entityAttributeInstance_armor!=null) {
                entityAttributeInstance_armor.removeModifier(WARRIOR_ARMOR_BOOST);
                entityAttributeInstance_armor.addTransientModifier(WARRIOR_ARMOR_BOOST);
            }

            AttributeInstance entityAttributeInstance_toughness = this.getAttribute(Attributes.ARMOR_TOUGHNESS);
            if(entityAttributeInstance_toughness!=null) {
                entityAttributeInstance_toughness.removeModifier(WARRIOR_TOUGHNESS_BOOST);
                entityAttributeInstance_toughness.addTransientModifier(WARRIOR_TOUGHNESS_BOOST);
            }

            AttributeInstance entityAttributeInstance_knockback = this.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
            if(entityAttributeInstance_knockback!=null) {
                entityAttributeInstance_knockback.removeModifier(WARRIOR_KNOCKBACK_BOOST);
                entityAttributeInstance_knockback.addTransientModifier(WARRIOR_KNOCKBACK_BOOST);
            }

        } else {
            AttributeInstance entityAttributeInstance_attack = this.getAttribute(Attributes.ATTACK_DAMAGE);
            if(entityAttributeInstance_attack!=null) entityAttributeInstance_attack.removeModifier(WARRIOR_STRENGTH_BOOST);

            AttributeInstance entityAttributeInstance_armor = this.getAttribute(Attributes.ARMOR);
            if(entityAttributeInstance_armor!=null) entityAttributeInstance_armor.removeModifier(WARRIOR_ARMOR_BOOST);

            AttributeInstance entityAttributeInstance_toughness = this.getAttribute(Attributes.ARMOR_TOUGHNESS);
            if(entityAttributeInstance_toughness!=null) entityAttributeInstance_toughness.removeModifier(WARRIOR_TOUGHNESS_BOOST);

            AttributeInstance entityAttributeInstance_knockback = this.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
            if(entityAttributeInstance_knockback!=null) entityAttributeInstance_knockback.removeModifier(WARRIOR_KNOCKBACK_BOOST);
        }

        if(!this.isAggressive() && this.getHealth() < this.getMaxHealth()){
            if(this.isWarrior()){
                //Heals passively 1HP each 50 ticks (1HP each 2.5s)
                if(this.tickCount%50==0){
                    this.heal(1.0F);
                }
            } else {
                //Heals passively 1HP each 100 ticks (1HP each 5s)
                if(this.tickCount%100==0){
                    this.heal(1.0F);
                }
            }
        }
    }

    //Sounds
    @javax.annotation.Nullable
    protected SoundEvent getAmbientSound() {
        if (this.isSleeping()) {
            return null;
        } else {
            return this.isTrading() ? DwarvenForge_Sounds.DWARF_TRADE.get() : DwarvenForge_Sounds.DWARF_AMBIENT.get();
        }
    }

    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource p_35498_) {
        return DwarvenForge_Sounds.DWARF_HURT.get();
    }

    protected @NotNull SoundEvent getDeathSound() {
        return DwarvenForge_Sounds.DWARF_DEATH.get();
    }

    public @NotNull SoundEvent getNotifyTradeSound() {
        return DwarvenForge_Sounds.DWARF_YES.get();
    }

    protected @NotNull SoundEvent getTradeUpdatedSound(boolean p_35323_) {
        return p_35323_ ? DwarvenForge_Sounds.DWARF_YES.get() : DwarvenForge_Sounds.DWARF_NO.get();
    }

    public void playCelebrateSound() {
        this.playSound(DwarvenForge_Sounds.DWARF_YES.get(), this.getSoundVolume(), this.getVoicePitch());
    }


    //Villager Data
    private static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES;
    private static final ImmutableList<SensorType<? extends Sensor<? super Villager>>> SENSOR_TYPES;
    static {
        MEMORY_TYPES = ImmutableList.of(
                MemoryModuleType.HOME,
                MemoryModuleType.JOB_SITE,
                MemoryModuleType.POTENTIAL_JOB_SITE,
                MemoryModuleType.MEETING_POINT,
                MemoryModuleType.NEAREST_LIVING_ENTITIES,
                MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
                MemoryModuleType.VISIBLE_VILLAGER_BABIES,
                MemoryModuleType.NEAREST_PLAYERS,
                MemoryModuleType.NEAREST_VISIBLE_PLAYER,
                MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER,
                MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM,
                MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS,
                MemoryModuleType.WALK_TARGET,
                MemoryModuleType.LOOK_TARGET,
                MemoryModuleType.INTERACTION_TARGET,
                MemoryModuleType.BREED_TARGET,
                MemoryModuleType.PATH,
                MemoryModuleType.DOORS_TO_CLOSE,
                MemoryModuleType.NEAREST_BED,
                MemoryModuleType.HURT_BY,
                MemoryModuleType.HURT_BY_ENTITY,
                MemoryModuleType.NEAREST_HOSTILE,
                MemoryModuleType.SECONDARY_JOB_SITE,
                MemoryModuleType.HIDING_PLACE,
                MemoryModuleType.HEARD_BELL_TIME,
                MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
                MemoryModuleType.LAST_SLEPT,
                MemoryModuleType.LAST_WOKEN,
                MemoryModuleType.LAST_WORKED_AT_POI,
                MemoryModuleType.GOLEM_DETECTED_RECENTLY,

                //Warrior
                MemoryModuleType.ANGRY_AT,
                MemoryModuleType.ATTACK_TARGET,
                MemoryModuleType.ATTACK_COOLING_DOWN
        );

        SENSOR_TYPES = ImmutableList.of(
                SensorType.NEAREST_LIVING_ENTITIES,
                SensorType.NEAREST_PLAYERS,
                SensorType.NEAREST_ITEMS,
                SensorType.NEAREST_BED,
                SensorType.HURT_BY,
                SensorType.VILLAGER_HOSTILES,
                DwarvenForge_Sensors.DWARF_BABIES.get(),
                SensorType.SECONDARY_POIS,
                SensorType.GOLEM_DETECTED
        );

    }

    @Override
    protected Brain.@NotNull Provider<Villager> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    protected @NotNull Brain<?> makeBrain(@NotNull Dynamic<?> p_35445_) {
        Brain<Villager> brain = this.brainProvider().makeBrain(p_35445_);
        this.registerDwarfBrainGoals(brain);
        return brain;
    }

    public void refreshBrain(@NotNull ServerLevel p_35484_) {
        Brain<Villager> brain = this.getBrain();
        brain.stopAll(p_35484_, this);
        this.brain = brain.copyWithoutBehaviors();
        this.registerDwarfBrainGoals(this.getBrain());
    }

    protected @NotNull Component getTypeName() {

        if(this.isWarrior()){
            return Component.translatable("entity.dwarven_forge.dwarf.warrior");
        } else {
            ResourceLocation profName = BuiltInRegistries.VILLAGER_PROFESSION.getKey(this.getVillagerData().getProfession());
            String description = this.getType().getDescriptionId();
            return Component.translatable(description + "." + (!"minecraft".equals(profName.getNamespace()) ? profName.getNamespace() + "." : "") + profName.getPath());
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        this.setAggressive(brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));

        if(this.isAggressive())
            brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT));

        if(!this.isAggressive() && this.brain.isActive(Activity.FIGHT))
            brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.IDLE));
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        boolean flag = super.hurt(source, amount);
        if (this.level().isClientSide) {
            return false;
        } else {
            if (flag && source.getEntity() instanceof LivingEntity living) {
                DwarfGoalPackages.wasHurtBy(this, living);
            }

            return flag;
        }
    }

    @Nullable
    public LivingEntity getTarget() {
        return this.brain.getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
    }

    private void registerDwarfBrainGoals(Brain<Villager> brain) {
        VillagerProfession profession = this.getVillagerData().getProfession();

        if (isWarrior()) {

            brain.setSchedule(DwarvenForge_Schedules.DWARF_WARRIOR_SCHEDULE.get());

            brain.addActivity(Activity.CORE, DwarfGoalPackages.getCoreWarriorPackage(profession, 0.5F));
            brain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 0, DwarfGoalPackages.initFightActivity(this), MemoryModuleType.ATTACK_TARGET);


            brain.addActivityWithConditions(Activity.MEET, DwarfGoalPackages.getMeetWarriorPackage(profession, 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.MEETING_POINT, MemoryStatus.VALUE_PRESENT)));
            brain.addActivity(Activity.IDLE, DwarfGoalPackages.getIdleWarriorPackage(profession, 0.5F));

        } else {

            if(this.isBaby()){
                brain.setSchedule(Schedule.VILLAGER_BABY);
                brain.addActivity(Activity.PLAY, DwarfGoalPackages.getPlayPackage(0.5F));
            }

            brain.setSchedule(Schedule.VILLAGER_DEFAULT);

            brain.addActivity(Activity.CORE, DwarfGoalPackages.getCorePackage(profession, 0.5F));
            brain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 0, DwarfGoalPackages.initFightActivity(this), MemoryModuleType.ATTACK_TARGET);
            brain.addActivityWithConditions(Activity.WORK, DwarfGoalPackages.getWorkPackage(profession, 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT)));
            brain.addActivityWithConditions(Activity.MEET, DwarfGoalPackages.getMeetPackage(profession, 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.MEETING_POINT, MemoryStatus.VALUE_PRESENT)));
            brain.addActivity(Activity.IDLE, DwarfGoalPackages.getIdlePackage(profession, 0.5F));
            brain.addActivity(Activity.PANIC, DwarfGoalPackages.getPanicPackage(profession, 0.5F));
            brain.addActivity(Activity.PRE_RAID, DwarfGoalPackages.getPreRaidPackage(profession, 0.5F));
            brain.addActivity(Activity.RAID, DwarfGoalPackages.getRaidPackage(profession, 0.5F, false));
            brain.addActivity(Activity.HIDE, DwarfGoalPackages.getHidePackage(profession, 0.5F));
        }

        brain.addActivity(Activity.REST, DwarfGoalPackages.getRestPackage(profession, 0.5F));

        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.setActiveActivityIfPossible(Activity.IDLE);

        brain.updateActivityFromSchedule(this.level().getDayTime(), this.level().getGameTime());
    }

    protected void updateTrades() {
        VillagerData villagerdata = this.getVillagerData();
        Int2ObjectMap<VillagerTrades.ItemListing[]> int2objectmap = DwarfTrades.TRADES.get(villagerdata.getProfession());
        if (int2objectmap != null && !int2objectmap.isEmpty()) {
            VillagerTrades.ItemListing[] avillagertrades$itemlisting = int2objectmap.get(villagerdata.getLevel());
            if (avillagertrades$itemlisting != null) {
                MerchantOffers merchantoffers = this.getOffers();
                this.addOffersFromItemListings(merchantoffers, avillagertrades$itemlisting, 2);
            }
        }
    }

    @Override
    protected void dropCustomDeathLoot(@NotNull ServerLevel level, @NotNull DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);

        if(this.isWarrior()){
            ItemStack metalDropped=new ItemStack(DwarvenForge_Items.DWARVEN_METAL_INGOT.get(), random.nextIntBetweenInclusive(1, 5));

            this.spawnAtLocation(metalDropped);
        }
    }

    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if(this.isAlive() && itemstack.getItem() instanceof AxeItem && player.isShiftKeyDown() && !this.isWarrior() && !this.isBaby()){

            if(this.getVillagerData().getLevel() <= 1){
                convertToWarrior();

                this.setItemSlot(EquipmentSlot.MAINHAND, itemstack);

                if(!player.isCreative()){
                    player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                }

                return InteractionResult.sidedSuccess(this.level().isClientSide);
            } else {
                //To not being able to convert experienced workers
                this.playSound(DwarvenForge_Sounds.DWARF_NO.get());

                return InteractionResult.FAIL;
            }
        }



        if (itemstack.getItem() != Items.VILLAGER_SPAWN_EGG && this.isAlive() && !this.isTrading() && !this.isSleeping() && !player.isSecondaryUseActive()) {
            if (this.isBaby()) {
                this.setUnhappy();
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            } else {
                if (!this.level().isClientSide) {
                    boolean flag = this.getOffers().isEmpty();
                    if (hand == InteractionHand.MAIN_HAND) {
                        if (flag) {
                            this.setUnhappy();
                        }

                        player.awardStat(Stats.TALKED_TO_VILLAGER);
                    }

                    if (!flag) {
                        this.startTrading(player);
                    }
                }

                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        } else {
            return super.mobInteract(player, hand);
        }
    }

    private void setUnhappy() {
        this.setUnhappyCounter(40);
        if (!this.level().isClientSide()) {
            this.playSound(DwarvenForge_Sounds.DWARF_NO.get(), this.getSoundVolume(), this.getVoicePitch());
        }
    }

    private void startTrading(Player p_35537_) {
        this.updateSpecialPrices(p_35537_);
        this.setTradingPlayer(p_35537_);
        this.openTradingScreen(p_35537_, this.getDisplayName(), this.getVillagerData().getLevel());
    }

    private void updateSpecialPrices(Player p_35541_) {
        int i = this.getPlayerReputation(p_35541_);
        if (i != 0) {
            for(MerchantOffer merchantoffer : this.getOffers()) {
                merchantoffer.addToSpecialPriceDiff(-Mth.floor((float)i * merchantoffer.getPriceMultiplier()));
            }
        }

        if (p_35541_.hasEffect(MobEffects.HERO_OF_THE_VILLAGE)) {
            MobEffectInstance mobeffectinstance = p_35541_.getEffect(MobEffects.HERO_OF_THE_VILLAGE);
            int k = mobeffectinstance.getAmplifier();

            for(MerchantOffer merchantoffer1 : this.getOffers()) {
                double d0 = 0.3 + (double)0.0625F * (double)k;
                int j = (int)Math.floor(d0 * (double)merchantoffer1.getBaseCostA().getCount());
                merchantoffer1.addToSpecialPriceDiff(-Math.max(j, 1));
            }
        }

    }



    @Nullable
    public Villager getBreedOffspring(@NotNull ServerLevel level, @NotNull AgeableMob ageableMob) {

        DwarfEntity dwarf = new DwarfEntity(DwarvenForge_Entities.DWARF.get(), level);
        dwarf.finalizeSpawn(level, level.getCurrentDifficultyAt(dwarf.blockPosition()), MobSpawnType.BREEDING, null);

        return dwarf;
    }

    @Override
    public boolean wantsToSpawnGolem(long p_35393_) {
        return false;
    }

    public void thunderHit(@NotNull ServerLevel p_35409_, @NotNull LightningBolt p_35410_) {
        if (p_35409_.getDifficulty() != Difficulty.PEACEFUL && EventHooks.canLivingConvert(this, EntityType.WITCH, (timer) -> {
        })) {
            DwarvenForgeMain.LOGGER.info("Dwarf {} was struck by lightning {}.", this, p_35410_);
            Tadpole tadpole = EntityType.TADPOLE.create(p_35409_);
            if (tadpole != null) {
                tadpole.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
                tadpole.finalizeSpawn(p_35409_, p_35409_.getCurrentDifficultyAt(tadpole.blockPosition()), MobSpawnType.CONVERSION, null);
                tadpole.setNoAi(this.isNoAi());
                if (this.hasCustomName()) {
                    tadpole.setCustomName(this.getCustomName());
                    tadpole.setCustomNameVisible(this.isCustomNameVisible());
                }

                tadpole.setPersistenceRequired();
                EventHooks.onLivingConvert(this, tadpole);
                p_35409_.addFreshEntityWithPassengers(tadpole);
                this.releaseAllPois();
                this.discard();
            } else {
                super.thunderHit(p_35409_, p_35410_);
            }
        } else {
            super.thunderHit(p_35409_, p_35410_);
        }
    }


    private void releaseAllPois() {
        this.releasePoi(MemoryModuleType.HOME);
        this.releasePoi(MemoryModuleType.JOB_SITE);
        this.releasePoi(MemoryModuleType.POTENTIAL_JOB_SITE);
        this.releasePoi(MemoryModuleType.MEETING_POINT);
    }

}
