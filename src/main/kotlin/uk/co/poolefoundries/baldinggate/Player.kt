package uk.co.poolefoundries.baldinggate

import com.badlogic.ashley.core.*
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Input.Keys.*
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.utils.Array
import uk.co.poolefoundries.baldinggate.ai.actions.MoveAction
import uk.co.poolefoundries.baldinggate.model.PlayerComponent
import uk.co.poolefoundries.baldinggate.model.SkeletonComponent
import uk.co.poolefoundries.baldinggate.skeleton.SKELETON_POSITION_KEY
import uk.co.poolefoundries.baldinggate.skeleton.SkeletonSystem

val UP = PositionComponent(0, 1)
val DOWN = PositionComponent(0, -1)
val LEFT = PositionComponent(-1, 0)
val RIGHT = PositionComponent(1, 0)

object PlayerInputHandler : InputAdapter() {
    private val actions = listOf(
        MoveAction(SKELETON_POSITION_KEY, UP),
        MoveAction(SKELETON_POSITION_KEY, DOWN),
        MoveAction(SKELETON_POSITION_KEY, LEFT),
        MoveAction(SKELETON_POSITION_KEY, RIGHT),
        FuckAll
    )

    private val validActions = actions
    // todo get valid actions

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            W -> PlayerSystem.moveUp()
            A -> PlayerSystem.moveLeft()
            S -> PlayerSystem.moveDown()
            D -> PlayerSystem.moveRight()
            SPACE -> PlayerSystem.FuckAll()
            1 -> validActions[0]
            2 -> validActions[1]
            3 -> validActions[2]
        }

        // TODO: (from Appliction.kt) Add a turn based entity system in order to make all updates to game at the end of a turn
        // TODO: make this such that all systems that should act are acting here
        SkeletonSystem.act()
        return true
    }
}

object PlayerSystem : EntitySystem() {
    private val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)

    private var walls = ImmutableArray(Array<Entity>())
    private var players = ImmutableArray(Array<Entity>())
    private var skeletons = ImmutableArray(Array<Entity>())
    // todo replace the wasd controls with text input options
    // cycle through players to ask what to do by printing all possible actions
    private fun move(direction: PositionComponent) {
        players.forEach { player ->
            val pos = positionMapper.get(player)
            // make a list of valid actions, display them and then, on button press,
            val newPos = PositionComponent(pos.x + direction.x, pos.y + direction.y)

            if (walls.none { positionMapper.get(it) == newPos } && skeletons.none { positionMapper.get(it) == newPos }) {
                player.add(newPos)
            } else if (skeletons.any { positionMapper.get(it) == newPos }){
                // TODO Attack
            } else{
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

    fun FuckAll(){

    }
}