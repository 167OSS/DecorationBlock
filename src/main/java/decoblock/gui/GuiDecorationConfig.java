package decoblock.gui;

import java.util.EnumMap;
import java.util.EnumSet;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import decoblock.*;

public class GuiDecorationConfig extends GuiContainer implements ButtonIconProvider {

    private static int PAGE_SLOTS = 64;
    private TileEntityDecoration tileEntity;
    private GuiIconButton buttonPagePrev;
    private GuiIconButton buttonPageNext;
    private GuiIconButton buttonModePrev;
    private GuiIconButton buttonModeNext;
    private GuiIconButton buttonRotatePrev;
    private GuiIconButton buttonRotateNext;
    private GuiCheckButton checkMirror;
    private GuiTabButton tabTexture;
    private GuiTabButton tabAdvanced;

    private int selectedSlot;
    private int offsetSlot;
    private int pageMax;

    private EnumMap<TextureType, TextureCache[]> textureList = new EnumMap<TextureType, TextureCache[]>(
        TextureType.class);
    private TextureCache[] selectedList;
    private EnumMap<TextureType, String> selectedTexture = new EnumMap<TextureType, String>(TextureType.class);
    private DecorationMode[] selectableMode = { DecorationMode.CROSS, DecorationMode.PLATE, DecorationMode.CENTER };
    private int selectedModeIndex;

    private int pointedSlot;

    protected int heightTab;
    protected int windowTop;

    private int selectedTab = 0;

    private static final int ID_BUTTON_PREV_PAGE = 21;
    private static final int ID_BUTTON_NEXT_PAGE = 22;
    private static final int ID_BUTTON_PREV_MODE = 100;
    private static final int ID_BUTTON_NEXT_MODE = 101;
    private static final int ID_CHECK_MIRROR = 102;
    private static final int ID_BUTTON_PREV_ROTATE = 103;
    private static final int ID_BUTTON_NEXT_ROTATE = 104;
    private static final int ID_TAB_TEXTURE = 200;
    private static final int ID_TAB_ADVANCED = 201;

    private static final int[] tabIDList = new int[] { ID_TAB_TEXTURE, ID_TAB_ADVANCED };
    private GuiTabButton[] tabList;

    public GuiDecorationConfig(InventoryPlayer inventoryPlayer, TileEntityDecoration tileEntity) {
        super(new ContainerDecorationConfig(inventoryPlayer, tileEntity));
        this.tileEntity = tileEntity;
        selectedSlot = -1;
        offsetSlot = 0;
        pageMax = 1;
        pointedSlot = -1;
    }

    private int getIndexFromMode(DecorationMode mode) {
        switch (mode) {
            case CROSS:
                return 0;
            case PLATE:
                return 1;
            case CENTER:
                return 2;
            default:
        }
        return 0;
    }

