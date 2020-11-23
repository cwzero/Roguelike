package com.liquidforte.roguelike.entities.facets

import com.liquidforte.roguelike.blocks.GameBlock
import com.liquidforte.roguelike.entities.attributes.types.StairsUp
import com.liquidforte.roguelike.entities.commands.MoveUp
import com.liquidforte.roguelike.extensions.GameCommand
import com.liquidforte.roguelike.extensions.position
import com.liquidforte.roguelike.functions.logGameEvent
import com.liquidforte.roguelike.game.GameContext
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.amethyst.api.entity.EntityType

object StairAscender : BaseFacet<GameContext>() {
    override suspend fun executeCommand(command: GameCommand<out EntityType>) = command.responseWhenCommandIs(MoveUp::class) { (context, source) ->
        val (world, player, screen, uiEvent) = context
        val pos = source.position
        world.fetchBlockAt(pos).map { block ->
            if (block.hasStairsUp) {
                logGameEvent("You move up one level...", this)
                world.moveEntity(player, pos.withRelativeZ(1))
                world.scrollOneUp()
            } else {
                logGameEvent("You jump up and try to reach the ceiling. You fail.", this)
            }
        }
        Consumed
    }

    private val GameBlock.hasStairsUp: Boolean
        get() = this.entities.any { it.type == StairsUp }
}