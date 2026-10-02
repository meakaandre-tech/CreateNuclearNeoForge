package net.nuclearteam.createnuclear.content.multiblock.controller;

import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.foundation.utility.IInteractionChecker;
import lib.multiblock.SimpleMultiBlockAislePatternBuilder;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.nuclearteam.createnuclear.CNBlocks;
import net.nuclearteam.createnuclear.CNDataComponents;
import net.nuclearteam.createnuclear.CNItems;
import net.nuclearteam.createnuclear.CreateNuclear;
import net.nuclearteam.createnuclear.content.multiblock.IHeat;
import net.nuclearteam.createnuclear.content.multiblock.bluePrintItem.PatternData;
import net.nuclearteam.createnuclear.content.multiblock.bluePrintItem.ReactorBluePrintData;
import net.nuclearteam.createnuclear.content.multiblock.input.ReactorInputEntity;
import net.nuclearteam.createnuclear.content.multiblock.input.ReactorInputInventory;
import net.nuclearteam.createnuclear.content.multiblock.output.ReactorOutput;
import net.nuclearteam.createnuclear.content.multiblock.output.ReactorOutputEntity;

import java.util.List;

import static net.nuclearteam.createnuclear.content.multiblock.CNMultiblock.*;
import static net.nuclearteam.createnuclear.content.multiblock.controller.ReactorControllerBlock.ASSEMBLED;

@SuppressWarnings({"unused"})
public class ReactorControllerBlockEntity extends SmartBlockEntity implements IInteractionChecker {
    public boolean destroyed = false;
    public boolean created = false;
    public boolean test = true;
    public int speed = 16; // This is the result speed of the reactor, change this to change the total capacity

    public boolean sendUpdate;

    public ReactorControllerBlock controller;

    public ReactorControllerInventory inventory;


    //private boolean powered;
    public State powered = State.OFF;
    public float reactorPower;
    public float lastReactorPower;
    int overFlowHeatTimer = 0;
    int overFlowLimiter = 30;
    double overHeat = 0;
    public int baseUraniumHeat = 25;
    public int baseGraphiteHeat = -10;
    public int proximityUraniumHeat = 5;
    public int proximityGraphiteHeat = -5;
    public int maxUraniumPerGraphite = 3;
    public int graphiteTimer = 3600;
    public int uraniumTimer = 3600;
    public int tmpGraphiteTimer = graphiteTimer;
    public int tmpUraniumTimer = uraniumTimer;
    public int countUraniumRod;
    public int countGraphiteRod;
    public int heat;
    public double total;
    private boolean isTotal = false;
    public CompoundTag screen_pattern = new CompoundTag();
    public ItemStack configuredPattern;

    private ItemStack fuelItem;
    private ItemStack coolerItem;

    private final int[][] formattedPattern = new int[][]{
            {99,99,99,0,1,2,99,99,99},
            {99,99,3,4,5,6,7,99,99},
            {99,8,9,10,11,12,13,14,99},
            {15,16,17,18,19,20,21,22,23},
            {24,25,26,27,28,29,30,31,32},
            {33,34,35,36,37,38,39,40,41},
            {99,42,43,44,45,46,47,48,99},
            {99,99,49,50,51,52,53,99,99},
            {99,99,99,54,55,56,99,99,99}
    };
    private final int[][] offsets = { {1, 0}, {-1, 0}, {0, 1}, {0, -1} };



    public ReactorControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        inventory = new ReactorControllerInventory(this);
        configuredPattern = ItemStack.EMPTY;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {

    }

    public boolean getAssembled() { // permet de savoir si le réacteur est formé ou pas.
        BlockState state = getBlockState();
        return state.getValue(ASSEMBLED);
    }

    // the goggle overlay lives in client.CNTooltips.ReactorController

    public ItemStack getFuelItem() {
        return fuelItem;
    }

    public ItemStack getCoolerItem() {
        return coolerItem;
    }

