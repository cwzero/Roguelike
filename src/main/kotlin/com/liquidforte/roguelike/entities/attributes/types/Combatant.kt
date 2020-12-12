package com.liquidforte.roguelike.entities.attributes.types

import com.liquidforte.roguelike.entities.attributes.CombatStats
import com.liquidforte.roguelike.extensions.GameEntity
import org.hexworks.amethyst.api.entity.EntityType

interface Combatant : EntityType

val GameEntity<EntityType>.combatStats: CombatStats
    get() = findAttribute(CombatStats::class).get()

fun GameEntity<EntityType>.whenHasNoHealthLeft(fn: () -> Unit) {
    if (combatStats.hp <= 0) {
        fn()
    }
}