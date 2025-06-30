package at.derfl007.dfroads.gui

import at.derfl007.dfroads.Constants
import at.derfl007.dfroads.DFRoads
import at.derfl007.dfroads.blockentity.ComplexRoadSignBlockEntity
import at.derfl007.dfroads.blockentity.ComplexRoadSignBlockEntity.SignElement
import at.derfl007.dfroads.blockentity.ComplexRoadSignBlockEntity.SignElement.Type.*
import at.derfl007.dfroads.gui.widget.*
import at.derfl007.dfroads.gui.widget.WDragArea.Companion.PADDING
import at.derfl007.dfroads.networking.BlockEntityUpdatePayload
import at.derfl007.dfroads.networking.SaveComplexSignPresetsS2CPayload
import at.derfl007.dfroads.networking.SaveComplexSignPresetsS2CPayload.SignElementPreset
import at.derfl007.dfroads.registry.ServerNetworkingRegistry.COMPLEX_SIGN_PRESETS
import at.derfl007.dfroads.util.Color
import io.github.cottonmc.cotton.gui.client.CottonClientScreen
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription
import io.github.cottonmc.cotton.gui.widget.*
import io.github.cottonmc.cotton.gui.widget.data.Insets
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.MinecraftClient
import net.minecraft.nbt.NbtCompound
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos

