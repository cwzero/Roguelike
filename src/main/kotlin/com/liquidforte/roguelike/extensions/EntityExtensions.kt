package com.liquidforte.roguelike.extensions

import com.liquidforte.roguelike.entities.attributes.*
import com.liquidforte.roguelike.entities.attributes.flags.BlockOccupier
import com.liquidforte.roguelike.entities.attributes.flags.RoughWall
import com.liquidforte.roguelike.entities.attributes.flags.VisionBlocker
import com.liquidforte.roguelike.entities.attributes.types.*
import com.liquidforte.roguelike.entities.facets.Openable
import com.liquidforte.roguelike.game.GameContext
import kotlinx.coroutines.runBlocking
import org.hexworks.amethyst.api.Attribute
import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.zircon.api.data.Position3D
import org.hexworks.zircon.api.data.Tile
import kotlin.reflect.KClass
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.Pass
import org.hexworks.amethyst.api.Response
import org.hexworks.amethyst.api.entity.Entity
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.zircon.api.data.GraphicalTile
import kotlin.reflect.full.isSuperclassOf

val AnyGameEntity.occupiesBlock: Boolean
    get() = findAttribute(BlockOccupier::class).isPresent

val AnyGameEntity.tile: Maybe<Tile>
    get() = this.findAttribute(EntityTile::class).map { it.tile }

val GameEntity<Item>.iconTile: GraphicalTile
    get() = findAttribute(ItemIcon::class).get().iconTile

val AnyGameEntity.isWall: Boolean
    get() = this.let { it.type is Wall }

val AnyGameEntity.isRoughWall: Boolean
    get() = this.let { it.isWall && it.findAttribute(RoughWall::class).isPresent }

val AnyGameEntity.isDoor: Boolean
    get() = this.let { it.type is Door }

val GameEntity<Door>.isOpen: Boolean
    get() = this.findFacet(Openable::class).isEmpty()

val GameEntity<Door>.isClosed: Boolean
    get() = this.findFacet(Openable::class).isPresent

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

fun AnyGameEntity.tryActionsOn(context: GameContext, target: AnyGameEntity): Response {
    var result: Response = Pass
    findAttribute(EntityActions::class).map { actions ->
        runBlocking {
            actions.createActionsFor(context, this@tryActionsOn, target).forEach { action ->
                if (target.executeCommand(action) is Consumed) {
                    result = Consumed
                    return@forEach
                }
            }
        }
    }
    return result
}

inline fun <reified T : EntityType> Iterable<AnyGameEntity>.filterType(): List<Entity<T, GameContext>> {
    return filter { T::class.isSuperclassOf(it.type::class) }.toList() as List<Entity<T, GameContext>>
}