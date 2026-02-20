package decoblock.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import decoblock.DecorationBlock;
import io.netty.buffer.ByteBuf;

public class MsgInitClient implements IMessage {

    public String cacheSubDir;

    public MsgInitClient() {}

    public MsgInitClient(String cacheSubDir) {
        this.cacheSubDir = cacheSubDir;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.cacheSubDir = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, cacheSubDir);
    }

    public static class Handler implements IMessageHandler<MsgInitClient, IMessage> {

        @Override
        public IMessage onMessage(MsgInitClient message, MessageContext ctx) {
            DecorationBlock.proxy.resetTextureManager(message.cacheSubDir);
            return null;
        }
    }
}
