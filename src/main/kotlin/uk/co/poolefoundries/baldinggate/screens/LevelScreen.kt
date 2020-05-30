package uk.co.poolefoundries.baldinggate.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import uk.co.poolefoundries.baldinggate.core.BaldingGateGame

import uk.co.poolefoundries.baldinggate.core.RenderingSystem
import uk.co.poolefoundries.baldinggate.input.PlayerInputHandler
import uk.co.poolefoundries.baldinggate.model.loadLevel
import uk.co.poolefoundries.baldinggate.model.toEntities

// LevelScreen represents the gamplay screen of the game.
class LevelScreen(val game: BaldingGateGame) : ScreenAdapter() {

    var stage = Stage(game.viewport, game.batch)
    var input = InputMultiplexer()


    override fun hide() {
        Gdx.input.inputProcessor = null
    }

    override fun show() {

        // TODO: Parameterise the level to load (so we could have a level select menu)
        loadLevel("level").toEntities().forEach(game.engine::addEntity)

        // TODO: Move the engine stuff out of the game (as it's only meant to multiplex screens)
        game.engine.addSystem(RenderingSystem(stage, game.tileSize))
        input.addProcessor(PlayerInputHandler(game))

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

    override fun pause() {

    }

    override fun resume() {

    }

    override fun resize(width: Int, height: Int) {
        game.viewport.update(width, height)
        game.camera.update()
    }

    override fun dispose() {

    }

}



