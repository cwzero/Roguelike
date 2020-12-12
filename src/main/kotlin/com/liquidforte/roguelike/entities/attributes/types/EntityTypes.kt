package com.liquidforte.roguelike.entities.attributes.types

import org.hexworks.amethyst.api.base.BaseEntityType
import org.hexworks.amethyst.api.entity.EntityType

interface Door : EntityType

object Zircon : BaseEntityType(
        name = "Zircon",
        description = "A small piece of Zircon. Its value is unfathomable."), Item

object Bat : BaseEntityType(
        name = "bat"), Combatant

object Fungus : BaseEntityType(
        name = "fungus"), Combatant

object Player : BaseEntityType(
        name = "Player"), ItemHolder, Combatant

object Wall : BaseEntityType(
        name = "wall")

object OpenDoor : BaseEntityType(
        name = "Door"), Door

object ClosedDoor : BaseEntityType(
        name = "Door"), Door

object HiddenDoor : BaseEntityType(
        name = "Door"), Door

object StairsDown : BaseEntityType(
        name = "stairs down")

object StairsUp : BaseEntityType(
        name = "stairs up")