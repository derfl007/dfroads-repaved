package at.derfl007.dfroads.networking

import at.derfl007.dfroads.DFRoads
import at.derfl007.dfroads.component.RoadPainterItemComponent
import at.derfl007.dfroads.registry.ComponentRegistry
import com.mojang.serialization.Codec
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.component.DataComponentTypes.CUSTOM_MODEL_DATA
import net.minecraft.component.type.CustomModelDataComponent
import net.minecraft.item.ItemStack
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Hand
import net.minecraft.util.Identifier

class RoadPainterPayload(val itemStack: ItemStack, val hand: String, val component: RoadPainterItemComponent): CustomPayload {

    companion object {
        val ROAD_PAINTER_ID: Identifier? = DFRoads.id("road_painter_payload")
        val ID: CustomPayload.Id<RoadPainterPayload> = CustomPayload.Id(ROAD_PAINTER_ID)
        val CODEC: PacketCodec<RegistryByteBuf, RoadPainterPayload> =
            PacketCodec.tuple<RegistryByteBuf, RoadPainterPayload, ItemStack, String, RoadPainterItemComponent>(
                ItemStack.PACKET_CODEC,
                RoadPainterPayload::itemStack,
                PacketCodecs.registryCodec(Codec.STRING),
                RoadPainterPayload::hand,
                PacketCodecs.registryCodec(RoadPainterItemComponent.CODEC),
                RoadPainterPayload::component,
                ::RoadPainterPayload
            )

        val ServerReceiverHandler =
            ServerPlayNetworking.PlayPayloadHandler { payload: RoadPainterPayload, context ->
                val itemStack = context.player().getStackInHand(Hand.valueOf(payload.hand))
                itemStack.set(ComponentRegistry.ROAD_PAINTER_ITEM_COMPONENT, payload.component)
                itemStack.set(CUSTOM_MODEL_DATA, CustomModelDataComponent(emptyList(), emptyList(), emptyList(), listOf(payload.component.component1().rgb)))
            }
    }

    override fun getId(): CustomPayload.Id<out CustomPayload?> {
        return ID
    }
}