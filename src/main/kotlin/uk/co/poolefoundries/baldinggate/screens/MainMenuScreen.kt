package uk.co.poolefoundries.baldinggate.screens


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Gdx.files
import com.badlogic.gdx.Screen
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.ScreenViewport
import uk.co.poolefoundries.baldinggate.core.BaldingGateGame
import uk.co.poolefoundries.baldinggate.desktop.DesktopLauncher

class MainMenuScreen(val game: BaldingGateGame) : ScreenAdapter() {

    var stage = Stage(game.viewport, game.batch)
    val atlas = TextureAtlas(files.internal("UISkins/default/skin/uiskin.atlas"))
    val skin = Skin(files.internal("UISkins/default/skin/uiskin.json"), atlas)

    override fun hide() {
        Gdx.input.inputProcessor = null
    }

    override fun show() {
        Gdx.input.inputProcessor = stage

        val table = Table()
        table.setFillParent(true)
        table.center()

        val playButton = TextButton("Play", skin)
        val optionsButton = TextButton("Options", skin)
        val quitButton = TextButton("Quit", skin)

        playButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = LevelScreen(game)
            }
        })
        optionsButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                // Options screen? maybe dynamic menus?
            }
        })

        quitButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                //todo figure out how to quit
            }
        })

        table.add(playButton)
        table.row()
        table.add(optionsButton)
        table.row()
        table.add(quitButton)
        stage.addActor(table)
    }


    override fun render(delta: Float) {
//        game.batch.begin()
//        game.batch.end()
        //todo: try with and without batch begin/end
        stage.draw()
    }

    override fun dispose() {
        //todo dispose of the stage for sure. probabyl skin and atlas too
//        stage.dispose()
        skin.dispose()
        atlas.dispose()
    }


}