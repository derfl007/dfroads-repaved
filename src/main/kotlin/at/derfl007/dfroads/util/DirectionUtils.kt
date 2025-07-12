/**
 * Random direction related extension functions and stuff
 */

package at.derfl007.dfroads.util

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.EightWayDirection
import kotlin.math.abs
import kotlin.math.floor

val Direction.isHorizontal: Boolean
    get() = this != Direction.DOWN && this != Direction.UP

val HORIZONTAL_DIRECTIONS = Direction.entries.filter { it.isHorizontal }

fun fromHorizontalEighthQuarterTurns(eighthRotation: Int) = EightWayDirection.entries[abs((eighthRotation + 4) % EightWayDirection.entries.size)]

fun fromHorizontalDegrees(angle: Double) = fromHorizontalEighthQuarterTurns(floor(angle / 45.0 + 0.5).toInt() and 7)

fun BlockPos.offsetEightWay(direction: EightWayDirection): BlockPos {
    var pos = this
    direction.directions.forEach { pos = pos.offset(it) }
    return pos
}