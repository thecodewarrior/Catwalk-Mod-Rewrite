package com.thecodewarrior.catwalks;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class InAndOutRenderer implements ISimpleBlockRenderingHandler {

	public int renderid;
	
	public InAndOutRenderer(int id) {
		renderid = id;
	}
	
	@Override
	public void renderInventoryBlock(Block block, int meta, int modelId,
			RenderBlocks renderer) {
		Tessellator tessellator = Tessellator.instance;
	    GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
	    tessellator.startDrawingQuads();

	    // no need for inside/outside rendering, items don't hide the backs of faces
	    
	    tessellator.setNormal(0, -1, 0);
	    renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 0, meta));
	    tessellator.setNormal(0, +1, 0);
	    renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 1, meta));
	    tessellator.setNormal(0, 0, -1);
	    renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 2, meta));
	    tessellator.setNormal(0, 0, +1);
	    renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 3, meta));
	    tessellator.setNormal(-1, 0, 0);
	    renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 4, meta));
	    tessellator.setNormal(+1, 0, 0);
	    renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 5, meta));
	    
	    tessellator.draw();
	    GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		renderer.flipTexture = true;
		renderer.renderFromInside = true;
		renderer.renderStandardBlock(block, x, y, z);
		
		renderer.flipTexture = false;
		renderer.renderFromInside = false;
		renderer.renderStandardBlock(block, x, y, z);
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return renderid;
	}

}
