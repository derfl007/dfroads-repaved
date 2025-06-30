package at.derfl007.dfroads.networking

import at.derfl007.dfroads.DFRoads
import at.derfl007.dfroads.blockentity.ComplexRoadSignBlockEntity.SignElement
import at.derfl007.dfroads.registry.ServerNetworkingRegistry.COMPLEX_SIGN_PRESETS
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.network.packet.CustomPayload

class SaveComplexSignPresetsS2CPayload(val data: Map<String, SignElementPreset>) : CustomPayload {

    companion object {
        val ID = CustomPayload.Id<SaveComplexSignPresetsS2CPayload>(DFRoads.id("save_complex_sign_presets"))
        val CODEC: PacketCodec<PacketByteBuf?, SaveComplexSignPresetsS2CPayload?> =
            PacketCodec.tuple<PacketByteBuf, SaveComplexSignPresetsS2CPayload, Map<String, SignElementPreset>>(
                PacketCodecs.codec(
                    Codec.unboundedMap(
                        Codec.STRING,
                        SignElementPreset.CODEC
                    )),
                { obj: SaveComplexSignPresetsS2CPayload -> obj.data },
                { obj -> SaveComplexSignPresetsS2CPayload(obj) }
            )

        val ServerReceiverHandler =
            ServerPlayNetworking.PlayPayloadHandler { payload: SaveComplexSignPresetsS2CPayload, context ->
                context.player().setAttached(
                    COMPLEX_SIGN_PRESETS,
                    payload.data
                )
            }
    }

    override fun getId() = ID

    data class SignElementPreset(var entityWidth: Float, var entityHeight: Float, var elements: List<SignElement>) {
        companion object {
            val CODEC: Codec<SignElementPreset> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.FLOAT.fieldOf("x").forGetter(SignElementPreset::entityWidth),
                    Codec.FLOAT.fieldOf("y").forGetter(SignElementPreset::entityHeight),
                    SignElement.CODEC.listOf().fieldOf("element").forGetter(SignElementPreset::elements)
                ).apply(instance, ::SignElementPreset)
            }
        }
    }
}