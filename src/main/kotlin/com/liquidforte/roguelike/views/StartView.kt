package com.liquidforte.roguelike.views

import com.liquidforte.roguelike.GameConfig.THEME
import org.hexworks.zircon.api.ComponentDecorations
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.uievent.ComponentEventType
import org.hexworks.zircon.api.uievent.Processed
import org.hexworks.zircon.api.view.base.BaseView

class StartView(private val grid: TileGrid)
    : BaseView(grid, THEME) {
    override fun onDock() {
        val msg = "Welcome to Liquid Forte Roguelike"
        val header = Components.textBox(msg.length)
                .addHeader(msg)
                .addNewLine()
                .withAlignmentWithin(screen, ComponentAlignment.CENTER)
                .build()

        val startButton = Components.button()
                .withAlignmentAround(header, ComponentAlignment.BOTTOM_CENTER)
                .withText("Start!")
                .withDecorations(ComponentDecorations.box())
                .build()

        startButton.handleComponentEvents(ComponentEventType.ACTIVATED) {
            replaceWith(PlayView(grid))
            Processed
        }

        screen.addComponent(header)
        screen.addComponent(startButton)
    }
}