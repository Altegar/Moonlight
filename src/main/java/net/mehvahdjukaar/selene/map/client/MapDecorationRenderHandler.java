package net.mehvahdjukaar.selene.map.client;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.mehvahdjukaar.selene.map.CustomDecoration;
import net.mehvahdjukaar.selene.map.CustomDecorationType;
import net.mehvahdjukaar.selene.map.MapDecorationHandler;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;

import java.util.Map;

public class MapDecorationRenderHandler {
    private static final Map<CustomDecorationType<?,?>, DecorationRenderer<?>> RENDERERS = Maps.newHashMap();

    static {
        bindSimpleRenderer(MapDecorationHandler.GENERIC_STRUCTURE_TYPE);
    }

    //TODO: stitch all textures on an atlas for simple renderers

    public static <T extends CustomDecoration> void bindDecorationRenderer(CustomDecorationType<T,?> type, DecorationRenderer<T> renderer){
        if(RENDERERS.containsKey(type)){
            throw new IllegalArgumentException("Duplicate map decoration renderer registration " + type.getRegistryId());
        }
        else {
            RENDERERS.put(type, renderer);
        }
    }

    /**
     * binds the default simple decoration renderer.<br>
     * will associate each decoration a texture based on its name<br>
     * texture location will be as follows:<br>
     * "textures/map/[type.id].png" under the namespace the decoration is registered under<br>
     *
     * For more control use {@link MapDecorationRenderHandler#bindDecorationRenderer(CustomDecorationType, DecorationRenderer)}<br>
     */
    public static void bindSimpleRenderer(CustomDecorationType<?,?> type){
        ResourceLocation texture = new ResourceLocation(type.getId().getNamespace(),"textures/map/"+type.getId().getPath()+".png");
        bindDecorationRenderer(type, new DecorationRenderer<>(texture));
    }

    public static <E extends CustomDecoration> DecorationRenderer<E> getRenderer(E decoration) {
        return (DecorationRenderer<E>)RENDERERS.get(decoration.getType());
    }

    public static <T extends CustomDecoration> boolean render(T decoration, MatrixStack matrixStack, IRenderTypeBuffer buffer, MapData mapData, boolean isOnFrame, int light, int index){
        DecorationRenderer<T> renderer = getRenderer(decoration);
        if(renderer!=null) {
            return renderer.render(decoration, matrixStack, buffer, mapData, isOnFrame, light, index);
        }
        return false;
    }

}
