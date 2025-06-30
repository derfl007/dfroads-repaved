package at.derfl007.dfroads.datagen

import at.derfl007.dfroads.datagen.DFRoadsDatagen.addBlockWithItem
import at.derfl007.dfroads.registry.BlockRegistry
import at.derfl007.dfroads.registry.ItemRegistry
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

class DFRoadsEnglishLangProvider(dataOutput: FabricDataOutput, wrapperLookup: CompletableFuture<RegistryWrapper.WrapperLookup>): FabricLanguageProvider(dataOutput, "en_us", wrapperLookup) {

    override fun generateTranslations(wrapperLookup: RegistryWrapper.WrapperLookup?, tb: TranslationBuilder) {
        tb.addBlockWithItem(BlockRegistry.ROAD_BLOCK, "Road")
        tb.addBlockWithItem(BlockRegistry.ROAD_SLAB_BLOCK, "Road Slab")
        tb.addBlockWithItem(BlockRegistry.ROAD_FULL_SLOPE_BLOCK, "Road Slope")
        tb.addBlockWithItem(BlockRegistry.ROAD_TOP_SLOPE_BLOCK, "Road Half Slope (Top)")
        tb.addBlockWithItem(BlockRegistry.ROAD_BOTTOM_SLOPE_BLOCK, "Road Half Slope (Bottom)")
        tb.addBlockWithItem(BlockRegistry.ROAD_SIGN_BLOCK, "Road Sign")
        tb.addBlockWithItem(BlockRegistry.SIGN_POST_BLOCK, "Sign Post")
        tb.addBlockWithItem(BlockRegistry.TRAFFIC_LIGHT_BLOCK, "Traffic Light")
        tb.addBlockWithItem(BlockRegistry.LED_SIGN_BLOCK, "LED Road Sign")
        tb.addBlockWithItem(BlockRegistry.COMPLEX_ROAD_SIGN_BLOCK, "Complex Road Sign")
        tb.addBlockWithItem(BlockRegistry.PEDESTRIAN_TRAFFIC_LIGHT_BLOCK, "Pedestrian Traffic Light")
        tb.addBlockWithItem(BlockRegistry.GUARD_RAIL_BLOCK, "Guardrail")
        tb.addBlockWithItem(BlockRegistry.SIDEWALK_CURB_BLOCK, "Sidewalk")
        tb.addBlockWithItem(BlockRegistry.TRAFFIC_CONE_BLOCK, "Traffic Cone")
        tb.addBlockWithItem(BlockRegistry.STREET_LAMP_BLOCK, "Street Lamp")
        tb.addBlockWithItem(BlockRegistry.BOLLARD_BLOCK, "Bollard")
        tb.addBlockWithItem(BlockRegistry.CONCRETE_BARRIER_BLOCK, "Concrete Barrier")

        // GUI
        tb.add("gui.dfroads.complex_sign_editor.add_element_label", "Add")
        tb.add("gui.dfroads.complex_sign_editor.background_picker_label", "Background")
        tb.add("gui.dfroads.complex_sign_editor.border_color_toggle_label", "Border color")
        tb.add("gui.dfroads.complex_sign_editor.color_picker_label", "Color")
        tb.add("gui.dfroads.complex_sign_editor.presets", "Presets")
        tb.add("gui.dfroads.complex_sign_editor.save_preset_hint", "Preset name")
        tb.add("gui.dfroads.facing.east_short", "E")
        tb.add("gui.dfroads.facing.north_short", "N")
        tb.add("gui.dfroads.facing.south_short", "S")
        tb.add("gui.dfroads.facing.west_short", "W")
        tb.add("gui.dfroads.generic.close", "Close")
        tb.add("gui.dfroads.road_painter.alternate_button_label", "Alternate")
        tb.add("gui.dfroads.road_painter.change_color_button_label", "Change Color")
        tb.add("gui.dfroads.road_painter.change_texture_button_label", "Change Texture")
        tb.add("gui.dfroads.road_painter.change_texture_facing_button_label", "Change Texture Facing")
        tb.add("gui.dfroads.road_painter.color_label", "Color")
        tb.add("gui.dfroads.road_painter.current_color_label", "Color: %s")
        tb.add("gui.dfroads.road_painter.current_range_label", "Range: %d")
        tb.add("gui.dfroads.road_painter.current_texture_facing", "Texture Facing: %s")
        tb.add("gui.dfroads.road_painter.current_texture_label", "Texture: %s")
        tb.add("gui.dfroads.road_painter.texture_facing_label", "Texture Facing")
        tb.add("gui.dfroads.road_painter.texture_label", "Texture")
        tb.add("gui.dfroads.road_painter.texture_preview_title", "Preview")
        tb.add("gui.dfroads.simple_sign_editor.texture_preview_title", "Preview")
        tb.add("gui.dfroads.simple_sign_editor.size_slider_label", "Size: %d")
        tb.add("gui.dfroads.traffic_light_editor.phases", "Phases")
        tb.add("gui.dfroads.traffic_light_editor.presets", "Presets")
        tb.add("gui.dfroads.traffic_light_editor.pulses", "Pulses")
        tb.add("gui.dfroads.traffic_light_editor.save_preset_hint", "Preset name")

        tb.add("itemGroup.dfroads", "DFRoads")

        tb.add(ItemRegistry.ROAD_PAINTER, "Road Painter")

        try {
            val existingFilePath = dataOutput.modContainer.findPath("assets/${dataOutput.modId}/lang/en_us.json").get()
            tb.add(existingFilePath)
        } catch (e: Exception) {
            println("No existing en_us.json found")
        }
    }
}