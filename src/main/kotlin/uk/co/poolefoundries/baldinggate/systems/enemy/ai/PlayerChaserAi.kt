package uk.co.poolefoundries.baldinggate.systems.enemy.ai

import uk.co.poolefoundries.baldinggate.ai.*
import java.lang.RuntimeException

class PlayerChaserAi(private var mobs: List<Mob>, private val goalAction: Action) : Ai {
    data class Mob(var id : String, var attacks: List<Attack>, var moves : List<MoveTowards>)

    private fun (Mob).canPerformAction(state: WorldState) : Boolean {
        return goalAction.prerequisitesMet(state) ||
                attacks.any { it.prerequisitesMet(state) } ||
                moves.any{ it.prerequisitesMet(state) }
    }

    private fun (Mob).getNextAction(state : WorldState) : Action {
        if (goalAction.prerequisitesMet(state)){
            return goalAction
        }

        val selfInfo = state.getMobInfo(id)
        val attack = attacks.filter { it.prerequisitesMet(state) }.minBy {
            val targetInfo = state.getMobInfo(it.targetId)
            selfInfo.pos.manhattanDistance(targetInfo.pos)
        }
        if (attack != null) {
            return attack
        }

        val move = moves.filter { it.prerequisitesMet(state) }.minBy {
            val targetInfo = state.getMobInfo(it.targetId)
            selfInfo.pos.manhattanDistance(targetInfo.pos)
        }
        if (move != null) {
            return move
        }
        throw RuntimeException("Can't perform any action")
    }

    override fun getActionPlan(state: WorldState): Plan {
        val plan = mutableListOf<Action>()
        var currentState = state
        while (true) {
            val pendingMob = mobs.find { it.canPerformAction(currentState) }
            if (pendingMob == null) {
                plan.add(EndTurn)
                return plan
            }

            val action = pendingMob.getNextAction(currentState)
            plan.add(action)
            if (action == goalAction) {
                return plan
            }
            currentState = action.update(currentState)
        }
    }

    companion object {
        fun new(players: Collection<MobInfo>, enemies: Collection<MobInfo>, goalAction: Action) : PlayerChaserAi {
            val mobs = enemies.map { self ->
                val attacks = players.map { player ->
                    Attack(
                        self.id,
                        player.id
                    )
                }
                val moves = players.map { player ->
                    MoveTowards(
                        self.id,
                        player.id
                    )
                }
                Mob(
                    self.id,
                    attacks,
                    moves
                )
            }
            return PlayerChaserAi(mobs, goalAction)
        }
    }
}