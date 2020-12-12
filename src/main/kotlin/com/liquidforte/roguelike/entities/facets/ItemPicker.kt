package com.liquidforte.roguelike.entities.facets

import com.liquidforte.roguelike.entities.attributes.types.Item
import com.liquidforte.roguelike.entities.attributes.types.addItem
import com.liquidforte.roguelike.entities.commands.PickItemUp
import com.liquidforte.roguelike.extensions.GameCommand
import com.liquidforte.roguelike.extensions.filterType
import com.liquidforte.roguelike.extensions.isPlayer
import com.liquidforte.roguelike.functions.logGameEvent
import com.liquidforte.roguelike.game.GameContext
import com.liquidforte.roguelike.world.World
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.zircon.api.data.Position3D


object ItemPicker : BaseFacet<GameContext>() {
    override suspend fun executeCommand(command: GameCommand<out EntityType>) =
        command.responseWhenCommandIs(PickItemUp::class) { (context, itemHolder, position) ->
            val world = context.world
            world.findTopItem(position).map { item ->       // 1
                if (itemHolder.addItem(item)) {             // 2
                    world.removeEntity(item)                // 3
                    val subject = if (itemHolder.isPlayer) "You" else "The $itemHolder" // 4
                    val verb = if (itemHolder.isPlayer) "pick up" else "picks up"
                    logGameEvent("$subject $verb the $item.", this)
                }
            }
            Consumed
        }

    private fun World.findTopItem(position: Position3D) =
        fetchBlockAt(position).flatMap { block ->       // 5
            Maybe.ofNullable(block.entities.filterType<Item>().firstOrNull())
        }
}