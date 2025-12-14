package dev.ambershadow.willofnature.mixin;

import dev.ambershadow.willofnature.index.recipe.WONBlastingRecipe;
import dev.ambershadow.willofnature.index.recipe.WONSmeltingRecipe;
import dev.ambershadow.willofnature.util.BlastingRecipeInput;
import dev.ambershadow.willofnature.util.SmeltingRecipeInput;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin {
    @Shadow
    protected NonNullList<ItemStack> items;

    @WrapOperation(
            method = "serverTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/crafting/RecipeManager$CachedCheck;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"
            )
    )
    private static Optional<RecipeHolder<?>> matchRecipe(
            RecipeManager.CachedCheck<?, ?> instance,
            RecipeInput input,
            Level world,
            Operation<Optional<RecipeHolder<?>>> original,
            Level tickWorld,
            BlockPos pos,
            BlockState state,
            AbstractFurnaceBlockEntity blockEntity
    ) {
        NonNullList<ItemStack> inv = ((AbstractFurnaceBlockEntityMixin)(Object)blockEntity).items;
        RecipeManager rm = world.getRecipeManager();
        for (RecipeHolder<?> entry : rm.getRecipes()) {
            if (entry.value() instanceof WONSmeltingRecipe won) {
                if (inv.size() <= 6) {
                    return original.call(instance, input, world);
                }
                ItemStack slot0 = inv.get(0);
                ItemStack slot6 = inv.get(6);
                RecipeInput custom = new SmeltingRecipeInput(slot0, slot6, List.of(inv.get(3), inv.get(4), inv.get(5)));
                if (won.matches(custom, world)) {
                    return Optional.of(entry);
                }
            }
            if (entry.value()instanceof WONBlastingRecipe won){
                if (inv.size() <= 5) {
                    return original.call(instance, input, world);
                }
                ItemStack slot0 = inv.get(0);
                RecipeInput custom = new BlastingRecipeInput(slot0, List.of(inv.get(3), inv.get(4), inv.get(5)),
                        won.getFluid(), won.getFluidAmount(), blockEntity);
                if (won.matches(custom, world)) {
                    return Optional.of(entry);
                }
            }
        }

        return original.call(instance, input, world);
    }

    @Inject(method = "burn", at = @At("HEAD"))
    private static void consumeSecondIngredient(RegistryAccess registryManager, RecipeHolder<?> recipe, NonNullList<ItemStack> slots, int count, CallbackInfoReturnable<Boolean> cir) {
        if (recipe != null && recipe.value() instanceof WONSmeltingRecipe wonRecipe) {
            if (wonRecipe.hasSecondIngredient() && slots.size() > 6) {
                slots.get(6).shrink(1);
            }
        }
    }

    @Inject(method = "burn", at = @At("RETURN"))
    private static void onCraftRecipe(RegistryAccess registryManager, RecipeHolder<?> recipe, NonNullList<ItemStack> slots, int count, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && recipe != null) {
            List<ItemStack> byproducts;
            if (recipe.value() instanceof WONSmeltingRecipe wonRecipe)
                byproducts = wonRecipe.getByproducts(registryManager);
            else if (recipe.value() instanceof WONBlastingRecipe wonRecipe)
                byproducts = wonRecipe.getByproducts(registryManager);
            else return;
            for (int i = 0; i < Math.min(byproducts.size(), 3); i++) {
                int slotIndex = 3 + i;
                if (slots.size() > slotIndex) {
                    ItemStack byproduct = byproducts.get(i);
                    if (!byproduct.isEmpty()) {
                        ItemStack byproductSlot = slots.get(slotIndex);
                        if (byproductSlot.isEmpty()) {
                            slots.set(slotIndex, byproduct.copy());
                        } else if (ItemStack.isSameItemSameComponents(byproductSlot, byproduct)) {
                            byproductSlot.grow(byproduct.getCount());
                        }
                    }
                }
            }
        }
    }

    @Unique
    private static final Map<AbstractFurnaceBlockEntity, ItemStack> TEMP_SLOT0_COPIES = Collections.synchronizedMap(new WeakHashMap<>());

    @Inject(method = "serverTick", at = @At("HEAD"), cancellable = true)
    private static void preventVanillaFurnaceTick(Level world, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity, CallbackInfo ci) {
        if (state.getBlock() == Blocks.FURNACE || state.getBlock() == Blocks.BLAST_FURNACE) {
            ci.cancel();
        }

        NonNullList<ItemStack> inv = ((AbstractFurnaceBlockEntityMixin)(Object)blockEntity).items;
        if (inv.size() <= 6) return;

        ItemStack slot0 = inv.get(0);
        ItemStack slot6 = inv.get(6);

        if (slot0.isEmpty() && !slot6.isEmpty()) {
            inv.set(0, slot6.copy());
            TEMP_SLOT0_COPIES.put(blockEntity, ItemStack.EMPTY);
        }
    }

    @Inject(method = "serverTick", at = @At("RETURN"))
    private static void postTickRestoreSlot0(Level world, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity, CallbackInfo ci) {
        if (TEMP_SLOT0_COPIES.remove(blockEntity) != null) {
            NonNullList<ItemStack> inv = ((AbstractFurnaceBlockEntityMixin)(Object)blockEntity).items;
            inv.set(0, ItemStack.EMPTY);
        }
    }
    @Shadow @Final private Object2IntOpenHashMap<ResourceLocation> recipesUsed;

    @Inject(method = "getRecipesToAwardAndPopExperience", at = @At("HEAD"), cancellable = true)
    private void onGetRecipesUsedAndDropExperience(ServerLevel world, Vec3 pos, CallbackInfoReturnable<List<RecipeHolder<?>>> cir) {
        List<RecipeHolder<?>> unlocked = new ArrayList<>();

        for (ResourceLocation id : recipesUsed.keySet()) {
            RecipeHolder<?> entry = world.getRecipeManager().byKey(id).orElse(null);
            if (entry != null && entry.value() instanceof WONSmeltingRecipe won) {
                float xp = won.getExperience();
                int count = recipesUsed.getOrDefault(id, 0);
                int totalXp = Mth.floor(xp * count);
                ExperienceOrb.award(world, pos, totalXp);

                unlocked.add(entry);
            }
        }

        cir.setReturnValue(unlocked);
        cir.cancel();
    }
}
