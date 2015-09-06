package com.thecodewarrior.catwalks.render;

import org.lwjgl.opengl.GL11;

import com.thecodewarrior.catwalks.IInOutRenderSettings;

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
		boolean forceBackFace = false;
		
		if(block instanceof IInOutRenderSettings) {
			forceBackFace = ( (IInOutRenderSettings)block ).shouldForceBackFaceRender();
		}
		
		Tessellator tessellator = Tessellator.instance;
	    GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
	    tessellator.startDrawingQuads();

	    if(forceBackFace) {
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
	    }
	    
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
	    if(forceBackFace)
	    	GL11.glEnable(GL11.GL_CULL_FACE);
	    
	    tessellator.draw();
	    
	    if(!a && forceBackFace)
	    	GL11.glDisable(GL11.GL_CULL_FACE);
	    
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
