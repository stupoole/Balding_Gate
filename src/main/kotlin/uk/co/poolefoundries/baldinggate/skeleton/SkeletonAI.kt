package uk.co.poolefoundries.baldinggate.skeleton

import uk.co.poolefoundries.baldinggate.ai.*
import uk.co.poolefoundries.baldinggate.core.PositionComponent
import uk.co.poolefoundries.baldinggate.core.Stats
import uk.co.poolefoundries.baldinggate.systems.PathfinderSystem


data class MobInfo(val id: String, val pos: PositionComponent, val stats: Stats)

object Win : Action {
    override fun cost(state: WorldState): Double {
        return 0.0
    }

    override fun prerequisitesMet(state: WorldState): Boolean {
        return state.getPlayerIds().none { state.getMobInfo(it).stats.hitPoints > 0 }
    }

    override fun update(state: WorldState): WorldState {
        return state
    }

    override fun toString(): String {
        return "Win"
    }

}

object EndTurn : Action {
    override fun cost(state: WorldState): Double {
        return 10.0
    }

    override fun prerequisitesMet(state: WorldState): Boolean {
        // TODO: This is a bit naff but reduces the explosion of branches. Will have to implement a heuristic AI at some
        // point so it doesn't explore every single possibility
        return state.getEnemyIds().none { state.getMobInfo(it).stats.currentAP > 0 }
    }

    override fun update(state: WorldState): WorldState {
        var newState = state
        state.getEnemyIds().forEach {
            val info = state.getMobInfo(it)
            newState = newState.setMobInfo(info.copy(stats = info.stats.restoreAp()))
        }
        return newState
    }

    override fun toString(): String {
        return "EndTurn"
    }

}

interface TargetedAction : Action {
    val selfId: String
    val targetId: String

    fun (WorldState).selfInfo() = getMobInfo(selfId)
    fun (WorldState).targetInfo() = getMobInfo(targetId)

    fun (WorldState).distToTarget() = selfInfo().pos.manhattanDistance(targetInfo().pos)
}

class MoveTowards(override val selfId: String, override val targetId: String) : TargetedAction {
    override fun cost(state: WorldState): Double {
        return 1.0
    }

    override fun prerequisitesMet(state: WorldState): Boolean {
        return state.distToTarget() > 1 && state.selfInfo().stats.currentAP >= 1
    }

    override fun update(state: WorldState): WorldState {
        val targetInfo = state.targetInfo()
        val selfInfo = state.selfInfo()

        return state.setMobInfo(selfInfo.copy(pos = getNewPos(selfInfo, targetInfo), stats = selfInfo.stats.useAp(1)))
    }

    override fun toString(): String {
        return "MoveTowards"
    }

//    fun getNewPos(selfInfo: MobInfo, targetInfo: MobInfo): PositionComponent {
//        val distance = selfInfo.pos.manhattanDistance(targetInfo.pos)
//        val path = PathfinderSystem.findPath(selfInfo.pos, targetInfo.pos)
//        if (path.isEmpty()){return selfInfo.pos}
//        return path.subList(0, minOf(selfInfo.stats.speed, distance) + 1).last()
//    }

    fun getNewPos(selfInfo: MobInfo, targetInfo: MobInfo) : PositionComponent {
        val distance = selfInfo.pos.manhattanDistance(targetInfo.pos)
        val path = PathfinderSystem.findPath(selfInfo.pos, targetInfo.pos)
        return if (path.isNotEmpty()){
            path.subList(0, minOf(selfInfo.stats.speed + 1, distance, path.size) ).last()
        } else selfInfo.pos
//        return selfInfo.pos.moveTowards(targetInfo.pos, selfInfo.stats.speed)
    }

    fun getNewPath(selfInfo: MobInfo, targetInfo: MobInfo): List<PositionComponent> {
        val distance = selfInfo.pos.manhattanDistance(targetInfo.pos)
        val path = PathfinderSystem.findPath(selfInfo.pos, targetInfo.pos)
        return path.subList(0, minOf(selfInfo.stats.speed + 1, distance, path.size) )

    }
}

class Attack(override val selfId: String, override val targetId: String) : TargetedAction {
    override fun cost(state: WorldState): Double {
        return 1.0
    }

    override fun prerequisitesMet(state: WorldState): Boolean {
        return state.distToTarget() == 1 && state.selfInfo().stats.currentAP >= 1
    }

    override fun update(state: WorldState): WorldState {
        val stats = state.targetInfo().stats.applyDamage(state.selfInfo().stats.attack.typical())
        return state
            .setMobInfo(state.targetInfo().copy(stats = stats))
            .setMobInfo(state.selfInfo().copy(stats = state.selfInfo().stats.useAp(1)))
    }

    override fun toString(): String {
        return "AttackPlayer"
    }
}

object SkeletonAI {

    fun getPlan(players: Collection<MobInfo>, enemies: Collection<MobInfo>): Branch {
        val goal = Goal(Win, 1.0)
        return getActionPlan(worldState(players, enemies), actions(players, enemies, goal), goal)!!
    }

    fun worldState(players: Collection<MobInfo>, enemies: Collection<MobInfo>): WorldState {
        return mutableMapOf<String, Any>()
            .setPlayerIds(players.map { it.id })
            .setEnemyIds(enemies.map { it.id })
            .setMobInfo(players.union(enemies))
    }

    fun actions(players: Collection<MobInfo>, enemies: Collection<MobInfo>, goal: Goal): List<Action> {
        val attackPlayerActions = players.flatMap { player -> enemies.map { enemy -> Attack(enemy.id, player.id) } }
        val moveTowardsPlayerActions =
            players.flatMap { player -> enemies.map { enemy -> MoveTowards(enemy.id, player.id) } }

        return listOf(goal.action, EndTurn)
            .union(attackPlayerActions)
            .union(moveTowardsPlayerActions)
            .toList()
    }
}