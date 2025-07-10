package at.derfl007.dfroads.component

import at.derfl007.dfroads.block.RoadBaseBlock
import at.derfl007.dfroads.util.Color
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.util.math.Direction
import java.util.function.Function


data class RoadPainterItemComponent(
    var color: Color = Color.WHITE,
    var texture: RoadBaseBlock.RoadTexture = RoadBaseBlock.RoadTexture.ROAD_EMPTY,
    var textureFacing: Direction = Direction.NORTH,
    var range: Int = 0,
    var interval: Int = 0,
    var big: Boolean = false,
    var changeColor: Boolean = true,
    var changeTexture: Boolean = true,
    var changeTextureFacing: Boolean = true) {

    companion object {
        val CODEC: Codec<RoadPainterItemComponent> = RecordCodecBuilder.create(Function { builder ->
            builder.group(
                Color.CODEC.fieldOf("color").forGetter(RoadPainterItemComponent::color),
                RoadBaseBlock.RoadTexture.CODEC.fieldOf("texture").forGetter(RoadPainterItemComponent::texture),
                Direction.CODEC.fieldOf("textureFacing").forGetter(RoadPainterItemComponent::textureFacing),
                Codec.INT.fieldOf("range").forGetter(RoadPainterItemComponent::range),
                Codec.INT.fieldOf("interval").forGetter(RoadPainterItemComponent::interval),
                Codec.BOOL.fieldOf("big").forGetter(RoadPainterItemComponent::big),
                Codec.BOOL.fieldOf("changeColor").forGetter(RoadPainterItemComponent::changeColor),
                Codec.BOOL.fieldOf("changeTexture").forGetter(RoadPainterItemComponent::changeTexture),
                Codec.BOOL.fieldOf("changeTextureFacing").forGetter(RoadPainterItemComponent::changeTextureFacing)
            ).apply<RoadPainterItemComponent>(builder, ::RoadPainterItemComponent)
        });

        fun copy(other: RoadPainterItemComponent): RoadPainterItemComponent {
            with(other) {
                return RoadPainterItemComponent(color, texture, textureFacing, range, interval, big, changeColor, changeTexture, changeTextureFacing)
            }
        }
    }
}