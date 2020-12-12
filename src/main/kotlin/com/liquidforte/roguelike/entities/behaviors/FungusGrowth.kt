package com.liquidforte.roguelike.entities.behaviors

import com.liquidforte.roguelike.entities.Entities
import com.liquidforte.roguelike.entities.attributes.FungusSpread
import com.liquidforte.roguelike.extensions.GameEntity
import com.liquidforte.roguelike.extensions.fungusSpread
import com.liquidforte.roguelike.extensions.position
import com.liquidforte.roguelike.extensions.tryToFindAttribute
import com.liquidforte.roguelike.game.GameContext
import org.hexworks.amethyst.api.base.BaseBehavior
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.zircon.api.data.Size

object FungusGrowth : BaseBehavior<GameContext>(FungusSpread::class) {
    override suspend fun update(entity: GameEntity<out EntityType>, context: GameContext): Boolean {
        val world = context.world
        return entity.fungusSpread.map {
            val fungusSpread = it
            val (spreadCount, maxSpread) = fungusSpread
            if (spreadCount < maxSpread && Math.random() < 0.015) {
                world.findEmptyLocationWithin(
                    offset = entity.position
                        .withRelativeX(-1)
                        .withRelativeY(-1),
                    size = Size.create(3, 3)
                ).map { emptyLocation ->
                    world.addEntity(Entities.newFungus(fungusSpread), emptyLocation)
                    fungusSpread.spreadCount++
                }
                true
            } else false
        }.orElse(false)
    }
}