package com.thecodewarrior.catwalks;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
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
		BlockCagedLadder block = (BlockCagedLadder) _block;
		boolean lights = block.lights;
		
		if(block.direction == ForgeDirection.EAST || block.direction == ForgeDirection.WEST) {
			renderer.uvRotateBottom = 1;
		}
		
//		renderer.renderFromInside = true;
//		renderer.renderStandardBlock(block, x, y, z);
//		RenderBlocks renderer = new RenderBlocks(world);
//		renderer.setRenderFromInside(true);
//		renderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
		double px = 1/16.0;
		renderer.overrideBlockBounds(px, 0, px, 1-px, 1, 1-px);
		
		renderer.flipTexture = true;
		renderer.renderFromInside = true;
		renderer.renderStandardBlock(_block, x, y, z);
		
		renderer.flipTexture = false;
		renderer.renderFromInside = false;
		renderer.renderStandardBlock(_block, x, y, z);
		
		boolean force = false; // force all light faces to be drawn, testing purposes only
		int meta = world.getBlockMetadata(x, y, z);
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
		renderer.uvRotateBottom = 0;
		renderer.unlockBlockBounds(); // don't force the block bounds anymore, we don't want stairs being the shape of ladders now do we?
		
		
		// Do this so the landing is the correct darkness when none of the block faces are being drawn.
		Tessellator.instance.setBrightness(block.getMixedBrightnessForBlock(world, x, y - 1, z));
		Tessellator.instance.setColorOpaque_F(0.5F, 0.5F, 0.5F);
		
		ForgeDirection[] cardinal = {ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.WEST};
		
		// for each cardinal direction
		for (int i = 0; i < cardinal.length; i++) {	
			ForgeDirection side = cardinal[i];
			RelativeSide relSide = RelativeSide.FDtoRS(side, block.direction);

			if(block.isOpen(relSide, meta)) {
				drawWideLanding(block, world, x, y, z, side);
			}
			
		}
		return true; // we ended up drawing something, so this 16x16x16 chunk needs to be drawn
	}
	
	public void drawWideLanding(BlockCagedLadder block, IBlockAccess world, int x, int y, int z, ForgeDirection side) {

		Tessellator tess = Tessellator.instance;
		
		int meta = world.getBlockMetadata(x,y,z);
	
		// this is handled before we're called
//		// don't even bother if our own side is closed
//		if(!block.isOpen(side, meta))
//			return;
		
		int adjX = x + side.offsetX;
		int adjY = y + side.offsetY;
		int adjZ = z + side.offsetZ;
		
		// get one of the perpendicular directions
		ForgeDirection diagDir1 = (side == ForgeDirection.NORTH || side == ForgeDirection.SOUTH) ? ForgeDirection.EAST : ForgeDirection.NORTH;
		ForgeDirection diagDir2 = diagDir1.getOpposite();
		
		Block adjacentBlock = world.getBlock(adjX,adjY,adjZ);
		Block diagBlock1 = world.getBlock(adjX+diagDir1.offsetX, adjY+diagDir1.offsetY, adjZ+diagDir1.offsetZ);
		Block diagBlock2 = world.getBlock(adjX+diagDir2.offsetX, adjY+diagDir2.offsetY, adjZ+diagDir2.offsetZ);
		
		// are none of the blocks I could connect to ICLCs?
//		if(!( adjacentBlock instanceof ICagedLadderConnectable || diagBlock1 instanceof ICagedLadderConnectable || diagBlock2 instanceof ICagedLadderConnectable ))
//			return;
		
		boolean isThin = false;
		boolean bottom = false;

		if(adjacentBlock instanceof ICagedLadderConnectable) { // we're next to an ICagedLadderConnectable? Great!
			ICagedLadderConnectable iclc = (ICagedLadderConnectable)adjacentBlock; // gimme an ICLC!
			if(!( iclc.shouldConnectToSide(world, adjX, adjY, adjZ, side.getOpposite()) )) // you don't want me to connect? Ok.
				return;
			isThin = iclc.isThin(world, adjX, adjY, adjZ, side.getOpposite()); // are you a thin guy?
			bottom = iclc.shouldHaveBottom(world, adjX, adjY, adjZ, side.getOpposite()); // should I display the bottom of the landing?
		} else {
			if(!world.isSideSolid(adjX, adjY, adjZ, side.getOpposite(), false) &&
				world.isSideSolid(adjX, adjY-1, adjZ, ForgeDirection.UP, false)) { // am I next to a surface?
				bottom = true;
			}
		}
		
		// is the top of the block below this one solid? if so, we don't want any z-fighting
		if(world.isSideSolid(x, y-1, z, ForgeDirection.UP, true))
			bottom = false;
		// if we don't want a bottom ourself we shouldn't have one
		if(!((ICagedLadderConnectable)block).shouldHaveBottom(world, x, y, z, side))
			bottom = false;
		
		float p = 1/16F;
		// all the capitals are 1-lowercase
		float P = 1 - p;
		
		IIcon c = block.tape ? block.landing_tape : block.landing;//( (BlockCatwalk) CatwalkMod.catwalkUnlitBottom ).sideTexture;
		
		float u = c.getMinU();
		float v = c.getMinV();
		// except these, they're the max values
		float U = c.getMaxU();
		float V = c.getMaxV();
		
		float u1 = c.getInterpolatedU(1);
		float u2 = c.getInterpolatedU(2);

		float v1 = c.getInterpolatedV(1);
		
		tess.addTranslation(x, y, z);
		
		
		// 0 if the landing sides should be flush to the sides of the block
		// side of block => __/ `- 0
		// p if the landing sides should be flush to the ladder
		// side of block => ___ ]<-p
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
		
		// start drawing the bottom of the landing
		
		if(side == ForgeDirection.NORTH && bottom) {
			drawBothSides(tess,
					p, 0, 0, u2, v,
					P, 0, 0, U,  v,
					P, 0, p, U,  v1,
					p, 0, p, u2, v1);
			// draw the corners
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
			// draw the corners
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
			// draw the corners
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
			// draw the corners
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
			// is the side of the adjacent block closed? then by all means connect!
			hasWallToConnect = ((ICagedLadderConnectable)tmp).doesSideHaveWall(w, x+a.offsetX, y+a.offsetY, z+a.offsetZ, b);
		}
		
		tmp = w.getBlock(x+b.offsetX, y+b.offsetY, z+b.offsetZ);
		if(tmp instanceof ICagedLadderConnectable) {
			ICagedLadderConnectable iclc = (ICagedLadderConnectable)tmp;
			hasWallToConnect = hasWallToConnect || (
					!iclc.doesSideHaveWall(w, x+b.offsetX, y+b.offsetY, z+b.offsetZ, a) &&
					 iclc.doesSideHaveWall(w, x+b.offsetX, y+b.offsetY, z+b.offsetZ, b.getOpposite())
					);
		}
		
		if(!hasWallToConnect) {
			tmp = w.getBlock(x+a.offsetX+b.offsetX, y+a.offsetY+b.offsetY, z+a.offsetZ+b.offsetZ);

			if(tmp instanceof ICagedLadderConnectable) {
				 hasWallToConnect = (
						 //   |_
						 // [=] (ladder)
						 // is the _ wall closed?
						  ((ICagedLadderConnectable)tmp).doesSideHaveWall(w, x+a.offsetX+b.offsetX, y+a.offsetY+b.offsetY, z+a.offsetZ+b.offsetZ, a.getOpposite()) &&
						 // is the | wall open?
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
