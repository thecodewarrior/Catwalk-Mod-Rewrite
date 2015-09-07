package com.thecodewarrior.catwalks.render;

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
	public void renderInventoryBlock(Block block, int metadata, int modelId,
			RenderBlocks renderer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		int meta = world.getBlockMetadata(x, y, z);
		double d = 3/16F;
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

		
		if(world.isSideSolid(x+1, y, z, ForgeDirection.WEST, false))
			posXSolid = true;
		if(world.isSideSolid(x-1, y, z, ForgeDirection.EAST, false))
			negXSolid = true;
		
		if(world.isSideSolid(x, y+1, z, ForgeDirection.DOWN, false))
			posYSolid = true;
		if(world.isSideSolid(x, y-1, z, ForgeDirection.UP, false))
			negYSolid = true;
		
		if(world.isSideSolid(x, y, z+1, ForgeDirection.NORTH, false))
			posZSolid = true;
		if(world.isSideSolid(x, y, z-1, ForgeDirection.SOUTH, false))
			negZSolid = true;
		
		Tessellator.instance.addTranslation(x, y, z);
		Tessellator.instance.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
		Tessellator.instance.setColorOpaque_F(1F, 1F, 1F);
		
		IIcon icon = ( (BlockSupportColumn)block ).support_cross;
		
		double o = D;
		
		if(posX || meta == 2) {
			o = 1;
			renderFace( 1, D, D,   16, 13,
						D, D, D,   13, 13,
						D, D, d,   13, 3,
						1, D, d,   16, 3,
						0, 1, 0, icon); // top
			renderFace( 1, d, D,   16, 13,
						D, d, D,   13, 13,
						D, d, d,   13, 3,
						1, d, d,   16, 3,
						0, -1, 0, icon); // bottom
			
			renderFace( D, D, D,   13, 3,
						1, D, D,   16, 3,
						1, d, D,   16, 13,
						D, d, D,   13, 13,
						0, 0, 1, icon); // north
			
			renderFace( 1, D, d,   0, 3,
						D, D, d,   3, 3,
						D, d, d,   3, 13,
						1, d, d,   0, 13,
						0, 0, -1, icon);
			
		}
		if(!posXSolid)
			renderFace( o, D, D,   3,  3,
						o, D, d,   13, 3,
						o, d, d,   13, 13,
						o, d, D,   3,  13,
						1, 0, 0, icon);
		
		o = d;
		if(negX || meta == 2) {
			o = 0;
			
			renderFace( 0, D, D,   0, 13,
						d, D, D,   3, 13,
						d, D, d,   3, 3,
						0, D, d,   0, 3,
						0, 1, 0, icon); // top
			renderFace( 0, d, D,   0, 13,
						d, d, D,   3, 13,
						d, d, d,   3, 3,
						0, d, d,   0, 3,
						0, -1, 0, icon); // bottom
			
			renderFace( d, D, D,   3, 3,
						0, D, D,   0, 3,
						0, d, D,   0, 13,
						d, d, D,   3, 13,
						0, 0, 1, icon); // north
			
			renderFace( 0, D, d,   16, 3,
						d, D, d,   13, 3,
						d, d, d,   13, 13,
						0, d, d,   16, 13,
						0, 0, -1, icon);
			
		}
		if(!negXSolid)
			renderFace( o, D, d,   3,  3,
						o, D, D,   13, 3,
						o, d, D,   13, 13,
						o, d, d,   3,  13,
						-1, 0, 0, icon);
			
		o = D;
		if(posZ || meta == 1) {
			o = 1;
			
			renderFace( d, D, D,   3, 13,
						D, D, D,   13, 13,
						D, D, 1,   13, 16,
						d, D, 1,   3, 16,
						0, 1, 0, icon);
			renderFace( d, d, D,   3, 13,
						D, d, D,   13, 13,
						D, d, 1,   13, 16,
						d, d, 1,   3, 16,
						0, 1, 0, icon);
			
			renderFace( D, D, 1,   0, 3,
						D, D, D,   3, 3,
						D, d, D,   3, 13,
						D, d, 1,   0, 13,
						1, 0, 0, icon);
			renderFace( d, D, D,   13, 3,
						d, D, 1,   16, 3,
						d, d, 1,   16, 13,
						d, d, D,   13, 13,
						-1, 0, 0, icon);
			
		}
		if(!posZSolid)
			renderFace( D, D, o,   3,  3,
						d, D, o,   13, 3,
						d, d, o,   13, 13,
						D, d, o,   3,  13,
						1, 0, 0, icon);
		
		o = d;
		if(negZ || meta == 1) {
			o = 0;
			
			renderFace( d, D, 0,   3, 0,
						D, D, 0,   13, 0,
						D, D, d,   13, 3,
						d, D, d,   3, 3,
						0, 1, 0, icon);
			
			renderFace( D, D, d,   13, 3,
						D, D, 0,   16, 3,
						D, d, 0,   16, 13,
						D, d, d,   13, 13,
						1, 0, 0, icon);
			
			renderFace( d, D, 0,   0, 3,
						d, D, d,   3, 3,
						d, d, d,   3, 13,
						d, d, 0,   0, 13,
						-1, 0, 0, icon);
			
		}
		if(!negZSolid)
			renderFace( d, D, o,   3,  3,
						D, D, o,   13, 3,
						D, d, o,   13, 13,
						d, d, o,   3,  13,
						-1, 0, 0, icon);
		o = D;
		if(posY || meta == 0) {
			o = 1;
			
			renderFace( D, 1, d,   3, 0,
						d, 1, d,   13, 0,
						d, D, d,   13, 3,
						D, D, d,   3, 3,
						0, 0, -1, icon); // north
			renderFace( d, 1, D,   3, 0,
						D, 1, D,   13, 0,
						D, D, D,   13, 3,
						d, D, D,   3, 3,
						0, 0, -1, icon); // south
			
			renderFace( D, 1, D,   3, 0,
						D, 1, d,   13, 0,
						D, D, d,   13, 3,
						D, D, D,   3, 3,
						1, 0, 0, icon); // east
			renderFace( d, 1, d,   3, 0,
						d, 1, D,   13, 0,
						d, D, D,   13, 3,
						d, D, d,   3, 3,
						1, 0, 0, icon); // west
		}
		if(!posYSolid)
			renderFace( d, o, d,   3, 3,
						D, o, d,   13, 3,
						D, o, D,   13, 13,
						d, o, D,   3, 13,
						0, 1, 0, icon);
		
		o = d;
		if(negY || meta == 0) {
			o = 0;
			
			renderFace( D, 0, d,   3, 16,
						d, 0, d,   13, 16,
						d, d, d,   13, 13,
						D, d, d,   3, 13,
						0, 0, -1, icon); // north
			renderFace( d, 0, D,   3, 16,
						D, 0, D,   13, 16,
						D, d, D,   13, 13,
						d, d, D,   3, 13,
						0, 0, -1, icon); // south
			
			renderFace( D, 0, D,   3, 16,
						D, 0, d,   13, 16,
						D, d, d,   13, 13,
						D, d, D,   3, 13,
						1, 0, 0, icon); // east
			renderFace( d, 0, d,   3, 16,
						d, 0, D,   13, 16,
						d, d, D,   13, 13,
						d, d, d,   3, 13,
						1, 0, 0, icon); // west
		}
		if(!negYSolid)
			renderFace( d, o, D,   3, 13,
						D, o, D,   13, 13,
						D, o, d,   13, 3,
						d, o, d,   3, 3,
						0, -1, 0, icon);
		
		
		
		
		
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
							IIcon icon) {
		Tessellator t = Tessellator.instance;
		
		t.setNormal(nX, nY, nZ);
		t.addVertexWithUV(x1, y1, z1, icon.getInterpolatedU(u1), icon.getInterpolatedV(v1));
		t.addVertexWithUV(x2, y2, z2, icon.getInterpolatedU(u2), icon.getInterpolatedV(v2));
		t.addVertexWithUV(x3, y3, z3, icon.getInterpolatedU(u3), icon.getInterpolatedV(v3));
		t.addVertexWithUV(x4, y4, z4, icon.getInterpolatedU(u4), icon.getInterpolatedV(v4));
		
		t.setNormal(-nX, -nY, -nZ);
		t.addVertexWithUV(x4, y4, z4, icon.getInterpolatedU(u4), icon.getInterpolatedV(v4));
		t.addVertexWithUV(x3, y3, z3, icon.getInterpolatedU(u3), icon.getInterpolatedV(v3));
		t.addVertexWithUV(x2, y2, z2, icon.getInterpolatedU(u2), icon.getInterpolatedV(v2));
		t.addVertexWithUV(x1, y1, z1, icon.getInterpolatedU(u1), icon.getInterpolatedV(v1));

	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getRenderId() {
		// TODO Auto-generated method stub
		return typeID;
	}

}
