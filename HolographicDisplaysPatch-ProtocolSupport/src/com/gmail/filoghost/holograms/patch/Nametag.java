package com.gmail.filoghost.holograms.patch;

import com.comphenix.packetwrapper.WrapperPlayServerAttachEntity;
import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.packetwrapper.WrapperPlayServerEntityTeleport;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntity;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Nametag {
	 
    int id;
 
    Location loc;
 
    double dy;
 
    String name;
 
    public Nametag(int id, Location loc, double dy, String name) {
 
        this.id = id;
 
        this.loc = loc;
 
        this.dy = dy;
 
        this.name = name;
    }
 
    public void showNametag(Player p) throws InvocationTargetException {
 
        WrapperPlayServerSpawnEntityLiving horse = new WrapperPlayServerSpawnEntityLiving();
 
        horse.setEntityID(id);
 
        horse.setType(EntityType.HORSE);
 
        horse.setX(loc.getX());
        horse.setY(loc.getY() + dy + 55);
        horse.setZ(loc.getZ());
 
        WrappedDataWatcher wdw = new WrappedDataWatcher();
 
        wdw.setObject(10, name);
        wdw.setObject(11, (byte) 1);
 
        wdw.setObject(12, -1700000);
 
        horse.setMetadata(wdw);
 
        ProtocolLibrary.getProtocolManager().sendServerPacket(p, horse.getHandle());
 
        WrapperPlayServerSpawnEntity skull = new WrapperPlayServerSpawnEntity();
 
        skull.setEntityID(id + 1);
 
        skull.setType(66);
 
        skull.setX(loc.getX());
        skull.setY(loc.getY() + dy + 55);
        skull.setZ(loc.getZ());
 
        ProtocolLibrary.getProtocolManager().sendServerPacket(p, skull.getHandle());
 
        WrapperPlayServerAttachEntity attach = new WrapperPlayServerAttachEntity();
 
        attach.setEntityId(id);
        attach.setVehicleId(id + 1);
 
        ProtocolLibrary.getProtocolManager().sendServerPacket(p, attach.getHandle());
    }
 
    public void hideNametag(Player p) throws InvocationTargetException {
 
        WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
 
        ArrayList<Integer> entities = new ArrayList<>();
 
        entities.add(id);
        entities.add(id + 1);
 
        destroy.setEntities(entities);
 
        ProtocolLibrary.getProtocolManager().sendServerPacket(p, destroy.getHandle());
    }
 
    public void setLocation(Player p, Location loc) throws InvocationTargetException {
 
        WrapperPlayServerEntityTeleport teleport = new WrapperPlayServerEntityTeleport();
 
        teleport.setEntityID(id + 1);
 
        teleport.setX(loc.getX());
        teleport.setY(loc.getY() + 55 + dy);
        teleport.setZ(loc.getZ());
 
        ProtocolLibrary.getProtocolManager().sendServerPacket(p, teleport.getHandle());
    }
 
    public void setName(Player p, String name) throws InvocationTargetException {
 
        this.name = name;
 
        WrapperPlayServerEntityMetadata eMeta = new WrapperPlayServerEntityMetadata();
 
        eMeta.setEntityId(id);
 
        WrappedDataWatcher wdw = new WrappedDataWatcher();
 
        wdw.setObject(10, name);
 
        eMeta.setEntityMetadata(wdw.getWatchableObjects());
 
        ProtocolLibrary.getProtocolManager().sendServerPacket(p, eMeta.getHandle());
    }
}