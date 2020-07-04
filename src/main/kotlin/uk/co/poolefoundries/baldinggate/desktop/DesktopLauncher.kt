package uk.co.poolefoundries.baldinggate.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import uk.co.poolefoundries.baldinggate.core.BaldingGateGame

object DesktopLauncher {
    lateinit var application:LwjglApplication
    @JvmStatic
    fun main(arg: Array<String>) {
        val configuration = LwjglApplicationConfiguration()
        configuration.title = "Balding Gate"
        configuration.width = 1280
        configuration.height = 720
        application = LwjglApplication(BaldingGateGame, configuration)
    }
}

