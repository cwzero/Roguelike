package com.liquidforte.roguelike.entities.action

import com.liquidforte.roguelike.extensions.AnyGameEntity
import com.liquidforte.roguelike.game.GameContext
import org.hexworks.amethyst.api.entity.EntityType

data class Open(override val context: GameContext,
                override val source: AnyGameEntity,
                override val target: AnyGameEntity) : EntityAction<EntityType, EntityType>
