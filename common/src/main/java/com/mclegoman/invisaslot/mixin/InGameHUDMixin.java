package com.mclegoman.invisaslot.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHUDMixin {
	@Shadow @Final private MinecraftClient client;

	@Inject(method = "renderHotbarItem", at = @At("HEAD"), cancellable = true)
	private void mclminvisaslot_renderHotbarItem(DrawContext context, int x, int y, RenderTickCounter tickCounter, PlayerEntity player, ItemStack stack, int seed, CallbackInfo ci) {
		if (!stack.isEmpty()) {
			if (stack.getCount() != 1) {
				String string = String.valueOf(stack.getCount());
				context.getMatrices().translate(0.0F, 0.0F, 200.0F);
				context.drawText(this.client.textRenderer, string, x + 19 - 2 - this.client.textRenderer.getWidth(string), y + 6 + 3, 16777215, true);
			}
		}
		ci.cancel();
	}
}
