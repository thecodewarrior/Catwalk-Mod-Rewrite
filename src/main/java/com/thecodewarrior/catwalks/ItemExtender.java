package com.thecodewarrior.catwalks;

import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.client.audio.SoundList;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemExtender extends Item {

	public IIcon closedIcon;
	public IIcon openIcon;
	
	public ItemExtender() {
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.tabTools);
		setUnlocalizedName("extender");
	}
	
	int lastTime = 0;
	
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        openIcon   = reg.registerIcon(CatwalkMod.MODID + ":" + "extender");
        closedIcon = reg.registerIcon(CatwalkMod.MODID + ":" + "extender_closed");
    }
	
	@Override
	public IIcon getIconFromDamage(int meta) {
	    if (meta > 1)
	        meta = 0;

	    return meta == 0 ? closedIcon : openIcon;
	}
	
	@Override
	public boolean hasEffect(ItemStack stack){
		return stack.getItemDamage() != 0;
	}
	
    public void extend(ItemStack stack, World world, EntityPlayer player) {
    	
    	ForgeDirection facing = CatwalkUtil.getFacingDirection(player);
    	
    	int x = MathHelper.floor_double( player.posX + (0.2*facing.offsetX));
    	int y = MathHelper.floor_double( player.posY );
    	int z = MathHelper.floor_double( player.posZ + (0.2*facing.offsetZ));
    	
    	if(facing == ForgeDirection.UP) {
    		Block b = world.getBlock(x, MathHelper.floor_double( player.posY + 1.5), z);
        	if(b instanceof IExtendable) {
        		IExtendable ex = (IExtendable) b;
        		if( ex.extend(world, x, MathHelper.floor_double( player.posY + 1.5), z, player) )
        			return;
        	}
    	}
    		
    	
    	Block b = world.getBlock(x, y, z);
    	if(b instanceof IExtendable) {
    		IExtendable ex = (IExtendable) b;
    		ex.extend(world, x, y, z, player);
    	} else {
    		x = MathHelper.floor_double( player.posX - (0.5*facing.offsetX));
    		y = MathHelper.floor_double( player.posZ - (0.5*facing.offsetZ));
    		
    		b = world.getBlock(x, y, z);
        	if(b instanceof IExtendable) {
        		IExtendable ex = (IExtendable) b;
        		ex.extend(world, x, y, z, player);
        	}
    	}
    }
    
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
    	if(player.isSneaking()) {
	    	ItemStack replace = stack.copy();
	    	replace.setItemDamage(stack.getItemDamage() == 0 ? 1 : 0);
	    	world.playSoundAtEntity(player, "random.successful_hit", 1, 1);
	        return replace;
    	} else {
    		
    		this.extend(stack, world, player);
    		
    		return stack;
    	}
    }
//    
//    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int itemInUseCount) {
//    	CatwalkUtil.getOrCreateEP(player).usingExtender = false;
//    }

	
}
