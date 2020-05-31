package uk.co.poolefoundries.baldinggate.screens

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import uk.co.poolefoundries.baldinggate.core.BaldingGateGame
import uk.co.poolefoundries.baldinggate.core.PositionComponent

import uk.co.poolefoundries.baldinggate.core.RenderingSystem
import uk.co.poolefoundries.baldinggate.core.StatsComponent
import uk.co.poolefoundries.baldinggate.entitysystems.enemy.EnemyTurnSystem
import uk.co.poolefoundries.baldinggate.input.PlayerInputHandler
import uk.co.poolefoundries.baldinggate.model.loadLevel
import uk.co.poolefoundries.baldinggate.model.toEntities
import uk.co.poolefoundries.baldinggate.skeleton.*
import java.util.*

// LevelScreen represents the gamplay screen of the game.
class GameScreen(val game: BaldingGateGame, levelName:String) : ScreenAdapter() {

    var stage = Stage(game.viewport, game.batch)
    var input = InputMultiplexer()

    init {
        loadLevel(levelName).toEntities().forEach(game.engine::addEntity)

        // TODO: Move the engine stuff out of the game (as it's only meant to multiplex screens)
        game.engine.addSystem(RenderingSystem(stage, game.tileSize))
        game.engine.addSystem(EnemyTurnSystem)
        input.addProcessor(PlayerInputHandler(game))
    }

    override fun hide() {
        Gdx.input.inputProcessor = null
    }

    override fun show() {
        Gdx.input.inputProcessor = input
        game.update()

    }

    override fun render(delta: Float) {

        Gdx.gl.glClearColor(.1F, .12F, .16F, 1F)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.engine.update(delta)

        if (!game.pendingAnimations.isEmpty) {
            game.animationStep(delta)
        }


    }

    override fun resize(width: Int, height: Int) {
        game.viewport.update(width, height)
        game.camera.update()
    }

    override fun dispose() {
        stage.dispose()
    }
}



