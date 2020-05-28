package uk.co.poolefoundries.baldinggate.model

import com.badlogic.ashley.core.Component
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.badlogic.ashley.core.Entity
import uk.co.poolefoundries.baldinggate.*
import uk.co.poolefoundries.baldinggate.core.*
import java.io.File
import java.lang.RuntimeException



val behaviourMap = mapOf("player" to PlayerComponent, "skeleton" to SkeletonComponent)

data class Position(val x: Int, val y: Int)
data class Tile(val tileType: String, val position: Position)
data class Mob(val mobType: String, val position: Position)

data class MobType(val name: String, val texture: String, val stats: Stats, val behaviour: String) {
    fun toEntity(): Entity {
        val entity = Entity()
            .add(
                VisualComponent(
                    TextureRenderable(
                        Resources.get(
                            texture
                        )
                    )
                )
            )
        entity.add(ColorComponent())
        return entity
    }
}

data class TileType(val name: String, val texture: String, val passable: Boolean) {
    fun toEntity(): Entity {
        val entity = Entity()
            .add(
                VisualComponent(
                    TextureRenderable(
                        Resources.get(
                            texture
                        )
                    )
                )
            )
        if (passable){
            entity.add(FloorComponent)
        }
        else {
            entity.add(WallComponent)
        }
        entity.add(ColorComponent())
        return entity
    }
}

fun loadLevelJson(levelJson: String): Level {
    return jacksonObjectMapper().readValue(levelJson)
}

fun loadLevel(levelName: String): Level {
    return jacksonObjectMapper().readValue(File("levels/$levelName.json"))
}

fun Level.toEntities() = this.tiles.map {
    val type = this.tileTypes.find { type -> type.name == it.tileType }
        ?: throw RuntimeException("No tile type specified with name ${it.tileType}")

    type.toEntity()
        .add(PositionComponent(it.position.x, it.position.y))

} + this.mobs.map {
    val type = this.mobTypes.find { type -> type.name == it.mobType }
        ?: throw RuntimeException("No mob type specified with name ${it.mobType}")
    type.toEntity()
        .add(PositionComponent(it.position.x, it.position.y))
        .add(StatsComponent(type.stats))
        .add(behaviourMap.getValue(type.behaviour))

}
