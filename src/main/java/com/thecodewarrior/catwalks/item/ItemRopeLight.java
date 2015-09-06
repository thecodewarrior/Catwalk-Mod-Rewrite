package com.thecodewarrior.catwalks.item;

import com.thecodewarrior.catwalks.CatwalkMod;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class ItemRopeLight extends Item {
	public ItemRopeLight() {
		setCreativeTab(CreativeTabs.tabMisc);
		setUnlocalizedName("rope_light");
	}
	
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.itemIcon = reg.registerIcon(CatwalkMod.MODID + ":" + "ropelight");
    }
	
	
	public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player)
    {
        return true;
    }
}
