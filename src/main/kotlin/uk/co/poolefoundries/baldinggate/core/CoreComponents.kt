package uk.co.poolefoundries.baldinggate.core

import com.badlogic.ashley.core.*
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import javafx.geometry.Pos
import uk.co.poolefoundries.baldinggate.model.Mob
import uk.co.poolefoundries.baldinggate.model.MobType
import uk.co.poolefoundries.baldinggate.model.Tile
import uk.co.poolefoundries.baldinggate.model.TileType
import kotlin.math.absoluteValue
import kotlin.math.sign
import kotlin.math.sqrt
import kotlin.random.Random

data class PositionComponent(val x: Int, val y: Int) : Component {
    fun distance(other: PositionComponent): Double {
        val xDiff = this.x - other.x
        val yDiff = this.y - other.y
        return sqrt((xDiff * xDiff + yDiff * yDiff).toDouble())
    }

    fun gridWiseDistance(other: PositionComponent):Int {
        val xDiff = this.x - other.x
        val yDiff = this.y - other.y
        return xDiff.absoluteValue + yDiff.absoluteValue
    }

    fun direction(other: PositionComponent): PositionComponent {
        val xDiff = other.x - this.x
        val yDiff = other.y - this.y
        // Moves hori
        return if (xDiff.absoluteValue >= yDiff.absoluteValue) {
            PositionComponent(xDiff.sign, 0)
        } else {
            PositionComponent(0, yDiff.sign)
        }
    }

    operator fun plus(other: PositionComponent): PositionComponent {
        return PositionComponent(
            this.x + other.x,
            this.y + other.y
        )
    }
    operator fun times(scalar:Float):PositionComponent{
        return PositionComponent((this.x.toFloat() * scalar).toInt(),(this.y.toFloat() * scalar).toInt())
    }

}



object SkeletonComponent : Component
object PlayerComponent : Component
object WallComponent : Component
object FloorComponent : Component

data class StatsComponent(val stats: Stats) : Component

data class VisualComponent(val renderable: Renderable) : Component

data class ColorComponent(val color:Color = Color.WHITE) : Component

data class Roll(val die: List<Int>, val mod: Int, val typical: Int) {
    fun roll() = die.map { Random.nextInt(it) }.sum() + mod
    fun typical() = die.map { it / 2 }.sum() + mod
}

data class Level(
    val name: String,
    val tiles: List<Tile>,
    val tileTypes: List<TileType>,
    val mobs: List<Mob>,
    val mobTypes: List<MobType>
)

data class Stats(
    val vitality: Int,
    var hitPoints: Int,
    val speed: Int,
    val maxAP: Int,
    val currentAP: Int,
    val attack: Roll
)

interface Renderable {
    fun draw(batch: Batch, x: Float, y: Float)
}

data class TextureRenderable(val texture: Texture) : Renderable {
    override fun draw(batch: Batch, x: Float, y: Float) {
        batch.draw(texture, x, y)
    }
}


class RenderingSystem(val stage: Stage, val tileSize: Float) : EntitySystem() {
    //    private val tileSize = 25f
    private val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)
    private val visualComponentMapper = ComponentMapper.getFor(VisualComponent::class.java)
    private val colorComponentMapper = ComponentMapper.getFor(ColorComponent::class.java)
    private var entities = ImmutableArray(Array<Entity>())


    override fun addedToEngine(engine: Engine) {
        entities = engine.getEntitiesFor(Family.all(PositionComponent::class.java, VisualComponent::class.java).get())
    }

    override fun update(deltaTime: Float) {
        stage.camera.update()
        stage.batch.projectionMatrix = stage.camera.combined

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.batch.begin()
        stage.batch.color= Color.WHITE
        entities.forEach(::drawEntity)
        stage.batch.end()
    }

    private fun drawEntity(entity: Entity) {
        val pos = positionMapper.get(entity)
        stage.batch.color = colorComponentMapper.get(entity).color
        visualComponentMapper.get(entity).renderable.draw(stage.batch, pos.x * tileSize, pos.y * tileSize)

    }
}
