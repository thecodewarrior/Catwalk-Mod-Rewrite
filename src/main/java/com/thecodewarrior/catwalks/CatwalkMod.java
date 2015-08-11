package com.thecodewarrior.catwalks;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid=CatwalkMod.MODID, name=CatwalkMod.MODNAME, version=CatwalkMod.MODVER)
public class CatwalkMod
{
    public static final String MODID = "catwalks";
    public static final String MODNAME = "Catwalks";
    public static final String MODVER = "0.1.0";
    
    @Instance(value = CatwalkMod.MODID)
    public static CatwalkMod instance;
    
    public static CatwalkOptions options = new CatwalkOptions();
    
    public static Block catwalkLitBottom;
    public static Block catwalkUnlitBottom;
    public static Block catwalkLitNoBottom;
    public static Block catwalkUnlitNoBottom;
        
    public static Block ladderNorthLit;
    public static Block ladderNorthUnlit;
    public static Block ladderSouthLit;
    public static Block ladderSouthUnlit;
    public static Block ladderWestLit;
    public static Block ladderWestUnlit;
    public static Block ladderEastLit;
    public static Block ladderEastUnlit;
    
    
    public static Item itemBlowtorch;
    public static Item itemRopeLight;
    
    public static int speedEffectLevel = 2;
    public static AttributeModifier speedModifier;
    
    @SidedProxy(clientSide="com.thecodewarrior.catwalks.ClientProxy", serverSide="com.thecodewarrior.catwalks.CommonProxy")
    public static CommonProxy proxy;
    
    public static int catwalkRenderType;
    public static int ladderRenderType;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	catwalkRenderType = RenderingRegistry.getNextAvailableRenderId();
    	ladderRenderType  = RenderingRegistry.getNextAvailableRenderId();
    	
    	options.init();
    	options.load();
    	
    	FMLCommonHandler.instance().bus().register(proxy);  
    	MinecraftForge.EVENT_BUS.register(proxy);
    	speedModifier =  new AttributeModifier(
    			UUID.fromString("eabff2b8-b6a0-436e-b570-f2e6b059fcd2"),
    			"catwalkmod.speedup",
    			0.20000000298023224D,
    			2);
    	speedModifier.setSaved(false);
    	
    	catwalkLitBottom     = new BlockCatwalk(true , true );
    	catwalkUnlitBottom   = new BlockCatwalk(false, true );
    	catwalkLitNoBottom   = new BlockCatwalk(true , false);
    	catwalkUnlitNoBottom = new BlockCatwalk(false, false);
    	
    	GameRegistry.registerBlock(catwalkLitBottom,     "catwalk_lit_bottom"    );
    	GameRegistry.registerBlock(catwalkUnlitBottom,   "catwalk_unlit_bottom"  );
    	GameRegistry.registerBlock(catwalkLitNoBottom,   "catwalk_lit_nobottom"  );
    	GameRegistry.registerBlock(catwalkUnlitNoBottom, "catwalk_unlit_nobottom");
    	
    	ladderNorthLit   = new BlockCagedLadder(ForgeDirection.NORTH, true);
    	ladderNorthUnlit = new BlockCagedLadder(ForgeDirection.NORTH, false);
    	ladderSouthLit   = new BlockCagedLadder(ForgeDirection.SOUTH, true);
    	ladderSouthUnlit = new BlockCagedLadder(ForgeDirection.SOUTH, false);
    	ladderWestLit   = new BlockCagedLadder(ForgeDirection.WEST, true);
    	ladderWestUnlit = new BlockCagedLadder(ForgeDirection.WEST, false);
    	ladderEastLit   = new BlockCagedLadder(ForgeDirection.EAST, true);
    	ladderEastUnlit = new BlockCagedLadder(ForgeDirection.EAST, false);
    	
    	GameRegistry.registerBlock(ladderNorthLit,   "cagedLadder_north_lit"  );
    	GameRegistry.registerBlock(ladderNorthUnlit, "cagedLadder_north_unlit");
    	GameRegistry.registerBlock(ladderSouthLit,   "cagedLadder_south_lit"  );
    	GameRegistry.registerBlock(ladderSouthUnlit, "cagedLadder_south_unlit");
    	GameRegistry.registerBlock(ladderWestLit,    "cagedLadder_west_lit"   );
    	GameRegistry.registerBlock(ladderWestUnlit,  "cagedLadder_west_unlit" );
    	GameRegistry.registerBlock(ladderEastLit,    "cagedLadder_east_lit"   );
    	GameRegistry.registerBlock(ladderEastUnlit,  "cagedLadder_east_unlit" );
    	
    	itemBlowtorch = new ItemBlowtorch();
    	itemRopeLight = new ItemRopeLight();
    	
    	GameRegistry.registerItem(itemBlowtorch, "blowtorch");
    	GameRegistry.registerItem(itemRopeLight, "ropeLight");
//    	GameRegistry.registerBlock(cagedLadderLit, 	 "cagedladder_lit"  );
//    	GameRegistry.registerBlock(cagedLadderUnlit, "cagedladder_unlit");
    	proxy.init();
    }
    
    @EventHandler
    public void load(FMLInitializationEvent event) {
    	
    }
        
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    	proxy.postInit();
    }
}