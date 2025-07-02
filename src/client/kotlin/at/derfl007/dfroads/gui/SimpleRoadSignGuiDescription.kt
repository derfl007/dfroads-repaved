package at.derfl007.dfroads.gui

import at.derfl007.dfroads.Constants
import at.derfl007.dfroads.DFRoads
import at.derfl007.dfroads.blockentity.RoadSignBlockEntity
import at.derfl007.dfroads.gui.widget.WIconButton
import at.derfl007.dfroads.gui.widget.WTexturePicker
import at.derfl007.dfroads.networking.BlockEntityUpdatePayload
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription
import io.github.cottonmc.cotton.gui.widget.*
import io.github.cottonmc.cotton.gui.widget.data.Axis
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment
import io.github.cottonmc.cotton.gui.widget.data.Insets
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.NbtCompound
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import java.util.function.IntConsumer

class SimpleRoadSignGuiDescription(
    val pos: BlockPos,
    val entityType: BlockEntityType<*>,
    val entity: RoadSignBlockEntity,
    val context: ClientPlayNetworking.Context
) : LightweightGuiDescription(), OnCloseHandler {

    private var signTexture: WSprite
    private var signTextureName: WLabel
    private var texturePicker: WTexturePicker
    private var sizeSlider: WLabeledSlider

    init {
        val root = WGridPanel(20)
        setRootPanel(root)
        root.insets = Insets.ROOT_PANEL

        root.add(
            WLabel(Text.translatable("gui.dfroads.simple_sign_editor.texture_preview_title")),
            10, 0, 7, 1
        );

        signTexture = WSprite(DFRoads.id("textures/block/${Constants.signTextures[entity.texture]}.png"))
        root.add(signTexture, 10, 1, 6, 6)

        signTextureName =
            WLabel(Text.translatable("gui.dfroads.simple_sign_editor.textures.${Constants.signTextures[entity.texture]}"))
        signTextureName.verticalAlignment = VerticalAlignment.CENTER
        signTextureName.horizontalAlignment = HorizontalAlignment.CENTER
        root.add(signTextureName, 10, 7, 6, 1)

        texturePicker = WTexturePicker(Constants.signTextures)
        texturePicker.columns = 9
        texturePicker.activeButtonIndex = entity.texture
        texturePicker.addOnClickHandlers {
            entity.texture = it
            signTexture.setImage(DFRoads.id("textures/block/${Constants.signTextures[entity.texture]}.png"))
            signTextureName.text =
                Text.translatable("gui.dfroads.simple_sign_editor.textures.${Constants.signTextures[entity.texture]}")
        }

        val texturePickerContainer = WScrollPanel(texturePicker)

        root.add(texturePickerContainer, 0, 0, 9, 10)

        sizeSlider = WLabeledSlider(0, 3, Axis.HORIZONTAL)
        sizeSlider.label = Text.translatable("gui.dfroads.simple_sign_editor.size_slider_label", entity.size)
        sizeSlider.labelUpdater = WLabeledSlider.LabelUpdater { value ->
            Text.translatable("gui.dfroads.simple_sign_editor.size_slider_label", value)
        }
        sizeSlider.value = entity.size
        sizeSlider.draggingFinishedListener = IntConsumer {
            entity.size = it
        }
        root.add(sizeSlider, 10, 8, 6, 1)

        val applyButton = WIconButton("apply").setOnClick(::save)
        root.add(applyButton, 16, 10, 1, 1)


        root.validate(this)
    }

    fun save() {
        val nbt = NbtCompound()
        nbt.putInt("texture", texturePicker.activeButtonIndex)
        nbt.putInt("size", sizeSlider.value)
        entity.markDirty()
        ClientPlayNetworking.send(BlockEntityUpdatePayload(pos, entityType, nbt))
        context.client().setScreen(null)
    }

    override fun onClose() = save()
}