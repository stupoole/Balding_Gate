package uk.co.poolefoundries.baldinggate.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.badlogic.ashley.core.Entity
import uk.co.poolefoundries.baldinggate.PositionComponent
import uk.co.poolefoundries.baldinggate.Resources
import uk.co.poolefoundries.baldinggate.TextureRenderable
import uk.co.poolefoundries.baldinggate.VisualComponent
import java.io.File


data class Position(val x: Int, val y: Int)
data class TileType(val name: String, val texture: String, val passable: Boolean)
data class Tile(val tileType: String, val position: Position)
data class Level(val name: String, val tiles: List<Tile>, val tileTypes: List<TileType>)


fun loadLevelJson(levelJson: String) : Level {
    return jacksonObjectMapper().readValue(levelJson)
}

fun loadLevel(levelName: String) : Level {
    return jacksonObjectMapper().readValue(File("levels/$levelName.json"))
}

fun Level.toEntities() = this.tiles.map {
    val type = this.tileTypes.find { type -> type.name == it.tileType  }

    Entity()
        .add(VisualComponent(TextureRenderable(Resources.get(type!!.texture))))
        .add(PositionComponent(it.position.x, it.position.y))
}