package com.liquidforte.roguelike.views.fragments

import com.liquidforte.roguelike.config.GameConfig.PLAY_AREA_HEIGHT
import com.liquidforte.roguelike.config.GameConfig.PLAY_AREA_WIDTH
import com.liquidforte.roguelike.config.GameTiles
import com.liquidforte.roguelike.world.World
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.GameComponents
import org.hexworks.zircon.api.builder.component.PanelBuilder
import org.hexworks.zircon.api.component.Fragment
import org.hexworks.zircon.api.game.ProjectionMode
import org.hexworks.zircon.api.screen.Screen

class PlayArea(world: World, screen: Screen, builder: PanelBuilder.() -> PanelBuilder = { this }) : Fragment {
    override val root = builder(Components.panel())
            .withComponentRenderer(GameComponents.newGameAreaComponentRenderer(world, ProjectionMode.TOP_DOWN.toProperty(), GameTiles.FLOOR))
            .withSize(PLAY_AREA_WIDTH, PLAY_AREA_HEIGHT)
            .build()

}