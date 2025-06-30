package at.derfl007.dfroads.gui.widget

import io.github.cottonmc.cotton.gui.widget.WText
import io.github.cottonmc.cotton.gui.widget.data.InputResult
import net.minecraft.text.Text

class WClickableText(text: Text): WText(text), WClickable {
    override var onClickHandler: (() -> Unit)? = null

    override fun onClick(x: Int, y: Int, button: Int): InputResult? {
        onClickHandler?.invoke()
        return if (onClickHandler == null) InputResult.IGNORED else InputResult.PROCESSED
    }
}