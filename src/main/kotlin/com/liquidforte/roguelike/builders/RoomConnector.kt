package com.liquidforte.roguelike.builders

import com.liquidforte.roguelike.blocks.GameBlocks
import com.liquidforte.roguelike.extensions.distanceTo
import com.liquidforte.roguelike.math.Position2D
import com.liquidforte.roguelike.math.toZircon
import kotlinx.collections.immutable.toImmutableList
import org.hexworks.zircon.api.data.Position
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min
import kotlin.random.Random

class RoomConnector(private val root: BinaryRoom, private val level: Level) {
    private val doors: MutableList<Position> = mutableListOf()
    private val connectedDoors: MutableList<Position> = mutableListOf()
    private val failedDoors: MutableList<Position> = mutableListOf()
    private val remainingDoors: MutableList<Position> = mutableListOf()

    private val unconnectedDoors
        get() = doors.filter { it !in connectedDoors && it !in failedDoors && it !in remainingDoors }

    private fun check(doors: Int): Boolean {
        if (doors <= 0)
            return false
        if (doors >= 3)
            return true

        return when (doors) {
            1 -> Random.nextFloat() < 0.05f
            2 -> Random.nextFloat() < 0.8f
            else -> false
        }
    }

    private fun addDoor(x: Int, y: Int): Boolean {
        val pos = Position.create(x, y)

        (x - doorRadius until x + doorRadius).forEach { checkX ->
            (y - doorRadius until y + doorRadius).forEach { checkY ->
                val pos = Position.create(checkX, checkY)
                if (pos !in level || level[pos].isDoor) {
                    return false
                }
            }
        }

        level.blocks[pos] = GameBlocks.door()
        doors.add(pos)
        return true
    }

    private fun addDoors() {
        root.traverseSubrooms { room ->
            var doors: Int = 0

            // For each wall, have a 25% chance of adding a door to a random position
            // (that's not too close to other doors)

            // Limit how close the doors can be to root

            val topLeft = Position2D.create(room.left, room.top)
            val bottomLeft = Position2D.create(room.left, room.bottom)
            val topRight = Position2D.create(room.right, room.top)
            val bottomRight = Position2D.create(room.right, room.bottom)

            val toSide: (Sequence<Position2D>) -> Iterable<Position2D> = {
                it.toMutableList().apply {
                    remove(first())
                    remove(last())
                }
            }

            val topSide = toSide(topLeft..topRight)
            val bottomSide = toSide(bottomLeft..bottomRight)
            val leftSide = toSide(topLeft..bottomLeft)
            val rightSide = toSide(topRight..bottomRight)

            val sides = listOf(topSide, bottomSide, leftSide, rightSide)

            while (!check(doors)) {
                val side = sides.filter { side -> side.none { pos -> level[pos.toZircon()].isDoor } }.shuffled().first()
                val pos = side.shuffled().first()

                if (addDoor(pos.x, pos.y)) {
                    doors++
                }
            }
        }
    }

    private fun connect(aStar: AStarForDoors) {
        val path = aStar.path

        path.forEach { pos ->
            if (level[pos].isRoughWall) {
                level[pos] = GameBlocks.roughFloor()
            } else if (level[pos].isDoor) {
                connectedDoors.add(pos)
                if (pos in remainingDoors) {
                    remainingDoors.remove(pos)
                } else {
                    if (pos in failedDoors) {
                        failedDoors.remove(pos)
                    }

                    val room = root[pos]
                    unconnectedDoors.filter { it in room }.forEach {
                        connectedDoors.add(it)
                        remainingDoors.add(it)
                    }
                }
            }
        }

        println("connected ${path.first()} to ${path.last()} in ${path.size} moves with ${aStar.turns} turns.  ${unconnectedDoors.size} left.")
    }

    private fun connectInitial() {
        while (connectedDoors.isEmpty()) {
            val door = doors.shuffled().first()
            val path = AStarForDoors(level, door, doors.filter { it != door && it !in root[door] }, true)
            if (path.isValid) {
                connect(path)
            }
        }
    }

    private fun connectRemaining() {
        // TODO: Fix
        val rDoors = remainingDoors.toImmutableList()
        while (remainingDoors.isNotEmpty()) {
            val door = remainingDoors.shuffled().first()
            val room = root[door]

            val path = AStarForDoors(level, door, doors.filter { it != door && it !in root[door] }, true)
            if (path.isValid) {
                connect(path)
            } else {
                failedDoors.add(door)
            }
            remainingDoors.remove(door)
        }

        println("Connected ${connectedDoors.size} doors.  ${failedDoors.size} failed.")
    }

