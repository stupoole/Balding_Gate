package uk.co.poolefoundries.baldinggate.systems

import com.badlogic.ashley.core.Engine
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
    private fun (Entity).toNode() = AStarNode(toPosition().x, toPosition().y)


    private fun (AStarNode).toPosition() = PositionComponent(x, y)
    private val pathfinder = AStar(manhattanHeuristic())
    private var levelMap = mutableListOf<AStarNode>()

    override fun addedToEngine(engine: Engine?) {
        levelMap = floors().map { it.toPosition() }.map { AStarNode(it.x, it.y) }.toMutableList()
    }

    fun findPath(start: PositionComponent, end: PositionComponent): List<PositionComponent> {
        val startNode = AStarNode(start.x, start.y)
        val endNode = AStarNode(end.x, end.y)
        val nodesToRemove = enemies().map { it.toNode() } + players().map { it.toNode() }
        return pathfinder.getPath(levelMap - nodesToRemove, startNode, endNode).map { it.toPosition() }.reversed()
    }
}





