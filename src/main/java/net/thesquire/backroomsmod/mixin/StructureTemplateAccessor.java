package net.thesquire.backroomsmod.mixin;

import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(StructureTemplate.class)
public interface StructureTemplateAccessor {

    @Accessor("blockInfoLists")
    List<StructureTemplate.PalettedBlockInfoList> getBlockInfoLists();

    @Accessor("entities")
    List<StructureTemplate.StructureEntityInfo> getEntities();

    @Accessor("size")
    void setSize(Vec3i size);

    // Invoker for the private static categorize method
    @Invoker("categorize")
    static void invokeCategorize(StructureTemplate.StructureBlockInfo blockInfo,
                                 List<StructureTemplate.StructureBlockInfo> fullBlocks,
                                 List<StructureTemplate.StructureBlockInfo> blocksWithNbt,
                                 List<StructureTemplate.StructureBlockInfo> otherBlocks) {
        throw new AssertionError(); // Body is ignored by Mixin
    }

    // Invoker for the private static combineSorted method
    @Invoker("combineSorted")
    static List<StructureTemplate.StructureBlockInfo> invokeCombineSorted(List<StructureTemplate.StructureBlockInfo> fullBlocks,
                                                                          List<StructureTemplate.StructureBlockInfo> blocksWithNbt,
                                                                          List<StructureTemplate.StructureBlockInfo> otherBlocks) {
        throw new AssertionError(); // Body is ignored by Mixin
    }

}
