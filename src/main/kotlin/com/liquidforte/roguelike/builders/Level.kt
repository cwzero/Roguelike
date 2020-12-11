package com.liquidforte.roguelike.builders

import com.liquidforte.roguelike.blocks.GameBlock
import com.liquidforte.roguelike.blocks.GameBlocks
import com.liquidforte.roguelike.config.GameTiles
import com.liquidforte.roguelike.math.Position2D
import com.liquidforte.roguelike.math.toZircon
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import kotlin.random.Random

data class Level(val size: Size) {
    val blocks: MutableMap<Position, GameBlock> = mutableMapOf()
    operator fun get(pos: Position): GameBlock =
        blocks[pos]!!

    operator fun get(pos: Position2D): GameBlock =
        blocks[pos.toZircon()]!!

    operator fun set(pos: Position, value: GameBlock) {
        blocks[pos] = value
    }

    operator fun set(pos: Position2D, value: GameBlock) {
        blocks[pos.toZircon()] = value
    }

    fun fill(block: () -> GameBlock) {
        eachPosition { pos ->
            blocks[pos] = block()
        }
    }

    operator fun contains(pos: Position): Boolean {
        return pos in size
    }

    operator fun contains(pos: Position2D): Boolean {
        return pos in size
    }

    fun eachPosition(fn: (Position) -> Unit) {
        (0..size.width).forEach { x ->
            (0..size.height).forEach { y ->
                fn(Position.create(x, y))
            }
        }
    }
}

operator fun Size.contains(pos: Position): Boolean {
    return this.containsPosition(pos)
}

operator fun Size.contains(pos: Position2D): Boolean {
    return this.containsPosition(pos.toZircon())
}