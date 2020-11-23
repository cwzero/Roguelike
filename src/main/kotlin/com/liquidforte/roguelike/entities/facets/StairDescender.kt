package com.liquidforte.roguelike.entities.facets

import com.liquidforte.roguelike.blocks.GameBlock
import com.liquidforte.roguelike.entities.attributes.types.StairsDown
import com.liquidforte.roguelike.entities.commands.MoveDown
import com.liquidforte.roguelike.extensions.position
import com.liquidforte.roguelike.functions.logGameEvent
import com.liquidforte.roguelike.game.GameContext
import org.hexworks.amethyst.api.Command
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.Response
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.amethyst.api.entity.EntityType

object StairDescender : BaseFacet<GameContext>() {
    override suspend fun executeCommand(command: Command<out EntityType, GameContext>): Response = command.responseWhenCommandIs(MoveDown::class) { (context, source) ->
        val (world, player, screen, uiEvent) = context
        val pos = source.position
        world.fetchBlockAt(pos).map { block ->
            when {
                block.hasStairsDown -> {
                    logGameEvent("You move down one level...", this)
                    world.moveEntity(source, pos.withRelativeZ(-1))
                    world.scrollOneDown()
                }
                else -> logGameEvent("You search for a trapdoor, but you find nothing.", this)
            }
        }
        Consumed
    }

    private val GameBlock.hasStairsDown: Boolean
        get() = this.entities.any { it.type == StairsDown }
}