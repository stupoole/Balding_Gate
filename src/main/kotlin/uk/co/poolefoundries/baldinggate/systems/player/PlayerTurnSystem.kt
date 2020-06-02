package uk.co.poolefoundries.baldinggate.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import uk.co.poolefoundries.baldinggate.core.*
import uk.co.poolefoundries.baldinggate.systems.EntitySelectionSystem
import uk.co.poolefoundries.baldinggate.systems.enemy.EnemyTurnSystem


object PlayerTurnSystem: EntitySystem() {
    private val playerFamily: Family = Family.all(PlayerComponent::class.java).get()
    private fun players() = engine.getEntitiesFor(playerFamily).toList()
    // todo get list of valid actions that aren't movement and display on UI
    private fun (Entity).toPosition(): PositionComponent {
        return getComponent(PositionComponent::class.java)
    }

    private fun (Entity).toStats(): Stats {
        return getComponent(StatsComponent::class.java).stats
    }

    // Finds target and attacks if possible (starts animation too). Returns if attack was successful
    private fun attack():Boolean{
        return false
        // Todo(NOT IMPLEMENTED)
    }

    // Finds path to target and moves if possible (starts animation too). Returns if move was successful
    private fun move(entity: Entity,target: PositionComponent):Boolean{
        val ap = entity.toStats().currentAP
        if (ap>0) {
            val positions = mutableListOf<PositionComponent>()
            var tempPos = entity.toPosition()
            val distance = tempPos.manhattanDistance(target)
            positions.add(tempPos)
            // TODO: pathfinding
            for (step in 0 until minOf(entity.toStats().speed, distance)) {
                tempPos += tempPos.direction(target)
                positions.add(tempPos)
            }
            entity.getComponent(VisualComponent::class.java).addAnimation(MoveAnimation(positions))
            entity.add(positions.last())
            entity.add(StatsComponent(entity.toStats().copy(currentAP = ap - 1)))
            return true
        }
        return false
    }

    // Will do the end of turn stuff such as refresh AP and act out enemy actions
    fun endTurn():Boolean{
        engine.getSystem(EnemyTurnSystem::class.java).takeTurn()
        engine.getSystem(EntitySelectionSystem::class.java).deselectEntity()
        players().forEach { player ->
            val stats = player.toStats()
            player.add(StatsComponent(stats.copy(currentAP = stats.maxAP)))
        }
        engine.getSystem(EntitySelectionSystem::class.java).deselectEntity()
        return true
    }

    // Will determine what action to do and do it and then return whether the action was successful
    fun determineAction(selected: Entity, positionComponent: PositionComponent):Boolean {
        move(selected, positionComponent)
        return true
    }
}