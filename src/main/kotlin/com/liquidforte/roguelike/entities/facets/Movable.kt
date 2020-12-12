package com.liquidforte.roguelike.entities.facets

import com.liquidforte.roguelike.entities.commands.MoveCamera
import com.liquidforte.roguelike.entities.commands.MoveTo
import com.liquidforte.roguelike.extensions.GameCommand
import com.liquidforte.roguelike.extensions.isPlayer
import com.liquidforte.roguelike.extensions.position
import com.liquidforte.roguelike.extensions.tryActionsOn
import com.liquidforte.roguelike.game.GameContext
import org.hexworks.amethyst.api.CommandResponse
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.Pass
import org.hexworks.amethyst.api.Response
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.amethyst.api.entity.EntityType

object Movable : BaseFacet<GameContext>() {
    override suspend fun executeCommand(command: GameCommand<out EntityType>) =
        command.responseWhenCommandIs(MoveTo::class) { (context, source, position) ->
            val (world, player, screen, uiEvent) = context
            val previousPosition = source.position
            var result: Response = Pass
            world.fetchBlockAt(position).map { block ->
                if (block.isOccupied) {
                    result = source.tryActionsOn(context, block.occupier!!)
                } else {
                    if (world.moveEntity(source, position)) {
                        result = if (source.isPlayer) {
                            CommandResponse(
                                MoveCamera(
                                    context = context,
                                    source = source,
                                    previousPosition = previousPosition
                                )
                            )
                        } else Consumed
                    }
                }
            }
            result
        }
}