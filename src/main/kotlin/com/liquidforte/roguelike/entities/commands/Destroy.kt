package com.liquidforte.roguelike.entities.commands

import com.liquidforte.roguelike.extensions.GameCommand
import com.liquidforte.roguelike.extensions.GameEntity
import com.liquidforte.roguelike.game.GameContext
import org.hexworks.amethyst.api.entity.EntityType

data class Destroy(
    override val context: GameContext,
    override val source: GameEntity<EntityType>,
    val target: GameEntity<EntityType>,
    val cause: String = "natural causes."
) : GameCommand<EntityType>