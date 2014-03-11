package com.mojang.minecraft.particle;

import com.mojang.minecraft.ColorCache;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.render.ShapeRenderer;

public class TerrainParticle extends Particle {

    private static final long serialVersionUID = 1L;

    public TerrainParticle(Level level, float var2, float var3, float var4, float var5, float var6,
            float var7, Block block) {
        super(level, var2, var3, var4, var5, var6, var7);
        tex = block.textureId;
        gravity = block.particleGravity;
        rCol = gCol = bCol = 0.6F;
    }

    @Override
    public int getParticleTexture() {
        return 1;
    }

    @Override
    public void render(ShapeRenderer shapeRenderer, float var2, float var3, float var4, float var5,
            float var6, float var7) {
        float var8;
        float var9 = (var8 = (tex % 16 + uo / 4F) / 16F) + 0.015609375F;
        float var10;
        float var11 = (var10 = (tex / 16 + vo / 4F) / 16F) + 0.015609375F;
        float var12 = 0.1F * size;
        float var13 = xo + (x - xo) * var2;
        float var14 = yo + (y - yo) * var2;
        float var15 = zo + (z - zo) * var2;
        ColorCache var21 = getBrightnessColor();
        shapeRenderer.color(var21.R * rCol, var21.G * gCol, var21.B * bCol);
        shapeRenderer.vertexUV(var13 - var3 * var12 - var6 * var12, var14 - var4 * var12,
                var15 - var5 * var12 - var7 * var12, var8, var11);
        shapeRenderer.vertexUV(var13 - var3 * var12 + var6 * var12, var14 + var4 * var12,
                var15 - var5 * var12 + var7 * var12, var8, var10);
        shapeRenderer.vertexUV(var13 + var3 * var12 + var6 * var12, var14 + var4 * var12,
                var15 + var5 * var12 + var7 * var12, var9, var10);
        shapeRenderer.vertexUV(var13 + var3 * var12 - var6 * var12, var14 - var4 * var12,
                var15 + var5 * var12 - var7 * var12, var9, var11);
    }
}
