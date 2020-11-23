package com.liquidforte.roguelike.views

import com.liquidforte.roguelike.GameConfig.THEME
import com.liquidforte.roguelike.views.fragments.InfoArea
import com.liquidforte.roguelike.views.fragments.PlayArea
import com.liquidforte.roguelike.views.fragments.StatusArea
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.view.base.BaseView

class PlayView(private val grid: TileGrid)
    : BaseView(grid, THEME) {
    override fun onDock() {
        val infoArea = InfoArea(screen)
        val playArea = PlayArea(screen)
        val statusArea = StatusArea(screen)

        screen.addFragment(infoArea)
        screen.addFragment(playArea)
        screen.addFragment(statusArea)
    }
}