package com.liquidforte.roguelike.entities.facets

import com.liquidforte.roguelike.entities.action.Open
import com.liquidforte.roguelike.extensions.AnyGameEntity
import com.liquidforte.roguelike.game.GameContext
import org.hexworks.amethyst.api.Command
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.Response
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.amethyst.api.entity.EntityType

class Openable(private val open: AnyGameEntity) : BaseFacet<GameContext>() {
    override suspend fun executeCommand(command: Command<out EntityType, GameContext>): Response = command.responseWhenCommandIs(Open::class) { (context, _, target) ->
        context.world.transformEntity(target, open)
        Consumed
    }
}