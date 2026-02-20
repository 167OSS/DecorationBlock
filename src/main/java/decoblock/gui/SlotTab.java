package decoblock.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class SlotTab extends Slot {

    private int tabId;
    private int realX;
    private int realY;

    public SlotTab(IInventory inventory, int index, int x, int y, int tabId) {
        super(inventory, index, x, y);
        this.tabId = tabId;
        this.realX = x;
        this.realY = y;
    }

    public void onTabChange(int page) {
        if (page == tabId || tabId == -1) {
            this.xDisplayPosition = realX;
            this.yDisplayPosition = realY;
        } else {
            this.xDisplayPosition = -1000;
            this.yDisplayPosition = -1000;
        }
    }
}
