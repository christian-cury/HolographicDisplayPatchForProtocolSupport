package com.gmail.filoghost.holograms.patch;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.comphenix.packetwrapper.WrapperPlayServerAttachEntity;
import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.packetwrapper.WrapperPlayServerEntityTeleport;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntity;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntity.ObjectTypes;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.ScheduledPacket;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.gmail.filoghost.holograms.api.HolographicDisplaysAPI;

import protocolsupport.api.ProtocolSupportAPI;

@SuppressWarnings("deprecation")
public class HologramsPacketListener extends PacketAdapter {
	
	private static final double OFFSET_HORSE = 58.25;
	private static final double OFFSET_OTHER = 1.2;
	
	private static final Byte ENTITY_INVISIBLE = Byte.valueOf((byte) 32);

	public HologramsPacketListener(Plugin plugin) {
		super(plugin, ListenerPriority.HIGHEST,
				PacketType.Play.Server.SPAWN_ENTITY_LIVING,
				PacketType.Play.Server.SPAWN_ENTITY,
				PacketType.Play.Server.ATTACH_ENTITY,
				PacketType.Play.Server.ENTITY_METADATA,
				PacketType.Play.Server.ENTITY_TELEPORT);
	}

	@Override
	public void onPacketSending(final PacketEvent event) {
		 if (event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY) {
	            WrapperPlayServerSpawnEntity spawnLivingPacket = new WrapperPlayServerSpawnEntity(event.getPacket());
	            
	            Entity entity = spawnLivingPacket.getEntity(event);

	            if (entity != null && entity.getType() == EntityType.ARMOR_STAND) {
	            	
	                if (HolographicDisplaysAPI.isHologramEntity(entity) && ProtocolSupportAPI.getProtocolVersion(event.getPlayer()).toString().startsWith("MINECRAFT_1_7")) {
	                	WrapperPlayServerEntityMetadata packetmetadata = new WrapperPlayServerEntityMetadata(event.getPacket());
	                	
	                	List<WrappedWatchableObject> metadata = packetmetadata.getEntityMetadata();
	                	Iterator<WrappedWatchableObject> iter =	metadata.iterator();
	                	
	                	
	                	String customName = "";
	                	
	            		WrappedWatchableObject current;
	            		
	            		while (iter.hasNext()) {
	            			current = iter.next();
	            			
	            			if (current.getIndex() == 2) {
	            				if (current.getValue() != null && current.getValue().getClass() == String.class) {
	            					customName = (String) current.getValue();
	            					
	            				}
	            				
	            			} else if (current.getIndex() == 3) {
	            				// Do nothing here
	            			} else {
	            				iter.remove();
	            			}
	            		}
	            		
	                    event.setCancelled(true);
	                    Nametag holo = new Nametag(1, event.getPlayer().getLocation(), 20 - (1 * 0.25), customName);
	                }
	            }
	        }
		 
		 /*
		if (!HologramsPatch.hasOldProtocol(event.getPlayer())) {
			// If the player has the 1.7 protocol, do nothing, because holograms will still work
			return;
		}
		
		if (event.isCancelled()) {
			return;
		}
		
		if (event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY_LIVING) {
			
			WrapperPlayServerSpawnEntityLiving spawnLivingPacket = new WrapperPlayServerSpawnEntityLiving(event.getPacket());
			
			Entity entity = spawnLivingPacket.getEntity(event);
			if (entity == null) {
				return;
			}
			
			if (spawnLivingPacket.getType() == EntityType.ARMOR_STAND && HolographicDisplaysAPI.isHologramEntity(entity)) {
				
				//spawnLivingPacket.setType(30); // Armor stands as living entities ID
				spawnLivingPacket.setType(EntityType.HORSE);//Set entity to Horse
				List<WrappedWatchableObject> metadata = spawnLivingPacket.getMetadata().getWatchableObjects();
				
				if (metadata != null) {
					fixIndexes(metadata, event.getPlayer());
					metadata.add(new WrappedWatchableObject(0, ENTITY_INVISIBLE));
					spawnLivingPacket.setMetadata(new WrappedDataWatcher(metadata));
					
				}
			}
			
		} else if (event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY) {
			
			WrapperPlayServerSpawnEntity spawnPacket = new WrapperPlayServerSpawnEntity(event.getPacket());
			
			Entity entity = spawnPacket.getEntity(event);
			if (entity == null) {
				return;
			}
			
			if (spawnPacket.getType() == ObjectTypes.WITHER_SKULL && HolographicDisplaysAPI.isHologramEntity(entity)) {
				
				spawnPacket.setType(78); // The object ID for armor stands
				//spawnPacket.setType(value);

				final WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata();
				metadataPacket.setEntityId(spawnPacket.getEntityID());

				List<WrappedWatchableObject> metadata = metadataPacket.getEntityMetadata();
				metadata.add(new WrappedWatchableObject(0, ENTITY_INVISIBLE));
				metadataPacket.setEntityMetadata(metadata);
				
				// Send the metadata packet later, after the spawn packet.
				event.schedule(ScheduledPacket.fromSilent(metadataPacket.getHandle(), event.getPlayer()));
			}
			
		} else if (event.getPacketType() == PacketType.Play.Server.ENTITY_METADATA) {
			
			WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata(event.getPacket());
			
			Entity entity = metadataPacket.getEntity(event);
			if (entity == null) {
				return;
			}
			
			if (entity.getType() == EntityType.ARMOR_STAND && HolographicDisplaysAPI.isHologramEntity(entity)) {
				
				List<WrappedWatchableObject> metadata = metadataPacket.getEntityMetadata();
				fixIndexes(metadata, event.getPlayer());
				metadata.add(new WrappedWatchableObject(0, ENTITY_INVISIBLE)); // To make the armor stand invisible
				metadataPacket.setEntityMetadata(metadata);
			}
			
		} else if (event.getPacketType() == PacketType.Play.Server.ATTACH_ENTITY) {
			
			WrapperPlayServerAttachEntity attachPacket = new WrapperPlayServerAttachEntity(event.getPacket());
			
			Entity vehicle = attachPacket.getVehicle(event);
			Entity passenger = attachPacket.getEntity(event);
			
			if (vehicle != null && passenger != null && HolographicDisplaysAPI.isHologramEntity(vehicle)) {
				// Correct the position of the vehicle, because when the vehicle is spawned it doesn't have a passenger.
				Location loc = vehicle.getLocation();
				final WrapperPlayServerEntityTeleport teleportPacket = new WrapperPlayServerEntityTeleport();
				teleportPacket.setEntityID(attachPacket.getVehicleId());
				teleportPacket.setX(loc.getX());
				teleportPacket.setZ(loc.getZ());
				
				if (passenger.getType() == EntityType.ARMOR_STAND) {
					teleportPacket.setY(loc.getY() - OFFSET_HORSE);
				} else if (passenger.getType() == EntityType.DROPPED_ITEM || passenger.getType() == EntityType.SLIME) {
					teleportPacket.setY(loc.getY() - OFFSET_OTHER);
				}

				event.schedule(ScheduledPacket.fromSilent(teleportPacket.getHandle(), event.getPlayer()));
			}
			
		} else if (event.getPacketType() == PacketType.Play.Server.ENTITY_TELEPORT) {

			WrapperPlayServerEntityTeleport teleportPacket = new WrapperPlayServerEntityTeleport(event.getPacket());
			
			Entity entity = teleportPacket.getEntity(event);
			if (entity == null) {
				return;
			}
			
			if (entity.getType() == EntityType.ARMOR_STAND && HolographicDisplaysAPI.isHologramEntity(entity)) {

				Entity passenger = entity.getPassenger();
				if (passenger == null) {
					return;
				}
				
				if (passenger.getType() == EntityType.DROPPED_ITEM || passenger.getType() == EntityType.SLIME) {
					teleportPacket.setY(entity.getLocation().getY() - OFFSET_OTHER);
				} else if (passenger.getType() == EntityType.HORSE) {
					teleportPacket.setEntityID(entity.getPassenger().getEntityId());
					teleportPacket.setY(entity.getLocation().getY() - OFFSET_HORSE);
				}
			}
		}
		*/
	}
	
