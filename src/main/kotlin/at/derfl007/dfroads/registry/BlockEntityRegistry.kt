package at.derfl007.dfroads.registry

import at.derfl007.dfroads.DFRoads
import at.derfl007.dfroads.blockentity.ComplexRoadSignBlockEntity
import at.derfl007.dfroads.blockentity.RoadSignBlockEntity
import at.derfl007.dfroads.blockentity.TrafficLightBlockEntity
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier


object BlockEntityRegistry {

    lateinit var ROAD_SIGN_BLOCK_ENTITY: BlockEntityType<RoadSignBlockEntity>
    lateinit var COMPLEX_ROAD_SIGN_BLOCK_ENTITY: BlockEntityType<ComplexRoadSignBlockEntity>
    lateinit var TRAFFIC_LIGHT_BLOCK_ENTITY: BlockEntityType<TrafficLightBlockEntity>

    fun registerBlockEntities() {
        DFRoads.LOGGER.info("Registering {} blockEntities", DFRoads.MOD_ID);

        ROAD_SIGN_BLOCK_ENTITY = register(
            "road_sign_block", ::RoadSignBlockEntity, BlockRegistry.ROAD_SIGN_BLOCK, BlockRegistry.LED_SIGN_BLOCK
        )
        COMPLEX_ROAD_SIGN_BLOCK_ENTITY = register(
            "complex_road_sign_block", ::ComplexRoadSignBlockEntity, BlockRegistry.COMPLEX_ROAD_SIGN_BLOCK
        )
        TRAFFIC_LIGHT_BLOCK_ENTITY = register(
            "traffic_light_block",
            ::TrafficLightBlockEntity,
            BlockRegistry.TRAFFIC_LIGHT_BLOCK,
            BlockRegistry.PEDESTRIAN_TRAFFIC_LIGHT_BLOCK
        )
    }

    private fun <T : BlockEntity?> register(
        name: String,
        entityFactory: FabricBlockEntityTypeBuilder.Factory<out T>,
        vararg blocks: Block
    ): BlockEntityType<T> {
        val id: Identifier = DFRoads.id(name)
        return Registry.register(
            Registries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.create(entityFactory, *blocks).build()
        )
    }
}