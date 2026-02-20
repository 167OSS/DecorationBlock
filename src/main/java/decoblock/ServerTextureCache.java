package decoblock;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class ServerTextureCache implements Comparable<ServerTextureCache> {

    private String name;
    private TextureType type;
    private File file;
    private byte[] image;

    public ServerTextureCache(String name, TextureType type) {
        this.name = name;
        this.type = type;
        file = new File(
            new File(new File(DecorationBlock.proxy.getBaseDir(), CommonProxy.DIR_LOCAL_TEXTURE), type.typeText()),
            name);
    }

    public void loadImage() {
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        try {
            fis = new FileInputStream(file);
            baos = new ByteArrayOutputStream();
            byte[] buf = new byte[256];
            int len;
            while ((len = fis.read(buf)) != -1) {
                baos.write(buf, 0, len);
            }
            image = baos.toByteArray();
        } catch (Exception e1) {} finally {
            try {
                if (fis != null) fis.close();
            } catch (Exception e2) {}
            try {
                if (baos != null) baos.close();
            } catch (Exception e3) {}
        }
    }

    public byte[] getImage() {
        if (image == null) {
            loadImage();
        }
        return image;
    }

    public String getTextureName() {
        return name;
    }

    public TextureType getTextureType() {
        return type;
    }

    @Override
    public int compareTo(ServerTextureCache y) {
        int result = type.compareTo(y.type);
        if (result == 0) {
            result = name.compareTo(y.name);
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof ServerTextureCache) && (compareTo((ServerTextureCache) o) == 0);
    }

    @Override
    public int hashCode() {
        return name.hashCode() ^ type.hashCode();
    }
}
