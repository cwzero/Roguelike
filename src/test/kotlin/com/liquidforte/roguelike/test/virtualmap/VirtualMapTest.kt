package com.liquidforte.roguelike.test.virtualmap

import com.github.kittinunf.result.success
import com.liquidforte.roguelike.generator.*
import com.liquidforte.roguelike.math.Position2D
import com.liquidforte.roguelike.math.withRelative
import com.liquidforte.roguelike.math.withX
import com.liquidforte.roguelike.math.withY
import org.junit.jupiter.api.Test
import kotlin.random.Random

data class TestCell(val pos: Position2D) : Cell

class VirtualMapTest {
    @Test
    fun test2() {
        val lPos = Position2D.create(0, 0)
        val rPos = Position2D.create(5, 5)

        val s = Random.nextFloat() < 0.5f
        val intermediate = if (s) {
            rPos.withY(lPos.y)
        } else {
            rPos.withX(lPos.x)
        }

        val lPath = lPos..intermediate
        val rPath = intermediate..rPos

        println("lPath: ${lPos in lPath}")
        println("rPath: ${rPos in rPath}")
    }

    @Test
    fun test() {
        val initializer = ::TestCell

        var view = VirtualMapView.create<TestCell>()

        var viewResult = VirtualMap.applyTransaction(view, initializer) {
            operation {
                mutate(Position2D.origin()) {

                }
            }
        }

        viewResult.success {
            view = it
            viewResult = it.applyTransaction(initializer) {
                operation {
                    mutate(Position2D.origin().withRelative(1, 1)) {
                        invalidate()
                    }
                }
            }
        }

        viewResult.fold(success = {
            println("Success!: ${it.data}")
        }, failure =
        {
            println("Failure!: $it")
            println(view.data)
        })
    }
}