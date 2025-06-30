package at.derfl007.dfroads.gui.widget

import io.github.cottonmc.cotton.gui.widget.WButton
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import net.minecraft.text.Text

class WPresetListItem(): WGridPanel() {


    var name: String = ""

    val deleteButton = WIconButton("trash")
    val loadButton = WButton(Text.of(name))

    init {
        horizontalGap = 5
        add(loadButton, 0, 0, 4, 1)
        add(deleteButton, 4, 0, 1, 1)
    }

}