package com.liquidforte.roguelike.extensions

import com.liquidforte.roguelike.Bounds3D
import com.liquidforte.roguelike.Vector3D
import org.hexworks.zircon.api.behavior.Scrollable3D
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Position3D

fun Scrollable3D.center() {
    val offset: Position = actualSize.run {
        Position.create(xLength / 2, yLength / 2)
    }.minus(visibleOffset.to2DPosition())

    scrollRightBy(offset.x)
    scrollForwardBy(offset.y)
}

val Scrollable3D.centerPoint: Position3D
    get() {
        return Position3D.create(actualSize.xLength / 2, actualSize.yLength / 2, actualSize.zLength / 2)
    }

val Scrollable3D.bounds: Bounds3D
    get() {
        return Bounds3D(actualSize, centerPoint)
    }

fun Scrollable3D.forAllPositions(fn: (Position3D) -> Unit) = actualSize.fetchPositions().forEach(fn)

fun Scrollable3D.forAllVectors(fn: (Vector3D) -> Unit) = bounds.forAllVectors(fn)