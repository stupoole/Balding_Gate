package uk.co.poolefoundries.baldinggate.screens


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Gdx.files
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import uk.co.poolefoundries.baldinggate.core.BaldingGateGame
import uk.co.poolefoundries.baldinggate.desktop.DesktopLauncher

class MainMenuScreen(val game: BaldingGateGame) : ScreenAdapter() {

    var stage = Stage(game.viewport, game.batch)
    private val atlas = TextureAtlas(files.internal("UISkins/StoneButtons/main-menu-buttons.atlas"))
    private val skin = Skin(files.internal("UISkins/StoneButtons/main-menu-buttons.json"), atlas)

    init {
        val table = Table()
        val scrollTable = Table()
        scrollTable.setFillParent(false)
        val scrollPane = ScrollPane(scrollTable, skin)
        scrollPane.fadeScrollBars=false
        table.setFillParent(true)
        table.center().center()
        val playButton = TextButton("play", skin)
        val levelsButton = TextButton("levels", skin)
        val optionsButton = TextButton("options", skin)
        val quitButton = TextButton("quit", skin)

        playButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = GameScreen(game, "level")
            }
        })
        levelsButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = LevelSelectScreen(game, this@MainMenuScreen)
            }
        })
        optionsButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = OptionsScreen(game, this@MainMenuScreen)
            }
        })
        quitButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                DesktopLauncher.application.exit()
            }
        })

        scrollTable.add(playButton).padBottom(4F).expand()
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
//        stage.draw()
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0F, 0F, 0F, 1F)
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