package at.derfl007.dfroads.registry

import at.derfl007.dfroads.DFRoads
import at.derfl007.dfroads.renderer.ComplexRoadSignBlockEntityRenderer
import at.derfl007.dfroads.renderer.RoadSignBlockEntityRenderer
import at.derfl007.dfroads.renderer.TrafficLightBlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories

object RendererRegistry {

    fun registerRenderers() {
        DFRoads.LOGGER.info("Registering {} renderers", DFRoads.MOD_ID)

        BlockEntityRendererFactories.register(
            BlockEntityRegistry.ROAD_SIGN_BLOCK_ENTITY,
            ::RoadSignBlockEntityRenderer
        );

        BlockEntityRendererFactories.register(
            BlockEntityRegistry.TRAFFIC_LIGHT_BLOCK_ENTITY,
            ::TrafficLightBlockEntityRenderer
        );

        BlockEntityRendererFactories.register(
            BlockEntityRegistry.COMPLEX_ROAD_SIGN_BLOCK_ENTITY,
            ::ComplexRoadSignBlockEntityRenderer
        )
    }
}