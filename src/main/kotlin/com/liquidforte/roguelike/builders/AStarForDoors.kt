package com.liquidforte.roguelike.builders

import com.liquidforte.roguelike.extensions.distanceTo
import com.liquidforte.roguelike.extensions.eachPosition
import com.liquidforte.roguelike.extensions.neighbors
import com.liquidforte.roguelike.extensions.orthogonalNeighbors
import org.hexworks.zircon.api.data.Position

data class Cell(
    var parent: Position? = null,
    var f: Double = Double.MAX_VALUE,
    var g: Double = Double.MAX_VALUE,
    var h: Double = Double.MAX_VALUE
)

data class SimpleCell(val pos: Position, var f: Double)

class AStarForDoors(
    private val root: BinaryRoom,
    private val level: Level,
    private val origin: Position,
    private val destinations: Iterable<Position>,
    private val digging: Boolean,
    private val canMove: (Position) -> Boolean = canMoveFunction(level, digging)
) {
    var destination: Position? = null
    val cells: MutableMap<Position, Cell> = mutableMapOf()
    var run = false

    val isValid: Boolean by lazy {
        run().destination != null
    }

    val path: List<Position> by lazy {
        run()
        if (destination == null) {
            error("!")
        } else {
            tracePath(destination!!)
        }
    }

    val turns: Int by lazy {
        run()
        if (destination == null) {
            error("!")
        } else {
            turns(destination!!)
        }
    }

    val rooms: Int by lazy {
        run()
        if (destination == null) {
            -1
        } else {
            val path = path
            val rooms = mutableListOf<Room>()

            path.forEach {
                val room = root[it]

                if (room !is BinaryRoom && room !in rooms) {
                    rooms.add(room)
                }
            }

            rooms.size
        }
    }

    private fun canDig(pos: Position): Boolean =
        digging && level[pos].isRoughWall

    private fun shouldDig(from: Position, pos: Position): Boolean {
        if (canDig(pos)) {
            // Does it get us closer to destination?
            val newAStar = AStarForDoors(
                root,
                level,
                pos,
                destinations,
                false
            )

            if (newAStar.isValid) {
                // Does it make a loop?
                val loopAStar = AStarForDoors(
                    root,
                    level,
                    pos,
                    listOf(from),
                    false
                )

                if (!loopAStar.isValid) {
                    return true
                } else {
                    return loopAStar.rooms > 1
                }
            }
        }
        return false
    }

    private fun isDestination(pos: Position): Boolean {
        return pos in destinations
    }

    private fun run(): AStarForDoors = apply {
        if (!run) {
            path()
            run = true
        }
    }

    private fun countTurns(path: List<Position>): Int {
        var turns = 0

        (1..path.size - 2).forEach {
            val a = path[it - 1]
            val b = path[it]
            val c = path[it + 1]

            if (b - a != c - b) {
                turns++
            }
        }

        return turns
    }

    private fun turns(destination: Position): Int =
        countTurns(tracePath(destination))

    private fun tracePath(destination: Position): List<Position> {
        val result = mutableListOf<Position>()
        // If you get a null pointer here, the pathing failed!
        var pos = destination
        var current = cells[pos]!!

        result.add(pos)

        do {
            pos = current.parent!!
            current = cells[pos]!!

            result.add(pos)
        } while (current.parent != pos)

        return result.reversed()
    }

    private fun path(): AStarForDoors = apply {
        level.size.eachPosition {
            cells[it] = Cell()
        }

        cells[origin]?.parent = origin
        cells[origin]?.f = 0.0
        cells[origin]?.g = 0.0
        cells[origin]?.h = 0.0

        val open = mutableListOf(SimpleCell(origin, 0.0))
        val closed = mutableListOf<Position>()

        var stop = false

        while (!stop && open.isNotEmpty()) {
            val q = open.minByOrNull { it.f }

            if (q != null) {
                open.remove(q)
                closed.add(q.pos)

                val cameFrom = cells[q.pos]?.parent!!

                val neighbors = q.pos.orthogonalNeighbors.filter { it in level }

                val cell = Cell(q.pos, 0.0, 0.0, 0.0)
                neighbors.forEach { successor ->
                    if (isDestination(successor) || shouldDig(q.pos, successor)) {
                        cells[successor] = cell
                        destination = successor
                        return@apply
                    } else if (successor !in closed && canMove(successor)) {
                        cell.g = cells[q.pos]?.g!! + 1.0
                        // TODO: space turns out, distribute evenly!
                        var h = destinations.map { it.distanceTo(successor) + turns(cameFrom) }.minOrNull()

                        if (h != null) {
                            cell.h = h
                            cell.f = cell.g + cell.h

                            if (open.none { it.pos == successor && it.f < cell.f }) {
                                val current = cells[successor]!!
                                if (current.f == Double.MAX_VALUE || current.f > cell.f) {
                                    open.add(SimpleCell(successor, cell.f))
                                    cells[successor] = cell
                                }
                            }
                        } else {
                            println("Error!")
                        }
                    }
                }
            }
        }
    }

    companion object {
        fun canMoveFunction(level: Level, digging: Boolean): (Position) -> Boolean = { pos ->
            val block = level[pos]
            var result = false

            val isSmoothWall = block.isSmoothWall

            if (!isSmoothWall) {
                if (block.isRoughFloor) {
                    result = true
                } else {
                    if (digging) {
                        if (block.isRoughWall) {
                            val hasSmoothWall = pos.neighbors.filter { it in level }.any { level[it].isSmoothWall }
                            val hasDoor = pos.orthogonalNeighbors.filter { it in level }.any { level[it].isDoor }

                            result = !hasSmoothWall || hasDoor
                        }
                    } else {
                        result = block.isSmoothFloor || block.isDoor
                    }
                }
            }


            result
        }
    }
}