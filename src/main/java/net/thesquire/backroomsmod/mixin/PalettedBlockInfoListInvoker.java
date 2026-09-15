package net.thesquire.backroomsmod.mixin;

import net.minecraft.structure.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import java.util.List;

@Mixin(targets = "net.minecraft.structure.StructureTemplate$PalettedBlockInfoList")
public interface PalettedBlockInfoListInvoker {

    // Constructor invoker targeting <init>. We return Object to bypass visibility issues.
    @Invoker("<init>(Ljava/util/List;)V")
    static Object invokeConstructor(List<StructureTemplate.StructureBlockInfo> infos) {
        throw new AssertionError();
    }
}