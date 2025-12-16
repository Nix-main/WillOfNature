package dev.ambershadow.willofnature.client.prov.data

import dev.ambershadow.willofnature.WillOfNature.id
import dev.ambershadow.willofnature.recipe.builder.CrushingRecipeBuilder.Companion.crushed
import dev.ambershadow.willofnature.registration.WONItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.core.HolderLookup
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.ItemLike
import java.util.concurrent.CompletableFuture

class RecipesProvider(o: FabricDataOutput, r: CompletableFuture<HolderLookup.Provider>) : FabricRecipeProvider(o, r) {
    override fun buildRecipes(o: RecipeOutput) {
        crushed(Items.GRAVEL, 1)
            .input(Items.COBBLESTONE)
            .energy(3)
            .time(50)
            .byproduct(WONItems.STONE_DUST, 2, 0.25f)
            .save(o, id("crushing/stone"))
    }

    fun ShapedRecipeBuilder.defineUnlockedBy(c: Char, item: ItemLike): ShapedRecipeBuilder =
        define(c, item).unlockedBy(item)

    fun ShapedRecipeBuilder.defineUnlockedBy(c: Char, tag: TagKey<Item>): ShapedRecipeBuilder =
        define(c, tag).unlockedBy(tag)

    inline fun <reified T : RecipeBuilder> T.unlockedBy(item: ItemLike): T =
        unlockedBy(getHasName(item), has(item)).cast<T>()

    inline fun <reified T : RecipeBuilder> T.unlockedBy(tag: TagKey<Item>): T =
        unlockedBy("has_${tag.location.path}", has(tag)).cast<T>()


    inline fun<reified T> Any?.cast(): T = this as T
}