package uk.co.poolefoundries.baldinggate.statusbars

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Pools
import kotlin.math.roundToInt

class HealthBar(
    var maxValue: Float,

    startValue: Float,
    private var style: HealthBarStyle?
) : Widget() {
    private var stepSize = 0.01F
    private val minValue = 0F
    private var animateFromValue = 0f
    private var knobPosition = 0f
    var animateDuration = 0f
    private var animateTime = 0F
    var value: Float = startValue
        set(value) {
            val newValue = clamp((value / stepSize).roundToInt() * stepSize)
            val oldValue = this.value
            if (value == oldValue) return
            val oldVisualValue = visualValue()
            field = newValue
            val changeEvent =
                Pools.obtain(
                    ChangeListener.ChangeEvent::class.java
                )
            val cancelled = fire(changeEvent)
            if (cancelled) field = oldValue else if (animateDuration > 0) {
                animateFromValue = oldVisualValue
                animateTime = animateDuration
            }
            Pools.free(changeEvent)
            return
        }
    private var animateInterpolation = Interpolation.linear
    private var visualInterpolation = Interpolation.linear

    override fun act(delta: Float) {
        super.act(delta)
        if (animateTime > 0) {
            animateTime -= delta
            val stage = stage
            if (stage != null && stage.actionsRequestRendering) Gdx.graphics.requestRendering()
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        val x = x
        val y = y
        val width = width
        val height = height
        drawAt(batch, x, y, width, height)

    }

    fun drawAt(batch: Batch, x: Float, y: Float, width: Float, height: Float) {
        val style = style
        val frame = style!!.frame
        val bar = style.bar
        val color = color
        val percent = visualPercent()
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
        val barWidth = maxBarWidth * percent
        if (bar != null) {
            if (barWidth > bar.minWidth) {
                bar.draw(
                    batch,
                    (x + bgLeftWidth).roundToInt().toFloat(),
                    (y + (height - bar.minHeight) * 0.5f).roundToInt().toFloat(),
                    barWidth,
                    bar.minHeight.roundToInt().toFloat()
                )
            }

        }

    }


    /**
     * If [animating][.setAnimateDuration] the progress bar value, this returns the value current displayed.
     */
    private fun visualValue(): Float =
        if (animateTime > 0) animateInterpolation.apply(animateFromValue, value, 1 - animateTime / animateDuration)
        else value

    private fun visualPercent(): Float =
        if (minValue == maxValue) 0F else visualInterpolation.apply((visualValue() - minValue) / (maxValue - minValue))


    /**
     * Clamps the value to the progress bar's min/max range. This can be overridden to allow a range different from the progress
     * bar knob's range.
     */
    private fun clamp(value: Float): Float {
        return MathUtils.clamp(value, minValue, maxValue)
    }


    override fun getPrefWidth(): Float = 140F

    override fun getPrefHeight(): Float {
        val frame = style!!.frame
        return frame?.minHeight ?: 0F

    }

    open class HealthBarStyle {
        /** The progress bar background, stretched only in x. Optional.  */
        var frame: Drawable? = null

        /** The Bar that is stretched to fill the background's content area.  */
        var bar: Drawable? = null

        constructor() {

        }

        constructor(style: HealthBarStyle) {
            frame = style.frame
            bar = style.bar
        }


    }


}