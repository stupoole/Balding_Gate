package uk.co.poolefoundries.baldinggate.core

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.ScreenViewport
import uk.co.poolefoundries.baldinggate.screens.MainMenuScreen
import uk.co.poolefoundries.baldinggate.skeleton.AttackPlayer
import uk.co.poolefoundries.baldinggate.skeleton.MoveTowardsPlayer
import uk.co.poolefoundries.baldinggate.skeleton.SkeletonAI
import uk.co.poolefoundries.baldinggate.skeleton.Win

data class SelectedEntity(
    var entity: Entity? = null,
    var position: PositionComponent? = null,
    var actionPoints: Int,
    var speed: Int
) {
    fun clear() {
        this.entity?.add(ColorComponent(Color.WHITE))
        this.entity = null
        this.position = null
        this.actionPoints = 0
        this.speed = 0
    }

    fun anyNull(): Boolean {
        return this.entity == null || this.position == null
    }

    fun noNull(): Boolean {
        return this.entity != null && this.position != null
    }
}

data class Direction(val x: Int, val y: Int) {
    operator fun plus(other: Direction): Direction {
        return Direction(this.x + other.x, this.y + other.y)
    }

    operator fun minus(other: Direction): Direction {
        return Direction(this.x - other.x, this.y - other.y)
    }

    operator fun times(other: Direction): Direction {
        return Direction(this.x * other.x, this.y * other.y)
    }

    operator fun times(multiple: Int): Direction {
        return Direction(this.x * multiple, this.y * multiple)
    }

    operator fun div(other: Direction): Direction {
        return Direction(this.x / other.x, this.y / other.y)
    }
}

data class Animation(val entity: Entity, var positions: Array<PositionComponent>, var progress: Float = 0F)

val PAN_UP = Direction(0, 1)
val PAN_DOWN = Direction(0, -1)
val PAN_LEFT = Direction(-1, 0)
val PAN_RIGHT = Direction(1, 0)
val PAN_NONE = Direction(0, 0)

class BaldingGateGame : Game() {

    lateinit var batch: SpriteBatch
    var engine = Engine()
    var camera = OrthographicCamera()
    var viewport = ScreenViewport(camera)
    val tileSize = 25F

    var walls = ImmutableArray(Array<Entity>())
    var floors = ImmutableArray(Array<Entity>())
    var players = ImmutableArray(Array<Entity>())
    var enemies = ImmutableArray(Array<Entity>())
    var mobs = ImmutableArray(Array<Entity>())
    val colorMapper = ComponentMapper.getFor(ColorComponent::class.java)
    var cameraMoveDirection = Direction(0, 0)

    var pendingAnimations = Array<Animation>()
    var isAnimating = false
    private val animationDuration = 0.1F

    private val greenTiles = Array<Entity>()
    private val redTiles = Array<Entity>()
    private var selectedEntity = SelectedEntity(null, null, 0, 0)
    private val panSpeed = 100F

    private val positionMapper: ComponentMapper<PositionComponent> =
        ComponentMapper.getFor(PositionComponent::class.java)
    private val statsMapper: ComponentMapper<StatsComponent> = ComponentMapper.getFor(StatsComponent::class.java)


