package com.liquidforte.roguelike.generator

import com.liquidforte.roguelike.math.Container2D
import com.liquidforte.roguelike.math.Position2D
import com.liquidforte.roguelike.math.toContainer
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableMap
import com.github.kittinunf.result.Result
import java.lang.RuntimeException

typealias ViewFilter<C> = (Position2D, C) -> Boolean

data class VirtualMapView<C : Cell>(val data: ImmutableMap<Position2D, C>) :
    Container2D by data.keys.toContainer(),
    VirtualMapTransactionTarget<C> {

    override fun applyTransaction(
        initializer: CellInitializer<C>,
        viewFilter: ViewFilter<C>,
        transaction: VirtualMapTransaction<C>
    ): Result<VirtualMapView<C>, RuntimeException> =
        VirtualMap.applyTransaction(this.toView(viewFilter), initializer, transaction)

    override fun toView(viewFilter: ViewFilter<C>): VirtualMapView<C> =
        create(data.filter { (k, v) -> viewFilter(k, v) })

    override fun equals(other: Any?): Boolean {
        if (other is VirtualMapView<*>) {
            return data.all { (pos, cell) ->
                other.data[pos] == cell
            }
        } else {
            return false
        }
    }

    companion object {
        fun <C : Cell> create(): VirtualMapView<C> = create(persistentMapOf())
        fun <C : Cell> create(data: Map<Position2D, C>): VirtualMapView<C> = create(data.toImmutableMap())
        fun <C : Cell> create(data: ImmutableMap<Position2D, C>): VirtualMapView<C> = VirtualMapView(data)

        fun <C : Cell> create(initializer: CellInitializer<C>, pos: Iterable<Position2D>) =
            create(mutableMapOf<Position2D, Cell>().apply {
                pos.forEach {
                    this[it] = initializer(it)
                }
            }.toImmutableMap())
    }
}