package com.liquidforte.roguelike.entities.behaviors

import com.liquidforte.roguelike.extensions.isVisible
import com.liquidforte.roguelike.extensions.position
import com.liquidforte.roguelike.game.GameContext
import org.hexworks.amethyst.api.base.BaseBehavior
import org.hexworks.amethyst.api.entity.Entity
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.zircon.api.data.Position3D

object PlayerVision : BaseBehavior<GameContext>() {
    private var previous: MutableSet<Position3D> = mutableSetOf()

    override suspend fun update(entity: Entity<EntityType, GameContext>, context: GameContext): Boolean {
        val (world, player, _, _) = context

        previous.filter { !player.isVisible(it) }.forEach { pos ->
            //world.fetchBlockAt(pos).map { it.render = false }
        }

        previous = mutableSetOf()

        world.findVisiblePositionsFor(player).map { it.toPosition3D(player.position.z) }.forEach { pos ->
            world.fetchBlockAt(pos).map { block ->
                block.render = true
                previous.add(pos)
            }
        }

        return true
    }
}