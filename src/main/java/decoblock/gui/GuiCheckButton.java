package decoblock.gui;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiCheckButton extends GuiIconButton {

    public boolean checked = false;

    public GuiCheckButton(int id, int x, int y, int width, int height, String text, ButtonIconProvider provider) {
        super(id, x, y, width, height, text, provider);
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            checked = !checked;
            return true;
        }
        return false;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            EnumSet<ButtonState> state = EnumSet.noneOf(ButtonState.class);
            if (this.enabled) state.add(ButtonState.ENABLED);
            if (mouseX >= this.xPosition && mouseY >= this.yPosition
                && mouseX < this.xPosition + this.width
                && mouseY < this.yPosition + this.height) {
                state.add(ButtonState.HOVERING);
            }
            if (checked) state.add(ButtonState.CHECKED);

            Object[] iconData = iconProvider.getIconForButton(this.id, state);
            if (iconData != null && iconData.length >= 3) {
                ResourceLocation res = (ResourceLocation) iconData[0];
                int u = (Integer) iconData[1];
                int v = (Integer) iconData[2];

                mc.getTextureManager()
                    .bindTexture(res);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.drawTexturedModalRect(this.xPosition, this.yPosition, u, v, this.width, this.height);
            }
        }
    }
}
