package com.thecodewarrior.codechicken.lib.inventory;

import com.thecodewarrior.codechicken.lib.packet.PacketCustom;

public interface IContainerSyncVar
{
    public boolean changed();
    
    public void reset();
    
    public void writeChange(PacketCustom packet);
    
    public void readChange(PacketCustom packet);
}
