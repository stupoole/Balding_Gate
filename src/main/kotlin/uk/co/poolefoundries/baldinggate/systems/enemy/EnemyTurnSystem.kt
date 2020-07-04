package uk.co.poolefoundries.baldinggate.systems.enemy

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import uk.co.poolefoundries.baldinggate.ai.*
import uk.co.poolefoundries.baldinggate.ai.experimental.ExperimentalAi
import uk.co.poolefoundries.baldinggate.core.*
import uk.co.poolefoundries.baldinggate.systems.PathfinderSystem
import uk.co.poolefoundries.baldinggate.systems.enemy.ai.*

object EnemyTurnSystem : EntitySystem() {
    private val playerFamily: Family = Family.all(PlayerComponent::class.java).get()
    private val enemyFamily: Family = Family.all(EnemyComponent::class.java).get()

    private fun (Entity).toMobInfo() : MobInfo {
        val stats = getComponent(StatsComponent::class.java).stats
        val pos = getComponent(PositionComponent::class.java)
        val id = getComponent(IdComponent::class.java)
        return MobInfo(
            id.id,
            pos.copy(),
            stats.copy()
        )
    }

    private fun (Entity).addMoveAnimation(path:List<PositionComponent>){
        val visualComponent = getComponent(VisualComponent::class.java)
        visualComponent.addAnimation(MoveAnimation(path))
    }

    private fun players() = engine.getEntitiesFor(playerFamily).toList()
    private fun enemies() = engine.getEntitiesFor(enemyFamily).toList()

    fun takeTurn() {
        val playerIds = players()
            .associateBy { it.getComponent(IdComponent::class.java).id }
        val enemyIds = enemies()
            .associateBy { it.getComponent(IdComponent::class.java).id }

        val actions = getPlan(playerIds.map { it.value.toMobInfo() }, enemyIds.map { it.value.toMobInfo() })

        actions.forEach {
            when(it) {
                is MoveTowards -> {
                    // TODO: Add attack/walking animations
                    val enemy = enemyIds.getValue(it.selfId).toMobInfo()
                    val target = playerIds.getValue(it.targetId).toMobInfo()

                    val path = getPath(enemy, target)
                    enemyIds.getValue(it.selfId).addMoveAnimation(path)
                    enemyIds.getValue(it.selfId).add(path.last())
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

    fun getPath(selfInfo: MobInfo, targetInfo: MobInfo): List<PositionComponent> {
        val distance = selfInfo.pos.manhattanDistance(targetInfo.pos)
        val path = PathfinderSystem.findPath(selfInfo.pos, targetInfo.pos)
        return path.take(minOf(selfInfo.stats.speed + 1, distance, path.size) )
    }

    private fun getPlan(players: Collection<MobInfo>, enemies: Collection<MobInfo>): Plan {
        // TODO(jpoole): We probably want to just inject this AI dependency into this
        val ai = if (Flags.USE_EXPERIMENTAL_AI) {
            ExperimentalAi.new(players, enemies,
                Win
            )
        } else {
            PlayerChaserAi.new(players, enemies,
                Win
            )
        }
        return ai.getActionPlan(worldState(players, enemies))
    }

    private fun worldState(players: Collection<MobInfo>, enemies: Collection<MobInfo>): WorldState {
        return mutableMapOf<String, Any>()
            .setPlayerIds(players.map { it.id })
            .setEnemyIds(enemies.map { it.id })
            .setMobInfo(players.union(enemies))
            .setWorldMap(PathfinderSystem.levelMap)
    }
}