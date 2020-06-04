package uk.co.poolefoundries.baldinggate.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import uk.co.poolefoundries.baldinggate.core.Stats
import uk.co.poolefoundries.baldinggate.core.StatsComponent

object HUDSystem : EntitySystem() {

    private val atlas = TextureAtlas(Gdx.files.internal("UISkins/StatusBars/status-bars.atlas"))
    private val skin = Skin(Gdx.files.internal("UISkins/StatusBars/status-bars.json"), atlas)
    private val table = Table()


//    val miniStaminaBar =
//        ProgressBar(0F, 1F, 0.01F, false, skin.get("mini-stamina", ProgressBar.ProgressBarStyle::class.java))
//    val miniHealthBar =
//        ProgressBar(0F, 1F, 0.01F, false, skin.get("mini-health", ProgressBar.ProgressBarStyle::class.java))
    val healthBar = ProgressBar(0F, 1F, 0.01F, false, skin.get("health", ProgressBar.ProgressBarStyle::class.java))
    val staminaBar = ProgressBar(0F, 1F, 0.01F, false, skin.get("stamina", ProgressBar.ProgressBarStyle::class.java))
    val animationDuration = 0.1F

    init {
        table.bottom().left()
        table.setFillParent(true)

        healthBar.setAnimateDuration(animationDuration); healthBar.value = 1F
        staminaBar.value = 1F
//        miniHealthBar.setAnimateDuration(animationDuration); miniHealthBar.value = 1F
//        miniStaminaBar.value = 1F

        table.add(healthBar).padBottom(10F)
        table.row()
        table.add(staminaBar).padBottom(10F)
        table.row()
//        table.add(miniHealthBar).padBottom(10F)
//        table.row()
//        table.add(miniStaminaBar).padBottom(10F)
//        table.row()
    }

    override fun update(deltaTime: Float) {
        val stats = EntitySelectionSystem.getSelectedEntityStats()
        if (stats == null) {
            table.remove()
        } else {
            CameraSystem.addActorToStage(table)
            healthBar.value = stats.hitPoints.toFloat() / stats.vitality.toFloat()
//            miniHealthBar.value = stats.hitPoints.toFloat() / stats.vitality.toFloat()
            staminaBar.value = stats.currentAP.toFloat() / stats.maxAP.toFloat()
//            miniStaminaBar.value = stats.currentAP.toFloat() / stats.maxAP.toFloat()
        }
    }


    fun show() {
        CameraSystem.addActorToHUD(table)
    }

    fun hide() {
        CameraSystem.newHUD()
    }

    override fun addedToEngine(engine: Engine?) {
        CameraSystem.addActorToHUD(table)
    }

}