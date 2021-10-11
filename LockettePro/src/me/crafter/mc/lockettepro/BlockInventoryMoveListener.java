package me.crafter.mc.lockettepro;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Hopper;
import org.bukkit.block.Sign;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
public class BlockInventoryMoveListener implements Listener {
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onInventoryMove(InventoryMoveItemEvent event) {
		/*
		 * 2 inventories are involved in this.
		 * Items will transfer from inventory "from" to inventory "to".
		 */
		Inventory from = event.getSource();
		Inventory to = event.getDestination();
		if(from == null || to == null) {
			return;
		}
		if((Config.isItemTransferOutBlocked() && !inventoryAccessibleToHopper(from)) || (Config.isItemTransferInBlocked() && !inventoryAccessibleToHopper(to))) {
			List<String> tonames = getInventoryLockSignLines(to), fromnames = getInventoryLockSignLines(from);
			boolean sameUser = false;
			for(String toname : tonames) {
				if(fromnames.contains(toname) && Utils.isUserName(toname)) {
					sameUser = true;
					break;
				}
			}
			if(!sameUser) {
				event.setCancelled(true);
			}
		}
	}
	public boolean isInventoryLocked(Inventory inventory) {
		InventoryHolder inventoryholder = inventory.getHolder();
		if(inventoryholder instanceof DoubleChest) {
			inventoryholder = ((DoubleChest)inventoryholder).getLeftSide();
		}
		if(inventoryholder instanceof BlockState) {
			Block block = ((BlockState)inventoryholder).getBlock();
			if(Config.isCacheEnabled()) { // Cache is enabled
				if(Utils.hasValidCache(block)) {
					return Utils.getAccess(block);
				}
				else {
					if(LocketteProAPI.isLocked(block)) {
						Utils.setCache(block, true);
						return true;
					}
					else {
						Utils.setCache(block, false);
						return false;
					}
				}
			}
			else { // Cache is disabled
				if(LocketteProAPI.isLocked(block)) {
					return true;
				}
				else {
					return false;
				}
			}
		}
		return false;
	}
	public String getInventoryOwner(Inventory inventory) {
		if(!isInventoryLocked(inventory)) {
			return null;
		}
		if(inventory instanceof DoubleChestInventory) {
			DoubleChestInventory dci = (DoubleChestInventory)inventory;
			Block l = dci.getLeftSide().getLocation().getBlock(), r = dci.getRightSide().getLocation().getBlock();
			String ownerl = LocketteProAPI.getOwner(l), ownerr = LocketteProAPI.getOwner(r);
			return ownerl == null ? ownerr : ownerl;
		}
		else {
			Block block = inventory.getLocation().getBlock();
			return LocketteProAPI.getOwner(block);
		}
	}
	public List<String> getInventoryLockSignLines(Inventory inventory) { // does not return line 0 : [Private] or [More Users]
		List<String> lines = new ArrayList<String>();
		if(!isInventoryLocked(inventory)) {
			return lines;
		}
		if(inventory instanceof DoubleChestInventory) {
			DoubleChestInventory dci = (DoubleChestInventory)inventory;
			Block l = dci.getLeftSide().getLocation().getBlock(), r = dci.getRightSide().getLocation().getBlock();
			for(BlockFace blockface : LocketteProAPI.newsfaces) {
				Block relativeblockl = l.getRelative(blockface), relativeblockr = r.getRelative(blockface);
				if(LocketteProAPI.isLockSignOrAdditionalSign(relativeblockl) && LocketteProAPI.getFacing(relativeblockl) == blockface) {
					Sign sign = (Sign)relativeblockl.getState();
					for(int i = 1; i < 4; i++) {
						lines.add(sign.getLine(i));
					}
				}
				if(LocketteProAPI.isLockSignOrAdditionalSign(relativeblockr) && LocketteProAPI.getFacing(relativeblockr) == blockface) {
					Sign sign = (Sign)relativeblockr.getState();
					for(int i = 1; i < 4; i++) {
						lines.add(sign.getLine(i));
					}
				}
			}
		}
		else {
			Block block = inventory.getLocation().getBlock();
			for(BlockFace blockface : LocketteProAPI.newsfaces) {
				Block relativeblock = block.getRelative(blockface);
				if(LocketteProAPI.isLockSignOrAdditionalSign(relativeblock) && LocketteProAPI.getFacing(relativeblock) == blockface) {
					Sign sign = (Sign)relativeblock.getState();
					for(int i = 1; i < 4; i++) {
						lines.add(sign.getLine(i));
					}
				}
			}
		}
		return lines;
	}
	public boolean inventoryAccessibleToEveryone(Inventory inventory) {
		if(!isInventoryLocked(inventory)) {
			return true;
		}
		for(String line : getInventoryLockSignLines(inventory)) {
			if(Config.isEveryoneSignString(line)) {
				return true;
			}
		}
		return false;
	}
	public boolean inventoryAccessibleToHopper(Inventory inventory) {
		if(inventoryAccessibleToEveryone(inventory)) {
			return true;
		}
		for(String line : getInventoryLockSignLines(inventory)) {
			if(Config.isAllowHopperSignString(line)) {
				return true;
			}
		}
		return false;
	}
	public boolean inventoryAccessibleToSomeone(Inventory inventory, String name) {
		if(inventoryAccessibleToHopper(inventory)) {
			return true;
		}
		return getInventoryLockSignLines(inventory).contains(name);
	}
}
