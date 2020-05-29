package uk.co.poolefoundries.baldinggate.screens

import com.badlogic.gdx.*
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import uk.co.poolefoundries.baldinggate.*
import uk.co.poolefoundries.baldinggate.core.*
import uk.co.poolefoundries.baldinggate.input.PlayerInputHandler
import uk.co.poolefoundries.baldinggate.model.loadLevel
import uk.co.poolefoundries.baldinggate.model.toEntities


class LevelScreen(val game: BaldingGateGame) : ScreenAdapter() {

    var stage = Stage(game.viewport, game.batch)
    var input = InputMultiplexer()


    override fun hide() {
        Gdx.input.inputProcessor = null
    }

    override fun show() {

        loadLevel("level").toEntities().forEach(game.engine::addEntity)
        game.engine.addSystem(RenderingSystem(stage, game.tileSize))
        input.addProcessor(PlayerInputHandler(game))

        // TODO: maybe the entity list should be stored in the screen and passed to classes on creation instead.
        Gdx.input.inputProcessor = input

        game.update()

    }

    override fun render(delta: Float) {

        Gdx.gl.glClearColor(.1F, .12F, .16F, 1F)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.engine.update(delta)
        if (game.cameraMoveDirection != PAN_NONE){
            game.cameraMove(delta)
        }
        if (!game.pendingAnimations.isEmpty) {
            game.animationStep(delta)
        }


    }

    override fun pause() {
        game.cameraMoveDirection = PAN_NONE
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



