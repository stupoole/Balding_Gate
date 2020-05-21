package uk.co.poolefoundries.baldinggate.skeleton

import uk.co.poolefoundries.baldinggate.*
import uk.co.poolefoundries.baldinggate.ai.*
import uk.co.poolefoundries.baldinggate.ai.actions.MoveAction
import uk.co.poolefoundries.baldinggate.ai.actions.getPosition
import uk.co.poolefoundries.baldinggate.ai.actions.getStats
import uk.co.poolefoundries.baldinggate.model.Stats
import java.net.StandardSocketOptions
import javax.swing.plaf.synth.SynthTextAreaUI

val POSITION_KEY = "position"
val PLAYER_POSITION_KEY = "playerPos"
val PLAYER_STATS_KEY = "playerStats"
val STATS_KEY = "stats"

val goal = Goal(AttackPlayer, 1.0)


// TODO: add goal: attack player
object MoveTowardsPlayer : Action {
    override val cost = 1.0

    override fun prerequisite(state: WorldState): Boolean {
        val pos = state.getPosition(POSITION_KEY)
        val playerPos = state.getPosition(PLAYER_POSITION_KEY)
        return pos.distance(playerPos) <= 1.0
    }

    override fun update(state: WorldState): WorldState {
        return state.withValue(POSITION_KEY, state.getValue(PLAYER_POSITION_KEY))

    }

    override fun toString(): String {
        return "MoveToPlayer"
    }
}

object AttackPlayer : Action {
    override val cost = 1.0

    override fun prerequisite(state: WorldState): Boolean {
        val pos = state.getPosition(POSITION_KEY)
        val playerPos = state.getPosition(PLAYER_POSITION_KEY)
        return pos.distance(playerPos) <= 1.0
    }

    override fun update(state: WorldState): WorldState {
        // TODO: implement a health/damage system
        val playerStats = state.getStats(PLAYER_STATS_KEY)
        val damage = state.getStats(STATS_KEY).stats.attack.roll()
        val newHealth = playerStats.stats.hitPoints - damage
        val newStats = StatsComponent(playerStats.stats.copy(hitPoints=newHealth))
        return state.withValue(PLAYER_STATS_KEY, newStats)
    }

    override fun toString(): String {
        return "AttackPlayer"
    }
}

object SkeletonAI {
    // Todo: automatically populate this list of actions?
    val actions = listOf(
        MoveAction(POSITION_KEY, UP),
        MoveAction(POSITION_KEY, DOWN),
        MoveAction(POSITION_KEY, LEFT),
        MoveAction(POSITION_KEY, RIGHT),
        MoveTowardsPlayer,
        AttackPlayer
    )

    fun getNewState(pos: PositionComponent, playerPos: PositionComponent, stats:StatsComponent, playerStats: StatsComponent): WorldState {
        if (!actions.contains(goal.action)) {
            throw RuntimeException("Action ${goal.action} not found in list of available actions")
        }

//        val worldState: WorldState = mapOf(PLAYER_POSITION_KEY to playerPos, POSITION_KEY to pos)
        val worldState: WorldState = mapOf(PLAYER_POSITION_KEY to playerPos, POSITION_KEY to pos, STATS_KEY to stats, PLAYER_STATS_KEY to playerStats)
        val plan = getActionPlan(worldState, actions, goal)!!
//      TODO:  implement pathfinding
        val action = plan.actions.first()

        val newState = action.update(worldState)

        return newState
    }
}