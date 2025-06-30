package at.derfl007.dfroads.gui.widget

import io.github.cottonmc.cotton.gui.widget.WSprite
import io.github.cottonmc.cotton.gui.widget.data.InputResult
import io.github.cottonmc.cotton.gui.widget.data.Texture
import net.minecraft.util.Identifier

class WClickableSprite(image: Identifier): WSprite(image), WClickable {

    override var onClickHandler: (() -> Unit)? = null

    override fun onClick(x: Int, y: Int, button: Int): InputResult? {
        onClickHandler?.invoke()
        return if (onClickHandler == null) InputResult.IGNORED else InputResult.PROCESSED
    }
}