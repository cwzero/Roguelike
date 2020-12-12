package com.liquidforte.roguelike.views.fragments

import com.liquidforte.roguelike.entities.attributes.Inventory
import com.liquidforte.roguelike.extensions.GameItem
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.Fragment
import org.hexworks.zircon.api.uievent.ComponentEventType.ACTIVATED
import org.hexworks.zircon.api.uievent.Processed
import org.hexworks.zircon.internal.Zircon
import org.hexworks.zircon.internal.component.impl.DefaultContainer

class InventoryFragment(inventory: Inventory,
                        width: Int,
                        onDrop: (GameItem) -> Unit) : Fragment {

    override val root = Components.vbox()           // 1
        .withSize(width, 11)
        .build().apply {
            val list = this
            addComponent(Components.hbox()      // 2
                .withSpacing(1)
                .withSize(width, 1)
                .build().apply {
                    addComponent(Components.label().withText("").withSize(1, 1))
                    addComponent(Components.header().withText("Name").withSize(NAME_COLUMN_WIDTH, 1))
                    addComponent(Components.header().withText("Actions").withSize(ACTIONS_COLUMN_WIDTH, 1))
                })
            inventory.items.forEach { item ->                           // 3
                addFragment(InventoryRowFragment(width, item).apply {
                    dropButton.onActivated {
                       //list.children.remove(this.root)
                       // list.removeComponent(this.root)                 // 5
                        onDrop(item)                                    // 6
                        Processed
                    }
                })
            }
        }

    companion object {
        const val NAME_COLUMN_WIDTH = 15
        const val ACTIONS_COLUMN_WIDTH = 10
    }
}