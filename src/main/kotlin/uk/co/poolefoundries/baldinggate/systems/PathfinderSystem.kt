package uk.co.poolefoundries.baldinggate.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import uk.co.poolefoundries.baldinggate.core.EnemyComponent
import uk.co.poolefoundries.baldinggate.core.FloorComponent
import uk.co.poolefoundries.baldinggate.core.PlayerComponent
import uk.co.poolefoundries.baldinggate.core.PositionComponent
import uk.co.poolefoundries.baldinggate.pathfinding.AStar
import uk.co.poolefoundries.baldinggate.pathfinding.AStarNode
import uk.co.poolefoundries.baldinggate.pathfinding.manhattanHeuristic

object PathfinderSystem : EntitySystem() {
    private val playerFamily: Family = Family.all(PlayerComponent::class.java).get()
    private val enemyFamily: Family = Family.all(EnemyComponent::class.java).get()
    private val floorsFamily: Family = Family.all(FloorComponent::class.java).get()
    private fun players() = engine.getEntitiesFor(playerFamily).toList()
    private fun enemies() = engine.getEntitiesFor(enemyFamily).toList()
    private fun floors() = engine.getEntitiesFor(floorsFamily).toList()
    private fun (Entity).toPosition() = getComponent(PositionComponent::class.java)
    private fun (AStarNode).toPosition() = PositionComponent(x, y)
    private val pathfinder = AStar(manhattanHeuristic())

    private fun getEmptyTiles(): List<PositionComponent> {
        val playerPositions = players().map { it.toPosition() }
        val enemyPositions = enemies().map { it.toPosition() }
        return floors().filter { !(playerPositions.contains(it.toPosition()) || enemyPositions.contains(it.toPosition())) }
            .map { it.toPosition() }
    }

    private fun constructGraph(
        tiles: List<PositionComponent>,
        start: PositionComponent,
        finish: PositionComponent
    ): MutableList<AStarNode> {
        val startToNowCosts = tiles.map { it.manhattanDistance(start) }
        val nowToEndCosts = tiles.map { it.manhattanDistance(finish) }
        val graph = mutableListOf<AStarNode>()
        tiles.sortedBy { it.x }.forEachIndexed { index, tile ->
            val node =
                AStarNode(tile.x, tile.y, startToNowCosts[index].toDouble(), nowToEndCosts[index].toDouble())
            graph.add(node)
        }
        return graph
    }

    fun findPath(start: PositionComponent, end: PositionComponent): List<PositionComponent> {
        val tiles = getEmptyTiles()
        val graph = constructGraph(tiles, start, end)
        val startNode = AStarNode(start.x, start.y, 0.0, start.manhattanDistance(end).toDouble())
        val endNode = AStarNode(end.x, end.y, start.manhattanDistance(end).toDouble(), 0.0)
        return pathfinder.getPath(graph, startNode, endNode).map { it.toPosition() }.reversed()
    }

}


