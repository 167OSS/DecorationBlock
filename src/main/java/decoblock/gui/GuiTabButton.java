package decoblock.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

public class GuiTabButton extends GuiButton {

    public boolean selected = false;

    public GuiTabButton(int id, int x, int y, int width, String text) {
        super(id, x, y, width, 14, text);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            mc.getTextureManager()
                .bindTexture(GuiHandler.GUI_DECORATION_CONTAINER);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            boolean hovering = mouseX >= this.xPosition && mouseY >= this.yPosition
                && mouseX < this.xPosition + this.width
                && mouseY < this.yPosition + this.height;

            int u = 0;
            int v = 166;
            if (selected) {
                v += 28;
            } else if (hovering) {
                v += 14;
            }

            this.drawTexturedModalRect(this.xPosition, this.yPosition, u, v, this.width / 2, this.height);
            this.drawTexturedModalRect(
                this.xPosition + this.width / 2,
                this.yPosition,
                176 - this.width / 2,
                v,
                this.width / 2,
                this.height);

            this.mouseDragged(mc, mouseX, mouseY);
            int color = 14737632;
            if (selected) color = 4210752;
            else if (hovering) color = 16777120;

            this.drawCenteredString(
                mc.fontRenderer,
                this.displayString,
                this.xPosition + this.width / 2,
                this.yPosition + (this.height - 8) / 2,
                color);
        }
    }
}
