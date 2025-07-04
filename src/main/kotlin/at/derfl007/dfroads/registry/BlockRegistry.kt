package at.derfl007.dfroads.registry

import at.derfl007.dfroads.DFRoads
import at.derfl007.dfroads.block.*
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.block.AbstractBlock.Settings
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.MapColor
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier


object BlockRegistry {

    // Road Blocks
    lateinit var ROAD_BLOCK: RoadFullBlock
    lateinit var ROAD_SLAB_BLOCK: RoadSlabBlock
    lateinit var ROAD_FULL_SLOPE_BLOCK: RoadFullSlopeBlock
    lateinit var ROAD_TOP_SLOPE_BLOCK: RoadTopSlopeBlock
    lateinit var ROAD_BOTTOM_SLOPE_BLOCK: RoadBottomSlopeBlock
    lateinit var CONCRETE_ROAD_BLOCK: RoadFullBlock
    lateinit var CONCRETE_ROAD_SLAB_BLOCK: RoadSlabBlock
    lateinit var CONCRETE_ROAD_FULL_SLOPE_BLOCK: RoadFullSlopeBlock
    lateinit var CONCRETE_ROAD_TOP_SLOPE_BLOCK: RoadTopSlopeBlock
    lateinit var CONCRETE_ROAD_BOTTOM_SLOPE_BLOCK: RoadBottomSlopeBlock
    lateinit var ROAD_SIGN_BLOCK: RoadSignBlock
    lateinit var LED_SIGN_BLOCK: LedSignBlock
    lateinit var COMPLEX_ROAD_SIGN_BLOCK: ComplexRoadSignBlock
    lateinit var SIGN_POST_BLOCK: SignPostBlock
    lateinit var SIGN_POST_THICK_BLOCK: SignPostThickBlock
    lateinit var TRAFFIC_LIGHT_BLOCK: TrafficLightBlock
    lateinit var PEDESTRIAN_TRAFFIC_LIGHT_BLOCK: PedestrianTrafficLightBlock
    lateinit var GUARD_RAIL_BLOCK: GuardRailBlock
    lateinit var SIDEWALK_CURB_BLOCK: SidewalkCurbBlock
    lateinit var TRAFFIC_CONE_BLOCK: TrafficConeBlock
    lateinit var STREET_LAMP_BLOCK: StreetLampBlock
    lateinit var BOLLARD_BLOCK: BollardBlock
    lateinit var CONCRETE_BARRIER_BLOCK: ConcreteBarrierBlock


