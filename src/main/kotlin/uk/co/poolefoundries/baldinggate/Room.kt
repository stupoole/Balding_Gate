package uk.co.poolefoundries.baldinggate

import com.badlogic.ashley.core.Entity


// Generates a room with given width and height, returning a list of things to draw. This also draws the room with
// associated walls
//fun roomOf(width: Int, height: Int): List<Entity> {
//    val entities = mutableListOf<Entity>()
//
//    for (i in 0 until width) {
//        for (j in 0 until height) {
//            val entity = if (i == 0 || i == width - 1 || j == 0 || j == height - 1) {
//                wallTileAt(i, j)
//            } else {
//                floorTileAt(i, j)
//            }
//            entities.add(entity)
//        }
//    }
//
//    entities.add(createSkeleton(1, 1))
//
//    return entities
//}

// Function to draw a floor tile at given grid location
fun floorTileAt(x: Int, y: Int): Entity {
    val entity = Entity()

    entity.add(VisualComponent(TextureRenderable(Resources.floor())))
    entity.add(PositionComponent(x, y))

    return entity
}

// Function to draw a wall tile at a given grid location
fun wallTileAt(x: Int, y: Int): Entity {
    val entity = Entity()

    entity.add(VisualComponent(TextureRenderable(Resources.wall())))
    entity.add(PositionComponent(x, y))
    entity.add(WallComponent)

    return entity
}