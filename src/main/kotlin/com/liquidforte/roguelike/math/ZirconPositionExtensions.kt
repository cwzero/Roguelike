package com.liquidforte.roguelike.math

import org.hexworks.zircon.api.data.Position

operator fun Position2D.rangeTo(pos: Position) = Position2D.range(this, Position2D.fromZircon(pos))

fun Position2D.toZircon() : Position = Position.create(x, y)

fun Position2D.Companion.toZircon(pos: Position2D) : Position = Position.create(pos.x, pos.y)

fun Position2D.Companion.fromZircon(pos: Position) : Position2D  = Position2D.create(pos.x, pos.y)

fun Position.toPosition2D() : Position2D = Position2D.create(x, y)

val Position.asPosition2D : Position2D
    get() = toPosition2D()

val Position2D.asZircon
    get() = toZircon()