package com.liquidforte.roguelike.blocks

import com.liquidforte.roguelike.config.GameTiles
import com.liquidforte.roguelike.entities.attributes.types.ClosedDoor
import com.liquidforte.roguelike.entities.attributes.types.HiddenDoor
import com.liquidforte.roguelike.entities.attributes.types.OpenDoor
import com.liquidforte.roguelike.extensions.*
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentHashMapOf
import kotlinx.collections.immutable.toPersistentHashMap
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.zircon.api.data.BlockTileType
import org.hexworks.zircon.api.data.CharacterTile
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.data.base.BaseBlock

class GameBlock(private val currentEntities: MutableList<AnyGameEntity> = mutableListOf(), private val defaultTile: CharacterTile = GameTiles.FLOOR)
    : BaseBlock<Tile>(emptyTile = GameTiles.UNREVEALED, tiles = persistentHashMapOf()) {override var tiles: PersistentMap<BlockTileType, Tile>
    get() = if (render) {
        mutableMapOf<BlockTileType, Tile>().run {
            if (isOccupied && occupier.tile.isPresent) {
                occupier.tile.map {
                    memory = it
                    put(BlockTileType.TOP, it)
                }
            }

            if (currentEntities.isNotEmpty()) {
                currentEntities.map { it.tile }.firstOrNull()?.map {
                    memory = it
                    put(BlockTileType.CONTENT, it)
                }
            }

            if (memory == GameTiles.UNREVEALED || memory == GameTiles.PLAYER) {
                memory = GameTiles.EMPTY
            }

            put(BlockTileType.BOTTOM, defaultTile)

            this.toPersistentHashMap()
        }
    } else {
        persistentHashMapOf(BlockTileType.TOP to memory, BlockTileType.BOTTOM to defaultTile)
    }
    set(_) = Unit

    var render: Boolean = true

    private var memory: Tile = GameTiles.UNREVEALED

    var dirty: Boolean = false

    val isEmptyFloor: Boolean
        get() = currentEntities.isEmpty()

    val occupier: GameEntity<EntityType>
        get() = currentEntities.first { it.occupiesBlock }

    val isOccupied: Boolean
        get() = currentEntities.any { it.occupiesBlock }

    val entities: Iterable<GameEntity<EntityType>>
        get() = currentEntities.toList()

    val door: AnyGameEntity?
        get() = currentEntities.first { it.isDoor }

    val isDoor: Boolean
        get() = currentEntities.any { it.isDoor }

    val isWall: Boolean
        get() = currentEntities.any { it.isWall }

    val isRoughFloor: Boolean
        get() = !isWall && defaultTile == GameTiles.ROUGH_FLOOR

    val isRoughWall: Boolean
        get() = currentEntities.any { it.isRoughWall }

    val isSmoothWall: Boolean
        get() = isWall && !isRoughWall

    val isSmoothFloor: Boolean
        get() = !isWall && !isRoughFloor

    fun addEntity(entity: GameEntity<EntityType>): Boolean {
        return currentEntities.add(entity)
    }

    fun removeEntity(entity: GameEntity<EntityType>): Boolean {
        return currentEntities.remove(entity)
    }

    fun <T : EntityType> hasEntity(entity: GameEntity<T>): Boolean {
        return currentEntities.contains(entity)
    }
}