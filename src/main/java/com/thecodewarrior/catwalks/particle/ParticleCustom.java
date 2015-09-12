package com.thecodewarrior.catwalks.particle;

import java.util.Random;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.thecodewarrior.catwalks.CatwalkMod;
import com.thecodewarrior.catwalks.util.ResourceManager;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ParticleCustom extends EntityFX {

	static Random rand = new Random();
	int red, green, blue, animationLength, frameOffset;
	IIcon[] particleIcons;
	
	
	public ParticleCustom(int index, World world, double x, double y, double z, double vX, double vY, double vZ) {
		this(index, world, x, y, z, vX, vY, vZ, 255, 255, 255);
	}
	
	public ParticleCustom(int index, World world, double x, double y, double z, double vX, double vY, double vZ, int red, int green, int blue) {
		super(world, x, y, z, vX, vY, vZ);
		this.motionX = vX;// + (rand.nextGaussian() * 0.02D);
	    this.motionY = vY;// + (rand.nextGaussian() * 0.02D);
	    this.motionZ = vZ;// + (rand.nextGaussian() * 0.02D);
	    this.particleIcons = ResourceManager.getParticle(index);
	    this.animationLength = particleIcons.length;
	    this.particleScale = this.particleScale/2F;
	    this.frameOffset = rand.nextInt(3);
	    this.particleMaxAge = animationLength-1 - frameOffset - rand.nextInt(2);
	    this.red = red;
	    this.green = green;
	    this.blue = blue;
	}
	
	
	// credit to Draconic Evolution
	// https://github.com/brandon3055/Draconic-Evolution/blob/master/src/main/java/com/brandon3055/draconicevolution/client/render/particle/ParticleCustom.java
	@Override
	@SideOnly(Side.CLIENT)
	public void renderParticle(Tessellator tesselator, float par2, float par3, float par4, float par5, float par6, float par7)
	{//Note U=X V=Y		
		tesselator.draw();
		ResourceManager.bindParticles();
		tesselator.startDrawingQuads();
		tesselator.setBrightness(200);//make sure you have this!!
		
		
		float minU = 0;
        float maxU = 1;
        float minV = 0;
        float maxV = 1;
        float drawScale = 0.1F * this.particleScale;

        int frame = this.particleAge + frameOffset;
        
        if (this.particleIcons[frame] != null)
        {
            minU = this.particleIcons[frame].getMinU();
            maxU = this.particleIcons[frame].getMaxU();
            minV = this.particleIcons[frame].getMinV();
            maxV = this.particleIcons[frame].getMaxV();
        }

        float drawX = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)par2 - interpPosX);
        float drawY = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)par2 - interpPosY);
        float drawZ = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)par2 - interpPosZ);
        
        tesselator.setColorRGBA(red, green, blue, (int)(this.particleAlpha * 255F));
        
        tesselator.addVertexWithUV((double)(drawX - par3 * drawScale - par6 * drawScale), (double)(drawY - par4 * drawScale), (double)(drawZ - par5 * drawScale - par7 * drawScale), (double)maxU, (double)maxV);
        tesselator.addVertexWithUV((double)(drawX - par3 * drawScale + par6 * drawScale), (double)(drawY + par4 * drawScale), (double)(drawZ - par5 * drawScale + par7 * drawScale), (double)maxU, (double)minV);
        tesselator.addVertexWithUV((double)(drawX + par3 * drawScale + par6 * drawScale), (double)(drawY + par4 * drawScale), (double)(drawZ + par5 * drawScale + par7 * drawScale), (double)minU, (double)minV);
        tesselator.addVertexWithUV((double)(drawX + par3 * drawScale - par6 * drawScale), (double)(drawY - par4 * drawScale), (double)(drawZ + par5 * drawScale - par7 * drawScale), (double)minU, (double)maxV);
        
        tesselator.draw();
		ResourceManager.bindDefaultParticles();
		tesselator.startDrawingQuads();
		
	}

}
