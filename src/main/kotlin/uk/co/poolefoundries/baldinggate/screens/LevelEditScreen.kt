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

class LevelEditScreen(private val game: BaldingGateGame, levelName:String) : ScreenAdapter() {

    private val input = InputMultiplexer()

    // Todo: made a scroll pane which shows all available mobs/tiles
    // tie in the UI to have an ability to edit stats/components of each entity in the level
    // create a popup/ability to save/load levels for editing.
    // create input handler to allow the left and right click to be interpreted
    // click to place current tile/mob
    // click again to select current tile type
    // r-click to remove tile
    // esc - deselect if selected else menu?
    // add layers to allow users to draw mobs on top of tiles?
    // create the code to convert a level to json
    //  ability to add and remove tile types
    //  to have a tabbed pane with tiles from each folder

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