package decoblock.network;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import decoblock.TileEntityDecoration;
import io.netty.buffer.ByteBuf;

public class MsgSetMirror implements IMessage {

    public int x, y, z;
    public boolean mirror;

    public MsgSetMirror() {}

    public MsgSetMirror(boolean mirror, int x, int y, int z) {
        this.mirror = mirror;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.mirror = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeBoolean(mirror);
    }

    public static class Handler implements IMessageHandler<MsgSetMirror, IMessage> {

        @Override
        public IMessage onMessage(MsgSetMirror message, MessageContext ctx) {
            World world = ctx.getServerHandler().playerEntity.worldObj;
            TileEntity te = world.getTileEntity(message.x, message.y, message.z);
            if (te instanceof TileEntityDecoration) {
                ((TileEntityDecoration) te).setMirror(message.mirror);
            }
            return null;
        }
    }
}
