package at.derfl007.dfroads.registry

import at.derfl007.dfroads.DFRoads
import at.derfl007.dfroads.component.RoadPainterItemComponent
import net.minecraft.component.ComponentType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object ComponentRegistry {

    lateinit var ROAD_PAINTER_ITEM_COMPONENT: ComponentType<RoadPainterItemComponent>

    fun registerComponents() {
        DFRoads.LOGGER.info("Registering {} components", DFRoads.MOD_ID);

        ROAD_PAINTER_ITEM_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(DFRoads.MOD_ID, "road_painter_item_component"),
            ComponentType.builder<RoadPainterItemComponent>().codec(RoadPainterItemComponent.CODEC).build()
        );
    }
}