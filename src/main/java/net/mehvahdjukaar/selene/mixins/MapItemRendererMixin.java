package net.mehvahdjukaar.selene.mixins;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.mehvahdjukaar.selene.map.CustomDecoration;
import net.mehvahdjukaar.selene.map.CustomDecorationHolder;
import net.mehvahdjukaar.selene.map.client.MapDecorationRenderHandler;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.world.storage.MapData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MapItemRenderer.class)
public abstract class MapItemRendererMixin {


    @Inject(method = "render", at = @At("RETURN"), cancellable = true)
    private void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, MapData mapData, boolean isOnFrame, int light, CallbackInfo ci) {
        if (mapData instanceof CustomDecorationHolder) {
            int index = mapData.decorations.size();
            for (CustomDecoration decoration : ((CustomDecorationHolder) mapData).getCustomDecorations().values()){

                if(MapDecorationRenderHandler.render(decoration,matrixStack,buffer,mapData,isOnFrame,light,index)) index++;
            }
        }
    }

}