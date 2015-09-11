package com.thecodewarrior.catwalks.legacy;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

import com.thecodewarrior.catwalks.CatwalkMod;

public class TileEntityCatwalk extends TileEntity{
	
	public void updateEntity() {
    	
    	Block b = CatwalkMod.catwalks.get(this.hasRopeLight()).get(!this.isDownOpen()).get(false);
    	
		int meta = 0;
		
		meta = setBit(meta, 3, isNorthOpen());
		meta = setBit(meta, 2, isSouthOpen());
		meta = setBit(meta, 1, isWestOpen() );
		meta = setBit(meta, 0, isEastOpen() );
		
    	worldObj.setBlock(xCoord, yCoord, zCoord, b, meta, 3);
    	CatwalkMod.l.info("Updated Catwalk at: " + xCoord + ", " + yCoord + ", " + zCoord);
    }
	
	public int setBit(int val, int pos, boolean value) {
		if(value)
			return val |  (1 << pos);
		else
			return val & ~(1 << pos);
	}
	
	public boolean north = true;
	public boolean south = true;
	public boolean east  = true;
	public boolean west  = true;
	public boolean down  = true;
	
	public boolean northForceOpen = false;
	public boolean southForceOpen = false;
	public boolean eastForceOpen  = false;
	public boolean westForceOpen  = false;
	public boolean downForceOpen  = false;
	
	public boolean ropeLight = false;
	
	public boolean isNorthOpen() {
		return !north || northForceOpen;
	}
	
	public boolean isSouthOpen() {
		return !south || southForceOpen;
	}
	
	public boolean isEastOpen() {
		return !east || eastForceOpen;
	}
	
	public boolean isWestOpen() {
		return !west || westForceOpen;
	}
	
	public boolean isDownOpen() {
		return !down || downForceOpen;
	}
	
	public boolean hasRopeLight() {
		return ropeLight;
	}
    
    @Override
    public AxisAlignedBB getRenderBoundingBox()
	{
		return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbtTag) {
		super.writeToNBT(nbtTag);
		nbtTag.setBoolean("n", north);
		nbtTag.setBoolean("s", south);
		nbtTag.setBoolean("e", east );
		nbtTag.setBoolean("w", west );
		nbtTag.setBoolean("d", down );
		
		nbtTag.setBoolean("nF", northForceOpen);
		nbtTag.setBoolean("sF", southForceOpen);
		nbtTag.setBoolean("eF", eastForceOpen );
		nbtTag.setBoolean("wF", westForceOpen );
		nbtTag.setBoolean("dF", downForceOpen );
		nbtTag.setBoolean("ropeLight", ropeLight);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
	    super.readFromNBT(nbt);
	    this.north = nbt.getBoolean("n");
	    this.south = nbt.getBoolean("s");
	    this.east  = nbt.getBoolean("e");
	    this.west  = nbt.getBoolean("w");
	    this.down  = nbt.getBoolean("d");
	    
	    this.northForceOpen = nbt.getBoolean("nF");
	    this.southForceOpen = nbt.getBoolean("sF");
	    this.eastForceOpen  = nbt.getBoolean("eF");
	    this.westForceOpen  = nbt.getBoolean("wF");
	    this.downForceOpen  = nbt.getBoolean("dF");
	    
	    this.ropeLight = nbt.getBoolean("ropeLight");
	}
	
	 public Packet getDescriptionPacket() {
		 NBTTagCompound nbtTag = new NBTTagCompound();
		 this.writeToNBT(nbtTag);
		 return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTag);
	 }
	 
	 public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		 readFromNBT(packet.func_148857_g());
	 }
}