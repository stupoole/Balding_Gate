package uk.co.poolefoundries.baldinggate.statusbars

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import kotlin.math.roundToInt

class StaminaBar(
    var maxAP: Int,
    startValue: Int,
    private var style: StaminaBarStyle?
) : Widget() {
    var value: Int = startValue


    override fun draw(batch: Batch, parentAlpha: Float) {
        val x = x
        val y = y
        val width = width
        val height = height
        drawAt(batch,x,y,width,height)
    }

    fun drawAt(batch: Batch, x: Float, y: Float, width: Float, height: Float) {
        val style = style
        val frame = style!!.frame
        val filledBar = style.filledBar
        val emptyBar = style.emptyBar
        val color = color
        val currentAP = value
        batch.setColor(color.r, color.g, color.b, color.a)

        var maxBarWidth = width
        var bgLeftWidth = 0f
        if (frame != null) {
            frame.draw(
                batch, x, (y + (height - frame.minHeight) * 0.5f).roundToInt().toFloat(),
                width, frame.minHeight.roundToInt().toFloat()
            )
            bgLeftWidth = frame.leftWidth
            maxBarWidth -= bgLeftWidth + frame.rightWidth
        }
        if (currentAP == 0) {
            return
        }

        val barWidth = maxBarWidth / maxAP.toFloat()

        if (filledBar != null) {
            for (bar in 0 until currentAP) {
                val left = x + bgLeftWidth + barWidth * bar
                filledBar.draw(
                    batch,
                    left,
                    (y + (height - filledBar.minHeight) * 0.5f).roundToInt().toFloat(),
                    barWidth,
                    filledBar.minHeight.roundToInt().toFloat()
                )

            }
        }
        if (emptyBar != null) {
            for (bar in currentAP until maxAP) {
                val left = x + bgLeftWidth + barWidth * bar
                emptyBar.draw(
                    batch,
                    left,
                    (y + (height - emptyBar.minHeight) * 0.5f).roundToInt().toFloat(),
                    barWidth,
                    emptyBar.minHeight.roundToInt().toFloat()
                )

            }
        }

    }

    override fun getPrefWidth(): Float = 140F

    override fun getPrefHeight(): Float {
        val bg = style!!.frame
        return bg?.minHeight ?: 0F

    }

    fun updateValues(currentAP: Int, maxAP: Int) {
        value = currentAP
        this.maxAP = maxAP
    }

    open class StaminaBarStyle {
        /** The progress bar background, stretched only in x. Optional.  */
        var frame: Drawable? = null

        /** The Bar that is stretched to fill the background's content area.  */
        var filledBar: Drawable? = null
        var emptyBar: Drawable? = null

        constructor(){

        }

        constructor(style: StaminaBarStyle){
            frame = style.frame
            filledBar = style.filledBar
            emptyBar = style.emptyBar
        }


    }


}