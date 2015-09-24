package com.thecodewarrior.catwalks.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.thecodewarrior.catwalks.block.BlockSupportColumn;
import com.thecodewarrior.codechicken.lib.vec.Vector3;

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
		
		Tessellator.instance.addTranslation(x, y, z);
		Tessellator.instance.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
		Tessellator.instance.setColorOpaque_F(0.5F, 0.5F, 0.5F);
		
		for(ForgeDirection d : ForgeDirection.VALID_DIRECTIONS) {
			renderExtension(block, world, x, y, z, d);
		}
		
		Tessellator.instance.addTranslation(-x, -y, -z);
		
		return true;
	}
	
	public void renderFace( double x1, double y1, double z1,
							double x2, double y2, double z2,
							double x3, double y3, double z3,
							double x4, double y4, double z4,
							float nX, float nY, float nZ,
							IIcon icon, int[][] corners, boolean back) {
		TransformingTessellator t = TransformingTessellator.instance;
		
		Vector3 normal = new Vector3(nX, nY, nZ);
		t.applyN(normal);
		nX = (float) normal.x;
		nY = (float) normal.y;
		nZ = (float) normal.z;
		
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
		t.addVertexWithUV(x1, y1, z1, icon.getInterpolatedU(corners[3][0]/2F), icon.getInterpolatedV(corners[3][1]/2F));
		t.addVertexWithUV(x2, y2, z2, icon.getInterpolatedU(corners[2][0]/2F), icon.getInterpolatedV(corners[2][1]/2F));
		t.addVertexWithUV(x3, y3, z3, icon.getInterpolatedU(corners[1][0]/2F), icon.getInterpolatedV(corners[1][1]/2F));
		t.addVertexWithUV(x4, y4, z4, icon.getInterpolatedU(corners[0][0]/2F), icon.getInterpolatedV(corners[0][1]/2F));
		
		if(!back)
			return;
		
		t.setNormal(-nX, -nY, -nZ);
		t.addVertexWithUV(x4, y4, z4, icon.getInterpolatedU(corners[0][0]/2F), icon.getInterpolatedV(corners[0][1]/2F));
		t.addVertexWithUV(x3, y3, z3, icon.getInterpolatedU(corners[1][0]/2F), icon.getInterpolatedV(corners[1][1]/2F));
		t.addVertexWithUV(x2, y2, z2, icon.getInterpolatedU(corners[2][0]/2F), icon.getInterpolatedV(corners[2][1]/2F));
		t.addVertexWithUV(x1, y1, z1, icon.getInterpolatedU(corners[3][0]/2F), icon.getInterpolatedV(corners[3][1]/2F));

	}
								// U, D, N, S, W, E
	public static int[] sideMap = {0, 0, 1, 1, 2, 2};
	
	public void renderExtension(Block block, IBlockAccess world, int x, int y, int z, ForgeDirection dir) {
		int meta = world.getBlockMetadata(x, y, z);
		int ax = x+dir.offsetX, ay = y+dir.offsetY, az = z+dir.offsetZ;
		Block adjacentBlock = world.getBlock(ax, ay, az);
		
		
		boolean shouldExtend = false;
		boolean isAdjacentSupport = false;
		boolean solid = false;
		
		if(meta == sideMap[dir.ordinal()]) {
			shouldExtend = true;
		}
		
		if(adjacentBlock instanceof BlockSupportColumn) {
			shouldExtend = true; isAdjacentSupport = true;
		} else if(shouldExtend && world.isSideSolid(ax, ay, az, dir.getOpposite() , false)) {
			solid = true;
		}
		
		
		TransformingTessellator t = TransformingTessellator.instance;
		t.pushMatrix();
		
		t.translate(0.5, 0.5, 0.5);
		switch(dir) {
		case UP: break;
		case DOWN:
			t.rotate(180, 1, 0, 0); break;
		case NORTH:
			t.rotate(-90, 1, 0, 0); t.rotate(-180, 0, 1, 0); break;
		case SOUTH:
			t.rotate(90, 1, 0, 0); break;
		case WEST:
			t.rotate(90, 0, 0, 1); t.rotate(-90, 0, 1, 0); break;
		case EAST:
			t.rotate(-90, 0, 0, 1); t.rotate(90, 0, 1, 0); break;
		default:
		}
		t.translate(-0.5,-0.5,-0.5);
		
		IIcon icon = ( (BlockSupportColumn)block ).support;
		
		// vertex coords
		double blockWidth = ( (BlockSupportColumn)block ).getWidth(); // width of block
		double d = (1-blockWidth)/2; // low inside coord
		double D = 1-d;              // high inside coord
		double smidge = 1/1024F; // faces will be offset by a smidge to prevent z-fighting. small enough to not be noticed, high enough to prevent z-fighting when the player is close
		double m = 0; // "zero", offset by a smidge for some sides to prevent z-fighting
		double M = 1; // "one", offset by a smidge for some sides to prevent z-fighting
		double o = D; // offset for the center faces, used so I only have to write one draw call for each. set for each side independantly.
		boolean back = false;
				
		// texture coords
		int _m = 0;      // minimum
		int _M = 32;     // maximum
		int _d  = 7;     // minimum u/v of center part
		int _D  = _M-_d; // maximum u/v of center part
		
		// [ clockwise end index ][ corner clockwise from "top-left" ][ u/v ]
		int [][][] sides = { // the u/v coords of each extension side, in clockwise order (top,right,bottom,left,center)
				{ // top
					{ _d, _m }, // top-left
					{ _D, _m }, // top-right
					{ _D, _d }, // bottom-right
					{ _d, _d }  // bottom-left
				},
				{ // right
					{ _M, _d }, // top-left
					{ _M, _D }, // top-right
					{ _D, _D }, // bottom-right
					{ _D, _d }  // bottom-left
				},
				{ // bottom
					{ _d, _M }, // top-left
					{ _D, _M }, // top-right
					{ _D, _D }, // bottom-right
					{ _d, _D }  // bottom-left
				},
				{ // left
					{ _m, _D }, // top-left
					{ _m, _d }, // top-right
					{ _d, _d }, // bottom-right
					{ _d, _D }  // bottom-left
				},
				{ // center
					{ _d, _d }, // top-left
					{ _D, _d }, // top-right
					{ _D, _D }, // bottom-right
					{ _d, _D }  // bottom-left
				}
		};
		
		if(solid) {
			m = smidge;
			M = 1-smidge;
		} else {
			m = 0;
			M = 1;
		}
		o = D;
		int side = 0;
		if(shouldExtend) { // top
			o = M;
			// sides
									
			switch(dir) { // get the correct side for the top/south when on top/north when on bottom
			case NORTH:
				side = 0; break; //
			case SOUTH:
				side = 2; break;
			case EAST:
				side = 1; break;
			case WEST:
				side = 3; break;
			case UP:
				side = 0; break;
			case DOWN:
				side = 2; break;
			}
			
			renderFace( D, D, d,
						d, D, d,
						d, M, d,
						D, M, d,
						0, 0,-1, icon, sides[side], back); // north (top)
			
			switch(dir) {
			case NORTH:
				side = 2; break;
			case SOUTH:
				side = 0; break;
			case EAST:
				side = 1; break;
			case WEST:
				side = 3; break;
			case UP:
				side = 0; break;
			case DOWN:
				side = 2; break;
			}
			
			renderFace( d, D, D,
						D, D, D,
						D, 1, D,
						d, 1, D,
						0, 0, 1, icon, sides[side], back); // south (bottom)
			
			if(dir.offsetY == 0) {
				side = 3;
			}
			
			renderFace( D, D, D,
						D, D, d,
						D, M, d,
						D, M, D,
						1, 0, 0, icon, sides[side], back); // east (left)
			
			if(dir.offsetY == 0) {
				side = 1;
			}
			renderFace( d, D, d,
						d, D, D,
						d, M, D,
						d, M, d,
					   -1, 0, 0, icon, sides[side], back); // west (right)
		}
		side = 4;
		if(!isAdjacentSupport)
			renderFace( d, o, D,
						D, o, D,
						D, o, d,
						d, o, d,
						0, 1, 0, icon, sides[side], back); // center
		
		t.popMatrix();
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
