package dev.ambershadow.willofnature.util

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import java.util.*
import java.util.Map
import java.util.function.Function
import java.util.function.ToIntFunction
import java.util.stream.Collectors

abstract class SimpleRectangularMultiblock : Multiblock {

    private fun listFromTriple(triple: Triple<Int, Int, Int>): List<Int> =
        (triple.first..triple.second step triple.third).toList()

    protected val outerEdgeBlocks = mutableListOf<Block>()
    protected val outerEdgeTags = mutableListOf<TagKey<Block>>()
    protected val innerEdgeBlocks = mutableListOf<Block>()
    protected val innerEdgeTags = mutableListOf<TagKey<Block>>()

    protected fun addOuterEdgeBlock(block: Block) { outerEdgeBlocks.add(block) }
    protected fun addOuterEdgeBlock(blockTag: TagKey<Block>){ outerEdgeTags.add(blockTag) }
    protected fun fetchOuterEdgeBlocks() : List<Block> = getBlocks(outerEdgeBlocks, outerEdgeTags)

    protected fun addInnerEdgeBlock(block: Block) { innerEdgeBlocks.add(block) }
    protected fun addInnerEdgeBlock(blockTag: TagKey<Block>){ innerEdgeTags.add(blockTag) }
    protected fun fetchInnerEdgeBlocks() : List<Block> = getBlocks(innerEdgeBlocks, innerEdgeTags)

    private fun getBlocks(blocks: List<Block>, tags: List<TagKey<Block>>) : List<Block>{
        val blks = mutableListOf<Block>()
        blks.addAll(blocks)
        BuiltInRegistries.BLOCK.forEach { block ->
            tags.forEach { tag ->
                block.defaultBlockState().`is`(tag)
                blks.add(block)
            }
        }
        return blks
    }

    private val height: Triple<Int, Int, Int>
    private val width: Triple<Int, Int, Int>
    private val length: Triple<Int, Int, Int>

    private val heightValues: List<Int>
    private val widthValues: List<Int>
    private val lengthValues: List<Int>


    protected abstract fun validateInterior(
        layers: MutableList<MutableList<BlockPos>>,
        level: Level,
        width: Int,
        length: Int,
        height: Int
    ) : Boolean

    protected open fun validateExterior(
        layers: MutableList<MutableList<BlockPos>>,
        level: Level,
        width: Int,
        length: Int,
        height: Int
    ) : Boolean {
        if (layers.size != height) {
            return false
        }

        for (i in layers.indices) {
            val layer: MutableList<BlockPos> = layers[i]
            val expectedCount: Int = if (i == 0 || i == layers.size - 1) {
                width * length
            } else {
                2 * width + 2 * length - 4
            }
            if (layer.size != expectedCount) {
                return false
            }
        }
        val min: BlockPos = layers.first().first()
        val max: BlockPos = layers.last().last()
        val minX = min.x
        val minY = min.y
        val minZ = min.z
        val maxX = max.x
        val maxY = max.y
        val maxZ = max.z
        for (y in minY..maxY) {
            for (z in minZ..maxZ) {
                for (x in minX..maxX) {
                    val pos = BlockPos(x, y, z)
                    val state: BlockState = level.getBlockState(pos)

                    val isCorner = (x == minX || x == maxX) && (z == minZ || z == maxZ)
                    val isWall =
                        (x == minX || x == maxX || z == minZ || z == maxZ)
                    val isOuterEdge = isWall && ((y == minY || y == maxY) || isCorner)
                    val isInnerEdge = isWall && !isOuterEdge

                    if (isOuterEdge && !fetchOuterEdgeBlocks().contains(state.block)) return false
                    if (isInnerEdge && !fetchInnerEdgeBlocks().contains(state.block)) return false
                }
            }
        }

        return true
    }

    private var sameSides: Boolean = false

    constructor(loc: ResourceLocation, height: Triple<Int, Int, Int>,
                sides: Triple<Int, Int, Int>,
                sameSides: Boolean) : this(loc, height, sides, sides){
        this.sameSides = sameSides
    }
    constructor(loc: ResourceLocation, height: Triple<Int, Int, Int>,
                width: Triple<Int, Int, Int>,
                length: Triple<Int, Int, Int>) : super(loc) {
        this.height = height
        this.width = width
        this.length = length
        heightValues = listFromTriple(height)
        widthValues = listFromTriple(width)
        lengthValues = listFromTriple(length)
    }


