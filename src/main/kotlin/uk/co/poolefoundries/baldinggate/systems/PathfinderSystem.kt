package uk.co.poolefoundries.baldinggate.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import uk.co.poolefoundries.baldinggate.core.EnemyComponent
import uk.co.poolefoundries.baldinggate.core.FloorComponent
import uk.co.poolefoundries.baldinggate.core.PlayerComponent
import uk.co.poolefoundries.baldinggate.core.PositionComponent
import uk.co.poolefoundries.baldinggate.pathfinding.Astar
import uk.co.poolefoundries.baldinggate.pathfinding.AstarNode
import uk.co.poolefoundries.baldinggate.pathfinding.manhattanHeuristic

object PathfinderSystem : EntitySystem() {
    private val playerFamily: Family = Family.all(PlayerComponent::class.java).get()
    private val enemyFamily: Family = Family.all(EnemyComponent::class.java).get()
    private val floorsFamily: Family = Family.all(FloorComponent::class.java).get()
    private fun players() = engine.getEntitiesFor(playerFamily).toList()
    private fun enemies() = engine.getEntitiesFor(enemyFamily).toList()
    private fun floors() = engine.getEntitiesFor(floorsFamily).toList()
    private fun (Entity).toPosition() = getComponent(PositionComponent::class.java)
    private fun (AstarNode).toPosition() = PositionComponent(x, y)
    private val pathfinder = Astar(manhattanHeuristic())

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
    ): MutableList<AstarNode> {
        val startToNowCosts = tiles.map { it.manhattanDistance(start) }
        val nowToEndCosts = tiles.map { it.manhattanDistance(finish) }
        val graph = mutableListOf<AstarNode>()
        tiles.sortedBy { it.x }.forEachIndexed { index, tile ->
            val node =
                AstarNode(tile.x, tile.y, 1.0, startToNowCosts[index].toDouble(), nowToEndCosts[index].toDouble())
            graph.add(node)
        }
        return graph
    }

    fun findPath(start: PositionComponent, end: PositionComponent): List<PositionComponent> {
        val tiles = getEmptyTiles()
        val graph = constructGraph(tiles, start, end)
        val startNode = AstarNode(start.x, start.y, 0.0, 0.0, start.manhattanDistance(end).toDouble())
        val endNode = AstarNode(end.x, end.y, 1.0, start.manhattanDistance(end).toDouble(), 0.0)
        return pathfinder.getPath(graph, startNode, endNode).map { it.toPosition() }.reversed()
    }

}


