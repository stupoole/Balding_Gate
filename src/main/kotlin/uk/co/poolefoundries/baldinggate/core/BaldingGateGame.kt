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
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.ScreenViewport
import javafx.geometry.Pos
import sun.net.www.http.PosterOutputStream
import uk.co.poolefoundries.baldinggate.screens.MainMenuScreen
import uk.co.poolefoundries.baldinggate.skeleton.*
import java.util.*

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

// TODO: this class should basically be empty
class BaldingGateGame : Game() {

    lateinit var batch : SpriteBatch
    var engine = Engine()
    var camera = OrthographicCamera()
    var viewport = ScreenViewport(camera)
    val tileSize = 25F

    private var walls = ImmutableArray(Array<Entity>())
    private var floors = ImmutableArray(Array<Entity>())
    private var players = ImmutableArray(Array<Entity>())
    private var enemies = ImmutableArray(Array<Entity>())
    private var mobs = ImmutableArray(Array<Entity>())

    // TODO: move camera movement logic out of the game class
    var cameraMoveDirection = Direction(0, 0)

    // TODO: move the pending animations to an animation/render component on the entity being animated
    var pendingAnimations = Array<Animation>()
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
        batch.dispose()
    }

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

    // TODO move this to the input handler and multi-plex it
    fun leftClick(x: Int, y: Int) {
        val gamePos = camera.unproject(Vector3(x.toFloat(),y.toFloat(),0F))
        val tilePos = PositionComponent((gamePos.x/tileSize).toInt(), (gamePos.y/tileSize).toInt())
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
        val gamePos = camera.unproject(Vector3(x.toFloat(),y.toFloat(),0F))
        val tilePos = PositionComponent((gamePos.x/tileSize).toInt(), (gamePos.y/tileSize).toInt())
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
            val end = animation.positions[1]

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
                selectedEntity.entity?.let { selectMob(it) }
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
        val distance = selectedEntity.position!!.manhattanDistance(target)
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

    private fun setGreenTiles(center: PositionComponent, range: Int) {
        clearColoredTiles()
        floors.forEach { tile ->
            if (positionMapper.get(tile).manhattanDistance(center) <= range) {
                greenTiles.add(tile)
                tile.add(ColorComponent(Color.GREEN))
            } else {
                tile.add(ColorComponent(Color.WHITE))
            }
        }
    }

    private fun setRedTiles(center: PositionComponent, range: Int) {
        clearColoredTiles()
        floors.forEach { tile ->
            if (positionMapper.get(tile).manhattanDistance(center) <= range) {
                redTiles.add(tile)
                tile.add(ColorComponent(Color.RED))
            } else {
                tile.add(ColorComponent(Color.WHITE))
            }
        }
    }


    private fun clearGreenTiles() {
        greenTiles.forEach {
            it.add(ColorComponent(Color.WHITE))
        }
        greenTiles.clear()
    }

    private fun clearRedTiles() {
        redTiles.forEach {
            it.add(ColorComponent(Color.WHITE))
        }
        redTiles.clear()
    }

    private fun clearColoredTiles() {
        clearGreenTiles()
        clearRedTiles()
    }

    fun (Map<String, Entity>).toMobInfo() : Collection<MobInfo> {
        return this.map{ it.value.toMobInfo(it.key) }
    }

    fun (Entity).toMobInfo(id: String) : MobInfo {
        val stats = getComponent(StatsComponent::class.java).stats
        val pos = getComponent(PositionComponent::class.java)
        return MobInfo(id, pos.copy(), stats.copy())
    }

    private fun enemyActions() {
        // TODO: Add an ID component so we can identify entities by a UUID
        val playerIds = players.associateBy { UUID.randomUUID().toString() }
        val enemyIds = enemies.associateBy { UUID.randomUUID().toString() }

        val actions = SkeletonAI.getPlan(playerIds.toMobInfo(), enemyIds.toMobInfo())

        actions.actions.forEach {
            when(it) {
                is MoveTowards -> {
                    // TODO: Add animations
                    val enemy = enemyIds.getValue(it.selfId).toMobInfo(it.selfId)
                    val target = playerIds.getValue(it.targetId).toMobInfo(it.targetId)

                    val newPos = it.getNewPos(enemy, target)

                    enemyIds.getValue(it.selfId).add(newPos)

                }
                is Attack -> {
                    val enemy = enemyIds.getValue(it.selfId).toMobInfo(it.selfId)
                    val target = playerIds.getValue(it.targetId).toMobInfo(it.targetId)

                    val damage = enemy.stats.attack.roll()
                    val newStats = target.stats.applyDamage(damage)
                    playerIds.getValue(it.targetId).add(StatsComponent(newStats))

                    println("Big oooof you just took $damage damage, ${newStats.hitPoints} hp left")
                }
                is Win -> {
                    println("You is dead!!!")
                    return
                }
                is EndTurn -> {
                    return
                }
            }
        }
    }
}

