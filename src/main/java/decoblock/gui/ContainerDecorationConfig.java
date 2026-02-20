package decoblock.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import decoblock.TileEntityDecoration;

public class ContainerDecorationConfig extends Container {

    protected TileEntityDecoration tileEntity;

    public ContainerDecorationConfig(InventoryPlayer inventoryPlayer, TileEntityDecoration te) {
        tileEntity = te;

        for (int i = 0; i < 9; ++i) addSlotToContainer(new SlotTab(tileEntity, i, 8 + i * 18, 27 + 14, 1));

        bindPlayerInventory(inventoryPlayer);
    }

    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
        int i, j;

        for (i = 0; i < 9; ++i) for (j = 0; j < 3; ++j)
            addSlotToContainer(new SlotTab(inventoryPlayer, i + j * 9 + 9, 8 + i * 18, 153 + j * 18, 1));

        for (i = 0; i < 9; ++i) addSlotToContainer(new SlotTab(inventoryPlayer, i, 8 + i * 18, 211, 1));
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileEntity.isUseableByPlayer(player);
    }

    @Override
    public ItemStack slotClick(int par1, int par2, int par3, EntityPlayer par4EntityPlayer) {
        if (par1 != -999 && (par2 == 0 || par2 == 1) && par3 == 1) {
            Slot slot = (Slot) this.inventorySlots.get(par1);
            if (slot != null && slot.canTakeStack(par4EntityPlayer)) {
                ItemStack stack = transferStackInSlot(par4EntityPlayer, par1);

                if (stack != null) {
                    return stack.copy();
                }
            }
            return null;
        }
        return super.slotClick(par1, par2, par3, par4EntityPlayer);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
        ItemStack stack = null;

        Slot slot = (Slot) inventorySlots.get(par2);
        if (slot != null && slot.getHasStack()) {
            ItemStack stackInSlot = slot.getStack();
            stack = stackInSlot.copy();
            if (slot.inventory instanceof TileEntityDecoration) {
                stackInSlot = transferStackInSlotToInventory(slot, InventoryPlayer.class, false);
                if (stackInSlot == null) return null;
            } else if (slot.inventory instanceof InventoryPlayer) {
                stackInSlot = transferStackInSlotToInventory(slot, TileEntityDecoration.class, true);
                if (stackInSlot == null) return null;
            }

            if (stackInSlot.stackSize <= 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
        }

        return stack;
    }

    protected ItemStack transferStackInSlotToInventory(Slot source, Class<? extends IInventory> inventory,
        boolean limit) {
        ItemStack stack = null;

        if (source == null || inventory.isInstance(source.inventory)) return null;

        stack = source.getStack();
        if (stack == null) return null;

        ItemStack stackOriginal = stack.copy();

        for (Object o : inventorySlots) {
            Slot slot = (Slot) o;
            if (inventory.isInstance(slot.inventory)) {
                ItemStack stackInSlot = slot.getStack();
                if (stackInSlot != null && stackInSlot.isItemEqual(stack)
                    && ItemStack.areItemStackTagsEqual(stackInSlot, stack)
                    && stackInSlot.stackSize < slot.getSlotStackLimit()
                    && stackInSlot.stackSize < stackInSlot.getMaxStackSize()) {
                    int stackLimit = Math.min(stackInSlot.getMaxStackSize(), slot.getSlotStackLimit());
                    if (stackInSlot.stackSize + stack.stackSize <= stackLimit) {
                        stackInSlot.stackSize += stack.stackSize;
                        stack.stackSize = 0;
                    } else {
                        stack.stackSize -= stackLimit - stackInSlot.stackSize;
                        stackInSlot.stackSize = stackInSlot.getMaxStackSize();
                    }
                    slot.onSlotChanged();
                    if (limit || stack.stackSize <= 0) return stack;
                }
            }
        }

        for (Object o : inventorySlots) {
            Slot slot = (Slot) o;
            if (inventory.isInstance(slot.inventory)) {
                if (!slot.getHasStack() && slot.isItemValid(stack)) {
                    slot.putStack(stack.copy());
                    ItemStack stackInSlot = slot.getStack();
                    if (stack.isItemEqual(stackInSlot) && ItemStack.areItemStackTagsEqual(stackInSlot, stack)) {
                        if (stack.stackSize > stackInSlot.stackSize) {
                            stack.stackSize -= stackInSlot.stackSize;
                        } else {
                            stack.stackSize = 0;
                        }
                        slot.onSlotChanged();
                        if (limit || stack.stackSize <= 0) return stack;
                    }
                }
            }
        }

        if (stackOriginal.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(stackOriginal, stack)
            && stackOriginal.stackSize == stack.stackSize) {
            return null;
        }

        return stack;
    }

    public void onTabChange(int page) {
        for (Object slot : inventorySlots) {
            if (slot instanceof SlotTab) {
                ((SlotTab) slot).onTabChange(page);
            }
        }
    }

}
