package com.liquidforte.roguelike.entities.behaviors

import com.liquidforte.roguelike.entities.commands.MoveTo
import com.liquidforte.roguelike.extensions.GameEntity
import com.liquidforte.roguelike.extensions.position
import com.liquidforte.roguelike.extensions.sameLevelNeighborsShuffled
import com.liquidforte.roguelike.game.GameContext
import org.hexworks.amethyst.api.base.BaseBehavior
import org.hexworks.amethyst.api.entity.EntityType

object Wanderer : BaseBehavior<GameContext>() {
    override suspend fun update(entity: GameEntity<EntityType>, context: GameContext): Boolean {
        val pos = entity.position
        if (pos.isUnknown.not()) {        // 1
            entity.executeCommand(
                MoveTo(   // 2
                    context = context,
                    source = entity,
                    position = pos.sameLevelNeighborsShuffled().first()
                )
            )   // 3
            return true
        }
        return false
    }
}