package com.liquidforte.roguelike.entities.commands

import com.liquidforte.roguelike.extensions.GameCommand
import com.liquidforte.roguelike.extensions.GameEntity
import com.liquidforte.roguelike.game.GameContext
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.zircon.api.data.Position3D

data class MoveTo(override val context: GameContext,
                  override val source: GameEntity<EntityType>,
                  val position: Position3D) : GameCommand<EntityType>