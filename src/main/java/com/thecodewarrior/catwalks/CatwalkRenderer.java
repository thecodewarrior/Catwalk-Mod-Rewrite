package com.thecodewarrior.catwalks;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

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
	    
	    renderer.renderFromInside = true;
	    renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 0, meta));
	    renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 1, meta));
	    renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 2, meta));
	    renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 3, meta));
	    renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 4, meta));
	    renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 5, meta));
	    
	    renderer.renderFromInside = false;
	    renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 0, meta));
	    renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 1, meta));
	    renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 2, meta));
	    renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 3, meta));
	    renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 4, meta));
	    renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 5, meta));
	    
	    tessellator.draw();
	    GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	    
	    renderer.unlockBlockBounds();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		
		boolean lights = false;
		boolean bottom = true;
		if(block instanceof BlockCatwalk) {
			lights = ( (BlockCatwalk)block).lights;
			bottom = ( (BlockCatwalk)block).bottom;
		}
		
//		renderer.renderFromInside = true;
//		renderer.renderStandardBlock(block, x, y, z);
//		RenderBlocks renderer = new RenderBlocks(world);
//		renderer.setRenderFromInside(true);
//		renderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
		renderer.overrideBlockBounds(0, 0, 0, 1, 1, 1);
		renderer.renderFromInside = true;
		renderer.renderStandardBlock(block, x, y, z);
		renderer.renderFromInside = false;
		renderer.renderStandardBlock(block, x, y, z);
		boolean force = false;
		if(lights) {
			BlockCatwalk b = ((BlockCatwalk) block);
			
			boolean oldAO = renderer.enableAO;
			renderer.enableAO = false;
			
			Tessellator.instance.setBrightness(15728880);
			
			renderer.renderFromInside = true;
			
			if( b.shouldSideBeRendered(world, x, y-1, z, 0) || force)
				renderer.renderFaceYNeg(b, x, y, z, b.bottomLights);
			if( b.shouldSideBeRendered(world, x, y, z-1, 2) || force)
				renderer.renderFaceZNeg(b, x, y, z, b.sideLights);
			if( b.shouldSideBeRendered(world, x, y, z+1, 3) || force)
				renderer.renderFaceZPos(b, x, y, z, b.sideLights);
			if( b.shouldSideBeRendered(world, x-1, y, z, 4) || force )
				renderer.renderFaceXNeg(b, x, y, z, b.sideLights);
			if( b.shouldSideBeRendered(world, x+1, y, z, 5) || force )
				renderer.renderFaceXPos(b, x, y, z, b.sideLights);
			
			renderer.renderFromInside = false;
			
			if( b.shouldSideBeRendered(world, x, y-1, z, 0) || force)
				renderer.renderFaceYNeg(b, x, y, z, b.bottomLights);
			if( b.shouldSideBeRendered(world, x, y, z-1, 2) || force)
				renderer.renderFaceZNeg(b, x, y, z, b.sideLights);
			if( b.shouldSideBeRendered(world, x, y, z+1, 3) || force)
				renderer.renderFaceZPos(b, x, y, z, b.sideLights);
			if( b.shouldSideBeRendered(world, x-1, y, z, 4) || force )
				renderer.renderFaceXNeg(b, x, y, z, b.sideLights);
			if( b.shouldSideBeRendered(world, x+1, y, z, 5) || force )
				renderer.renderFaceXPos(b, x, y, z, b.sideLights);
			
			renderer.enableAO = oldAO;
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
