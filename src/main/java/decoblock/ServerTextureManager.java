package decoblock;

import java.io.File;
import java.util.EnumMap;
import java.util.HashMap;

public class ServerTextureManager {

    static public ServerTextureManager instance = new ServerTextureManager();

    EnumMap<TextureType, HashMap<String, ServerTextureCache>> textureMap = new EnumMap<TextureType, HashMap<String, ServerTextureCache>>(
        TextureType.class);

    protected ServerTextureManager() {}

    private void createTextureMapFor(TextureType type) {
        textureMap.put(type, new HashMap<String, ServerTextureCache>());

        File fi = new File(DecorationBlock.proxy.getBaseDir(), CommonProxy.DIR_LOCAL_TEXTURE);
        if (!fi.exists()) {
            throw new RuntimeException("Directory does not exist : " + fi.getAbsolutePath());
        }
        File fi1 = new File(fi, type.typeText());
        if (!fi1.exists()) {
            throw new RuntimeException("Directory does not exist : " + fi.getAbsolutePath());
        }
        for (File tf : fi1.listFiles()) {
            if (tf.isFile() && tf.getName()
                .endsWith(".png")) {
                String name = tf.getName();
                textureMap.get(type)
                    .put(name, new ServerTextureCache(name, type));
            }
        }

    }

    private void prepareTextureMap(TextureType type) {
        if (!textureMap.containsKey(type) || textureMap.get(type) == null) createTextureMapFor(type);
    }

    public ServerTextureCache[] getTextureList(TextureType type) {
        prepareTextureMap(type);

        HashMap<String, ServerTextureCache> select = textureMap.get(type);

        ServerTextureCache list[] = new ServerTextureCache[select.size()];
        return select.values()
            .toArray(list);
    }

    public ServerTextureCache getTexture(String name, TextureType type) {
        if (textureMap.containsKey(type) && textureMap.get(type)
            .containsKey(name)) {
            return textureMap.get(type)
                .get(name);
        }

        return null;
    }
}
