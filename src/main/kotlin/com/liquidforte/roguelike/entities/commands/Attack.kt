package com.liquidforte.roguelike.entities.commands

import com.liquidforte.roguelike.entities.action.EntityAction
import com.liquidforte.roguelike.extensions.GameEntity
import com.liquidforte.roguelike.game.GameContext
import org.hexworks.amethyst.api.entity.EntityType

data class Attack(
    override val context: GameContext,
    override val source: GameEntity<EntityType>,
    override val target: GameEntity<EntityType>
) : EntityAction<EntityType, EntityType>
