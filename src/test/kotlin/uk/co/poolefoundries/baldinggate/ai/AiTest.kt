package uk.co.poolefoundries.baldinggate.ai

import kotlin.test.assertEquals
import kotlin.test.Test

class AiTest {
    @Test
    fun testing() {
        val action = GoalAction()
        val goal = Goal(action, 1.0)
        val plan = getActionPlan(emptyMap(), listOf(action), goal)
        assertEquals(1, plan?.actions?.size)
    }

    @Test
    fun fireTest() {
        val actions = listOf(MakeFire, GetTinderbox, GetWood)
        val goal = Goal(MakeFire, 1.0)
        val plan = getActionPlan(emptyMap(), actions, goal)
        assertEquals(80.0, plan?.cost)

    }
}


class GoalAction : Action {
    override fun cost(state: WorldState) = 5.0

    override fun prerequisitesMet(state: WorldState): Boolean {
        return true
    }

    override fun update(state: WorldState): WorldState {
        return state
    }

    override fun toString(): String {
        return "GoalAction"
    }

}

object MakeFire : Action {
    override fun cost(state: WorldState) = 20.0

    override fun prerequisitesMet(state: WorldState): Boolean {
        return state.get("tinderbox", false) && state.get("wood", 0) >= 3
    }

    override fun update(state: WorldState): WorldState {
        val newState = state.toMutableMap()
        newState["wood"] = newState.get("wood", 0) - 3
        return newState
    }


    override fun toString(): String {
        return "MakeFire"
    }

}

object GetWood : Action {
    override fun cost(state: WorldState) = 10.0

    override fun prerequisitesMet(state: WorldState) = true

    override fun update(state: WorldState): WorldState {
        val newState = state.toMutableMap()
        newState["wood"] = newState.get("wood", 0) + 1
        return newState
    }

    override fun toString(): String {
        return "GetWood"
    }
}

object GetTinderbox : Action {
    override fun cost(state: WorldState) = 30.0

    override fun prerequisitesMet(state: WorldState) = true

    override fun update(state: WorldState): WorldState {
        val newState = state.toMutableMap()
        newState["tinderbox"] = true
        return newState
    }

    override fun toString(): String {
        return "GetTinderbox"
    }
}