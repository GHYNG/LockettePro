package me.crafter.mc.lockettepro;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
/*
 * Unfinished.
 */
/**
 * @author GHYNG
 */
public class LockedWall {
	public final Material material;
	public final World world;
	protected final Set<Location> locations = new HashSet<Location>();
	protected String owner = "player";
	public LockedWall(World world, Material material, String owner) {
		this.world = world;
		this.material = material;
		this.owner = owner;
	}
	public boolean contains(Location location) {
		if(location == null) {
			return false;
		}
		if(locations.contains(location)) {
			return true;
		}
		Location locB = location;
		for(Location locA : locations) {
			double xa = locA.getX(), xb = locB.getX();
			double ya = locA.getY(), yb = locB.getY();
			double za = locA.getZ(), zb = locB.getZ();
			World worldA = locA.getWorld(), worldB = locB.getWorld();
			if(worldA.equals(worldB)) {
				if(xa == xb && ya == yb && za == zb) {
					return true;
				}
			}
		}
		return false;
	}
	public static Set<Block> getWallOfBlock(Block block) {
		return getWallOfBlock(block, new HashSet<Block>());
	}
	public static Set<Block> getWallOfBlock(Block block, Set<Block> blocks) {
		Material material = block.getType();
		if(!Config.isLockable(material)) {
			return blocks;
		}
		Location location = block.getLocation();
		Location xp = location.add(1, 0, 0), xn = location.add(-1, 0, 0);
		Location yp = location.add(0, 1, 0), yn = location.add(0, -1, 0);
		Location zp = location.add(0, 0, 1), zn = location.add(0, 0, -1);
		Block bxp = xp.getBlock(), bxn = xn.getBlock();
		Block byp = yp.getBlock(), byn = yn.getBlock();
		Block bzp = zp.getBlock(), bzn = zn.getBlock();
		getWallOfBlockProgress(bxp, blocks, material);
		getWallOfBlockProgress(bxn, blocks, material);
		getWallOfBlockProgress(byp, blocks, material);
		getWallOfBlockProgress(byn, blocks, material);
		getWallOfBlockProgress(bzp, blocks, material);
		getWallOfBlockProgress(bzn, blocks, material);
		return blocks;
	}
	private static void getWallOfBlockProgress(Block block, Set<Block> blocks, Material targetMaterial) {
		if(blocks.contains(block)) {
			return;
		}
		if(block == null || blocks == null) {
			return;
		}
		if(block.getType() == targetMaterial) {
			getWallOfBlock(block, blocks);
		}
	}
}
