package com.liquidforte.roguelike.builders

import com.liquidforte.roguelike.extensions.neighbors
import com.liquidforte.roguelike.extensions.orthogonalNeighbors
import kotlinx.collections.immutable.toImmutableMap
import org.hexworks.zircon.api.data.Position
import java.lang.StringBuilder

class DijkstraMapForDoors(private val level: Level, private var map: MutableMap<Position, Int> = mutableMapOf()) {
    operator fun get(pos: Position): Int =
        map[pos]!!

    operator fun set(pos: Position, value: Int) {
        map[pos] = value
    }

    val data: Map<Position, Int>
        get() {
            if (map.isEmpty()) {
                initMap()
            }
            return map.toImmutableMap()
        }

    var dirty: Boolean = true

    operator fun contains(pos: Position): Boolean =
        pos in map

    private fun initMap(): DijkstraMapForDoors = apply {
        level.eachPosition { pos ->
            if (pos in level) {
                val block = level[pos]

                if (block.isRoughFloor) {
                    map[pos] = 0
                } else if (block.isRoughWall) {
                    if (pos.neighbors.none { it !in level }) {
                        val neighbors = pos.neighbors.map { level[it] }
                        val orthogonalNeighbors = pos.orthogonalNeighbors.map { level[it] }
                        val hasDoor = orthogonalNeighbors.any { it.isDoor }
                        val noWalls = neighbors.none { it.isSmoothWall }

                        if (noWalls || hasDoor)
                            map[pos] = Int.MAX_VALUE
                    }
                }
            }
        }
    }

    fun build(): DijkstraMapForDoors = apply {
        initMap()

        while (dirty) {
            dirty = false
            generateMap()
        }
    }

    private fun generateMap(): DijkstraMapForDoors = apply {
        val d = data

        val toSet =
            d.filter { (key, value) -> value > 0 }
                .filter { (key, _) ->
                    val neighbors =  key.orthogonalNeighbors
                        .filter { it in d }
                        .map { d[it]!! }
                    return@filter neighbors.any { it >= 0 && it < Int.MAX_VALUE }
                }


        toSet.forEach { (key, value) ->
            val lowPos: Position? = key.orthogonalNeighbors.filter { it in d }.minByOrNull { d[it]!! }

            if (lowPos != null) {
                val new = if (level[key].isRoughFloor || level[key].isDoor || level[key].isRoughFloor) {
                    d[lowPos]!!
                } else {
                    d[lowPos]!! + 1
                }

                if (new >= 0 && new < Int.MAX_VALUE && new < d[key]!!) {
                    map[key] = new
                    dirty = true
                }
            }
        }
    }

    override fun toString(): String = StringBuilder().apply {
        val d = data
        (0..level.size.height).forEach { y ->
            (0..level.size.width).forEach { x ->
                val pos = Position.create(x, y)
                append("${d[pos]},")
            }
            appendLine()
        }
    }.toString()
}