    @Override
    public void destroy() {
        super.destroy();
        Containers.dropContents(level, worldPosition, inventory);
    }

    //(Si les methode read et write ne sont pas implémenté alors lorsque l'on relance le monde minecraft les items dans le composant auront disparu !)
    @Override
    protected void read(ValueInput tag, boolean clientPacket) { //Permet de stocker les item 1/2
        if (!clientPacket) {
            inventory.read(tag.childOrEmpty("inventory"));
        }
        configuredPattern = tag.read("configuredPattern", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        coolerItem = tag.read("coolerItem", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        fuelItem = tag.read("fuelItem", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);

        total = tag.getDoubleOr("total", 0);
        heat = tag.getIntOr("heat", 0);
        super.read(tag, clientPacket);
    }

    @Override
    protected void write(ValueOutput compound, boolean clientPacket) { //Permet de stocker les item 2/2
        if (!clientPacket) {
            inventory.write(compound.child("inventory"));
        }

        if (configuredPattern != null) compound.store("configuredPattern", ItemStack.OPTIONAL_CODEC, configuredPattern);
        if (coolerItem != null) compound.store("coolerItem", ItemStack.OPTIONAL_CODEC, coolerItem);
        if (fuelItem != null) compound.store("fuelItem", ItemStack.OPTIONAL_CODEC, fuelItem);

        compound.putDouble("total", Double.isNaN(total) ? total : calculateProgress());
        compound.putInt("heat", heat);
        super.write(compound, clientPacket);
    }

    public enum State {
        ON, OFF
    }

    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide())
            return;

        if (isEmptyConfiguredPattern()) {
            ReactorBluePrintData data = getReactorBluePrintData();
            countGraphiteRod = data.countGraphiteRod();
            countUraniumRod = data.countUraniumRod();

            if (!isTotal) {
                total = calculateProgress();
                isTotal = true;
            }
            BlockEntity blockEntity = level.getBlockEntity(getBlockPosForReactor('I'));

            if (blockEntity instanceof ReactorInputEntity be) {
                fuelItem = be.inventory.getItem(0);
                coolerItem = be.inventory.getItem(1);

                ReactorInputInventory capability = be.inventory;
                if (tmpUraniumTimer >= 0) {
                    tmpUraniumTimer -= 1 * countUraniumRod;
                } else {
                    ItemStack extractItem1 = capability.removeItem(0, 1);
                    capability.setChanged();
                    tmpUraniumTimer = uraniumTimer;
                }
                if (tmpGraphiteTimer >= 0) {
                    tmpGraphiteTimer -= 1 * countGraphiteRod;
                } else {
                    ItemStack extractItem2 = capability.removeItem(1, 1);
                    capability.setChanged();
                    tmpGraphiteTimer = graphiteTimer;
                }

                if (!fuelItem.isEmpty() && !coolerItem.isEmpty()) {
                    heat = (int) calculateHeat(inventory.getItem(0));
                    if (updateTimers()) {

                        if (IHeat.HeatLevel.of(heat) == IHeat.HeatLevel.SAFETY || IHeat.HeatLevel.of(heat) == IHeat.HeatLevel.CAUTION || IHeat.HeatLevel.of(heat) == IHeat.HeatLevel.WARNING) {
                            this.rotate(getBlockState(), new BlockPos(getBlockPos().getX(), getBlockPos().getY() + FindController('O').getY(), getBlockPos().getZ()), getLevel(), heat/4, true);
                            return;
                        } else {
                            EventTriggerPacket packet = new EventTriggerPacket(600);
                            CreateNuclear.LOGGER.warn("hum EventTriggerBlock ? {}", packet);
                            for (ServerPlayer player : PlayerLookup.around((ServerLevel) level, getBlockPos(), 32))
                                ServerPlayNetworking.send(player, packet);

                            this.rotate(getBlockState(), new BlockPos(getBlockPos().getX(), getBlockPos().getY() + FindController('O').getY(), getBlockPos().getZ()), getLevel(), 0, false);
                            isTotal = false;
                            return;
                        }
                    } else {
                        this.rotate(getBlockState(), new BlockPos(getBlockPos().getX(), getBlockPos().getY() + FindController('O').getY(), getBlockPos().getZ()), getLevel(), 0, false);
                        isTotal = false;
                        return;
                    }
                }
                this.notifyUpdate();
            }
        }
    }

