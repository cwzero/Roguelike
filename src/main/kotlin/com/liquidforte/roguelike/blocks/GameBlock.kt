package com.liquidforte.roguelike.blocks

import com.liquidforte.roguelike.config.GameTiles
import com.liquidforte.roguelike.entities.attributes.EntityTile
import com.liquidforte.roguelike.entities.attributes.types.ClosedDoor
import com.liquidforte.roguelike.entities.attributes.types.HiddenDoor
import com.liquidforte.roguelike.entities.attributes.types.OpenDoor
import com.liquidforte.roguelike.entities.attributes.types.StairsUp
import com.liquidforte.roguelike.extensions.*
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentHashMapOf
import kotlinx.collections.immutable.toPersistentHashMap
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.zircon.api.data.BlockTileType
import org.hexworks.zircon.api.data.CharacterTile
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.data.base.BaseBlock

class GameBlock(
    private val currentEntities: MutableList<AnyGameEntity> = mutableListOf(),
    private val defaultTile: CharacterTile = GameTiles.FLOOR
) : BaseBlock<Tile>(emptyTile = defaultTile, tiles = persistentHashMapOf()) {
    override var tiles: PersistentMap<BlockTileType, Tile>
        get() = mutableMapOf<BlockTileType, Tile>().apply {
            if (render) {
                occupier?.tile?.map {
                    memory = it
                    put(BlockTileType.TOP, it)
                }

                currentEntities.firstOrNull()?.tile?.map {
                    memory = it
                    put(BlockTileType.CONTENT, it)
                }

                if (memory == GameTiles.UNREVEALED || memory == GameTiles.PLAYER) {
                    memory = defaultTile
                }
            } else {
                put(BlockTileType.CONTENT, memory)
            }

            put(BlockTileType.BOTTOM, defaultTile)

            BlockTileType.values().forEach {
                if (it !in this) {
                    put(it, Tile.empty())
                }
            }
        }.toPersistentHashMap()
        set(_) = Unit

    var render: Boolean = false

    private var memory: Tile = GameTiles.UNREVEALED

    var dirty: Boolean = false

    val isStairsUp: Boolean
        get() = currentEntities.any { it.type == StairsUp }

    val isEmptyFloor: Boolean
        get() = currentEntities.isEmpty()

    val occupier: GameEntity<EntityType>?
        get() = currentEntities.firstOrNull { it.occupiesBlock }

    val isOccupied: Boolean
        get() = currentEntities.isNotEmpty() && currentEntities.any { it.occupiesBlock }

    val entities: Iterable<GameEntity<EntityType>>
        get() = currentEntities.toList()

    val door: AnyGameEntity?
        get() = currentEntities.firstOrNull { it.isDoor }

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