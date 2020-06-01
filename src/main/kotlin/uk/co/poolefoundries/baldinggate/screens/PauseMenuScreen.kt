package uk.co.poolefoundries.baldinggate.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import uk.co.poolefoundries.baldinggate.core.BaldingGateGame
import uk.co.poolefoundries.baldinggate.desktop.DesktopLauncher

// will need to take in previous screen and just display this before returning to the previous screen on resume or
// change to options or quit
// options screen should do the same thing so that you can go back from options to pause and then back to game
class PauseMenuScreen(val game: BaldingGateGame, val previousScreen: ScreenAdapter) : ScreenAdapter() {

    var stage = Stage(game.viewport, game.batch)
    private val atlas = TextureAtlas(Gdx.files.internal("UISkins/StoneButtons/main-menu-buttons.atlas"))
    private val skin = Skin(Gdx.files.internal("UISkins/StoneButtons/main-menu-buttons.json"), atlas)
//    private val atlas = TextureAtlas(Gdx.files.internal("UISkins/default/skin/uiskin.atlas"))
//    private val skin = Skin(Gdx.files.internal("UISkins/default/skin/uiskin.json"), atlas)

    init {
        val table = Table()
        table.center().center()
        table.setFillParent(true)
        val scrollTable = Table()
        scrollTable.setFillParent(false)
        val scrollPane = ScrollPane(scrollTable, skin)
        scrollPane.fadeScrollBars=false

        val resumeButton = TextButton("resume", skin)
        val levelsButton = TextButton("levels", skin)
        val optionsButton = TextButton("options", skin)
        val quitButton = TextButton("quit", skin)

        resumeButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = previousScreen
            }
        })
        levelsButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = LevelSelectScreen(game,this@PauseMenuScreen)
            }
        })
        optionsButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = OptionsScreen(game, this@PauseMenuScreen)
            }
        })
        quitButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                DesktopLauncher.application.exit()
            }
        })

        scrollTable.add(resumeButton).padBottom(4F).expand()
        scrollTable.row()
        scrollTable.add(levelsButton).padBottom(4F).expand()
        scrollTable.row()
        scrollTable.add(optionsButton).padBottom(4F).expand()
        scrollTable.row()
        scrollTable.add(quitButton).padBottom(4F).expand()

        table.add(scrollPane).fill().expand()

        stage.addActor(table)
        stage.scrollFocus = scrollPane
    }

    override fun hide() {
        Gdx.input.inputProcessor = null
    }

    override fun show() {
        Gdx.input.inputProcessor = stage
    }


    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0F,0F,0F,1F)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        stage.act()
        stage.draw()
    }

    override fun dispose() {
        stage.dispose()
        skin.dispose()
        atlas.dispose()
    }


}