    @Override
    public void initGui() {

        heightTab = 14;
        xSize = 176;
        ySize = 222 + heightTab;
        super.initGui();
        windowTop = guiTop + heightTab;

        textureList.put(TextureType.CROSS, TextureManager.instance.getTextureList(TextureType.CROSS));
        textureList.put(TextureType.PLATE, TextureManager.instance.getTextureList(TextureType.PLATE));
        selectedTexture.put(TextureType.CROSS, "");
        selectedTexture.put(TextureType.PLATE, "");

        selectedModeIndex = getIndexFromMode(tileEntity.mode);
        selectedList = textureList.get(selectableMode[selectedModeIndex].getTextureType());

        selectedTexture.put(selectableMode[selectedModeIndex].getTextureType(), tileEntity.textureName);

        if (selectedList != null) {
            for (int i = 0; i < selectedList.length; ++i) {
                if (selectedList[i].getTextureName()
                    .equals(tileEntity.textureName)) {
                    selectedSlot = i;
                    offsetSlot = i - i % PAGE_SLOTS;
                    break;
                }
            }
            pageMax = selectedList.length / PAGE_SLOTS + (selectedList.length % PAGE_SLOTS == 0 ? 0 : 1);
        }

        buttonPagePrev = new GuiIconButton(ID_BUTTON_PREV_PAGE, guiLeft + 94, windowTop + 163, 12, 12, "<", this);
        this.buttonList.add(buttonPagePrev);
        buttonPageNext = new GuiIconButton(ID_BUTTON_NEXT_PAGE, guiLeft + 148, windowTop + 163, 12, 12, ">", this);
        this.buttonList.add(buttonPageNext);
        buttonModePrev = new GuiIconButton(ID_BUTTON_PREV_MODE, guiLeft + 16, windowTop + 163, 12, 12, "<", this);
        this.buttonList.add(buttonModePrev);
        buttonModeNext = new GuiIconButton(ID_BUTTON_NEXT_MODE, guiLeft + 82, windowTop + 163, 12, 12, ">", this);
        this.buttonList.add(buttonModeNext);
        checkMirror = new GuiCheckButton(ID_CHECK_MIRROR, guiLeft + 16, windowTop + 177, 12, 12, "Mirror", this);
        this.buttonList.add(checkMirror);
        buttonRotatePrev = new GuiIconButton(ID_BUTTON_PREV_ROTATE, guiLeft + 136, windowTop + 177, 12, 12, "<", this);
        this.buttonList.add(buttonRotatePrev);
        buttonRotateNext = new GuiIconButton(ID_BUTTON_NEXT_ROTATE, guiLeft + 148, windowTop + 177, 12, 12, ">", this);
        this.buttonList.add(buttonRotateNext);
        tabTexture = new GuiTabButton(ID_TAB_TEXTURE, guiLeft + 8, guiTop, 64, "Texture");
        this.buttonList.add(tabTexture);
        tabAdvanced = new GuiTabButton(ID_TAB_ADVANCED, guiLeft + 72, guiTop, 64, "Advanced");
        this.buttonList.add(tabAdvanced);

        tabList = new GuiTabButton[] { tabTexture, tabAdvanced };

        checkMirror.checked = tileEntity.mirror;

        updatePageButtonState();
        onTab(tabTexture);
    }

    private double textureRect[] = new double[8];

    private void resetTextureRect() {
        textureRect[0] = 0.0D;
        textureRect[1] = 0.0D;
        textureRect[2] = 0.0D;
        textureRect[3] = 1.0D;
        textureRect[4] = 1.0D;
        textureRect[5] = 1.0D;
        textureRect[6] = 1.0D;
        textureRect[7] = 0.0D;
    }

    private double[] mirrorTexture(double[] src) {
        double[] result = new double[8];
        result[0] = src[6];
        result[1] = src[1];
        result[2] = src[4];
        result[3] = src[3];
        result[4] = src[2];
        result[5] = src[5];
        result[6] = src[0];
        result[7] = src[7];

        return result;
    }

    private double[] rotateTexture(double[] src) {
        double[] result = new double[8];
        result[0] = src[2];
        result[1] = src[3];
        result[2] = src[4];
        result[3] = src[5];
        result[4] = src[6];
        result[5] = src[7];
        result[6] = src[0];
        result[7] = src[1];

        return result;
    }

    private String[] rotateText = { "0", "90", "180", "270" };

    private void drawStringCenter(FontRenderer fontRenderer, String text, int centerX, int centerY, int color) {
        int strWidth = fontRenderer.getStringWidth(text);
        fontRenderer.drawString(text, centerX - strWidth / 2, centerY - 4, color);
    }

    private void drawStringRight(FontRenderer fontRenderer, String text, int right, int top, int color) {
        int strWidth = fontRenderer.getStringWidth(text);
        fontRenderer.drawString(text, right - strWidth, top, color);
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        if (guibutton.id == ID_BUTTON_PREV_PAGE) onButtonPagePrev(guibutton);
        else if (guibutton.id == ID_BUTTON_NEXT_PAGE) onButtonPageNext(guibutton);
        else if (guibutton.id == ID_BUTTON_PREV_MODE) onButtonModePrev(guibutton);
        else if (guibutton.id == ID_BUTTON_NEXT_MODE) onButtonModeNext(guibutton);
        else if (guibutton.id == ID_CHECK_MIRROR) onCheckMirror(guibutton);
        else if (guibutton.id == ID_BUTTON_PREV_ROTATE) onButtonRotatePrev(guibutton);
        else if (guibutton.id == ID_BUTTON_NEXT_ROTATE) onButtonRotateNext(guibutton);

        onTab(guibutton);
    }

