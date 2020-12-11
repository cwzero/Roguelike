package com.liquidforte.roguelike.entities.facets

import com.liquidforte.roguelike.entities.commands.MoveCamera
import com.liquidforte.roguelike.extensions.GameCommand
import com.liquidforte.roguelike.extensions.position
import com.liquidforte.roguelike.game.GameContext
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.amethyst.api.entity.EntityType

object CameraMover : BaseFacet<GameContext>() {
    override suspend fun executeCommand(command: GameCommand<out EntityType>) =
        command.responseWhenCommandIs(MoveCamera::class) { cmd ->
            val (context, source, previousPosition) = cmd
            val (world, player, screen, uiEvent) = context

            val screenPos = source.position - world.visibleOffset
            val halfHeight = world.visibleSize.yLength / 2
            val halfWidth = world.visibleSize.xLength / 2
            val currentPosition = source.position

            when {
                (previousPosition.y > currentPosition.y && screenPos.y < halfHeight) -> {
                    world.scrollBy(deltaY = -1)
                    //world.scrollOneBackward()
                }
                (previousPosition.y < currentPosition.y && screenPos.y > halfHeight) -> {
                    world.scrollBy(deltaY = 1)
                    //world.scrollOneForward()
                }
                (previousPosition.x > currentPosition.x && screenPos.x < halfWidth) -> {
                    world.scrollBy(deltaX = -1)
                    //world.scrollOneLeft()
                }
                (previousPosition.x < currentPosition.x && screenPos.x > halfWidth) -> {
                    world.scrollBy(deltaX = 1)
                    //world.scrollOneRight()
                }
            }

            Consumed
        }
}