package com.liquidforte.roguelike.entities.attributes.types

import org.hexworks.amethyst.api.base.BaseEntityType

object Player : BaseEntityType(
        name = "Player"), ItemHolder

object Wall : BaseEntityType(
        name = "wall")

object StairsDown : BaseEntityType(
        name = "stairs down")

object StairsUp : BaseEntityType(
        name = "stairs up")