    @Override
    public Object[] getIconForButton(int id, EnumSet<ButtonState> state) {
        int xBase = 0;
        int yBase = 0;
        int xOffset = 0;
        int yOffset = 0;
        if ((id == ID_BUTTON_PREV_PAGE) || (id == ID_BUTTON_PREV_MODE) || (id == ID_BUTTON_PREV_ROTATE)) {
            xBase = 176;
            yBase = 96;
            if (state.contains(ButtonState.HOVERING)) xOffset = 12;
            if (state.contains(ButtonState.PRESSED)) yOffset = 24;
            if (!state.contains(ButtonState.ENABLED)) yOffset = 12;
        } else if ((id == ID_BUTTON_NEXT_PAGE) || (id == ID_BUTTON_NEXT_MODE) || (id == ID_BUTTON_NEXT_ROTATE)) {
            xBase = 200;
            yBase = 96;
            if (state.contains(ButtonState.HOVERING)) xOffset = 12;
            if (state.contains(ButtonState.PRESSED)) yOffset = 24;
            if (!state.contains(ButtonState.ENABLED)) yOffset = 12;
        } else if (id == ID_CHECK_MIRROR) {
            xBase = 176;
            yBase = 132;
            if (state.contains(ButtonState.HOVERING)) xOffset = 12;
            if (!state.contains(ButtonState.ENABLED)) yOffset = 12;
            if (state.contains(ButtonState.CHECKED)) xOffset += 24;
        }

        Object[] result = new Object[3];
        result[0] = GuiHandler.GUI_DECORATION_CONTAINER;
        result[1] = xBase + xOffset;
        result[2] = yBase + yOffset;

        return result;
    }

    private void renderTexture(int x, int y, int width, int height) {
        Tessellator tes = Tessellator.instance;
        tes.startDrawingQuads();
        tes.addVertexWithUV(x + 0, y + 0, 0.0D, textureRect[0], textureRect[1]);
        tes.addVertexWithUV(x + 0, y + height, 0.0D, textureRect[2], textureRect[3]);
        tes.addVertexWithUV(x + width, y + height, 0.0D, textureRect[4], textureRect[5]);
        tes.addVertexWithUV(x + width, y + 0, 0.0D, textureRect[6], textureRect[7]);
        tes.draw();
    }

    private void updatePageButtonState() {
        if (offsetSlot <= 0) buttonPagePrev.enabled = false;
        else buttonPagePrev.enabled = true;

        if (selectedList == null || offsetSlot >= (pageMax - 1) * PAGE_SLOTS) buttonPageNext.enabled = false;
        else buttonPageNext.enabled = true;
    }

    private void onTabChange() {
        ((ContainerDecorationConfig) inventorySlots).onTabChange(selectedTab);

        switch (selectedTab) {
            case 0:
                showTabPageTexture();
                break;
            case 1:
                hideTabPageTexture();
                break;
        }
    }

    private void showTabPageTexture() {
        buttonPagePrev.visible = true;
        buttonPageNext.visible = true;
        buttonModePrev.visible = true;
        buttonModeNext.visible = true;
        checkMirror.visible = true;
        buttonRotatePrev.visible = true;
        buttonRotateNext.visible = true;
    }

    private void hideTabPageTexture() {
        buttonPagePrev.visible = false;
        buttonPageNext.visible = false;
        buttonModePrev.visible = false;
        buttonModeNext.visible = false;
        checkMirror.visible = false;
        buttonRotatePrev.visible = false;
        buttonRotateNext.visible = false;
    }

    private void selectedModeChanged() {
        TextureType textype = selectableMode[selectedModeIndex].getTextureType();
        selectedList = textureList.get(textype);
        String selectedTextureName = selectedTexture.get(textype);

        selectedSlot = -1;
        offsetSlot = 0;
        pageMax = 1;
        if (selectedList != null) {
            for (int i = 0; i < selectedList.length; ++i) {
                if (selectedList[i].getTextureName()
                    .equals(selectedTextureName)) {
                    selectedSlot = i;
                    offsetSlot = i - i % PAGE_SLOTS;
                    break;
                }
            }
            pageMax = selectedList.length / PAGE_SLOTS + (selectedList.length % PAGE_SLOTS == 0 ? 0 : 1);
        }
        updatePageButtonState();
        tileEntity.setDecorationMode(selectableMode[selectedModeIndex]);
        tileEntity.setTextureName(selectedTextureName);
    }

