package com.liquidforte.roguelike

import org.hexworks.zircon.api.data.Position3D
import org.hexworks.zircon.api.data.Size3D

data class Bounds3D(val size: Size3D, val centerPoint: Position3D = Position3D.create(size.xLength / 2, size.yLength / 2, size.zLength / 2)) {
    fun relative(pos: Position3D) : Position3D {
        return pos.copy().plus(centerPoint)
    }

    fun absolute(pos: Position3D) : Position3D {
        return pos.copy().minus(centerPoint)
    }

    fun contains(pos: Position3D) : Boolean {
        return size.containsPosition(relative(pos))
    }

    fun fetchPositions() : Sequence<Position3D> {
        return sequence {
            (0 until size.zLength).flatMap { z ->
                (0 until size.yLength).flatMap { y ->
                    (0 until size.xLength).map { x ->
                        yield(absolute(Position3D.create(x, y, z)))
                    }
                }
            }
        }
    }

    fun forAllPositions(fn: (Position3D) -> Unit) {
        fetchPositions().forEach(fn)
    }
}