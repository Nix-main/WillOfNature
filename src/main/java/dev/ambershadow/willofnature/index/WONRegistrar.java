package dev.ambershadow.willofnature.index;

import dev.ambershadow.willofnature.WillOfNature;
import dev.ambershadow.willofnature.client.networking.UpdateFluidS2CPacket;
import dev.ambershadow.willofnature.index.networking.FillBucketC2SPacket;
import dev.ambershadow.willofnature.index.networking.UpdateFluidC2SPacket;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.function.TriFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class
WONRegistrar {
    private WONRegistrar(){}
    public static ResourceKey<CreativeModeTab> ITEMS_REGISTRY_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, WillOfNature.id("items"));
    private static final CreativeModeTab ITEMS = FabricItemGroup.builder()
            .icon(Items.STICK::getDefaultInstance)
            .title(Component.translatable("item_group.willofnature.items"))
            .build();

    public static ResourceKey<CreativeModeTab> BLOCKS_REGISTRY_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, WillOfNature.id("blocks"));
    private static final CreativeModeTab BLOCKS = FabricItemGroup.builder()
            .icon(Items.STICK::getDefaultInstance)
            .title(Component.translatable("item_group.willofnature.blocks"))
            .build();

    private static final HashMap<ResourceKey<CreativeModeTab>, List<Item>> itemGroups = new HashMap<>();

    @SafeVarargs
    static Item register(String name, Function<Item.Properties, Item> factory, Item.Properties settings, ResourceKey<CreativeModeTab>... groups){
        Item item = Registry.register(BuiltInRegistries.ITEM, WillOfNature.id(name), factory.apply(settings));
        Arrays.stream(groups).forEach(group -> itemGroups.computeIfAbsent(group, k -> new ArrayList<>()).add(item));
        return item;
    }

    static Block register(String name, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties settings){
        return register(name, factory, settings, false, (ResourceKey<CreativeModeTab>) null);
    }

    @SafeVarargs
    static Block register(String name, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties settings, boolean shouldRegisterItem, ResourceKey<CreativeModeTab>... groups){
        return register(name, factory, settings, shouldRegisterItem, null, groups);
    }

    @SafeVarargs
    static Block register(String name, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties settings, boolean shouldRegisterItem, Item.Properties itemSettings, ResourceKey<CreativeModeTab>... groups){
        Block block = Registry.register(BuiltInRegistries.BLOCK, WillOfNature.id(name), factory.apply(settings));
        if (shouldRegisterItem){
            BlockItem blockItem = new BlockItem(block, itemSettings != null ? itemSettings : new Item.Properties());
            Item item = Registry.register(BuiltInRegistries.ITEM, WillOfNature.id(name), blockItem);
            Arrays.stream(groups).forEach(group -> itemGroups.computeIfAbsent(group, k -> new ArrayList<>()).add(item));
        }
        return block;
    }

    static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType.BlockEntitySupplier<T> factory, Block... blocks){
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, WillOfNature.id(name), BlockEntityType.Builder.of(factory, blocks).build());
    }

    static <T extends AbstractContainerMenu> MenuType<T> register(String name, MenuType.MenuSupplier<T> factory, FeatureFlagSet features) {
        return Registry.register(BuiltInRegistries.MENU, WillOfNature.id(name), new MenuType<>(factory, features));
    }
    static <T extends AbstractContainerMenu, D> ExtendedScreenHandlerType<T, D> register(String name, TriFunction<Integer, Inventory, D, T> factory, StreamCodec<? super RegistryFriendlyByteBuf, D> packetCodec){
        return Registry.register(BuiltInRegistries.MENU, WillOfNature.id(name), new ExtendedScreenHandlerType<T, D>(factory::apply, packetCodec));
    }

    static <H extends RecipeInput, T extends Recipe<H>> RecipeType<T> register(String name){
        return register(name, new RecipeType<T>(){
            @Override
            public String toString() {
                return name;
            }
        });
    }

    static <H extends RecipeInput, T extends Recipe<H>> RecipeType<T> register(String name, RecipeType<T> entry){
        return Registry.register(BuiltInRegistries.RECIPE_TYPE, WillOfNature.id(name), entry);
    }

    static <H extends RecipeInput, T extends Recipe<H>> RecipeSerializer<T> register(String name, RecipeSerializer<T> entry){
        return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, WillOfNature.id(name), entry);
    }

    public static void registerAll(){
        WONItems.init();
        WONBlocks.init();
        WONBlockEntities.init();
        WONScreenHandlers.init();
        WONRecipeTypes.init();
        WONRecipeSerializers.init();
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ITEMS_REGISTRY_KEY, ITEMS);
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, BLOCKS_REGISTRY_KEY, BLOCKS);
        itemGroups.forEach((key, val) -> ItemGroupEvents.modifyEntriesEvent(key)
                .register((itemGroup) -> val.forEach(itemGroup::accept)));
        itemGroups.clear();

        PayloadTypeRegistry.playS2C().register(UpdateFluidS2CPacket.ID, UpdateFluidS2CPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(UpdateFluidC2SPacket.ID, UpdateFluidC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(FillBucketC2SPacket.ID, FillBucketC2SPacket.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(UpdateFluidC2SPacket.ID, new UpdateFluidC2SPacket.Receiver());
        ServerPlayNetworking.registerGlobalReceiver(FillBucketC2SPacket.ID, new FillBucketC2SPacket.Receiver());
    }
}
