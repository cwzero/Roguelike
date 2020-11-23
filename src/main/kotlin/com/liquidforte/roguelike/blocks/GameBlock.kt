package com.liquidforte.roguelike.blocks

import com.liquidforte.roguelike.config.GameTiles
import com.liquidforte.roguelike.extensions.GameEntity
import com.liquidforte.roguelike.extensions.occupiesBlock
import com.liquidforte.roguelike.extensions.tile
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentHashMapOf
import kotlinx.collections.immutable.toPersistentHashMap
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.zircon.api.data.BlockTileType
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.data.base.BaseBlock

class GameBlock(private val currentEntities: MutableList<GameEntity<EntityType>> = mutableListOf())
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

            this.toPersistentHashMap()
        }
    } else {
        persistentHashMapOf(BlockTileType.TOP to memory)
    }
    set(_) = Unit

    var render: Boolean = false

    private var memory: Tile = GameTiles.UNREVEALED

    val isEmptyFloor: Boolean
        get() = currentEntities.isEmpty()

    val occupier: GameEntity<EntityType>
        get() = currentEntities.first { it.occupiesBlock }

    val isOccupied: Boolean
        get() = currentEntities.any { it.occupiesBlock }

    val entities: Iterable<GameEntity<EntityType>>
        get() = currentEntities.toList()

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