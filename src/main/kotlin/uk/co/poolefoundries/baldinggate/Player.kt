package uk.co.poolefoundries.baldinggate

import com.badlogic.ashley.core.*
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Input.Keys.*
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.utils.Array
import uk.co.poolefoundries.baldinggate.ai.actions.MoveAction
import uk.co.poolefoundries.baldinggate.model.PlayerComponent
import uk.co.poolefoundries.baldinggate.model.SkeletonComponent
import uk.co.poolefoundries.baldinggate.skeleton.POSITION_KEY
import uk.co.poolefoundries.baldinggate.skeleton.SkeletonSystem
import kotlin.random.Random

val UP = PositionComponent(0, 1)
val DOWN = PositionComponent(0, -1)
val LEFT = PositionComponent(-1, 0)
val RIGHT = PositionComponent(1, 0)

//object PlayerInputHandler : InputAdapter() {
//
//
//    override fun keyDown(keycode: Int): Boolean {
//        when (keycode) {
//            W -> PlayerSystem.moveUp()
//            A -> PlayerSystem.moveLeft()
//            S -> PlayerSystem.moveDown()
//            D -> PlayerSystem.moveRight()
//        }
//
//        // TODO: (from Appliction.kt) Add a turn based entity system in order to make all updates to game at the end of a turn
//        // TODO: make this such that all systems that should act are acting here
//        SkeletonSystem.act()
//        return true
//    }
//}

object PlayerSystem : EntitySystem() {
    private val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)
    private val actions = listOf(
        MoveAction(POSITION_KEY, UP),
        MoveAction(POSITION_KEY, DOWN),
        MoveAction(POSITION_KEY, LEFT),
        MoveAction(POSITION_KEY, RIGHT),
        FuckAll
    )
    private var walls = ImmutableArray(Array<Entity>())
    private var players = ImmutableArray(Array<Entity>())
    private var skeletons = ImmutableArray(Array<Entity>())
    // todo replace the wasd controls with text input options
    // cycle through players to ask what to do by printing all possible actions
    private fun move(direction: PositionComponent) {
        players.forEach { player ->
            val pos = positionMapper.get(player)

            val newPos = PositionComponent(pos.x + direction.x, pos.y + direction.y)

            if (walls.none { positionMapper.get(it) == newPos } && skeletons.none { positionMapper.get(it) == newPos }) {
                player.add(newPos)
            } else {
                println("something blocking movement")
            }
        }
    }

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        walls = engine.getEntitiesFor(Family.all(WallComponent::class.java, PositionComponent::class.java).get())
        players = engine.getEntitiesFor(
            Family.all(
                PlayerComponent::class.java,
                PositionComponent::class.java,
                StatsComponent::class.java
            ).get()
        )
        skeletons = engine.getEntitiesFor(
            Family.all(
                SkeletonComponent::class.java,
                PositionComponent::class.java,
                StatsComponent::class.java
            ).get()
        )
    }


    fun moveUp() {
        move(UP)
    }

    fun moveDown() {
        move(DOWN)
    }

    fun moveLeft() {
        move(LEFT)
    }

    fun moveRight() {
        move(RIGHT)
    }
}