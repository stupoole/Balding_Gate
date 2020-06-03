package uk.co.poolefoundries.baldinggate.core

import com.badlogic.ashley.core.*
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import uk.co.poolefoundries.baldinggate.model.Mob
import uk.co.poolefoundries.baldinggate.model.MobType
import uk.co.poolefoundries.baldinggate.model.Tile
import uk.co.poolefoundries.baldinggate.model.TileType
import kotlin.math.absoluteValue
import kotlin.math.round
import kotlin.math.sign
import kotlin.math.sqrt
import kotlin.random.Random

data class IdComponent(val id : String) : Component

data class PositionComponent(val x: Int, val y: Int) : Component {

    fun crowFliesDistance(other: PositionComponent):Float{
        val xDiff = this.x - other.x
        val yDiff = this.y - other.y
        return sqrt((xDiff*xDiff).toFloat() + (yDiff*yDiff).toFloat())
    }

    fun manhattanDistance(other: PositionComponent):Int {
        val xDiff = this.x - other.x
        val yDiff = this.y - other.y
        return xDiff.absoluteValue + yDiff.absoluteValue
    }

    fun direction(other: PositionComponent): PositionComponent {
        val xDiff = other.x - this.x
        val yDiff = other.y - this.y
        return if (xDiff.absoluteValue >= yDiff.absoluteValue) {
            PositionComponent(xDiff.sign, 0)
        } else {
            PositionComponent(0, yDiff.sign)
        }
    }

    fun moveTowards(target : PositionComponent, speed: Int) : PositionComponent {
        var distanceTravelled = 0
        var pos = this
        while (distanceTravelled <= speed) {
            val next = pos + pos.direction(target)
            distanceTravelled++

            if (next == target) {
                return pos
            }
            pos = next
        }

        return pos
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

interface Animation {
    fun update(delta: Float)
    fun getX():Int
    fun getY():Int
    fun complete():Boolean
}



data class MoveAnimation(val positions: List<PositionComponent> = listOf(), private var progress:Float = 0F) : Animation {
    private val animationDuration:Float = 0.1F

    override fun update(delta: Float) {
        this.progress += delta / animationDuration
    }

    override fun getX(): Int {
        return positions[minOf(round(this.progress).toInt(), this.positions.size-1)].x
    }

    override fun getY(): Int {
        return positions[minOf(round(this.progress).toInt(), this.positions.size-1)].y
    }

    override fun complete() : Boolean {
        return progress > this.positions.size
    }

}

object EnemyComponent : Component
object PlayerComponent : Component
object WallComponent : Component
object FloorComponent : Component

data class StatsComponent(val stats: Stats) : Component

data class VisualComponent(val renderable: Renderable) : Component{

    private var pendingAnimations = mutableListOf<Animation>()
    fun addAnimation(animation: Animation){
        pendingAnimations.add(animation)
    }

    fun updateAndGetAnimationPos(delta: Float):PositionComponent?{
        var pos:PositionComponent? = null
        if (pendingAnimations.isNotEmpty()){
            pendingAnimations.first().update(delta)
            pos = PositionComponent(pendingAnimations.first().getX(), pendingAnimations.first().getY())
            if (pendingAnimations.first().complete()){
                pendingAnimations.removeAt(0)
            }
        }
        return pos
    }

}

data class ColorComponent(val color:Color = Color.WHITE) : Component

data class Roll(val die: List<Int>, val mod: Int) {
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
    val hitPoints: Int,
    val speed: Int,
    val maxAP: Int,
    val currentAP: Int,
    val attack: Roll
) {
    fun useAp(ap : Int) = copy(currentAP = currentAP-ap)
    fun restoreAp() = copy(currentAP = maxAP)
    fun applyDamage(damage: Int) = copy(hitPoints = hitPoints - damage)
}

interface Renderable {
    fun draw(batch: Batch, x: Float, y: Float)
}

data class TextureRenderable(val texture: Texture) : Renderable {
    override fun draw(batch: Batch, x: Float, y: Float) {
        batch.draw(texture, x, y)
    }
}

data class MobRenderable(val entity: Entity, val renderable: Renderable) : Renderable {
    override fun draw(batch: Batch, x: Float, y: Float) {
        if (entity.getComponent(StatsComponent::class.java).stats.hitPoints > 0) {
            renderable.draw(batch, x, y)
        }
    }
}



