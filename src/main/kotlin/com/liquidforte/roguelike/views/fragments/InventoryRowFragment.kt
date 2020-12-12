package com.liquidforte.roguelike.views.fragments

import com.liquidforte.roguelike.extensions.GameItem
import com.liquidforte.roguelike.extensions.iconTile
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.Fragment
import org.hexworks.zircon.api.graphics.Symbols

class InventoryRowFragment(width: Int, item: GameItem) : Fragment {
    val dropButton = Components.button()
        .withText("${Symbols.ARROW_DOWN}")
        .build()

    override val root = Components.hbox()
        .withSpacing(1)                                     // 5
        .withSize(width, 1)
        .build().apply {
            addComponent(Components.icon()                  // 6
                .withIcon(item.iconTile))
            addComponent(Components.label()
                .withSize(InventoryFragment.NAME_COLUMN_WIDTH, 1)   // 7
                .withText(item.name))
            addComponent(dropButton)
        }
}