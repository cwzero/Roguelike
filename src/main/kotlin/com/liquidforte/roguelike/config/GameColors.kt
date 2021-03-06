package com.liquidforte.roguelike.config

import org.hexworks.zircon.api.color.TileColor

object GameColors {
    val DOOR_COLOR = TileColor.fromString("#C93030")
    val FLOOR_FOREGROUND = TileColor.fromString("#75715E")
    val FLOOR_BACKGROUND = TileColor.fromString("#1e2320")

    val ACCENT_COLOR = TileColor.fromString("#FFCD22")
    val UNREVEALED_COLOR = TileColor.fromString("#000000")

    val WALL_FOREGROUND = TileColor.fromString("#75715E")
    val WALL_BACKGROUND = FLOOR_BACKGROUND

    val FUNGUS_COLOR = TileColor.fromString("#85DD1B")
    val BAT_COLOR = TileColor.fromString("#2348b2")

    val ZIRCON_COLOR = TileColor.fromString("#dddddd")
}