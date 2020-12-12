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
import org.hexworks.zircon.api.builder.game.GameAreaBuilder
import org.hexworks.zircon.api.data.*
import org.hexworks.zircon.api.game.GameArea
import org.hexworks.zircon.api.game.ProjectionMode
import org.hexworks.zircon.api.game.base.BaseGameArea
import org.hexworks.zircon.api.screen.Screen
import org.hexworks.zircon.api.shape.EllipseFactory
import org.hexworks.zircon.api.shape.LineFactory
import org.hexworks.zircon.api.uievent.UIEvent
import org.hexworks.zircon.internal.game.ProjectionStrategy
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class World(
    startingBlocks: Map<Position3D, GameBlock>,
    visibleSize: Size3D,
    actualSize: Size3D
) : BaseGameArea<Tile, GameBlock>(visibleSize, actualSize) {
    private val engine: Engine<GameContext> = Engines.newEngine()

    init {
        startingBlocks.forEach { (pos, block) ->
            setBlockAt(pos, block)
            block.entities.forEach { entity ->
                engine.addEntity(entity)
                entity.position = pos
            }
        }
    }

    fun scrollBy(deltaX: Int = 0, deltaY: Int = 0, deltaZ: Int = 0) {
        var x = visibleOffset.x + deltaX
        var y = visibleOffset.y + deltaY
        var z = visibleOffset.z + deltaZ

        val maxX = (actualSize.xLength - 1) - (visibleSize.xLength - 1)
        val maxY = (actualSize.yLength - 1) - (visibleSize.yLength - 1)
        val maxZ = actualSize.zLength

        x = x.coerceIn(0..maxX)
        y = y.coerceIn(0..maxY)
        z = z.coerceIn(0..maxZ)

        val newPos = Position3D.create(x, y, z)

        scrollTo(newPos)
    }

    val centerPoint: Position
        get() {
            return Position.create(actualSize.xLength / 2, actualSize.yLength / 2)
        }

    fun center() {
        val offset: Position = actualSize.run {
            Position.create(xLength / 2, yLength / 2)
        }.minus(visibleOffset.to2DPosition())

        scrollRightBy(offset.x)
        scrollForwardBy(offset.y)
    }

    fun center(pos: Position3D) {
        val offset = Position3D.create(
            visibleSize.xLength / 2,
            visibleSize.yLength / 2,
            0
        )
        moveCameraTo(pos - offset)
    }

    fun moveCameraTo(pos: Position3D) {
        var x = pos.x
        var y = pos.y
        var z = pos.z

        val maxX = (actualSize.xLength - 1) - (visibleSize.xLength - 1)
        val maxY = (actualSize.yLength - 1) - (visibleSize.yLength - 1)
        val maxZ = actualSize.zLength - 1

        x = x.coerceIn(0..maxX)
        y = y.coerceIn(0..maxY)
        z = z.coerceIn(0..maxZ)

        val newPos = Position3D.create(x, y, z)

        scrollTo(newPos)
    }


    fun findVisiblePositionsFor(entity: GameEntity<EntityType>): Iterable<Position> {
        val centerPos = entity.position.to2DPosition()
        return entity.findAttribute(Vision::class).map { (radius) ->
            EllipseFactory.buildEllipse(
                fromPosition = centerPos,
                toPosition = centerPos.withRelativeX(radius).withRelativeY(radius)
            )
                .positions
                .flatMap { ringPos ->
                    val result = mutableListOf<Position>()
                    val iter = LineFactory.buildLine(centerPos, ringPos).iterator()
                    do {
                        val next = iter.next()
                        result.add(next)
                    } while (iter.hasNext() &&
                        isVisionBlockedAt(Position3D.from2DPosition(next, entity.position.z)).not()
                    )
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

    fun addEntity(entity: AnyGameEntity, position: Position3D): AnyGameEntity {
        entity.position = position
        engine.addEntity(entity)

        if (!hasBlockAt(position)) {
            setBlockAt(position, GameBlocks.floor())
        }

        fetchBlockAt(position).map {
            it.addEntity(entity)
        }

        return entity
    }

    fun findEmptyLocationWithin(
        offset: Position3D = Position3D.create(centerPoint.x, centerPoint.y, actualSize.zLength - 1),
        size: Size = Size.create(visibleSize.xLength, visibleSize.yLength)
    ): Maybe<Position3D> {
        val xRange = offset.x..(offset.x + size.width)
        val yRange = offset.y..(offset.y + size.width)
        val positions = xRange.flatMap { x -> yRange.map { y -> Position3D.create(x, y, offset.z) } }

        val validPositions = positions.filter { fetchBlockAt(it).map { it.isSmoothFloor }.orElse(false) }

        if (validPositions.any()) {
            return Maybe.of(validPositions.shuffled().first())
        } else {
            return Maybe.empty()
        }
    }

    fun addAtEmptyPosition(
        entity: AnyGameEntity,
        offset: Position3D = Position3D.create(centerPoint.x, centerPoint.y, actualSize.zLength - 1),
        size: Size = Size.create(actualSize.xLength, actualSize.yLength)
    ): Boolean = findEmptyLocationWithin(offset, size).fold(
        whenEmpty = {
            false
        },
        whenPresent = { location ->
            addEntity(entity, location)
            true
        })

    fun removeEntity(entity: AnyGameEntity) {
        fetchBlockAt(entity.position).map {
            it.removeEntity(entity)
        }

        engine.removeEntity(entity)

        entity.position = Position3D.unknown()
    }

    fun transformEntity(from: AnyGameEntity, to: AnyGameEntity) {
        fetchBlockAt(from.position).map {
            it.removeEntity(from)
        }

        engine.removeEntity(from)
        addEntity(to, from.position)

        from.position = Position3D.unknown()
    }

    fun moveEntity(entity: AnyGameEntity, position: Position3D): Boolean {
        if (!actualSize.containsPosition(position)) {
            return false
        }

        if (hasBlockAt(entity.position) && hasBlockAt(position)) {
            val oldBlock = fetchBlockAt(entity.position).get()
            val newBlock = fetchBlockAt(position).get()

            if (!oldBlock.dirty && !newBlock.dirty) {
                oldBlock.dirty = true
                newBlock.dirty = true
                if (oldBlock.hasEntity(entity) && !(entity.occupiesBlock && newBlock.isOccupied)) {
                    if (oldBlock.removeEntity(entity)) {
                        entity.position = position
                        newBlock.addEntity(entity)

                        oldBlock.dirty = false
                        newBlock.dirty = false
                        return true
                    }
                }
            }
        }
        return false
    }

    fun update(screen: Screen, uiEvent: UIEvent, game: Game) {
        engine.update(
            GameContext(
                world = this,
                player = game.player,
                screen = screen,
                uiEvent = uiEvent
            )
        )
    }
}