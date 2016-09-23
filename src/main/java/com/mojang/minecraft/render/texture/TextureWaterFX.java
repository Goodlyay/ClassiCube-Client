package com.mojang.minecraft.render.texture;

import com.mojang.minecraft.level.tile.Block;

public final class TextureWaterFX extends TextureFX {

    private float[] soupHeat = new float[256];
    private float[] potHeat = new float[256];
    private float[] flameHeat = new float[256];

    public TextureWaterFX() {
        super(Block.WATER.textureId);
    }

    @Override
    public final void animate() {
        int col;
        int row;
        float var3;
        int var4;
        int var5;
        int var6;
        for (col = 0; col < 16; ++col) {
            for (row = 0; row < 16; ++row) {
                var3 = 0F;
                //instead of 3x3, 3x1
                for (var4 = col - 1; var4 <= col + 1; ++var4) {
                    var5 = var4 & 15;
                    var6 = row & 15;
                    var3 += soupHeat[var5 + (var6 << 4)];
                }

                soupHeat[col + (row << 4)] = var3 / 3.3F + potHeat[col + (row << 4)] * 0.8F;
            }
        }

        for (col = 0; col < 16; ++col) {
            for (row = 0; row < 16; ++row) {
                potHeat[col + (row << 4)] += flameHeat[col + (row << 4)] * 0.05F;
                if (potHeat[col + (row << 4)] < 0F) {
                    potHeat[col + (row << 4)] = 0F;
                }

                flameHeat[col + (row << 4)] -= 0.1F;
                if (Math.random() < 0.05D) {
                    flameHeat[col + (row << 4)] = 0.5F;
                }
            }
        }

        //float[] var8 = soupHeat;
        //soupHeat = red;
        //red = var8;

        for (row = 0; row < 256; ++row) {
        	float colorHeat;
            if ((colorHeat = soupHeat[row]) > 1F) {
            	colorHeat = 1F;
            }

            if (colorHeat < 0F) {
            	colorHeat = 0F;
            }

            float colorHeatSquared = colorHeat * colorHeat;
            int red = (int) (32F + colorHeatSquared * 32F);
            int green = (int) (50F + colorHeatSquared * 64F);
            int blue = 255;
            int alpha = (int) (146F + colorHeatSquared * 50F);
            textureData[row << 2] = (byte) red;
            textureData[(row << 2) + 1] = (byte) green;
            textureData[(row << 2) + 2] = (byte) blue;
            textureData[(row << 2) + 3] = (byte) alpha;
        }
    }
}