    private void onTab(GuiButton guibutton) {
        if (!(guibutton instanceof GuiTabButton)) return;

        boolean changed = false;

        int i;
        for (i = 0; i < tabIDList.length; ++i) {
            if (tabIDList[i] == guibutton.id) {
                changed = !tabList[i].selected;
                tabList[i].selected = true;
                selectedTab = i;
            } else {
                tabList[i].selected = false;
            }
        }

        if (changed) onTabChange();
    }

    private void onButtonPagePrev(GuiButton guibutton) {
        offsetSlot -= PAGE_SLOTS;
        if (offsetSlot < 0) offsetSlot = 0;
        updatePageButtonState();
    }

    private void onButtonPageNext(GuiButton guibutton) {
        if (selectedList == null) {
            offsetSlot = 0;
        } else {
            offsetSlot += PAGE_SLOTS;
            if (offsetSlot >= selectedList.length) offsetSlot = (pageMax - 1) * PAGE_SLOTS;
        }
        updatePageButtonState();
    }

    private void onButtonModePrev(GuiButton guibutton) {
        selectedModeIndex--;
        if (selectedModeIndex < 0) selectedModeIndex = selectableMode.length - 1;
        selectedModeChanged();
    }

    private void onButtonModeNext(GuiButton guibutton) {
        selectedModeIndex++;
        if (selectedModeIndex >= selectableMode.length) selectedModeIndex = 0;
        selectedModeChanged();
    }

    private void onButtonRotatePrev(GuiButton guibutton) {
        int rotate = tileEntity.rotate - 1;
        if (rotate < 0) rotate = 3;
        tileEntity.setRotate(rotate);
    }

    private void onButtonRotateNext(GuiButton guibutton) {
        int rotate = tileEntity.rotate + 1;
        if (rotate >= 4) rotate = 0;
        tileEntity.setRotate(rotate);
    }

    private void onCheckMirror(GuiButton guibutton) {
        tileEntity.setMirror(checkMirror.checked);
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3) {
        super.mouseClicked(par1, par2, par3);
        switch (selectedTab) {
            case 0:
                mouseClickedForTabTexture(par1, par2, par3);
                break;
            case 1:
                break;
        }
    }

    private void mouseClickedForTabTexture(int par1, int par2, int par3) {
        calcPointed(par1, par2);
        if (pointedSlot >= 0 && selectedList != null && pointedSlot < selectedList.length) {
            selectedSlot = pointedSlot;
            String selectedName = selectedList[selectedSlot].getTextureName();
            selectedTexture.put(selectableMode[selectedModeIndex].getTextureType(), selectedName);
            tileEntity.setTextureName(selectedName);
        }
    }

