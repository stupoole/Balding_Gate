package uk.co.poolefoundries.baldinggate.pathfinding

import uk.co.poolefoundries.baldinggate.core.PositionComponent

data class AstarNode(
    val x: Int,
    val y: Int,
    var startToNowCost: Double,
    var nowToEndCost: Double,
    var prevNode: AstarNode? = null
) {

    fun getPosition(): PositionComponent {
        return PositionComponent(x, y)
    }

    fun equals(other: AstarNode): Boolean {
        return this.x == other.x && this.y == other.y
    }

}