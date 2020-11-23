package com.liquidforte.roguelike.builders

import com.liquidforte.roguelike.blocks.GameBlock
import com.liquidforte.roguelike.blocks.GameBlocks
import com.liquidforte.roguelike.config.GameConfig.WORLD_SIZE
import com.liquidforte.roguelike.extensions.below
import com.liquidforte.roguelike.extensions.sameLevelNeighborsShuffled
import com.liquidforte.roguelike.world.World
import org.hexworks.zircon.api.data.Position3D
import org.hexworks.zircon.api.data.Size3D
import kotlin.random.Random

class WorldBuilder(private val worldSize: Size3D = WORLD_SIZE) {
    private val width = worldSize.xLength
    private val depth = worldSize.yLength
    private val height = worldSize.zLength
    private var blocks: MutableMap<Position3D, GameBlock> = mutableMapOf()

    fun makeCaves(): WorldBuilder {
        return randomizeTiles()
                .smooth(8)
                .connectLevels()
    }


    fun build(visibleSize: Size3D): World = World(blocks, visibleSize, worldSize)

    private fun randomizeTiles(): WorldBuilder {
        forAllPositions { pos ->
            blocks[pos] = if (Math.random() < 0.5) {
                GameBlocks.floor()
            } else GameBlocks.wall()
        }
        return this
    }

    private fun connectLevels() = also {
        (height - 1).downTo(1).forEach(::connectRegionDown)
    }

    private fun smooth(iterations: Int): WorldBuilder {
        val newBlocks = mutableMapOf<Position3D, GameBlock>()
        repeat(iterations) {
            forAllPositions { pos ->
                val (x, y, z) = pos
                var floors = 0
                var rocks = 0
                pos.sameLevelNeighborsShuffled().plus(pos).forEach { neighbor ->
                    blocks.whenPresent(neighbor) { block ->
                        if (block.isEmptyFloor) {
                            floors++
                        } else rocks++
                    }
                }
                newBlocks[Position3D.create(x, y, z)] = if (floors >= rocks) GameBlocks.floor() else GameBlocks.wall()
            }
            blocks = newBlocks
        }
        return this
    }

    private fun connectRegionDown(currentLevel: Int) {
        val posToConnect = generateRandomFloorPositionsOn(currentLevel)
                .first { pos ->
                    blocks[pos].isEmptyFloor() && blocks[pos.below()].isEmptyFloor()
                }
        blocks[posToConnect] = GameBlocks.stairsDown()
        blocks[posToConnect.below()] = GameBlocks.stairsUp()

    }

    private fun generateRandomFloorPositionsOn(level: Int) = sequence {
        while (true) {
            var pos = Position3D.unknown()
            while (pos.isUnknown) {
                val candidate = Position3D.create(
                        x = Random.nextInt(width - 1),
                        y = Random.nextInt(depth - 1),
                        z = level)
                if (blocks[candidate].isEmptyFloor()) {
                    pos = candidate
                }
            }
            yield(pos)
        }
    }

    private fun GameBlock?.isEmptyFloor(): Boolean {
        return this?.isEmptyFloor ?: false
    }


    private fun forAllPositions(fn: (Position3D) -> Unit) {
        worldSize.fetchPositions().forEach(fn)
    }

    private fun MutableMap<Position3D, GameBlock>.whenPresent(pos: Position3D, fn: (GameBlock) -> Unit) {
        this[pos]?.let(fn)
    }
}