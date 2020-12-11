package com.liquidforte.roguelike.math

import kotlinx.collections.immutable.toImmutableSet
import kotlin.math.abs
import kotlin.math.sqrt

typealias Line2D = (Int) -> Int

typealias PositionFilter = (Position2D) -> Boolean

data class Position2D private constructor(val x: Int, val y: Int) {
    val orthogonalNeighbors
        get() = mutableSetOf<Position2D>().apply {
            listOf(-1, 1).forEach { delta ->
                add(withRelativeX(delta))
                add(withRelativeY(delta))
            }
        }.toImmutableSet()

    val neighbors
        get() = mutableSetOf<Position2D>().apply {
            addAll(orthogonalNeighbors)

            addAll(
                this@Position2D.let { pos ->
                    listOf(-1, 1).let { list ->
                        list.flatMap { x ->
                            list.map { y -> pos.withRelative(x, y) }
                        }
                    }
                })
        }.toImmutableSet()

    fun withRelative(pos: Position2D) =
        withRelative(pos.x, pos.y)

    fun with(pos: Position2D) =
        with(pos.x, pos.y)

    fun withRelativeX(pos: Position2D) =
        withRelativeX(pos.x)

    fun withX(pos: Position2D) =
        withX(pos.x)

    fun withRelativeY(pos: Position2D) =
        withRelativeY(pos.y)

    fun abs() =
        create(abs(x), abs(y))

    fun scale(scalar: Double) =
        create((x * scalar).toInt(), (y * scalar).toInt())

    operator fun plus(pos: Position2D): Position2D {
        return Position2D(pos.x + this.x, pos.y + this.y)
    }

    operator fun minus(pos: Position2D): Position2D {
        return Position2D(pos.x - this.x, pos.y - this.y)
    }

    operator fun rangeTo(pos: Position2D) = range(this, pos)

    override fun toString(): String {
        return "(${x},${y})"
    }

    companion object {
        fun origin() = create()

        fun create(x: Int = 0, y: Int = 0) = Position2D(x, y)

        private fun d2(from: Position2D, to: Position2D): Int {
            val deltaX: Int = to.x - from.x
            val deltaY: Int = to.y - from.y
            return (deltaX * deltaX) + (deltaY * deltaY)
        }

        fun distance(from: Position2D, to: Position2D): Double {
            return sqrt(d2(from, to).toDouble())
        }

        fun line(from: Position2D, to: Position2D): Line2D {
            val dX: Int = to.x - from.x
            val dY: Int = to.y - from.y
            val m = dY.toDouble() / dX.toDouble()

            val b = to.y - (dX * m)

            return { x -> ((m * x) + b).toInt() }
        }

        fun rangeX(from: Position2D, to: Int) = range(from, from.withRelativeX(to))

        fun rangeY(from: Position2D, to: Int) = range(from, from.withRelativeY(to))

        fun range(from: Position2D, to: Position2D) = range(from.x..to.x, from.y..to.y)
    }
}