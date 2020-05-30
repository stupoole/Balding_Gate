package uk.co.poolefoundries.baldinggate.pathfinding

import kotlin.math.absoluteValue
import kotlin.math.sqrt


interface AStarHeuristic{
    fun costEstimate(node:AstarNode, endNode:AstarNode):Double
}

class euclideanHeuristic:AStarHeuristic{
    override fun costEstimate(node: AstarNode, endNode: AstarNode): Double {
        val xDiff = node.x - endNode.x
        val yDiff = node.y - endNode.y
        return sqrt((xDiff * xDiff + yDiff * yDiff).toDouble())
    }
}

class manhattanHeuristic:AStarHeuristic{
    override fun costEstimate(node: AstarNode, endNode: AstarNode): Double {
        val xDiff = node.x - endNode.x
        val yDiff = node.y - endNode.y
        return (xDiff.absoluteValue + yDiff.absoluteValue).toDouble()
    }
}