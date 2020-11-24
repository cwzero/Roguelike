package com.liquidforte.roguelike.world

import com.liquidforte.roguelike.blocks.GameBlock
import com.liquidforte.roguelike.blocks.GameBlocks
import com.liquidforte.roguelike.entities.attributes.Vision
import com.liquidforte.roguelike.extensions.*
import com.liquidforte.roguelike.game.Game
import com.liquidforte.roguelike.game.GameContext
import org.hexworks.amethyst.api.Engine
import org.hexworks.amethyst.api.Engines
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.zircon.api.data.*
import org.hexworks.zircon.api.screen.Screen
import org.hexworks.zircon.api.shape.EllipseFactory
import org.hexworks.zircon.api.shape.LineFactory
import org.hexworks.zircon.api.uievent.UIEvent
import kotlin.math.abs
import kotlin.random.Random

class World(startingBlocks: Map<Position3D, GameBlock>,
        visibleSize: Size3D,
        actualSize: Size3D)
    : GameAreaBase(visibleSize, actualSize) {
    private val engine: Engine<GameContext> = Engines.newEngine()

    init {
        startingBlocks.forEach { (pos, block) ->
            setBlockAt(pos, block)
            block.entities.forEach { entity ->
                engine.addEntity(entity)
                entity.position = pos
            }
        }
        scrollUpBy(actualSize.zLength)
        center()
    }

    fun findVisiblePositionsFor(entity: GameEntity<EntityType>): Iterable<Position> {
        val centerPos = entity.position.to2DPosition()
        return entity.findAttribute(Vision::class).map { (radius) ->
            EllipseFactory.buildEllipse(
                    fromPosition = centerPos,
                    toPosition = centerPos.withRelativeX(radius).withRelativeY(radius))
                    .positions
                    .flatMap { ringPos ->
                        val result = mutableListOf<Position>()
                        val iter = LineFactory.buildLine(centerPos, ringPos).iterator()
                        do {
                            val next = iter.next()
                            result.add(next)
                        } while (iter.hasNext() &&
                                isVisionBlockedAt(Position3D.from2DPosition(next, entity.position.z)).not())
                        result
                    }
        }.orElse(listOf())
    }

    fun isVisionBlockedAt(pos: Position3D): Boolean {
        return fetchBlockAt(pos).fold(whenEmpty = { false }, whenPresent = {
            it.entities.any(GameEntity<EntityType>::blocksVision)
        })
    }

    fun whenCanSee(looker: GameEntity<EntityType>, target: GameEntity<EntityType>, fn: (path: List<Position>) -> Unit) {
        looker.findAttribute(Vision::class).map { (radius) ->
            val level = looker.position.z
            if (looker.position.isWithinRangeOf(target.position, radius)) {
                val path = LineFactory.buildLine(looker.position.to2DPosition(), target.position.to2DPosition())
                if (path.none { isVisionBlockedAt(Position3D.from2DPosition(it, level)) }) {
                    fn(path.positions.toList().drop(1))
                }
            }
        }
    }

    private fun Position3D.isWithinRangeOf(other: Position3D, radius: Int): Boolean {
        return this.isUnknown.not()
                && other.isUnknown.not()
                && this.z == other.z
                && abs(x - other.x) + abs(y - other.y) <= radius
    }

    fun addEntity(entity: AnyGameEntity, position: Position3D) : AnyGameEntity {
        entity.position = bounds.relative(position)
        engine.addEntity(entity)

        if (!hasBlockAt(position)) {
            setBlockAt(position, GameBlocks.floor())
        }

        fetchBlockAt(position).map {
            it.addEntity(entity)
        }

        return entity
    }

    fun findEmptyLocationWithin(offset: Position3D = Position3D.create(centerPoint.x, centerPoint.y, actualSize.zLength - 1),
                                      size: Size = Size.create(visibleSize.xLength, visibleSize.yLength)): Maybe<Position3D> {
        var position = Maybe.empty<Position3D>()
        val maxTries = 10
        var currentTry = 0
        while (position.isPresent.not() && currentTry < maxTries) {
            val pos = Position3D.create(
                    x = Random.nextInt(size.width) + offset.x,
                    y = Random.nextInt(size.height) + offset.y,
                    z = offset.z)
            if (!hasBlockAt(pos)) {
                position = Maybe.of(pos)
            } else {
                fetchBlockAt(pos).map {
                    if (it.isEmptyFloor) {
                        position = Maybe.of(pos)
                    }
                }
            }

            currentTry++
        }
        return position
    }

    fun addAtEmptyPosition(entity: AnyGameEntity,
                           offset: Position3D = Position3D.create(centerPoint.x, centerPoint.y, actualSize.zLength - 1),
                           size: Size = Size.create(visibleSize.xLength, visibleSize.yLength)): Boolean = findEmptyLocationWithin(offset, size).fold(
                whenEmpty = {
                    false
                },
                whenPresent = { location ->
                    addEntity(entity, location)
                    true
                })

    fun moveEntity(entity: AnyGameEntity, position: Position3D): Boolean {
        if (!actualSize.containsPosition(position)) {
            return false
        }

        if (!hasBlockAt(position)) {
            setBlockAt(position, GameBlocks.floor())
        }

        if (hasBlockAt(entity.position) && hasBlockAt(position)) {
            val oldBlock = fetchBlockAt(entity.position).get()
            val newBlock = fetchBlockAt(position).get()

            if (oldBlock.hasEntity(entity) && !(entity.occupiesBlock && newBlock.isOccupied)) {
                if (oldBlock.removeEntity(entity)) {
                    entity.position = position
                    newBlock.addEntity(entity)
                    return true
                }
            }
        }
        return false
    }

    fun update(screen: Screen, uiEvent: UIEvent, game: Game) {
        engine.update(GameContext(
                world = this,
                player = game.player,
                screen = screen,
                uiEvent = uiEvent))
    }
}