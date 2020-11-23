package com.liquidforte.roguelike.extensions

import com.liquidforte.roguelike.entities.attributes.EntityPosition
import com.liquidforte.roguelike.entities.attributes.EntityTile
import com.liquidforte.roguelike.entities.attributes.Vision
import com.liquidforte.roguelike.entities.attributes.flags.BlockOccupier
import com.liquidforte.roguelike.entities.attributes.flags.VisionBlocker
import com.liquidforte.roguelike.entities.attributes.types.Player
import org.hexworks.amethyst.api.Attribute
import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.zircon.api.data.Position3D
import org.hexworks.zircon.api.data.Tile
import kotlin.reflect.KClass


val AnyGameEntity.occupiesBlock: Boolean
    get() = findAttribute(BlockOccupier::class).isPresent

val AnyGameEntity.tile: Maybe<Tile>
    get() = this.findAttribute(EntityTile::class).map { it.tile }

fun AnyGameEntity.isVisible(position: Position3D): Boolean {
    return if (vision.isEmpty() || position.isUnknown) {
        false
    } else {
        val radius: Int = vision.get()
        val diff = position.minus(this.position).to2DPosition()

        ((radius * radius) >= (diff.x * diff.x) + (diff.y * diff.y))
    }
}

val AnyGameEntity.vision: Maybe<Int>
    get() = this.findAttribute(Vision::class).map { it.radius }

val AnyGameEntity.isPlayer: Boolean
    get() = this.type == Player

val AnyGameEntity.blocksVision: Boolean
    get() = this.findAttribute(VisionBlocker::class).isPresent

var AnyGameEntity.position
    get() = tryToFindAttribute(EntityPosition::class).position
    set(value) {
        findAttribute(EntityPosition::class).map {
            it.position = value
        }
    }

fun <T : Attribute> AnyGameEntity.tryToFindAttribute(klass: KClass<T>): T = findAttribute(klass).orElseThrow {
    NoSuchElementException("Entity '$this' has no property with type '${klass.simpleName}'.")
}