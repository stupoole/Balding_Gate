package uk.co.poolefoundries.baldinggate.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import uk.co.poolefoundries.baldinggate.core.BaldingGateGame

object DesktopLauncher {
    lateinit var application:LwjglApplication
    @JvmStatic
    fun main(arg: Array<String>) {
        application = LwjglApplication(BaldingGateGame(), LwjglApplicationConfiguration())

    }
}

