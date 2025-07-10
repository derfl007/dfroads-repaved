package at.derfl007.dfroads.item

import at.derfl007.dfroads.block.RoadBaseBlock
import at.derfl007.dfroads.component.RoadPainterItemComponent
import at.derfl007.dfroads.networking.RoadPainterPayload
import at.derfl007.dfroads.registry.ComponentRegistry
import at.derfl007.dfroads.util.fromHorizontalDegrees
import at.derfl007.dfroads.util.offsetMultiple
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemUsageContext
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World


class RoadPainterItem(settings: Settings) : Item(settings) {

    override fun use(world: World, user: PlayerEntity, hand: Hand): ActionResult {
        if (world.isClient) return ActionResult.SUCCESS_SERVER
        val itemStack = user.getStackInHand(hand)
        ServerPlayNetworking.send(
            user as ServerPlayerEntity,
            RoadPainterPayload(
                itemStack,
                hand.toString(),
                itemStack.getTyped(ComponentRegistry.ROAD_PAINTER_ITEM_COMPONENT)?.value ?: RoadPainterItemComponent()
            )
        )
        return ActionResult.SUCCESS
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        var returnValue: ActionResult = ActionResult.PASS

        val itemStack = context.player?.getStackInHand(context.hand)
        if (itemStack?.item !is RoadPainterItem) {
            return returnValue
        }
        val component: RoadPainterItemComponent =
            context.player?.getStackInHand(context.hand)?.getTyped(ComponentRegistry.ROAD_PAINTER_ITEM_COMPONENT)?.value
                ?: RoadPainterItemComponent()

        if (context.player!!.isSneaking) {
            val state = context.world.getBlockState(context.blockPos)
            if (state.block !is RoadBaseBlock) return ActionResult.PASS
            component.texture = state[RoadBaseBlock.Companion.TEXTURE]
            val turns =
                context.player!!.horizontalFacing.horizontalQuarterTurns + state[RoadBaseBlock.Companion.TEXTURE_FACING].horizontalQuarterTurns
            component.textureFacing = Direction.fromHorizontalQuarterTurns(turns)
            component.color = state[RoadBaseBlock.Companion.COLOR]
            return ActionResult.SUCCESS
        }

        findNextRoadBlock(
            context.world,
            context.blockPos,
            context.playerYaw.toDouble(),
            component.range,
            component,
            returnValue
        ).let {
            returnValue = it
        }
        return returnValue
    }

    /**
     * Recursively finds the next road block in the given direction and sets its properties
     * If there is a road block above, it prioritizes that one
     */
    private fun findNextRoadBlock(
        world: World,
        currentPos: BlockPos,
        yaw: Double,
        remainingRange: Int,
        component: RoadPainterItemComponent,
        returnValue: ActionResult
    ): ActionResult {
        if (remainingRange == -1) {
            return if (returnValue == ActionResult.SUCCESS) {
                ActionResult.SUCCESS
            } else {
                ActionResult.PASS
            }
        }

        val state = world.getBlockState(currentPos)
        val posUp = currentPos.up()
        val stateUp = world.getBlockState(posUp)
        val posDown = currentPos.down()
        val stateDown = world.getBlockState(posDown)
        return when {
            stateUp.block is RoadBaseBlock -> {
                if (component.interval != 0 && remainingRange % component.interval == 0) {
                    world.setBlockState(
                        posUp,
                        setBlockProperties(stateUp, Direction.fromHorizontalDegrees(yaw), component)
                    )
                }
                findNextRoadBlock(
                    world,
                    posUp.offsetMultiple(fromHorizontalDegrees(yaw)),
                    yaw,
                    remainingRange - 1,
                    component,
                    ActionResult.SUCCESS
                )
            }

            stateDown.block is RoadBaseBlock -> {
                if (component.interval != 0 && remainingRange % component.interval == 0) {
                    world.setBlockState(
                        posDown,
                        setBlockProperties(stateDown, Direction.fromHorizontalDegrees(yaw), component)
                    )
                }
                findNextRoadBlock(
                    world,
                    posDown.offsetMultiple(fromHorizontalDegrees(yaw)),
                    yaw,
                    remainingRange - 1,
                    component,
                    ActionResult.SUCCESS
                )
            }

            state.block is RoadBaseBlock -> {
                if (component.interval != 0 && remainingRange % component.interval == 0) {
                    world.setBlockState(
                        currentPos,
                        setBlockProperties(state, Direction.fromHorizontalDegrees(yaw), component)
                    )
                }
                findNextRoadBlock(
                    world, currentPos.offsetMultiple(fromHorizontalDegrees(yaw)), yaw, remainingRange - 1,
                    component, ActionResult.SUCCESS
                )
            }

            returnValue == ActionResult.SUCCESS -> {
                ActionResult.SUCCESS
            }

            else -> {
                ActionResult.PASS
            }
        }
    }

    private fun setBlockProperties(
        state: BlockState,
        playerFacing: Direction,
        component: RoadPainterItemComponent
    ): BlockState? {
        if (state.block !is RoadBaseBlock) return state
        val newColor = if (component.changeColor) component.color else state[RoadBaseBlock.COLOR]
        val newTexture = if (component.changeTexture) component.texture else state[RoadBaseBlock.TEXTURE]
        val big = component.big && newTexture.canBeBig
        val newTextureFacing =
            if (component.changeTextureFacing) {
                val turns =
                    playerFacing.horizontalQuarterTurns + component.textureFacing.opposite.horizontalQuarterTurns

                Direction.fromHorizontalQuarterTurns(turns)
            } else {
                state[RoadBaseBlock.TEXTURE_FACING]
            }
        return state.with(RoadBaseBlock.COLOR, newColor).with(RoadBaseBlock.TEXTURE, newTexture)
            .with(RoadBaseBlock.TEXTURE_FACING, newTextureFacing).with(RoadBaseBlock.BIG, big)
    }
}