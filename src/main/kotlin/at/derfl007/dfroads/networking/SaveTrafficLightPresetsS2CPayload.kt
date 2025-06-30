package at.derfl007.dfroads.networking

import at.derfl007.dfroads.DFRoads
import at.derfl007.dfroads.registry.ServerNetworkingRegistry.TRAFFIC_LIGHT_PRESETS
import com.mojang.serialization.Codec
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.network.packet.CustomPayload

class SaveTrafficLightPresetsS2CPayload(val data: Map<String, List<Int>>) : CustomPayload {

    companion object {
        val ID = CustomPayload.Id<SaveTrafficLightPresetsS2CPayload>(DFRoads.id("save_traffic_light_presets"))
        val CODEC: PacketCodec<PacketByteBuf?, SaveTrafficLightPresetsS2CPayload?> =
            PacketCodec.tuple<PacketByteBuf, SaveTrafficLightPresetsS2CPayload, Map<String, List<Int>>>(
                PacketCodecs.codec(Codec.unboundedMap(Codec.STRING, Codec.INT.listOf())),
                { obj: SaveTrafficLightPresetsS2CPayload -> obj.data },
                { obj -> SaveTrafficLightPresetsS2CPayload(obj) }
            )

        val ServerReceiverHandler =
            ServerPlayNetworking.PlayPayloadHandler { payload: SaveTrafficLightPresetsS2CPayload, context ->
                context.player().setAttached(
                    TRAFFIC_LIGHT_PRESETS,
                    payload.data
                )
            }
    }

    override fun getId() = ID
}