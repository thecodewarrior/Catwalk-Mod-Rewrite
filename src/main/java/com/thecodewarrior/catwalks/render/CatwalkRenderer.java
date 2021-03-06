package com.thecodewarrior.catwalks.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.thecodewarrior.catwalks.CatwalkMod;
import com.thecodewarrior.catwalks.block.BlockCatwalk;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class CatwalkRenderer implements ISimpleBlockRenderingHandler {
	
	@Override
	public void renderInventoryBlock(Block block, int meta, int modelId,
			RenderBlocks renderer) {
		boolean lights = false;
		boolean bottom = true;
		if(block instanceof BlockCatwalk) {
			lights = ( (BlockCatwalk)block).lights;
			bottom = ( (BlockCatwalk)block).bottom;
		}
		
		renderer.overrideBlockBounds(0, 0, 0, 1, 1, 1);
		Tessellator tessellator = Tessellator.instance;
	    GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
	    tessellator.startDrawingQuads();
	    
	    renderer.flipTexture = true;
	    renderer.renderFromInside = true;
	    tessellator.setNormal(0, +1, 0);
	    renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 100, meta));
	    tessellator.setNormal(0, -1, 0);
	    renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 101, meta));
	    tessellator.setNormal(0, 0, +1);
	    renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 102, meta));
	    tessellator.setNormal(0, 0, -1);
	    renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 103, meta));
	    tessellator.setNormal(+1, 0, 0);
	    renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 104, meta));
	    tessellator.setNormal(-1, 0, 0);
	    renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 105, meta));
	    
	    renderer.flipTexture = false;
	    renderer.renderFromInside = false;
	    tessellator.setNormal(0, -1, 0);
	    renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 100, meta));
	    tessellator.setNormal(0, +1, 0);
	    renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 101, meta));
	    tessellator.setNormal(0, 0, -1);
	    renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 102, meta));
	    tessellator.setNormal(0, 0, +1);
	    renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 103, meta));
	    tessellator.setNormal(-1, 0, 0);
	    renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 104, meta));
	    tessellator.setNormal(+1, 0, 0);
	    renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 105, meta));
	    
	    boolean a = GL11.glGetBoolean(GL11.GL_CULL_FACE);
	    GL11.glEnable(GL11.GL_CULL_FACE);
	    
	    tessellator.draw();
	    
	    if(!a)
	    	GL11.glDisable(GL11.GL_CULL_FACE);
	    
	    GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	    
	    renderer.unlockBlockBounds();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block _block, int modelId, RenderBlocks renderer) {
		
		
		int meta = world.getBlockMetadata(x, y, z);
		BlockCatwalk block = (BlockCatwalk)_block;
		boolean lights = block.lights;
		boolean bottom = block.bottom;
		renderer.overrideBlockBounds(0, 0, 0, 1, 1, 1);
		
		renderer.flipTexture = true;
		renderer.renderFromInside = true;
		renderer.renderStandardBlock(block, x, y, z);
		
		renderer.flipTexture = false;
		renderer.renderFromInside = false;
		renderer.renderStandardBlock(block, x, y, z);
		
		boolean force = false;
		if(lights) {			
			boolean oldAO = renderer.enableAO;
			renderer.enableAO = false; // don't go re-calculating the light on me
			
			
			/* 
			 * MinecraftForge.net forums to the rescue!
			 * "If you are curious about why I chose 15728880...
			 * 
			 * 15728880 = 15 << 20 | 15 << 4
			 * The first 15 is lighting of environment, and the second 15 is lighting of itself.
			 * So, I guess that 15728880 is potentially the brightest lighting in game..."
			 */
			Tessellator.instance.setBrightness(15728880); // GIMME BRIGHT!
			
			renderer.flipTexture = true;
			renderer.renderFromInside = true;
			
			if( block.shouldSideBeRendered(world, x, y-1, z, 0) || force)
				renderer.renderFaceYNeg(block, x, y, z, block.getLightIcon(0, meta));
			if( block.shouldSideBeRendered(world, x, y, z-1, 2) || force)
				renderer.renderFaceZNeg(block, x, y, z, block.getLightIcon(2, meta));
			if( block.shouldSideBeRendered(world, x, y, z+1, 3) || force)
				renderer.renderFaceZPos(block, x, y, z, block.getLightIcon(3, meta));
			if( block.shouldSideBeRendered(world, x-1, y, z, 4) || force )
				renderer.renderFaceXNeg(block, x, y, z, block.getLightIcon(4, meta));
			if( block.shouldSideBeRendered(world, x+1, y, z, 5) || force )
				renderer.renderFaceXPos(block, x, y, z, block.getLightIcon(5, meta));
			
			renderer.flipTexture = false;
			renderer.renderFromInside = false;
			
			if( block.shouldSideBeRendered(world, x, y-1, z, 0) || force)
				renderer.renderFaceYNeg(block, x, y, z, block.getLightIcon(0, meta));
			if( block.shouldSideBeRendered(world, x, y, z-1, 2) || force)
				renderer.renderFaceZNeg(block, x, y, z, block.getLightIcon(2, meta));
			if( block.shouldSideBeRendered(world, x, y, z+1, 3) || force)
				renderer.renderFaceZPos(block, x, y, z, block.getLightIcon(3, meta));
			if( block.shouldSideBeRendered(world, x-1, y, z, 4) || force )
				renderer.renderFaceXNeg(block, x, y, z, block.getLightIcon(4, meta));
			if( block.shouldSideBeRendered(world, x+1, y, z, 5) || force )
				renderer.renderFaceXPos(block, x, y, z, block.getLightIcon(5, meta));
			
			renderer.enableAO = oldAO; // whatever you were doing before, continue.
		}
		renderer.unlockBlockBounds();
		renderer.clearOverrideBlockTexture();
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return CatwalkMod.catwalkRenderType;
	}

}
