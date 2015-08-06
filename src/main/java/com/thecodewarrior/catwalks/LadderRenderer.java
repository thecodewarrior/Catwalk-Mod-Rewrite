package com.thecodewarrior.catwalks;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class LadderRenderer implements ISimpleBlockRenderingHandler {

	public LadderRenderer() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId,
			RenderBlocks renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		BlockCagedLadder bcl = (BlockCagedLadder) block;
		boolean lights = bcl.lights;
		
		
//		renderer.renderFromInside = true;
//		renderer.renderStandardBlock(block, x, y, z);
//		RenderBlocks renderer = new RenderBlocks(world);
//		renderer.setRenderFromInside(true);
//		renderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
		double px = 1/16.0;
		renderer.overrideBlockBounds(px, 0, px, 1-px, 1, 1-px);
		renderer.renderFromInside = true;
		renderer.renderStandardBlock(block, x, y, z);
		renderer.renderFromInside = false;
		renderer.renderStandardBlock(block, x, y, z);
		
		boolean force = false;
		int meta = world.getBlockMetadata(x, y, z);
		if(lights) {			
			boolean oldAO = renderer.enableAO;
			renderer.enableAO = false;
			
			Tessellator.instance.setBrightness(15728880);
			
			renderer.renderFromInside = true;
			
			if( bcl.shouldSideBeRendered(world, x, y-1, z, 0) || force)
				renderer.renderFaceYNeg(bcl, x, y, z, bcl.getLightIcon(0, meta));
			if( bcl.shouldSideBeRendered(world, x, y, z-1, 2) || force)
				renderer.renderFaceZNeg(bcl, x, y, z, bcl.getLightIcon(2, meta));
			if( bcl.shouldSideBeRendered(world, x, y, z+1, 3) || force)
				renderer.renderFaceZPos(bcl, x, y, z, bcl.getLightIcon(3, meta));
			if( bcl.shouldSideBeRendered(world, x-1, y, z, 4) || force )
				renderer.renderFaceXNeg(bcl, x, y, z, bcl.getLightIcon(4, meta));
			if( bcl.shouldSideBeRendered(world, x+1, y, z, 5) || force )
				renderer.renderFaceXPos(bcl, x, y, z, bcl.getLightIcon(5, meta));
			
			renderer.renderFromInside = false;
			
			if( bcl.shouldSideBeRendered(world, x, y-1, z, 0) || force)
				renderer.renderFaceYNeg(bcl, x, y, z, bcl.getLightIcon(0, meta));
			if( bcl.shouldSideBeRendered(world, x, y, z-1, 2) || force)
				renderer.renderFaceZNeg(bcl, x, y, z, bcl.getLightIcon(2, meta));
			if( bcl.shouldSideBeRendered(world, x, y, z+1, 3) || force)
				renderer.renderFaceZPos(bcl, x, y, z, bcl.getLightIcon(3, meta));
			if( bcl.shouldSideBeRendered(world, x-1, y, z, 4) || force )
				renderer.renderFaceXNeg(bcl, x, y, z, bcl.getLightIcon(4, meta));
			if( bcl.shouldSideBeRendered(world, x+1, y, z, 5) || force )
				renderer.renderFaceXPos(bcl, x, y, z, bcl.getLightIcon(5, meta));
			
			renderer.enableAO = oldAO;
		}
		
		renderer.unlockBlockBounds();
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getRenderId() {
		// TODO Auto-generated method stub
		return CatwalkMod.ladderRenderType;
	}

}
