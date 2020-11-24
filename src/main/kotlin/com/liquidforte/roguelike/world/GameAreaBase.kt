package com.liquidforte.roguelike.world

import com.liquidforte.roguelike.Vector3D
import com.liquidforte.roguelike.blocks.GameBlock
import com.liquidforte.roguelike.extensions.bounds
import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.zircon.api.builder.game.GameAreaBuilder
import org.hexworks.zircon.api.data.Position3D
import org.hexworks.zircon.api.data.Size3D
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.game.GameArea

open class GameAreaBase(visibleSize: Size3D, actualSize: Size3D)
    : GameArea<Tile, GameBlock> by GameAreaBuilder.newBuilder<Tile, GameBlock>()
        .withVisibleSize(visibleSize)
        .withActualSize(actualSize)
        .build() {
    fun setBlockAt(vec: Vector3D, block: GameBlock) {
        setBlockAt(bounds.getPosition(vec), block)
    }

    fun fetchBlockAt(vec: Vector3D): Maybe<GameBlock> {
        return fetchBlockAt(bounds.getPosition(vec))
    }
}