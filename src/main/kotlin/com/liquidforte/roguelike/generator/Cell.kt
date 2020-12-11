package com.liquidforte.roguelike.generator

import com.liquidforte.roguelike.math.Position2D
import java.util.*

typealias CellInitializer<C> = (Position2D) -> C

typealias CellMutation<C> = (C) -> Unit

typealias MutationFilter<C> = (CellMutation<C>) -> Boolean

typealias CellFilter<C> = (C) -> Boolean

class CompositeCellMutation<C: Cell>(vararg mutation: CellMutation<C>) : (C) -> Unit {
    private val mutations: Stack<CellMutation<C>> = Stack()

    init {
        add(*mutation)
    }

    fun add(vararg mutation: CellMutation<C>) {
        mutation.forEach { mutations.push(it) }
    }

    override fun invoke(cell: C) {
        mutations.forEach { it(cell) }
    }
}

interface Cell