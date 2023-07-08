package me.crafter.mc.lockettepro;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import io.papermc.paper.event.player.PlayerOpenSignEvent;
/**
 * This listener is used for sign protection since game 1.20 allows players to edit signs.
 * <p>
 * For now, this plugin will disable all sign editing for locked blocks,
 * including player's own sign and others'.
 * 
 * @author GHYNG
 * @since Game 1.20 Plugin 2.10.10-MW1.2
 */
public class SignEditingListener implements Listener {
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerOpenSign(PlayerOpenSignEvent event) {
		Sign sign = event.getSign();
		if(LocketteProAPI.isLockSignOrAdditionalSign(sign.getBlock())) {
			event.setCancelled(true);
		}
	}
}