    private boolean isEmptyConfiguredPattern() {
        return !configuredPattern.isEmpty();// || !configuredPattern.getOrCreateTag().isEmpty();
    }

    private boolean updateTimers() {
        total -= 1;
        return total >= 0;//(total/constTotal) >= 0;
    }

    private ReactorBluePrintData getDefaultReactorBluePrintData() {
        return new ReactorBluePrintData(
                0, 0, 0, 0,
                new PatternData[0], new PatternData[0]
        );
    }
    private ReactorBluePrintData getReactorBluePrintData() {
        return configuredPattern.getOrDefault(CNDataComponents.REACTOR_BLUE_PRINT_DATA, getDefaultReactorBluePrintData());
    }

    private double calculateProgress() {
        ReactorBluePrintData data = getReactorBluePrintData();
        countGraphiteRod = data.countGraphiteRod();
        countUraniumRod = data.countUraniumRod();
        graphiteTimer = data.graphiteTime();
        uraniumTimer = data.uraniumTime();

        double progressGraphite = countGraphiteRod  > 0
                ? (double) graphiteTimer  / countGraphiteRod
                : 0.0;
        double progressUranium  = countUraniumRod > 0
                ? (double) uraniumTimer   / countUraniumRod
                : 0.0;

        double tmp = progressGraphite + progressUranium;

        return progressGraphite + progressUranium;
    }

    private double calculateHeat(ItemStack input) {
        ReactorBluePrintData data = input.getOrDefault(CNDataComponents.REACTOR_BLUE_PRINT_DATA, getDefaultReactorBluePrintData());

        countGraphiteRod = data.countGraphiteRod();
        countUraniumRod = data.countUraniumRod();
        heat = 0;

        // if more than maxUraniumPerGraphite of the rods are uranium, the reactor will overheat
        if (countUraniumRod > countGraphiteRod * maxUraniumPerGraphite) {
            overFlowHeatTimer++;
            if (overFlowHeatTimer >= overFlowLimiter) {
                overHeat++;
                overFlowHeatTimer = 0;
                if (overFlowLimiter > 2) {
                    overFlowLimiter--;
                }
            }
        } else {
            overFlowHeatTimer = 0;
            overFlowLimiter     = 30;
            overHeat = Math.max(0, overHeat - 2);
        }

        PatternData[] patternDataAll = data.patternAll();
        for (PatternData pd : patternDataAll) {
            char currentRod = '\0';
            ItemStack stack = pd.stack();

            if (stack.is(CNItems.URANIUM_ROD.get())) {
                heat += baseUraniumHeat;
                currentRod = 'u';
            } else if (stack.is(CNItems.GRAPHITE_ROD.get())) {
                heat += baseGraphiteHeat;
                currentRod = 'g';
            }

            if (currentRod != '\0') {
                pattern:
                for (int i = 0; i < formattedPattern.length; i++) {
                    for (int j = 0; j < formattedPattern[i].length; j++) {
                        if (formattedPattern[i][j] == pd.slot()) {
                            // the offsets for the four directions (down, up, right, left) is int[][] offsets = { {1, 0}, {-1, 0}, {0, 1}, {0, -1} }; (defined at the top of the class)
                            for (int[] offset : offsets) {
                                int ni = i + offset[0], nj = j + offset[1];
                                if (ni < 0 || ni >= formattedPattern.length || nj < 0 || nj >= formattedPattern[i].length) continue;

                                int neighborSlot = formattedPattern[ni][nj];
                                for (PatternData pd2 : patternDataAll) {
                                    if (pd2.slot() == neighborSlot) {
                                        ItemStack stack2 = pd.stack();
                                        if (currentRod == 'u') {
                                            heat += stack2.is(CNItems.URANIUM_ROD.get()) ? proximityUraniumHeat : proximityGraphiteHeat;
                                        }
                                        break;
                                    }
                                }
                            }
                            break pattern;
                        }
                    }
                }
            }
        }

        return heat + overHeat;
    }

