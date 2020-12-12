package com.liquidforte.roguelike.entities.facets


import com.liquidforte.roguelike.entities.commands.Attack
import com.liquidforte.roguelike.extensions.GameCommand
import com.liquidforte.roguelike.extensions.isPlayer
import com.liquidforte.roguelike.game.GameContext
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.amethyst.api.entity.EntityType

import com.liquidforte.roguelike.entities.attributes.types.combatStats
import com.liquidforte.roguelike.entities.attributes.types.whenHasNoHealthLeft
import com.liquidforte.roguelike.entities.commands.Destroy
import com.liquidforte.roguelike.functions.logGameEvent
import kotlinx.coroutines.runBlocking
import org.hexworks.amethyst.api.Pass

object Attackable : BaseFacet<GameContext>() {
    override suspend fun executeCommand(command: GameCommand<out EntityType>) =
        command.responseWhenCommandIs(Attack::class) { (context, attacker, target) ->
            if (attacker.isPlayer || target.isPlayer) {
                val damage = 0.coerceAtLeast(attacker.combatStats.attackValue - target.combatStats.defenseValue)
                val finalDamage = (Math.random() * damage).toInt() + 1
                target.combatStats.hp -= finalDamage

                logGameEvent("The $attacker hits the $target for $finalDamage!", this)

                target.whenHasNoHealthLeft {    // 5
                    runBlocking {
                        target.executeCommand(
                            Destroy(  // 6
                                context = context,
                                source = attacker,
                                target = target,
                                cause = "a blow to the head"
                            )
                        )
                    }
                }

                Consumed    // 7
            } else Pass
        }
}