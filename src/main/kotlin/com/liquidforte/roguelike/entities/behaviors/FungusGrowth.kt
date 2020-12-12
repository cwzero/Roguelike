package com.liquidforte.roguelike.entities.behaviors

import com.liquidforte.roguelike.entities.Entities
import com.liquidforte.roguelike.entities.attributes.FungusSpread
import com.liquidforte.roguelike.extensions.GameEntity
import com.liquidforte.roguelike.extensions.position
import com.liquidforte.roguelike.extensions.tryToFindAttribute
import com.liquidforte.roguelike.game.GameContext
import org.hexworks.amethyst.api.base.BaseBehavior
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.zircon.api.data.Size

object FungusGrowth : BaseBehavior<GameContext>(FungusSpread::class) {
    override suspend fun update(entity: GameEntity<out EntityType>, context: GameContext): Boolean {
        val world = context.world
        val fungusSpread = entity.tryToFindAttribute(FungusSpread::class)   // 2
        val (spreadCount, maxSpread) = fungusSpread                         // 3
        return if (spreadCount < maxSpread && Math.random() < 0.015) {      // 4
            world.findEmptyLocationWithin(
                offset = entity.position
                    .withRelativeX(-1)
                    .withRelativeY(-1),
                size = Size.create(3, 3,)
            ).map { emptyLocation ->
                world.addEntity(Entities.newFungus(fungusSpread), emptyLocation)   // 5
                fungusSpread.spreadCount++
            }
            true
        } else false
    }
}