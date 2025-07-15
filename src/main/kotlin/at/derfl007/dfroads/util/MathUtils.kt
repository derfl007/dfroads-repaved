package at.derfl007.dfroads.util

fun Double.toRad() = this * Math.PI / 180.0
fun Float.toRad(): Float = (this * Math.PI / 180.0).toFloat()
fun Double.toDeg() = this * 180.0 / Math.PI
fun Float.toDeg() = (this * 180f / Math.PI).toFloat()