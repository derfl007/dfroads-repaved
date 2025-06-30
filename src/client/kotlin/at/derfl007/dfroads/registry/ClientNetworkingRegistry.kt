package at.derfl007.dfroads.registry

import at.derfl007.dfroads.DFRoads
import at.derfl007.dfroads.blockentity.ComplexRoadSignBlockEntity
import at.derfl007.dfroads.blockentity.RoadSignBlockEntity
import at.derfl007.dfroads.blockentity.TrafficLightBlockEntity
import at.derfl007.dfroads.gui.*
import at.derfl007.dfroads.networking.BlockEntityUpdatePayload
import at.derfl007.dfroads.networking.RoadPainterPayload
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.registry.RegistryWrapper

object ClientNetworkingRegistry {
    fun registerReceivers() {
        DFRoads.LOGGER.info("Registering {} client receivers", DFRoads.MOD_ID)


        ClientPlayNetworking.registerGlobalReceiver(RoadPainterPayload.ID) { payload, _ ->
            MinecraftClient.getInstance().setScreen(DFRoadsScreen(RoadPainterGuiDescription(payload.itemStack, payload.hand)))
        }

        ClientPlayNetworking.registerGlobalReceiver(BlockEntityUpdatePayload.ID) { payload, context ->
            context.client().world?.getBlockEntity(payload.pos, payload.blockEntityType)?.ifPresent {
                when (it) {
                    is TrafficLightBlockEntity -> {
                        it.read(
                            payload.nbt,
                            RegistryWrapper.WrapperLookup.of(context.client().networkHandler?.registryManager?.stream())
                        )
                        MinecraftClient.getInstance().setScreen(
                            DFRoadsScreen(
                                TrafficLightEditorGuiDescription(
                                    payload.pos, payload.blockEntityType, it,
                                    context
                                )
                            )
                        )
                    }

                    is ComplexRoadSignBlockEntity -> {
                        it.read(
                            payload.nbt,
                            RegistryWrapper.WrapperLookup.of(context.client().networkHandler?.registryManager?.stream())
                        )
                        MinecraftClient.getInstance().setScreen(
                            DFRoadsScreen(
                                ComplexRoadSignEditorGuiDescription(payload.pos, payload.blockEntityType, it, context)
                            )
                        )
                    }

                    is RoadSignBlockEntity -> {
                        it.read(
                            payload.nbt,
                            RegistryWrapper.WrapperLookup.of(context.client().networkHandler?.registryManager?.stream())
                        )
                        MinecraftClient.getInstance().setScreen(
                            DFRoadsScreen(
                                SimpleRoadSignGuiDescription(payload.pos, payload.blockEntityType, it, context)
                            )
                        )
                    }

                    else -> {
                        println("No suitable block entity found at ${payload.pos}")
                    }
                }
            }
        }
    }
}