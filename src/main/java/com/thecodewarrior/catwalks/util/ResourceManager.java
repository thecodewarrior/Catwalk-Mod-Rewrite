package com.thecodewarrior.catwalks.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import com.thecodewarrior.catwalks.CatwalkMod;

import cpw.mods.fml.relauncher.ReflectionHelper;

public class ResourceManager {
	
	static List<IIcon[]> particleIcons = new ArrayList<IIcon[]>();
	static ResourceLocation particles = new ResourceLocation(CatwalkMod.MODID, "textures/particles.png");
	static ResourceLocation defaultParticles;
	
	public static void refreshIcons() {
		particleIcons = new ArrayList<IIcon[]>();
		float widthPer = 1/(float)getParticleCount();
		float heightPer = 1/(float)getParticleFrames();
		
		for(int i = 0; i < getParticleCount(); i++) {
			IIcon[] arr = new IIcon[getParticleLength(i)];
			particleIcons.add(arr);
			for(int f = 0; f < getParticleLength(i); f++) {
				IIcon icon = new BasicIcon(  i   *widthPer,  f   *heightPer,
											(i+1)*widthPer, (f+1)*heightPer,
											8, 8);
				arr[f] = icon;
			}
		}
	}
	
	public static int getParticleCount() {
		return 8;
	}
	
	public static int getParticleFrames() {
		return 8;
	}
	
	public static int getParticleLength(int particle) {
		switch(particle) {
		case 0: return 8;
		case 1: return 8;
		case 2: return 8;
		case 3: return 8;
		default: return 1;
		}
	}
	
	public static IIcon[] getParticle(int position) {
		if(getParticleCount() != particleIcons.size())
			refreshIcons();
		if(position >= particleIcons.size())
			return getParticle(0);			
		return particleIcons.get(position);
	}
	
	public static void bindParticles() {
		bindTexture(particles);
	}	
	
	public static void bindTexture(ResourceLocation texture)
	{
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
	}

	/**Binds the vanilla particle sheet*/
	public static void bindDefaultParticles()
	{
		if (defaultParticles == null)
		{
			try
			{
				defaultParticles = (ResourceLocation) ReflectionHelper.getPrivateValue(EffectRenderer.class, null, "particleTextures", "field_110737_b");
			}
			catch (Exception e) {}
		}
		if (defaultParticles != null) bindTexture(defaultParticles);
	}
	
	public static class BasicIcon implements IIcon {

		float minU, minV, maxU, maxV;
		int width, height;
		
		public BasicIcon(float minU, float minV, float maxU, float maxV, int width, int height) {
			this.minU = minU;
			this.minV = minV;
			this.maxU = maxU;
			this.maxV = maxV;
			this.width = width;
			this.height = height;
		}
		
		@Override
		public int getIconWidth() {
			return width;
		}

		@Override
		public int getIconHeight() {
			return height;
		}

		@Override
		public float getMinU() {
			return minU;
		}

		@Override
		public float getMaxU() {
			return maxU;
		}

		@Override
		public float getInterpolatedU(double d) {
			return (float) (minU + ( (maxU-minU) * d));
		}

		@Override
		public float getMinV() {
			return minV;
		}

		@Override
		public float getMaxV() {
			return maxV;
		}

		@Override
		public float getInterpolatedV(double d) {
			return (float) (minV + ( (maxV-minV) * d));
		}

		@Override
		public String getIconName() {
			return "[BASIC ICON " + Integer.toHexString(hashCode()) + "]";
		}
		
	}
}
