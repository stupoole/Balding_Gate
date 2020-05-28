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
import uk.co.poolefoundries.baldinggate.skeleton.SkeletonSystem

data class SelectedEntity(
    var entity: Entity? = null,
    var position: PositionComponent? = null,
    var actionPoints: Int,
    var speed: Int
) {
    fun clear() {
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

class BaldingGateGame : Game() {

    // TODO: Have Jon look at this
    val batch: SpriteBatch
        get() = SpriteBatch()
    var engine = Engine()
    var camera = OrthographicCamera()
    var viewport = ScreenViewport(camera)
    val tileSize = 25F

    var walls = ImmutableArray(Array<Entity>())
    var floors = ImmutableArray(Array<Entity>())
    var players = ImmutableArray(Array<Entity>())
    var enemies = ImmutableArray(Array<Entity>())
    var mobs = ImmutableArray(Array<Entity>())
    val greenTiles = Array<Entity>()
    val redTiles = Array<Entity>()
    var selectedEntity = SelectedEntity(null, null, 0, 0)

    val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)
    val statsMapper = ComponentMapper.getFor(StatsComponent::class.java)
    val colorMapper = ComponentMapper.getFor(ColorComponent::class.java)


    override fun create() {
        batch
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

    fun endTurn() {
        SkeletonSystem.act()
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

    fun leftClick(x: Int, y: Int) {
        val tilePos = PositionComponent(
            ((x.toFloat() + camera.position.x - viewport.worldWidth / 2) / tileSize).toInt(),
            (((camera.position.y + viewport.worldHeight / 2) - y.toFloat()) / tileSize).toInt()
        )
        selectedEntity.entity?.add(ColorComponent(Color.WHITE))
        val clickedMobs = mobs.filter { positionMapper.get(it) == tilePos }
        when (clickedMobs.size) {
            0 -> {
                clearGreenTiles(); clearRedTiles(); selectedEntity.clear()
            }
            1 -> {
                if (players.contains(clickedMobs.first())) {
                    playerSelect(clickedMobs.first())
                } else {
                    enemySelect(clickedMobs.first())
                }
            }
            else -> {
                println("too many mobs to sort through damnit")
                // TODO prefer players.
            }

        }
    }

    fun nextPlayer(){

        //TODO make this select "nextplayer"
    }


    fun rightClick(x: Int, y: Int) {
        // TODO implement attack instead of just always move. Should probably make methods to handle moving, attacking
        //  and other things which are called by this method
        val tilePos = PositionComponent(
            ((x.toFloat() + camera.position.x - viewport.worldWidth / 2) / tileSize).toInt(),
            (((camera.position.y + viewport.worldHeight / 2) - y.toFloat()) / tileSize).toInt()
        )
        if (players.contains(selectedEntity.entity)) {
            if (selectedEntity.actionPoints > 0) {
                playerMove(tilePos)
            } else {
                println("NO AP")
            }
        }

    }

    private fun playerSelect(player: Entity) {
        selectedEntity = SelectedEntity(
            player,
            positionMapper.get(player),
            statsMapper.get(player).stats.currentAP,
            statsMapper.get(player).stats.speed
        )
        selectedEntity.entity?.add(ColorComponent(Color.LIME))
        if (selectedEntity.actionPoints > 0) {
            setGreenTiles(selectedEntity.position!!, selectedEntity.speed)
        } else {
            clearGreenTiles()
            clearRedTiles()
        }
    }

    private fun enemySelect(enemy: Entity) {

        selectedEntity = SelectedEntity(
            enemy,
            positionMapper.get(enemy),
            statsMapper.get(enemy).stats.currentAP,
            statsMapper.get(enemy).stats.speed
        )
        selectedEntity.entity?.add(ColorComponent(Color.ORANGE))
        if (selectedEntity.actionPoints > 0) {
            setRedTiles(selectedEntity.position!!, selectedEntity.speed)
        } else {
            clearGreenTiles()
            clearRedTiles()
        }
    }

    private fun playerMove(target: PositionComponent) {

        val distance = selectedEntity.position!!.gridWiseDistance(target)
        // TODO: pathfinding
        for (step in 0 until minOf(selectedEntity.speed, distance)) {
            selectedEntity.entity!!.add(
                selectedEntity.position!! + selectedEntity.position!!.direction(target)
            )
            selectedEntity.position = selectedEntity.entity!!.getComponent(PositionComponent::class.java)
        }

        selectedEntity.actionPoints -= 1
        selectedEntity.entity!!.add(
            StatsComponent(
                statsMapper.get(selectedEntity.entity).stats.copy(currentAP = selectedEntity.actionPoints)
            )
        )
        if (selectedEntity.actionPoints <= 0) {
            clearGreenTiles()
        } else {
            setGreenTiles(selectedEntity.position!!, selectedEntity.speed)
        }


    }

    private fun setGreenTiles(center: PositionComponent, range: Int) {
        greenTiles.clear()
        floors.forEach { tile ->
            if (positionMapper.get(tile).gridWiseDistance(center) <= range) {
                greenTiles.add(tile)
                tile.add(ColorComponent(Color.GREEN))
            } else {
                tile.add(ColorComponent(Color.WHITE))
            }
        }
    }

    private fun setRedTiles(center: PositionComponent, range: Int) {
        redTiles.clear()
        floors.forEach { tile ->
            if (positionMapper.get(tile).gridWiseDistance(center) <= range) {
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


}

