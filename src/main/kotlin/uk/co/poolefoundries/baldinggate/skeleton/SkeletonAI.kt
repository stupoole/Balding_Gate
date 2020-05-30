package uk.co.poolefoundries.baldinggate.skeleton

import uk.co.poolefoundries.baldinggate.ai.*
import uk.co.poolefoundries.baldinggate.ai.actions.getPosition
import uk.co.poolefoundries.baldinggate.ai.actions.getStats
import uk.co.poolefoundries.baldinggate.core.PositionComponent
import uk.co.poolefoundries.baldinggate.core.Stats

const val SKELETON_POSITION_KEY = "position"
const val PLAYER_POSITION_KEY = "playerPos"
const val PLAYER_STATS_KEY = "playerStats"
const val SKELETON_STATS_KEY = "stats"

val goal = Goal(Win, 1.0)


object Win : Action {
    override fun cost(state: WorldState): Double {
        return 0.0
    }

    override fun prerequisite(state: WorldState): Boolean {
        // TODO: consider that there are more players now
        return state.getStats(PLAYER_STATS_KEY).hitPoints <= 0
    }

    override fun update(state: WorldState): WorldState {
        return state
    }

    override fun toString(): String {
        return "Win"
    }

}

// TODO: take in a parameter of the player to move towards as well as the skeleton doing the moving
object MoveTowardsPlayer : Action {
    override fun cost(state: WorldState): Double {
        return state.getPosition(SKELETON_POSITION_KEY).euclideanDistance(state.getPosition(PLAYER_POSITION_KEY))
    }

    override fun prerequisite(state: WorldState): Boolean {
        return state.getPosition(SKELETON_POSITION_KEY).euclideanDistance(state.getPosition(PLAYER_POSITION_KEY)) > 1.0
    }

    override fun update(state: WorldState): WorldState {

        return state.withValue(SKELETON_POSITION_KEY, state.getValue(PLAYER_POSITION_KEY))
    }

    override fun toString(): String {
        return "MoveTowardsPlayer"
    }
}

// TODO: make this take in the player to attack and the skeleton doing the attacking as a parameter
object AttackPlayer : Action {
    override fun cost(state: WorldState): Double {
        return state.getStats(PLAYER_STATS_KEY).attack.typical().toDouble()
    }


    override fun prerequisite(state: WorldState): Boolean {
        val pos = state.getPosition(SKELETON_POSITION_KEY)
        val playerPos = state.getPosition(PLAYER_POSITION_KEY)
        return pos.euclideanDistance(playerPos) <= 1.0
    }

    override fun update(state: WorldState): WorldState {
        // TODO: simplify
        val playerStats = state.getStats(PLAYER_STATS_KEY)
        val damage = state.getStats(SKELETON_STATS_KEY).attack.typical()
        val newHealth = playerStats.hitPoints - damage
        val newStats = playerStats.copy(hitPoints = newHealth)
//        println(newHealth)
        // TODO replace with setstats
        return state.withValue(PLAYER_STATS_KEY, newStats)
    }

    override fun toString(): String {
        return "AttackPlayer"
    }
}

object SkeletonAI {

    private val actions = listOf(
        MoveTowardsPlayer, //TODO: create one of these for each alive player/skeleton
        AttackPlayer, //TODO: create one of these for each alive player/skeleton
        Win
    )

    fun getPlan(
        pos: PositionComponent,
        playerPos: PositionComponent,
        stats: Stats,
        playerStats: Stats
    ): Branch {
        if (!actions.contains(goal.action)) {
            throw RuntimeException("Action ${goal.action} not found in list of available actions")
        }

        val worldState: WorldState = mapOf(
            PLAYER_POSITION_KEY to playerPos,
            SKELETON_POSITION_KEY to pos,
            SKELETON_STATS_KEY to stats,
            PLAYER_STATS_KEY to playerStats
        )

        return getActionPlan(worldState, actions, goal)!!
    }
}