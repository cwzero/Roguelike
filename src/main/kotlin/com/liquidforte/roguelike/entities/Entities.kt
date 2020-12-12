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
import com.liquidforte.roguelike.entities.behaviors.FungusGrowth
import com.liquidforte.roguelike.entities.behaviors.Wanderer
import com.liquidforte.roguelike.entities.commands.Attack
import com.liquidforte.roguelike.entities.facets.*
import org.hexworks.amethyst.api.builder.EntityBuilder
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.amethyst.api.newEntityOfType
import org.hexworks.zircon.api.GraphicalTilesetResources
import org.hexworks.zircon.api.data.CharacterTile
import org.hexworks.zircon.api.data.Tile

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
            EntityActions(Open::class, Attack::class),
            CombatStats.create(
                maxHp = 100,
                attackValue = 10,
                defenseValue = 5
            )
        )
        behaviors(InputReceiver, PlayerVision)
        facets(Movable, CameraMover, StairAscender, StairDescender, Attackable, Destructible, ItemPicker)
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

    fun newFungus(fungusSpread: FungusSpread = FungusSpread()) = newGameEntityOfType(Fungus) {
        attributes(
            BlockOccupier,
            EntityPosition(),
            EntityTile(GameTiles.FUNGUS),
            fungusSpread,
            CombatStats.create(
                maxHp = 10,
                attackValue = 0,
                defenseValue = 0
            )
        )
        facets(Attackable, Destructible)
        behaviors(FungusGrowth)
    }

    fun newBat() = newGameEntityOfType(Bat) {
        attributes(
            BlockOccupier,                   // 1
            EntityPosition(),
            EntityTile(GameTiles.BAT),
            CombatStats.create(                 // 2
                maxHp = 5,
                attackValue = 2,
                defenseValue = 1
            ),
            EntityActions(Attack::class)
        )       // 3
        facets(Movable, Attackable, Destructible)   // 4
        behaviors(Wanderer)                         // 5
    }

    fun newZircon() = newGameEntityOfType(Zircon) {
        attributes(
            ItemIcon(
                Tile.newBuilder()
                    .withName("white gem")
                    .withTileset(GraphicalTilesetResources.nethack16x16())
                    .buildGraphicalTile()
            ),
            EntityPosition(),
            EntityTile(GameTiles.ZIRCON)
        )
    }
}