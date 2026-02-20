package decoblock.network;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import decoblock.TileEntityDecoration;
import io.netty.buffer.ByteBuf;

public class MsgTileEntityData implements IMessage {

    public int x, y, z;
    public NBTTagCompound data;

    public MsgTileEntityData() {}

    public MsgTileEntityData(int x, int y, int z, NBTTagCompound data) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.data = data;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.data = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        ByteBufUtils.writeTag(buf, data);
    }

    public static class Handler implements IMessageHandler<MsgTileEntityData, IMessage> {

        @Override
        public IMessage onMessage(MsgTileEntityData message, MessageContext ctx) {
            World world = decoblock.DecorationBlock.proxy.getClientWorld();
            if (world != null) {
                TileEntity te = world.getTileEntity(message.x, message.y, message.z);
                if (te instanceof TileEntityDecoration) {
                    te.readFromNBT(message.data);
                }
            }
            return null;
        }
    }
}
