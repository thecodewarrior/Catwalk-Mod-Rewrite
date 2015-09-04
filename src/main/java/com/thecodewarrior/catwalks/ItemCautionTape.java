package com.thecodewarrior.catwalks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class ItemCautionTape extends Item {
	public ItemCautionTape() {
		setCreativeTab(CreativeTabs.tabMisc);
		setUnlocalizedName("caution_tape");
	}
	
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.itemIcon = reg.registerIcon(CatwalkMod.MODID + ":" + "tape");
    }
	
	
	public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player)
    {
        return true;
    }
}