    override fun create() {
        batch = SpriteBatch()
        engine
        camera
        viewport
        setScreen(MainMenuScreen(this))
        viewport.apply()
        camera.update()

    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun dispose() {
        //todo add disposes here for batch etc
    }

    //    override fun render() {
//        super.render()
//    }
    fun cameraMove(delta: Float) {
        camera.translate(cameraMoveDirection.x * delta * panSpeed, cameraMoveDirection.y * delta * panSpeed)
    }

    fun update() {
        walls = engine.getEntitiesFor(Family.all(WallComponent::class.java, PositionComponent::class.java).get())
        floors = engine.getEntitiesFor(Family.all(FloorComponent::class.java, PositionComponent::class.java).get())
        players = engine.getEntitiesFor(
            Family.all(
                PlayerComponent::class.java,
                PositionComponent::class.java,
                StatsComponent::class.java
            ).get()
        )
        enemies = engine.getEntitiesFor(
            Family.all(
                SkeletonComponent::class.java,
                PositionComponent::class.java,
                StatsComponent::class.java
            ).get()
        )
        mobs = engine.getEntitiesFor(Family.all(PositionComponent::class.java, StatsComponent::class.java).get())
    }


    fun leftClick(x: Int, y: Int) {
        val tilePos = PositionComponent(
            ((x.toFloat() + camera.position.x - viewport.worldWidth / 2) / tileSize).toInt(),
            (((camera.position.y + viewport.worldHeight / 2) - y.toFloat()) / tileSize).toInt()
        )
        selectedEntity.entity?.add(ColorComponent(Color.WHITE))
        val clickedMobs = mobs.filter { positionMapper.get(it) == tilePos }
        when (clickedMobs.size) {
            0 -> {
                clearColoredTiles(); selectedEntity.clear()
            }
            1 -> {
                selectMob(clickedMobs.first())
            }
            else -> {
                println("too many mobs to sort through damnit")
                // TODO prefer players.
            }

        }
    }


    fun rightClick(x: Int, y: Int) {
        // TODO implement attack instead of just always move. Should probably make methods to handle moving, attacking
        //  and other things which are called by this method
        val tilePos = PositionComponent(
            ((x.toFloat() + camera.position.x - viewport.worldWidth / 2) / tileSize).toInt(),
            (((camera.position.y + viewport.worldHeight / 2) - y.toFloat()) / tileSize).toInt()
        )
        if ( selectedEntity.anyNull()){return}
        if (
            positionMapper.get(selectedEntity.entity) != tilePos &&
            players.contains(selectedEntity.entity) &&
            selectedEntity.actionPoints > 0
        ) {
            clearColoredTiles()
            playerMove(tilePos)
        } else {
            println("Either you tried to move to the same spot or tried to move a skeleton or had no AP...")
        }

    }

    fun nextPlayer() {
        val current = players.indexOf(selectedEntity.entity)
        val next = (current + 1) % players.size()
        selectMob(players[next])
    }

    fun pauseMenu() {
        // TODO implement pause menu
        // PAN_NONE assigned here so that if a keyup is missed, camera can be stopped with esc
        cameraMoveDirection = PAN_NONE
    }

    fun endTurn() {
        enemyActions()
        players.forEach { player ->
            val stats = statsMapper.get(player).stats
            player.add(StatsComponent(stats.copy(currentAP = stats.maxAP)))
        }
        enemies.forEach { enemy ->
            val stats = statsMapper.get(enemy).stats
            enemy.add(StatsComponent(stats.copy(currentAP = stats.maxAP)))
        }
        clearGreenTiles()
        clearRedTiles()
        selectedEntity.entity?.add(ColorComponent(Color.WHITE))
        selectedEntity.clear()

    }

    fun animationStep(delta: Float) {
        pendingAnimations.forEachIndexed { index, animation ->
            val entity = animation.entity
            val progress = animation.progress + (delta / animationDuration)
            val start = animation.positions.first()
            val end = animation.positions[1]
//            val direction = start.direction(end)

            if (progress > 1) {
                entity.add(end)
                animation.positions.removeIndex(0)
                pendingAnimations[index].positions = animation.positions
                pendingAnimations[index].progress = 0F
            } else {
                pendingAnimations[index].progress = progress
            }
            if (animation.positions.size < 2) {
                // todo set colors
                pendingAnimations.removeIndex(index)
                selectedEntity?.entity?.let { selectMob(it) }
                if (selectedEntity.actionPoints <= 0) {
                    clearGreenTiles()
                } else {
                    selectedEntity.entity?.let { selectMob(it) }
                }
            }
        }



    }

    private fun selectMob(mob: Entity) {
        selectedEntity = SelectedEntity(
            mob,
            positionMapper.get(mob),
            statsMapper.get(mob).stats.currentAP,
            statsMapper.get(mob).stats.speed
        )
        clearColoredTiles()
        if (players.contains(mob)) {
            selectedEntity.entity?.add(ColorComponent(Color.LIME))
            if (selectedEntity.actionPoints > 0) {
                setGreenTiles(selectedEntity.position!!, selectedEntity.speed)
            } else {
                clearColoredTiles()
            }
        } else if (enemies.contains(mob)) {
            selectedEntity.entity?.add(ColorComponent(Color.ORANGE))
            if (selectedEntity.actionPoints > 0) {
                setRedTiles(selectedEntity.position!!, selectedEntity.speed)
            } else {
                clearColoredTiles()
            }
        } else {
            clearColoredTiles()
        }
    }

    private fun playerMove(target: PositionComponent) {
        val distance = selectedEntity.position!!.gridWiseDistance(target)
        var tempPos = selectedEntity.position!!
        val positions = Array<PositionComponent>()
        positions.add(tempPos)
        // TODO: pathfinding
        for (step in 0 until minOf(selectedEntity.speed, distance)) {
            tempPos += tempPos.direction(target)
            positions.add(tempPos)
        }
        pendingAnimations.add(selectedEntity.entity?.let { Animation(it, positions) })
        selectedEntity.actionPoints -= 1
        selectedEntity.entity!!.add(
            StatsComponent(
                statsMapper.get(selectedEntity.entity).stats.copy(currentAP = selectedEntity.actionPoints)
            )
        )
    }

    fun setGreenTiles(center: PositionComponent, range: Int) {
        clearColoredTiles()
        floors.forEach { tile ->
            if (positionMapper.get(tile).gridWiseDistance(center) <= range) {
                greenTiles.add(tile)
                tile.add(ColorComponent(Color.GREEN))
            } else {
                tile.add(ColorComponent(Color.WHITE))
            }
        }
    }

    fun setRedTiles(center: PositionComponent, range: Int) {
        clearColoredTiles()
        floors.forEach { tile ->
            if (positionMapper.get(tile).gridWiseDistance(center) <= range) {
                redTiles.add(tile)
                tile.add(ColorComponent(Color.RED))
            } else {
                tile.add(ColorComponent(Color.WHITE))
            }
        }
    }


    fun clearGreenTiles() {
        greenTiles.forEach {
            it.add(ColorComponent(Color.WHITE))
        }
        greenTiles.clear()
    }

    fun clearRedTiles() {
        redTiles.forEach {
            it.add(ColorComponent(Color.WHITE))
        }
        redTiles.clear()
    }

    fun clearColoredTiles() {
        clearGreenTiles()
        clearRedTiles()
//        mobs.forEach { mob -> mob.add(ColorComponent(Color.WHITE)) }
    }

    fun enemyActions() {
        // TODO: somehow handle multiple players
        enemies.forEach { enemy ->
            var playerPos: PositionComponent
            var playerStats: Stats
            val player: Entity
            player = players.first()
            // TODO: convert players in to a simplified representation of a player?
            // TODO replace this action points system to do one action per skeleton each, render then do the next?
            var stats = statsMapper.get(enemy).stats
            val speed = stats.speed
            var pos = positionMapper.get(enemy)
            val movePositions = Array<PositionComponent>()

            for (action in 0 until stats.currentAP) {
                playerPos = positionMapper.get(player)
                playerStats = statsMapper.get(player).stats

                val actionPlan = SkeletonAI.getPlan(pos, playerPos, stats, playerStats)
                when (actionPlan.actions.first()) {
                    is MoveTowardsPlayer -> {
                        //TODO add collision detection
                        val distance = pos.gridWiseDistance(playerPos)
                        var tempPos = pos
                        movePositions.add(tempPos)
                        for (step in 0 until minOf(speed, pos.gridWiseDistance(playerPos)-1)) {
                            tempPos += tempPos.direction(playerPos)
                            movePositions.add(tempPos)
                        }
                        pos = tempPos

                    }
                    is AttackPlayer -> {
                        player.add(
                            StatsComponent(
                                statsMapper.get(player).stats.copy(
                                    hitPoints = playerStats.hitPoints - stats.attack.roll()
                                )
                            )
                        )
                        println("Hitpoints: " + statsMapper.get(player).stats.hitPoints)
                    }
                    is Win -> {
                        player.remove(VisualComponent::class.java)
                        println("HAHAHAHAHA YOU DIED!")
                    }
                }
                enemy.add(StatsComponent(stats.copy(currentAP = stats.currentAP - 1)))
                stats = statsMapper.get(enemy).stats
            }
            if (movePositions.size > 2) {
            pendingAnimations.add(Animation(enemy, movePositions))}
        }
    }



}