    private void calcPointed(int x, int y) {
        int pointedX = (x - guiLeft - 17) / 18;
        int pointedY = (y - windowTop - 18) / 18;
        if (pointedX >= 0 && pointedX < 8 && pointedY >= 0 && pointedY < 8) {
            pointedSlot = offsetSlot + pointedX + pointedY * 8;
        } else {
            pointedSlot = -1;
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager()
            .bindTexture(GuiHandler.GUI_DECORATION_CONTAINER);
        drawTexturedModalRect(guiLeft, windowTop, 0, 0, xSize, ySize - heightTab);

        switch (selectedTab) {
            case 0:
                drawGuiContainerBackgroundLayerForTabTexture(par1, par2, par3);
                break;
            case 1:
                drawGuiContainerBackgroundLayerForTabAdvance(par1, par2, par3);
                break;
        }
    }

    private void drawGuiContainerBackgroundLayerForTabTexture(float var1, int var2, int var3) {
        GL11.glColor4f(0.25F, 0.25F, 0.25F, 1.0F);
        drawTexturedModalRect(guiLeft + 132, windowTop + 179, 192, 0, 3, 3);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager()
            .bindTexture(GuiHandler.GUI_ITEMS);
        int i, j;
        for (i = 0; i < 8; ++i) {
            for (j = 0; j < 8; ++j) {
                drawTexturedModalRect(guiLeft + 16 + i * 18, windowTop + 17 + j * 18, 0, 46, 18, 18);
            }
        }

        int width = 54 / 2;
        drawTexturedModalRect(guiLeft + 28, windowTop + 163, 0, 34, width, 12);
        drawTexturedModalRect(guiLeft + 28 + width, windowTop + 163, 256 - width, 34, width, 12);

        width = 42 / 2;
        drawTexturedModalRect(guiLeft + 106, windowTop + 163, 0, 34, width, 12);
        drawTexturedModalRect(guiLeft + 106 + width, windowTop + 163, 256 - width, 34, width, 12);
    }

    private void drawGuiContainerBackgroundLayerForTabAdvance(float var1, int var2, int var3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager()
            .bindTexture(GuiHandler.GUI_ITEMS);

        int i, j;
        for (i = 0; i < 9; ++i) {
            for (j = 0; j < 3; ++j) {
                drawTexturedModalRect(guiLeft + 7 + i * 18, windowTop + 138 + j * 18, 0, 46, 18, 18);
            }
        }
        for (i = 0; i < 9; ++i) {
            drawTexturedModalRect(guiLeft + 7 + i * 18, windowTop + 26, 0, 46, 18, 18);
            drawTexturedModalRect(guiLeft + 7 + i * 18, windowTop + 196, 0, 46, 18, 18);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        switch (selectedTab) {
            case 0:
                drawGuiContainerForegroundLayerForTabTexture(par1, par2);
                break;
            case 1:
                drawGuiContainerForegroundLayerForTabAdvance(par1, par2);
                break;
        }
    }

    private void drawGuiContainerForegroundLayerForTabTexture(int par1, int par2) {
        fontRendererObj.drawString("Decoration", 16, heightTab + 6, 4210752);
        drawStringCenter(fontRendererObj, selectableMode[selectedModeIndex].typeText(), 55, heightTab + 169, 4210752);
        drawStringCenter(
            fontRendererObj,
            "" + (offsetSlot / PAGE_SLOTS + 1) + "/" + pageMax,
            128,
            heightTab + 169,
            4210752);
        fontRendererObj.drawString("Mirror", 30, heightTab + 179, 4210752);
        drawStringRight(fontRendererObj, rotateText[tileEntity.rotate], 132, heightTab + 179, 4210752);
        drawStringRight(fontRendererObj, "Rotate:", 108, heightTab + 179, 4210752);

        int i, j;
        int selX, selY;

        resetTextureRect();
        if (tileEntity.mirror) textureRect = mirrorTexture(textureRect);
        for (i = 0; i < tileEntity.rotate; ++i) textureRect = rotateTexture(textureRect);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (selectedList != null) {
            for (j = 0; j < 8; ++j) {
                selY = 18 + j * 18;
                for (i = 0; i < 8; ++i) {
                    selX = 17 + i * 18;
                    if ((i + j * 8 + offsetSlot) < selectedList.length) {
                        GL11.glDisable(GL11.GL_LIGHTING);
                        this.mc.getTextureManager()
                            .bindTexture(selectedList[i + j * 8 + offsetSlot].getResourceLocation());
                        renderTexture(selX, heightTab + selY, 16, 16);
                        GL11.glEnable(GL11.GL_LIGHTING);
                    }
                }
            }
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        this.mc.getTextureManager()
            .bindTexture(GuiHandler.GUI_DECORATION_CONTAINER);

        int markerPos = selectedSlot - offsetSlot;
        if (markerPos >= 0 && markerPos < 64) {
            selX = 17 + (markerPos % 8) * 18;
            selY = 18 + (markerPos / 8) * 18;
            drawTexturedModalRect(selX, heightTab + selY, 176, 0, 16, 16);
        }
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    private void drawGuiContainerForegroundLayerForTabAdvance(int par1, int par2) {
        fontRendererObj.drawString("Effects", 7, heightTab + 14, 4210752);
    }
}
