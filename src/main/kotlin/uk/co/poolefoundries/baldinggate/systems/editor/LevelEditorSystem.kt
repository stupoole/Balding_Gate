package uk.co.poolefoundries.baldinggate.systems.editor

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import uk.co.poolefoundries.baldinggate.core.*
import uk.co.poolefoundries.baldinggate.model.*
import uk.co.poolefoundries.baldinggate.systems.CameraSystem
import java.io.File
import java.util.*

object LevelEditorSystem : EntitySystem() {
    private val playerFamily: Family = Family.all(PlayerComponent::class.java).get()
    private val enemyFamily: Family = Family.all(EnemyComponent::class.java).get()
    private val floorsFamily: Family = Family.all(FloorComponent::class.java).get()
    private val wallsFamily: Family = Family.all(WallComponent::class.java).get()
    private fun players() = engine.getEntitiesFor(playerFamily).toList()
    private fun enemies() = engine.getEntitiesFor(enemyFamily).toList()
    private fun floors() = engine.getEntitiesFor(floorsFamily).toList()
    private fun walls() = engine.getEntitiesFor(wallsFamily).toList()
    private fun allEntities() = players() + enemies() + floors() + walls()
    private fun (Entity).toPosition() = getComponent(PositionComponent::class.java)
    private fun (Entity).toMobType() = getComponent(MobTypeComponent::class.java).mobType
    private fun (Entity).toTileType() = getComponent(TileTypeComponent::class.java).tileType
    private fun (Entity).toTile() = Tile(toTileType().name, Position(toPosition().x, toPosition().y))
    private fun (Entity).toMob() = Mob(toMobType().name, Position(toPosition().x, toPosition().y))
    private const val tileSize = 25F
//    private var tileTypes = setOf<TileType>()
//    private var mobTypes = setOf<MobType>()


    // takes game coords and places selectedtile at that position if valid
    fun place(x: Int, y: Int) {

        // TODO: catch when the json is not valid
        val gamePos = CameraSystem.unproject(x, y)
        val tilePos = PositionComponent((gamePos.x / tileSize).toInt(), (gamePos.y / tileSize).toInt())
        val selectedTile = LevelEditHUDSystem.selectedTile ?: return
        if (selectedTile.toString().contains("floors") or selectedTile.toString().contains("walls")) {
            val tileType: TileType =
                jacksonObjectMapper().readValue(File(selectedTile.pathWithoutExtension() + ".json"))
//            tileTypes = tileTypes.union(setOf(tileType))
            addTile(tilePos, tileType)

        } else if (selectedTile.toString().contains("characters") or selectedTile.toString().contains("mobs")) {
            val mobType: MobType = jacksonObjectMapper().readValue(File(selectedTile.pathWithoutExtension() + ".json"))
//            mobTypes = mobTypes.union(setOf(mobType))
            addMob(tilePos, mobType)

        } else {
            println("Tile type not yet implemented")
        }
    }

    fun remove(x: Int, y: Int) {
        val gamePos = CameraSystem.unproject(x, y)
        val tilePos = PositionComponent((gamePos.x / tileSize).toInt(), (gamePos.y / tileSize).toInt())
        val selectedTile = LevelEditHUDSystem.selectedTile ?: return
        if (selectedTile.toString().contains("floors") or selectedTile.toString().contains("walls")) {
            removeTile(tilePos)
        } else if (selectedTile.toString().contains("characters") or selectedTile.toString().contains("mobs")) {
            removeMob(tilePos)
        } else {
            println("Tile type not yet implemented")
        }
    }

    fun saveLevel(levelName: String): Boolean {
        // TODO check if save succeeds and make overwrite harder
        if (levelName == "Saved Successfully") {return false}

        val tiles = (floors() + walls())
        val mobs = (enemies() + players())
        val level = Level(
            levelName,
            tiles.map { it.toTile() },
            tiles.map { it.toTileType() }.distinct(),
            mobs.map { it.toMob() },
            mobs.map { it.toMobType() }.distinct()
        )
        val file = File("levels/$levelName.json")
        if (!file.isFile) {
            jacksonObjectMapper().writeValue(file, level)
            return true
        }
        return false
    }

    private fun addTile(pos: PositionComponent, type: TileType) {
        val tiles = (floors() + walls()).filter { it.toPosition() == pos }
        if (tiles.isNotEmpty()) {
            tiles.forEach { engine.removeEntity(it) }
        }
        engine.addEntity(
            type.toEntity().add(pos)
                .add(TileTypeComponent(type))
        )

    }

    private fun addMob(pos: PositionComponent, mobType: MobType) {
        val mobs = (enemies() + players()).filter { it.toPosition() == pos }
        if (mobs.isNotEmpty()) {
            mobs.forEach { engine.removeEntity(it) }
        }
        engine.addEntity(
            mobType.toEntity().add(pos)
                .add(StatsComponent(mobType.stats))
                .add(IdComponent(UUID.randomUUID().toString()))
                .add(behaviourMap.getValue(mobType.behaviour))
                .add(MobTypeComponent(mobType))
        )
    }

    private fun removeTile(pos: PositionComponent) {
        val tiles = (floors() + walls()).filter { it.toPosition() == pos }
        if (tiles.isNotEmpty()) {
            tiles.forEach { engine.removeEntity(it) }
        }
    }

    private fun removeMob(pos: PositionComponent) {
        val mobs = (enemies() + players()).filter { it.toPosition() == pos }
        if (mobs.isNotEmpty()) {
            mobs.forEach { engine.removeEntity(it) }
        }
    }


}