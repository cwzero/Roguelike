package com.liquidforte.roguelike

import com.liquidforte.roguelike.config.GameConfig
import com.liquidforte.roguelike.views.StartView
import org.hexworks.zircon.api.SwingApplications

fun main() {
    val grid = SwingApplications.startTileGrid(GameConfig.buildAppConfig())
    grid.dock(StartView(grid))
}