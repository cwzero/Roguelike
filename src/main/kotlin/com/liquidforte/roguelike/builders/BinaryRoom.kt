package com.liquidforte.roguelike.builders

import com.liquidforte.roguelike.extensions.distanceTo
import com.liquidforte.roguelike.functions.logGameEvent
import kotlinx.collections.immutable.toImmutableList
import org.hexworks.zircon.api.data.Position
import kotlin.random.Random

class BinaryRoom(left: Int, right: Int, top: Int, bottom: Int) : Room(left, right, top, bottom) {
    var horizontalSplit: Boolean = false
    var verticalSplit: Boolean = false

    val split: Boolean
        get() = horizontalSplit || verticalSplit

    var leftRoom: BinaryRoom? = null
    var rightRoom: BinaryRoom? = null

    override val isConnected: Boolean
        get() {
            if (isLeaf) {
                return connected
            } else {
                return leftRoom!!.isConnected && rightRoom!!.isConnected && connected
            }
        }

    fun connect(level: Level, connectFn: BinaryRoom.(Level) -> Unit) {
        if (split) {
            leftRoom!!.connect(level, connectFn)
            rightRoom!!.connect(level, connectFn)
            connectFn(level)
            super.connect()
        } else {
            super.connect()
        }
    }

    fun traverse(fn: BinaryRoom.() -> Unit) {
        traverse({ true }, fn)
    }

    fun traverse(matchFn: BinaryRoom.() -> Boolean, fn: BinaryRoom.() -> Unit) {
        match(matchFn).forEach(fn)
    }

    fun traverseLeaves(fn: BinaryRoom.() -> Unit) {
        leaves.forEach(fn)
    }

    fun match(fn: BinaryRoom.() -> Boolean): List<BinaryRoom> =
        mutableListOf<BinaryRoom>().also { list ->
            if (fn(this)) {
                list.add(this)
            } else {
                if (leftRoom != null) {
                    list.addAll(leftRoom!!.match(fn))
                }

                if (rightRoom != null) {
                    list.addAll(rightRoom!!.match(fn))
                }
            }
        }.toImmutableList()

    val leaves: List<BinaryRoom>
        get() = match { isLeaf }

    operator fun get(pos: Position): Room =
        getRoom(pos)

    val subRoom: Room? by lazy {
        if (isLeaf) createSubroom()
        else null
    }

    val leftSubRoom: Room?
        get() = if (isLeaf) subRoom else leftRoom?.leftSubRoom

    val rightSubRoom: Room?
        get() = if (isLeaf) subRoom else rightRoom?.rightSubRoom

    init {
        if (height < minHeight || width < maxWidth) {
            logGameEvent("Small Room", this)
        }
    }

    val isLeaf: Boolean
        get() = !split

    fun split() {
        val rand: Float = Random.nextFloat()

        if (!split && (rand < 0.5f && width >= 2 * minWidth)) {
            verticalSplit()
        }

        if (!split && (rand >= 0.5f && height >= 2 * minHeight)) {
            horizontalSplit()
        }

        if (!split && width > maxWidth) {
            verticalSplit()
        }

        if (!split && height > maxHeight) {
            horizontalSplit()
        }
    }

    private fun verticalSplit() {
        val minX = left + 1 + minWidth
        val maxX = right - minWidth

        if (minX < maxX) {
            val x = Random.nextInt(minX, maxX)

            leftRoom = BinaryRoom(left, x - 1, top, bottom)
            rightRoom = BinaryRoom(x, right, top, bottom)

            leftRoom?.split()
            rightRoom?.split()

            verticalSplit = true
        }
    }

    private fun horizontalSplit() {
        val minY = top + 1 + minHeight
        val maxY = bottom - maxHeight

        if (minY < maxY) {
            val y = Random.nextInt(minY, maxY)

            leftRoom = BinaryRoom(left, right, top, y - 1)
            rightRoom = BinaryRoom(left, right, y, bottom)

            leftRoom?.split()
            rightRoom?.split()

            horizontalSplit = true
        }
    }

    private fun createSubroom(): Room {
        val roomWidth = Random.nextInt(5, 10)
        val roomHeight = Random.nextInt(5, 10)

        val roomX = left + Random.nextInt(1, width - roomWidth - 1)
        val roomY = top + Random.nextInt(1, height - roomHeight - 1)

        return Room(roomX, roomX + roomWidth, roomY, roomY + roomHeight)
    }

    fun getRoom(pos: Position): Room {
        if (isLeaf) {
            if (subRoom?.contains(pos) == true) {
                return subRoom!!
            } else {
                return this
            }
        } else {
            if (leftRoom?.contains(pos) == true) {
                return leftRoom?.getRoom(pos)!!
            }
            if (rightRoom?.contains(pos) == true) {
                return rightRoom?.getRoom(pos)!!
            }
        }
        return this
    }

    fun getClosestRoomTo(room: Room): BinaryRoom {
        var min: Double = Double.NaN
        var result: BinaryRoom? = null

        (room.left..room.right).forEach { x ->
            val p1 = Position.create(x, room.top)
            val p2 = Position.create(x, room.bottom)

            val ld1 = leftRoom?.distanceTo(p1)!!
            val ld2 = leftRoom?.distanceTo(p2)!!
            val ld = Math.min(ld1, ld2)

            if (min.isNaN() || min > ld) {
                min = ld
                result = leftRoom
            }

            val rd1 = rightRoom?.distanceTo(p1)!!
            val rd2 = rightRoom?.distanceTo(p2)!!
            val rd = Math.min(rd1, rd2)

            if (min.isNaN() || min > rd) {
                min = rd
                result = rightRoom
            }
        }

        (room.top..room.bottom).forEach { y ->
            val p1 = Position.create(room.left, y)
            val p2 = Position.create(room.right, y)

            val ld1 = leftRoom?.distanceTo(p1)!!
            val ld2 = leftRoom?.distanceTo(p2)!!
            val ld = Math.min(ld1, ld2)

            if (min.isNaN() || min > ld) {
                min = ld
                result = leftRoom
            }

            val rd1 = rightRoom?.distanceTo(p1)!!
            val rd2 = rightRoom?.distanceTo(p2)!!
            val rd = Math.min(rd1, rd2)

            if (min.isNaN() || min > rd) {
                min = rd
                result = rightRoom
            }
        }

        return result!!
    }

    fun getClosestRoomTo(pos: Position): Room {
        if (isLeaf) {
            return subRoom!!
        } else {
            if (leftRoom?.distanceTo(pos)!! < rightRoom?.distanceTo(pos)!!) {
                return leftRoom!!.getClosestRoomTo(pos)
            } else {
                return rightRoom!!.getClosestRoomTo(pos)
            }
        }
    }

    fun traverseSubrooms(fn: (Room) -> Unit) =
        traverseLeaves {
            fn(subRoom!!)
        }

    override fun drawWalls(level: Level) {
        if (isLeaf) {
            subRoom?.drawWalls(level)
        } else {
            leftRoom?.drawWalls(level)
            rightRoom?.drawWalls(level)
        }
    }

    override fun draw(level: Level) {
        if (isLeaf) {
            subRoom?.draw(level)
        } else {
            leftRoom?.draw(level)
            rightRoom?.draw(level)
        }
    }

    companion object {
        const val minWidth = 12
        const val maxWidth = 24
        const val minHeight = 12
        const val maxHeight = 24
    }
}