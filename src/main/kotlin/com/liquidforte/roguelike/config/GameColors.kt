package com.liquidforte.roguelike.config

import org.hexworks.zircon.api.color.TileColor

object GameColors {
    val FLOOR_FOREGROUND = TileColor.fromString("#75715E")
    val FLOOR_BACKGROUND = TileColor.fromString("#1e2320")

    val ACCENT_COLOR = TileColor.fromString("#FFCD22")
    val UNREVEALED_COLOR = TileColor.fromString("#000000")

    val WALL_FOREGROUND = TileColor.fromString("#75715E")
    val WALL_BACKGROUND = TileColor.fromString("#3E3D32")
}