package at.derfl007.dfroads.registry

import at.derfl007.dfroads.DFRoads
import at.derfl007.dfroads.DFRoads.id
import at.derfl007.dfroads.networking.BlockEntityUpdatePayload
import at.derfl007.dfroads.networking.RoadPainterPayload
import at.derfl007.dfroads.networking.SaveComplexSignPresetsS2CPayload
import at.derfl007.dfroads.networking.SaveTrafficLightPresetsS2CPayload
import com.mojang.serialization.Codec
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate
import net.fabricmc.fabric.api.attachment.v1.AttachmentType
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.codec.PacketCodecs

object ServerNetworkingRegistry {

    @Suppress("UnstableApiUsage")
    val TRAFFIC_LIGHT_PRESETS: AttachmentType<Map<String, List<Int>>> =
        AttachmentRegistry.create(id("traffic_light_presets")) { builder ->
            builder.initializer { mapOf() }
                .persistent(Codec.unboundedMap(Codec.STRING, Codec.INT.listOf()))
                .syncWith(
                    PacketCodecs.codec(Codec.unboundedMap(Codec.STRING, Codec.INT.listOf())),
                    AttachmentSyncPredicate.all()
                )
        }

    @Suppress("UnstableApiUsage")
    val COMPLEX_SIGN_PRESETS: AttachmentType<Map<String, SaveComplexSignPresetsS2CPayload.SignElementPreset>> =
        AttachmentRegistry.create(id("complex_sign_presets")) { builder ->
            builder.initializer { mapOf() }
                .persistent(
                    Codec.unboundedMap(Codec.STRING, SaveComplexSignPresetsS2CPayload.SignElementPreset.CODEC)
                )
                .syncWith(
                    PacketCodecs.codec(
                        Codec.unboundedMap(
                            Codec.STRING,
                            SaveComplexSignPresetsS2CPayload.SignElementPreset.CODEC
                        )
                    ),
                    AttachmentSyncPredicate.all()
                )
        }

    fun registerPayloads() {
        DFRoads.LOGGER.info("Registering {} payloads", DFRoads.MOD_ID)

        PayloadTypeRegistry.playS2C().register(RoadPainterPayload.ID, RoadPainterPayload.CODEC)
        PayloadTypeRegistry.playC2S().register(RoadPainterPayload.ID, RoadPainterPayload.CODEC)
        PayloadTypeRegistry.playS2C().register(BlockEntityUpdatePayload.ID, BlockEntityUpdatePayload.CODEC)
        PayloadTypeRegistry.playC2S().register(BlockEntityUpdatePayload.ID, BlockEntityUpdatePayload.CODEC)
        PayloadTypeRegistry.playC2S()
            .register(SaveTrafficLightPresetsS2CPayload.ID, SaveTrafficLightPresetsS2CPayload.CODEC)
        PayloadTypeRegistry.playC2S()
            .register(SaveComplexSignPresetsS2CPayload.ID, SaveComplexSignPresetsS2CPayload.CODEC)
    }

    fun registerServerReceivers() {
        DFRoads.LOGGER.info("Registering {} server receivers", DFRoads.MOD_ID)

        ServerPlayNetworking.registerGlobalReceiver(
            BlockEntityUpdatePayload.ID, BlockEntityUpdatePayload.ServerReceiverHandler
        )
        ServerPlayNetworking.registerGlobalReceiver(
            SaveTrafficLightPresetsS2CPayload.ID, SaveTrafficLightPresetsS2CPayload.ServerReceiverHandler
        )
        ServerPlayNetworking.registerGlobalReceiver(
            SaveComplexSignPresetsS2CPayload.ID, SaveComplexSignPresetsS2CPayload.ServerReceiverHandler
        )
        ServerPlayNetworking.registerGlobalReceiver(
            RoadPainterPayload.ID, RoadPainterPayload.ServerReceiverHandler
        )
    }
}