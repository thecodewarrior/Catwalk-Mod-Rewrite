package com.thecodewarrior.catwalks;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thecodewarrior.catwalks.block.BlockCagedLadder;
import com.thecodewarrior.catwalks.block.BlockCatwalk;
import com.thecodewarrior.catwalks.block.BlockScaffold;
import com.thecodewarrior.catwalks.block.BlockSturdyRail;
import com.thecodewarrior.catwalks.block.BlockSturdyRailActivator;
import com.thecodewarrior.catwalks.block.BlockSturdyRailBooster;
import com.thecodewarrior.catwalks.block.BlockSturdyRailDetector;
import com.thecodewarrior.catwalks.block.BlockSupportColumn;
import com.thecodewarrior.catwalks.item.ItemBlockScaffold;
import com.thecodewarrior.catwalks.item.ItemBlockSupportColumn;
import com.thecodewarrior.catwalks.item.ItemBlowtorch;
import com.thecodewarrior.catwalks.item.ItemCautionTape;
import com.thecodewarrior.catwalks.item.ItemRopeLight;
import com.thecodewarrior.catwalks.legacy.BlockLegacyCatwalk;
import com.thecodewarrior.catwalks.util.CatwalkOptions;
import com.thecodewarrior.catwalks.util.CatwalkUtil;
import com.thecodewarrior.catwalks.util.CommonProxy;

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
    public static final String MODVER = "0.1.17";
    
	public static final String loggerName = "Catwalks";
	public static Logger l;
	
	public static Logger logger(String... name) {
		if(name.length > 0) {
			return LogManager.getLogger(loggerName + "][" + StringUtils.join(name, "]["));
		} else {
			return LogManager.getLogger(loggerName);
		}
	}
    
    @Instance(value = CatwalkMod.MODID)
    public static CatwalkMod instance;
    
    public static CatwalkOptions options = new CatwalkOptions();
        
    public static Block.SoundType catwalkSounds;
    public static Block.SoundType ladderSounds;
    
    public static Block sturdyTrack;
    public static Block sturdyPoweredTrack;
    public static Block sturdyDetectorTrack;
    public static Block sturdyActivatorTrack;
    
    public static Block supportColumn;
    public static Block scaffold;
    
    public static Block defaultCatwalk;
    /**
     * Usage: <pre> catwalks.get(lights).get(isBottomOpen).get(tape); </pre>
     */
    public static Map<Boolean,         // lights
    				  Map<Boolean,     // bottom
    				      Map<Boolean, // tape
    				          Block>>> catwalks;
    
    public static Block defaultLadder;
    /**
     * Usage: <pre> ladders.get(facing).get(lights).get(isBottomOpen).get(tape); </pre>
     */
    public static Map<ForgeDirection,      // facing
    				  Map<Boolean,         // light
    				      Map<Boolean,     // bottom
    				          Map<Boolean, // tape
    				          	  Block>>>> ladders;
    
    public static Item itemBlowtorch;
    public static Item itemRopeLight;
    public static Item itemCautionTape;
    
    public static int speedEffectLevel = 2;
    public static AttributeModifier speedModifier;
    
    @SidedProxy(clientSide="com.thecodewarrior.catwalks.util.ClientProxy", serverSide="com.thecodewarrior.catwalks.util.CommonProxy")
    public static CommonProxy proxy;
    
    public static int catwalkRenderType;
    public static int ladderRenderType;
    public static int inAndOutRenderType;
    public static int supportRenderType;
    
	public static int lightLevel = 12;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	l = logger();
    	CatwalkUtil.init();

    	boolean[] trueFalse = {true, false};
    	
    	catwalkRenderType   = RenderingRegistry.getNextAvailableRenderId();
    	ladderRenderType    = RenderingRegistry.getNextAvailableRenderId();
    	inAndOutRenderType  = RenderingRegistry.getNextAvailableRenderId();
    	supportRenderType   = RenderingRegistry.getNextAvailableRenderId();
    	
    	catwalkSounds = new Block.SoundType("catwalk", 1.0F, 1.0F) {
    		public String getBreakSound()
            {
                return "catwalks:dig." + this.soundName;
            }

            public String getStepResourcePath()
            {
                return "catwalks:step." + this.soundName;
            }
    	};
    	ladderSounds  = new Block.SoundType("ladder",  1.0F, 1.0F) {
    		public String getBreakSound()
            {
                return "catwalks:dig." + this.soundName;
            }

            public String getStepResourcePath()
            {
                return "catwalks:step." + this.soundName;
            }
    	};
    	
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
    	
    	
    	sturdyTrack = new BlockSturdyRail();
    	GameRegistry.registerBlock(sturdyTrack, "sturdy_rail");
    	
    	sturdyPoweredTrack = new BlockSturdyRailBooster();
    	GameRegistry.registerBlock(sturdyPoweredTrack, "sturdy_rail_powered");
    	
    	sturdyDetectorTrack = new BlockSturdyRailDetector();
    	GameRegistry.registerBlock(sturdyDetectorTrack, "sturdy_rail_detector");
    	
    	sturdyActivatorTrack = new BlockSturdyRailActivator();
    	GameRegistry.registerBlock(sturdyActivatorTrack, "sturdy_rail_activator");
    	
    	
    	supportColumn = new BlockSupportColumn();
    	GameRegistry.registerBlock(supportColumn, ItemBlockSupportColumn.class, "support_column");
    	scaffold = new BlockScaffold();
    	GameRegistry.registerBlock(scaffold, ItemBlockScaffold.class, "scaffold");
    	
    	catwalks = new HashMap<Boolean, Map<Boolean, Map<Boolean,Block>>>();
    	for(boolean lights : trueFalse) {
    		Map<Boolean, Map<Boolean, Block>> lightMap = new HashMap<Boolean, Map<Boolean,Block>>();
    		catwalks.put(lights, lightMap);
    		for(boolean bottom : trueFalse) {
    			Map<Boolean, Block> bottomMap = new HashMap<Boolean,Block>();
        		lightMap.put(bottom, bottomMap);
    			for(boolean tape : trueFalse) {
    				BlockCatwalk b = new BlockCatwalk(lights, bottom, tape);
    				bottomMap.put(tape, b);
    				String id = "catwalk";
    				id += lights ? "_lit" : "_unlit";
    				if(!bottom) id += "_nobottom";
    				if(tape) id += "_tape";
    				GameRegistry.registerBlock(b, id);
    			}
    		}
    	}
    	defaultCatwalk = catwalks.get(false).get(false).get(false);
    	
    	ForgeDirection[] directions = {ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST};
    	ladders = new HashMap<ForgeDirection, Map<Boolean,Map<Boolean,Map<Boolean,Block>>>>();
    	for (ForgeDirection dir : directions) {
			Map<Boolean, Map<Boolean, Map<Boolean, Block>>> dirMap = new HashMap<Boolean, Map<Boolean,Map<Boolean,Block>>>();
			ladders.put(dir, dirMap);
			
			for (boolean lights : trueFalse) {
				Map<Boolean, Map<Boolean, Block>> lightMap = new HashMap<Boolean, Map<Boolean,Block>>();
				dirMap.put(lights, lightMap);
				
				for (boolean bottomOpen : trueFalse) {
					Map<Boolean, Block> bottomMap = new HashMap<Boolean, Block>();
					lightMap.put(bottomOpen, bottomMap);
					
					for (boolean tape : trueFalse) {
						BlockCagedLadder b = new BlockCagedLadder(dir, lights, bottomOpen, tape);
						bottomMap.put(tape, b);
						String id = "cagedLadder";
						if(dir == ForgeDirection.NORTH) id += "_north";
						if(dir == ForgeDirection.SOUTH) id += "_south";
						if(dir == ForgeDirection.WEST)  id += "_west";
						if(dir == ForgeDirection.EAST)  id += "_east";
						if(lights) id += "_lit"; else id += "_unlit";
						if(bottomOpen) id += "_nobottom";
						if(tape) id += "_tape";
//						System.out.println("Registered block: " + id);
				    	GameRegistry.registerBlock(b, id);
					}
				}
			}
		}
    	defaultLadder = ladders.get(ForgeDirection.NORTH).get(false).get(false).get(false);
    	
    	itemBlowtorch   = new ItemBlowtorch();
    	itemRopeLight   = new ItemRopeLight();
    	itemCautionTape = new ItemCautionTape();
    	
    	GameRegistry.registerItem(itemBlowtorch,   "blowtorch"  );
    	GameRegistry.registerItem(itemRopeLight,   "ropeLight"  );
    	GameRegistry.registerItem(itemCautionTape, "cautionTape");
    	
    	proxy.init();
    }
    
    @EventHandler
    public void load(FMLInitializationEvent event) {
    	
    }
        
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    	proxy.postInit();
    	options.initCustomLadders();
    }
}