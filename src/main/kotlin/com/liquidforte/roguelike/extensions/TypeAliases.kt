package com.liquidforte.roguelike.extensions

import com.liquidforte.roguelike.entities.attributes.types.Item
import com.liquidforte.roguelike.entities.attributes.types.ItemHolder
import com.liquidforte.roguelike.game.GameContext
import org.hexworks.amethyst.api.Command
import org.hexworks.amethyst.api.entity.Entity
import org.hexworks.amethyst.api.entity.EntityType

typealias GameEntity<T> = Entity<T, GameContext>

typealias AnyGameEntity = GameEntity<EntityType>

typealias GameCommand<T> = Command<T, GameContext>

typealias GameItem = GameEntity<Item>

typealias GameItemHolder = GameEntity<ItemHolder>