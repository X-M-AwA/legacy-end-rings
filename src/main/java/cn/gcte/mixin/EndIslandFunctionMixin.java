package cn.gcte.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.densityfunction.generator.EndIslandFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EndIslandFunction.class)
public abstract class EndIslandFunctionMixin implements DensityFunction {
	@ModifyConstant(
            method = "getHeightValue",
            constant = @Constant(floatValue = -100.0F)
    )
    private static float setValue(float constant, @Local(ordinal = 0, argsOnly = true) int subSectionX, @Local(ordinal = 1, argsOnly = true) int subSectionZ) {
        float i = 100.0F - Mth.sqrt(subSectionX * subSectionX + subSectionZ * subSectionZ) * 8.0F;
        return Math.clamp(i, -100.0F, 80.0F);
    }
}