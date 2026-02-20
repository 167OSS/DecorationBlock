package decoblock.network;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import decoblock.DecorationMode;
import decoblock.TileEntityDecoration;
import io.netty.buffer.ByteBuf;

public class MsgSetDecorationMode implements IMessage {

    public int x, y, z;
    public DecorationMode mode;

    public MsgSetDecorationMode() {}

    public MsgSetDecorationMode(DecorationMode mode, int x, int y, int z) {
        this.mode = mode;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.mode = DecorationMode.values()[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(mode.ordinal());
    }

    public static class Handler implements IMessageHandler<MsgSetDecorationMode, IMessage> {

        @Override
        public IMessage onMessage(MsgSetDecorationMode message, MessageContext ctx) {
            World world = ctx.getServerHandler().playerEntity.worldObj;
            TileEntity te = world.getTileEntity(message.x, message.y, message.z);
            if (te instanceof TileEntityDecoration) {
                ((TileEntityDecoration) te).setDecorationMode(message.mode);
            }
            return null;
        }
    }
}
