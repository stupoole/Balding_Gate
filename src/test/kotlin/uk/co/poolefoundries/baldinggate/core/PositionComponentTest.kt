package uk.co.poolefoundries.baldinggate.core

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PositionComponentTest {
    @Test
    fun testGridDistance(){
        run {
            val pos1 = PositionComponent(0, 0)
            val pos2 = PositionComponent(0, 1)

            assertEquals(1, pos1.manhattanDistance(pos2))
            assertEquals(1, pos2.manhattanDistance(pos1))
            assertEquals(PositionComponent(0, 1), pos1.direction(pos2))
            assertEquals(PositionComponent(0, -1), pos2.direction(pos1))
        }
        run {
            val pos1 = PositionComponent(0, 0)
            val pos2 = PositionComponent(1, 1)
            assertEquals(2, pos1.manhattanDistance(pos2))
            assertEquals(2, pos2.manhattanDistance(pos1))
            assertNotEquals(PositionComponent(0, 0), pos1.direction(pos2))
            assertNotEquals(PositionComponent(0, 0), pos2.direction(pos1))
        }
    }

    @Test
    fun testMoveTowards() {
        val pos1 = PositionComponent(0, 0)
        val pos2 = PositionComponent(1, 1)

        assertEquals(2, pos1.manhattanDistance(pos2))
        val newPos = pos2.moveTowards(pos1, 5)
        assertEquals(1, newPos.manhattanDistance(pos2))
    }

}