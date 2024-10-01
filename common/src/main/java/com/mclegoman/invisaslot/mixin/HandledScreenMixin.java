package com.mclegoman.invisaslot.mixin;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T> {
	@Shadow @Nullable private Slot touchDragSlotStart;
	@Shadow private ItemStack touchDragStack;
	@Shadow private boolean touchIsRightClickDrag;
	@Shadow @Final protected T handler;
	@Shadow protected boolean cursorDragging;
	@Shadow @Final protected Set<Slot> cursorDragSlots;
	@Shadow private int heldButtonType;
	@Shadow protected abstract void calculateOffset();
	protected HandledScreenMixin(Text title) {
		super(title);
	}
	@Inject(method = "drawItem", at = @At("HEAD"), cancellable = true)
	private void mclminvisaslot_drawItem(DrawContext context, ItemStack stack, int x, int y, String amountText, CallbackInfo ci) {
		context.getMatrices().push();
		context.getMatrices().translate(0.0F, 0.0F, 232.0F);
		if (!stack.isEmpty()) {
			if (stack.getCount() != 1 || amountText != null) {
				amountText = amountText == null ? String.valueOf(stack.getCount()) : amountText;
				context.getMatrices().translate(0.0F, 0.0F, 200.0F);
				assert this.client != null;
				context.drawText(this.client.textRenderer, amountText, x + 19 - 2 - this.client.textRenderer.getWidth(amountText), y + 6 + 3, 16777215, true);
			}
		}
		context.getMatrices().pop();
		ci.cancel();
	}

	@Inject(method = "drawSlot", at = @At("HEAD"), cancellable = true)
	private void mclminvisaslot_drawSlot(DrawContext context, Slot slot, CallbackInfo ci) {
		int slotX = slot.x;
		int slotY = slot.y;
		ItemStack itemStack = slot.getStack();
		boolean bl = false;
		boolean bl2 = slot == this.touchDragSlotStart && !this.touchDragStack.isEmpty() && !this.touchIsRightClickDrag;
		ItemStack itemStack2 = this.handler.getCursorStack();
		String string = null;
		int k;
		if (slot == this.touchDragSlotStart && !this.touchDragStack.isEmpty() && this.touchIsRightClickDrag && !itemStack.isEmpty()) {
			itemStack = itemStack.copyWithCount(itemStack.getCount() / 2);
		} else if (this.cursorDragging && this.cursorDragSlots.contains(slot) && !itemStack2.isEmpty()) {
			if (this.cursorDragSlots.size() == 1) {
				return;
			}

			if (ScreenHandler.canInsertItemIntoSlot(slot, itemStack2, true) && this.handler.canInsertIntoSlot(slot)) {
				bl = true;
				k = Math.min(itemStack2.getMaxCount(), slot.getMaxItemCount(itemStack2));
				int l = slot.getStack().isEmpty() ? 0 : slot.getStack().getCount();
				int m = ScreenHandler.calculateStackSize(this.cursorDragSlots, this.heldButtonType, itemStack2) + l;
				if (m > k) {
					m = k;
					String var10000 = Formatting.YELLOW.toString();
					string = var10000 + k;
				}

				itemStack = itemStack2.copyWithCount(m);
			} else {
				this.cursorDragSlots.remove(slot);
				this.calculateOffset();
			}
		}

		context.getMatrices().push();
		context.getMatrices().translate(0.0F, 0.0F, 100.0F);
		if (itemStack.isEmpty() && slot.isEnabled()) {
			Pair<Identifier, Identifier> pair = slot.getBackgroundSprite();
			if (pair != null) {
				assert this.client != null;
				Sprite sprite = this.client.getSpriteAtlas(pair.getFirst()).apply(pair.getSecond());
				context.drawSprite(slotX, slotY, 0, 16, 16, sprite);
				bl2 = true;
			}
		}

		if (!bl2) {
			if (bl) {
				context.fill(slotX, slotY, slotX + 16, slotY + 16, -2130706433);
			}
			if (!itemStack.isEmpty()) {
				if (itemStack.getCount() != 1 || string != null) {
					string = string == null ? String.valueOf(itemStack.getCount()) : string;
					context.getMatrices().translate(0.0F, 0.0F, 200.0F);
					assert this.client != null;
					context.drawText(this.client.textRenderer, string, slotX + 19 - 2 - this.client.textRenderer.getWidth(string), slotY + 6 + 3, 16777215, true);
				}
			}
		}

		context.getMatrices().pop();
		ci.cancel();
	}
}
