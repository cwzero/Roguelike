package com.liquidforte.roguelike.blocks

import com.liquidforte.roguelike.entities.Entities

object GameBlocks {
    fun floor() = GameBlock()

    fun wall() = GameBlock().apply { addEntity(Entities.newWall()) }

    fun stairsDown() = GameBlock().apply { addEntity(Entities.newStairsDown()) }

    fun stairsUp() = GameBlock().apply { addEntity(Entities.newStairsUp()) }
}