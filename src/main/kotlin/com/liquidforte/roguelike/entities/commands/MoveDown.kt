package com.liquidforte.roguelike.entities.commands

import com.liquidforte.roguelike.extensions.GameCommand
import com.liquidforte.roguelike.extensions.GameEntity
import com.liquidforte.roguelike.game.GameContext
import org.hexworks.amethyst.api.entity.EntityType

data class MoveDown(override val context: GameContext,
                    override val source: GameEntity<EntityType>) : GameCommand<EntityType>