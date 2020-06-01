package uk.co.poolefoundries.baldinggate.screens

import com.badlogic.gdx.Gdx
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


class LevelSelectScreen(val game: BaldingGateGame, val previousScreen: ScreenAdapter) : ScreenAdapter() {

    var stage = Stage(game.viewport, game.batch)
    private val atlas = TextureAtlas(Gdx.files.internal("UISkins/StoneButtons/main-menu-buttons.atlas"))
    private val skin = Skin(Gdx.files.internal("UISkins/StoneButtons/main-menu-buttons.json"), atlas)


    init {
        val dirHandle = Gdx.files.internal("levels/")
        val table = Table()
        table.center().center()
        table.setFillParent(true)
        val scrollTable = Table()
        scrollTable.setFillParent(false)
        val scrollPane = ScrollPane(scrollTable, skin)
        scrollPane.fadeScrollBars=false

        val backButton = TextButton("BACK???", skin)
        backButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = previousScreen
            }
        })

        scrollTable.add(backButton).padBottom(4F).expand()
        scrollTable.row()
        dirHandle.list().forEach { level ->
            val name = level.nameWithoutExtension()
            val button = TextButton(name, skin)
            button.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    game.screen = GameScreen(game, name)
                }
            })
            scrollTable.add(button).padBottom(4F).expand()
            scrollTable.row()
        }

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