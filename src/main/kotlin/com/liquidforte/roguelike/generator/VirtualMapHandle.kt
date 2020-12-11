package com.liquidforte.roguelike.generator

import com.liquidforte.roguelike.math.Position2D

interface VirtualMapHandle<C : Cell> : VirtualMapTransactionTarget<C> {
    fun mutate(pos: Position2D, mutation: CellMutation<C>)

    fun mutate(filter: (Position2D, C) -> Boolean, mutation: CellMutation<C>)

    fun mutate(pos: Iterable<Position2D>, mutation: CellMutation<C>) = pos.forEach { mutate(it, mutation) }

    fun mutate(x: Int, y: Int, mutation: CellMutation<C>) = mutate(Position2D.create(x, y), mutation)

    fun operation(op: VirtualMapTransactionOperation<C>) = this.op()

    fun invalidate()
}