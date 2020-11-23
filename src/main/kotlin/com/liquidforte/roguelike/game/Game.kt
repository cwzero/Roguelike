package com.liquidforte.roguelike.game

import com.liquidforte.roguelike.entities.attributes.types.Player
import com.liquidforte.roguelike.extensions.GameEntity
import com.liquidforte.roguelike.world.World

data class Game(val world: World, val player: GameEntity<Player>)