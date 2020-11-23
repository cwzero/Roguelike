package com.liquidforte.roguelike.entities.attributes

import com.liquidforte.roguelike.extensions.GameItem
import org.hexworks.amethyst.api.Attribute
import org.hexworks.cobalt.core.api.UUID
import org.hexworks.cobalt.datatypes.Maybe

class Inventory : Attribute {
    private val currentItems = mutableListOf<GameItem>()

    val items: List<GameItem>
        get() = currentItems.toList()

    val isEmpty: Boolean
        get() = currentItems.isEmpty()

    fun findItemBy(id: UUID): Maybe<GameItem> {
        return Maybe.ofNullable(items.firstOrNull { it.id == id })
    }

    fun addItem(item: GameItem): Boolean {
        return currentItems.add(item)
    }

    fun removeItem(entity: GameItem): Boolean {
        return currentItems.remove(entity)
    }
}
