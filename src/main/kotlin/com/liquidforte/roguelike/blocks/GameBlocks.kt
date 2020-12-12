package com.liquidforte.roguelike.blocks

import com.liquidforte.roguelike.config.GameTiles
import com.liquidforte.roguelike.entities.Entities
import org.hexworks.zircon.api.data.CharacterTile

object GameBlocks {
    fun floor() = GameBlock()

    fun roughFloor() = GameBlock(defaultTile = GameTiles.ROUGH_FLOOR)

    fun roughWall() = wall(GameTiles.UNREVEALED, true)

    fun smoothWall(tile: CharacterTile) = wall(tile, false)

    private fun wall(tile: CharacterTile, rough: Boolean) =
        GameBlock().apply { addEntity(Entities.newWall(tile, rough)) }

    fun stairsDown() = GameBlock().apply { addEntity(Entities.newStairsDown()) }

    fun stairsUp() = GameBlock().apply { addEntity(Entities.newStairsUp()) }

    fun door() = GameBlock().apply { addEntity(Entities.newClosedDoor()) }
}