package decoblock;

import java.io.File;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommonProxy {

    public static final String CHANNEL = "DecoBlock";

    public static final String DIR_LOCAL_TEXTURE = "resources/mod/decorations";
    public static final String DIR_CACHE_TEXTURE = "cache/decorations";

    public static final String DEFAULT_CACHE_SUBDIR = "share";

    public static final net.minecraft.util.ResourceLocation ICON_BLOCK_DECORATION = new net.minecraft.util.ResourceLocation(
        "decoblock",
        "textures/blocks/decorationBlock.png");

    public static int renderID = 0;

    public void prepareDirsForTextureTypes(File basedir) {
        for (TextureType type : TextureType.availableTypes) {
            File dir = new File(basedir, type.typeText());
            if (!dir.exists()) {
                dir.mkdir();
            }
        }
    }

    public void prepareDir() {
        File resdir = new File(getBaseDir(), DIR_LOCAL_TEXTURE);
        if (!resdir.exists()) {
            resdir.mkdirs();
        }
        prepareDirsForTextureTypes(resdir);
    }

    public void sendToPlayer(EntityPlayer player, Object packet) {
        // Handled by SimpleNetworkWrapper in DecorationBlock.network
    }

    public World getWorld(int dimension) {
        return FMLCommonHandler.instance()
            .getMinecraftServerInstance()
            .worldServerForDimension(dimension);
    }

    public World getClientWorld() {
        return null;
    }

    public File getBaseDir() {
        return new File(".");
    }

    public long getSystemTime() {
        return 0;
    }

    public void registerRenderer() {}

    public void resetTextureManager(String cacheSubDir) {}
}
