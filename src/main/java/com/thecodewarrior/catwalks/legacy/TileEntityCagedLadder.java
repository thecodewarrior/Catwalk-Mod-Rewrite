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
import com.thecodewarrior.catwalks.block.BlockCagedLadder.RelativeSide;

public class TileEntityCagedLadder extends TileEntity {
	
	public void updateEntity() {
    	
		int old_meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
    	ForgeDirection d = ForgeDirection.NORTH;
    	switch(old_meta) {
    	case 0:
    		d = ForgeDirection.SOUTH; break;
    	case 1:
    		d = ForgeDirection.EAST;  break;
    	case 2:
    		d = ForgeDirection.NORTH; break;
    	case 3:
    		d = ForgeDirection.WEST;  break;
    	}
    	
    	Block b = CatwalkMod.ladders.get(d).get(ropeLight).get(!this.isOnBottom()).get(false);
		
		int meta = 0;
		
		meta = setOpenMeta(meta, d, ForgeDirection.NORTH, isNorthOpen());
		meta = setOpenMeta(meta, d, ForgeDirection.SOUTH, isSouthOpen());
		meta = setOpenMeta(meta, d, ForgeDirection.EAST,  isEastOpen() );
		meta = setOpenMeta(meta, d, ForgeDirection.WEST,  isWestOpen() );
		
    	worldObj.setBlock(xCoord, yCoord, zCoord, b, meta, 3);
    	CatwalkMod.l.info("Updated Caged Ladder at: " + xCoord + ", " + yCoord + ", " + zCoord);
    }
	
	public int setOpenMeta(int meta, ForgeDirection facing, ForgeDirection side, boolean value) {
		RelativeSide relSide = RelativeSide.FDtoRS(side, facing);
		switch(relSide) {
		case LADDER:
			meta = setBit(meta, 3, value);
			break;
		case FRONT:
			meta = setBit(meta, 2, value);
			break;
		case LEFT:
			meta = setBit(meta, 1, value);
			break;
		case RIGHT:
			meta = setBit(meta, 0, value);
			break;
		}
		return meta;
	}
	
	public int setBit(int val, int pos, boolean value) {
		if(value)
			return val |  (1 << pos);
		else
			return val & ~(1 << pos);
	}

	public boolean getBit(int val, int pos) {
		return ( val & (1 << pos) ) > 0;
	}
	
	public int directionOverride = -1;
	
	public boolean north = true;
	public boolean south = true;
	public boolean east  = true;
	public boolean west  = true;
	
	public boolean northForceOpen = false;
	public boolean southForceOpen = false;
	public boolean eastForceOpen  = false;
	public boolean westForceOpen  = false;
	
	public boolean ropeLight = false;

	public boolean bottomForceOpen;
	
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
	
	public boolean hasRopeLight() {
		return ropeLight;
	}
	
	public boolean isOnBottom() {
		return !bottomForceOpen && onBottom();
	}
	
	public boolean onBottom() {
		return worldObj.isAirBlock(xCoord, yCoord-1, zCoord);
	}
	
	public boolean onTop() {
		return worldObj.isAirBlock(xCoord, yCoord+1, zCoord);
	}

	public int getDirection() {
		if(directionOverride != -1) {
			return directionOverride;
		} else {
			return getWorldObj().getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
		}
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
		
		nbtTag.setBoolean("nF", northForceOpen);
		nbtTag.setBoolean("sF", southForceOpen);
		nbtTag.setBoolean("eF", eastForceOpen );
		nbtTag.setBoolean("wF", westForceOpen );
		nbtTag.setBoolean("bF", bottomForceOpen);
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
	    
	    this.northForceOpen = nbt.getBoolean("nF");
	    this.southForceOpen = nbt.getBoolean("sF");
	    this.eastForceOpen  = nbt.getBoolean("eF");
	    this.westForceOpen  = nbt.getBoolean("wF");
	    this.bottomForceOpen = nbt.getBoolean("bF");
	    
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
