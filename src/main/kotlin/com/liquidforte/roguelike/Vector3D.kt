package com.liquidforte.roguelike

import org.hexworks.zircon.api.data.Position3D

data class Vector3D(val x: Int, val y: Int, val z: Int) {
    fun plus(pos: Position3D) : Position3D {
        return pos.plus(Position3D.create(x, y, z))
    }
}