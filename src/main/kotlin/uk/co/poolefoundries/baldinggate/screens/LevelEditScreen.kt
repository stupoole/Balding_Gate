package uk.co.poolefoundries.baldinggate.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.ScreenAdapter
import uk.co.poolefoundries.baldinggate.core.BaldingGateGame
import uk.co.poolefoundries.baldinggate.input.*
import uk.co.poolefoundries.baldinggate.model.loadLevel
import uk.co.poolefoundries.baldinggate.model.toEntities
import uk.co.poolefoundries.baldinggate.systems.*
import uk.co.poolefoundries.baldinggate.systems.editor.LevelEditHUDSystem
import uk.co.poolefoundries.baldinggate.systems.editor.LevelEditorSystem

class LevelEditScreen(private val game: BaldingGateGame, val levelName:String) : ScreenAdapter() {

    private val input = InputMultiplexer()


    /* TODO ( SPoole): tie in the UI to have an ability to edit stats/components of each entity in the level
         click again to select current tile type
         esc - deselect all tiles
         ability to add and remove tile types to have a tabbed pane with tiles from each folder
    */

    init {
        if (levelName.isNotBlank()) {
            loadLevel(levelName).toEntities().forEach(game.engine::addEntity)
        }
        game.engine.addSystem(RenderingSystem)
        game.engine.addSystem(LevelEditorInputProcessor)
        game.engine.addSystem(LevelEditHUDSystem)
        game.engine.addSystem(LevelEditorSystem)

        input.addProcessor(0, CameraSystem.HUDStage)
        input.addProcessor(1, RawEditorInputHandler)



    }

    override fun show() {
        Gdx.input.inputProcessor = input
        LevelEditHUDSystem.show()
    }

    override fun hide() {
        Gdx.input.inputProcessor = null
        LevelEditHUDSystem.hide()
        super.hide()
    }

    override fun dispose() {
        CameraSystem.newMenu()
        CameraSystem.newHUD()
        HUDSystem.clear()
        game.engine.removeAllEntities()
        game.engine.removeSystem(RenderingSystem)
        game.engine.removeSystem(LevelEditorInputProcessor)
        game.engine.removeSystem(LevelEditHUDSystem)
        game.engine.removeSystem(LevelEditorSystem)
        Gdx.input.inputProcessor = null
    }

    override fun render(delta: Float) {
        game.engine.update(delta)
    }



}