package com.liquidforte.roguelike.entities

import com.liquidforte.roguelike.entities.behaviors.InputReceiver
import com.liquidforte.roguelike.entities.behaviors.PlayerVision
import com.liquidforte.roguelike.game.GameContext
import com.liquidforte.roguelike.config.GameTiles
import com.liquidforte.roguelike.entities.action.Open
import com.liquidforte.roguelike.entities.attributes.*
import com.liquidforte.roguelike.entities.attributes.flags.BlockOccupier
import com.liquidforte.roguelike.entities.attributes.flags.RoughWall
import com.liquidforte.roguelike.entities.attributes.flags.VisionBlocker
import com.liquidforte.roguelike.entities.attributes.types.*
import com.liquidforte.roguelike.entities.facets.*
import org.hexworks.amethyst.api.builder.EntityBuilder
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.amethyst.api.newEntityOfType
import org.hexworks.zircon.api.data.CharacterTile

fun <T : EntityType> newGameEntityOfType(type: T, init: EntityBuilder<T, GameContext>.() -> Unit) =
    newEntityOfType(type, init)

object Entities {
    fun newPlayer() = newGameEntityOfType(Player) {
        attributes(
            EntityPosition(),
            EntityTile(GameTiles.PLAYER),
            Inventory(),
            Vision(9),
            BlockOccupier,
            EntityActions(Open::class)
        )
        behaviors(InputReceiver, PlayerVision)
        facets(Movable, CameraMover, StairAscender, StairDescender)
    }

    fun newWall(tile: CharacterTile, rough: Boolean = false) = newGameEntityOfType(Wall) {
        val attr = arrayOf(
            VisionBlocker,
            EntityPosition(),
            BlockOccupier,
            EntityTile(tile)
        )

        if (rough)
            attributes(*attr, RoughWall)
        else
            attributes(*attr)
    }

    fun newOpenDoor() = newGameEntityOfType(OpenDoor) {
        attributes(EntityPosition(), EntityTile(GameTiles.OPEN_DOOR))
    }

    fun newClosedDoor() = newGameEntityOfType(ClosedDoor) {
        attributes(
            VisionBlocker,
            EntityPosition(),
            BlockOccupier,
            EntityTile(GameTiles.CLOSED_DOOR)
        )
        facets(Openable(newOpenDoor()))
    }

    fun newStairsDown() = newGameEntityOfType(StairsDown) {
        attributes(
            EntityTile(GameTiles.STAIRS_DOWN),
            EntityPosition()
        )
    }

    fun newStairsUp() = newGameEntityOfType(StairsUp) {
        attributes(
            EntityTile(GameTiles.STAIRS_UP),
            EntityPosition()
        )
    }
}