package at.derfl007.dfroads.gui.widget

import io.github.cottonmc.cotton.gui.widget.WButton
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import net.minecraft.text.Text
import net.minecraft.util.math.Direction

class WDirectionPicker(var activeButtonDirection: Direction = Direction.NORTH) : WGridPanel() {


    private val buttons: MutableMap<Direction, WButton> = mutableMapOf()

    var enabled: Boolean = true
        set(value) {
            buttons.forEach { direction, button ->
                button.setEnabled(value && direction != activeButtonDirection)
            }
            field = value
        }

    init {
        val northButton = WButton().setLabel(Text.translatable("gui.dfroads.facing.north_short"))
        val eastButton = WButton().setLabel(Text.translatable("gui.dfroads.facing.east_short"))
        val southButton = WButton().setLabel(Text.translatable("gui.dfroads.facing.south_short"))
        val westButton = WButton().setLabel(Text.translatable("gui.dfroads.facing.west_short"))

        buttons.put(Direction.NORTH, northButton)
        this.add(northButton, 1, 0, 1, 1)
        buttons.put(Direction.WEST, westButton)
        this.add(westButton, 0, 1, 1, 1)
        buttons.put(Direction.SOUTH, southButton)
        this.add(southButton, 1, 1, 1, 1)
        buttons.put(Direction.EAST, eastButton)
        this.add(eastButton, 2, 1, 1, 1)

        buttons.forEach { direction, button -> button.setEnabled(enabled && direction != activeButtonDirection) }
    }

    fun addOnClickHandlers(handler: (direction: Direction) -> Unit) {
        buttons.forEach { direction, button ->
            button.setOnClick {
                handler(direction)
                activeButtonDirection = direction
                buttons.forEach { direction, button -> button.setEnabled(enabled && direction != activeButtonDirection) }
            }
        }
    }
}