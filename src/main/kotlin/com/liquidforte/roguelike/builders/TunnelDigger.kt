package com.liquidforte.roguelike.builders

import com.liquidforte.roguelike.blocks.GameBlock
import com.liquidforte.roguelike.blocks.GameBlocks
import com.liquidforte.roguelike.math.Position2D
import com.liquidforte.roguelike.math.toZircon
import com.liquidforte.roguelike.math.withRelativeX
import com.liquidforte.roguelike.math.withRelativeY
import kotlin.math.abs
import kotlin.math.sign

class TunnelDigger(
    private val level: Level,
    corridor: Corridor
) {
    private val origin = corridor.origin
    private val originAxis = corridor.originAxis
    private val destination = corridor.destination
    private val destinationAxis = corridor.destinationAxis

    private var current = origin
    private var currentAxis = originAxis

    private var target = destination
    private var targetAxis = destinationAxis

    private val deltaX
        get() = current.x - target.x
    private val deltaY
        get() = current.y - target.y

    private val path = mutableListOf(origin)
    private val targetPath = mutableListOf(destination)

    fun dig(): Boolean {
        var result = false
        val path = AStarForDiggers(
            level,
            origin.toZircon(),
            originAxis.toZircon(),
            (destination + destinationAxis).toZircon()
        ).path

        if (path.isNotEmpty()) {
            path.toMutableList()
                .apply {
                    remove(first())
                }
                .forEach {
                    level[it] = GameBlocks.roughFloor()
                    result = true
                }
        }
        return result
    }
}