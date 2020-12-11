package com.liquidforte.roguelike.builders

import com.liquidforte.roguelike.config.GameConfig.VISIBLE_SIZE
import com.liquidforte.roguelike.config.GameConfig.WORLD_SIZE
import com.liquidforte.roguelike.entities.Entities
import com.liquidforte.roguelike.entities.attributes.types.Player
import com.liquidforte.roguelike.extensions.GameEntity
import com.liquidforte.roguelike.extensions.position
import com.liquidforte.roguelike.game.Game
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.zircon.api.data.Size3D
import kotlin.system.exitProcess

class GameBuilder(val worldSize: Size3D = WORLD_SIZE) {
    private val visibleSize = VISIBLE_SIZE
    private val world = WorldBuilder(worldSize).generate().build(visibleSize = visibleSize)

    fun buildGame(): Game {
        val player = addPlayer()

        prepareWorld(player)

        return Game(world, player)
    }

    private fun prepareWorld(player: GameEntity<Player>) = also {
        world.center(player.position)
    }

    private fun addPlayer(): GameEntity<Player> {
        return Entities.newPlayer().addToWorld()
    }

    private fun <T : EntityType> GameEntity<T>.addToWorld(): GameEntity<T> = apply {
        world.addAtEmptyPosition(this)
    }

    companion object {
        fun defaultGame() = GameBuilder(
            worldSize = WORLD_SIZE
        ).buildGame()
    }
}