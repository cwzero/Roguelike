package com.liquidforte.roguelike.generator

import java.util.*

class VirtualMapTransactionBuilder<C : Cell> private constructor(
    private val view: VirtualMapView<C>,
    private val initializer: CellInitializer<C>
) {
    private val ops: Queue<VirtualMapTransactionOperation<C>> = LinkedList()

    fun operation(op: VirtualMapTransactionOperation<C>) {
        ops.add(op)
    }

    fun operator(op: VirtualMapTransactionOperator<C>) {
        ops.add(op(view))
    }

    fun build(): VirtualMapTransaction<C> {
        val map = VirtualMap.create(view, initializer)
        ops.forEach { it(map) }
        return map.toTransaction()
    }

    companion object {
        fun <C : Cell> create(initializer: CellInitializer<C>) = create(VirtualMapView.create(), initializer)

        fun <C : Cell> create(view: VirtualMapView<C>, initializer: CellInitializer<C>) =
            VirtualMapTransactionBuilder(view, initializer)
    }
}