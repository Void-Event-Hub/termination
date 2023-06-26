package synthesyzer.termination.registry.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import synthesyzer.termination.Termination;
import synthesyzer.termination.registry.blocks.custom.entity.NucleusBlockEntity;

public class TMBlockEntities {

    public static final BlockEntityType<NucleusBlockEntity> NUCLEUS_BLOCK_ENTITY = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            new Identifier(Termination.MOD_ID, "nucleus_block_entity"),
            FabricBlockEntityTypeBuilder.create(NucleusBlockEntity::new, TMBlocks.NUCLEUS_BLOCK).build()
    );

    public static void register() {
        Termination.LOGGER.info("Registering block entities");
    }

}