    private fun connectFailed() {
        while (failedDoors.isNotEmpty()) {
            val door = failedDoors.shuffled().first()
            val path = AStarForDoors(level, door, connectedDoors.filter { it != door && it !in root[door] }, true)
            if (path.isValid) {
                connect(path)
                failedDoors.remove(door)
            } else {
                println("Couldn't connect $door.")
                failedDoors.remove(door)
            }
        }

        println("Connected ${connectedDoors.size} doors.  ${failedDoors.size} failed.")
    }

    private fun connectDoor() {
        val potentialDoors = connectedDoors.flatMap { pos1 ->
            unconnectedDoors.sortedBy { it.distanceTo(pos1) }.take(10)
        }.distinct()
        val door = potentialDoors.shuffled().first()
        val aStar = AStarForDoors(
            level,
            door,
            connectedDoors.filter { it != door && it !in root[door] },
            true
        )

        if (aStar.isValid) {
            connect(aStar)
        } else {
            failedDoors.add(door)
        }
    }

    private fun connectDoors() {

    }

    fun connect() {
        root.connect(level) {
            var centerPoint = if (verticalSplit) {
                Position.create(rightRoom!!.left, height / 2)
            } else {
                Position.create(width / 2, rightRoom!!.top)
            }

            var lRoom: Room = leftRoom!!.getClosestRoomTo(centerPoint)
            var rRoom: Room = rightRoom!!.getClosestRoomTo(centerPoint)

            val lSide = lRoom.sides.minByOrNull { side ->
                side.map { it.distanceTo(centerPoint) }.distinct().minOrNull() ?: Double.MAX_VALUE
            }!!.toMutableList()
                .apply {
                    remove(first())
                    remove(last())
                }

            val rSide = rRoom.sides.minByOrNull { side ->
                side.map { it.distanceTo(centerPoint) }.distinct().minOrNull() ?: Double.MAX_VALUE
            }!!.toMutableList()
                .apply {
                    remove(first())
                    remove(last())
                }

            var lPoint = lSide.firstOrNull { level[it].isDoor } ?: lSide.shuffled().first()
            var rPoint = rSide.firstOrNull { level[it].isDoor } ?: rSide.shuffled().first()

            level[lPoint] = GameBlocks.door()
            level[rPoint] = GameBlocks.door()

            var lAxis = (lSide.get(1) - lSide.get(0)).let { Position.create(it.y, it.x) }
            var rAxis = (rSide.get(1) - rSide.get(0)).let { Position.create(it.y, it.x) }

            if (level[lPoint + lAxis].isSmoothFloor) {
                lAxis = lAxis.let { Position.create(it.x * -1, it.y * -1) }
            }

            if (level[rPoint + rAxis].isSmoothFloor) {
                rAxis = rAxis.let { Position.create(it.x * -1, it.y * -1) }
            }

            lPoint += lAxis
            rPoint += rAxis
            level[lPoint] = GameBlocks.roughFloor()
            level[rPoint] = GameBlocks.roughFloor()

            lPoint += lAxis
            rPoint += rAxis
            level[lPoint] = GameBlocks.roughFloor()
            level[rPoint] = GameBlocks.roughFloor()

            val deltaX = abs((lPoint.x - rPoint.x).toDouble() / 2.0)
            val deltaY = abs((lPoint.y - rPoint.y).toDouble() / 2.0)

            val minX = min(lPoint.x, rPoint.x)
            val minY = min(lPoint.y, rPoint.y)

            val cp = listOf(
                Position.create(floor(minX + deltaX).toInt(), floor(minY + deltaY).toInt()),
                Position.create(floor(minX + deltaX).toInt(), ceil(minY + deltaY).toInt()),
                Position.create(ceil(minX + deltaX).toInt(),  floor(minY + deltaY).toInt()),
                Position.create(ceil(minX + deltaX).toInt(),  ceil(minY + deltaY).toInt())
            )

            val lcp = cp.minByOrNull { lPoint.distanceTo(it) } ?: cp.shuffled().first()
            val rcp = cp.minByOrNull { rPoint.distanceTo(it) } ?: cp.shuffled().first()

            centerPoint = cp.minByOrNull { lPoint.distanceTo(it) + rPoint.distanceTo(it) } ?: cp.shuffled().first()

            level[centerPoint] = GameBlocks.roughFloor()

            val lPath = AStarForDoors(level, lcp, listOf(lPoint), true).path
            val rPath = AStarForDoors(level, rcp, listOf(rPoint), true).path

            lPath.forEach {
                level[it] = GameBlocks.roughFloor()
            }

            rPath.forEach {
                level[it] = GameBlocks.roughFloor()
            }
        }
    }

    companion object {
        val doorRadius = 5
    }
}