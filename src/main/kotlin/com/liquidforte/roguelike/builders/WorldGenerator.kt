package com.liquidforte.roguelike.builders

import com.liquidforte.roguelike.blocks.GameBlock
import com.liquidforte.roguelike.config.GameConfig
import com.liquidforte.roguelike.world.World
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableMap
import org.hexworks.zircon.api.data.Position3D
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.data.Size3D

abstract class WorldGenerator(private val worldSize: Size3D = GameConfig.WORLD_SIZE) {
    protected val width = worldSize.xLength - 1
    protected val height = worldSize.yLength - 1
    protected val depth = worldSize.zLength - 1
    protected var levels: MutableMap<Int, Level> = mutableMapOf()

    protected val blocks: ImmutableMap<Position3D, GameBlock>
        get() {
            val temp: MutableMap<Position3D, GameBlock> = mutableMapOf()

            levels.forEach() { index, level ->
                level.eachPosition {
                    val pos = Position3D.create(it.x, it.y, depth - index)
                    level.blocks[it]?.let { block ->
                        temp[pos] = block
                    }
                }
            }

            return temp.toImmutableMap()
        }

    init {
        (0..depth).forEach {
            levels[it] = Level(Size.create(width, height))
        }
    }

    abstract fun generate() : WorldGenerator

    fun build(visibleSize: Size3D): World = World(blocks, visibleSize, worldSize)

    protected fun forAllPositions(fn: (Position3D) -> Unit) {
        worldSize.fetchPositions().forEach(fn)
    }
}