package com.liquidforte.roguelike.builders

import com.liquidforte.roguelike.blocks.GameBlocks
import com.liquidforte.roguelike.config.GameConfig
import com.liquidforte.roguelike.extensions.orthogonalNeighbors
import com.liquidforte.roguelike.math.*
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size3D

class RoomGenerator(worldSize: Size3D = GameConfig.WORLD_SIZE) : WorldGenerator(worldSize) {
    private val rooms = mutableMapOf<Int, BinaryRoom>()
    private val corridors = mutableMapOf<Int, MutableList<Corridor>>()
    private val connectedCooridors = mutableMapOf<Int, MutableList<Corridor>>()

    private fun unconnectedCorridors(level: Int): ImmutableList<Corridor> {
        return ((corridors[level] ?: listOf()) - (connectedCooridors[level] ?: listOf())).toImmutableList()
    }

    private fun generateCorridors(root: BinaryRoom, level: Level, index: Int) {
        if (index !in corridors) {
            corridors[index] = mutableListOf()
        }

        root.traverseUp {
            val centerPoint = if (verticalSplit) {
                Position.create(rightRoom!!.left, height / 2)
            } else {
                Position.create(width / 2, rightRoom!!.top)
            }

            val lRoom: Room = leftRoom!!.getClosestRoomTo(centerPoint)
            val rRoom: Room = rightRoom!!.getClosestRoomTo(centerPoint)

            if (lRoom is BinaryRoom || rRoom is BinaryRoom) {
                error("!!")
            }

            val lWall = lRoom.getClosestSideTo(rRoom).filter { it !in lRoom.corners }.map { it.toPosition2D() }
            val rWall = rRoom.getClosestSideTo(lRoom).filter { it !in rRoom.corners }.map { it.toPosition2D() }

            // Get the axis perpendicular to the wall
            var lAxis = (lWall[1] - lWall[0]).abs().let { Position2D.create(x = it.y, y = it.x) }
            var rAxis = (rWall[1] - rWall[0]).abs().let { Position2D.create(x = it.y, y = it.x) }

            val lTest = lWall[0] + lAxis
            val rTest = rWall[0] + rAxis

            if (lTest.toZircon() !in level.blocks || level[lTest].isRoughFloor) {
                lAxis = lAxis.scale(-1.0)
            }

            if (rTest.toZircon() !in level.blocks || level[rTest].isRoughFloor) {
                rAxis = rAxis.scale(-1.0)
            }

            var lPos = lWall.map { it + lAxis }.let { list ->
                list.firstOrNull { it.toZircon() in level.blocks && level[it].isRoughFloor } ?: list.shuffled().first()
            }

            var rPos = rWall.map { it + rAxis }.let { list ->
                list.firstOrNull { it.toZircon() in level.blocks && level[it].isRoughFloor } ?: list.shuffled().first()
            }

            listOf(lPos, rPos).forEach { pos ->
                level[pos] = GameBlocks.roughFloor()
            }

            corridors[index]!!.add(Corridor(lPos, lAxis, rPos, rAxis))
        }
    }

    private fun generateLevel(index: Int) {
        if (index !in corridors) {
            corridors[index] = mutableListOf()
        }

        if (index !in connectedCooridors) {
            connectedCooridors[index] = mutableListOf()
        }

        levels[index]!!.run {
            eachPosition { pos ->
                if (pos !in blocks)
                    this[pos] = GameBlocks.roughWall()
            }
        }

        val root = BinaryRoom(0, width, 0, height)
        root.split()
        rooms[index] = root

        levels[index]!!.let(root::draw)
        generateCorridors(root, levels[index]!!, index)

        levels[index]!!.let(root::drawWalls)

        levels[index]!!.run {
            eachPosition { pos ->
                if (pos in blocks) {
                    if (this[pos].isRoughFloor) {
                        pos.orthogonalNeighbors.filter { it in blocks }
                            .filter { this[it].isSmoothWall && it !in root.getRoom(it).corners }
                            .forEach { potentialDoor ->
                                val room = root[potentialDoor]
                                val sides = room.sides.filter { potentialDoor in it }.toList()
                                if (!sides.all { it.any { pos -> pos in this@run.blocks && this@run[pos].isDoor } }) {
                                    this[potentialDoor] = GameBlocks.door()
                                }
                            }
                    }
                }
            }
        }

        println("Connecting corridors...")

        corridors[index]!!.forEach { corridor ->
            if (TunnelDigger(levels[index]!!, corridor).dig()) {
                connectedCooridors[index]!!.add(corridor)
            }
        }

        unconnectedCorridors(index)!!.forEach { corridor ->
            if (TunnelDigger(levels[index]!!, corridor).dig()) {
                connectedCooridors[index]!!.add(corridor)
            }
        }

        println("Connected: ${connectedCooridors.size}, Unconnected: ${unconnectedCorridors(index).size}, Splits: ${root.countSplit()}")
    }

    override fun generate(): WorldGenerator = apply {
        levels.keys.forEach { z ->
            generateLevel(z)
        }

        StairDigger(levels).digStairs()

        levels.keys.forEach { z ->
            RoomPopulator(rooms[z]!!, levels[z]!!).populateRooms()
        }
    }
}