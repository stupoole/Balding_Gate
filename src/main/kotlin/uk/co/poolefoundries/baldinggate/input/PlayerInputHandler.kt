package uk.co.poolefoundries.baldinggate.input

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Array
import uk.co.poolefoundries.baldinggate.PlayerSystem
import uk.co.poolefoundries.baldinggate.core.*
import uk.co.poolefoundries.baldinggate.skeleton.SkeletonSystem

class PlayerInputHandler(val game: BaldingGateGame) : InputAdapter() {

    private var lastX = 0f
    private var lastY = 0f
    private val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)
    private val statsMapper = ComponentMapper.getFor(StatsComponent::class.java)
    private val colorMapper = ComponentMapper.getFor(ColorComponent::class.java)
    private var floors = game.engine.getEntitiesFor(
        Family.all(
            FloorComponent::class.java,
            PositionComponent::class.java
        ).get()
    )
    private val walls = game.engine.getEntitiesFor(
        Family.all(
            WallComponent::class.java,
            PositionComponent::class.java
        ).get()
    )

    private val players = game.engine.getEntitiesFor(
        Family.all(
            PlayerComponent::class.java,
            PositionComponent::class.java,
            StatsComponent::class.java
        ).get()
    )
    private val skeletons = game.engine.getEntitiesFor(
        Family.all(
            SkeletonComponent::class.java,
            PositionComponent::class.java,
            StatsComponent::class.java
        ).get()
    )
    private var selected: Entity? = null
    private val greenTiles = Array<Entity>()
    // todo get list of valid actions that aren't movement and display on UI

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            // TODO replace player movement with camera movement
            Input.Keys.ENTER -> endTurn()
            Input.Keys.SPACE -> PlayerSystem.FuckAll()

        }

        // TODO: (from Appliction.kt) Add a turn based entity system in order to make all updates to game at the end
        // of a turn
        // TODO: make this such that all systems that should act are acting here

        return true
    }

    override fun touchDown(x: Int, y: Int, pointer: Int, button: Int): Boolean {
        // TODO: attack skeletons with certain button presses
        val tilePos = PositionComponent(
            ((x + game.camera.position.x - game.viewport.worldWidth / 2) / game.tileSize).toInt(),
            (((game.camera.position.y + game.viewport.worldHeight / 2) - y.toFloat()) / game.tileSize).toInt()
        )
        // Button: 0-> left, 1-> right, 2-> middle, 4 -> mouse forward, 3 -> mouse back
        when (button) {
            0 -> {
                lastX = x.toFloat()
                lastY = y.toFloat()
                selected = players.find { positionMapper.get(it) == tilePos }
                if (selected == null) {
                    clearGreenTiles()
                } else {
                    val pos = positionMapper.get(selected)
                    val speed = statsMapper.get(selected).stats.speed
                    val actionPoints = statsMapper.get(selected).stats.currentAP
                    greenTiles(pos, speed, actionPoints)
                }
                // TODO: if this successfully selects a player, should have a sprite change/shader


                return true
            }
            1 -> {
                // TODO implement attack instead of move. Should probably make methods to handle moving, attacking and
                //  other things which are called by this handler
                if (selected != null) {
                    val player = selected!!
                    val stats = statsMapper.get(player).stats
                    val speed = player.getComponent(StatsComponent::class.java).stats.speed
                    var AP = stats.currentAP
                    return if (AP > 0) {
                        var playerPos = player.getComponent(PositionComponent::class.java)
                        val distance = playerPos.gridWiseDistance(tilePos)
                        // TODO: pathfinding
                        for (step in 0 until minOf(speed, distance.toInt())) {
                            player.add(playerPos + playerPos.direction(tilePos))
                            playerPos = player.getComponent(PositionComponent::class.java)
                        }

                        AP -= 1
                        player.add(StatsComponent(stats.copy(currentAP = AP)))
                        greenTiles(playerPos, speed, AP)
                        true
                    } else {
                        println("NO AP")
                        false
                    }
                }

            }

        }
        return false
    }

    override fun touchDragged(x: Int, y: Int, pointer: Int): Boolean {
        println(pointer)
        game.camera.translate(-(x - lastX), y - lastY)

        lastX = x.toFloat()
        lastY = y.toFloat()
        // TODO replace touchDragged with wasd. Rotate with q and e?
        return true
    }

    private fun clearGreenTiles() {
        greenTiles.forEach {
            it.add(ColorComponent(Color.WHITE))
        }
        greenTiles.clear()
    }

    private fun greenTiles(pos: PositionComponent, speed: Int, AP: Int) {
        greenTiles.clear()
        floors.forEach { tile ->
            if (positionMapper.get(tile).gridWiseDistance(pos) <= speed && AP > 0) {
                greenTiles.add(tile)
                tile.add(ColorComponent(Color.GREEN))
            } else {
                tile.add(ColorComponent(Color.WHITE))
            }
        }
    }

    // TODO: move the end turn method somewhere more sensible.
    private fun endTurn() {
        SkeletonSystem.act()
        players.forEach { player ->
            val stats = statsMapper.get(player).stats
            player.add(StatsComponent(stats.copy(currentAP = stats.maxAP)))
        }
        skeletons.forEach { skeleton ->
            val stats = statsMapper.get(skeleton).stats
            skeleton.add(StatsComponent(stats.copy(currentAP = stats.maxAP)))
        }
        clearGreenTiles()
        selected = null

    }

}