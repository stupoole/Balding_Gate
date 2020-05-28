package uk.co.poolefoundries.baldinggate.skeleton

import uk.co.poolefoundries.baldinggate.ai.*
import uk.co.poolefoundries.baldinggate.ai.actions.getPosition
import uk.co.poolefoundries.baldinggate.ai.actions.getStats
import uk.co.poolefoundries.baldinggate.core.PositionComponent
import uk.co.poolefoundries.baldinggate.model.Stats

data class Player(
    val hitPoints: Int,
    val pos: PositionComponent
)


val SKELETON_POSITION_KEY = "position"
val PLAYER_POSITION_KEY = "playerPos"
val PLAYER_STATS_KEY = "playerStats"
val SKELETON_STATS_KEY = "stats"
val PLAYERS_KEY = "players"

val goal = Goal(Win, 1.0)


object Win : Action {
    override fun cost(state: WorldState): Double {
        return 0.0
    }

    override fun prerequisite(state: WorldState): Boolean {
        // TODO think about fixing the typecast
//        val playerslist = state.get(PLAYERS_KEY) as List<Player>
//        return playerslist.all { it.hitPoints <= 0 }
        return state.getStats(PLAYER_STATS_KEY).hitPoints <= 0
    }

    override fun update(state: WorldState): WorldState {
        return state
    }

    override fun toString(): String {
        return "Win"
    }

}

object MoveTowardsPlayer : Action {
    override fun cost(state: WorldState): Double {
        return state.getPosition(SKELETON_POSITION_KEY).distance(state.getPosition(PLAYER_POSITION_KEY))
    }

    override fun prerequisite(state: WorldState): Boolean {
        return state.getPosition(SKELETON_POSITION_KEY).distance(state.getPosition(PLAYER_POSITION_KEY)) > 1.0
    }

    override fun update(state: WorldState): WorldState {

        return state.withValue(SKELETON_POSITION_KEY, state.getValue(PLAYER_POSITION_KEY))
    }

    override fun toString(): String {
        return "MoveTowardsPlayer"
    }
}

object AttackPlayer : Action {
    override fun cost(state: WorldState): Double {
        return state.getStats(PLAYER_STATS_KEY).attack.typical().toDouble()
    }


    override fun prerequisite(state: WorldState): Boolean {
        val pos = state.getPosition(SKELETON_POSITION_KEY)
        val playerPos = state.getPosition(PLAYER_POSITION_KEY)
        return pos.distance(playerPos) <= 1.0
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
        MoveTowardsPlayer,
        AttackPlayer,
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

//        val worldState: WorldState = mapOf(PLAYER_POSITION_KEY to playerPos, POSITION_KEY to pos)
        val worldState: WorldState = mapOf(
            PLAYER_POSITION_KEY to playerPos,
            SKELETON_POSITION_KEY to pos,
            SKELETON_STATS_KEY to stats,
            PLAYER_STATS_KEY to playerStats
        )

        return getActionPlan(worldState, actions, goal)!!
    }
}