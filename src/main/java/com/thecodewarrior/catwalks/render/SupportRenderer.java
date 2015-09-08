package com.thecodewarrior.catwalks.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import com.thecodewarrior.catwalks.block.BlockSupportColumn;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class SupportRenderer implements ISimpleBlockRenderingHandler {

	int typeID;
	
	public SupportRenderer(int id) {
		typeID = id;
	}
	
	@Override
	public void renderInventoryBlock(Block block, int meta, int modelId,
			RenderBlocks renderer) {
		
		double blockWidth = ( (BlockSupportColumn)block ).getWidth();
		double d = (1-blockWidth)/2;
		double D = 1-d;
		
		Tessellator tessellator = Tessellator.instance;
	    tessellator.startDrawingQuads();
	    
	    renderer.overrideBlockBounds(d, 0, d, D, 1, D);
	    
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
	    
	    renderer.unlockBlockBounds();
	    
	    GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
	    tessellator.draw();
	    GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		int meta = world.getBlockMetadata(x, y, z);
		double blockWidth = ( (BlockSupportColumn)block ).getWidth();
		double d = (1-blockWidth)/2;
		double D = 1-d;
		
		boolean force = false;
		boolean posX = force, posY = force, posZ = force, negX = force, negY = force, negZ = force;
		
		if(world.getBlock(x+1, y, z) instanceof BlockSupportColumn)
			posX = true;
		if(world.getBlock(x-1, y, z) instanceof BlockSupportColumn)
			negX = true;
		
		if(world.getBlock(x, y+1, z) instanceof BlockSupportColumn)
			posY = true;
		if(world.getBlock(x, y-1, z) instanceof BlockSupportColumn)
			negY = true;
		
		if(world.getBlock(x, y, z+1) instanceof BlockSupportColumn)
			posZ = true;
		if(world.getBlock(x, y, z-1) instanceof BlockSupportColumn)
			negZ = true;

		
		

		boolean solidForce = false;
		boolean posXSolid = solidForce, negXSolid = solidForce, posYSolid = solidForce, negYSolid = solidForce, posZSolid = solidForce, negZSolid = solidForce;

		
		if(posX || meta == 2 && world.isSideSolid(x+1, y, z, ForgeDirection.WEST, false))
			posXSolid = true;
		if(negX || meta == 2 && world.isSideSolid(x-1, y, z, ForgeDirection.EAST, false))
			negXSolid = true;
		
		if(posY || meta == 0 && world.isSideSolid(x, y+1, z, ForgeDirection.DOWN, false))
			posYSolid = true;
		if(negY || meta == 0 && world.isSideSolid(x, y-1, z, ForgeDirection.UP, false))
			negYSolid = true;
		
		if(posZ || meta == 1 && world.isSideSolid(x, y, z+1, ForgeDirection.NORTH, false))
			posZSolid = true;
		if(negZ || meta == 1 && world.isSideSolid(x, y, z-1, ForgeDirection.SOUTH, false))
			negZSolid = true;
		
		Tessellator.instance.addTranslation(x, y, z);
		Tessellator.instance.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
		Tessellator.instance.setColorOpaque_F(1F, 1F, 1F);
		
		IIcon icon = ( (BlockSupportColumn)block ).support;
		
		double o = D;
		boolean back = false;
		
		if(posX || meta == 2) { // east
			o = 1;
			renderFace( 1, D, d,   16, 3,
						D, D, d,   13, 3,
						D, D, D,   13, 13,
						1, D, D,   16, 13,
						0, 1, 0, icon, back); // top
			renderFace( 1, d, D,   16, 13,
						D, d, D,   13, 13,
						D, d, d,   13, 3,
						1, d, d,   16, 3,
						0, -1, 0, icon, back); // bottom
			
			renderFace( D, d, D,   13, 13,
						1, d, D,   16, 13,
						1, D, D,   16, 3,
						D, D, D,   13, 3,
						0, 0, 1, icon, back); // south
			
			renderFace( 1, d, d,   0, 13,
						D, d, d,   3, 13,
						D, D, d,   3, 3,
						1, D, d,   0, 3,
						0, 0, -1, icon, back); // north
			
		}
		if(!posXSolid && !posX)
			renderFace( o, d, D,   3,  13,
						o, d, d,   13, 13,
						o, D, d,   13, 3,
						o, D, D,   3,  3,
						1, 0, 0, icon, back);
		
		o = d;
		if(negX || meta == 2) { // west
			o = 0;
			
			renderFace( 0, D, D,   0, 13,
						d, D, D,   3, 13,
						d, D, d,   3, 3,
						0, D, d,   0, 3,
						0, 1, 0, icon, back); // top
			renderFace( 0, d, d,   0, 3,
						d, d, d,   3, 3,
						d, d, D,   3, 13,
						0, d, D,   0, 13,
						0, -1, 0, icon, back); // bottom
			
			renderFace( d, D, D,   3, 3,
						0, D, D,   0, 3,
						0, d, D,   0, 13,
						d, d, D,   3, 13,
						0, 0, 1, icon, back); // south
			
			renderFace( 0, D, d,   16, 3,
						d, D, d,   13, 3,
						d, d, d,   13, 13,
						0, d, d,   16, 13,
						0, 0, -1, icon, back); // north
			
		}
		if(!negXSolid && !negX)
			renderFace( o, d, d,   3,  13,
						o, d, D,   13, 13,
						o, D, D,   13, 3,
						o, D, d,   3,  3,
						-1, 0, 0, icon, back);
			
		o = D;
		if(posZ || meta == 1) { // south
			o = 1;
			
			renderFace( d, D, 1,   3, 16,
						D, D, 1,   13, 16,
						D, D, D,   13, 13,
						d, D, D,   3, 13,
						0, 1, 0, icon, back); // top
			renderFace( d, d, D,   3, 13,
						D, d, D,   13, 13,
						D, d, 1,   13, 16,
						d, d, 1,   3, 16,
						0, 1, 0, icon, back); // bottom
			
			renderFace( D, d, 1,   0, 13,
						D, d, D,   3, 13,
						D, D, D,   3, 3,
						D, D, 1,   0, 3,
						1, 0, 0, icon, back); // east
			renderFace( d, d, D,   13, 13,
						d, d, 1,   16, 13,
						d, D, 1,   16, 3,
						d, D, D,   13, 3,
						-1, 0, 0, icon, back); // west
			
		}
		if(!posZSolid && !posZ)
			renderFace( D, D, o,   3,  3,
						d, D, o,   13, 3,
						d, d, o,   13, 13,
						D, d, o,   3,  13,
						1, 0, 0, icon, back);
		
		o = d;
		if(negZ || meta == 1) {
			o = 0;
			
			renderFace( d, D, d,   3, 3,
						D, D, d,   13, 3,
						D, D, 0,   13, 0,
						d, D, 0,   3, 0,
						0, 1, 0, icon, back); // top
			renderFace( d, d, 0,   3, 0,
						D, d, 0,   13, 0,
						D, d, d,   13, 3,
						d, d, d,   3, 3,
						0, 1, 0, icon, back); // bottom
			
			renderFace( D, d, d,   13, 13,
						D, d, 0,   16, 13,
						D, D, 0,   16, 3,
						D, D, d,   13, 3,
						1, 0, 0, icon, back); // east
			
			renderFace( d, d, 0,   0, 13,
						d, d, d,   3, 13,
						d, D, d,   3, 3,
						d, D, 0,   0, 3,
						-1, 0, 0, icon, back); // west
			
		}
		if(!negZSolid && !negZ)
			renderFace( d, D, o,   3,  3,
						D, D, o,   13, 3,
						D, d, o,   13, 13,
						d, d, o,   3,  13,
						-1, 0, 0, icon, back);
		o = D;
		if(posY || meta == 0) { // top
			o = 1;
			
			renderFace( D, D, d,   3, 3,
						d, D, d,   13, 3,
						d, 1, d,   13, 0,
						D, 1, d,   3, 0,
						0, 0, -1, icon, back); // north
			renderFace( d, D, D,   3, 3,
						D, D, D,   13, 3,
						D, 1, D,   13, 0,
						d, 1, D,   3, 0,
						0, 0, -1, icon, back); // south
			
			renderFace( D, D, D,   3, 3,
						D, D, d,   13, 3,
						D, 1, d,   13, 0,
						D, 1, D,   3, 0,
						1, 0, 0, icon, back); // east
			renderFace( d, D, d,   3, 3,
						d, D, D,   13, 3,
						d, 1, D,   13, 0,
						d, 1, d,   3, 0,
						1, 0, 0, icon, back); // west
		}
		if(!posYSolid && !posY)
			renderFace( d, o, D,   3, 13,
						D, o, D,   13, 13,
						D, o, d,   13, 3,
						d, o, d,   3, 3,
						0, 1, 0, icon, back);
		
		o = d;
		if(negY || meta == 0) { // bottom
			o = 0;
			
			renderFace( D, 0, d,   3, 16,
						d, 0, d,   13, 16,
						d, d, d,   13, 13,
						D, d, d,   3, 13,
						0, 0, -1, icon, back); // north
			renderFace( d, 0, D,   3, 16,
						D, 0, D,   13, 16,
						D, d, D,   13, 13,
						d, d, D,   3, 13,
						0, 0, -1, icon, back); // south
			
			renderFace( D, 0, D,   3, 16,
						D, 0, d,   13, 16,
						D, d, d,   13, 13,
						D, d, D,   3, 13,
						1, 0, 0, icon, back); // east
			renderFace( d, 0, d,   3, 16,
						d, 0, D,   13, 16,
						d, d, D,   13, 13,
						d, d, d,   3, 13,
						1, 0, 0, icon, back); // west
		}
		if(!negYSolid && !negY)
			renderFace( d, o, d,   3, 3,
						D, o, d,   13, 3,
						D, o, D,   13, 13,
						d, o, D,   3, 13,
						0, -1, 0, icon, back);
		
		
		
		
		
		Tessellator.instance.addTranslation(-x, -y, -z);
		
		return true;
	}
	
	public void renderFace( double x1, double y1, double z1,
							int u1, int v1,
							double x2, double y2, double z2,
							int u2, int v2,
							double x3, double y3, double z3,
							int u3, int v3,
							double x4, double y4, double z4,
							int u4, int v4,
							float nX, float nY, float nZ,
							IIcon icon, boolean back) {
		Tessellator t = Tessellator.instance;
		
		t.setNormal(nX, nY, nZ);
		t.addVertexWithUV(x1, y1, z1, icon.getInterpolatedU(u1), icon.getInterpolatedV(v1));
		t.addVertexWithUV(x2, y2, z2, icon.getInterpolatedU(u2), icon.getInterpolatedV(v2));
		t.addVertexWithUV(x3, y3, z3, icon.getInterpolatedU(u3), icon.getInterpolatedV(v3));
		t.addVertexWithUV(x4, y4, z4, icon.getInterpolatedU(u4), icon.getInterpolatedV(v4));
		
		if(!back)
			return;
		
		t.setNormal(-nX, -nY, -nZ);
		t.addVertexWithUV(x4, y4, z4, icon.getInterpolatedU(u4), icon.getInterpolatedV(v4));
		t.addVertexWithUV(x3, y3, z3, icon.getInterpolatedU(u3), icon.getInterpolatedV(v3));
		t.addVertexWithUV(x2, y2, z2, icon.getInterpolatedU(u2), icon.getInterpolatedV(v2));
		t.addVertexWithUV(x1, y1, z1, icon.getInterpolatedU(u1), icon.getInterpolatedV(v1));

	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return typeID;
	}

}
