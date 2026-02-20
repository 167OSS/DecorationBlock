package decoblock;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import decoblock.render.TileEntityDecorationRenderer;

public class ClientProxy extends CommonProxy {

    @Override
    public void prepareDir() {
        super.prepareDir();

        try {
            File cachedir = new File(new File(getBaseDir(), DIR_CACHE_TEXTURE), DEFAULT_CACHE_SUBDIR);
            if (!cachedir.exists()) {
                cachedir.mkdirs();
            }

            prepareDirsForTextureTypes(cachedir);

        } catch (Exception e) {
            FMLLog.getLogger()
                .log(org.apache.logging.log4j.Level.ERROR, "Exception occured in ClientProxy prepareDir", e);
            FMLCommonHandler.instance()
                .raiseException(e, "Exception occured in ClientProxy", true);
        }
    }

    @Override
    public World getWorld(int dimension) {
        return FMLClientHandler.instance()
            .getClient()
            .getIntegratedServer()
            .worldServerForDimension(dimension);
    }

    @Override
    public World getClientWorld() {
        return FMLClientHandler.instance()
            .getClient().theWorld;
    }

    @Override
    public File getBaseDir() {
        return Minecraft.getMinecraft().mcDataDir;
    }

    @Override
    public long getSystemTime() {
        return Minecraft.getSystemTime();
    }

    @Override
    public void registerRenderer() {
        renderID = RenderingRegistry.getNextAvailableRenderId();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDecoration.class, new TileEntityDecorationRenderer());
    }

    @Override
    public void resetTextureManager(String cacheSubDir) {
        TextureManager.reset(cacheSubDir);
    }
}
