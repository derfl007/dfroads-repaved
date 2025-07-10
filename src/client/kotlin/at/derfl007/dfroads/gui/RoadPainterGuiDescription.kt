package at.derfl007.dfroads.gui

import at.derfl007.dfroads.DFRoads
import at.derfl007.dfroads.block.RoadBaseBlock
import at.derfl007.dfroads.component.RoadPainterItemComponent
import at.derfl007.dfroads.gui.widget.*
import at.derfl007.dfroads.networking.RoadPainterPayload
import at.derfl007.dfroads.registry.ComponentRegistry
import at.derfl007.dfroads.util.Color
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription
import io.github.cottonmc.cotton.gui.widget.*
import io.github.cottonmc.cotton.gui.widget.data.Axis
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment
import io.github.cottonmc.cotton.gui.widget.data.Insets
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import java.util.function.IntConsumer

class RoadPainterGuiDescription(val itemStack: ItemStack, val hand: String) : LightweightGuiDescription(), OnCloseHandler {

    //    private var compassTexture: WSprite
    private var roadTextureContainer: WRotatablePanel
    private var roadTexture: WSprite
    private var roadTextureName: WLabel
    private var roadBgTexture: WSprite
    private var colorPicker: WColorPicker
    private var texturePicker: WTexturePicker
    private var textureFacingPicker: WDirectionPicker
    private var changeColorButton: WToggleButton
    private var changeTextureButton: WToggleButton
    private var changeTextureFacingButton: WToggleButton
    private var rangeSlider: WLabeledSlider
    private var intervalSlider: WLabeledSlider
    private var bigButton: WToggleButton
    
    var component = RoadPainterItemComponent.copy(itemStack.getTyped(ComponentRegistry.ROAD_PAINTER_ITEM_COMPONENT)?.value()!!)

    init {
        val root = WGridPanel()
        setRootPanel(root)
//        root.setSize(256, 240)
        root.insets = Insets.ROOT_PANEL

        root.add(
            WLabel(Text.translatable("gui.dfroads.road_painter.texture_preview_title")),
            10, 0, 7, 1
        )
        // Road texture Background
        roadBgTexture = WSprite(DFRoads.id("textures/block/road.png"))
        root.add(roadBgTexture, 10, 1, 6, 6)

        roadTextureContainer = WRotatablePanel(component.textureFacing.opposite.positiveHorizontalDegrees)
        roadTextureContainer.setInsets(Insets.NONE)
        root.add(roadTextureContainer, 10, 1, 6, 6)

        // Road texture
        roadTexture =
            WSprite(DFRoads.id("textures/block/${component.texture}.png"))
        roadTexture.setOpaqueTint(component.color.rgb)
        roadTexture.setImage(DFRoads.id("textures/block/${component.texture}.png"))
        roadTexture.setUv(0f, 0f, 1f, 1f)
        roadTextureContainer.add(roadTexture, 0, 0, 6 * 18, 6 * 18)

        roadTextureName = WLabel(Text.translatable("gui.dfroads.road_painter.textures.${component.texture}"))
        roadTextureName.verticalAlignment = VerticalAlignment.CENTER
        roadTextureName.horizontalAlignment = HorizontalAlignment.RIGHT
        root.add(roadTextureName, 1, 7, 15, 1)

        // Color
        changeColorButton = WToggleButton(Text.translatable("gui.dfroads.road_painter.change_color_button_label"))
        changeColorButton.toggle = component.changeColor
        changeColorButton.setOnToggle { isOn ->
            if (isOn) {
                component.changeColor = true
                roadTexture.setOpaqueTint(component.color.rgb)
            } else {
                component.changeColor = false
                roadTexture.setOpaqueTint(Color.WHITE.rgb)
            }
            colorPicker.enabled = isOn
        }
        root.add(changeColorButton, 0, 0, 8, 1)

        colorPicker = WColorPicker(component.color.ordinal)
        colorPicker.addOnClickHandlers {
            component.color = Color.entries[it]
            roadTexture.setOpaqueTint(component.color.rgb)
        }

        root.add(colorPicker, 0, 1)

        // Texture
        changeTextureButton = WToggleButton(Text.translatable("gui.dfroads.road_painter.change_texture_button_label"))
        changeTextureButton.toggle = component.changeTexture
        changeTextureButton.setOnToggle { isOn ->
            component.changeTexture = isOn
            texturePicker.enabled = isOn
        }
        root.add(changeTextureButton, 0, 3, 8, 1)

        texturePicker = WTexturePicker(RoadBaseBlock.RoadTexture.entries.map { it.toString() }, component.texture.ordinal)
        texturePicker.addOnClickHandlers {
            component.texture = RoadBaseBlock.RoadTexture.entries[it]
            roadTexture.setImage(DFRoads.id("textures/block/${component.texture}.png"))
            roadTextureName.text =
                Text.translatable("gui.dfroads.road_painter.textures.${component.texture}")
        }
        root.add(texturePicker, 0, 4)

        // Texture facing
        changeTextureFacingButton = WToggleButton(Text.translatable("gui.dfroads.road_painter.change_texture_facing_button_label"))
        changeTextureFacingButton.toggle = component.changeTextureFacing
        changeTextureFacingButton.setOnToggle { isOn ->
            component.changeTextureFacing = isOn
            textureFacingPicker.enabled = isOn
        }
        root.add(changeTextureFacingButton, 0, 8, 8, 1)

        textureFacingPicker = WDirectionPicker(component.textureFacing)
        textureFacingPicker.addOnClickHandlers {
            component.textureFacing = it
            roadTextureContainer.rotate(component.textureFacing.opposite.positiveHorizontalDegrees)
        }
        root.add(textureFacingPicker, 2, 9)

        // Alternate
        intervalSlider = WLabeledSlider(1, 6, Axis.HORIZONTAL)
        intervalSlider.value = component.interval
        intervalSlider.label = Text.translatable("gui.dfroads.road_painter.interval_label", component.interval)
        intervalSlider.labelUpdater = WLabeledSlider.LabelUpdater { value ->
            Text.translatable("gui.dfroads.road_painter.interval_label", value)
        }
        intervalSlider.draggingFinishedListener = IntConsumer {
            component.interval = it
        }
        root.add(intervalSlider, 10, 8, 6, 1)

        // Range
        rangeSlider = WLabeledSlider(0, 64, Axis.HORIZONTAL)
        rangeSlider.value = component.range
        rangeSlider.label = Text.translatable("gui.dfroads.road_painter.current_range_label", component.range)
        rangeSlider.labelUpdater = WLabeledSlider.LabelUpdater { value ->
            Text.translatable("gui.dfroads.road_painter.current_range_label", value)
        }
        rangeSlider.draggingFinishedListener = IntConsumer {
            component.range = it
        }
        root.add(rangeSlider, 10, 9, 6, 1)

        bigButton = WToggleButton(Text.translatable("gui.dfroads.road_painter.big_button_label"))
        bigButton.toggle = component.big
        bigButton.setOnToggle {
            component.big = it
        }
        root.add(bigButton, 10, 10, 6, 1)

        val applyButton = WIconButton("apply").setOnClick(::save)
        root.add(applyButton, 16, 11, 1, 1)

        root.validate(this)
    }

    fun save() {
        ClientPlayNetworking.send(RoadPainterPayload(itemStack, hand, component))
        MinecraftClient.getInstance().setScreen(null)
    }

    override fun onClose() = save()
}