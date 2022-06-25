package me.melontini.cartcopy.mixin;

import io.netty.util.internal.StringUtil;
import me.melontini.cartcopy.CartCopy;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(MinecartItem.class)
public abstract class MinecartItemMixin extends Item {
    @Final
    @Shadow
    private static final DispenserBehavior DISPENSER_BEHAVIOR = new ItemDispenserBehavior() {
        private final ItemDispenserBehavior defaultBehavior = new ItemDispenserBehavior();

        public ItemStack dispenseSilently(@NotNull BlockPointer pointer, @NotNull ItemStack stack) {
            Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
            World world = pointer.getWorld();
            double d = pointer.getX() + (double) direction.getOffsetX() * 1.125;
            double e = Math.floor(pointer.getY()) + (double) direction.getOffsetY();
            double f = pointer.getZ() + (double) direction.getOffsetZ() * 1.125;
            BlockPos blockPos = pointer.getPos().offset(direction);
            BlockState blockState = world.getBlockState(blockPos);
            RailShape railShape = blockState.getBlock() instanceof AbstractRailBlock ? blockState.get(((AbstractRailBlock) blockState.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
            double k;
            if (stack.getItem() == Items.CHEST_MINECART) {
                if (blockState.isIn(BlockTags.RAILS)) {
                    if (railShape.isAscending()) {
                        k = 0.6;
                    } else {
                        k = 0.1;
                    }
                } else {
                    if (!blockState.isAir() || !world.getBlockState(blockPos.down()).isIn(BlockTags.RAILS))
                        return this.defaultBehavior.dispense(pointer, stack);


                    BlockState blockState2 = world.getBlockState(blockPos.down());
                    RailShape railShape2 = blockState2.getBlock() instanceof AbstractRailBlock ? blockState2.get(((AbstractRailBlock) blockState2.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
                    if (direction != Direction.DOWN && railShape2.isAscending()) {
                        k = -0.4;
                    } else {
                        k = -0.9;
                    }
                }

                AbstractMinecartEntity abstractMinecartEntity = AbstractMinecartEntity.create(world, d, e + k, f, AbstractMinecartEntity.Type.CHEST);
                ChestMinecartEntity chestMinecart = (ChestMinecartEntity) abstractMinecartEntity;

                NbtCompound nbt = stack.getNbt();
                if (nbt != null) if (nbt.getList("Items", 10) != null) {
                    NbtList nbtList = nbt.getList("Items", 10);
                    for (int i = 0; i < nbtList.size(); ++i) {
                        NbtCompound nbtCompound = nbtList.getCompound(i);
                        int j = nbtCompound.getByte("Slot") & 255;
                        //noinspection ConstantConditions
                        if (j >= 0 && j < chestMinecart.size()) {
                            chestMinecart.setStack(j, ItemStack.fromNbt(nbtCompound));
                        }
                    }
                }


                if (stack.hasCustomName()) chestMinecart.setCustomName(stack.getName());

                world.spawnEntity(chestMinecart);
                stack.decrement(1);
                return stack;
            } else if (stack.getItem() == Items.HOPPER_MINECART) {
                if (blockState.isIn(BlockTags.RAILS)) {
                    if (railShape.isAscending()) {
                        k = 0.6;
                    } else {
                        k = 0.1;
                    }
                } else {
                    if (!blockState.isAir() || !world.getBlockState(blockPos.down()).isIn(BlockTags.RAILS))
                        return this.defaultBehavior.dispense(pointer, stack);


                    BlockState blockState2 = world.getBlockState(blockPos.down());
                    RailShape railShape2 = blockState2.getBlock() instanceof AbstractRailBlock ? blockState2.get(((AbstractRailBlock) blockState2.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
                    if (direction != Direction.DOWN && railShape2.isAscending()) {
                        k = -0.4;
                    } else {
                        k = -0.9;
                    }
                }

                AbstractMinecartEntity abstractMinecartEntity = AbstractMinecartEntity.create(world, d, e + k, f, AbstractMinecartEntity.Type.HOPPER);
                HopperMinecartEntity hopperMinecart = (HopperMinecartEntity) abstractMinecartEntity;

                NbtCompound nbt = stack.getNbt();
                if (nbt != null) if (nbt.getList("Items", 10) != null) {
                    NbtList nbtList = nbt.getList("Items", 10);
                    for (int i = 0; i < nbtList.size(); ++i) {
                        NbtCompound nbtCompound = nbtList.getCompound(i);
                        int j = nbtCompound.getByte("Slot") & 255;
                        //noinspection ConstantConditions
                        if (j >= 0 && j < hopperMinecart.size()) {
                            hopperMinecart.setStack(j, ItemStack.fromNbt(nbtCompound));
                        }
                    }
                }


                if (stack.hasCustomName()) hopperMinecart.setCustomName(stack.getName());

                world.spawnEntity(hopperMinecart);
                stack.decrement(1);
                return stack;
            } else if (stack.getItem() == Items.FURNACE_MINECART) {
                if (blockState.isIn(BlockTags.RAILS)) {
                    if (railShape.isAscending()) {
                        k = 0.6;
                    } else {
                        k = 0.1;
                    }
                } else {
                    if (!blockState.isAir() || !world.getBlockState(blockPos.down()).isIn(BlockTags.RAILS))
                        return this.defaultBehavior.dispense(pointer, stack);


                    BlockState blockState2 = world.getBlockState(blockPos.down());
                    RailShape railShape2 = blockState2.getBlock() instanceof AbstractRailBlock ? blockState2.get(((AbstractRailBlock) blockState2.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
                    if (direction != Direction.DOWN && railShape2.isAscending()) {
                        k = -0.4;
                    } else {
                        k = -0.9;
                    }
                }

                AbstractMinecartEntity abstractMinecartEntity = AbstractMinecartEntity.create(world, d, e + k, f, AbstractMinecartEntity.Type.FURNACE);
                FurnaceMinecartEntity furnaceMinecart = (FurnaceMinecartEntity) abstractMinecartEntity;

                NbtCompound nbt = stack.getNbt();
                //Just making sure
                if (nbt != null) if (!(nbt.getInt("Fuel") <= 0)) {
                    furnaceMinecart.fuel = Math.min(nbt.getInt("Fuel"), world.getGameRules().getInt(CartCopy.MAX_FURNACE_FUEL));
                    furnaceMinecart.pushX = furnaceMinecart.getX() - blockPos.getX();
                    furnaceMinecart.pushZ = furnaceMinecart.getZ() - blockPos.getZ();
                }


                if (stack.hasCustomName()) furnaceMinecart.setCustomName(stack.getName());


                world.spawnEntity(furnaceMinecart);
                stack.decrement(1);
                return stack;
            } else if (!(stack.getItem() == CartCopy.SPAWNER_MINECART)) {
                if (blockState.isIn(BlockTags.RAILS)) {
                    if (railShape.isAscending()) {
                        k = 0.6;
                    } else {
                        k = 0.1;
                    }
                } else {
                    if (!blockState.isAir() || !world.getBlockState(blockPos.down()).isIn(BlockTags.RAILS))
                        return this.defaultBehavior.dispense(pointer, stack);


                    BlockState blockState2 = world.getBlockState(blockPos.down());
                    RailShape railShape2 = blockState2.getBlock() instanceof AbstractRailBlock ? blockState2.get(((AbstractRailBlock) blockState2.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
                    if (direction != Direction.DOWN && railShape2.isAscending()) {
                        k = -0.4;
                    } else {
                        k = -0.9;
                    }
                }

                AbstractMinecartEntity abstractMinecartEntity = AbstractMinecartEntity.create(world, d, e + k, f, ((MinecartItem) stack.getItem()).type);
                if (stack.hasCustomName()) abstractMinecartEntity.setCustomName(stack.getName());


                world.spawnEntity(abstractMinecartEntity);
                stack.decrement(1);
                return stack;
            }
            return stack;
        }

        protected void playSound(@NotNull BlockPointer pointer) {
            pointer.getWorld().syncWorldEvent(1000, pointer.getPos(), 0);
        }
    };
    private static final Logger LOGGER = LogManager.getLogger();
    @Shadow
    @Final
    public AbstractMinecartEntity.Type type;

    public MinecartItemMixin(Settings settings) {
        super(settings);
        DispenserBlock.registerBehavior(this, DISPENSER_BEHAVIOR);
    }

    @Inject(at = @At("HEAD"), method = "useOnBlock", cancellable = true)
    public void cartCopy$useOnStuff(@NotNull ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        ItemStack stack = context.getStack();
        PlayerEntity player = context.getPlayer();

        assert player != null;
        if (state.isIn(BlockTags.RAILS)) {
            if (stack.getItem() == Items.CHEST_MINECART) {
                if (!world.isClient) {
                    RailShape railShape = state.getBlock() instanceof AbstractRailBlock ? state.get(((AbstractRailBlock) state.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
                    double d = 0.0;
                    if (railShape.isAscending()) d = 0.5;


                    AbstractMinecartEntity abstractMinecartEntity = AbstractMinecartEntity.create(world, (double) pos.getX() + 0.5, (double) pos.getY() + 0.0625 + d, (double) pos.getZ() + 0.5, this.type);
                    ChestMinecartEntity chestMinecart = (ChestMinecartEntity) abstractMinecartEntity;

                    NbtCompound nbt = stack.getNbt();
                    if (nbt != null) if (nbt.getList("Items", 10) != null) {
                        NbtList nbtList = nbt.getList("Items", 10);
                        for (int i = 0; i < nbtList.size(); ++i) {
                            NbtCompound nbtCompound = nbtList.getCompound(i);
                            int j = nbtCompound.getByte("Slot") & 255;
                            //noinspection ConstantConditions
                            if (j >= 0 && j < chestMinecart.size()) {
                                chestMinecart.setStack(j, ItemStack.fromNbt(nbtCompound));
                            }
                        }
                    }

                    if (stack.hasCustomName()) chestMinecart.setCustomName(stack.getName());

                    world.spawnEntity(chestMinecart);
                }

                if (!player.isCreative()) stack.decrement(1);
                cir.setReturnValue(ActionResult.success(world.isClient));
            }
            if (stack.getItem() == Items.HOPPER_MINECART) {
                if (!world.isClient) {
                    RailShape railShape = state.getBlock() instanceof AbstractRailBlock ? state.get(((AbstractRailBlock) state.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
                    double d = 0.0;
                    if (railShape.isAscending()) d = 0.5;

                    AbstractMinecartEntity abstractMinecartEntity = AbstractMinecartEntity.create(world, (double) pos.getX() + 0.5, (double) pos.getY() + 0.0625 + d, (double) pos.getZ() + 0.5, this.type);
                    HopperMinecartEntity hopperMinecart = (HopperMinecartEntity) abstractMinecartEntity;

                    NbtCompound nbt = stack.getNbt();
                    if (nbt != null) if (nbt.getList("Items", 10) != null) {
                        NbtList nbtList = nbt.getList("Items", 10);
                        for (int i = 0; i < nbtList.size(); ++i) {
                            NbtCompound nbtCompound = nbtList.getCompound(i);
                            int j = nbtCompound.getByte("Slot") & 255;
                            //noinspection ConstantConditions
                            if (j >= 0 && j < hopperMinecart.size()) {
                                hopperMinecart.setStack(j, ItemStack.fromNbt(nbtCompound));
                            }
                        }
                    }

                    if (stack.hasCustomName()) hopperMinecart.setCustomName(stack.getName());

                    world.spawnEntity(hopperMinecart);
                }

                if (!player.isCreative()) stack.decrement(1);
                cir.setReturnValue(ActionResult.success(world.isClient));
            }
            if (stack.getItem() == Items.FURNACE_MINECART) {
                if (!world.isClient) {
                    RailShape railShape = state.getBlock() instanceof AbstractRailBlock ? state.get(((AbstractRailBlock) state.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
                    double d = 0.0;
                    if (railShape.isAscending()) d = 0.5;


                    AbstractMinecartEntity abstractMinecartEntity = AbstractMinecartEntity.create(world, (double) pos.getX() + 0.5, (double) pos.getY() + 0.0625 + d, (double) pos.getZ() + 0.5, this.type);
                    FurnaceMinecartEntity furnaceMinecart = (FurnaceMinecartEntity) abstractMinecartEntity;

                    NbtCompound nbt = stack.getNbt();
                    //Just making sure
                    if (nbt != null) if (!(nbt.getInt("Fuel") <= 0)) {
                        furnaceMinecart.fuel = Math.min(nbt.getInt("Fuel"), world.getGameRules().getInt(CartCopy.MAX_FURNACE_FUEL));
                        furnaceMinecart.interact(player, player.getActiveHand());
                    }

                    if (stack.hasCustomName()) furnaceMinecart.setCustomName(stack.getName());

                    world.spawnEntity(furnaceMinecart);
                }

                if (!player.isCreative()) stack.decrement(1);
                cir.setReturnValue(ActionResult.success(world.isClient));
            }
        }
        if (player.isSneaking()) {
            if (state.isOf(Blocks.CHEST) && stack.getItem() == Items.MINECART) {
                if (!world.isClient) {
                    ChestBlockEntity chestBlockEntity = (ChestBlockEntity) world.getBlockEntity(pos);
                    if (!player.isCreative()) stack.decrement(1);
                    ItemStack chestMinecart = new ItemStack(Items.CHEST_MINECART, 1);

                    NbtCompound nbt = new NbtCompound();
                    NbtList nbtList = new NbtList();
                    assert chestBlockEntity != null;
                    for (int i = 0; i < chestBlockEntity.size(); ++i) {
                        ItemStack itemStack = chestBlockEntity.getStack(i);
                        if (!itemStack.isEmpty()) {
                            NbtCompound nbtCompound = new NbtCompound();
                            nbtCompound.putByte("Slot", (byte) i);
                            itemStack.writeNbt(nbtCompound);
                            nbtList.add(nbtCompound);
                        }
                    }
                    nbt.put("Items", nbtList);
                    chestMinecart.setNbt(nbt);

                    player.inventory.insertStack(chestMinecart);
                    chestBlockEntity.inventory.clear();
                    world.breakBlock(pos, false);
                }
                cir.setReturnValue(ActionResult.success(world.isClient));
            }
            if (state.isOf(Blocks.SPAWNER) && stack.getItem() == Items.MINECART) {
                if (world.getGameRules().getBoolean(CartCopy.ALLOW_PICKING_SPAWNERS)) {
                    if (!world.isClient) {
                        MobSpawnerBlockEntity mobSpawnerBlockEntity = (MobSpawnerBlockEntity) world.getBlockEntity(pos);
                        if (!player.isCreative()) stack.decrement(1);
                        ItemStack spawnerMinecart = new ItemStack(CartCopy.SPAWNER_MINECART, 1);

                        NbtCompound nbtCompound = new NbtCompound();
                        assert mobSpawnerBlockEntity != null : "Somehow, MobSpawnerBlockEntity was null!";
                        nbtCompound.putString("Entity", String.valueOf(getEntityId(mobSpawnerBlockEntity)));
                        spawnerMinecart.setNbt(nbtCompound);

                        if (stack.hasCustomName()) {
                            spawnerMinecart.setCustomName(new TranslatableText("item.cart-copy.spawner_minecart.filled.custom", stack.getName(), Registry.ENTITY_TYPE.get(getEntityId(mobSpawnerBlockEntity)).getName()).formatted(Formatting.RESET));
                        } else {
                            spawnerMinecart.setCustomName(new TranslatableText("item.cart-copy.spawner_minecart.filled", Registry.ENTITY_TYPE.get(getEntityId(mobSpawnerBlockEntity)).getName()).formatted(Formatting.RESET));
                        }

                        player.inventory.insertStack(spawnerMinecart);
                        world.breakBlock(pos, false);
                    }
                    cir.setReturnValue(ActionResult.success(world.isClient));
                } else {
                    cir.setReturnValue(ActionResult.CONSUME);
                }
            }
            if (state.isOf(Blocks.TNT) && stack.getItem() == Items.MINECART) {
                if (!world.isClient) {
                    if (!player.isCreative()) stack.decrement(1);
                    ItemStack tntMinecart = new ItemStack(Items.TNT_MINECART, 1);

                    player.inventory.insertStack(tntMinecart);
                    world.breakBlock(pos, false);
                }
                cir.setReturnValue(ActionResult.success(world.isClient));
            }
            if (state.isOf(Blocks.FURNACE) && stack.getItem() == Items.MINECART) {
                if (!world.isClient) {
                    if (!player.isCreative()) stack.decrement(1);
                    AbstractFurnaceBlockEntity furnaceBlock = (AbstractFurnaceBlockEntity) world.getBlockEntity(pos);
                    ItemStack furnaceMinecart = new ItemStack(Items.FURNACE_MINECART, 1);
                    //2.25
                    assert furnaceBlock != null;
                    int burnTime = furnaceBlock.burnTime;
                    int fuel = (int) (burnTime * 2.25);

                    NbtCompound nbt = new NbtCompound();
                    nbt.putInt("Fuel", fuel);
                    furnaceMinecart.setNbt(nbt);

                    player.inventory.insertStack(furnaceMinecart);
                    world.breakBlock(pos, false);
                }
                cir.setReturnValue(ActionResult.success(world.isClient));
            }
            if (state.isOf(Blocks.HOPPER) && stack.getItem() == Items.MINECART) {
                if (!world.isClient) {
                    HopperBlockEntity hopperBlockEntity = (HopperBlockEntity) world.getBlockEntity(pos);
                    if (!player.isCreative()) stack.decrement(1);
                    ItemStack hopperMinecart = new ItemStack(Items.HOPPER_MINECART, 1);

                    NbtCompound nbt = new NbtCompound();
                    NbtList nbtList = new NbtList();
                    assert hopperBlockEntity != null;
                    for (int i = 0; i < hopperBlockEntity.size(); ++i) {
                        ItemStack itemStack = hopperBlockEntity.getStack(i);
                        if (!itemStack.isEmpty()) {
                            NbtCompound nbtCompound = new NbtCompound();
                            nbtCompound.putByte("Slot", (byte) i);
                            itemStack.writeNbt(nbtCompound);
                            nbtList.add(nbtCompound);
                        }
                    }
                    nbt.put("Items", nbtList);
                    hopperMinecart.setNbt(nbt);

                    player.inventory.insertStack(hopperMinecart);
                    hopperBlockEntity.inventory.clear();
                    world.breakBlock(pos, false);
                }
                cir.setReturnValue(ActionResult.success(world.isClient));
            }
        }
    }
    @Nullable
    private Identifier getEntityId(MobSpawnerBlockEntity mobSpawnerBlockEntity) {
        String string = mobSpawnerBlockEntity.logic.spawnEntry.getNbt().getString("id");

        try {
            return StringUtils.isEmpty(string) ? null : new Identifier(string);
        } catch (InvalidIdentifierException var4) {
            BlockPos blockPos = mobSpawnerBlockEntity.getPos();
            LOGGER.warn("Invalid entity id '{}' at spawner {}:[{},{},{}]", string, Objects.requireNonNull(mobSpawnerBlockEntity.getWorld()).getRegistryKey().getValue(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
            return null;
        }
    }
}
