package at.derfl007.dfroads.block

import at.derfl007.dfroads.blockentity.ComplexRoadSignBlockEntity
import at.derfl007.dfroads.blockentity.RoadSignBlockEntity
import at.derfl007.dfroads.blockentity.Utils.validateTicker
import at.derfl007.dfroads.networking.BlockEntityUpdatePayload
import at.derfl007.dfroads.registry.BlockEntityRegistry
import com.mojang.serialization.MapCodec
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.registry.RegistryWrapper
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.state.StateManager
import net.minecraft.util.ActionResult
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World

open class RoadSignBlock(settings: Settings) : SignPostBlock(settings), BlockEntityProvider {

    init {
        defaultState = stateManager.defaultState
            .with(FACING, Direction.NORTH)
            .with(POWER, 0)
            .with(UP, false)
            .with(DOWN, false)
            .with(NORTH, false)
            .with(SOUTH, false)
            .with(EAST, false)
            .with(WEST, false)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING, POWER, UP, DOWN, NORTH, SOUTH, EAST, WEST)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        return super.getPlacementState(ctx)
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape? {
        val facing = state[FACING] ?: Direction.NORTH
        val blockEntity = world.getBlockEntity(pos)
        var minX: Double
        var minY: Double
        var maxX: Double
        var maxY: Double
        when (blockEntity) {
            is RoadSignBlockEntity -> {
                val size = when (blockEntity.size) {
                    0 -> 0.5f
                    1 -> 1f
                    2 -> 2f
                    3 -> 4f
                    else -> 1f
                }
                minX = (0.5 - size / 2) * 16
                minY = (0.5 - size / 2) * 16
                maxX = (0.5 + size / 2) * 16
                maxY = (0.5 + size / 2) * 16
            }

            is ComplexRoadSignBlockEntity -> {
                minX = (0.5 - blockEntity.width / 2) * 16
                minY = (0.5 - blockEntity.height / 2) * 16
                maxX = (0.5 + blockEntity.width / 2) * 16
                maxY = (0.5 + blockEntity.height / 2) * 16
            }

            else -> {
                minX = (0.5 - 1 / 2) * 16
                minY = (0.5 - 1 / 2) * 16
                maxX = (0.5 + 1 / 2) * 16
                maxY = (0.5 + 1 / 2) * 16
            }
        }
        val signShape = getSignShape(minX, minY, maxX, maxY)
        val postShape = getPostShape()
        val outlineShape = VoxelShapes.union(signShape, postShape)
        return VoxelShapes.createHorizontalFacingShapeMap(outlineShape)[facing.opposite]
    }

    open fun getSignShape(minX: Double, minY: Double, maxX: Double, maxY: Double): VoxelShape =
        createCuboidShape(minX, minY, 13.1, maxX, maxY, 13.2)
    open fun getPostShape(): VoxelShape = createCuboidShape(6.0, 6.0, 6.0, 10.0, 10.0, 12.8)

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hit: BlockHitResult
    ): ActionResult {
        if (world.isClient) {
            return ActionResult.SUCCESS_SERVER
        } else if (player is ServerPlayerEntity) {
            world.getBlockEntity(pos)?.let {
                ServerPlayNetworking.send(
                    player,
                    BlockEntityUpdatePayload(
                        pos,
                        it.type,
                        it.toInitialChunkDataNbt(RegistryWrapper.WrapperLookup.of(world.registryManager.stream()))
                    )
                )
            }
        }
        return ActionResult.SUCCESS
    }
    
    override fun getCodec(): MapCodec<out HorizontalFacingBlock?>? = createCodec { settings -> RoadSignBlock(settings) }
    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = RoadSignBlockEntity(pos, state)

    override fun <T : BlockEntity> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return validateTicker(
            type,
            BlockEntityRegistry.ROAD_SIGN_BLOCK_ENTITY,
            BlockEntityTicker { _, _, _, _  -> }
        )
    }

}