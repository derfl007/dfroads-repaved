package at.derfl007.dfroads.gui.widget

import at.derfl007.dfroads.DFRoads
import at.derfl007.dfroads.blockentity.TrafficLightBlockEntity
import io.github.cottonmc.cotton.gui.widget.WButton
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WTextField
import io.github.cottonmc.cotton.gui.widget.WToggleButton
import net.minecraft.text.Text

class WTrafficLightPhaseItem(val hasYellow: Boolean = true) : WGridPanel() {

    var enabled: Boolean = true
        set(value) {
            // side effects here
            field = value
        }

    var trafficLightPhase = TrafficLightBlockEntity.TrafficLightPhase(time = 0)
        set(value) {
            field = value
            redOnButton.toggle = value.isRedOn
            yellowOnButton.toggle = value.isYellowOn
            greenOnButton.toggle = value.isGreenOn
            timeInput.text = value.time.toString()
        }

    val redOffImage = DFRoads.id("textures/block/traffic_light_red_off.png")
    val redOnImage = DFRoads.id("textures/block/traffic_light_red_on.png")
    val yellowOffImage = DFRoads.id("textures/block/traffic_light_yellow_off.png")
    val yellowOnImage = DFRoads.id("textures/block/traffic_light_yellow_on.png")
    val greenOffImage = DFRoads.id("textures/block/traffic_light_green_off.png")
    val greenOnImage = DFRoads.id("textures/block/traffic_light_green_on.png")

    val redOnButton: WToggleButton
    val yellowOnButton: WToggleButton
    val greenOnButton: WToggleButton
    val timeInput: WTextField
    val deleteButton: WButton

    init {

        horizontalGap = 5

        redOnButton = WToggleButton(redOnImage, redOffImage).setOnToggle {
            trafficLightPhase.isRedOn = it
        }
        yellowOnButton = WToggleButton(yellowOnImage, yellowOffImage).setOnToggle {
            trafficLightPhase.isYellowOn = it
        }
        greenOnButton = WToggleButton(greenOnImage, greenOffImage).setOnToggle {
            trafficLightPhase.isGreenOn = it
        }
        timeInput =
            WTextField(Text.translatable("gui.dfroads.traffic_light_editor.pulses")).setTextPredicate { input -> input matches Regex("\\d*") }
                .setChangedListener {
                    try {
                        trafficLightPhase.time = it.toInt()
                    } catch (_: NumberFormatException) {
                        trafficLightPhase.time = 0
                    }
                }
        deleteButton = WIconButton("minus")
//        deleteButton = WButton(Text.of("delete"))

        add(redOnButton, 0, 0, 1, 1)
        if (hasYellow) {
            add(yellowOnButton, 1, 0, 1, 1)
        }
        add(greenOnButton, 2, 0, 1, 1)
        add(timeInput, 3, 0, 3, 1)
        add(deleteButton, 6, 0, 1, 1)
    }
}