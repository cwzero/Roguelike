package com.liquidforte.roguelike.entities.behaviors

import com.liquidforte.roguelike.entities.commands.MoveDown
import com.liquidforte.roguelike.entities.commands.MoveTo
import com.liquidforte.roguelike.entities.commands.MoveUp
import com.liquidforte.roguelike.entities.attributes.types.Player
import com.liquidforte.roguelike.entities.commands.PickItemUp
import com.liquidforte.roguelike.extensions.GameEntity
import com.liquidforte.roguelike.extensions.position
import com.liquidforte.roguelike.game.GameContext
import org.hexworks.amethyst.api.base.BaseBehavior
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.zircon.api.data.Position3D
import org.hexworks.zircon.api.uievent.KeyCode
import org.hexworks.zircon.api.uievent.KeyboardEvent

object InputReceiver : BaseBehavior<GameContext>() {
    override suspend fun update(entity: GameEntity<out EntityType>, context: GameContext): Boolean {
        val (world, player, screen, uiEvent) = context
        val currentPos = player.position
        if (uiEvent is KeyboardEvent) {
            when (uiEvent.code) {
                KeyCode.KEY_W -> player.moveTo(currentPos.withRelativeY(-1), context)
                KeyCode.KEY_A -> player.moveTo(currentPos.withRelativeX(-1), context)
                KeyCode.KEY_S -> player.moveTo(currentPos.withRelativeY(1), context)
                KeyCode.KEY_D -> player.moveTo(currentPos.withRelativeX(1), context)
                KeyCode.KEY_R -> player.moveUp(context)
                KeyCode.KEY_F -> player.moveDown(context)
                KeyCode.KEY_P -> player.pickItemUp(currentPos, context)
            }
        }
        return true
    }

    private suspend fun GameEntity<Player>.pickItemUp(position: Position3D, context: GameContext) {     // 2
        executeCommand(PickItemUp(context, this, position))
    }

    private suspend fun GameEntity<Player>.moveTo(position: Position3D, context: GameContext) {
        executeCommand(MoveTo(context, this, position))
    }

    private suspend fun GameEntity<Player>.moveUp(context: GameContext) {
        executeCommand(MoveUp(context, this))
    }

    private suspend fun GameEntity<Player>.moveDown(context: GameContext) {
        executeCommand(MoveDown(context, this))
    }
}