package uk.co.poolefoundries.baldinggate.ai

typealias WorldState = Map<String, Any>


data class Goal(val action: Action, val priority: Double)

interface Action {
    val cost : Double
    fun prerequisite(state : WorldState) : Boolean
    fun update(state: WorldState) : WorldState

    fun apply(branch: Branch) : Branch {
        val newState = update(branch.state)
        return Branch(branch.actions+this, newState, branch.cost + cost)
    }
}

data class Branch(val actions: List<Action>, val state: WorldState, val cost: Double)

fun List<Action>.applyValidActions(branch: Branch) =
        this.filter { it.prerequisite(branch.state) }.map { it.apply(branch) }


fun getActionPlan(state: WorldState, actions: List<Action>, primaryGoal: Goal): Branch? {
    val openSet = actions.applyValidActions(Branch(emptyList(), state, 0.0))

    return getActionPlan(actions, primaryGoal, openSet, null)
}

fun getActionPlan(actions: List<Action>, primaryGoal: Goal, openSet: List<Branch>, bestOption: Branch?): Branch? {
    val goalAchievedBranches = (openSet.filter { it.actions.last() == primaryGoal.action } + bestOption).filterNotNull()
    var newOpenSet = openSet - goalAchievedBranches


    val newBestOption = goalAchievedBranches.minBy { it.cost }
    if (newBestOption != null) newOpenSet = newOpenSet.filter{it.cost < newBestOption.cost}


    newOpenSet = newOpenSet.flatMap { actions.applyValidActions(it) }
    return if (newOpenSet.isNotEmpty()) {
        getActionPlan(actions, primaryGoal, newOpenSet, newBestOption)
    } else {
        newBestOption
    }
}

