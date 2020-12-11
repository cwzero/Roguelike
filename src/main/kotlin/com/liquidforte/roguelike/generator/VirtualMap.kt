package com.liquidforte.roguelike.generator

import com.liquidforte.roguelike.math.Container2D
import com.liquidforte.roguelike.math.Position2D
import kotlinx.collections.immutable.toImmutableMap
import java.lang.RuntimeException

import com.github.kittinunf.result.Result

typealias VirtualMapTransactionOperation<C> = VirtualMapHandle<C>.() -> Unit
typealias VirtualMapTransactionOperator<C> = (VirtualMapView<C>) -> VirtualMapTransactionOperation<C>

class VirtualMap<C : Cell> private constructor(
    private val view: VirtualMapView<C>,
    private val initializer: CellInitializer<C>
) : VirtualMapHandle<C>, Container2D by view {
    private val mutations: MutableMap<Position2D, CellMutation<C>> = mutableMapOf()
    private var valid = true

    override fun mutate(pos: Position2D, mutation: CellMutation<C>) {
        if (pos !in mutations || mutations[pos] == null) {
            mutations[pos] = mutation
        } else {
            val current = mutations[pos]!!

            if (current is CompositeCellMutation<C>) {
                current.add(mutation)
            } else {
                mutations[pos] = CompositeCellMutation(current, mutation)
            }
        }
    }

    override fun toView(viewFilter: ViewFilter<C>): VirtualMapView<C> {
        val map: MutableMap<Position2D, C> = view.data.toMutableMap()

        mutations.forEach { (key, value) ->
            if (key !in view.data) {
                map[key] = initializer(key)
            } else {
                map[key] = view.data[key]!!
            }
            value.invoke(map[key]!!)
        }

        return VirtualMapView.create(map.filter { (key, value) -> viewFilter(key, value) }.toImmutableMap())
    }

    override fun applyTransaction(
        initializer: CellInitializer<C>,
        viewFilter: ViewFilter<C>,
        transaction: VirtualMapTransaction<C>
    ): Result<VirtualMapView<C>, RuntimeException> =
        run {
            if (!transaction.valid) {
                return Result.error(RuntimeException("Transaction is invalid!"))
            } else if (transaction.isValid(toView(viewFilter))) {
                transaction.data.forEach { (pos, mutation) -> mutate(pos, mutation) }
                if (!valid || !transaction.valid) {
                    return Result.error(RuntimeException("Transaction is invalid!"))
                }
                return Result.success(toView())
            } else {
                return Result.error(RuntimeException("Tried to apply an existing transaction to an invalid view!"))
            }
        }

    override fun mutate(filter: (Position2D, C) -> Boolean, mutation: CellMutation<C>) =
        view.data.filter { (key: Position2D, value: C) -> filter(key, value) }.keys
            .forEach { mutate(it, mutation) }

    fun toTransaction() : VirtualMapTransaction<C> {
        val data = view.data.toMutableMap()
        mutations.forEach { (pos, mutation) ->
            if (data[pos] == null) {
                data[pos] = initializer(pos)
            }
            mutation(data[pos]!!)
        }
        val result = VirtualMapTransaction(view, mutations.toImmutableMap())
        result.valid = valid
        return result
    }

    companion object {
        fun <C : Cell> create(initializer: CellInitializer<C>) =
            create(VirtualMapView.create(), initializer)

        fun <C : Cell> create(view: VirtualMapView<C> = VirtualMapView.create(), initializer: CellInitializer<C>) =
            VirtualMap(view, initializer)

        fun <C : Cell> applyTransaction(
            initializer: CellInitializer<C>,
            buildFunction: VirtualMapTransactionBuilder<C>.() -> Unit
        ) : Result<VirtualMapView<C>, RuntimeException> =
            applyTransaction(VirtualMapView.create(), initializer, buildFunction)

        fun <C : Cell> applyTransaction(
            view: VirtualMapView<C> = VirtualMapView.create(),
            initializer: CellInitializer<C>,
            buildFunction: VirtualMapTransactionBuilder<C>.() -> Unit
        ) : Result<VirtualMapView<C>, RuntimeException> =
            create(view, initializer).applyTransaction(initializer, buildFunction)

        fun <C : Cell> applyTransaction(
            view: VirtualMapView<C> = VirtualMapView.create(),
            initializer: CellInitializer<C>,
            transaction: VirtualMapTransaction<C>
        ) : Result<VirtualMapView<C>, RuntimeException> =
            create(view, initializer).applyTransaction(initializer, transaction)
    }

    override fun invalidate() {
        valid = false
    }
}