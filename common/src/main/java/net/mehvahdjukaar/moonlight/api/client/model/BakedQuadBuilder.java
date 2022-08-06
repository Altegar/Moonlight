package net.mehvahdjukaar.moonlight.api.client.model;

import com.mojang.math.Matrix4f;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public interface BakedQuadBuilder {

    @ExpectPlatform
    static BakedQuadBuilder create() {
        throw new AssertionError();
    }

    BakedQuadBuilder setSprite(TextureAtlasSprite sprite);

    BakedQuadBuilder setDirection(Direction direction);


    BakedQuadBuilder pos(float x, float y, float z);

    default BakedQuadBuilder pos(Vector3f vec3) {
        return pos(vec3.x(), vec3.y(), vec3.z());
    }

    BakedQuadBuilder normal(float x, float y, float z);

    default BakedQuadBuilder normal(Vector3f vector3f) {
        return normal(vector3f.x(), vector3f.y(), vector3f.z());
    }

    BakedQuadBuilder color(int rgba);



    BakedQuadBuilder uv(float u, float v);

    default BakedQuadBuilder spriteUV(TextureAtlasSprite sprite, float u, float v) {
        return uv(sprite.getU(u), sprite.getV(v)).setSprite(sprite);
    }

    /**
     * Applies a transformation to the output quads. Must be called before building any vertex data
     * Successful calls will simply replace the applied transform and won't combine them
     */
    BakedQuadBuilder useTransform(Matrix4f matrix4f);

    default BakedQuadBuilder useTransform(Transformation transformation) {
        if (transformation == Transformation.identity()) return this;
        return useTransform(transformation.getMatrix());
    }


    BakedQuadBuilder endVertex();

    BakedQuad build();

    static Vector3f applyModelRotation(float x, float y, float z, Matrix4f pTransform) {
        Vector3f v = new Vector3f(x, y, z);
        rotateVertexBy(v, new Vector3f(0.5F, 0.5F, 0.5F), pTransform);
        return v;
    }

    static void rotateVertexBy(Vector3f pPos, Vector3f pOrigin, Matrix4f pTransform) {
        Vector4f vector4f = new Vector4f(pPos.x() - pOrigin.x(), pPos.y() - pOrigin.y(), pPos.z() - pOrigin.z(), 1.0F);
        vector4f.transform(pTransform);
        pPos.set(vector4f.x() + pOrigin.x(), vector4f.y() + pOrigin.y(), vector4f.z() + pOrigin.z());
    }
}