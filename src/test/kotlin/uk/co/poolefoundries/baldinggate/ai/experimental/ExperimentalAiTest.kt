package uk.co.poolefoundries.baldinggate.ai.experimental

import org.junit.Test
import uk.co.poolefoundries.baldinggate.ai.*
import uk.co.poolefoundries.baldinggate.ai.pathfinding.AStarNode
import uk.co.poolefoundries.baldinggate.core.PositionComponent
import uk.co.poolefoundries.baldinggate.core.Roll
import uk.co.poolefoundries.baldinggate.core.Stats
import uk.co.poolefoundries.baldinggate.systems.PathfinderSystem
import uk.co.poolefoundries.baldinggate.systems.enemy.*
import uk.co.poolefoundries.baldinggate.systems.enemy.ai.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ExperimentalAiTest {
    fun worldMap() : Collection<AStarNode> {
        val list = mutableListOf<AStarNode>()
        for (i  in 0..100) {
            for (j in 0..100) {
                list.add(AStarNode(i, j))
            }
        }
        return list
    }
    fun basicMob(id : String, pos: PositionComponent) : MobInfo {
        return MobInfo(
            id,
            pos,
            Stats(10, 10, 5, 2, 2, Roll(listOf(6), 0))
        )
    }

    private fun worldState(players: Collection<MobInfo>, enemies: Collection<MobInfo>): WorldState {
        return mutableMapOf<String, Any>()
            .setPlayerIds(players.map { it.id })
            .setEnemyIds(enemies.map { it.id })
            .setMobInfo(players.union(enemies))
            .setWorldMap(worldMap())
    }


    @Test
    fun testActions() {
        val players = listOf(basicMob("player1", PositionComponent(0, 0)))
        val enemies = listOf(basicMob("skel1", PositionComponent(3, 3)))

        val actions = ExperimentalAi.new(players, enemies, Win).actions
        assertEquals(4, actions.size)

        val moveAction = actions.filterIsInstance<MoveTowards>().first()
        assertEquals("player1", moveAction.targetId)
        assertEquals("skel1", moveAction.selfId)

        val attackAction = actions.filterIsInstance<MoveTowards>().first()
        assertEquals("player1", attackAction.targetId)
        assertEquals("skel1", attackAction.selfId)

        assertTrue(actions.filterIsInstance<EndTurn>().isNotEmpty())
        assertTrue(actions.filterIsInstance<Win>().isNotEmpty())
    }

    @Test
    fun testMoveAction() {
        val playerPos = PositionComponent(0, 0)

        val players = listOf(basicMob("player1", playerPos))
        val enemies = listOf(basicMob("skel1", PositionComponent(0, 20)))

        val actions = ExperimentalAi.new(players, enemies, Win).actions
        var state = worldState(players, enemies)
        fun info() = state.getMobInfo("skel1")

        val moveAction = actions.filterIsInstance<MoveTowards>().first()
        val endTurn = actions.filterIsInstance<EndTurn>().first()


        val pos = moveAction.getNewPos(state, enemies.first(), players.first())
        assertEquals(15, playerPos.manhattanDistance(pos))
        assertTrue(moveAction.prerequisitesMet(state))

        // Move once
        state = moveAction.update(state)
        assertEquals(15, playerPos.manhattanDistance(info().pos))
        assertEquals(1, info().stats.currentAP)
        assertTrue(moveAction.prerequisitesMet(state))

        // Move second time (now have 0 AP)
        state = moveAction.update(state)
        assertEquals(10, playerPos.manhattanDistance(info().pos))
        assertEquals(0, info().stats.currentAP)
        assertFalse(moveAction.prerequisitesMet(state))

        state = endTurn.update(state)
        assertEquals(2, info().stats.currentAP)
        assertTrue(moveAction.prerequisitesMet(state))
    }

    @Test
    fun testAttackAction() {
        val players = listOf(basicMob("player1", PositionComponent(0, 0)))
        val enemies = listOf(basicMob("skel1", PositionComponent(2, 0)))

        val actions = ExperimentalAi.new(players, enemies, Win).actions
        var state = worldState(players, enemies)

        fun info() = state.getMobInfo("skel1")
        fun targetInfo() = state.getMobInfo("player1")


        val moveAction = actions.filterIsInstance<MoveTowards>().first()
        val attackAction = actions.filterIsInstance<Attack>().first()

        assertFalse(attackAction.prerequisitesMet(state))
        state = moveAction.update(state)
        assertEquals(1, info().stats.currentAP)
        assertTrue(attackAction.prerequisitesMet(state))

        state = attackAction.update(state)
        assertEquals(0, info().stats.currentAP)
        assertFalse(attackAction.prerequisitesMet(state))
        assertEquals(7, targetInfo().stats.hitPoints)
    }
}