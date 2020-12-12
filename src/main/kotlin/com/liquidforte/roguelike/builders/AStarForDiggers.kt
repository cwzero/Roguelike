package com.liquidforte.roguelike.builders

import com.liquidforte.roguelike.extensions.distanceTo
import com.liquidforte.roguelike.extensions.eachPosition
import com.liquidforte.roguelike.extensions.neighbors
import com.liquidforte.roguelike.extensions.orthogonalNeighbors
import org.hexworks.zircon.api.data.Position
import kotlin.math.abs

data class Cell(
    var parent: Position? = null,
    var f: Double = Double.MAX_VALUE,
    var g: Double = Double.MAX_VALUE,
    var h: Double = Double.MAX_VALUE
)

data class SimpleCell(val pos: Position, var f: Double)

class AStarForDiggers(
    private val level: Level,
    private val origin: Position,
    private val originAxis: Position,
    private val destination: Position
) {
    private val cells: MutableMap<Position, Cell> = mutableMapOf()

    val path: List<Position> by lazy {
        path()
    }

    private fun countTurns(path: List<Position>): Int {
        var turns = 0

        val range = (1..path.size - 2)
        val size = range.count()

        range.forEach {
            val a = path[it - 1]
            val b = path[it]
            val c = path[it + 1]

            val weight = abs(it.toDouble() - (size.toDouble() / 2.0))

            if (b.neighbors.any {
                    it in level.blocks && level[it].isSmoothWall
                } && b.orthogonalNeighbors.none {
                    it in level.blocks && level[it].isDoor
                }) {
                turns += weight.toInt()
            }

            if (b.neighbors.any { n -> n in level && level[n].isRoughFloor && n !in path } || b - a != c - b) {
                turns += weight.toInt()
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

        if (current.parent != null) {
            result.add(pos)

            do {
                pos = current.parent!!
                current = cells[pos]!!

                result.add(pos)
            } while (current.parent != pos)
        }

        return result.reversed()
    }

    private fun canMove(pos: Position): Boolean {
        return pos in level.blocks && (level[pos].isRoughWall || level[pos].isRoughFloor)
    }

    private fun path(): List<Position> {
        level.size.eachPosition {
            cells[it] = Cell()
        }

        val a = origin - originAxis

        cells[a]?.parent = a
        cells[a]?.f = 0.0
        cells[a]?.g = 0.0
        cells[a]?.h = 0.0

        cells[origin]?.parent = a
        cells[origin]?.f = 0.0
        cells[origin]?.g = 0.0
        cells[origin]?.h = 0.0

        val open = mutableListOf(SimpleCell(origin, 0.0))
        val closed = mutableListOf(origin - originAxis)

        var stop = false

        while (!stop && open.isNotEmpty()) {
            val q = open.minByOrNull { it.f }

            if (q != null) {
                open.remove(q)
                closed.add(q.pos)

                val cameFrom = cells[q.pos]?.parent!!

                val neighbors = q.pos.orthogonalNeighbors.filter { it !in closed && it in level && canMove(it) }

                val cell = Cell(q.pos, 0.0, 0.0, 0.0)
                neighbors.forEach { successor ->
                    if (successor == destination) {
                        cells[successor] = cell
                        stop = true
                        return tracePath(destination)
                    } else {
                        cell.g = cells[q.pos]?.g!! + 1.0
                        // TODO: space turns out, distribute evenly!
                        // Weight turns as 10 moves
                        var h = destination.distanceTo(successor) + (turns(cameFrom) * 20)

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
                        }
                    }
                }
            }
        }

        if (!stop && open.isNotEmpty()) {
            println("Pathing failed for corridor $origin to $destination")
        }

        return when {
            stop -> tracePath(destination)
            open.isNotEmpty() -> listOf()
            else -> listOf()
        }
    }
}