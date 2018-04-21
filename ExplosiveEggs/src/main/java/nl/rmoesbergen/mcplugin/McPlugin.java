package nl.rmoesbergen.mcplugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import nl.rmoesbergen.mcplugin.Sphere;

import java.util.Objects;
import java.util.logging.Logger;

public final class McPlugin extends JavaPlugin implements Listener {

	private Location lastPosition;
	private boolean devilEnabled = false;

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

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("devil")) {
			devilEnabled = !devilEnabled;
			getLogger().info("Devil mode " + devilEnabled);
			return true;
		}
		return false;
	}

	/* Disable player damage */
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntityType() == EntityType.PLAYER) {
			if (event.getCause() == DamageCause.FALL) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (!devilEnabled) return;
		
		Player player = event.getPlayer();
		Location loc = player.getLocation();

		if (Objects.isNull(lastPosition)) {
			lastPosition = loc;
		}
		if (loc.getBlockX() != lastPosition.getBlockX() || loc.getBlockY() != lastPosition.getBlockY()
				|| loc.getBlockZ() != lastPosition.getBlockZ()) {

			ChangeWorldTask task = new ChangeWorldTask(player);
			task.runTask(this);
		}
		lastPosition = loc;
	}

	@EventHandler
	public void onEggHits(ProjectileHitEvent event) {

		World world = event.getEntity().getWorld();

		if (event.getEntityType() == EntityType.EGG) {
			Location loc = event.getEntity().getLocation();

/*			int radius = 5;
			int y = loc.getBlockY() + 2;
			for (int x = loc.getBlockX() - radius; x <= loc.getBlockX() + radius; x++)
				for (int z = loc.getBlockZ() - radius; z <= loc.getBlockZ() + radius; z++) {
					if (x == loc.getBlockX() - radius || z == loc.getBlockZ() - radius || x == loc.getBlockX() + radius
							|| z == loc.getBlockZ() + radius) {
						Block block = world.getBlockAt(x, y, z);
						block.setType(Material.FENCE);

						// Location curloc = new Location(world, x, y , z);
						// world.spawnFallingBlock(curloc, Material.FENCE, (byte)0);
					}
				}*/

			Sphere sphere = new Sphere();
			world.createExplosion(loc, 4F);
			sphere.Draw(loc, 5F, Material.GLASS);
			loc.add(0, 5, 0);
			for (int count = 0; count < 10; count++) {
				world.spawnEntity(loc, EntityType.FIREWORK);
				//fireball = (Fireball) world.spawnEntity(loc, EntityType.FIREBALL);
				//fireball.setDirection(new Vector(loc.getX(), loc.getY() + 10F, loc.getZ()));
			}
		}
	}
}

/*
 * @EventHandler public static void onEggThrown(PlayerEggThrowEvent event) {
 * Player player = event.getPlayer();
 * 
 * Location loc = player.getTargetBlock(null, 0).getLocation();
 * 
 * if (loc != null) { Sphere sphere = new Sphere();
 * player.getWorld().createExplosion(loc, 4F); sphere.Draw(loc, 5F,
 * Material.GLASS); } } }
 */
