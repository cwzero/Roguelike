package com.liquidforte.roguelike.builders

import com.liquidforte.roguelike.blocks.GameBlocks
import com.liquidforte.roguelike.config.GameTiles
import com.liquidforte.roguelike.math.Position2D
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.immutableListOf
import kotlinx.collections.immutable.persistentListOf
import org.hexworks.zircon.api.data.Position
import java.lang.Math.sqrt
import kotlin.random.Random

open class Room(
    val left: Int,
    val right: Int,
    val top: Int,
    val bottom: Int
) {
    var connected = false

    open val isConnected
        get() = connected

    open fun connect() {
        connected = true
    }

    val width: Int
        get() = right - left + 1
    val height: Int
        get() = bottom - top + 1

    val sides = sequence {
        val horizontal = left..right
        val vertical = top..bottom

        // Left
        yield(vertical.map { Position.create(left, it) })

        // Top
        yield(horizontal.map { Position.create(it, top) })

        // Right
        yield(vertical.map { Position.create(right, it) })

        // Bottom
        yield(horizontal.map { Position.create(it, bottom) })
    }

    fun getClosestSideTo(other: Room) : List<Position> {
        return sides.minByOrNull { side ->
            side.map { pos ->
                other.distanceTo(pos)
            }.minOrNull() ?: Double.MAX_VALUE
        }!!
    }

    operator fun contains(pos: Position): Boolean = (pos.x in left..right) && (pos.y in top..bottom)

    operator fun contains(pos: Position2D): Boolean = (pos.x in left..right) && (pos.y in top..bottom)

    fun scrambleAxis(pos: Position): Position {
        var moveX = false
        var moveY = false

        if (pos.x == left) {
            moveY = true
        } else if (pos.x == right) {
            moveY = true
        }

        if (pos.y == top) {
            moveX = true
        } else if (pos.y == bottom) {
            moveX = true
        }

        if (moveX) {
            pos.withX(left + Random.nextInt(1, width - 2))
        }

        if (moveY) {
            pos.withY(top + Random.nextInt(1, height - 2))
        }

        return pos
    }

    fun borderPointBy(fn: (Position) -> Double): Position {
        var min: Double = Double.NaN
        var result: Position = Position.unknown()

        eachBorderPoint {
            val r = fn(it)
            if ((min.isNaN() || r < min) && r != 0.0) {
                min = r
                result = it
            }
        }

        return result
    }

    fun eachBorderPoint(fn: (Position) -> Unit) {
        listOf(bottom, top).forEach { y ->
            (left + 1 until right).forEach { x ->
                fn(Position.create(x, y))
            }
        }

        listOf(left, right).forEach { x ->
            (top + 1 until bottom).forEach { y ->
                fn(Position.create(x, y))
            }
        }
    }

    fun cornerBy(fn: (Position) -> Double): Position {
        var min: Double = Double.NaN
        var result: Position = Position.unknown()

        eachCorner {
            val r = fn(it)

            if ((min.isNaN() || r < min) && r != 0.0) {
                min = r
                result = it
            }
        }

        return result
    }

    val corners: List<Position>
        get() = listOf(
            left to top,
            right to top,
            left to bottom,
            right to bottom
        ).map { (x, y) ->
            Position.create(x, y)
        }

    fun isCorner(pos: Position) =
        pos in corners

    fun eachCorner(fn: (Position) -> Unit) =
        corners.forEach(fn)

    fun closestCorner(room: Room): Position = cornerBy(room::distanceTo)

    fun closestPointTo(room: Room): Position = borderPointBy(room::distanceTo)

    fun closestPointTo(pos: Position): Position = borderPointBy { distance(pos, it) }

    fun distanceTo(pos: Position): Double = distance(pos, closestPointTo(pos))

    private fun d2(pos1: Position, pos2: Position): Int {
        val deltaX = pos1.x - pos2.x
        val deltaY = pos1.y - pos2.y
        return (deltaX * deltaX) + (deltaY * deltaY)
    }

    private fun distance(pos1: Position, pos2: Position): Double =
        sqrt(d2(pos1, pos2).toDouble())

    fun randomBorderPosition(): Position {
        var x = left + 1
        var y = top + 1
        if (Random.nextFloat() > 0.5f) {
            y += Random.nextInt(0, height - 2)
            if (Random.nextFloat() > 0.5f) {
                x = right - 1
            }
        } else {
            x += Random.nextInt(0, width - 2)
            if (Random.nextFloat() > 0.5f) {
                y = bottom - 1
            }
        }
        return Position.create(x, y)
    }

    open fun drawWalls(level: Level) {

        (left + 1 until right).forEach { x ->
            val top = Position.create(x, top)
            val bottom = Position.create(x, bottom)
            level.blocks[top] = GameBlocks.smoothWall(GameTiles.WALL_HORIZONTAL)
            level.blocks[bottom] = GameBlocks.smoothWall(GameTiles.WALL_HORIZONTAL)
        }

        (top + 1 until bottom).forEach { y ->
            val left = Position.create(left, y)
            val right = Position.create(right, y)
            level.blocks[left] = GameBlocks.smoothWall(GameTiles.WALL_VERTICAL)
            level.blocks[right] = GameBlocks.smoothWall(GameTiles.WALL_VERTICAL)
        }

        (left + 1 until right).forEach { x ->
            (top + 1 until bottom).forEach { y ->
                level.blocks[Position.create(x, y)] = GameBlocks.floor()
            }
        }

        mapOf(
            (left to top) to GameTiles.WALL_UPPER_LEFT_CORNER,
            (left to bottom) to GameTiles.WALL_LOWER_LEFT_CORNER,
            (right to top) to GameTiles.WALL_UPPER_RIGHT_CORNER,
            (right to bottom) to GameTiles.WALL_LOWER_RIGHT_CORNER
        ).map { (co, t) -> Position.create(co.first, co.second) to t }.forEach { (pos, tile) ->
            level.blocks[pos] = GameBlocks.smoothWall(tile)
        }
    }

    open fun draw(level: Level) {
        (left..right).forEach { x ->
            (top..bottom).forEach { y ->
                level.blocks[Position.create(x, y)] = GameBlocks.roughFloor()
            }
        }
    }

    override fun toString(): String {
        return "${javaClass.name} [left: $left, top: $top, right: $right, bottom: $bottom]"
    }
}