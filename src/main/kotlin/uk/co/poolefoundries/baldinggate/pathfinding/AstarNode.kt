package uk.co.poolefoundries.baldinggate.pathfinding

import uk.co.poolefoundries.baldinggate.core.PositionComponent

data class AstarNode(
    val x: Int,
    val y: Int,
    var gScore: Double,
    var fScore: Double,
    var passingCost: Double,
    var prevNode: AstarNode?
) {

    fun getPosition(): PositionComponent {
        return PositionComponent(x, y)
    }

    fun isSamePos(other: AstarNode): Boolean {
        return this.x == other.x && this.y == other.y
    }

}