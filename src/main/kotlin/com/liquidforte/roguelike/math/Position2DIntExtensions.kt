package com.liquidforte.roguelike.math

// TODO: Finish Moving

fun Position2D.withRelative(deltaX: Int = 0, deltaY: Int = 0) =
    Position2D.create(x + deltaX, y + deltaY)

fun Position2D.with(newX: Int = x, newY: Int = y) =
    Position2D.create(newX, newY)

fun Position2D.withRelativeX(deltaX: Int) =
    Position2D.create(x + deltaX, y)

fun Position2D.withX(newX: Int) =
    Position2D.create(newX, y)

fun Position2D.withRelativeY(deltaY: Int) =
    Position2D.create(x, y + deltaY)

fun Position2D.withY(newY: Int) =
    Position2D.create(x, newY)

fun Position2D.withY(pos: Position2D) =
    Position2D.create(x, pos.y)

fun Position2D.Companion.create(x: Int, y: Int) = Position2D.create(x, y)

fun Position2D.Companion.range(xRange: IntProgression, yRange: IntProgression) =
    sequence {
        yRange.forEach { y ->
            xRange.forEach { x ->
                yield(Position2D.create(x,y))
            }
        }
    }

fun Position2D.Companion.range(x: Int = 0, width: Int = 0, y: Int = 0, height: Int = 0) = range(x until x + width, y until y + height)