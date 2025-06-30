package at.derfl007.dfroads.util

import net.minecraft.util.StringIdentifiable

/**
 * Enum with the 16 colors used by this mod. The [rgb] value should be provided in `0xRRGGBB` format.
 *
 * Optional opacity can be provided when using the [Color.argb] method
 *
 * Use [Color.colors] instead of [Color.entries] if you only want the colors without the [Color.NONE] option
 */
enum class Color(val rgb: Int): StringIdentifiable {
    WHITE(0xffffff),
    ORANGE(0xe26100),
    MAGENTA(0xa92b9f),
    LIGHT_BLUE(0x176FC1),
    YELLOW(0xf1af0d),
    LIME(0x5eaa10),
    PINK(0xd5648e),
    GRAY(0x34383c),
    LIGHT_GRAY(0x9e9e9e),
    CYAN(0x4c7c9e),
    PURPLE(0x9e4c9e),
    BLUE(0x292b91),
    BROWN(0x613a1a),
    GREEN(0x0B8F4B),
    RED(0x8f1a1a),
    BLACK(0x000000),
    NONE(-1);

    /**
     * Returns the color as an [Int] in `0xAARRGGBB` format.
     * The [opacity] is mapped from `0f..1f` to a `0x00..0xFF` and appended in front of the rrggbb hex value
     */
    fun argb(opacity: Float = 1f): Int {
        val opacityHex = (opacity.coerceIn(0f..1f) * 255).toInt() shl 24
        return opacityHex or rgb
    }

    override fun asString(): String? {
        return toString().lowercase()
    }

    companion object {
        fun of(color: Int): Color {
            return entries.find { it.rgb == color } ?: NONE
        }

        /**
         * Like [entries] but without the [NONE] option
         */
        val colors = entries.filter { color -> color != NONE }

        val CODEC = StringIdentifiable.createCodec(Color::values);
    }
}