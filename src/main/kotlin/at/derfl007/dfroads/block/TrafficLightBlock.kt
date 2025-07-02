package at.derfl007.dfroads.block

import at.derfl007.dfroads.blockentity.TrafficLightBlockEntity
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
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World

open class TrafficLightBlock(settings: Settings): SignPostBlock(settings, false), BlockEntityProvider {
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
                    BlockEntityUpdatePayload(pos, it.type, it.toInitialChunkDataNbt(RegistryWrapper.WrapperLookup.of(world.registryManager.stream())))
                )
            }
        }
        return ActionResult.SUCCESS
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING, POWER, UP, DOWN, NORTH, SOUTH, EAST, WEST)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        return super.getPlacementState(ctx)
    }

    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState {
        return state.with(
            FACING,
            rotation.rotate(state[FACING] as Direction)
        ) as BlockState
    }

    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState {
        return state.rotate(mirror.getRotation(state[FACING] as Direction))
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        return createCuboidShape(4.0, 0.0, 4.0, 12.0, 16.0, 12.0)
    }

    override fun getCodec(): MapCodec<out HorizontalFacingBlock?>? = createCodec { settings -> TrafficLightBlock(settings) }

    override fun createBlockEntity(
        pos: BlockPos,
        state: BlockState
    ): BlockEntity = TrafficLightBlockEntity(pos, state)

    override fun <T : BlockEntity> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return validateTicker(type, BlockEntityRegistry.TRAFFIC_LIGHT_BLOCK_ENTITY, TrafficLightBlockEntity.TrafficLightBlockEntityTicker)
    }

}