	private void fixIndexes(List<WrappedWatchableObject> metadata, Player player) {
		Iterator<WrappedWatchableObject> iter =	metadata.iterator();
		WrappedWatchableObject current;
		
		while (iter.hasNext()) {
			current = iter.next();
			
			if (current.getIndex() == 2) {
				if (current.getValue() != null && current.getValue().getClass() == String.class) {
					String customName = (String) current.getValue();

					if (customName.contains("{player}") || customName.contains("{displayname}")) {
						customName = customName.replace("{player}", player.getName()).replace("{displayname}", player.getDisplayName());
						current.setValue(customName);
					}
				}
				
			} else if (current.getIndex() == 3) {
				// Do nothing here
			} else {
				iter.remove();
			}
		}
	}
	
	private int entityId = Short.MAX_VALUE;

    private final int WITHER_SKULL = 66;
    
	public void spawnNametag(Location loc, double dy,  String message) {
        WrapperPlayServerSpawnEntityLiving horse = new WrapperPlayServerSpawnEntityLiving();
        WrapperPlayServerSpawnEntity skull = new WrapperPlayServerSpawnEntity();
        WrapperPlayServerAttachEntity attach = new WrapperPlayServerAttachEntity();
        
        // Initialize horse packet
        horse.setEntityID(entityId);
        horse.setType(EntityType.HORSE);
        setLocation(horse, dy, loc);

        WrappedDataWatcher wdw = new WrappedDataWatcher();
        wdw.setObject(10, message);
        wdw.setObject(11, (byte) 1);
        wdw.setObject(12, -1700000);
        horse.setMetadata(wdw);
        
        // Initialize skull packet
        skull.setEntityID(entityId + 1);
        skull.setType(WITHER_SKULL);
        setLocation(skull, dy, loc);

        // The horse is riding on the skull
        attach.setEntityId(entityId);
        attach.setVehicleId(entityId + 1);
       
        for(Player p : Bukkit.getOnlinePlayers()){
            try {
            	ProtocolLibrary.getProtocolManager().sendServerPacket(p, horse.getHandle());
            	ProtocolLibrary.getProtocolManager().sendServerPacket(p, skull.getHandle());
				ProtocolLibrary.getProtocolManager().sendServerPacket(p, attach.getHandle());
				
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
        }
    }
    
    private void setLocation(WrapperPlayServerSpawnEntity living, double dy, Location location) {
        living.setX(location.getX());
        living.setY(location.getY() + dy + 55);
        living.setZ(location.getZ());
    }
    
    private void setLocation(WrapperPlayServerSpawnEntityLiving living, double dy, Location location) {
        living.setX(location.getX());
        living.setY(location.getY() + dy + 55);
        living.setZ(location.getZ());
    }

}