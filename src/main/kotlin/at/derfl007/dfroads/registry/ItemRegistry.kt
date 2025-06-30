package at.derfl007.dfroads.registry

import at.derfl007.dfroads.DFRoads
import at.derfl007.dfroads.DFRoads.id
import at.derfl007.dfroads.component.RoadPainterItemComponent
import at.derfl007.dfroads.item.RoadPainterItem
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.CustomModelDataComponent
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.text.Text
import net.minecraft.util.Identifier


object ItemRegistry {

    val ITEM_GROUP: ItemGroup? = FabricItemGroup.builder().icon { BlockRegistry.ROAD_BLOCK.asItem().defaultStack }
        .displayName(Text.translatable("itemGroup.dfroads")).build()
    val ITEM_GROUP_KEY: RegistryKey<ItemGroup> = RegistryKey.of(Registries.ITEM_GROUP.key, id("item_group"))


    lateinit var ROAD_PAINTER: RoadPainterItem

    fun registerItems() {
        DFRoads.LOGGER.info("Registering {} items", DFRoads.MOD_ID);

        ROAD_PAINTER = register(
            "road_painter", ::RoadPainterItem, Item.Settings().component(
                ComponentRegistry.ROAD_PAINTER_ITEM_COMPONENT,
                RoadPainterItemComponent()
            ).component(
                DataComponentTypes.CUSTOM_MODEL_DATA,
                CustomModelDataComponent(emptyList(), emptyList(), emptyList(), listOf(0xFFFFFF))
            )
        )
    }

    fun registerItemGroups() {
        DFRoads.LOGGER.info("Registering {} item groups", DFRoads.MOD_ID);

        Registry.register(Registries.ITEM_GROUP, ITEM_GROUP_KEY, ITEM_GROUP)
    }

    fun <T: Item> register(name: String, itemFactory: (settings: Item.Settings) -> T, settings: Item.Settings = Item.Settings()): T {
        // Create the item key.
        val itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DFRoads.MOD_ID, name))

        // Create the item instance.
        val item: T = itemFactory.invoke(settings.registryKey(itemKey))

        ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP_KEY).register {
            it.add(item)
        }

        // Register the item.
        Registry.register(Registries.ITEM, itemKey, item)

        return item
    }
}