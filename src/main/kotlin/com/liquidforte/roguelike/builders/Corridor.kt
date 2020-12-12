package com.liquidforte.roguelike.builders

import com.liquidforte.roguelike.math.Position2D

data class Corridor(val origin: Position2D, val originAxis: Position2D, val destination: Position2D, val destinationAxis: Position2D)