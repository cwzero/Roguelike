package com.liquidforte.roguelike.entities.attributes

import com.liquidforte.roguelike.entities.action.EntityAction
import com.liquidforte.roguelike.extensions.AnyGameEntity
import com.liquidforte.roguelike.game.GameContext
import org.hexworks.amethyst.api.Attribute
import org.hexworks.amethyst.api.entity.EntityType
import kotlin.reflect.KClass

class EntityActions(private vararg val actions: KClass<out EntityAction<out EntityType, out EntityType>>) : Attribute {
    fun createActionsFor(context: GameContext, source: AnyGameEntity, target: AnyGameEntity):
            Iterable<EntityAction<out EntityType, out EntityType>> {
        return actions.map {
            try {
                it.constructors.first().call(context, source, target)   // 3
            } catch (e: Exception) {                                    // 4
                throw IllegalArgumentException("Can't create EntityAction. Does it have the proper constructor?")
            }
        }
    }
}