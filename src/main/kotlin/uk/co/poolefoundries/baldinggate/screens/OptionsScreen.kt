package uk.co.poolefoundries.baldinggate.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import uk.co.poolefoundries.baldinggate.core.BaldingGateGame
import uk.co.poolefoundries.baldinggate.systems.CameraSystem

// will need to take in previous screen and just display this before returning to the previous screen on resume or
// change to options or quit
// options screen should do the same thing so that you can go back from options to pause and then back to game
class OptionsScreen(val game: BaldingGateGame, val previousScreen: ScreenAdapter) : ScreenAdapter() {

    private val atlas = TextureAtlas(Gdx.files.internal("UISkins/StoneButtons/main-menu-buttons.atlas"))
    private val skin = Skin(Gdx.files.internal("UISkins/StoneButtons/main-menu-buttons.json"), atlas)
    private val table = Table()
    private val scrollTable = Table()
    private val scrollPane = ScrollPane(scrollTable, skin)

    init {

        table.setFillParent(true)
        table.center().center()
        scrollPane.fadeScrollBars = false
        val backButton = TextButton("BACK???", skin)
        backButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = previousScreen
            }
        })

        scrollTable.add(backButton).padBottom(4F).expand()
        scrollTable.row()

        table.add(scrollPane).fill().expand()


    }

    override fun hide() {
        Gdx.input.inputProcessor = null
    }

    override fun show() {
        val cameraSystem = game.engine.getSystem(CameraSystem::class.java)
        cameraSystem.switchToStage()
        cameraSystem.addActorToStage(table)
        cameraSystem.setScrollFocus(scrollPane)
        Gdx.input.inputProcessor = cameraSystem.stage
    }

    override fun render(delta: Float) {
        val cameraSystem = game.engine.getSystem(CameraSystem::class.java)
        cameraSystem.renderStage()
    }

    override fun dispose() {
        val cameraSystem = game.engine.getSystem(CameraSystem::class.java)
        cameraSystem.newStage()
        skin.dispose()
        atlas.dispose()
    }

}
