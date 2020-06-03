package uk.co.poolefoundries.baldinggate.systems.enemy

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import uk.co.poolefoundries.baldinggate.core.*
import uk.co.poolefoundries.baldinggate.skeleton.*

object EnemyTurnSystem : EntitySystem() {
    private val playerFamily: Family = Family.all(PlayerComponent::class.java).get()
    private val enemyFamily: Family = Family.all(EnemyComponent::class.java).get()


    private fun (Entity).toMobInfo() : MobInfo {
        val stats = getComponent(StatsComponent::class.java).stats
        val pos = getComponent(PositionComponent::class.java)
        val id = getComponent(IdComponent::class.java)
        return MobInfo(id.id, pos.copy(), stats.copy())
    }

    private fun players() = engine.getEntitiesFor(playerFamily).toList()
    private fun enemies() = engine.getEntitiesFor(enemyFamily).toList()


    fun takeTurn() {
        // TODO: Add an ID component so we can identify entities by a UUID
        val playerIds = players()
            .associateBy { it.getComponent(IdComponent::class.java).id }
        val enemyIds = enemies()
            .associateBy { it.getComponent(IdComponent::class.java).id }

        val actions = SkeletonAI.getPlan(
            playerIds.map { it.value.toMobInfo() },
            enemyIds.map { it.value.toMobInfo() }
        )

        actions.actions.forEach {
            when(it) {
                is MoveTowards -> {
                    // TODO: Add animations
                    val enemy = enemyIds.getValue(it.selfId).toMobInfo()
                    val target = playerIds.getValue(it.targetId).toMobInfo()

                    val newPos = it.getNewPos(enemy, target)

                    enemyIds.getValue(it.selfId).add(newPos)

                }
                is Attack -> {
                    val enemy = enemyIds.getValue(it.selfId).toMobInfo()
                    val target = playerIds.getValue(it.targetId).toMobInfo()

                    val damage = enemy.stats.attack.roll()
                    val newStats = target.stats.applyDamage(damage)
                    playerIds.getValue(it.targetId).add(StatsComponent(newStats))

                    println("Big oof you just took $damage damage, ${newStats.hitPoints} hp left")
                }
                is Win -> {
                    println("You is dead!!!")
                    return
                }
                is EndTurn -> {
                    return
                }
            }
        }

        enemyIds.values.forEach {
            val stats = it.getComponent(StatsComponent::class.java).stats.restoreAp()
            it.add(StatsComponent(stats))
        }
    }
}