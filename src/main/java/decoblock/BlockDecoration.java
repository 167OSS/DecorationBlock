package decoblock;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import decoblock.gui.GuiHandler;

public class BlockDecoration extends BlockContainer {

    protected float hardnessCollidable;

    public BlockDecoration setHardnessCollidable(float hardness) {
        hardnessCollidable = hardness;
        return this;
    }

    public BlockDecoration() {
        super(Material.wood);
        this.setLightOpacity(0); // 确保光线可以穿过，不产生阴影
        setHardness(ModConfiguration.hardness);
        setHardnessCollidable(ModConfiguration.hardnessCollidable);
        setStepSound(Block.soundTypeGrass);
        setBlockName("decorationBlock");
        setBlockTextureName("decoblock:decorationBlock");
        setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public boolean getUseNeighborBrightness() {
        return true; // 强制从相邻方块采样光照，修复只有特定方向正确的问题
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityDecoration();
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityDecoration && ((TileEntityDecoration) tileEntity).collidable) {
            setBlockBoundsBasedOnState(world, x, y, z);
            return super.getCollisionBoundingBoxFromPool(world, x, y, z);
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int i, int j, int k) {
        setBlockBoundsBasedOnState(world, i, j, k);
        return super.getSelectedBoundingBoxFromPool(world, i, j, k);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
        TileEntity tileEntityRaw = par1IBlockAccess.getTileEntity(par2, par3, par4);
        if (!(tileEntityRaw instanceof TileEntityDecoration)) return;

        TileEntityDecoration tileEntity = (TileEntityDecoration) tileEntityRaw;
        int var5 = par1IBlockAccess.getBlockMetadata(par2, par3, par4) & 7;
        float var6 = 0.0625F;

        switch (tileEntity.mode) {
            case CROSS:
                this.setBlockBounds(var6, 0.0F, var6, 1.0F - var6, 1.0F, 1.0F - var6);
                break;
            case CENTER:
                if (var5 == 2 || var5 == 3) {
                    this.setBlockBounds(0.0F, 0.0F, 0.5F - var6, 1.0F, 1.0F, 0.5F + var6);
                } else if (var5 == 4 || var5 == 5) {
                    this.setBlockBounds(0.5F - var6, 0.0F, 0.0F, 0.5F + var6, 1.0F, 1.0F);
                } else {
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                }
                break;
            case PLATE:
                switch (var5) {
                    case 2:
                        this.setBlockBounds(0.0F, 0.0F, 1.0F - var6 * 2, 1.0F, 1.0F, 1.0F);
                        break;
                    case 3:
                        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var6 * 2);
                        break;
                    case 4:
                        this.setBlockBounds(1.0F - var6 * 2, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                        break;
                    case 5:
                        this.setBlockBounds(0.0F, 0.0F, 0.0F, var6 * 2, 1.0F, 1.0F);
                        break;
                    default:
                        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                }
                break;
            default:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7,
        float par8, float par9) {
        if (entityplayer.isSneaking()) {
            return false;
        }
        entityplayer.openGui(DecorationBlock.instance, GuiHandler.ID_GUI_DECORATION_CONFIG, world, i, j, k);
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entityliving, ItemStack itemstack) {
        byte byte0 = 0;
        int l1 = MathHelper.floor_double((entityliving.rotationYaw * 4F) / 360F + 0.5D) & 3;
        if (l1 == 0) {
            byte0 = 2;
        }
        if (l1 == 1) {
            byte0 = 5;
        }
        if (l1 == 2) {
            byte0 = 3;
        }
        if (l1 == 3) {
            byte0 = 4;
        }
        world.setBlockMetadataWithNotify(i, j, k, byte0, 3);
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityDecoration) {
            return ((TileEntityDecoration) tileEntity).lightValue;
        }
        return 0;
    }

    @Override
    public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityDecoration) {
            return ((TileEntityDecoration) tileEntity).ladder;
        }
        return false;
    }

    @Override
    public void breakBlock(World par1World, int par2, int par3, int par4, Block par5, int par6) {
        dropItems(par1World, par2, par3, par4);
        super.breakBlock(par1World, par2, par3, par4, par5, par6);
    }

    private void dropItems(World world, int x, int y, int z) {
        Random rand = new Random();

        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (!(tileEntity instanceof IInventory)) {
            return;
        }
        IInventory inventory = (IInventory) tileEntity;

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack item = inventory.getStackInSlot(i);

            if (item != null && item.stackSize > 0) {
                float rx = rand.nextFloat() * 0.8F + 0.1F;
                float ry = rand.nextFloat() * 0.8F + 0.1F;
                float rz = rand.nextFloat() * 0.8F + 0.1F;

                EntityItem entityItem = new EntityItem(world, x + rx, y + ry, z + rz, item.copy());

                float factor = 0.05F;
                entityItem.motionX = rand.nextGaussian() * factor;
                entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
                entityItem.motionZ = rand.nextGaussian() * factor;
                world.spawnEntityInWorld(entityItem);
                item.stackSize = 0;
            }
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityDecoration) {
            TileEntityDecoration decoration = (TileEntityDecoration) tileEntity;
            if (decoration.damageSource >= 0 && decoration.damageSource < 9) {
                ItemStack weapon = decoration.getStackInSlot(decoration.damageSource);
                if (weapon != null && entity instanceof EntityLivingBase) {
                    if (entity.attackEntityFrom(DamageSource.cactus, 1.0F)) {
                        // In 1.7.10 hitEntity logic is complex,
                        // but we align with source as much as possible
                    }
                }
            }
        }
    }

    @Override
    public float getBlockHardness(World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityDecoration && ((TileEntityDecoration) tileEntity).collidable) {
            return hardnessCollidable;
        }
        return super.getBlockHardness(world, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityDecoration) {
            TileEntityDecoration decoration = (TileEntityDecoration) tileEntity;
            if (decoration.particle != null) decoration.particle.spawnParticle(world, x, y, z, random);
        }
    }
}
