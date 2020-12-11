package com.liquidforte.roguelike.generator

import com.liquidforte.roguelike.math.Position2D

data class DijkstraCell private constructor(var value: Int = Int.MAX_VALUE) : Cell {
    companion object {
        fun create(pos: Position2D) = DijkstraCell()
    }
}