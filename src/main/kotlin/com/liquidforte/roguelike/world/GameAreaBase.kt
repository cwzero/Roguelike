package com.liquidforte.roguelike.world

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
    override fun setBlockAt(position: Position3D, block: GameBlock) {
        if (bounds.contains(position))
            setBlockAt(bounds.relative(position), block)
    }

    override fun fetchBlockAt(position: Position3D): Maybe<GameBlock> {
        if (bounds.contains(position))
            return fetchBlockAt(bounds.relative(position))
        else
            throw RuntimeException("access block outside bounds")
    }
}