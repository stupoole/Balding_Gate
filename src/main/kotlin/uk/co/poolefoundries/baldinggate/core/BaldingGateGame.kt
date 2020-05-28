package uk.co.poolefoundries.baldinggate.core

import com.badlogic.ashley.core.*
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.ScreenViewport
import uk.co.poolefoundries.baldinggate.*
import uk.co.poolefoundries.baldinggate.model.loadLevel
import uk.co.poolefoundries.baldinggate.model.toEntities
import uk.co.poolefoundries.baldinggate.screens.MainMenuScreen
import uk.co.poolefoundries.baldinggate.skeleton.SkeletonSystem
import java.nio.channels.spi.SelectorProvider


class BaldingGateGame : Game() {

    // TODO: Have Jon look at this because I'm sure it's wrong
    val batch: SpriteBatch
        get() = SpriteBatch()
    var engine = Engine()
    var camera= OrthographicCamera()
    var viewport = ScreenViewport(camera)
    val tileSize = 25F


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

    override fun render() {
        super.render()
    }
}

