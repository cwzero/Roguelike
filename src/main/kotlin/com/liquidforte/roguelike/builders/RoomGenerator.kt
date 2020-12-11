package com.liquidforte.roguelike.builders

import com.liquidforte.roguelike.blocks.GameBlocks
import com.liquidforte.roguelike.config.GameConfig
import com.liquidforte.roguelike.extensions.orthogonalNeighbors
import com.liquidforte.roguelike.math.*
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size3D

class RoomGenerator(worldSize: Size3D = GameConfig.WORLD_SIZE) : WorldGenerator(worldSize) {
    private val corridors: MutableList<Pair<Position2D, Position2D>> = mutableListOf()

    private fun generateCorridors(root: BinaryRoom, level: Level) {
        root.traverseUp {
            if (split) {
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

                var lPos = lWall.map { it + lAxis }.let {
                    it.firstOrNull { it.toZircon() in level.blocks && level[it].isRoughFloor } ?: it.shuffled().first()
                }

                var rPos = rWall.map { it + rAxis }.let {
                    it.firstOrNull { it.toZircon() in level.blocks && level[it].isRoughFloor } ?: it.shuffled().first()
                }

                lPos.let {
                    level[it] = GameBlocks.roughFloor()
                    level[it + lAxis] = GameBlocks.roughFloor()
                }

                rPos.let {
                    level[it] = GameBlocks.roughFloor()
                    level[it + rAxis] = GameBlocks.roughFloor()
                }

                lPos += lAxis
                rPos += rAxis
            }
        }
    }

    override fun generate(): WorldGenerator = apply {
        val root = BinaryRoom(0, width, 0, height)
        root.split()
        levels[0]?.let(root::draw)
        generateCorridors(root, levels[0]!!)

        levels[0]?.let(root::drawWalls)

        // TODO: Connect, draw walls

        levels[0]?.run {
            eachPosition { pos ->
                if (pos !in blocks)
                    this[pos] = GameBlocks.roughWall()
                else {
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
    }
}