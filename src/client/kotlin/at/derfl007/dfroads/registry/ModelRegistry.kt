package at.derfl007.dfroads.registry

import at.derfl007.dfroads.DFRoads
import at.derfl007.dfroads.model.RoadBlockStateModel
import com.mojang.serialization.MapCodec
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.model.loading.v1.CustomUnbakedBlockStateModel
import net.minecraft.client.render.RenderLayer
import net.minecraft.util.Identifier

object ModelRegistry {

    fun registerModels() {
        DFRoads.LOGGER.info("Registering {} models", DFRoads.MOD_ID)

        CustomUnbakedBlockStateModel.register(
            DFRoads.id("block/road_full_block"),
            MapCodec.unit(RoadBlockStateModel.Unbaked(1f, 0f, 1f))
        )
        CustomUnbakedBlockStateModel.register(
            DFRoads.id("block/road_full_slope_block"),
            MapCodec.unit(RoadBlockStateModel.Unbaked(1f, 0f, 0f))
        )
        CustomUnbakedBlockStateModel.register(
            DFRoads.id("block/road_top_slope_block"),
            MapCodec.unit(RoadBlockStateModel.Unbaked(1f, 0f, 0.5f))
        )
        CustomUnbakedBlockStateModel.register(
            DFRoads.id("block/road_bottom_slope_block"),
            MapCodec.unit(RoadBlockStateModel.Unbaked(0.5f, 0f, 0f))
        )
        CustomUnbakedBlockStateModel.register(
            DFRoads.id("block/road_slab_block"),
            MapCodec.unit(RoadBlockStateModel.Unbaked(0.5f, 0f, 0.5f))
        )

        CustomUnbakedBlockStateModel.register(
            DFRoads.id("block/concrete_road_full_block"),
            MapCodec.unit(RoadBlockStateModel.Unbaked(0.9999f, 0.0001f, 0.9999f, Identifier.ofVanilla("block/light_gray_concrete")))
        )
        CustomUnbakedBlockStateModel.register(
            DFRoads.id("block/concrete_road_full_slope_block"),
            MapCodec.unit(RoadBlockStateModel.Unbaked(0.9999f, 0.0001f, 0.0001f, Identifier.ofVanilla("block/light_gray_concrete")))
        )
        CustomUnbakedBlockStateModel.register(
            DFRoads.id("block/concrete_road_top_slope_block"),
            MapCodec.unit(RoadBlockStateModel.Unbaked(0.9999f, 0.0001f, 0.5f, Identifier.ofVanilla("block/light_gray_concrete")))
        )
        CustomUnbakedBlockStateModel.register(
            DFRoads.id("block/concrete_road_bottom_slope_block"),
            MapCodec.unit(RoadBlockStateModel.Unbaked(0.5f, 0.0001f, 0.0001f, Identifier.ofVanilla("block/light_gray_concrete")))
        )
        CustomUnbakedBlockStateModel.register(
            DFRoads.id("block/concrete_road_slab_block"),
            MapCodec.unit(RoadBlockStateModel.Unbaked(0.5f, 0.0001f, 0.5f, Identifier.ofVanilla("block/light_gray_concrete")))
        )


        BlockRenderLayerMap.INSTANCE.putBlocks(
            RenderLayer.getCutoutMipped(), BlockRegistry.ROAD_FULL_SLOPE_BLOCK,
            BlockRegistry.ROAD_TOP_SLOPE_BLOCK, BlockRegistry.ROAD_BOTTOM_SLOPE_BLOCK, BlockRegistry.ROAD_SLAB_BLOCK
        )
    }
}