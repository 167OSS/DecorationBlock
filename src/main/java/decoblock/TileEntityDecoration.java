package decoblock;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import decoblock.network.MsgSetDecorationMode;
import decoblock.network.MsgSetMirror;
import decoblock.network.MsgSetRotate;
import decoblock.network.MsgSetTexture;

public class TileEntityDecoration extends TileEntity implements ISidedInventory {

    public String textureName = ModConfiguration.defaultTexture;
    public DecorationMode mode = ModConfiguration.defaultType;
    public boolean mirror = false;
    public int rotate = 0;

    public int lightValue = 0;
    public boolean collidable = false;
    public boolean ladder = false;
    public boolean flammable = false;
    public boolean burning = false;
    public int damageSource = -1;
    public DecorationParticle particle = null;

    ItemStack[] inventory = new ItemStack[9];

    public TileEntityDecoration() {}

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        textureName = nbttagcompound.getString("Texture");
        mode = DecorationMode.fromTypeText(nbttagcompound.getString("Mode"));
        mirror = nbttagcompound.getBoolean("Mirror");
        rotate = nbttagcompound.getByte("Rotate");
        burning = nbttagcompound.getBoolean("Burning");

        for (int i = 0; i < inventory.length; ++i) {
            if (nbttagcompound.hasKey("inventory[" + i + "]")) {
                inventory[i] = ItemStack.loadItemStackFromNBT(nbttagcompound.getCompoundTag("inventory[" + i + "]"));
            }
        }
        updateEffect();
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setString("Texture", textureName);
        nbttagcompound.setString("Mode", mode.typeText());
        nbttagcompound.setBoolean("Mirror", mirror);
        nbttagcompound.setByte("Rotate", (byte) rotate);
        nbttagcompound.setBoolean("Burning", burning);

        for (int i = 0; i < inventory.length; ++i) {
            if (inventory[i] != null) {
                nbttagcompound.setTag("inventory[" + i + "]", inventory[i].writeToNBT(new NBTTagCompound()));
            }
        }
    }

    public World getWorld() {
        return worldObj;
    }

    public void setDecorationMode(DecorationMode mode) {
        if (worldObj.isRemote) {
            DecorationBlock.network.sendToServer(new MsgSetDecorationMode(mode, xCoord, yCoord, zCoord));
        }

        this.mode = mode;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void setTextureName(String name) {
        if (worldObj.isRemote) {
            DecorationBlock.network.sendToServer(new MsgSetTexture(name, xCoord, yCoord, zCoord));
        }

        this.textureName = name;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void setMirror(boolean mirror) {
        if (worldObj.isRemote) {
            DecorationBlock.network.sendToServer(new MsgSetMirror(mirror, xCoord, yCoord, zCoord));
        }

        this.mirror = mirror;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void setRotate(int rotate) {
        if (rotate < 0 || rotate >= 4) this.rotate = 0;
        else this.rotate = rotate;

        if (worldObj.isRemote) {
            DecorationBlock.network.sendToServer(new MsgSetRotate(this.rotate, xCoord, yCoord, zCoord));
        }

        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound var1 = new NBTTagCompound();
        this.writeToNBT(var1);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, var1);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this
            && player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) < 64;
    }

    @Override
    public int getSizeInventory() {
        return inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int var1) {
        return inventory[var1];
    }

    @Override
    public ItemStack decrStackSize(int var1, int var2) {
        ItemStack stack = getStackInSlot(var1);
        if (stack != null) {
            if (stack.stackSize <= var2) {
                setInventorySlotContents(var1, null);
            } else {
                stack = stack.splitStack(var2);
                if (stack.stackSize == 0) {
                    setInventorySlotContents(var1, null);
                }
            }
        }
        return stack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int var1) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int var1, ItemStack var2) {
        inventory[var1] = var2;
        if (var2 != null && var2.stackSize > getInventoryStackLimit()) {
            var2.stackSize = getInventoryStackLimit();
        }
        updateEffect();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public String getInventoryName() {
        return "inventory.decorationblock";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return true;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
    }

    @Override
    public boolean canInsertItem(int i, ItemStack itemstack, int j) {
        return true;
    }

    @Override
    public boolean canExtractItem(int i, ItemStack itemstack, int j) {
        return true;
    }

    private void resetEffect() {
        lightValue = 0;
        collidable = false;
        ladder = false;
        flammable = false;
        damageSource = -1;
        particle = null;
    }

    private void updateEffect() {
        resetEffect();

        for (int i = 0; i < 9; ++i) {
            if (inventory[i] != null) {
                DecorationEffect effect = DecorationEffect.getEffect(inventory[i]);
                lightValue = Math.max(lightValue, effect.lightValue);
                collidable |= effect.collidable;
                ladder |= effect.ladder;
                flammable |= effect.flammable;
                if (damageSource < 0 && effect.weapon) damageSource = i;
                if (particle == null) particle = effect.particle;
            }
        }

        if (!flammable) burning = false;
    }
}
