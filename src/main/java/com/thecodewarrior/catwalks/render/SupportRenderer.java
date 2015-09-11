package com.thecodewarrior.catwalks.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

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
		
		boolean force = false; // used to test by setting all sides to extend
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

		
		
		
		boolean solidForce = false; // used to test by setting solid to true for all sides
		boolean posXSolid = solidForce, negXSolid = solidForce, posYSolid = solidForce, negYSolid = solidForce, posZSolid = solidForce, negZSolid = solidForce;
			// stores whether any of the sides are solid, doesn't get set if the particular side isn't going to be in contact with the block
		
		if((posX || meta == 2) && world.isSideSolid(x+1, y, z, ForgeDirection.WEST , false) && !(world.getBlock(x+1, y, z) instanceof BlockSupportColumn))
			posXSolid = true;
		if((negX || meta == 2) && world.isSideSolid(x-1, y, z, ForgeDirection.EAST , false) && !(world.getBlock(x-1, y, z) instanceof BlockSupportColumn))
			negXSolid = true;
		
		if((posY || meta == 0) && world.isSideSolid(x, y+1, z, ForgeDirection.DOWN , false) && !(world.getBlock(x, y+1, z) instanceof BlockSupportColumn))
			posYSolid = true;
		if((negY || meta == 0) && world.isSideSolid(x, y-1, z, ForgeDirection.UP   , false) && !(world.getBlock(x, y-1, z) instanceof BlockSupportColumn))
			negYSolid = true;
		
		if((posZ || meta == 1) && world.isSideSolid(x, y, z+1, ForgeDirection.NORTH, false) && !(world.getBlock(x, y, z+1) instanceof BlockSupportColumn))
			posZSolid = true;
		if((negZ || meta == 1) && world.isSideSolid(x, y, z-1, ForgeDirection.SOUTH, false) && !(world.getBlock(x, y, z-1) instanceof BlockSupportColumn))
			negZSolid = true;
		
		Tessellator.instance.addTranslation(x, y, z);
		Tessellator.instance.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
		Tessellator.instance.setColorOpaque_F(0.5F, 0.5F, 0.5F);

		
		IIcon icon = ( (BlockSupportColumn)block ).support;
		
		// vertex coords
		double blockWidth = ( (BlockSupportColumn)block ).getWidth(); // width of block
		double d = (1-blockWidth)/2; // low inside coord
		double D = 1-d;              // high inside coord
		double smidge = 1/1024F; // faces will be offset by a smidge to prevent z-fighting. small enough to not be noticed, high enough to prevent z-fighting when the player is close
		double m = 0; // "zero", offset by a smidge for some sides to prevent z-fighting
		double M = 1; // "one", offset by a smidge for some sides to prevent z-fighting
		double o = D; // offset for the center faces, used so I only have to write one draw call for each. set for each side independantly.
		boolean back = false; // whether to draw the backs of the faces
		
		// texture coords
		int _m = 0;      // minimum
		int _M = 32;     // maximum
		int _d  = 7;     // minimum u/v of center part
		int _D  = _M-_d; // maximum u/v of center part
		
		
		if(posXSolid) { // if the side is solid we need to offset by a smidge.
			m = smidge;
			M = 1-smidge;
		} else { // if it isn't we should reset them.
			m = 0;
			M = 1;
		}
		o = D;
		if(posX || meta == 2) { // east
			o = M;
			renderFace( M, D, d,   _M, _d,
						D, D, d,   _D, _d,
						D, D, D,   _D, _D,
						M, D, D,   _M, _D,
						0, 1, 0, icon, back); // top
			renderFace( M, d, D,   _M, _D,
						D, d, D,   _D, _D,
						D, d, d,   _D, _d,
						M, d, d,   _M, _d,
						0, -1, 0, icon, back); // bottom
			
			renderFace( D, d, D,   _D, _D,
						M, d, D,   _M, _D,
						M, D, D,   _M, _d,
						D, D, D,   _D, _d,
						0, 0, 1, icon, back); // south
			
			renderFace( M, d, d,   _m, _D,
						D, d, d,   _d, _D,
						D, D, d,   _d, _d,
						M, D, d,   _m, _d,
						0, 0, -1, icon, back); // north
			
		}
		if(!(world.getBlock(x+1, y, z) instanceof BlockSupportColumn))
			renderFace( o, d, D,   _d, _D,
						o, d, d,   _D, _D,
						o, D, d,   _D, _d,
						o, D, D,   _d, _d,
						1, 0, 0, icon, back); // center
		
		if(negXSolid) {
			m = smidge;
			M = 1-smidge;
		} else {
			m = 0;
			M = 1;
		}
		o = d;
		if(negX || meta == 2) { // west
			o = m;
			
			renderFace( m, D, D,   _m, _D,
						d, D, D,   _d, _D,
						d, D, d,   _d, _d,
						m, D, d,   _m, _d,
						0, 1, 0, icon, back); // top
			renderFace( m, d, d,   _m, _d,
						d, d, d,   _d, _d,
						d, d, D,   _d, _D,
						m, d, D,   _m, _D,
						0, -1, 0, icon, back); // bottom
			
			renderFace( d, D, D,   _d, _d,
						m, D, D,   _m, _d,
						m, d, D,   _m, _D,
						d, d, D,   _d, _D,
						0, 0, 1, icon, back); // south
			
			renderFace( m, D, d,   _M, _d,
						d, D, d,   _D, _d,
						d, d, d,   _D, _D,
						m, d, d,   _M, _D,
						0, 0, -1, icon, back); // north
			
		}
		if(!(world.getBlock(x-1, y, z) instanceof BlockSupportColumn))
			renderFace( o, d, d,   _d, _D,
						o, d, D,   _D, _D,
						o, D, D,   _D, _d,
						o, D, d,   _d, _d,
						-1, 0, 0, icon, back); // center
			
		if(posZSolid) {
			m = smidge;
			M = 1-smidge;
		} else {
			m = 0;
			M = 1;
		}
		o = D;
		if(posZ || meta == 1) { // south
			o = M;
			
			renderFace( d, D, M,   _d, _M,
						D, D, M,   _D, _M,
						D, D, D,   _D,  _D,
						d, D, D,   _d,  _D,
						0, 1, 0, icon, back); // top
			renderFace( d, d, D,   _d,  _D,
						D, d, D,   _D,  _D,
						D, d, M,   _D, _M,
						d, d, M,   _d, _M,
						0, 1, 0, icon, back); // bottom
			
			renderFace( D, d, M,   _m, _D,
						D, d, D,   _d, _D,
						D, D, D,   _d, _d,
						D, D, M,   _m, _d,
						1, 0, 0, icon, back); // east
			renderFace( d, d, D,   _D, _D,
						d, d, M,   _M, _D,
						d, D, M,   _M, _d,
						d, D, D,   _D, _d,
						-1, 0, 0, icon, back); // west
			
		}
		if(!(world.getBlock(x, y, z+1) instanceof BlockSupportColumn))
			renderFace( D, D, o,   _d, _d,
						d, D, o,   _D, _d,
						d, d, o,   _D, _D,
						D, d, o,   _d, _D,
						0, 0, 1, icon, back); // center
		
		if(negZSolid) {
			m = smidge;
			M = 1-smidge;
		} else {
			m = 0;
			M = 1;
		}
		o = d;
		if(negZ || meta == 1) {
			o = m;
			
			renderFace( d, D, d,   _d, _d,
						D, D, d,   _D, _d,
						D, D, m,   _D, _m,
						d, D, m,   _d, _m,
						0, 1, 0, icon, back); // top
			renderFace( d, d, m,   _d, _m,
						D, d, m,   _D, _m,
						D, d, d,   _D, _d,
						d, d, d,   _d, _d,
						0, 1, 0, icon, back); // bottom
			
			renderFace( D, d, d,   _D, _D,
						D, d, m,   _M, _D,
						D, D, m,   _M, _d,
						D, D, d,   _D, _d,
						1, 0, 0, icon, back); // east
			
			renderFace( d, d, m,   _m, _D,
						d, d, d,   _d, _D,
						d, D, d,   _d, _d,
						d, D, m,   _m, _d,
						0, 0, -1, icon, back); // west
			
		}
		if(!(world.getBlock(x, y, z-1) instanceof BlockSupportColumn))
			renderFace( d, D, o,   _d, _d,
						D, D, o,   _D, _d,
						D, d, o,   _D, _D,
						d, d, o,   _d, _D,
						0, 0, -1, icon, back); // center
		
		if(posYSolid) {
			m = smidge;
			M = 1-smidge;
		} else {
			m = 0;
			M = 1;
		}
		o = D;
		if(posY || meta == 0) { // top
			o = M;
			
			renderFace( D, D, d,   _d, _d,
						d, D, d,   _D, _d,
						d, M, d,   _D, _m,
						D, M, d,   _d, _m,
						0, 0, -1, icon, back); // north
			renderFace( d, D, D,   _d, _d,
						D, D, D,   _D, _d,
						D, 1, D,   _D, _m,
						d, 1, D,   _d, _m,
						0, 0, -1, icon, back); // south
			
			renderFace( D, D, D,   _d, _d,
						D, D, d,   _D, _d,
						D, M, d,   _D, _m,
						D, M, D,   _d, _m,
						1, 0, 0, icon, back); // east
			renderFace( d, D, d,   _d, _d,
						d, D, D,   _D, _d,
						d, M, D,   _D, _m,
						d, M, d,   _d, _m,
						1, 0, 0, icon, back); // west
		}
		if(!(world.getBlock(x, y+1, z) instanceof BlockSupportColumn))
			renderFace( d, o, D,   _d, _D,
						D, o, D,   _D, _D,
						D, o, d,   _D, _d,
						d, o, d,   _d, _d,
						0, 1, 0, icon, back); // center
		
		if(negYSolid) {
			m = smidge;
			M = 1-smidge;
		} else {
			m = 0;
			M = 1;
		}
		o = d;
		if(negY || meta == 0) { // bottom
			o = m;
			
			renderFace( D, m, d,   _d, _M,
						d, m, d,   _D, _M,
						d, d, d,   _D, _D,
						D, d, d,   _d, _D,
						0, 0, -1, icon, back); // north
			renderFace( d, m, D,   _d, _M,
						D, m, D,   _D, _M,
						D, d, D,   _D, _D,
						d, d, D,   _d, _D,
						0, 0, -1, icon, back); // south
			
			renderFace( D, m, D,   _d, _M,
						D, m, d,   _D, _M,
						D, d, d,   _D, _D,
						D, d, D,   _d, _D,
						1, 0, 0, icon, back); // east
			renderFace( d, m, d,   _d, _M,
						d, m, D,   _D, _M,
						d, d, D,   _D, _D,
						d, d, d,   _d, _D,
						-1, 0, 0, icon, back); // west
		}
		if(!(world.getBlock(x, y-1, z) instanceof BlockSupportColumn))
			renderFace( d, o, d,   _d, _d,
						D, o, d,   _D, _d,
						D, o, D,   _D, _D,
						d, o, D,   _d, _D,
						0, -1, 0, icon, back); // center
		
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
		
		float f3 = 0.5F;
        float f4 = 1.0F;
        float f5 = 0.8F;
        float f6 = 0.6F;
        float f7 = f4;
        float f8 = f4;
        float f9 = f4;
        float f10 = f3;
        float f11 = f5;
        float f12 = f6;
        float f13 = f3;
        float f14 = f5;
        float f15 = f6;
        float f16 = f3;
        float f17 = f5;
        float f18 = f6;
		
		if(nX ==  1) {
            t.setColorOpaque_F(f12, f15, f18);
		} else
		if(nX == -1) {
            t.setColorOpaque_F(f12, f15, f18);
		} else
			
		if(nY ==  1) {
            t.setColorOpaque_F(f7, f8, f9);
		} else
		if(nY == -1) {
            t.setColorOpaque_F(f10, f13, f16);
		} else
		
		if(nZ ==  1) {
            t.setColorOpaque_F(f11, f14, f17);
		} else
		if(nZ == -1) {
            t.setColorOpaque_F(f11, f14, f17);
		}
		
		t.setNormal(nX, nY, nZ);
		t.addVertexWithUV(x1, y1, z1, icon.getInterpolatedU(u1/2F), icon.getInterpolatedV(v1/2F));
		t.addVertexWithUV(x2, y2, z2, icon.getInterpolatedU(u2/2F), icon.getInterpolatedV(v2/2F));
		t.addVertexWithUV(x3, y3, z3, icon.getInterpolatedU(u3/2F), icon.getInterpolatedV(v3/2F));
		t.addVertexWithUV(x4, y4, z4, icon.getInterpolatedU(u4/2F), icon.getInterpolatedV(v4/2F));
		
		if(!back)
			return;
		
		t.setNormal(-nX, -nY, -nZ);
		t.addVertexWithUV(x4, y4, z4, icon.getInterpolatedU(u4/2F), icon.getInterpolatedV(v4/2F));
		t.addVertexWithUV(x3, y3, z3, icon.getInterpolatedU(u3/2F), icon.getInterpolatedV(v3/2F));
		t.addVertexWithUV(x2, y2, z2, icon.getInterpolatedU(u2/2F), icon.getInterpolatedV(v2/2F));
		t.addVertexWithUV(x1, y1, z1, icon.getInterpolatedU(u1/2F), icon.getInterpolatedV(v1/2F));

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
