package com.liquidforte.roguelike.builders

import com.liquidforte.roguelike.config.GameConfig.WORLD_SIZE
import com.liquidforte.roguelike.world.World
import org.hexworks.zircon.api.data.Size3D

class WorldBuilder(private val worldSize: Size3D = WORLD_SIZE) {
    private val generator: WorldGenerator = RoomGenerator(worldSize)

    fun generate(): WorldBuilder = apply {
        generator.generate()
    }


    fun build(visibleSize: Size3D): World = generator.build(visibleSize)
}