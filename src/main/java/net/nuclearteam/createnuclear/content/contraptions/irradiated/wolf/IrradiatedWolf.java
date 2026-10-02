package net.nuclearteam.createnuclear.content.contraptions.irradiated.wolf;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.equine.Llama;
import net.minecraft.world.entity.animal.turtle.Turtle;
import net.minecraft.world.entity.animal.wolf.WolfSoundVariant;
import net.minecraft.world.entity.animal.wolf.WolfSoundVariants;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.nuclearteam.createnuclear.CNEntityType;
import net.nuclearteam.createnuclear.CNTags;
import org.jspecify.annotations.Nullable;

@SuppressWarnings({"unused"})
public class IrradiatedWolf extends TamableAnimal implements NeutralMob {
    private static final EntityDataAccessor<Boolean> DATA_INTERESTED_ID;
    private static final EntityDataAccessor<Long> DATA_ANGER_END_TIME;
    public static final TargetingConditions.Selector PREY_SELECTOR;
    private static final float START_HEALTH = 8.0F;
    private static final float TAME_HEALTH = 40.0F;
    private static final float ARMOR_REPAIR_UNIT = 0.125F;
    private float interestedAngle;
    private float interestedAngleO;
    private boolean isWet;
    private boolean isShaking;
    private float shakeAnim;
    private float shakeAnimO;
    private static final UniformInt PERSISTENT_ANGER_TIME;
    @Nullable
    private EntityReference<LivingEntity> persistentAngerTarget;

