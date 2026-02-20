package decoblock;

import java.io.File;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.TreeSet;

public class TextureManager {

    static public TextureManager instance = new TextureManager();

    EnumMap<TextureType, HashMap<String, TextureCache>> textureMap = new EnumMap<TextureType, HashMap<String, TextureCache>>(
        TextureType.class);

    private boolean initialized;
    private long lastUpdateTime;
    private String cacheSubDir;

    protected TextureManager() {
        initialized = false;
        cacheSubDir = CommonProxy.DEFAULT_CACHE_SUBDIR;
    }

    protected TextureManager(String cacheSubDir) {
        initialized = false;
        this.cacheSubDir = cacheSubDir;
    }

    public static void reset(String cacheSubDir) {
        instance = null;
        File cacheDir = new File(
            new File(DecorationBlock.proxy.getBaseDir(), CommonProxy.DIR_CACHE_TEXTURE),
            cacheSubDir);

        if (cacheDir.exists() || cacheDir.mkdirs()) {
            DecorationBlock.proxy.prepareDirsForTextureTypes(cacheDir);
            instance = new TextureManager(cacheSubDir);
        } else {
            instance = new TextureManager();
        }
    }

    public String getCacheSubdir() {
        return cacheSubDir;
    }

    private void prepareTextureMap(TextureType type) {
        if (!textureMap.containsKey(type) || textureMap.get(type) == null)
            textureMap.put(type, new HashMap<String, TextureCache>());
    }

    public void addTexture(TextureType type, String name) {
        prepareTextureMap(type);
        initialized = true;
        textureMap.get(type)
            .put(name, new TextureCache(name, type));
    }

    public boolean requestTextures() {
        if (initialized) {
            long currentTime = DecorationBlock.proxy.getSystemTime();
            if ((currentTime - lastUpdateTime) >= 300000) {
                lastUpdateTime = currentTime;
                DecorationBlock.network.sendToServer(new decoblock.network.MsgRequestTextureList());
                return true;
            }
        } else {
            lastUpdateTime = DecorationBlock.proxy.getSystemTime();
            initialized = true;
            DecorationBlock.network.sendToServer(new decoblock.network.MsgRequestTextureList());
            return true;
        }
        return false;
    }

    public TextureCache[] getTextureList(TextureType type) {
        prepareTextureMap(type);

        HashMap<String, TextureCache> select = textureMap.get(type);

        if (select.size() > 0) {
            TextureCache list[] = new TextureCache[select.size()];
            return new TreeSet<TextureCache>(select.values()).toArray(list);
        }

        return null;
    }

    public net.minecraft.util.ResourceLocation getTextureLocation(String name, TextureType type) {
        if (name == null || name.equals("")) {
            if (!initialized) requestTextures();
            return CommonProxy.ICON_BLOCK_DECORATION;
        }

        prepareTextureMap(type);

        HashMap<String, TextureCache> select = textureMap.get(type);

        if (!select.containsKey(name)) {
            requestTextures();
            return CommonProxy.ICON_BLOCK_DECORATION;
        }

        return select.get(name)
            .getResourceLocation();
    }

    public int getTexture(String name, TextureType type) {
        // Deprecated in 1.7.10 (Scheme A), use getTextureLocation
        return -1;
    }
}
