package com.liquidforte.roguelike.views.fragments

import com.liquidforte.roguelike.GameConfig.PLAY_AREA_HEIGHT
import com.liquidforte.roguelike.GameConfig.PLAY_AREA_WIDTH
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.builder.component.PanelBuilder
import org.hexworks.zircon.api.component.Fragment
import org.hexworks.zircon.api.screen.Screen

class PlayArea(screen: Screen, builder: PanelBuilder.() -> PanelBuilder = { this }) : Fragment {
    override val root = builder(Components.panel())
            .withSize(PLAY_AREA_WIDTH, PLAY_AREA_HEIGHT)
            .build()

    init {

    }
}