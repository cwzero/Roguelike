package com.liquidforte.roguelike.generator

import com.liquidforte.roguelike.math.Position2D
import kotlinx.collections.immutable.ImmutableMap
import java.lang.RuntimeException
import com.github.kittinunf.result.Result

open class VirtualMapTransaction<C : Cell>(
    val view: VirtualMapView<C>,
    val data: ImmutableMap<Position2D, CellMutation<C>>,
    val check: (VirtualMapView<C>) -> Boolean = { it == view }
) {
    var valid = true
    open fun isValid(v: VirtualMapView<C>): Boolean = valid && check(v)
}

interface VirtualMapTransactionTarget<C : Cell> {
    fun transactionBuilder(
        initializer: CellInitializer<C>,
        buildFunction: VirtualMapTransactionBuilder<C>.() -> Unit
    ): VirtualMapTransactionBuilder<C> =
        transactionBuilder(initializer, { _, _ -> true }, buildFunction)

    fun transactionBuilder(
        initializer: CellInitializer<C>,
        viewFilter: ViewFilter<C>,
        buildFunction: VirtualMapTransactionBuilder<C>.() -> Unit
    ): VirtualMapTransactionBuilder<C> =
        VirtualMapTransactionBuilder.create(toView(viewFilter), initializer).apply(buildFunction)

    fun buildTransaction(
        initializer: CellInitializer<C>,
        buildFunction: VirtualMapTransactionBuilder<C>.() -> Unit,
    ): VirtualMapTransaction<C> =
        buildTransaction(initializer, { _, _ -> true }, buildFunction)

    fun buildTransaction(
        initializer: CellInitializer<C>,
        viewFilter: ViewFilter<C>,
        buildFunction: VirtualMapTransactionBuilder<C>.() -> Unit,
    ): VirtualMapTransaction<C> =
        transactionBuilder(initializer, viewFilter, buildFunction).build()

    fun applyTransaction(
        initializer: CellInitializer<C>,
        buildFunction: VirtualMapTransactionBuilder<C>.() -> Unit
    ): Result<VirtualMapView<C>, RuntimeException> =
        applyTransaction(initializer, { _, _ -> true }, buildFunction)

    fun applyTransaction(
        initializer: CellInitializer<C>,
        viewFilter: ViewFilter<C>,
        buildFunction: VirtualMapTransactionBuilder<C>.() -> Unit
    ): Result<VirtualMapView<C>, RuntimeException> =
        applyTransaction(
            initializer,
            viewFilter,
            buildTransaction(initializer, viewFilter, buildFunction)
        )

    fun applyTransaction(
        initializer: CellInitializer<C>,
        transaction: VirtualMapTransaction<C>
    ): Result<VirtualMapView<C>, RuntimeException> =
        applyTransaction(initializer, { _, _ -> true }, transaction)

    fun applyTransaction(
        initializer: CellInitializer<C>,
        viewFilter: ViewFilter<C>,
        transaction: VirtualMapTransaction<C>
    ): com.github.kittinunf.result.Result<VirtualMapView<C>, RuntimeException>

    fun toView(viewFilter: ViewFilter<C> = { _, _ -> true }): VirtualMapView<C>
}