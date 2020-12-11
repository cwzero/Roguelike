package com.liquidforte.roguelike.math

data class Offset2D private constructor(val deltaX: Int, val deltaY: Int) {
    operator fun invoke(pos: Position2D): Position2D = pos.withRelative(deltaX, deltaY)

    fun toPosition(): Position2D = this(Position2D.origin())

    companion object {
        fun create(deltaX: Int = 0, deltaY: Int = 0) = Offset2D(deltaX, deltaY)
    }
}

fun Position2D.toOffset(): Offset2D = Offset2D.create(x, y)