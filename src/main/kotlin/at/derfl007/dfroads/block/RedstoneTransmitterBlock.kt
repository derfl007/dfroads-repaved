package at.derfl007.dfroads.block

import com.mojang.serialization.MapCodec
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.IntProperty
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.block.WireOrientation
import net.minecraft.world.tick.TickPriority

open class RedstoneTransmitterBlock(settings: Settings): HorizontalFacingBlock(settings) {

    init {
        defaultState = stateManager.defaultState.with(POWER, 0).with(FACING, Direction.NORTH)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(POWER).add(FACING)
    }

    override fun emitsRedstonePower(state: BlockState): Boolean {
        return true
    }

    override fun getStrongRedstonePower(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        direction: Direction
    ): Int {
        return getWeakRedstonePower(state, world, pos, direction)
    }

    override fun getWeakRedstonePower(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        direction: Direction
    ): Int {
        val power = state[POWER]
        return if (world.getBlockState(pos.offset(direction.opposite)).block is RedstoneTransmitterBlock) power else power - 1
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        return super.getPlacementState(ctx)
            ?.with(POWER, computePower(ctx.world, ctx.blockPos))
            ?.with(FACING, ctx.horizontalPlayerFacing.opposite)
    }

    override fun onBlockAdded(
        state: BlockState,
        world: World,
        pos: BlockPos,
        oldState: BlockState,
        notify: Boolean
    ) {
        super.onBlockAdded(state, world, pos, oldState, notify)
        checkPowerChange(world, pos, state, true)
        world.scheduleBlockTick(pos, this, 0, TickPriority.NORMAL)
    }

    override fun scheduledTick(state: BlockState?, world: ServerWorld?, pos: BlockPos?, random: Random?) {
        super.scheduledTick(state, world, pos, random)
        checkPowerChange(world!!, pos!!, state!!, false)
        world.scheduleBlockTick(pos, this, 0, TickPriority.NORMAL)
    }

    override fun neighborUpdate(
        state: BlockState?,
        world: World?,
        pos: BlockPos?,
        sourceBlock: Block?,
        wireOrientation: WireOrientation?,
        notify: Boolean
    ) {
        super.neighborUpdate(state, world, pos, sourceBlock, wireOrientation, notify)
        checkPowerChange(world!!, pos!!, state!!, false)
    }

    override fun onBreak(world: World?, pos: BlockPos?, state: BlockState?, player: PlayerEntity?): BlockState? {
        super.onBreak(world, pos, state, player)
        return checkPowerChange(world!!, pos!!, state!!, true)
    }

    /**
     * Calculates the power of the block
     */
    private fun computePower(world: World, pos: BlockPos): Int {
        if (world.getBlockState(pos).block !is RedstoneTransmitterBlock) return 0
        val power = world.getReceivedRedstonePower(pos)
        if (power < 1) return 0
        return power - 1
    }

    /**
     * Checks if the power of the block has changed and updates it if necessary (or forced by [force])
     */
    private fun checkPowerChange(world: World, pos: BlockPos, state: BlockState, force: Boolean): BlockState? {
        if (world.getBlockState(pos).block !is RedstoneTransmitterBlock) return null
        val power = computePower(world, pos)
        var newState = state
        if (force || power != state[POWER]) {
            newState = newState.with(POWER, power)
            world.setBlockState(pos, newState, NOTIFY_ALL)
        }
        return newState
    }

    override fun getCodec(): MapCodec<out HorizontalFacingBlock?>? = createCodec { settings -> RedstoneTransmitterBlock(settings) }

    companion object {
        val POWER: IntProperty = IntProperty.of("power", 0, 15)
    }
}