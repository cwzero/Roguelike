package com.liquidforte.roguelike.extensions

import com.liquidforte.roguelike.blocks.GameBlock
import kotlinx.collections.immutable.toImmutableList
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Position3D
import org.hexworks.zircon.api.data.Size

fun Position3D.sameLevelNeighborsShuffled(): List<Position3D> {
    return (-1..1).flatMap { x ->
        (-1..1).map { y ->
            withRelativeX(x).withRelativeY(y)
        }
    }.minus(this).shuffled()
}

fun Position3D.below() = copy(z = z - 1)

fun GameBlock?.isEmptyFloor(): Boolean {
    return this?.isEmptyFloor ?: false
}

fun MutableMap<Position3D, GameBlock>.whenPresent(pos: Position3D, fn: (GameBlock) -> Unit) {
    this[pos]?.let(fn)
}

fun Position.distanceTo(x: Int, y: Int): Double {
    val deltaX = this.x - x
    val deltaY = this.y - y
    val d2 = (deltaX * deltaX) + (deltaY * deltaY)
    return Math.sqrt(d2.toDouble())
}

fun Position.distanceTo(pos: Position): Double {
    val deltaX = x - pos.x
    val deltaY = y - pos.y
    val d2 = (deltaX * deltaX) + (deltaY * deltaY)
    return Math.sqrt(d2.toDouble())
}

val Position.neighbors: List<Position>
    get() = mutableListOf<Position>().apply {
        (-1..1).forEach { deltaX ->
            (-1..1).forEach { deltaY ->
                if (deltaX != 0 || deltaY != 0)
                    add(Position.create(x + deltaX, y + deltaY))
            }
        }
        assert(!contains(this@neighbors))
    }.distinct().toImmutableList()

val Position.orthogonalNeighbors: List<Position>
    get() = mutableListOf<Position>().apply {
        listOf(-1, 1).forEach { deltaX ->
            add(Position.create(x + deltaX, y))
        }
        listOf(-1, 1).forEach { deltaY ->
            add(Position.create(x, y + deltaY))
        }
        assert(!contains(this@orthogonalNeighbors))
    }.distinct().toImmutableList()

fun Size.eachPosition(fn: (Position) -> Unit) {
    (0..width).forEach { x ->
        (0..height).forEach { y ->
            fn(Position.create(x, y))
        }
    }
}