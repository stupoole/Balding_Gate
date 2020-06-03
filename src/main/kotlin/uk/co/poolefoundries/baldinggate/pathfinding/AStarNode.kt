package uk.co.poolefoundries.baldinggate.pathfinding

import uk.co.poolefoundries.baldinggate.core.PositionComponent

data class AStarNode(
    val x: Int,
    val y: Int,
    var startToNowCost: Double,
    var nowToEndCost: Double,
    var prevNode: AStarNode? = null
) {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is AStarNode -> this.x == other.x && this.y == other.y
            is PositionComponent -> this.x == other.x && this.y == other.y
            else -> false
        }
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + startToNowCost.hashCode()
        result = 31 * result + nowToEndCost.hashCode()
        result = 31 * result + (prevNode?.hashCode() ?: 0)
        return result
    }

}