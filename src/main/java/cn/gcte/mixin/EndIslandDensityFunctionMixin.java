package cn.gcte.mixin;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.DensityFunction;
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

@Mixin(targets = "net.minecraft.world.level.levelgen.DensityFunctions$EndIslandDensityFunction")
public abstract class EndIslandDensityFunctionMixin
		implements DensityFunction.SimpleFunction {
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


	@Inject(method = "getHeightValue", at = @At("HEAD"), cancellable = true)
	private static void injectOldOverflow(
            SimplexNoise islandNoise, int sectionX, int sectionZ, CallbackInfoReturnable<Float> cir
    ) {
		int chunkX = sectionX / 2;
		int chunkZ = sectionZ / 2;
		int subSectionX = sectionX % 2;
		int subSectionZ = sectionZ % 2;
		float doffs = 100.0F - Mth.sqrt(sectionX * sectionX + sectionZ * sectionZ) * 8.0F;
		doffs = Mth.clamp(doffs, -100.0F, 80.0F);

		for(int xo = -12; xo <= 12; ++xo) {
			for(int zo = -12; zo <= 12; ++zo) {
				long totalChunkX = chunkX + xo;
				long totalChunkZ = chunkZ + zo;
				if (totalChunkX * totalChunkX + totalChunkZ * totalChunkZ > 4096L && islandNoise.get((double)totalChunkX, (double)totalChunkZ) < (double)-0.9F) {
					float islandSize = (Mth.abs((float)totalChunkX) * 3439.0F + Mth.abs((float)totalChunkZ) * 147.0F) % 13.0F + 9.0F;
					float xd = (float)(subSectionX - xo * 2);
					float zd = (float)(subSectionZ - zo * 2);
					float newDoffs = 100.0F - Mth.sqrt(xd * xd + zd * zd) * islandSize;
					newDoffs = Mth.clamp(newDoffs, -100.0F, 80.0F);
					doffs = Math.max(doffs, newDoffs);
				}
			}
		}
		cir.cancel();
		cir.setReturnValue(doffs);
	}
}