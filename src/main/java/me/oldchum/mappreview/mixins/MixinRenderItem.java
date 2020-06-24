package me.oldchum.mappreview.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.MapData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Old Chum
 * @since 6/22/20
 */
@Mixin(RenderItem.class)
public class MixinRenderItem {
    @Shadow
    public float zLevel;

    @Inject(method = "renderItemModelIntoGUI", at = @At("HEAD"), cancellable = true)
    public void renderItemModelIntoGUIPRE (ItemStack stack, int x, int y, IBakedModel bakedmodel, CallbackInfo cb) {
        if (!stack.isEmpty() && stack.getItem() instanceof ItemMap) {
            Minecraft mc = Minecraft.getMinecraft();
            MapData data = ((ItemMap) stack.getItem()).getMapData(stack, mc.world);

            if (data != null) {
                GlStateManager.pushMatrix();
                GlStateManager.translate((float) x, (float) y, 100.0F + zLevel);
                // 0.125 because the map has to be scaled down from exactly 128x128 to 16x16 (Inventory slot size)
                GlStateManager.scale(0.125F, 0.125F, 0.125F);
                mc.entityRenderer.getMapItemRenderer().renderMap(data, true);
                GlStateManager.popMatrix();

                cb.cancel();
            }
        }
    }
}
