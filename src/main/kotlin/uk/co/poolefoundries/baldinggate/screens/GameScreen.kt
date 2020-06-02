package uk.co.poolefoundries.baldinggate.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.ScreenAdapter
import uk.co.poolefoundries.baldinggate.core.BaldingGateGame
import uk.co.poolefoundries.baldinggate.input.RawInputHandler
import uk.co.poolefoundries.baldinggate.systems.enemy.EnemyTurnSystem
import uk.co.poolefoundries.baldinggate.systems.player.PlayerTurnSystem
import uk.co.poolefoundries.baldinggate.systems.EntitySelectionSystem
import uk.co.poolefoundries.baldinggate.systems.InputProcessorSystem
import uk.co.poolefoundries.baldinggate.model.loadLevel
import uk.co.poolefoundries.baldinggate.model.toEntities
import uk.co.poolefoundries.baldinggate.systems.CameraSystem
import uk.co.poolefoundries.baldinggate.systems.RenderingSystem

// LevelScreen represents the gameplay screen of the game.
class GameScreen(private val game: BaldingGateGame, levelName:String) : ScreenAdapter() {

//    var stage = Stage(game.viewport, game.batch)
    private val input = InputMultiplexer()

    init {
        loadLevel(levelName).toEntities().forEach(game.engine::addEntity)
        // Maybe the tile size should be specified after the loadLevel?

        // TODO: Move the engine stuff out of the game (as it's only meant to multiplex screens)
        // Should probably make this simpler for each screen (such as having an extended Engine with "add all systems" built in)

        game.engine.addSystem(RenderingSystem)
        game.engine.addSystem(PlayerTurnSystem)
        game.engine.addSystem(EnemyTurnSystem)
        game.engine.addSystem(InputProcessorSystem)
        game.engine.addSystem(EntitySelectionSystem)
        input.addProcessor(0, RawInputHandler)

    }

    override fun hide() {
        Gdx.input.inputProcessor = null
    }

    override fun show() {
        CameraSystem.switchToGame()
        Gdx.input.inputProcessor = input
    }

    override fun render(delta: Float) {
        game.engine.update(delta)
    }


    override fun dispose() {
        game.engine.removeAllEntities()
    }
}



