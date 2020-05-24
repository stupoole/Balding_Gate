package uk.co.poolefoundries.baldinggate.ai

typealias WorldState = Map<String, Any>
typealias Plan = List<Action>
data class Branch(val actions: Plan, val state: WorldState, val cost: Double)

fun (WorldState).get(key: String, default: Int): Int {
    return this.getOrDefault(key, default) as Int
}

fun (WorldState).get(key: String, default: Boolean): Boolean {
    return this.getOrDefault(key, default) as Boolean
}

fun (WorldState).withValue(key: String, value: Any): WorldState {
    val newState = this.toMutableMap()
    newState[key] = value
    return newState.toMap()
}

data class Goal(val action: Action, val priority: Double)

interface Action {
    // How much time this action should take in arbitrary units
    fun cost(state: WorldState): Double

    // If for the given world state this action can be performed at all
    fun prerequisite(state: WorldState): Boolean

    // Update the world state as if this action was performed
    fun update(state: WorldState): WorldState

    // Produce a new branch where this action has been performed
    fun apply(branch: Branch): Branch {
        val newState = update(branch.state)
        return Branch(branch.actions + this, newState, branch.cost + cost(branch.state))
    }

    override fun toString(): String

}



fun Plan.applyValidActions(branch: Branch) =
    this.getValidActions(branch.state).map { it.apply(branch) }

fun Plan.getValidActions(worldState: WorldState) =
    this.filter { it.prerequisite(worldState) }


fun getActionPlan(state: WorldState, actions: Plan, primaryGoal: Goal): Branch? {
    val openSet = actions.applyValidActions(Branch(emptyList(), state, 0.0))

    return getActionPlan(actions, primaryGoal, openSet, null)
}

fun getActionPlan(actions:Plan, primaryGoal: Goal, openSet: List<Branch>, bestOption: Branch?): Branch? {
    val goalAchievedBranches = (openSet.filter { it.actions.last() == primaryGoal.action } + bestOption).filterNotNull()
    var newOpenSet = openSet - goalAchievedBranches


    val newBestOption = goalAchievedBranches.minBy { it.cost }
    if (newBestOption != null) newOpenSet = newOpenSet.filter { it.cost < newBestOption.cost }


    newOpenSet = newOpenSet.flatMap { actions.applyValidActions(it) }
    return if (newOpenSet.isNotEmpty()) {
        getActionPlan(actions, primaryGoal, newOpenSet, newBestOption)
    } else {
        newBestOption
    }
}

