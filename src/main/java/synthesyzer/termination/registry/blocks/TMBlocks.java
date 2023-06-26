package synthesyzer.termination.registry.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import synthesyzer.termination.Termination;
import synthesyzer.termination.registry.blocks.custom.block.NucleusBlock;

public class TMBlocks {

    public static final Block NUCLEUS_BLOCK = registerBlock("nucleus_block", new NucleusBlock(Block.Settings.copy(Blocks.DEEPSLATE).resistance(36000f).luminance(state -> 15).requiresTool()), ItemGroup.MISC);

    private static Block registerBlock(String name, Block block, ItemGroup tab) {
        registerBlockItem(name, block, tab);
        return Registry.register(Registry.BLOCK, new Identifier(Termination.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block, ItemGroup tab) {
        return Registry.register(Registry.ITEM, new Identifier(Termination.MOD_ID, name), new BlockItem(block, new Item.Settings().group(tab)));
    }

    public static void register() {
        Termination.LOGGER.info("Registering blocks");
    }

}
