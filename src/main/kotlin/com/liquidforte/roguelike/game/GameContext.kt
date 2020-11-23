package com.liquidforte.roguelike.game

import com.liquidforte.roguelike.entities.attributes.types.Player
import com.liquidforte.roguelike.extensions.GameEntity
import com.liquidforte.roguelike.world.World
import org.hexworks.amethyst.api.Context
import org.hexworks.zircon.api.screen.Screen
import org.hexworks.zircon.api.uievent.UIEvent

data class GameContext(val world: World, val player: GameEntity<Player>, val screen: Screen, val uiEvent: UIEvent) : Context