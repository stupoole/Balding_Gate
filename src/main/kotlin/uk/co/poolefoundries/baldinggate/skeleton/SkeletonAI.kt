package uk.co.poolefoundries.baldinggate.skeleton

import uk.co.poolefoundries.baldinggate.*
import uk.co.poolefoundries.baldinggate.ai.*
import uk.co.poolefoundries.baldinggate.ai.actions.MoveAction
import uk.co.poolefoundries.baldinggate.ai.actions.getPosition

val POSITION_KEY = "position"
val PLAYER_POSITION_KEY = "player"

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
        return state
        // TODO: implement a health/damage system
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

    fun getNewPosition(pos: PositionComponent, playerPos: PositionComponent): PositionComponent {
        if (!actions.contains(goal.action)) {
            throw RuntimeException("Action ${goal.action} not found in list of available actions")
        }

        val worldState: WorldState = mapOf(PLAYER_POSITION_KEY to playerPos, POSITION_KEY to pos)
        val plan = getActionPlan(worldState, actions, goal)!!
//        implement pathfinding
        val action = plan.actions.first()
        val newState = action.update(worldState)

        return newState.getPosition(POSITION_KEY)
    }
}