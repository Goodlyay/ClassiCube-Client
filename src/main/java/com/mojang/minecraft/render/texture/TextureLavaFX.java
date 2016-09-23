package com.mojang.minecraft.render.texture;

import com.mojang.minecraft.level.tile.Block;
//import com.mojang.util.LogUtil;
import com.mojang.util.MathHelper;

public final class TextureLavaFX extends TextureFX {

    //private float[] red = new float[256];
    private float[] soupHeat = new float[256];
    private float[] potHeat = new float[256];
    private float[] flameHeat = new float[256];

    public TextureLavaFX() {
        super(Block.LAVA.textureId);
    }

    @Override
    public final void animate() {
        int col;
        int row;
        int neighborhoodCol;
        int neighborhoodRow;
        for (col = 0; col < 16; ++col) {
            for (row = 0; row < 16; ++row) {
                float localSoupHeat = 0F;
              //goes through a full sin wave across the columns of the texture, 22.5 degrees per pixel.
                int rowSin = (int) (MathHelper.sin(row * (float) Math.PI * 2F / 16F) * 1.2F);
              // goes through a full sin wave down the rows of the texture, 22.5 degrees per pixel.
                int colSin = (int) (MathHelper.sin(col * (float) Math.PI * 2F / 16F) * 1.2F);
                
                
                //calculates a seed for the current spot equal to the sum of itself and its neighbors.
                for (neighborhoodCol = col - 1; neighborhoodCol <= col + 1; ++neighborhoodCol) {
                    for (neighborhoodRow = row - 1; neighborhoodRow <= row + 1; ++neighborhoodRow) {
                    	//for each spot
                    	//there is 3x3 area around that spot
                    	//that area is offset horizontally by the first four bits of rowSin
                    	//that area is offset vertically by the first four bits of colSin
                        int shiftedCol = neighborhoodCol + rowSin & 15;
                        int shiftedRow = neighborhoodRow + colSin & 15;
                        localSoupHeat += soupHeat[shiftedCol + (shiftedRow << 4)];
                    }
                }
                
                //Sum of 2x2 grid of potHeat with top left corner as current spot
                float localPotHeat =
                		  potHeat[(col) + ((row) << 4)]
        				+ potHeat[(col + 1 & 15) + ((row) << 4)]
        				+ potHeat[(col + 1 & 15) + ((row + 1 & 15) << 4)]
        				+ potHeat[(col & 15) + ((row + 1 & 15) << 4)];
                
                
                soupHeat[col + (row << 4)] = localSoupHeat / 10F + (localPotHeat / 4F * 0.8F);
                
                potHeat[col + (row << 4)] += flameHeat[col + (row << 4)] * 0.01F;
                
                if (potHeat[col + (row << 4)] < 0F) {
                    potHeat[col + (row << 4)] = 0F;
                }

                flameHeat[col + (row << 4)] -= 0.06F;
                if (Math.random() < 0.005D) {
                    flameHeat[col + (row << 4)] = 1.5F;
                }
            }
        }
        
        //swap the arrays so it can use the previous result in the next frame's calculation
        //float[] var10 = green;
        //green = red;
        //red = var10; JUST KIDDING
        
        //red = green;
        
        
        //take the colorSeed for each place in the array, double it, clamp between 0 and 1 inclusive.
        for (row = 0; row < 256; ++row) {
        	float colorHeat;
        	
            if ((colorHeat = soupHeat[row] * 2F) > 1F) {
            	colorHeat = 1F;
            }

            if (colorHeat < 0F) {
            	colorHeat = 0F;
            }

            int rColor = (int) (colorHeat * 100F + 155F);
            int gColor = (int) (colorHeat * colorHeat * 255F);
            int bColor = (int) (colorHeat * colorHeat * colorHeat * colorHeat * 128F);
            //set the texture data to the red green and blue and alpha
            textureData[row << 2] = (byte) rColor;
            textureData[(row << 2) + 1] = (byte) gColor;
            textureData[(row << 2) + 2] = (byte) bColor;
            textureData[(row << 2) + 3] = -1;
        }
    }
}