    fun registerBlocks() {
        DFRoads.LOGGER.info("Registering {} blocks", DFRoads.MOD_ID);

        val roadBlockSettingsSolid = Settings.copy(Blocks.STONE).velocityMultiplier(1.2f).mapColor(MapColor.BLACK)
        val roadBlockSettingsTransparent =
            Settings.copy(Blocks.STONE).velocityMultiplier(1.2f).solidBlock { _, _, _ -> false }.nonOpaque()

        ROAD_BLOCK = register(
            "road_full_block", { settings -> RoadFullBlock(settings) }, true, roadBlockSettingsSolid
        )
        ROAD_SLAB_BLOCK = register(
            "road_slab_block", { settings -> RoadSlabBlock(settings) }, true, roadBlockSettingsTransparent
        )
        ROAD_FULL_SLOPE_BLOCK = register(
            "road_full_slope_block", { settings -> RoadFullSlopeBlock(settings) }, true, roadBlockSettingsTransparent
        )
        ROAD_TOP_SLOPE_BLOCK = register(
            "road_top_slope_block", { settings -> RoadTopSlopeBlock(settings) }, true, roadBlockSettingsTransparent
        )
        ROAD_BOTTOM_SLOPE_BLOCK = register(
            "road_bottom_slope_block",
            { settings -> RoadBottomSlopeBlock(settings) },
            true,
            roadBlockSettingsTransparent
        )
        CONCRETE_ROAD_BLOCK = register(
            "concrete_road_full_block", { settings -> RoadFullBlock(settings) }, true, roadBlockSettingsSolid
        )
        CONCRETE_ROAD_SLAB_BLOCK = register(
            "concrete_road_slab_block", { settings -> RoadSlabBlock(settings) }, true, roadBlockSettingsTransparent
        )
        CONCRETE_ROAD_FULL_SLOPE_BLOCK = register(
            "concrete_road_full_slope_block", { settings -> RoadFullSlopeBlock(settings) }, true, roadBlockSettingsTransparent
        )
        CONCRETE_ROAD_TOP_SLOPE_BLOCK = register(
            "concrete_road_top_slope_block", { settings -> RoadTopSlopeBlock(settings) }, true, roadBlockSettingsTransparent
        )
        CONCRETE_ROAD_BOTTOM_SLOPE_BLOCK = register(
            "concrete_road_bottom_slope_block",
            { settings -> RoadBottomSlopeBlock(settings) },
            true,
            roadBlockSettingsTransparent
        )
        ROAD_SIGN_BLOCK = register(
            "road_sign_block", ::RoadSignBlock, true, Settings.copy(Blocks.IRON_BARS)
        )
        LED_SIGN_BLOCK = register(
            "led_sign_block", ::LedSignBlock, true, Settings.copy(Blocks.IRON_BARS)
        )
        COMPLEX_ROAD_SIGN_BLOCK = register(
            "complex_road_sign_block", ::ComplexRoadSignBlock, true, Settings.copy(Blocks.IRON_BARS)
        )
        SIGN_POST_BLOCK = register(
            "sign_post_block", ::SignPostBlock, true, Settings.copy(Blocks.IRON_BLOCK)
        )
        SIGN_POST_THICK_BLOCK = register(
            "sign_post_thick_block", ::SignPostThickBlock, true, Settings.copy(Blocks.IRON_BLOCK)
        )
        TRAFFIC_LIGHT_BLOCK = register(
            "traffic_light_block", ::TrafficLightBlock, true
        )
        PEDESTRIAN_TRAFFIC_LIGHT_BLOCK = register(
            "pedestrian_traffic_light_block", ::PedestrianTrafficLightBlock, true
        )
        GUARD_RAIL_BLOCK = register(
            "guard_rail_block", ::GuardRailBlock, true, Settings.copy(Blocks.IRON_BARS)
        )
        SIDEWALK_CURB_BLOCK = register(
            "sidewalk_curb_block", ::SidewalkCurbBlock, true, Settings.copy(Blocks.STONE)
        )
        TRAFFIC_CONE_BLOCK = register(
            "traffic_cone_block", ::TrafficConeBlock, true, Settings.copy(Blocks.BLACK_CONCRETE)
        )
        BOLLARD_BLOCK = register(
            "bollard_block", ::BollardBlock, true, Settings.copy(Blocks.BLACK_CONCRETE)
        )
        CONCRETE_BARRIER_BLOCK =
            register("concrete_barrier_block", ::ConcreteBarrierBlock, true, Settings.copy(Blocks.BLACK_CONCRETE))
        STREET_LAMP_BLOCK = register(
            "street_lamp_block",
            ::StreetLampBlock,
            true,
            Settings.copy(Blocks.IRON_BLOCK)
                .luminance { state -> if (state[RedstoneTransmitterBlock.Companion.POWER] > 0) 15 else 0 })
    }

    private fun <T : Block> register(
        name: String,
        blockFactory: (Settings) -> T,
        shouldRegisterItem: Boolean,
        settings: Settings = Settings.copy(Blocks.STONE),
        itemSettings: Item.Settings = Item.Settings().equippableUnswappable(EquipmentSlot.HEAD),
    ): T {
        // Create a registry key for the block
        val blockKey = keyOfBlock(name)
        // Create the block instance
        val block: T = blockFactory.invoke(settings.registryKey(blockKey))

        if (shouldRegisterItem) {
            // Items need to be registered with a different type of registry key, but the ID
            // can be the same.
            val itemKey = keyOfItem(name)

            val blockItem = BlockItem(block, itemSettings.registryKey(itemKey))
            ItemGroupEvents.modifyEntriesEvent(ItemRegistry.ITEM_GROUP_KEY).register {
                it.add(blockItem)
            }
            Registry.register(Registries.ITEM, itemKey, blockItem)
        }

        return Registry.register(Registries.BLOCK, blockKey, block)
    }

    private fun keyOfBlock(name: String): RegistryKey<Block> {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DFRoads.MOD_ID, name))
    }

    private fun keyOfItem(name: String): RegistryKey<Item> {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DFRoads.MOD_ID, name))
    }
}