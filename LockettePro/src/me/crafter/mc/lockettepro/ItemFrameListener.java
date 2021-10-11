package me.crafter.mc.lockettepro;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
public class ItemFrameListener implements Listener {
	@EventHandler
	public void onItemFrameDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		if(entity instanceof ItemFrame itemFrame) {
			Location itemFrameLocation = itemFrame.getLocation();
			Block itemFrameAtBlock = itemFrameLocation.getBlock();
			Block supportBlock = itemFrameAtBlock.getRelative(itemFrame.getFacing().getOppositeFace());
			if(!LocketteProAPI.isLocked(supportBlock)) {
				return;
			}
			if(event instanceof EntityDamageByEntityEvent event1) {
				Entity damager = event1.getDamager();
				if(damager instanceof Player player) {
					if(LocketteProAPI.isOwner(supportBlock, player)) {
						return;
					}
					Utils.sendMessages(player, Config.getLang("block-is-locked"));
				}
			}
			event.setCancelled(true);
		}
	}
	@EventHandler
	public void onPlayerInteract(PlayerInteractEntityEvent event) {
		Entity entity = event.getRightClicked();
		if(entity instanceof ItemFrame itemFrame) {
			Location itemFrameLocation = itemFrame.getLocation();
			Block itemFrameAtBlock = itemFrameLocation.getBlock();
			Block supportBlock = itemFrameAtBlock.getRelative(itemFrame.getFacing().getOppositeFace());
			if(!LocketteProAPI.isLocked(supportBlock)) {
				return;
			}
			Player player = event.getPlayer();
			if(LocketteProAPI.isOwner(supportBlock, player)) {
				return;
			}
			Utils.sendMessages(player, Config.getLang("block-is-locked"));
			event.setCancelled(true);
		}
	}
}
