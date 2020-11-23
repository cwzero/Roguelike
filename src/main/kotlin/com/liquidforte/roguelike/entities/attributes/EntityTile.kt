package com.liquidforte.roguelike.entities.attributes

import com.liquidforte.roguelike.config.GameTiles
import org.hexworks.amethyst.api.Attribute
import org.hexworks.zircon.api.data.Tile

data class EntityTile(val tile: Tile = GameTiles.EMPTY) : Attribute