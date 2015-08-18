package com.thecodewarrior.catwalks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;
import net.minecraft.util.Vec3;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class ClientProxy extends CommonProxy {
    	public boolean isBottomOpen = false;
	
    public ISimpleBlockRenderingHandler catwalkRenderer;
    
    public ISimpleBlockRenderingHandler ladderRenderer;

    boolean debug = true;
    
    List<String> headers = new ArrayList<String>();
	int headerWidth = 0;
	double maxSpeed = 0;
    
    @Override
    public void initClient() {
    	
    	isClient = true;
    	
    	catwalkRenderer = new CatwalkRenderer();
    	RenderingRegistry.registerBlockHandler(CatwalkMod.catwalkRenderType, catwalkRenderer);
    	
    	ladderRenderer = new LadderRenderer();
    	RenderingRegistry.registerBlockHandler(CatwalkMod.ladderRenderType, ladderRenderer);

//    	id = CatwalkMod.catwalkLitNoBottom.getRenderType();
//    	catwalkRenderer_Lit_No_Bottom = new CatwalkRenderer(true, false, id);
//    	RenderingRegistry.registerBlockHandler(id, catwalkRenderer_Lit_No_Bottom);
//    	
//    	id = CatwalkMod.catwalkUnlitBottom.getRenderType();
//    	catwalkRenderer_Unlit_Bottom = new CatwalkRenderer(false, true, id);
//    	RenderingRegistry.registerBlockHandler(id, catwalkRenderer_Unlit_Bottom);
//    	
//    	id = CatwalkMod.catwalkUnlitNoBottom.getRenderType();
//    	catwalkRenderer_Unlit_No_Bottom = new CatwalkRenderer(false, false, id);
//    	RenderingRegistry.registerBlockHandler(id, catwalkRenderer_Unlit_No_Bottom);
    	
//    	id = CatwalkMod.ladderNorthLit.
    	
    	headers.add("m/t:");
    	headers.add("m/s:");
    	headers.add("m/t max:");
    	headers.add("m/s max:");
    	for(int i = 0; i < 5; i++) {
    		headers.add("_" + i + ":");
    	}
    }
    
    @Override
    public void postInit() {
    	for(String head : headers) {
			headerWidth = Math.max( Minecraft.getMinecraft().fontRenderer.getStringWidth(head), headerWidth );
		}
    }
    
	public EntityPlayer getPlayerLooking(Vec3 start, Vec3 end) {
		return Minecraft.getMinecraft().thePlayer;
	}
    
    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
    	if(!CatwalkMod.options.dev)
    		return;
    	if(event.phase == Phase.END && !Minecraft.getMinecraft().gameSettings.showDebugInfo) {
    		EntityPlayer p = Minecraft.getMinecraft().thePlayer;
    		if(p == null) {
    			return;
    		}
    		double dX = Math.abs(p.lastTickPosX - p.posX);
    		double dY = Math.abs(p.lastTickPosY - p.posY);
    		double dZ = Math.abs(p.lastTickPosZ - p.posZ);
    		
    		double speed = Math.sqrt((dX*dX) + (dY*dY) + (dZ*dZ));
//    		int bottom = Minecraft.getMinecraft().displayHeight;
//    		int right  = Minecraft.getMinecraft().displayWidth;
    		if(speed > maxSpeed)
    			maxSpeed = speed;
    		
    		FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

    		List<String> data = new ArrayList<String>();
    		
    		int precision = 5;
    		String f = "%2." + precision + "f";
    		
    		data.add(String.format(f, speed    ));
    		data.add(String.format(f, speed*20 ));
    		
    		data.add(String.format(f, maxSpeed    ));
    		data.add(String.format(f, maxSpeed*20 ));
    		    		
    		int lineNum = 0;
    		
    		for(String head : headers) {
    			fr.drawStringWithShadow(head, 5+headerWidth-fr.getStringWidth(head), linePos(lineNum), 0xffffff);
    			lineNum++;
    		}
    		
    		lineNum = 0;
    		
    		for(String dat : data) {
    			fr.drawStringWithShadow(dat, 8+headerWidth, linePos(lineNum), 0xffffff);
    			lineNum++;
    		}
    	}
    }
    
    public int linePos(int line) {
    	return 5 + ( (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT+1) * line);
    }
    
}
