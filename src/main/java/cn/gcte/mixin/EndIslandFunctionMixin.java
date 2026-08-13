package cn.gcte.mixin;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.generator.EndIslandFunction;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndIslandFunction.class)
public abstract class EndIslandFunctionMixin implements DensityFunction {
	@Final
    @Mutable
	@Shadow
	private SimplexNoise islandNoise;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void overwriteIslandNoise(long seed, CallbackInfo ci) {
		RandomSource islandRandom = new LegacyRandomSource(seed);
		islandRandom.consumeCount(17292);
		this.islandNoise = new SimplexNoise(islandRandom);
	}

	@ModifyConstant(
            method = "getHeightValue",
            constant = @Constant(floatValue = -100.0F)
    )
    private static float setValue(float constant, @Local(ordinal = 0, argsOnly = true) int subSectionX, @Local(ordinal = 1, argsOnly = true) int subSectionZ) {
        float i = 100.0F - Mth.sqrt(subSectionX * subSectionX + subSectionZ * subSectionZ) * 8.0F;
        return Math.clamp(i, -100.0F, 80.0F);
    }
}
