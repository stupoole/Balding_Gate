package uk.co.poolefoundries.baldinggate.ai

import uk.co.poolefoundries.baldinggate.skeleton.MobInfo
import java.util.*

const val MOBS_KEY = "mobs"
const val PLAYER_IDS_KEY = "players"
const val ENEMY_IDS_KEY = "enemies"

typealias WorldState = Map<String, Any>

fun (WorldState).get(key: String, default: Int): Int {
    return this.getOrDefault(key, default) as Int
}

fun (WorldState).get(key: String, default: Boolean): Boolean {
    return this.getOrDefault(key, default) as Boolean
}

fun (WorldState).withValue(key: String, value: Any): WorldState {
    val newState = this.toMutableMap()
    newState[key] = value
    return newState.toMap()
}

fun (WorldState).getMobInfo(mobId : String) : MobInfo {
    val mobs = this[MOBS_KEY] as Map<String, MobInfo>
    return mobs[mobId] ?: error("Could not find mob with ID $mobId")
}

fun (WorldState).setMobInfo(info: MobInfo) : WorldState {
    val mobs = (this[MOBS_KEY] as Map<String, MobInfo>).toMutableMap()
    mobs[info.id] = info
    return withValue(MOBS_KEY, mobs.toMap())
}

fun (WorldState).setMobInfo(info : Collection<MobInfo>) = withValue(MOBS_KEY, info.associateBy { it.id })

fun (WorldState).getPlayerIds() : Collection<String> = this[PLAYER_IDS_KEY] as Collection<String>
fun (WorldState).setPlayerIds(ids : Collection<String>) = withValue(PLAYER_IDS_KEY, ids)

fun (WorldState).getEnemyIds() : Collection<String> = this[ENEMY_IDS_KEY] as Collection<String>
fun (WorldState).setEnemyIds(ids : Collection<String>) = withValue(ENEMY_IDS_KEY, ids)
