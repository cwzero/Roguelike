package com.liquidforte.roguelike

import org.hexworks.zircon.api.CP437TilesetResources
import org.hexworks.zircon.api.ColorThemes
import org.hexworks.zircon.api.application.AppConfig
import org.hexworks.zircon.api.data.Size

object GameConfig {
    val TILESET = CP437TilesetResources.rexPaint16x16()
    val THEME = ColorThemes.afterglow()

    const val TITLE = "Liquid Forte Roguelike"
    const val WINDOW_WIDTH = 110
    const val WINDOW_HEIGHT = 60

    const val PLAY_AREA_RATIO = 0.8
    const val PLAY_AREA_HEIGHT_RATIO = PLAY_AREA_RATIO
    const val PLAY_AREA_WIDTH_RATIO = PLAY_AREA_RATIO
    const val PLAY_AREA_HEIGHT = (PLAY_AREA_HEIGHT_RATIO * WINDOW_HEIGHT).toInt()
    const val PLAY_AREA_WIDTH = (PLAY_AREA_WIDTH_RATIO * WINDOW_WIDTH).toInt()

    const val STATUS_AREA_HEIGHT = WINDOW_HEIGHT - PLAY_AREA_HEIGHT
    const val INFO_AREA_WIDTH = WINDOW_WIDTH - PLAY_AREA_WIDTH

    fun buildAppConfig() = AppConfig.newBuilder()
            .withDefaultTileset(TILESET)
            .withSize(Size.create(WINDOW_WIDTH, WINDOW_HEIGHT))
            .build()
}