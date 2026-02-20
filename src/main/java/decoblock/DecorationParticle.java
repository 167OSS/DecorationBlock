package decoblock;

import java.util.Random;

import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DecorationParticle {

    public String particle;
    public double ofsX;
    public double ofsY;
    public double ofsZ;
    public double rangeX;
    public double rangeY;
    public double rangeZ;
    public double velX;
    public double velY;
    public double velZ;
    public double rangeVelX;
    public double rangeVelY;
    public double rangeVelZ;

    public DecorationParticle(String particle, double ofsX, double ofsY, double rangeX, double rangeY, double rangeZ,
        double ofsZ, double velX, double velY, double velZ, double rangeVelX, double rangeVelY, double rangeVelZ) {
        this.particle = particle;
        this.ofsX = ofsX;
        this.ofsY = ofsY;
        this.ofsZ = ofsZ;
        this.rangeX = rangeX;
        this.rangeY = rangeY;
        this.rangeZ = rangeZ;
        this.velX = velX;
        this.velY = velY;
        this.velZ = velZ;
        this.rangeVelX = rangeVelX;
        this.rangeVelY = rangeVelY;
        this.rangeVelZ = rangeVelZ;
    }

    @SideOnly(Side.CLIENT)
    public void spawnParticle(World world, int x, int y, int z, Random random) {
        double posX = x + ofsX;
        double posY = y + ofsY;
        double posZ = z + ofsZ;
        double velX = this.velX;
        double velY = this.velY;
        double velZ = this.velZ;
        if (rangeX > 0) posX += (random.nextDouble() - 0.5D) * rangeX;
        if (rangeY > 0) posY += (random.nextDouble() - 0.5D) * rangeY;
        if (rangeZ > 0) posZ += (random.nextDouble() - 0.5D) * rangeZ;
        if (rangeVelX > 0) velX += (random.nextDouble() - 0.5D) * rangeVelX;
        if (rangeVelY > 0) velY += (random.nextDouble() - 0.5D) * rangeVelY;
        if (rangeVelZ > 0) velZ += (random.nextDouble() - 0.5D) * rangeVelZ;
        world.spawnParticle(particle, posX, posY, posZ, velX, velY, velZ);
    }
}