    public IrradiatedWolf(EntityType<? extends IrradiatedWolf> entityType, Level level) {
        super(entityType, level);
        this.setTame(false, false);
        this.setPathfindingMalus(PathType.POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(PathType.DANGER_POWDER_SNOW, -1.0F);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new IrradiatedWolfAvoidEntityGoal<>(this, Llama.class, 24.0F, (double) 1.5F, (double) 1.5F));
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, (double)1.0F, true));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, (double)1.0F, 10.0F, 2.0F));
        this.goalSelector.addGoal(7, new BreedGoal(this, (double)1.0F));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, (double)1.0F));
        this.goalSelector.addGoal(9, new BegGoal(this, 8.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, (new HurtByTargetGoal(this, new Class[0])).setAlertOthers(new Class[0]));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, Animal.class, false, PREY_SELECTOR));
        this.targetSelector.addGoal(6, new NonTameRandomTargetGoal<>(this, Turtle.class, false, Turtle.BABY_ON_LAND_SELECTOR));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, AbstractSkeleton.class, false));
        this.targetSelector.addGoal(8, new ResetUniversalAngerTargetGoal<>(this, true));
    }


    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, (double)0.3F)
                .add(Attributes.MAX_HEALTH, (double)8.0F)
                .add(Attributes.ATTACK_DAMAGE, (double)4.0F);
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_INTERESTED_ID, false);
        builder.define(DATA_ANGER_END_TIME, -1L);
    }

    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(sounds().stepSound().value(), 0.15F, 1.0F);
    }

    // the wolf sounds are per sound variant now; these are the classic ones
    private static WolfSoundVariant.WolfSoundSet sounds() {
        return SoundEvents.WOLF_SOUNDS.get(WolfSoundVariants.SoundSet.CLASSIC).adultSounds();
    }

    protected void addAdditionalSaveData(ValueOutput compound) {
        super.addAdditionalSaveData(compound);

        this.addPersistentAngerSaveData(compound);
    }

    protected void readAdditionalSaveData(ValueInput compound) {
        super.readAdditionalSaveData(compound);

        this.readPersistentAngerSaveData(this.level(), compound);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnType, @Nullable SpawnGroupData spawnGroupData) {
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    protected SoundEvent getAmbientSound() {
        if (this.isAngry()) {
            return sounds().growlSound().value();
        } else if (this.random.nextInt(3) != 0) {
            return sounds().ambientSound().value();
        } else {
            return this.isTame() && this.getHealth() < 20.0F ? sounds().whineSound().value() : sounds().pantSound().value();
        }
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return this.canArmorAbsorb(damageSource) ? SoundEvents.WOLF_ARMOR_DAMAGE : sounds().hurtSound().value();
    }

    protected SoundEvent getDeathSound() {
        return sounds().deathSound().value();
    }

    protected float getSoundVolume() {
        return 0.4F;
    }

    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide() && this.isWet && !this.isShaking && !this.isPathFinding() && this.onGround()) {
            this.isShaking = true;
            this.shakeAnim = 0.0F;
            this.shakeAnimO = 0.0F;
            this.level().broadcastEntityEvent(this, (byte)8);
        }

        if (!this.level().isClientSide()) {
            this.updatePersistentAnger((ServerLevel)this.level(), true);
        }

    }

    public void tick() {
        super.tick();
        if (this.isAlive()) {
            this.interestedAngleO = this.interestedAngle;
            if (this.isInterested()) {
                this.interestedAngle += (1.0F - this.interestedAngle) * 0.4F;
            } else {
                this.interestedAngle += (0.0F - this.interestedAngle) * 0.4F;
            }

            if (this.isInWaterOrRain()) {
                this.isWet = true;
                if (this.isShaking && !this.level().isClientSide()) {
                    this.level().broadcastEntityEvent(this, (byte)56);
                    this.cancelShake();
                }
            } else if ((this.isWet || this.isShaking) && this.isShaking) {
                if (this.shakeAnim == 0.0F) {
                    this.playSound(SoundEvents.WOLF_SHAKE, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                    this.gameEvent(GameEvent.ENTITY_ACTION);
                }

                this.shakeAnimO = this.shakeAnim;
                this.shakeAnim += 0.05F;
                if (this.shakeAnimO >= 2.0F) {
                    this.isWet = false;
                    this.isShaking = false;
                    this.shakeAnimO = 0.0F;
                    this.shakeAnim = 0.0F;
                }

                if (this.shakeAnim > 0.4F) {
                    float f = (float)this.getY();
                    int i = (int)(Mth.sin((this.shakeAnim - 0.4F) * (float)Math.PI) * 7.0F);
                    Vec3 vec3 = this.getDeltaMovement();

                    for(int j = 0; j < i; ++j) {
                        float f1 = (this.random.nextFloat() * 2.0F - 1.0F) * this.getBbWidth() * 0.5F;
                        float f2 = (this.random.nextFloat() * 2.0F - 1.0F) * this.getBbWidth() * 0.5F;
                        this.level().addParticle(ParticleTypes.SPLASH, this.getX() + (double)f1, (double)(f + 0.8F), this.getZ() + (double)f2, vec3.x, vec3.y, vec3.z);
                    }
                }
            }
        }

    }

    private void cancelShake() {
        this.isShaking = false;
        this.shakeAnim = 0.0F;
        this.shakeAnimO = 0.0F;
    }

    public void die(DamageSource cause) {
        this.isWet = false;
        this.isShaking = false;
        this.shakeAnimO = 0.0F;
        this.shakeAnim = 0.0F;
        super.die(cause);
    }

    public boolean isWet() {
        return this.isWet;
    }

    public float getWetShade(float partialTicks) {
        return Math.min(0.75F + Mth.lerp(partialTicks, this.shakeAnimO, this.shakeAnim) / 2.0F * 0.25F, 1.0F);
    }

    public float getBodyRollAngle(float partialTicks, float offset) {
        float f = (Mth.lerp(partialTicks, this.shakeAnimO, this.shakeAnim) + offset) / 1.8F;
        if (f < 0.0F) {
            f = 0.0F;
        } else if (f > 1.0F) {
            f = 1.0F;
        }

        return Mth.sin(f * (float)Math.PI) * Mth.sin(f * (float)Math.PI * 11.0F) * 0.15F * (float)Math.PI;
    }

    public float getHeadRollAngle(float partialTicks) {
        return Mth.lerp(partialTicks, this.interestedAngleO, this.interestedAngle) * 0.15F * (float)Math.PI;
    }

    public int getMaxHeadXRot() {
        return this.isInSittingPose() ? 20 : super.getMaxHeadXRot();
    }

    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        if (this.isInvulnerableTo(level, source)) {
            return false;
        } else {
            this.setOrderedToSit(false);

            return super.hurtServer(level, source, amount);
        }
    }

    public boolean canUseSlot(EquipmentSlot slot) {
        return true;
    }

    protected void actuallyHurt(ServerLevel level, DamageSource damageSource, float damageAmount) {
        if (!this.canArmorAbsorb(damageSource)) {
            super.actuallyHurt(level, damageSource, damageAmount);
        } else {
            ItemStack itemstack = this.getBodyArmorItem();
            int i = itemstack.getDamageValue();
            int j = itemstack.getMaxDamage();
            itemstack.hurtAndBreak(Mth.ceil(damageAmount), this, EquipmentSlot.BODY);
            if (Crackiness.WOLF_ARMOR.byDamage(i, j) != Crackiness.WOLF_ARMOR.byDamage(this.getBodyArmorItem())) {
                this.playSound(SoundEvents.WOLF_ARMOR_CRACK);
                level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, Items.ARMADILLO_SCUTE), this.getX(), this.getY() + (double)1.0F, this.getZ(), 20, 0.2, 0.1, 0.2, 0.1);
            }
        }

    }

    private boolean canArmorAbsorb(DamageSource damageSource) {
        return this.hasArmor() && !damageSource.is(DamageTypeTags.BYPASSES_WOLF_ARMOR);
    }

    protected void applyTamingSideEffects() {
        if (this.isTame()) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)40.0F);
            this.setHealth(40.0F);
        } else {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)8.0F);
        }

    }

    protected void hurtArmor(DamageSource damageSource, float damageAmount) {
        this.doHurtEquipment(damageSource, damageAmount, new EquipmentSlot[]{EquipmentSlot.BODY});
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        if (!this.level().isClientSide() || this.isBaby() && this.isFood(itemstack)) {
            if (this.isTame()) {
                if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
                    FoodProperties foodproperties = itemstack.get(DataComponents.FOOD);
                    float f = foodproperties != null ? (float)foodproperties.nutrition() : 1.0F;
                    this.heal(2.0F * f);
                    itemstack.consume(1, player);
                    this.gameEvent(GameEvent.EAT);
                    return InteractionResult.SUCCESS;
                } else {

                    if (itemstack.is(Items.WOLF_ARMOR) && this.isOwnedBy(player) && this.getBodyArmorItem().isEmpty() && !this.isBaby()) {
                        this.setItemSlot(EquipmentSlot.BODY, itemstack.copyWithCount(1));
                        itemstack.consume(1, player);
                        return InteractionResult.SUCCESS;
                    } else if (!itemstack.is(Items.SHEARS) || !this.isOwnedBy(player) || !this.hasArmor() || EnchantmentHelper.has(this.getBodyArmorItem(), EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE) && !player.isCreative()) {
                        if (itemstack.is(ArmorMaterials.ARMADILLO_SCUTE.repairIngredient()) && this.isInSittingPose() && this.hasArmor() && this.isOwnedBy(player) && this.getBodyArmorItem().isDamaged()) {
                            itemstack.shrink(1);
                            this.playSound(SoundEvents.WOLF_ARMOR_REPAIR);
                            ItemStack itemstack2 = this.getBodyArmorItem();
                            int i = (int)((float)itemstack2.getMaxDamage() * 0.125F);
                            itemstack2.setDamageValue(Math.max(0, itemstack2.getDamageValue() - i));
                            return InteractionResult.SUCCESS;
                        } else {
                            InteractionResult interactionresult = super.mobInteract(player, hand);
                            if (!interactionresult.consumesAction() && this.isOwnedBy(player)) {
                                this.setOrderedToSit(!this.isOrderedToSit());
                                this.jumping = false;
                                this.navigation.stop();
                                this.setTarget((LivingEntity)null);
                                return InteractionResult.SUCCESS.withoutItem();
                            } else {
                                return interactionresult;
                            }
                        }
                    } else {
                        itemstack.hurtAndBreak(1, player, hand);
                        this.playSound(SoundEvents.ARMOR_UNEQUIP_WOLF);
                        ItemStack itemstack1 = this.getBodyArmorItem();
                        this.setItemSlot(EquipmentSlot.BODY, ItemStack.EMPTY);
                        if (this.level() instanceof ServerLevel serverLevel)
                            this.spawnAtLocation(serverLevel, itemstack1);
                        return InteractionResult.SUCCESS;
                    }
                }
            } else if (itemstack.is(Items.BONE) && !this.isAngry()) {
                itemstack.consume(1, player);
                this.tryToTame(player);
                return InteractionResult.SUCCESS;
            } else {
                return super.mobInteract(player, hand);
            }
        } else {
            boolean flag = this.isOwnedBy(player) || this.isTame() || itemstack.is(Items.BONE) && !this.isTame() && !this.isAngry();
            return flag ? InteractionResult.CONSUME : InteractionResult.PASS;
        }
    }

    private void tryToTame(Player player) {
        if (this.random.nextInt(3) == 0) {
            this.tame(player);
            this.navigation.stop();
            this.setTarget((LivingEntity)null);
            this.setOrderedToSit(true);
            this.level().broadcastEntityEvent(this, (byte)7);
        } else {
            this.level().broadcastEntityEvent(this, (byte)6);
        }

    }

    public void handleEntityEvent(byte id) {
        if (id == 8) {
            this.isShaking = true;
            this.shakeAnim = 0.0F;
            this.shakeAnimO = 0.0F;
        } else if (id == 56) {
            this.cancelShake();
        } else {
            super.handleEntityEvent(id);
        }

    }

    public float getTailAngle() {
        if (this.isAngry()) {
            return 1.5393804F;
        } else if (this.isTame()) {
            float f = this.getMaxHealth();
            float f1 = (f - this.getHealth()) / f;
            return (0.55F - f1 * 0.4F) * (float)Math.PI;
        } else {
            return ((float)Math.PI / 5F);
        }
    }

    public boolean isFood(ItemStack stack) {
        return stack.is(CNTags.CNItemTags.FUEL.tag);
    }

    public int getMaxSpawnClusterSize() {
        return 8;
    }

    @Override
    public long getPersistentAngerEndTime() {
        return this.entityData.get(DATA_ANGER_END_TIME);
    }

    @Override
    public void setPersistentAngerEndTime(long time) {
        this.entityData.set(DATA_ANGER_END_TIME, time);
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setTimeToRemainAngry(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    @Nullable
    @Override
    public EntityReference<LivingEntity> getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable EntityReference<LivingEntity> target) {
        this.persistentAngerTarget = target;
    }


    public boolean hasArmor() {
        return this.getBodyArmorItem().is(Items.WOLF_ARMOR);
    }


    @Nullable
    public IrradiatedWolf getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return CNEntityType.IRRADIATED_WOLF.create(level);
    }

    public void setIsInterested(boolean isInterested) {
        this.entityData.set(DATA_INTERESTED_ID, isInterested);
    }

    public boolean canMate(Animal otherAnimal) {
        if (otherAnimal == this) {
            return false;
        } else if (!this.isTame()) {
            return false;
        } else if (otherAnimal instanceof IrradiatedWolf irradiatedWolf) {
            if (!irradiatedWolf.isTame()) {
                return false;
            } else {
                return irradiatedWolf.isInSittingPose() ? false : this.isInLove() && irradiatedWolf.isInLove();
            }
        } else {
            return false;
        }
    }

    public boolean isInterested() {
        return (Boolean)this.entityData.get(DATA_INTERESTED_ID);
    }

    public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        if (!(target instanceof Creeper) && !(target instanceof Ghast)) {
            if (target instanceof IrradiatedWolf wolf) {
                return !wolf.isTame() || wolf.getOwner() != owner;
            } else if (target instanceof Player && owner instanceof Player && !((Player)owner).canHarmPlayer((Player)target)) {
                return false;
            } else if (target instanceof AbstractHorse && ((AbstractHorse)target).isTamed()) {
                return false;
            } else {
                return !(target instanceof TamableAnimal) || !((TamableAnimal)target).isTame();
            }
        } else {
            return false;
        }
    }

    public boolean canBeLeashed() {
        return !this.isAngry();
    }

    public Vec3 getLeashOffset() {
        return new Vec3((double)0.0F, (double)(0.6F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
    }

    public static boolean checkWolfSpawnRules(EntityType<IrradiatedWolf> wolf, LevelAccessor level, EntitySpawnReason spawnType, BlockPos pos, RandomSource random) {
        return level.getBlockState(pos.below()).is(BlockTags.WOLVES_SPAWNABLE_ON) && isBrightEnoughToSpawn(level, pos);
    }

    static {
        DATA_INTERESTED_ID = SynchedEntityData.defineId(IrradiatedWolf.class, EntityDataSerializers.BOOLEAN);
        DATA_ANGER_END_TIME = SynchedEntityData.defineId(IrradiatedWolf.class, EntityDataSerializers.LONG);
        PREY_SELECTOR = (p_348295_, level) -> {
            EntityType<?> entitytype = p_348295_.getType();
            return entitytype == EntityTypes.SHEEP || entitytype == EntityTypes.RABBIT || entitytype == EntityTypes.FOX || entitytype == CNEntityType.IRRADIATED_CAT.get();
        };
        PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    }

    static class IrradiatedWolfAvoidEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
        private final IrradiatedWolf wolf;

        public IrradiatedWolfAvoidEntityGoal(IrradiatedWolf wolf, Class<T> entityClassToAvoid, float maxDist, double walkSpeedModifier, double sprintSpeedModifier) {
            super(wolf, entityClassToAvoid, maxDist, walkSpeedModifier, sprintSpeedModifier);
            this.wolf = wolf;
        }

        @Override
        public boolean canUse() {
            return super.canUse()
                    && this.toAvoid instanceof Llama llama
                    && !this.wolf.isTame()
                    && this.avoidLlama(llama);
        }

        private boolean avoidLlama(Llama llama) {
            return llama.getStrength() >= this.wolf.getRandom().nextInt(5);
        }

        @Override
        public void start() {
            this.wolf.setTarget(null);
            super.start();
        }

        @Override
        public void tick() {
            this.wolf.setTarget(null);
            super.tick();
        }
    }
}
