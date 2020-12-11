package com.liquidforte.roguelike.math

import org.hexworks.zircon.api.data.Position

fun interface Container2D {
    operator fun contains(pos: Position2D) : Boolean

    operator fun contains(positions: Iterable<Position2D>) : Boolean = positions.all { contains(it) }

    operator fun contains(pos: Position) : Boolean = contains(Position2D.fromZircon(pos))

    operator fun contains(pos: Pair<Int, Int>) : Boolean = contains(Position2D.fromPair(pos))

    fun contains(x: Int, y: Int) : Boolean = contains(Position2D.create(x, y))
}

fun Iterable<Position2D>.toContainer() : Container2D = Container2D { (pos) -> this.contains(pos) }