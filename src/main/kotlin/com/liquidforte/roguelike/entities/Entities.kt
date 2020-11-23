package com.liquidforte.roguelike.entities

import com.liquidforte.roguelike.entities.attributes.EntityTile
import com.liquidforte.roguelike.entities.attributes.Inventory
import com.liquidforte.roguelike.entities.behaviors.InputReceiver
import com.liquidforte.roguelike.entities.behaviors.PlayerVision
import com.liquidforte.roguelike.entities.facets.CameraMover
import com.liquidforte.roguelike.entities.facets.Movable
import com.liquidforte.roguelike.entities.facets.StairAscender
import com.liquidforte.roguelike.entities.facets.StairDescender
import com.liquidforte.roguelike.game.GameContext
import com.liquidforte.roguelike.config.GameTiles
import com.liquidforte.roguelike.entities.attributes.EntityPosition
import com.liquidforte.roguelike.entities.attributes.Vision
import com.liquidforte.roguelike.entities.attributes.flags.BlockOccupier
import com.liquidforte.roguelike.entities.attributes.flags.VisionBlocker
import com.liquidforte.roguelike.entities.attributes.types.Player
import com.liquidforte.roguelike.entities.attributes.types.StairsDown
import com.liquidforte.roguelike.entities.attributes.types.StairsUp
import com.liquidforte.roguelike.entities.attributes.types.Wall
import org.hexworks.amethyst.api.builder.EntityBuilder
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.amethyst.api.newEntityOfType

fun <T : EntityType> newGameEntityOfType(type: T, init: EntityBuilder<T, GameContext>.() -> Unit) =
        newEntityOfType(type, init)

object Entities {
    fun newPlayer() = newGameEntityOfType(Player) {
        attributes(EntityPosition(), EntityTile(GameTiles.PLAYER), Inventory(), Vision(9), BlockOccupier)
        behaviors(InputReceiver, PlayerVision)
        facets(Movable, CameraMover, StairAscender, StairDescender)
    }

    fun newWall() = newGameEntityOfType(Wall) {
        attributes(
                VisionBlocker,
                EntityPosition(),
                BlockOccupier,
                EntityTile(GameTiles.WALL))
    }

    fun newStairsDown() = newGameEntityOfType(StairsDown) {
        attributes(EntityTile(GameTiles.STAIRS_DOWN),
                EntityPosition())
    }

    fun newStairsUp() = newGameEntityOfType(StairsUp) {
        attributes(EntityTile(GameTiles.STAIRS_UP),
                EntityPosition())
    }
}