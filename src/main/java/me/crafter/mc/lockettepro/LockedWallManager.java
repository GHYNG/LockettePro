package me.crafter.mc.lockettepro;
import java.util.HashSet;
import java.util.Set;
public class LockedWallManager {
	protected final Set<LockedWall> lockedWalls = new HashSet<LockedWall>();
	public final LockettePro plugin;
	public LockedWallManager(LockettePro plugin) {
		this.plugin = plugin;
	}
}