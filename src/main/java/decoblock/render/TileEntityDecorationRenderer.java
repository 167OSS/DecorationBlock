package decoblock.render;

import java.nio.FloatBuffer;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import decoblock.ModItems;
import decoblock.TextureManager;
import decoblock.TileEntityDecoration;

public class TileEntityDecorationRenderer extends TileEntitySpecialRenderer {

    private static FloatBuffer colorBuffer = GLAllocation.createDirectFloatBuffer(16);

    private static FloatBuffer setColorBuffer(float par0, float par1, float par2, float par3) {
        colorBuffer.clear();
        colorBuffer.put(par0)
            .put(par1)
            .put(par2)
            .put(par3);
        colorBuffer.flip();
        return colorBuffer;
    }

    public void renderTileEntityDecoration(TileEntityDecoration tileEntity, double d, double d1, double d2, float f) {
        int i;
        if (tileEntity.getWorld() == null) {
            i = 0;
        } else {
            Block block = tileEntity.getBlockType();
            i = tileEntity.getBlockMetadata();
            if (block != null && i == 0) {
                i = 2;
            }
        }
        int j = 0;
        if (i == 2) {
            j = 180;
        }
        if (i == 3) {
            j = 0;
        }
        if (i == 4) {
            j = -90;
        }
        if (i == 5) {
            j = 90;
        }
        // Load texture.
        ResourceLocation rl = TextureManager.instance
            .getTextureLocation(tileEntity.textureName, tileEntity.mode.getTextureType());
        this.bindTexture(rl);

        GL11.glPushMatrix();

        // 核心修复：改用双精度平移，解决移动时的抖动问题
        GL11.glTranslated(d, d1, d2);

        // 禁用剔除以支持双面渲染，启用透明度裁剪（足以处理多数装饰贴图，且不会引起抖动）
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_ALPHA_TEST);

        // Material
        GL11.glEnable(32826 /* GL_RESCALE_NORMAL_EXT */);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        float emitRed = tileEntity.lightValue / 15.0F;
        float emitGreen = tileEntity.lightValue / 15.0F;
        float emitBlue = tileEntity.lightValue / 15.0F;
        GL11.glMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_EMISSION, setColorBuffer(emitRed, emitGreen, emitBlue, 1.0F));

        // Position & direction offset.
        GL11.glTranslatef(0.5F, 0.0F, 0.5F);
        GL11.glRotatef(j, 0.0F, 1.0F, 0.0F);

        resetTextureRect();
        if (tileEntity.mirror) {
            textureRect = mirrorTexture(textureRect);
        }
        for (i = 0; i < tileEntity.rotate; ++i) {
            textureRect = rotateTexture(textureRect);
        }
        // Rendering.
        switch (tileEntity.mode) {
            case PLATE:
                renderPlate(tileEntity, false);
                break;
            case CENTER:
                renderPlate(tileEntity, true);
                break;
            default:
                renderCross(tileEntity);
        }

        GL11.glMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_EMISSION, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));

        // 状态恢复
        GL11.glEnable(GL11.GL_CULL_FACE);

        GL11.glPopMatrix();
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double d, double d1, double d2, float f) {
        renderTileEntityDecoration((TileEntityDecoration) tileEntity, d, d1, d2, f);
    }

    private double[] textureRect = new double[8];

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

    private static final double EPS = 0.0005D;

    protected void renderCross(TileEntityDecoration tileEntity) {
        // +
        Tessellator tessellator = Tessellator.instance;
        double d7 = -0.45000000000000001D;
        double d8 = 0.45000000000000001D;
        double d9 = -0.45000000000000001D;
        double d10 = 0.45000000000000001D;
        tessellator.startDrawingQuads();
        tessellator.setBrightness(
            ModItems.decoration.getMixedBrightnessForBlock(
                tileEntity.getWorldObj(),
                tileEntity.xCoord,
                tileEntity.yCoord,
                tileEntity.zCoord));
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        tessellator.setNormal(0.0F, 1.0F, 0.0F);

        // 第一片（对角）正面
        tessellator.addVertexWithUV(d7, 1.0D, d9, textureRect[0], textureRect[1]);
        tessellator.addVertexWithUV(d7, 0.0D, d9, textureRect[2], textureRect[3]);
        tessellator.addVertexWithUV(d8, 0.0D, d10, textureRect[4], textureRect[5]);
        tessellator.addVertexWithUV(d8, 1.0D, d10, textureRect[6], textureRect[7]);
        // 第一片背面（加 epsilon 打破共面，避免视角旋转闪烁）
        tessellator.addVertexWithUV(d8, 1.0D, d10 + EPS, textureRect[0], textureRect[1]);
        tessellator.addVertexWithUV(d8, 0.0D, d10 + EPS, textureRect[2], textureRect[3]);
        tessellator.addVertexWithUV(d7, 0.0D, d9 + EPS, textureRect[4], textureRect[5]);
        tessellator.addVertexWithUV(d7, 1.0D, d9 + EPS, textureRect[6], textureRect[7]);

        // 第二片（反对角）正面
        tessellator.addVertexWithUV(d7, 1.0D, d10, textureRect[0], textureRect[1]);
        tessellator.addVertexWithUV(d7, 0.0D, d10, textureRect[2], textureRect[3]);
        tessellator.addVertexWithUV(d8, 0.0D, d9, textureRect[4], textureRect[5]);
        tessellator.addVertexWithUV(d8, 1.0D, d9, textureRect[6], textureRect[7]);
        // 第二片背面（加 epsilon）
        tessellator.addVertexWithUV(d8, 1.0D, d9 - EPS, textureRect[0], textureRect[1]);
        tessellator.addVertexWithUV(d8, 0.0D, d9 - EPS, textureRect[2], textureRect[3]);
        tessellator.addVertexWithUV(d7, 0.0D, d10 - EPS, textureRect[4], textureRect[5]);
        tessellator.addVertexWithUV(d7, 1.0D, d10 - EPS, textureRect[6], textureRect[7]);

        tessellator.draw();
    }

    protected void renderPlate(TileEntityDecoration tileEntity, boolean center) {
        // -
        Tessellator tessellator = Tessellator.instance;
        double d4 = center ? 0D : 0.05000000074505806D - 0.5D;
        double d4b = d4 + EPS;
        tessellator.startDrawingQuads();
        tessellator.setBrightness(
            ModItems.decoration.getMixedBrightnessForBlock(
                tileEntity.getWorldObj(),
                tileEntity.xCoord,
                tileEntity.yCoord,
                tileEntity.zCoord));
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        tessellator.setNormal(0.0F, 1.0F, 0.0F);

        // 正面
        tessellator.addVertexWithUV(0.5D, 1D, d4, textureRect[0], textureRect[1]);
        tessellator.addVertexWithUV(0.5D, 0D, d4, textureRect[2], textureRect[3]);
        tessellator.addVertexWithUV(-0.5D, 0D, d4, textureRect[4], textureRect[5]);
        tessellator.addVertexWithUV(-0.5D, 1D, d4, textureRect[6], textureRect[7]);
        // 背面（加 epsilon）
        tessellator.addVertexWithUV(-0.5D, 1D, d4b, textureRect[6], textureRect[7]);
        tessellator.addVertexWithUV(-0.5D, 0D, d4b, textureRect[4], textureRect[5]);
        tessellator.addVertexWithUV(0.5D, 0D, d4b, textureRect[2], textureRect[3]);
        tessellator.addVertexWithUV(0.5D, 1D, d4b, textureRect[0], textureRect[1]);

        tessellator.draw();
    }
}
