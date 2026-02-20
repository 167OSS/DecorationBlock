package decoblock.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import decoblock.ServerTextureCache;
import decoblock.ServerTextureManager;
import decoblock.TextureType;
import io.netty.buffer.ByteBuf;

public class MsgRequestImage implements IMessage {

    public String name;
    public TextureType type;

    public MsgRequestImage() {}

    public MsgRequestImage(String name, TextureType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.name = ByteBufUtils.readUTF8String(buf);
        this.type = TextureType.values()[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, name);
        buf.writeInt(type.ordinal());
    }

    public static class Handler implements IMessageHandler<MsgRequestImage, IMessage> {

        @Override
        public IMessage onMessage(MsgRequestImage message, MessageContext ctx) {
            ServerTextureCache cache = ServerTextureManager.instance.getTexture(message.name, message.type);
            if (cache != null) {
                return new MsgTransferImage(message.name, message.type, cache.getImage());
            }
            return null;
        }
    }
}
