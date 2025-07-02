package at.derfl007.dfroads.gui.widget

import at.derfl007.dfroads.DFRoads
import at.derfl007.dfroads.util.Color
import io.github.cottonmc.cotton.gui.widget.WButton
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.data.Insets
import io.github.cottonmc.cotton.gui.widget.icon.TextureIcon

class WColorPicker(activeButtonIndex: Int = 0, var columns: Int = 8, gaps: Int = 0) : WGridPanel() {

    var activeButtonIndex = activeButtonIndex
        set(value) {
            field = value
            buttons.forEachIndexed { index, button ->
                button.setEnabled(enabled && index != value)
            }
        }
    private val buttons: MutableList<WButton> = mutableListOf()

    var enabled: Boolean = true
        set(value) {
            buttons.forEachIndexed { index, button ->
                button.setEnabled(value && index != activeButtonIndex)
            }
            field = value
        }

    init {
        this.setInsets(Insets(1, 1))
        Color.colors.forEachIndexed { index, color ->
            val colorButton = WButton()
            colorButton.setIcon(TextureIcon(DFRoads.id("textures/block/road_white.png")).setColor(color.argb()))
                .setIconSize(14)
            buttons.add(colorButton)
            this.add(colorButton, index % columns, index / columns, 1, 1)
        }
        buttons.forEachIndexed { index, button -> button.setEnabled(enabled && index != activeButtonIndex) }
    }

    fun addOnClickHandlers(handler: (index: Int) -> Unit) {
        buttons.forEachIndexed { index, button ->
            button.setOnClick {
                handler(index)
                activeButtonIndex = index
                buttons.forEachIndexed { index, button -> button.setEnabled(enabled && index != activeButtonIndex) }
            }
        }
    }
}