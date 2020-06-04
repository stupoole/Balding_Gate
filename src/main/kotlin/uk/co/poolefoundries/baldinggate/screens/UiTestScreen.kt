package uk.co.poolefoundries.baldinggate.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import uk.co.poolefoundries.baldinggate.core.BaldingGateGame
import uk.co.poolefoundries.baldinggate.systems.CameraSystem

class UiTestScreen(val game: BaldingGateGame) : ScreenAdapter() {

    private val atlas = TextureAtlas(Gdx.files.internal("UISkins/StatusBars/status-bars.atlas"))
    private val skin = Skin(Gdx.files.internal("UISkins/StatusBars/status-bars.json"), atlas)
    private val table = Table()


    init {


        table.center().center()
        table.setFillParent(true)

        val healthBar = ProgressBar(0F, 1F, 0.1F, false, skin.get("health", ProgressBar.ProgressBarStyle::class.java))
        healthBar.setAnimateDuration(2F); healthBar.value = 1F
        val staminaBar = ProgressBar(0F, 1F, 0.1F, false, skin.get("stamina", ProgressBar.ProgressBarStyle::class.java))
        staminaBar.setAnimateDuration(2F); staminaBar.value = 1F
        val miniHealthBar = ProgressBar(0F, 1F, 0.1F, false, skin.get("mini-health", ProgressBar.ProgressBarStyle::class.java))
        miniHealthBar.setAnimateDuration(2F); miniHealthBar.value = 1F
        val miniStaminaBar = ProgressBar(0F, 1F, 0.1F, false, skin.get("mini-stamina", ProgressBar.ProgressBarStyle::class.java))
        miniStaminaBar.setAnimateDuration(2F); miniStaminaBar.value = 1F

        table.add(healthBar).padBottom(10F).expand().fill()
        table.row()
        table.add(staminaBar).padBottom(10F).expand().fill()
        table.row()
        table.add(miniHealthBar).padBottom(10F).expand().fill()
        table.row()
        table.add(miniStaminaBar).padBottom(10F).expand().fill()
        table.row()


    }

    override fun hide() {
        Gdx.input.inputProcessor = null
    }

    override fun show() {
        val cameraSystem = game.engine.getSystem(CameraSystem::class.java)
        CameraSystem.switchToStage()
        cameraSystem.addActorToStage(table)
//        cameraSystem.setScrollFocus(scrollPane)
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