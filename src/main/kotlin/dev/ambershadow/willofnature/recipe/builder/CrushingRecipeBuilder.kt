package dev.ambershadow.willofnature.recipe.builder

import dev.ambershadow.willofnature.index.recipe.CrushingRecipe
import net.minecraft.advancements.Advancement
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger
import net.minecraft.advancements.AdvancementRequirements.Strategy;
import net.minecraft.advancements.AdvancementRewards.Builder;
import net.minecraft.core.NonNullList
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.ItemLike

/*
{
  "group": "example_group",
  "ingredient": {
    "item": "minecraft:cobblestone"
  },
  "result": {
    "id": "minecraft:stone",
    "count": 2
  },
  "time": 100,
  "energy": 500,
  "byproducts": [
    {
      "id": "minecraft:gravel",
      "count": 1
    },
    {
      "id": "minecraft:stone",
      "count": 4
    }
  ]
}
 */
class CrushingRecipeBuilder(private val result: ItemLike, private val count: Int) : RecipeBuilder {
    private var group: String? = null;
    private lateinit var input: Ingredient
    private var time = 0
    private var byproducts: NonNullList<ItemStack> = NonNullList.create()
    private var energy = -1
    private var criteria = mutableMapOf<String, Criterion<*>>()

    fun input(input: TagKey<Item>): CrushingRecipeBuilder = input(Ingredient.of(input))
    fun input(input: ItemLike): CrushingRecipeBuilder = input(Ingredient.of(ItemStack(input)))
    fun input(input: Ingredient): CrushingRecipeBuilder {
        this.input = input
        return this
    }

    fun time(time: Int): CrushingRecipeBuilder {
        this.time = time
        return this
    }

    fun energy(energy: Int): CrushingRecipeBuilder {
        this.energy = energy
        return this
    }

    fun byproduct(item: ItemLike, count: Int = 1): CrushingRecipeBuilder {
        this.byproducts.add(ItemStack(item, count))
        return this
    }

    override fun getResult(): Item = result.asItem()

    override fun unlockedBy(string: String, criterion: Criterion<*>): RecipeBuilder {
        criteria[string] = criterion
        return this
    }

    override fun group(group: String?): RecipeBuilder {
        this.group = group
        return this
    }

    override fun save(output: RecipeOutput, id: ResourceLocation) {
        ensureValid(id)
        val advancement = output.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
            .rewards(Builder.recipe(id))
            .requirements(Strategy.OR);
        criteria.forEach(advancement::addCriterion);
        val shapelessRecipe = CrushingRecipe(group ?: "", input, ItemStack(result, count), time, byproducts, energy)
        output.accept(
            id, shapelessRecipe,
            if (criteria.isNotEmpty()) advancement.build(id.withPrefix("recipes/crushing/")) else null
        )
    }

    fun ensureValid(id: ResourceLocation) {
        if (energy < 0) throw IllegalArgumentException("Energy is less then 0 in [$id]")
        if (time <= 0) throw IllegalArgumentException("Time is less then 1 in [$id]")
    }

    companion object {
        @JvmStatic
        fun crushed(result: ItemLike, count: Int = 1) = CrushingRecipeBuilder(result, count)
    }
}