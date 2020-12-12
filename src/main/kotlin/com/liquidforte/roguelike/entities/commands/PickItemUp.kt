package com.liquidforte.roguelike.entities.commands

import com.liquidforte.roguelike.entities.attributes.types.ItemHolder
import com.liquidforte.roguelike.extensions.GameCommand
import com.liquidforte.roguelike.extensions.GameItemHolder
import com.liquidforte.roguelike.game.GameContext
import org.hexworks.zircon.api.data.Position3D

data class PickItemUp(
    override val context: GameContext,
    override val source: GameItemHolder,                  // 1
    val position: Position3D
) : GameCommand<ItemHolder>