    protected open fun collectStructure(start: BlockPos, world: Level): MutableList<MutableList<BlockPos>> {
        val visited: MutableList<BlockPos> = ArrayList()
        val queue: Queue<BlockPos> = ArrayDeque<BlockPos>()
        queue.add(start)

        while (!queue.isEmpty()) {
            val current = queue.poll()
            if (visited.contains(current)) continue

            val state = world.getBlockState(current)
            if (!(fetchInnerEdgeBlocks().contains(state.block) || fetchOuterEdgeBlocks().contains(state.block))) {
                continue
            }

            visited.add(current)

            for (dir in Direction.entries) {
                queue.add(current.relative(dir))
            }
        }
        val grouped = visited.stream()
            .collect(Collectors.groupingBy(Function { obj: BlockPos -> obj.y }))

        return grouped.entries.stream()
            .sorted(Map.Entry.comparingByKey<Int, MutableList<BlockPos>>(Comparator.naturalOrder()))
            .map<MutableList<BlockPos>> { entry: MutableMap.MutableEntry<Int, MutableList<BlockPos>> ->
                entry.value.stream()
                    .sorted(
                        Comparator
                            .comparingInt<BlockPos?>(ToIntFunction { pos: BlockPos -> pos.z })
                            .thenComparingInt(ToIntFunction { obj: BlockPos? -> obj!!.x })
                    )
                    .toList()
            }
            .toList()
    }

    protected open fun collectInterior(blocks: MutableList<MutableList<BlockPos>>): MutableList<MutableList<BlockPos>> {
        val min: BlockPos = blocks.first().first().offset(1, 1, 1)
        val max: BlockPos = blocks.last().last().offset(-1, -1, -1)
        val minX = min.x
        val minY = min.y
        val minZ = min.z
        val maxX = max.x
        val maxY = max.y
        val maxZ = max.z
        val layers = mutableListOf<MutableList<BlockPos>>()
        for (y in minY..maxY) {
            val layer = mutableListOf<BlockPos>()
            layers.add(layer)
            for (z in minZ..maxZ) {
                for (x in minX..maxX) {
                    val pos = BlockPos(x, y, z)
                    layer.add(pos)
                }
            }
        }
        return layers
    }

    protected fun validateSize(layers: MutableList<MutableList<BlockPos>>) : Boolean {
        val expectedWidth = getWidth(layers.first())
        val expectedLength = getLength(layers.first())
        if (!widthValues.contains(expectedWidth) ||
            !lengthValues.contains(expectedLength) ||
            !heightValues.contains(layers.size))
            return false
        val consistent = layers.all { getWidth(it) == expectedWidth && getLength(it) == expectedLength }
        return consistent && (!sameSides || expectedWidth == expectedLength)
    }

    private fun getWidth(layer: MutableList<BlockPos>) : Int {
        val minX: Int = layer.stream().mapToInt { obj: BlockPos? -> obj!!.x }.min().orElseThrow()
        val maxX: Int = layer.stream().mapToInt { obj: BlockPos? -> obj!!.x }.max().orElseThrow()
        return (maxX - minX) + 1
    }

    private fun getLength(layer: MutableList<BlockPos>) : Int {
        val minZ: Int = layer.stream().mapToInt { obj: BlockPos? -> obj!!.z }.min().orElseThrow()
        val maxZ: Int = layer.stream().mapToInt { obj: BlockPos? -> obj!!.z }.max().orElseThrow()
        return (maxZ - minZ) + 1
    }

    override fun validate(pos: BlockPos, level: Level) : Boolean {
        val block = level.getBlockState(pos).block
        if (!(fetchInnerEdgeBlocks().contains(block) || fetchOuterEdgeBlocks().contains(block)))
            for (dir in Direction.entries) {
                val newBlock = level.getBlockState(pos.relative(dir)).block
                if (fetchInnerEdgeBlocks().contains(newBlock) || fetchOuterEdgeBlocks().contains(newBlock)) {
                    return validate(pos.relative(dir), level)
                }
            }
        val lists = collectStructure(pos, level)
        if (lists.isEmpty()) return false
        val width = getWidth(lists.first())
        val length = getLength(lists.first())
        val height = lists.size
        return validateInterior(collectInterior(lists), level, width - 2, length - 2, height - 2)
                && validateExterior(lists, level, width, length, height)
                && validateSize(lists)
    }
}