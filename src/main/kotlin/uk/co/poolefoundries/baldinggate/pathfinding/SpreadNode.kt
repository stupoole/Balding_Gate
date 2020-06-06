package uk.co.poolefoundries.baldinggate.pathfinding

import uk.co.poolefoundries.baldinggate.core.PositionComponent
// Connections : 0 -> fully connected, 1-> none on top, 2-> none on bottom, 4-> none on left, 8-> none on right
data class SpreadNode (
    val x: Int,
    val y: Int,
    var top: Boolean = false,
    var bottom : Boolean = false,
    var left : Boolean = false,
    var right : Boolean = false
){
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is SpreadNode -> this.x == other.x && this.y == other.y
            is AStarNode -> this.x == other.x && this.y == other.y
            is PositionComponent -> this.x == other.x && this.y == other.y
            else -> false
        }
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + top.hashCode()
        result = 31 * result + bottom.hashCode()
        result = 31 * result + left.hashCode()
        result = 31 * result + right.hashCode()
        return result
    }


}