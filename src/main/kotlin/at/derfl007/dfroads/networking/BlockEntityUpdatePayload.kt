package at.derfl007.dfroads.networking

import at.derfl007.dfroads.DFRoads
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.network.packet.CustomPayload
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos


class BlockEntityUpdatePayload(val pos: BlockPos, val blockEntityType: BlockEntityType<*>, val nbt: NbtCompound) :
    CustomPayload {

    companion object {
        val BLOCK_ENTITY_UPDATE_ID: Identifier? = DFRoads.id("block_entity_update")
        val ID: CustomPayload.Id<BlockEntityUpdatePayload> = CustomPayload.Id(BLOCK_ENTITY_UPDATE_ID)
        val CODEC: PacketCodec<RegistryByteBuf, BlockEntityUpdatePayload> =
            PacketCodec.tuple<RegistryByteBuf, BlockEntityUpdatePayload, BlockPos, BlockEntityType<*>, NbtCompound>(
                BlockPos.PACKET_CODEC,
                BlockEntityUpdatePayload::pos,
                PacketCodecs.registryValue<BlockEntityType<*>?>(RegistryKeys.BLOCK_ENTITY_TYPE),
                BlockEntityUpdatePayload::blockEntityType,
                PacketCodecs.UNLIMITED_NBT_COMPOUND,
                BlockEntityUpdatePayload::nbt,
                ::BlockEntityUpdatePayload
            )

        val ServerReceiverHandler =
            ServerPlayNetworking.PlayPayloadHandler { payload: BlockEntityUpdatePayload, context ->
                val player = context.player()
                val world = player.world!!
                world.getBlockEntity(payload.pos, payload.blockEntityType)?.ifPresent { entity ->
                    entity.read(
                        payload.nbt,
                        RegistryWrapper.WrapperLookup.of(player.registryManager.stream())
                    )
                    val state = world.getBlockState(payload.pos)
                    world.updateListeners(payload.pos, state, state, 0);
                }
            }
    }

    override fun getId(): CustomPayload.Id<out CustomPayload?> {
        return ID
    }
}