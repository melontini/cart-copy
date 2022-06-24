package me.melontini.cartcopy;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;

public class CartCopy implements ModInitializer {
    public static final Item SPAWNER_MINECART = new SpawnerMinecartItem(AbstractMinecartEntity.Type.SPAWNER, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1));

    public static final GameRules.Key<GameRules.BooleanRule> ALLOW_PICKING_SPAWNERS =
            GameRuleRegistry.register("CartCopyAllowPickingSpawners", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));


    /**
     * This is here for the sake of compat with mods that edit max fuel int of furnace minecarts.
     */
    public static final GameRules.Key<GameRules.IntRule> MAX_FURNACE_FUEL =
            GameRuleRegistry.register("CartCopyMaxFurnaceMinecartFuel", GameRules.Category.MISC, GameRuleFactory.createIntRule(32000));

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier("cart-copy", "spawner_minecart"), SPAWNER_MINECART);
    }
}
