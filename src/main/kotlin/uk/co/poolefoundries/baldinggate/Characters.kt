package uk.co.poolefoundries.baldinggate

import com.badlogic.ashley.core.*
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.utils.Array
import javafx.geometry.Pos
import uk.co.poolefoundries.baldinggate.model.PlayerComponent
import uk.co.poolefoundries.baldinggate.model.SkeletonComponent
import uk.co.poolefoundries.baldinggate.model.Stats
import kotlin.random.Random


data class Roll(val die: List<Int>, val mod: Int) {
    fun roll() = die.map { Random.nextInt(it) }.sum() + mod
}

data class StatsComponent(val stats: Stats) : Component



object SkeletonAI {

}