@Suppress("UnstableApiUsage")
class ComplexRoadSignEditorGuiDescription(
    val pos: BlockPos,
    val entityType: BlockEntityType<*>,
    val entity: ComplexRoadSignBlockEntity,
    val context: ClientPlayNetworking.Context
) : LightweightGuiDescription(), OnCloseHandler {

    val signElements = entity.elements.sortedByDescending(SignElement::type).toMutableList()
    var selectedElementIndex: Int = 0

    var loadPresetList: WListPanel<String, WPresetListItem>
    val presets: MutableMap<String, SignElementPreset> = mutableMapOf()

    var previewPanel = WDragArea()
    val colorPicker = WColorPicker()
    val borderColorPicker = WColorPicker()
    val borderColorToggle = WToggleButton(Text.translatable("gui.dfroads.complex_sign_editor.border_color_toggle_label"))
    val textInput = WTextField()
    var iconTexturePicker = WTexturePicker(textures = Constants.iconTextures, columns = 4) { DFRoads.id("textures/block/${it}.png") }
    var backgroundTexturePicker = WTexturePicker(
        textures = Constants.complexSignTextures,
        columns = 12,
        textureGetter = { DFRoads.id("textures/gui/sprites/${it}.png") }
    )

    init {
        val root = WGridPanel(20)
        setRootPanel(root)
        root.insets = Insets.ROOT_PANEL

        val presetNameInput = WTextField(Text.translatable("gui.dfroads.complex_sign_editor.save_preset_hint"))

        val saveAsPresetButton = WIconButton("save").setOnClick {
            savePreset(presetNameInput.text)
        }

        loadPresets()

        loadPresetList = WListPanel(presets.keys.toList(), ::WPresetListItem) { preset, listItem ->
            listItem.loadButton.label = Text.of(preset)
            listItem.loadButton.setOnClick { root.loadPreset(preset) }
            listItem.deleteButton.setOnClick { removePreset(preset) }
        }

        val applyButton = WIconButton("apply").setOnClick(::save)

        root.renderPreview()

        val typeTextButton = WIconButton("type_text")
        val typeArrowButton = WIconButton("type_arrow")
        val typeIconButton = WIconButton("type_icon")
        val typeBoxButton = WIconButton("type_box")
        val removeElementButton = WIconButton("trash")

        typeTextButton.setOnClick {
            signElements.add(SignElement(type = TEXT, text = "enter text", width = 0.2f, height = 0.2f))
            root.renderPreview()
        }

        typeArrowButton.setOnClick {
            signElements.add(SignElement(type = ARROW, width = 0.2f, height = 0.2f))
            root.renderPreview()

        }

        typeIconButton.setOnClick {
            signElements.add(SignElement(type = ICON, width = 0.2f, height = 0.2f))
            root.renderPreview()

        }

        typeBoxButton.setOnClick {
            signElements.add(SignElement(type = BOX, width = 0.2f, height = 0.2f))
            root.renderPreview()
        }

        removeElementButton.setOnClick {
            signElements.removeAt(selectedElementIndex)
            root.renderPreview()
        }

        colorPicker.addOnClickHandlers {
            signElements[selectedElementIndex].color = Color.entries[it]
            root.renderPreview()
        }

        borderColorPicker.addOnClickHandlers {
            signElements[selectedElementIndex].borderColor = Color.entries[it]
            root.renderPreview()
        }

        borderColorToggle.setOnToggle {
            borderColorPicker.enabled = it
            signElements[selectedElementIndex].borderColor = if (it) Color.WHITE else Color.NONE
            root.renderPreview()
        }

        textInput.setChangedListener {
            if (signElements[selectedElementIndex].text != it) {
                signElements[selectedElementIndex].text = it
                root.renderPreview()
            }
        }

        iconTexturePicker.addOnClickHandlers {
            println("click")
            signElements[selectedElementIndex].iconTexture = it
            root.renderPreview()
        }

        backgroundTexturePicker.addOnClickHandlers {
            entity.backgroundTexture = it
            root.renderPreview()
        }

        root.add(WLabel(Text.translatable("gui.dfroads.complex_sign_editor.add_element_label")).setVerticalAlignment(VerticalAlignment.BOTTOM), 0, 6)
        root.add(typeTextButton, 0, 7, 1, 1)
        root.add(typeArrowButton, 1, 7, 1, 1)
        root.add(typeIconButton, 2, 7, 1, 1)
        root.add(typeBoxButton, 3, 7, 1, 1)
        root.add(removeElementButton, 4, 7, 1, 1)
        root.add(loadPresetList, 12, 0, 7, 5)
        root.add(presetNameInput, 12, 5, 5, 1)
        root.add(saveAsPresetButton, 17, 5, 1, 1)
        root.add(applyButton, 18, 10, 1, 1)
        root.add(
            WLabel(Text.translatable("gui.dfroads.complex_sign_editor.color_picker_label")).setVerticalAlignment(VerticalAlignment.BOTTOM),
            0,
            8
        )
        root.add(colorPicker, 0, 9)
        root.add(borderColorToggle, 10, 8)
        root.add(borderColorPicker, 10, 9)
        root.add(
            WLabel(Text.translatable("gui.dfroads.complex_sign_editor.background_picker_label")).setVerticalAlignment(VerticalAlignment.BOTTOM),
            10,
            6
        )
        root.add(backgroundTexturePicker, 10, 7)
        root.validate(this)
    }

    fun WGridPanel.renderPreview() {
        remove(previewPanel)

        previewPanel = WDragArea()
        previewPanel.maxX = (entity.width * 90).toInt() + PADDING
        previewPanel.maxY = (entity.height * 90).toInt() + PADDING

        val backgroundSprite =
            MinecraftClient.getInstance().guiAtlasManager.getSprite(DFRoads.id(Constants.complexSignTextures[entity.backgroundTexture]))
        val backgroundSpriteScaling = MinecraftClient.getInstance().guiAtlasManager.getScaling(backgroundSprite)

        val backgroundTexture = WDraggableWidget(
            WNineSliceSprite(
                DFRoads.id("textures/gui/sprites/${Constants.complexSignTextures[entity.backgroundTexture]}.png"),
                backgroundSpriteScaling
            ), canMove = false, canRotate = false, canResize = true, canIgnoreBoundaries = true
        )
        backgroundTexture.resizeHandlers += { w, h ->
            entity.width = w / 90f
            entity.height = h / 90f
            previewPanel.maxX = w + PADDING
            previewPanel.maxY = h + PADDING
        }

        previewPanel.add(
            backgroundTexture, 0, 0, (entity.width * 90).toInt(), (entity.height * 90).toInt()
        )

        signElements.forEachIndexed { index, element ->
            val widget: WWidget = when (element.type) {
                TEXT -> {
                    val widget = WText(Text.of(element.text))
                    widget.setColor(element.color.rgb)
                    val scalableWidget = WScalableWidget((1 / 9f) * (element.height * 90f) * 0.9f)
                    scalableWidget.add(widget)
                    scalableWidget
                }

                ARROW -> {
                    val widget =
                        WArrowSprite(DFRoads.id("textures/gui/sprites/road_sign_arrow.png"))
                    widget.setOpaqueTint(element.color.rgb)
                    widget
                }

                ICON -> {
                    val widget = WSprite(
                        DFRoads.id("textures/block/${Constants.iconTextures[element.iconTexture]}.png")
                    )
                    widget.setOpaqueTint(element.color.rgb)
                    widget
                }

                BOX -> {
                    val widget = WSprite(DFRoads.id("textures/block/white.png"))
                    widget.setOpaqueTint(element.color.rgb)
                    widget
                }
            }

            val draggable = WDraggableWidget(
                widget,
                outlineColor = if (element.borderColor != Color.NONE) element.borderColor.argb() else -1,
                rotation = element.rotation, canIgnoreBoundaries = false
            )
            draggable.selectHandlers += {
                selectedElementIndex = index

                val signElement = signElements[selectedElementIndex]

                if (signElement.type == TEXT) {
                    textInput.host = this@ComplexRoadSignEditorGuiDescription
                    add(textInput, 5, 7, 4, 1)
                } else {
                    remove(textInput)
                }

                if (signElement.type == ICON) {
                    iconTexturePicker.host = this@ComplexRoadSignEditorGuiDescription
                    add(iconTexturePicker, 5, 7, 4, 1)
                } else {
                    remove(iconTexturePicker)
                }

                colorPicker.activeButtonIndex = signElement.color.ordinal
                borderColorPicker.activeButtonIndex = signElement.borderColor.ordinal
                borderColorPicker.enabled = signElement.borderColor != Color.NONE
                borderColorToggle.toggle = signElement.borderColor != Color.NONE
                iconTexturePicker.activeButtonIndex = signElement.iconTexture
                textInput.text = signElements[selectedElementIndex].text
            }
            draggable.moveHandlers += { x, y ->
                element.x = (x - PADDING) / 90f
                element.y = (y - PADDING) / 90f
            }
            draggable.resizeHandlers += { w, h ->
                element.width = w / 90f
                element.height = h / 90f
            }
            draggable.rotationHandlers += { rotation ->
                element.rotation = rotation
            }

            previewPanel.add(
                draggable,
                (element.x * 90).toInt(),
                (element.y * 90).toInt(),
                if (element.type == TEXT) element.text.length * (1 / 9f * element.height * 90 * 6).toInt() else (element.width * 90).toInt(),
                (element.height * 90).toInt()
            )
        }

        add(previewPanel, 0, 0, 11, 6)
    }

    fun save() {
        val nbt = NbtCompound()
        nbt.copyFromCodec(SignElement.CODEC.listOf().fieldOf("elements"), signElements)
        nbt.putFloat("width", entity.width)
        nbt.putFloat("height", entity.height)
        nbt.putInt("backgroundTexture", entity.backgroundTexture)
        entity.elements = signElements
        entity.markDirty()
        ClientPlayNetworking.send(BlockEntityUpdatePayload(pos, entityType, nbt))
        ClientPlayNetworking.send(SaveComplexSignPresetsS2CPayload(presets.mapValues { entry ->
            entry.value
        }))
        context.client().setScreen(null)
    }

    fun loadPresets() {
        if (context.player().hasAttached(COMPLEX_SIGN_PRESETS)) {
            val attachment = context.player().getAttached(COMPLEX_SIGN_PRESETS)
            if (attachment == null) {
                return
            }
            presets.clear()
            presets.putAll(attachment)
        }
    }

    fun WGridPanel.loadPreset(name: String) {
        val preset = presets.getOrDefault(name, null) ?: return
        signElements.clear()
        signElements.addAll(preset.elements)
        entity.width = preset.entityWidth
        entity.height = preset.entityHeight
        previewPanel.layout()
        loadPresetList.layout()
        renderPreview()
    }

    fun removePreset(name: String) {
        presets.remove(name)
        context.player().setAttached(
            COMPLEX_SIGN_PRESETS, presets
        )
        // reopen to reload presets (TODO: figure out if there's a better way)
        save()
        context.client()
            .setScreen(CottonClientScreen(ComplexRoadSignEditorGuiDescription(pos, entityType, entity, context)))
    }

    fun savePreset(name: String) {
        presets.put(name, SignElementPreset(entity.width, entity.height, signElements))
        context.player().setAttached(
            COMPLEX_SIGN_PRESETS, presets
        )
        // reopen to reload presets (TODO: figure out if there's a better way)
        save()
        context.client()
            .setScreen(CottonClientScreen(ComplexRoadSignEditorGuiDescription(pos, entityType, entity, context)))
    }

    override fun onClose() = save()
}