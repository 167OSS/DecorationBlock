package decoblock.network;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import decoblock.ServerTextureCache;
import decoblock.TextureType;
import io.netty.buffer.ByteBuf;

public class MsgTextureList implements IMessage {

    public List<String> crossNames = new ArrayList<String>();
    public List<String> plateNames = new ArrayList<String>();

    public MsgTextureList() {}

    public MsgTextureList(ServerTextureCache[] cross, ServerTextureCache[] plate) {
        if (cross != null) for (ServerTextureCache c : cross) crossNames.add(c.getTextureName());
        if (plate != null) for (ServerTextureCache p : plate) plateNames.add(p.getTextureName());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int crossSize = buf.readInt();
        for (int i = 0; i < crossSize; i++) crossNames.add(ByteBufUtils.readUTF8String(buf));
        int plateSize = buf.readInt();
        for (int i = 0; i < plateSize; i++) plateNames.add(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(crossNames.size());
        for (String s : crossNames) ByteBufUtils.writeUTF8String(buf, s);
        buf.writeInt(plateNames.size());
        for (String s : plateNames) ByteBufUtils.writeUTF8String(buf, s);
    }

    public static class Handler implements IMessageHandler<MsgTextureList, IMessage> {

        @Override
        public IMessage onMessage(MsgTextureList message, MessageContext ctx) {
            for (String s : message.crossNames) decoblock.TextureManager.instance.addTexture(TextureType.CROSS, s);
            for (String s : message.plateNames) decoblock.TextureManager.instance.addTexture(TextureType.PLATE, s);
            return null;
        }
    }
}
