package uk.co.poolefoundries.baldinggate

import com.badlogic.ashley.core.Component
import kotlin.math.sqrt

data class PositionComponent(val x: Int, val y: Int) : Component {
    fun distance(other: PositionComponent) : Double {
        val xDiff = this.x - other.x
        val yDiff = this.y - other.y
        return sqrt((xDiff*xDiff + yDiff*yDiff).toDouble())
    }
}
data class VisualComponent(val renderable: Renderable) : Component
object WallComponent : Component