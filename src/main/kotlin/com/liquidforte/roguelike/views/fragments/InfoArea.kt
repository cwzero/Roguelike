package com.liquidforte.roguelike.views.fragments

import com.liquidforte.roguelike.config.GameConfig.INFO_AREA_WIDTH
import com.liquidforte.roguelike.config.GameConfig.WINDOW_HEIGHT
import org.hexworks.zircon.api.ComponentDecorations
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.builder.component.PanelBuilder
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.component.Fragment
import org.hexworks.zircon.api.screen.Screen

class InfoArea(screen: Screen, builder: PanelBuilder.() -> PanelBuilder = { this }) : Fragment {
    override val root = builder(Components.panel())
            .withSize(INFO_AREA_WIDTH, WINDOW_HEIGHT)
            .withAlignmentWithin(screen, ComponentAlignment.TOP_RIGHT)
            .withDecorations(ComponentDecorations.box())
            .build()

}