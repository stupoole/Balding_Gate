package uk.co.poolefoundries.baldinggate.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import uk.co.poolefoundries.baldinggate.Application

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        LwjglApplication(Application(), LwjglApplicationConfiguration())
    }
}
