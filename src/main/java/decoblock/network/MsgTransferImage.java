package decoblock.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import decoblock.TextureType;
import io.netty.buffer.ByteBuf;

public class MsgTransferImage implements IMessage {

    public String name;
    public TextureType type;
    public byte[] data;

    public MsgTransferImage() {}

    public MsgTransferImage(String name, TextureType type, byte[] data) {
        this.name = name;
        this.type = type;
        this.data = data;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.name = ByteBufUtils.readUTF8String(buf);
        this.type = TextureType.values()[buf.readInt()];
        int len = buf.readInt();
        this.data = new byte[len];
        buf.readBytes(this.data);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, name);
        buf.writeInt(type.ordinal());
        buf.writeInt(data.length);
        buf.writeBytes(data);
    }

    public static class Handler implements IMessageHandler<MsgTransferImage, IMessage> {

        @Override
        public IMessage onMessage(MsgTransferImage message, MessageContext ctx) {
            // Client side logic
            decoblock.TextureCache cache = new decoblock.TextureCache(message.name, message.type);
            cache.setTextureImage(message.data);
            return null;
        }
    }
}
