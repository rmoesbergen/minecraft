package nl.rmoesbergen.mcplugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEggThrowEvent;
import nl.rmoesbergen.mcplugin.Sphere;

import java.util.logging.Logger;

public final class McPlugin extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		Logger logger = getLogger();
		logger.info("McPlugin Enabled");
		
		getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable() {
		Logger logger = getLogger();
		logger.info("McPlugin Disabled");
	}
	
	@EventHandler
	public static void onEggThrown(PlayerEggThrowEvent event) {
		Player player = event.getPlayer();

		Location loc = player.getTargetBlock(null, 0).getLocation();
		
		if (loc != null) {
			Sphere sphere = new Sphere();
			player.getWorld().createExplosion(loc, 0.5F);
			sphere.Draw(loc, 5F, Material.GLASS);
		}
	}
}
