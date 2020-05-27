package uk.co.poolefoundries.baldinggate

import com.badlogic.ashley.core.Component
import uk.co.poolefoundries.baldinggate.model.Stats
import uk.co.poolefoundries.baldinggate.screens.Renderable
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

    operator fun plus (other: PositionComponent): PositionComponent{
        return PositionComponent(this.x + other.x, this.y + other.y)
    }
}


data class Roll(val die: List<Int>, val mod: Int, val typical: Int) {
    fun roll() = die.map { Random.nextInt(it) }.sum() + mod
    fun typical() = die.map { it / 2 }.sum() + mod
}

data class StatsComponent(val stats: Stats) : Component


data class VisualComponent(val renderable: Renderable) : Component
object WallComponent : Component