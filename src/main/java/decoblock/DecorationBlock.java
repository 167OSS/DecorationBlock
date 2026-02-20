package decoblock;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import decoblock.gui.GuiHandler;

@Mod(modid = "decoblock", name = "DecorationBlock", version = "1.0.0")
public class DecorationBlock {

    @Instance("decoblock")
    public static DecorationBlock instance;

    @SidedProxy(clientSide = "decoblock.ClientProxy", serverSide = "decoblock.CommonProxy")
    public static CommonProxy proxy;

    public static SimpleNetworkWrapper network;

    public static String defaultTexture = "";
    public static boolean useIcon = true;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ModConfiguration.loadConfiguration(event.getSuggestedConfigurationFile());

        network = NetworkRegistry.INSTANCE.newSimpleChannel(CommonProxy.CHANNEL);

        int disc = 0;
        network.registerMessage(
            decoblock.network.MsgRequestTextureList.Handler.class,
            decoblock.network.MsgRequestTextureList.class,
            disc++,
            cpw.mods.fml.relauncher.Side.SERVER);
        network.registerMessage(
            decoblock.network.MsgTextureList.Handler.class,
            decoblock.network.MsgTextureList.class,
            disc++,
            cpw.mods.fml.relauncher.Side.CLIENT);
        network.registerMessage(
            decoblock.network.MsgRequestImage.Handler.class,
            decoblock.network.MsgRequestImage.class,
            disc++,
            cpw.mods.fml.relauncher.Side.SERVER);
        network.registerMessage(
            decoblock.network.MsgTransferImage.Handler.class,
            decoblock.network.MsgTransferImage.class,
            disc++,
            cpw.mods.fml.relauncher.Side.CLIENT);
        network.registerMessage(
            decoblock.network.MsgSetDecorationMode.Handler.class,
            decoblock.network.MsgSetDecorationMode.class,
            disc++,
            cpw.mods.fml.relauncher.Side.SERVER);
        network.registerMessage(
            decoblock.network.MsgSetTexture.Handler.class,
            decoblock.network.MsgSetTexture.class,
            disc++,
            cpw.mods.fml.relauncher.Side.SERVER);
        network.registerMessage(
            decoblock.network.MsgSetMirror.Handler.class,
            decoblock.network.MsgSetMirror.class,
            disc++,
            cpw.mods.fml.relauncher.Side.SERVER);
        network.registerMessage(
            decoblock.network.MsgSetRotate.Handler.class,
            decoblock.network.MsgSetRotate.class,
            disc++,
            cpw.mods.fml.relauncher.Side.SERVER);
        network.registerMessage(
            decoblock.network.MsgInitClient.Handler.class,
            decoblock.network.MsgInitClient.class,
            disc++,
            cpw.mods.fml.relauncher.Side.CLIENT);
        network.registerMessage(
            decoblock.network.MsgTileEntityData.Handler.class,
            decoblock.network.MsgTileEntityData.class,
            disc++,
            cpw.mods.fml.relauncher.Side.CLIENT);

        ModItems.decoration = new BlockDecoration();
        GameRegistry.registerBlock(ModItems.decoration, "decorationBlock");
        GameRegistry.registerTileEntity(TileEntityDecoration.class, "DecorationBlock");
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.prepareDir();
        proxy.registerRenderer();

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

        // Recipes
        ItemStack planks = new ItemStack(Blocks.planks, 1, 32767); // 32767 is OreDictionary.WILDCARD_VALUE
        ItemStack decoration = new ItemStack(ModItems.decoration);
        GameRegistry.addShapelessRecipe(decoration, planks, new ItemStack(Blocks.red_flower));
        GameRegistry.addShapelessRecipe(decoration, planks, new ItemStack(Blocks.yellow_flower));
    }
}
