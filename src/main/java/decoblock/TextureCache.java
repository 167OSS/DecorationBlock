package decoblock;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import decoblock.network.MsgRequestImage;

public class TextureCache implements Comparable<TextureCache> {

    private String textureName;
    private String textureFullName;
    private TextureType textureType;
    private boolean local;
    private boolean cached;
    private boolean loaded;
    private File localFile;
    private File cacheFile;
    private ResourceLocation resourceLocation;

    public TextureCache(String name, TextureType type) {
        loaded = false;
        textureName = name;
        textureType = type;
        textureFullName = type.typeText() + "/" + name;
        localFile = new File(
            new File(DecorationBlock.proxy.getBaseDir(), CommonProxy.DIR_LOCAL_TEXTURE),
            textureFullName);
        cacheFile = new File(
            new File(
                new File(DecorationBlock.proxy.getBaseDir(), CommonProxy.DIR_CACHE_TEXTURE),
                TextureManager.instance.getCacheSubdir()),
            textureFullName);
        resourceLocation = new ResourceLocation(
            "decoblock",
            "decoration/" + textureFullName.toLowerCase()
                .replace(".png", ""));

        if (localFile.exists()) {
            local = true;
        } else if (cacheFile.exists()) {
            local = false;
            cached = true;
        } else {
            local = false;
            cached = false;
        }
    }

    public ResourceLocation getResourceLocation() {
        if (!loaded) loadTexture();

        if (loaded) {
            return resourceLocation;
        } else {
            return CommonProxy.ICON_BLOCK_DECORATION;
        }
    }

    public String getTextureName() {
        return textureName;
    }

    public TextureType getTextureType() {
        return textureType;
    }

    private BufferedImage readTextureImage(InputStream par1InputStream) throws IOException {
        BufferedImage var2 = ImageIO.read(par1InputStream);
        par1InputStream.close();
        return var2;
    }

    public void loadTexture() {
        InputStream is = null;
        try {
            if (local) {
                is = new FileInputStream(localFile);
            } else if (cached || cacheFile.exists()) {
                cached = true;
                is = new FileInputStream(cacheFile);
            } else {
                DecorationBlock.network.sendToServer(new MsgRequestImage(textureName, textureType));
                return;
            }

            BufferedImage img = readTextureImage(is);
            DynamicTexture dynamicTexture = new DynamicTexture(img);
            Minecraft.getMinecraft()
                .getTextureManager()
                .loadTexture(resourceLocation, dynamicTexture);

            loaded = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {}
            }
        }
    }

    public void setTextureImage(byte[] data) {
        try {
            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(data);
            BufferedImage img = ImageIO.read(bais);
            DynamicTexture dynamicTexture = new DynamicTexture(img);
            Minecraft.getMinecraft()
                .getTextureManager()
                .loadTexture(resourceLocation, dynamicTexture);
            loaded = true;

            // Save to cache
            if (!cacheFile.getParentFile()
                .exists())
                cacheFile.getParentFile()
                    .mkdirs();
            javax.imageio.ImageIO.write(img, "png", cacheFile);
            cached = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int compareTo(TextureCache y) {
        int result = textureType.compareTo(y.textureType);
        if (result == 0) {
            result = textureName.compareTo(y.textureName);
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof TextureCache) && (compareTo((TextureCache) o) == 0);
    }

    @Override
    public int hashCode() {
        return textureName.hashCode() ^ textureType.hashCode();
    }
}
