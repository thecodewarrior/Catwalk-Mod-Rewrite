package com.thecodewarrior.catwalks.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.thecodewarrior.catwalks.block.BlockCagedLadder;
import com.thecodewarrior.catwalks.block.BlockCatwalk;
import com.thecodewarrior.catwalks.block.BlockSupportColumn;
import com.thecodewarrior.catwalks.util.CatwalkUtil;
import com.thecodewarrior.catwalks.util.Predicate;
import com.thecodewarrior.codechicken.lib.vec.BlockCoord;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockSupportColumn extends ItemBlock {

	public ItemBlockSupportColumn(Block b) {
		super(b);
	}
	
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int _side, float hitX, float hitY, float hitZ)
    {
        Block block = world.getBlock(x, y, z);
        ForgeDirection side = ForgeDirection.getOrientation(_side);
        
        if(( block instanceof BlockCatwalk || block instanceof BlockCagedLadder ) && world.getBlock(x+side.offsetX, y+side.offsetY, z+side.offsetZ) instanceof BlockSupportColumn) {
        	x += side.offsetX;
        	y += side.offsetY;
        	z += side.offsetZ;
        	hitX -= side.offsetX;
        	hitY -= side.offsetY;
        	hitZ -= side.offsetZ;
        	
        	_side = side.getOpposite().ordinal();
        	block = world.getBlock(x,y,z);
        }
        
        return CatwalkUtil.extendBlock(stack, player, world, x, y, z, _side, hitX, hitY, hitZ, this,
        new Predicate<Block>() { public boolean test(Block block) { /*---*/ return block instanceof BlockSupportColumn; /*---*/ } },
        new Predicate<BlockCoord>(world) {
			@Override
			public boolean test(BlockCoord coord) {
				World world = (World)args[0];
				return world.getBlock(coord.x, coord.y, coord.z) instanceof BlockSupportColumn;
			}
    	},
    	new Predicate<BlockCoord>() { public boolean test(BlockCoord coord) { /*---*/ return false; /*---*/ } });
        
        
        /*
        int meta = world.getBlockMetadata(x, y, z);
        
        boolean isExtending = false;
        boolean placeSucceeded = false;
        boolean didHitAnother = false;

        int oldX = x;
        int oldY = y;
        int oldZ = z;
        
        if (block instanceof BlockSupportColumn && player.isSneaking() )//&& isOnCorrectSide)
        {
    		isExtending = true;
    		
        	ForgeDirection dir = ForgeDirection.getOrientation(_side).getOpposite();
        	Predicate<BlockCoord> pred = new Predicate<BlockCoord>(world) {
				@Override
				public boolean test(BlockCoord coord) {
					World world = (World)args[0];
					return world.getBlock(coord.x, coord.y, coord.z) instanceof BlockSupportColumn;
				}
        	};
        	BlockHit coord = CatwalkUtil.getExtendCoord(world, x, y, z, dir, pred);
        	x = coord.x;
        	y = coord.y;
        	z = coord.z;
        	_side = coord.side;
        	if(_side == dir.ordinal())
        		isExtending = true;
        	didHitAnother = pred.test( new BlockCoord(x + (2*dir.offsetX), y + (2*dir.offsetY), z + (2*dir.offsetZ)) );
        }
        
        if(_side == ForgeDirection.UNKNOWN.ordinal()) {
        	// extension errored out, don't try to place.
        }
        else if (block == Blocks.snow_layer && (world.getBlockMetadata(x, y, z) & 7) < 1)
        {
            _side = 1;
        }
        else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush && !block.isReplaceable(world, x, y, z))
        {
            if (_side == 0)
            {
                --y;
            }

            if (_side == 1)
            {
                ++y;
            }

            if (_side == 2)
            {
                --z;
            }

            if (_side == 3)
            {
                ++z;
            }

            if (_side == 4)
            {
                --x;
            }

            if (_side == 5)
            {
                ++x;
            }
        }

        if (stack.stackSize == 0)
        {
            placeSucceeded = false;
        }
        else if (!player.canPlayerEdit(x, y, z, _side, stack))
        {
            placeSucceeded = false;
        }
        else if (y == 255 && this.field_150939_a.getMaterial().isSolid())
        {
            placeSucceeded = false;
        }
        else if (world.canPlaceEntityOnSide(this.field_150939_a, x, y, z, false, _side, player, stack))
        {
            int stackMeta = this.getMetadata(stack.getItemDamage());
            int newMeta = this.field_150939_a.onBlockPlaced(world, x, y, z, _side, hitX, hitY, hitZ, stackMeta);

            if (placeBlockAt(stack, player, world, x, y, z, _side, hitX, hitY, hitZ, newMeta))
            {
                world.playSoundEffect((double)((float)oldX + 0.5F), (double)((float)oldY + 0.5F), (double)((float)oldZ + 0.5F), this.field_150939_a.stepSound.func_150496_b(), (this.field_150939_a.stepSound.getVolume() + 1.0F) / 2.0F, this.field_150939_a.stepSound.getPitch() * 0.8F);
                --stack.stackSize;
            }

            placeSucceeded = true;
        }
        else
        {
            placeSucceeded = false;
        }
        
        if(isExtending)
        	CatwalkUtil.extendParticles(placeSucceeded, didHitAnother, world, oldX, oldY, oldZ, hitX, hitY, hitZ, side);
        
        return placeSucceeded;
        */
    }
    
    @SideOnly(Side.CLIENT)
    public boolean func_150936_a(World world, int x, int y, int z, int _side, EntityPlayer player, ItemStack stack)
    {
    	return true;
    }
	
}
