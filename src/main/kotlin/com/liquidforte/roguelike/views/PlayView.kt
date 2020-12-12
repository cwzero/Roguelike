package com.liquidforte.roguelike.views

import com.liquidforte.roguelike.builders.GameBuilder
import com.liquidforte.roguelike.config.GameConfig
import com.liquidforte.roguelike.config.GameConfig.THEME
import com.liquidforte.roguelike.events.GameLogEvent
import com.liquidforte.roguelike.game.Game
import com.liquidforte.roguelike.views.fragments.InfoArea
import com.liquidforte.roguelike.views.fragments.PlayArea
import com.liquidforte.roguelike.views.fragments.StatusArea
import org.hexworks.cobalt.events.api.EventScope
import org.hexworks.zircon.api.ComponentDecorations
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.uievent.ComponentEvent
import org.hexworks.zircon.api.uievent.ComponentEventType
import org.hexworks.zircon.api.uievent.KeyboardEventType
import org.hexworks.zircon.api.uievent.Processed
import org.hexworks.zircon.api.view.base.BaseView
import org.hexworks.zircon.internal.Zircon

class PlayView(grid: TileGrid, private val game: Game = GameBuilder.defaultGame())
    : BaseView(grid, THEME) {
    override fun onDock() {
        val infoArea = InfoArea(screen)
        val playArea = PlayArea(game.world, screen)
        val statusArea = StatusArea(screen)

        val logArea = Components.logArea()
            .withSize(GameConfig.INFO_AREA_WIDTH, GameConfig.WINDOW_HEIGHT)
            .withAlignmentWithin(screen, ComponentAlignment.TOP_RIGHT)
            .withDecorations(ComponentDecorations.box())
            .build()

        screen.addComponent(logArea)
        // TODO: Log area

        //screen.addFragment(infoArea)
        screen.addFragment(playArea)
        screen.addFragment(statusArea)

        screen.handleKeyboardEvents(KeyboardEventType.KEY_PRESSED) { event, _ ->
            game.world.update(screen, event, game)
            Processed
        }

        game.world.update(screen, ComponentEvent(ComponentEventType.ACTIVATED), game)
    }
}