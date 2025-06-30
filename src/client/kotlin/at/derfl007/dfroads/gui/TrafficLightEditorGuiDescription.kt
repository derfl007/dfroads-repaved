package at.derfl007.dfroads.gui

import at.derfl007.dfroads.block.PedestrianTrafficLightBlock
import at.derfl007.dfroads.blockentity.TrafficLightBlockEntity
import at.derfl007.dfroads.gui.widget.WIconButton
import at.derfl007.dfroads.gui.widget.WPresetListItem
import at.derfl007.dfroads.gui.widget.WTrafficLightPhaseItem
import at.derfl007.dfroads.networking.BlockEntityUpdatePayload
import at.derfl007.dfroads.networking.SaveTrafficLightPresetsS2CPayload
import at.derfl007.dfroads.registry.ServerNetworkingRegistry.TRAFFIC_LIGHT_PRESETS
import io.github.cottonmc.cotton.gui.client.CottonClientScreen
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WLabel
import io.github.cottonmc.cotton.gui.widget.WListPanel
import io.github.cottonmc.cotton.gui.widget.WTextField
import io.github.cottonmc.cotton.gui.widget.data.Insets
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.NbtCompound
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos

class TrafficLightEditorGuiDescription(
    val pos: BlockPos,
    val entityType: BlockEntityType<*>,
    val entity: TrafficLightBlockEntity,
    val context: ClientPlayNetworking.Context
) : LightweightGuiDescription(), OnCloseHandler {

    val trafficLightPhases = entity.phases.mapIndexed { index, p -> Pair(index, p) }.toMutableList()

    var list: WListPanel<Pair<Int, TrafficLightBlockEntity.TrafficLightPhase>, WTrafficLightPhaseItem>
    var loadPresetList: WListPanel<String, WPresetListItem>


    val presets: MutableMap<String, List<TrafficLightBlockEntity.TrafficLightPhase>> = mutableMapOf()
    val presetKeys = presets.keys.toList()

    init {

        val root = WGridPanel()
        setRootPanel(root)
        root.insets = Insets.ROOT_PANEL
        root.setGaps(1, 1)

        list = WListPanel(trafficLightPhases, { WTrafficLightPhaseItem(entity.cachedState.block !is PedestrianTrafficLightBlock) }) { phase, widget ->
            widget.trafficLightPhase = phase.second
            widget.deleteButton.setOnClick {
                trafficLightPhases.remove(phase)
                list.remove(widget)
                list.layout()
            }
        }

        val addButton = WIconButton("plus").setOnClick {
            trafficLightPhases.add(Pair(trafficLightPhases.size, TrafficLightBlockEntity.TrafficLightPhase(time = 1)))
            list.layout()
        }

        val clearButton = WIconButton("trash").setOnClick {
            trafficLightPhases.clear()
            list.layout()
        }

        val presetNameInput = WTextField(Text.translatable("gui.dfroads.traffic_light_editor.save_preset_hint"))

        val saveAsPresetButton = WIconButton("save").setOnClick {
            savePreset(presetNameInput.text)
        }

        loadPresets()

        loadPresetList = WListPanel(presetKeys, ::WPresetListItem) { preset, listItem ->
            listItem.loadButton.label = Text.of(preset)
            listItem.loadButton.setOnClick { loadPreset(preset) }
            listItem.deleteButton.setOnClick { removePreset(preset) }
        }

        val applyButton = WIconButton("apply").setOnClick(::save)

        root.add(WLabel(Text.translatable("gui.dfroads.traffic_light_editor.phases")), 0, 0)
        root.add(list, 0, 1, 9, 7)
        root.add(WLabel(Text.translatable("gui.dfroads.traffic_light_editor.presets")), 10, 0)
        root.add(loadPresetList, 10, 1, 7, 7)
        root.add(addButton, 0, 9, 1, 1)
        root.add(clearButton, 1, 9, 1, 1)
        root.add(presetNameInput, 3, 9, 5, 1)
        root.add(saveAsPresetButton, 8, 9, 1, 1)
        root.add(applyButton, 17, 10, 1, 1)

        root.validate(this)
    }

    fun save() {
        val nbt = NbtCompound()
        nbt.putIntArray("phases", trafficLightPhases.map { it.second.mapToInt() }.toIntArray())
        entity.phases = trafficLightPhases.map { it.second }
        entity.markDirty()
        ClientPlayNetworking.send(BlockEntityUpdatePayload(pos, entityType, nbt))
        ClientPlayNetworking.send(SaveTrafficLightPresetsS2CPayload(presets.mapValues { entry -> entry.value.map { it.mapToInt() } }))
        context.client().setScreen(null)
    }

    fun loadPresets() {
        if (context.player().hasAttached(TRAFFIC_LIGHT_PRESETS)) {
            val attachment = context.player().getAttached(TRAFFIC_LIGHT_PRESETS)
            if (attachment == null) {
                return
            }
            presets.clear()
            presets.putAll(attachment.mapValues { it ->
                it.value.map { phase ->
                    TrafficLightBlockEntity.TrafficLightPhase.mapFromInt(phase)
                }
            })
        }
    }

    fun loadPreset(name: String) {
        val preset = presets.getOrDefault(name, emptyList())
        trafficLightPhases.clear()
        trafficLightPhases.addAll(preset.mapIndexed { index, p -> Pair(index, p) })
        list.layout()
        loadPresetList.layout()
    }

    fun removePreset(name: String) {
        presets.remove(name)
        context.player().setAttached(
            TRAFFIC_LIGHT_PRESETS,
            presets.mapValues { entry -> entry.value.map { it.mapToInt() } }
        )
        // reopen to reload presets (TODO: figure out if there's a better way)
        save()
        context.client()
            .setScreen(CottonClientScreen(TrafficLightEditorGuiDescription(pos, entityType, entity, context)))
    }

    fun savePreset(name: String) {
        presets.put(name, trafficLightPhases.map { it.second })
        context.player().setAttached(
            TRAFFIC_LIGHT_PRESETS,
            presets.mapValues { entry -> entry.value.map { it.mapToInt() } }
        )
        // reopen to reload presets (TODO: figure out if there's a better way)
        save()
        context.client()
            .setScreen(CottonClientScreen(TrafficLightEditorGuiDescription(pos, entityType, entity, context)))
    }

    override fun onClose() = save()
}