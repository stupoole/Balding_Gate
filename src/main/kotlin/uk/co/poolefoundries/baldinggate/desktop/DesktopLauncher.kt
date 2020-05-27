package uk.co.poolefoundries.baldinggate.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import uk.co.poolefoundries.baldinggate.AppAdapter

object DesktopLauncher {
    val appAdapter:AppAdapter
        get() {
            return AppAdapter()
        }
    val BaldingGateApplication: LwjglApplication
        get() {
            return LwjglApplication(appAdapter, LwjglApplicationConfiguration())
        }

    @JvmStatic
    fun main(arg: Array<String>) {
        BaldingGateApplication
    }

    fun quit() {
        BaldingGateApplication.exit()
    }
}

