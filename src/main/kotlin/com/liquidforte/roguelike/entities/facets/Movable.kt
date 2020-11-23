package com.liquidforte.roguelike.entities.facets

import com.liquidforte.roguelike.entities.commands.MoveCamera
import com.liquidforte.roguelike.entities.commands.MoveTo
import com.liquidforte.roguelike.extensions.GameCommand
import com.liquidforte.roguelike.extensions.isPlayer
import com.liquidforte.roguelike.extensions.position
import com.liquidforte.roguelike.game.GameContext
import org.hexworks.amethyst.api.CommandResponse
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.Pass
import org.hexworks.amethyst.api.Response
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.amethyst.api.entity.EntityType

object Movable : BaseFacet<GameContext>() {

    override suspend fun executeCommand(command: GameCommand<out EntityType>) = command.responseWhenCommandIs(MoveTo::class) { (context, entity, position) ->
        val (world, player, screen, uiEvent) = context
        val previousPosition = entity.position
        var result: Response = Pass
        if (world.moveEntity(entity, position)) {
            result = if (entity.isPlayer) {
                CommandResponse(MoveCamera(
                        context = context,
                        source = entity,
                        previousPosition = previousPosition))
            } else Consumed
        }
        result
    }
}