    private BlockPos getBlockPosForReactor(char character) {
        BlockPos pos = FindController(character);
        BlockPos posController = getBlockPos();
        BlockPos posInput = new BlockPos(posController.getX(), posController.getY(), posController.getZ());

        int[][] directions = {
                {0,0, pos.getX()}, // NORTH
                {0,0, -pos.getX()}, // SOUTH
                {-pos.getX(),0,0}, // EAST
                {pos.getX(),0,0} // WEST
        };


        for (int[] direction : directions) {
            BlockPos newPos = posController.offset(direction[0], direction[1], direction[2]);
            if (level.getBlockState(newPos).is(CNBlocks.REACTOR_INPUT.get())) {
                posInput = newPos;
                break;
            }
        }

        return posInput;
    }

    private static BlockPos FindController(char character) {
        return SimpleMultiBlockAislePatternBuilder.start()
                .aisle(AAAAA, AAAAA, AAAAA, AAAAA, AAAAA)
                .aisle(AABAA, ADADA, BACAB, ADADA, AABAA)
                .aisle(AABAA, ADADA, BACAB, ADADA, AABAA)
                .aisle(AAIAA, ADADA, BACAB, ADADA, AAAA)
                .aisle(AABAA, ADADA, BACAB, ADADA, AABAA)
                .aisle(AABAA, ADADA, BACAB, ADADA, AABAA)
                .aisle(AAAAA, AAAAA, AAAAA, AAAAA, AAOAA)
                .where('A', a -> a.getState().is(CNBlocks.REACTOR_CASING.get()))
                .where('B', a -> a.getState().is(CNBlocks.REACTOR_FRAME.get()))
                .where('C', a -> a.getState().is(CNBlocks.REACTOR_CORE.get()))
                .where('D', a -> a.getState().is(CNBlocks.REACTOR_COOLER.get()))
                .where('*', a -> a.getState().is(CNBlocks.REACTOR_CONTROLLER.get()))
                .where('O', a -> a.getState().is(CNBlocks.REACTOR_OUTPUT.get()))
                .where('I', a -> a.getState().is(CNBlocks.REACTOR_INPUT.get()))
                .getDistanceController(character);
    }

    public void rotate(BlockState state, BlockPos pos, Level level, int rotation, boolean isActif) {
        rotation = rotation > 0 ? rotation : heat/4;
        if (level.getBlockState(pos).is(CNBlocks.REACTOR_OUTPUT.get()) && rotation > 0 && isActif) {
            if (level.getBlockState(pos).getBlock() instanceof ReactorOutput block) {
                ReactorOutputEntity entity = block.getBlockEntityType().getBlockEntity(level, pos);
                if (entity == null) return;
                if (state.getValue(ASSEMBLED)) { // Starting the energy
                    entity.speed = rotation;
                    entity.heat = rotation;
                } else { // stopping the energy
                    entity.speed = 0;
                    entity.heat = 0;
                }
                entity.updateSpeed = true;
                entity.updateGeneratedRotation();
                entity.setSpeed(rotation);

            }
        }
        else {
            if (level.getBlockState(pos).getBlock() instanceof ReactorOutput block) {
                ReactorOutputEntity entity = block.getBlockEntityType().getBlockEntity(level, pos);
                if (entity == null) return;
                entity.setSpeed(0);
                entity.heat = 0;
                entity.updateSpeed = true;
                entity.updateGeneratedRotation();
            }
        }
    }
}