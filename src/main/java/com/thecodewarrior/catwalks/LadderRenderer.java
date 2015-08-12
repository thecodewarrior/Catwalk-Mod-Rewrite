package com.thecodewarrior.catwalks;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.thecodewarrior.catwalks.BlockCagedLadder.RelativeSide;
import com.thecodewarrior.codechicken.lib.vec.BlockCoord;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class LadderRenderer implements ISimpleBlockRenderingHandler {

	public LadderRenderer() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId,
			RenderBlocks renderer) {
		int meta = 0;
//		block = CatwalkMod.ladderWestUnlit;
		
		double px = 1/16.0;
		renderer.overrideBlockBounds(px, 0, px, 1-px, 1, 1-px);
		Tessellator tessellator = Tessellator.instance;
	    GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
	    tessellator.startDrawingQuads();
	    
	    renderer.renderFromInside = true;
//	    renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 0, meta));
	    renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 1, meta));
	    renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 2, meta));
	    renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 3, meta));
	    renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 4, meta));
	    renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 5, meta));
	    
	    renderer.renderFromInside = false;
//	    renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 0, meta));
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
			Block _block, int modelId, RenderBlocks renderer) {
		BlockCagedLadder block = (BlockCagedLadder) _block;
		boolean lights = block.lights;
		
		
//		renderer.renderFromInside = true;
//		renderer.renderStandardBlock(block, x, y, z);
//		RenderBlocks renderer = new RenderBlocks(world);
//		renderer.setRenderFromInside(true);
//		renderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
		double px = 1/16.0;
		renderer.overrideBlockBounds(px, 0, px, 1-px, 1, 1-px);
		renderer.renderFromInside = true;
		renderer.renderStandardBlock(_block, x, y, z);
		renderer.renderFromInside = false;
		renderer.renderStandardBlock(_block, x, y, z);
		
		boolean force = false;
		int meta = world.getBlockMetadata(x, y, z);
		if(lights) {			
			boolean oldAO = renderer.enableAO;
			renderer.enableAO = false;
			
			Tessellator.instance.setBrightness(15728880);
			
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
			
			renderer.enableAO = oldAO;
		}
		
		renderer.unlockBlockBounds();
		
		Tessellator.instance.setBrightness(block.getMixedBrightnessForBlock(world, x, y - 1, z));
		Tessellator.instance.setColorOpaque_F(0.5F, 0.5F, 0.5F);
//		renderer.renderFaceXPos(block, x, y, z, block.transparent); // to set up lighting even when no faces are enabled
		
		ForgeDirection[] cardinal = {ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.WEST};
		for (int i = 0; i < cardinal.length; i++) {
			ForgeDirection side = cardinal[i];
			RelativeSide relSide = RelativeSide.FDtoRS(side, block.direction);
			if(block.isOpen(relSide, meta)) {
				drawWideLanding(block, world, x, y, z, side);
			}
			
		}
//		drawWideLanding(block, world, x, y, z, ForgeDirection.NORTH);

		return true;
	}
	
	public void drawWideLanding(BlockCagedLadder block, IBlockAccess world, int x, int y, int z, ForgeDirection side) {

		int meta = world.getBlockMetadata(x,y,z);
		
		if(!block.isOpen(side, meta))
			return;
		
		int adjX = x + side.offsetX;
		int adjY = y + side.offsetY;
		int adjZ = z + side.offsetZ;
		ForgeDirection diagDir1 = (side == ForgeDirection.NORTH || side == ForgeDirection.SOUTH) ? ForgeDirection.EAST : ForgeDirection.NORTH;
		ForgeDirection diagDir2 = diagDir1.getOpposite();
		
		Block adjacentBlock = world.getBlock(adjX,adjY,adjZ);
		Block diagBlock1 = world.getBlock(adjX+diagDir1.offsetX, adjY+diagDir1.offsetY, adjZ+diagDir1.offsetZ);
		Block diagBlock2 = world.getBlock(adjX+diagDir2.offsetX, adjY+diagDir2.offsetY, adjZ+diagDir2.offsetZ);
		
		if(!( adjacentBlock instanceof ICagedLadderConnectable || diagBlock1 instanceof ICagedLadderConnectable || diagBlock2 instanceof ICagedLadderConnectable ))
			return;
		
		boolean isThin = false;
		boolean bottom = false;

		if(adjacentBlock instanceof ICagedLadderConnectable) {
			ICagedLadderConnectable iclc = (ICagedLadderConnectable)adjacentBlock;
			if(!( iclc.shouldConnectToSide(world, adjX, adjY, adjZ, side.getOpposite()) ))
				return;
			isThin = iclc.isThin(world, adjX, adjY, adjZ, side.getOpposite());
			bottom = iclc.shouldHaveBottom(world, adjX, adjY, adjZ, side.getOpposite());
		}
		
				
		Tessellator tess = Tessellator.instance;
		
		float p = 1/16F;
		float P = 1 - p;
		
		IIcon c = block.landing;//( (BlockCatwalk) CatwalkMod.catwalkUnlitBottom ).sideTexture;
		
		float u = c.getMinU();
		float v = c.getMinV();
		float U = c.getMaxU();
		float V = c.getMaxV();
		
		float u1 = c.getInterpolatedU(1);
		float u2 = c.getInterpolatedU(2);

		float v1 = c.getInterpolatedV(1);
		
		tess.addTranslation(x, y, z);
				
		float rot = 0;
		switch(side) {
		case NORTH:
			break;
		case SOUTH:
			rot = 180F; break;
		case EAST:
			rot =  90F; break;
		case WEST:
			rot = -90F; break;
		default:
		}
		
		float g = (isThin && side.offsetZ != 0) ? p : 0;
		float h = (isThin && side.offsetX != 0) ? p : 0;
		float G = 1-g;
		float H = 1-h;
		
		
		
		if(shouldCornerShow(world, x, y, z, block, meta, side,
				ForgeDirection.NORTH, ForgeDirection.WEST))
			drawBothSides(tess,
					g, 0, h, u,  V,
					g, 1, h, u,  v,
					p, 1, p, u1, v,
					p, 0, p, u1, V);
		
		if(shouldCornerShow(world, x, y, z, block, meta, side,
				ForgeDirection.NORTH, ForgeDirection.EAST))
			drawBothSides(tess,
					G, 0, h, u,  V,
					G, 1, h, u,  v,
					P, 1, p, u1, v,
					P, 0, p, u1, V
					);
		
		if(shouldCornerShow(world, x, y, z, block, meta, side,
				ForgeDirection.SOUTH, ForgeDirection.WEST))
			drawBothSides(tess,
					g, 0, H, u,  V,
					g, 1, H, u,  v,
					p, 1, P, u1, v,
					p, 0, P, u1, V
					);
		
		if(shouldCornerShow(world, x, y, z, block, meta, side,
				ForgeDirection.SOUTH, ForgeDirection.EAST))
			drawBothSides(tess,
					G, 0, H, u,  V,
					G, 1, H, u,  v,
					P, 1, P, u1, v,
					P, 0, P, u1, V
					);
		
		
		if(world.isSideSolid(x, y-1, z, ForgeDirection.UP, true))
			bottom = false;
		if(!((ICagedLadderConnectable)block).shouldHaveBottom(world, x, y, z, side)) {
			bottom = false;
		}
		
		if(side == ForgeDirection.NORTH && bottom) {
			drawBothSides(tess,
					p, 0, 0, u2, v,
					P, 0, 0, U,  v,
					P, 0, p, U,  v1,
					p, 0, p, u2, v1);
			if(!isThin) {
				drawBothSides(tess,
						0, 0, 0, u1, v,
						p, 0, 0, u2, v,
						p, 0, p, u2, v1,
						0, 0, 0, u1, v);
				
				drawBothSides(tess,
						1, 0, 0, u1, v,
						P, 0, 0, u2, v,
						P, 0, p, u2, v1,
						1, 0, 0, u1, v);
			}
		}
		
		if(side == ForgeDirection.SOUTH && bottom) {
			drawBothSides(tess,
					p, 0, 1, u2, v,
					P, 0, 1, U,  v,
					P, 0, P, U,  v1,
					p, 0, P, u2, v1);
			if(!isThin) {
				drawBothSides(tess,
						0, 0, 1, u1, v,
						p, 0, 1, u2, v,
						p, 0, P, u2, v1,
						0, 0, 1, u1, v);
				
				drawBothSides(tess,
						1, 0, 1, u1, v,
						P, 0, 1, u2, v,
						P, 0, P, u2, v1,
						1, 0, 1, u1, v);
			}
		}
		
		if(side == ForgeDirection.EAST && bottom) {
			drawBothSides(tess,
					1, 0, p, u2, v,
					1, 0, P, U,  v,
					P, 0, P, U,  v1,
					P, 0, p, u2, v1);
			if(!isThin) {
				drawBothSides(tess,
						1, 0, 0, u1, v,
						1, 0, p, u2, v,
						P, 0, p, u2, v1,
						1, 0, 0, u1, v);
				
				drawBothSides(tess,
						1, 0, 1, u1, v,
						1, 0, P, u2, v,
						P, 0, P, u2, v1,
						1, 0, 1, u1, v);
			}
		}
		
		if(side == ForgeDirection.WEST && bottom) {
			drawBothSides(tess,
					0, 0, P, u2, v,
					0, 0, p, U,  v,
					p, 0, p, U,  v1,
					p, 0, P, u2, v1);
			if(!isThin) {
				drawBothSides(tess,
						0, 0, 1, u1, v,
						0, 0, P, u2, v,
						p, 0, P, u2, v1,
						0, 0, 1, u1, v);
				
				drawBothSides(tess,
						0, 0, 0, u1, v,
						0, 0, p, u2, v,
						p, 0, p, u2, v1,
						0, 0, 0, u1, v);
			}
		}
		
		
		
		
		tess.addTranslation(-x, -y, -z);
		
	}
	
	public boolean shouldCornerShow(IBlockAccess w, int x, int y, int z,
								BlockCagedLadder block, int meta, ForgeDirection side, ForgeDirection _a, ForgeDirection _b) {
		if(side != _a && side != _b)
			return false;
		
		ForgeDirection a = (side == _a) ? _a : _b;
		ForgeDirection b = (side == _a) ? _b : _a;
		
		if(block.isOpen(b, meta))
			return false;
		
		Block tmp = w.getBlock(x+a.offsetX, y+a.offsetY, z+a.offsetZ);
		boolean hasWallToConnect = false;
		if(tmp instanceof ICagedLadderConnectable) {
			 hasWallToConnect = ((ICagedLadderConnectable)tmp).doesSideHaveWall(w, x+a.offsetX, y+a.offsetY, z+a.offsetZ, b);
		}
		
		if(!hasWallToConnect) {
			tmp = w.getBlock(x+a.offsetX+b.offsetX, y+a.offsetY+b.offsetY, z+a.offsetZ+b.offsetZ);

			if(tmp instanceof ICagedLadderConnectable) {
				 hasWallToConnect = (
						  ((ICagedLadderConnectable)tmp).doesSideHaveWall(w, x+a.offsetX+b.offsetX, y+a.offsetY+b.offsetY, z+a.offsetZ+b.offsetZ, a.getOpposite()) &&
						 !((ICagedLadderConnectable)tmp).doesSideHaveWall(w, x+a.offsetX+b.offsetX, y+a.offsetY+b.offsetY, z+a.offsetZ+b.offsetZ, b.getOpposite())
						 );
			}
		}
		if(!hasWallToConnect) {
			return false;
		}
		
		return true;
	}
	
	public void drawBothSides(Tessellator tess,
			double x1, double y1, double z1, double u1, double v1,
			double x2, double y2, double z2, double u2, double v2,
			double x3, double y3, double z3, double u3, double v3,
			double x4, double y4, double z4, double u4, double v4) {
		
		tess.addVertexWithUV(x1, y1, z1, u1, v1);
		tess.addVertexWithUV(x2, y2, z2, u2, v2);
		tess.addVertexWithUV(x3, y3, z3, u3, v3);
		tess.addVertexWithUV(x4, y4, z4, u4, v4);
		
		tess.addVertexWithUV(x4, y4, z4, u4, v4);
		tess.addVertexWithUV(x3, y3, z3, u3, v3);
		tess.addVertexWithUV(x2, y2, z2, u2, v2);
		tess.addVertexWithUV(x1, y1, z1, u1, v1);
		
	}
	
	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return CatwalkMod.ladderRenderType;
	}

}
