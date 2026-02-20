package decoblock;

import java.io.File;

import decoblock.util.ConfigHelper;

public class ModConfiguration {

    @ConfigHelper.Value
    public static DecorationMode defaultType = DecorationMode.CROSS;

    @ConfigHelper.Value
    public static String defaultTexture = "";

    @ConfigHelper.Value
    public static float hardness = 0.0F;

    @ConfigHelper.Value
    public static float hardnessCollidable = 0.75F;

    @ConfigHelper.Value(comment = "This field will be used as the name of the cache subdirectory for this server.")
    public static String cacheSubDir = CommonProxy.DEFAULT_CACHE_SUBDIR;

    public static void loadConfiguration(File file) {
        ConfigHelper helper = new ConfigHelper(file);
        helper.loadTo(ModConfiguration.class);

        if (cacheSubDir == null || cacheSubDir.isEmpty()) {
            cacheSubDir = CommonProxy.DEFAULT_CACHE_SUBDIR;
        }
    }
}
