package at.derfl007.dfroads.gui.widget

import io.github.cottonmc.cotton.gui.widget.WClippedPanel
import io.github.cottonmc.cotton.gui.widget.WWidget

class WClippedPanelFixed: WClippedPanel() {
    fun add(w: WWidget) {
        children.add(w)
    }
}