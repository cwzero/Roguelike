package com.liquidforte.roguelike.builders

import com.liquidforte.roguelike.blocks.GameBlocks
import com.liquidforte.roguelike.extensions.neighbors
import com.liquidforte.roguelike.extensions.orthogonalNeighbors
import org.hexworks.zircon.api.data.Position
import java.lang.Math.sqrt
import java.util.*

class Path(origin: Position, private val destination: Position) : Stack<Position>() {
    init {
        push(origin)
    }

    var valid = true

    fun validate(level: Level) : Boolean {
        return valid
    }

    fun place(level: Level) : Boolean {
        forEach {
            level[it] = GameBlocks.roughFloor()
        }
        println("connected $destination in $size moves")
        return true
    }

    val current: Position
        get() = peek()

    val isComplete: Boolean
        get() = current.distanceTo(destination) <= 1

    fun Position.distanceTo(pos: Position): Double {
        val deltaX = x - pos.x
        val deltaY = y - pos.y
        val d2 = (deltaX * deltaX) + (deltaY * deltaY)
        return sqrt(d2.toDouble())
    }
}