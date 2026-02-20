package decoblock;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class DecorationEffect {

    public ItemStack itemstack;
    public int lightValue = 0;
    public boolean collidable = false;
    public boolean ladder = false;
    public boolean flammable = false;
    public boolean weapon;
    public DecorationParticle particle = null;

    private static List<DecorationEffect> effects = new ArrayList<DecorationEffect>();

    static {
        getOrRegisterEffect(new ItemStack(Blocks.iron_bars)).setCollidable(true);
        getOrRegisterEffect(new ItemStack(Blocks.ladder)).setLadder(true);
        getOrRegisterEffect(new ItemStack(Blocks.vine)).setLadder(true);
        getOrRegisterEffect(new ItemStack(Blocks.cactus)).setWeapon(true);
    }

    public static DecorationEffect getEffect(ItemStack itemstack) {
        for (DecorationEffect effect : effects) {
            if (effect.itemstack.isItemEqual(itemstack)) {
                return effect;
            }
            if (effect.itemstack.getItem()
                .isDamageable() && effect.itemstack.getItem() == itemstack.getItem()) {
                return effect;
            }
        }
        return new DecorationEffect(itemstack);
    }

    public static DecorationEffect getOrRegisterEffect(DecorationEffect newEffect) {
        for (DecorationEffect effect : effects) {
            if (effect.itemstack.isItemEqual(newEffect.itemstack)) {
                return effect;
            }
            if (effect.itemstack.getItem()
                .isDamageable() && effect.itemstack.getItem() == newEffect.itemstack.getItem()) {
                return effect;
            }
        }
        effects.add(newEffect);
        return newEffect;
    }

    public static DecorationEffect getOrRegisterEffect(ItemStack itemstack) {
        return getOrRegisterEffect(new DecorationEffect(itemstack));
    }

    public DecorationEffect(ItemStack itemstack) {
        this.itemstack = itemstack;
        if (itemstack != null && itemstack.getItem() != null) {
            if (itemstack.getItem() instanceof ItemBlock) {
                Block block = Block.getBlockFromItem(itemstack.getItem());
                lightValue = block.getLightValue();
                collidable = block == Blocks.iron_bars;
                ladder = block == Blocks.ladder || block == Blocks.vine;
                flammable = block == Blocks.netherrack;
            }
            weapon = itemstack.getItem() instanceof ItemSword;
        }
    }

    public DecorationEffect setLightValue(int lightValue) {
        this.lightValue = lightValue;
        return this;
    }

    public DecorationEffect setCollidable(boolean collidable) {
        this.collidable = collidable;
        return this;
    }

    public DecorationEffect setLadder(boolean ladder) {
        this.ladder = ladder;
        return this;
    }

    public DecorationEffect setFlammable(boolean flammable) {
        this.flammable = flammable;
        return this;
    }

    public DecorationEffect setWeapon(boolean weapon) {
        this.weapon = weapon;
        return this;
    }

    public DecorationEffect setParticle(DecorationParticle particle) {
        this.particle = particle;
        return this;
    }
}
