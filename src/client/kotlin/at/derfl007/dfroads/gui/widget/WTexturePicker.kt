package at.derfl007.dfroads.gui.widget

import at.derfl007.dfroads.DFRoads
import io.github.cottonmc.cotton.gui.widget.WButton
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.icon.TextureIcon
import net.minecraft.util.Identifier

class WTexturePicker(
    textures: List<String>,
    var activeButtonIndex: Int = 0,
    var columns: Int = 8,
    textureGetter: ((String) -> Identifier) = { DFRoads.id("textures/${it}.png") }
) : WGridPanel() {

    private val buttons: MutableList<WButton> = mutableListOf()

    var enabled: Boolean = true
        set(value) {
            buttons.forEachIndexed { index, button ->
                button.setEnabled(value && index != activeButtonIndex)
            }
            field = value
        }

    init {
        textures.forEachIndexed { index, texture ->
            val textureButton = WButton()
            textureButton.setIcon(TextureIcon(textureGetter(texture)))
                .setIconSize(14)
            buttons.add(textureButton)
            this.add(textureButton, index % columns, index / columns, 1, 1)
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