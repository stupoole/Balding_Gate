package uk.co.poolefoundries.baldinggate.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import uk.co.poolefoundries.baldinggate.core.*
import uk.co.poolefoundries.baldinggate.systems.PathfinderSystem
import uk.co.poolefoundries.baldinggate.systems.enemy.EnemyTurnSystem


object PlayerTurnSystem : EntitySystem() {
    private val playerFamily: Family = Family.all(PlayerComponent::class.java).get()

    private val enemyFamily: Family = Family.all(EnemyComponent::class.java).get()
    private val floorsFamily: Family = Family.all(FloorComponent::class.java).get()
    private fun players() = engine.getEntitiesFor(playerFamily).toList()
    private fun enemies() = engine.getEntitiesFor(enemyFamily).toList()
    private fun floors() = engine.getEntitiesFor(floorsFamily).toList()

    // TODO: get list of valid actions that aren't movement and display on UI
    private fun (Entity).toPosition(): PositionComponent {
        return getComponent(PositionComponent::class.java)
    }

    private fun (Entity).toStats(): Stats {
        return getComponent(StatsComponent::class.java).stats
    }

    // Finds target and attacks if possible (starts animation too). Returns if attack was successful
    private fun (Entity).attack(target: Entity): Boolean {
        val damage = toStats().attack.roll()
        val targetStats = target.toStats()
        target.add(StatsComponent(targetStats.copy(hitPoints = targetStats.hitPoints - damage)))
        val rmnHP = target.toStats().hitPoints

        this.add(StatsComponent(toStats().useAp(1)))
        if (rmnHP > 0) {
            println("Dealt $damage Damage. ${target.toStats().hitPoints} hp left")
        } else {
            println("Dealt $damage Damage. Target dies by your hand!")
        }
        return true
    }

    // Finds path to target and moves if possible (starts animation too).
    // Returns if move reached as close to target as is valid i.e. is finished moving
    private fun move(entity: Entity, target: PositionComponent): Boolean {
        val ap = entity.toStats().currentAP
        val startPos = entity.toPosition()
        val distance = startPos.manhattanDistance(target)
        val path = PathfinderSystem.findPath(startPos, target)
        if (path.isEmpty() || path.last() == startPos) {
            return false
        }
        val positions = path.take(minOf(entity.toStats().speed + 1, distance + 1, path.size))
        entity.getComponent(VisualComponent::class.java).addAnimation(MoveAnimation(positions))
        entity.add(positions.last())
        entity.add(StatsComponent(entity.toStats().useAp(1)))
        return positions.size == path.size

    }

    // Will do the end of turn stuff such as refresh AP and act out enemy actions
    fun endTurn(): Boolean {
        engine.getSystem(EnemyTurnSystem::class.java).takeTurn()
//        engine.getSystem(EntitySelectionSystem::class.java).deselectEntity()
        players().forEach { player ->
            val stats = player.toStats()
            player.add(StatsComponent(stats.restoreAp()))
        }

        return true
    }

    // Will determine what action to do and do it and then return whether the action was successful
    fun determineAction(selected: Entity, targetPos: PositionComponent): Boolean {
        val distance = selected.toPosition().manhattanDistance(targetPos)
        val playerStats = selected.toStats()
        if (selected.toStats().currentAP <= 0 || !floors().map { it.toPosition() }.contains(targetPos)) {
            return false
        }
        return when {
            distance == 1 -> {
                val targetEnemies = enemies().filter { it.toPosition() == targetPos }
                return if (targetEnemies.isNotEmpty()) {
                    selected.attack(targetEnemies.first())
                    true
                } else {
                    move(selected, targetPos)
                }

            }
            distance <= playerStats.speed * playerStats.currentAP && distance > 1 -> {
                for (i in 0 until playerStats.currentAP) {
                    if (move(selected, targetPos)) {
                        return true
                    }
                }
                return true
            }
            else -> false
        }
    }
}
