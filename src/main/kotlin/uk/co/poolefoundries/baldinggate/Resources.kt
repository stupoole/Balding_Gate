package uk.co.poolefoundries.baldinggate

import com.badlogic.gdx.graphics.Texture


object Resources {
    private val resources = mutableMapOf<String, Texture>()

    fun get(name: String): Texture {
        return if (resources.containsKey(name)) {
            resources[name]!!
        } else {
            val texture = Texture(name)
            resources[name] = texture
            texture
        }
    }
}