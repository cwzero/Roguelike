package com.liquidforte.roguelike

import org.hexworks.zircon.api.data.Position3D
import org.hexworks.zircon.api.data.Size3D

data class Bounds3D(val size: Size3D, val centerPoint: Position3D = Position3D.create(size.xLength / 2, size.yLength / 2, size.zLength / 2)) {
    fun getPosition(vec: Vector3D) : Position3D = vec.plus(centerPoint)

    fun contains(vec: Vector3D) : Boolean = size.containsPosition(getPosition(vec))

    fun fetchVectors() : Sequence<Vector3D> = sequence {
        (0 .. size.xLength).forEach { x ->
            (0 .. size.yLength).forEach { y ->
                (0 .. size.zLength).forEach { z ->
                    yield(Vector3D(x, y, z))
                }
            }
        }
    }

    fun forAllVectors(fn: (Vector3D) -> Unit) = fetchVectors().forEach(fn)
}