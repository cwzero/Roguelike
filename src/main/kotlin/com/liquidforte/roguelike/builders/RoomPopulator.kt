package com.liquidforte.roguelike.builders

class RoomPopulator(private val root: BinaryRoom, private val level: Level) {
    private fun populateRoom(room: Room) {

    }

    fun populateRooms() {
        root.traverseLeaves {
            populateRoom(subRoom!!)
        }
    }
}