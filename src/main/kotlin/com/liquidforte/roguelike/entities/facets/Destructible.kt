package com.liquidforte.roguelike.entities.facets

import com.liquidforte.roguelike.entities.commands.Destroy
import com.liquidforte.roguelike.extensions.GameCommand
import com.liquidforte.roguelike.functions.logGameEvent
import com.liquidforte.roguelike.game.GameContext
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.amethyst.api.entity.EntityType

object Destructible : BaseFacet<GameContext>() {
    override suspend fun executeCommand(command: GameCommand<out EntityType>) =
        command.responseWhenCommandIs(Destroy::class) { (context, attacker, target, cause) ->
            context.world.removeEntity(target)
            logGameEvent("$target dies after receiving $cause.", this)
            Consumed
        }
}