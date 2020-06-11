package uk.co.poolefoundries.baldinggate.systems.editor

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import uk.co.poolefoundries.baldinggate.core.*
import uk.co.poolefoundries.baldinggate.model.MobType
import uk.co.poolefoundries.baldinggate.model.TileType
import uk.co.poolefoundries.baldinggate.model.behaviourMap
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
    private const val tileSize = 25F


    // takes game coords and places selectedtile at that position if valid
    fun place(x: Int, y: Int) {
        val gamePos = CameraSystem.unproject(x, y)
        val tilePos = PositionComponent((gamePos.x / tileSize).toInt(), (gamePos.y / tileSize).toInt())
        val selectedTile = LevelEditHUDSystem.selectedTile ?: return
        if (selectedTile.toString().contains("floors") or selectedTile.toString().contains("walls")) {
            val tileType: TileType =
                jacksonObjectMapper().readValue(File(selectedTile.pathWithoutExtension() + ".json"))
            addTile(tilePos, tileType)

        } else if (selectedTile.toString().contains("characters") or selectedTile.toString().contains("mobs")) {
            val mobType: MobType = jacksonObjectMapper().readValue(File(selectedTile.pathWithoutExtension() + ".json"))
            addMob(tilePos, mobType)

        } else {
            println("Tile type not yet implemented")
        }
    }

    fun remove(x: Int, y: Int) {
        val gamePos = CameraSystem.unproject(x, y)
        val tilePos = PositionComponent((gamePos.x / tileSize).toInt(), (gamePos.y / tileSize).toInt())
        val selectedTile = LevelEditHUDSystem.selectedTile ?: return
        if (selectedTile.toString().contains("floors") or selectedTile.toString().contains("walls")){
            removeTile(tilePos)
        } else if (selectedTile.toString().contains("characters") or selectedTile.toString().contains("mobs")){
            removeMob(tilePos)
        } else {
            println("Tile type not yet implemented")
        }
    }

    private fun addTile(pos: PositionComponent, type: TileType) {
        val tiles = (floors() + walls()).filter { it.toPosition() == pos }
        if (tiles.isNotEmpty()) {
            tiles.forEach { engine.removeEntity(it) }
        }
        engine.addEntity(type.toEntity().add(pos))

    }

    private fun addMob(pos:PositionComponent, mob: MobType) {
        val mobs = (enemies() + players()).filter { it.toPosition() == pos }
        if (mobs.isNotEmpty()) {mobs.forEach { engine.removeEntity(it) }}
        engine.addEntity(mob.toEntity().add(pos)
            .add(StatsComponent(mob.stats))
            .add(IdComponent(UUID.randomUUID().toString()))
            .add(behaviourMap.getValue(mob.behaviour)))
    }

    private fun removeTile(pos: PositionComponent) {
        val tiles = (floors() + walls()).filter { it.toPosition() == pos }
        if (tiles.isNotEmpty()) {tiles.forEach { engine.removeEntity(it) }}
    }

    private fun removeMob(pos: PositionComponent) {
        val mobs = (enemies() + players()).filter { it.toPosition() == pos }
        if (mobs.isNotEmpty()) {mobs.forEach { engine.removeEntity(it) }}
    }


}