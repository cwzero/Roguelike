package com.liquidforte.roguelike.builders

import com.liquidforte.roguelike.config.GameConfig.BATS_PER_LEVEL
import com.liquidforte.roguelike.config.GameConfig.FUNGI_PER_LEVEL
import com.liquidforte.roguelike.config.GameConfig.VISIBLE_SIZE
import com.liquidforte.roguelike.config.GameConfig.WORLD_SIZE
import com.liquidforte.roguelike.config.GameConfig.ZIRCONS_PER_LEVEL
import com.liquidforte.roguelike.entities.Entities
import com.liquidforte.roguelike.entities.attributes.types.Player
import com.liquidforte.roguelike.extensions.GameEntity
import com.liquidforte.roguelike.extensions.position
import com.liquidforte.roguelike.game.Game
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.zircon.api.data.Position3D
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.data.Size3D
import kotlin.system.exitProcess

class GameBuilder(val worldSize: Size3D = WORLD_SIZE) {
    private val visibleSize = VISIBLE_SIZE
    private val world = WorldBuilder(worldSize).generate().build(visibleSize = visibleSize)

    fun buildGame(): Game {
        val player = addPlayer()
        //addFungi()
        addBats()
        addZircons()

        prepareWorld(player)

        return Game(world, player)
    }

    private fun prepareWorld(player: GameEntity<Player>) = also {
        world.center(player.position)
    }

    private fun addFungi() = also {
        repeat(world.actualSize.zLength) { level ->
            repeat(FUNGI_PER_LEVEL) {
                Entities.newFungus().addToWorld(level)
            }
        }
    }

    private fun addBats() = also {
        repeat(world.actualSize.zLength) { level ->
            repeat(BATS_PER_LEVEL) {
                Entities.newBat().addToWorld(level)
            }
        }
    }

    private fun addZircons() = also {
        repeat(world.actualSize.zLength) { level ->
            repeat(ZIRCONS_PER_LEVEL) {
                Entities.newZircon().addToWorld(level)
            }
        }
    }

    private fun addPlayer(): GameEntity<Player> {
        return Entities.newPlayer().addToWorld(world.actualSize.zLength - 1)
    }

    private fun <T : EntityType> GameEntity<T>.addToWorld(  // 1
        atLevel: Int,                                   // 2
        atArea: Size = world.actualSize.to2DSize()
    ): GameEntity<T> {  // 3
        world.addAtEmptyPosition(
            this,
            offset = Position3D.defaultPosition().withZ(atLevel),      // 4
            size = atArea
        )                           // 5
        return this
    }

    companion object {
        fun defaultGame() = GameBuilder(
            worldSize = WORLD_SIZE
        ).buildGame()
    }
}