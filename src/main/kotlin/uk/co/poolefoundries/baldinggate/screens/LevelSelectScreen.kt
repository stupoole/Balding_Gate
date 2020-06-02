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
import uk.co.poolefoundries.baldinggate.systems.CameraSystem


class LevelSelectScreen(val game: BaldingGateGame, val previousScreen: ScreenAdapter) : ScreenAdapter() {

    private val atlas = TextureAtlas(Gdx.files.internal("UISkins/StoneButtons/main-menu-buttons.atlas"))
    private val skin = Skin(Gdx.files.internal("UISkins/StoneButtons/main-menu-buttons.json"), atlas)
    private val embossedSkin = Skin(Gdx.files.internal("UISkins/StoneButtons/main-menu-buttons-embossed.json"), atlas)
    private val table = Table()
    private val scrollTable = Table()
    private val scrollPane = ScrollPane(scrollTable, skin)

    init {
        val dirHandle = Gdx.files.internal("levels/")

        table.center().center()
        table.setFillParent(true)
        scrollTable.setFillParent(false)
        scrollPane.fadeScrollBars=false

        val backButton = TextButton("BACK...", embossedSkin)
        backButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = previousScreen
                // TODO: This didn't work in the new camera system.
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


    }

    override fun hide() {
        Gdx.input.inputProcessor = null
    }

    override fun show() {
        val cameraSystem = game.engine.getSystem(CameraSystem::class.java)
        CameraSystem.switchToStage()
        cameraSystem.addActorToStage(table)
        cameraSystem.setScrollFocus(scrollPane)
        Gdx.input.inputProcessor = cameraSystem.stage
    }

    override fun render(delta: Float) {
        val cameraSystem = game.engine.getSystem(CameraSystem::class.java)
        cameraSystem.renderStage(delta)
    }

    override fun dispose() {
        val cameraSystem = game.engine.getSystem(CameraSystem::class.java)
        cameraSystem.newStage()
        skin.dispose()
        atlas.dispose()
    }
}