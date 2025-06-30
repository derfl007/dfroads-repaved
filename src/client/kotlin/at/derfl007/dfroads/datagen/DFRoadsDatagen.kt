package at.derfl007.dfroads.datagen

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.block.Block


object DFRoadsDatagen : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(it: FabricDataGenerator) {
        val pack = it.createPack()

        pack.addProvider(::DFRoadsEnglishLangProvider)
    }

    fun FabricLanguageProvider.TranslationBuilder.addBlockWithItem(block: Block, text: String) {
        add(block, text)
        add(block.translationKey.replace("block.", "item."), text)
    }
}