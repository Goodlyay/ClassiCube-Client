package com.mojang.minecraft.gui.inputscreens;

import com.mojang.minecraft.gui.AdvancedOptionsScreen;
import com.mojang.minecraft.gui.Button;
import com.mojang.minecraft.gui.GuiScreen;

public class FogColorInputScreen extends InputValueScreen {

    String defaultHex = "ffffff";

    public FogColorInputScreen(GuiScreen var1, String var2, int var3, String Title) {
        super(var1, var2, var3, Title);
    }

    @Override
    protected final void onButtonClick(Button button) {
        if (button.active) {
            if (button.id == 0 && name.length() > 0) {
                String color = name;
                minecraft.level.fogColor = Integer.parseInt(color, 16);
                minecraft.setCurrentScreen(new AdvancedOptionsScreen(parent, minecraft.settings));
            }

            if (button.id == 1) {
                minecraft.setCurrentScreen(new AdvancedOptionsScreen(parent, minecraft.settings));
            }
            if (button.id == 800) {
                minecraft.level.fogColor = Integer.parseInt(defaultHex, 16);
                minecraft.setCurrentScreen(new AdvancedOptionsScreen(parent, minecraft.settings));
            }
        }
    }
}
