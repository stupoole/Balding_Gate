package uk.co.poolefoundries.baldinggate.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import uk.co.poolefoundries.baldinggate.core.BaldingGateGame

class LevelSelectScreen(val game: BaldingGateGame, val previousScreen: ScreenAdapter) : ScreenAdapter() {

    var stage = Stage(game.viewport, game.batch)
    private val atlas = TextureAtlas(Gdx.files.internal("UISkins/StoneButtons/main-menu-buttons.atlas"))
    private val skin = Skin(Gdx.files.internal("UISkins/StoneButtons/main-menu-buttons.json"), atlas)
    private val buttons = mutableListOf<Actor>()

    init {

        val table = Table()
        table.setFillParent(true)
        table.center()
        val dirHandle = Gdx.files.internal("levels/")
        println(dirHandle)
        dirHandle.list().forEach { level ->
            val name = level.nameWithoutExtension()
            val button = TextButton(name, skin)
            button.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    game.screen = GameScreen(game, name)
                }
            })
            table.add(button).padBottom(10F)
            table.row()
        }
        stage.addActor(table)

        // TODO add scroll for level select
        // TODO use the screen size to find out how many to draw

//        val playButton = TextButton("play", skin)
//        val levelsButton = TextButton("levels", skin)
//        val optionsButton = TextButton("options", skin)
//        val quitButton = TextButton("quit", skin)
//        playButton.addListener(object : ClickListener() {
//            override fun clicked(event: InputEvent?, x: Float, y: Float) {
//                game.screen = GameScreen(game, "level")
//            }
//        })
//        levelsButton.addListener(object : ClickListener() {
//            override fun clicked(event: InputEvent?, x: Float, y: Float) {
//                // TODO: level selec screen
//            }
//        })
//        optionsButton.addListener(object : ClickListener() {
//            override fun clicked(event: InputEvent?, x: Float, y: Float) {
//                game.screen = OptionsScreen(game, this@MainMenuScreen)
//            }
//        })
//
//        quitButton.addListener(object : ClickListener() {
//            override fun clicked(event: InputEvent?, x: Float, y: Float) {
//                //todo figure out how to quit
//                DesktopLauncher.application.exit()
//            }
//        })
//        table.padBottom()
//        table.add(playButton).padBottom(10F)
//        table.row()
//        table.add(levelsButton).padBottom(10F)
//        table.row()
//        table.add(optionsButton).padBottom(10F)
//        table.row()
//        table.add(quitButton).padBottom(10F)
//        stage.addActor(table)
    }

    override fun hide() {
        Gdx.input.inputProcessor = null
    }

    override fun show() {
        Gdx.input.inputProcessor = stage
        stage.draw()
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0F, 0F, 0F, 1F)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        stage.draw()
    }

    override fun dispose() {
        stage.dispose()
        skin.dispose()
        atlas.dispose()
    }
}