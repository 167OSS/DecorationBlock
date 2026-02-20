package decoblock.network;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import decoblock.TileEntityDecoration;
import io.netty.buffer.ByteBuf;

public class MsgSetTexture implements IMessage {

    public int x, y, z;
    public String textureName;

    public MsgSetTexture() {}

    public MsgSetTexture(String textureName, int x, int y, int z) {
        this.textureName = textureName;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.textureName = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        ByteBufUtils.writeUTF8String(buf, textureName);
    }

    public static class Handler implements IMessageHandler<MsgSetTexture, IMessage> {

        @Override
        public IMessage onMessage(MsgSetTexture message, MessageContext ctx) {
            World world = ctx.getServerHandler().playerEntity.worldObj;
            TileEntity te = world.getTileEntity(message.x, message.y, message.z);
            if (te instanceof TileEntityDecoration) {
                ((TileEntityDecoration) te).setTextureName(message.textureName);
            }
            return null;
        }
    }
}
