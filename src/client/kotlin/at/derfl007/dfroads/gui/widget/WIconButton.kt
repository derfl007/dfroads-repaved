package at.derfl007.dfroads.gui.widget

import at.derfl007.dfroads.DFRoads
import io.github.cottonmc.cotton.gui.widget.WButton
import io.github.cottonmc.cotton.gui.widget.icon.TextureIcon

/**
 * Helper class to create a [WButton] with a [TextureIcon]
 * Only works with icons stored in this mod's assets under textures/gui/buttons!
 */
class WIconButton(textureName: String): WButton(TextureIcon(DFRoads.id("textures/gui/sprites/buttons/$textureName.png")))