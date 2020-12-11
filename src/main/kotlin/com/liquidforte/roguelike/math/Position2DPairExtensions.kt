package com.liquidforte.roguelike.math

fun Position2D.toPair() : Pair<Int, Int> = Pair(x, y)

val Position2D.asPair
    get() = toPair()

operator fun Position2D.rangeTo(pos: Pair<Int, Int>) = Position2D.range(this, Position2D.fromPair(pos))

fun Position2D.Companion.toPair(pos: Position2D) : Pair<Int, Int> = Pair(pos.x, pos.y)

fun Position2D.Companion.fromPair(pos: Pair<Int, Int>) : Position2D = Position2D.create(pos.first, pos.second)

fun Position2D.Companion.range(from: Pair<Int, Int>, to: Pair<Int, Int>) = Position2D.range(from.first..to.first, from.second..to.second)

fun Pair<Int, Int>.toPosition2D() : Position2D = Position2D.create(first, second)

val Pair<Int, Int>.asPosition2D : Position2D
    get() = toPosition2D()