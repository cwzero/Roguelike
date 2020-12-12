package com.liquidforte.roguelike.builders

import com.liquidforte.roguelike.blocks.GameBlocks
import com.liquidforte.roguelike.extensions.distanceTo
import org.hexworks.zircon.api.data.Position

class StairDigger(private var levels: MutableMap<Int, Level>) {
    fun digStairs() {
        (0..(levels.size - 2)).forEach { z ->
            digStairs(z)
        }
    }

    private fun digStairs(z: Int) {
        val level = levels[z]!!
        val below = levels[z + 1]!!

        val potentialStairs = level.blocks.filter { (pos, block) ->
            block.isSmoothFloor && below.blocks[pos]!!.isSmoothFloor
        }.map { it.key }


        var stairs: Position

        if (z > 0) {
            do {
                stairs = potentialStairs.shuffled().first()

                val upStairs = levels[z - 1]!!.blocks.mapNotNull { (pos, block) ->
                    if (block.isStairsUp)
                        pos
                    else
                        null
                }

                val dist: Double = upStairs.map { it.distanceTo(stairs) }.minOrNull() ?: Double.MAX_VALUE
            } while (dist < 20)
        } else {
            stairs = potentialStairs.shuffled().first()
        }

        level[stairs] = GameBlocks.stairsDown()
        below[stairs] = GameBlocks.stairsUp()
    }
}