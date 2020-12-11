package com.liquidforte.roguelike.config

import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.graphics.Symbols

object GameTiles {
    val EMPTY = Tile.empty()

    val FLOOR = Tile.newBuilder()
        .withCharacter(Symbols.INTERPUNCT)
        .withForegroundColor(GameColors.FLOOR_FOREGROUND)
        .withBackgroundColor(GameColors.FLOOR_BACKGROUND)
        .buildCharacterTile()

    val ROUGH_FLOOR = Tile.newBuilder()
        .withCharacter(Symbols.BLOCK_SPARSE)
        .withForegroundColor(GameColors.FLOOR_FOREGROUND)
        .withBackgroundColor(GameColors.FLOOR_BACKGROUND)
        .buildCharacterTile()

    val PLAYER = Tile.newBuilder()
            .withCharacter('@')
            .withBackgroundColor(GameColors.FLOOR_BACKGROUND)
            .withForegroundColor(GameColors.ACCENT_COLOR)
            .buildCharacterTile()

    val UNREVEALED = Tile.newBuilder()
            .withCharacter(' ')
            .withBackgroundColor(GameColors.UNREVEALED_COLOR)
            .buildCharacterTile()

    private val wallBuilder
        get() = Tile.newBuilder()
            .withForegroundColor(GameColors.WALL_FOREGROUND)
            .withBackgroundColor(GameColors.WALL_BACKGROUND)

    private fun wall(char: Char) = wallBuilder.withCharacter(char).buildCharacterTile()

    val WALL_UPPER_LEFT_CORNER = wall(Symbols.SINGLE_LINE_TOP_LEFT_CORNER)
    val WALL_UPPER_RIGHT_CORNER = wall(Symbols.SINGLE_LINE_TOP_RIGHT_CORNER)
    val WALL_LOWER_LEFT_CORNER = wall(Symbols.SINGLE_LINE_BOTTOM_LEFT_CORNER)
    val WALL_LOWER_RIGHT_CORNER = wall(Symbols.SINGLE_LINE_BOTTOM_RIGHT_CORNER)
    val WALL_HORIZONTAL = wall(Symbols.SINGLE_LINE_HORIZONTAL)
    val WALL_VERTICAL = wall(Symbols.SINGLE_LINE_VERTICAL)

    val STAIRS_UP = Tile.newBuilder()
            .withCharacter('<')
            .withForegroundColor(GameColors.ACCENT_COLOR)
            .withBackgroundColor(GameColors.FLOOR_BACKGROUND)
            .buildCharacterTile()

    val STAIRS_DOWN = Tile.newBuilder()
            .withCharacter('>')
            .withForegroundColor(GameColors.ACCENT_COLOR)
            .withBackgroundColor(GameColors.FLOOR_BACKGROUND)
            .buildCharacterTile()

    val CLOSED_DOOR = Tile.newBuilder()
        .withCharacter('+')
        .withForegroundColor(GameColors.DOOR_COLOR)
        .withBackgroundColor(GameColors.FLOOR_BACKGROUND)
        .buildCharacterTile()

    val OPEN_DOOR = Tile.newBuilder()
        .withCharacter(Symbols.INTERPUNCT)
        .withForegroundColor(GameColors.DOOR_COLOR)
        .withBackgroundColor(GameColors.FLOOR_BACKGROUND)
        .buildCharacterTile()
}