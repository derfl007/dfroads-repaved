package at.derfl007.dfroads.gui

import io.github.cottonmc.cotton.gui.GuiDescription
import io.github.cottonmc.cotton.gui.client.CottonClientScreen
import net.minecraft.text.Text

class DFRoadsScreen(val guiDescription: GuiDescription, title: Text = Text.of("")) : CottonClientScreen(title, guiDescription) {
    override fun close() {
        if (guiDescription is OnCloseHandler) {
            guiDescription.onClose()
        }
        super.close()
    }
}