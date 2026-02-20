package decoblock.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import decoblock.ServerTextureCache;
import decoblock.ServerTextureManager;
import decoblock.TextureType;
import io.netty.buffer.ByteBuf;

public class MsgRequestTextureList implements IMessage {

    public MsgRequestTextureList() {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<MsgRequestTextureList, IMessage> {

        @Override
        public IMessage onMessage(MsgRequestTextureList message, MessageContext ctx) {
            ServerTextureCache[] cross = ServerTextureManager.instance.getTextureList(TextureType.CROSS);
            ServerTextureCache[] plate = ServerTextureManager.instance.getTextureList(TextureType.PLATE);
            return new MsgTextureList(cross, plate);
        }
    }
}
