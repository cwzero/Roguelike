package com.liquidforte.roguelike.views.fragments

import com.liquidforte.roguelike.GameConfig.PLAY_AREA_WIDTH
import com.liquidforte.roguelike.GameConfig.STATUS_AREA_HEIGHT
import org.hexworks.zircon.api.ComponentDecorations
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.builder.component.PanelBuilder
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.component.Fragment
import org.hexworks.zircon.api.screen.Screen

class StatusArea(screen: Screen, builder: PanelBuilder.() -> PanelBuilder = { this }) : Fragment {
    override val root = builder(Components.panel())
            .withSize(PLAY_AREA_WIDTH, STATUS_AREA_HEIGHT)
            .withAlignmentWithin(screen, ComponentAlignment.BOTTOM_LEFT)
            .withDecorations(ComponentDecorations.box())
            .build()

    init {

    }
}