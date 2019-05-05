package uk.co.poolefoundries.baldinggate.model

import kotlin.test.Test
import kotlin.test.assertEquals

class LevelTest {
    @Test
    fun testLoadLevel(){
        val json = LevelTest::class.java.getResourceAsStream("test-level.json")
                .bufferedReader().use{ it.readText() }
        val level = loadLevelJson(json)

        assertEquals("Meeting Tavern", level.name)
        assertEquals(25, level.tiles.size)
        assertEquals(4, level.tileTypes.size)

    }
}