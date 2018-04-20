package nl.rmoesbergen.mcplugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerMoveEvent;

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
		Player player = event.getPlayer();
		ChangeWorldTask task = new ChangeWorldTask(player);
		task.runTask(this);
	}
	
	
	@EventHandler
	public void onEggHits(ProjectileHitEvent event) {

		World world = event.getEntity().getWorld();

		if (event.getEntityType() == EntityType.EGG) {
			Location loc = event.getEntity().getLocation();

			Sphere sphere = new Sphere();
			world.createExplosion(loc, 4F);
			sphere.Draw(loc, 5F, Material.GLASS);
			loc.add(0, 5, 0);
			for (int count=0; count < 10; count++) {
				world.spawnEntity(loc, EntityType.FIREWORK);
				//Fireball fireball = (Fireball)world.spawnEntity(loc, EntityType.FIREBALL);
				//fireball.setDirection(new Vector(loc.getX(), loc.getY()+10F, loc.getZ()));
			}
		}
	}
}
	
/*	@EventHandler
	public static void onEggThrown(PlayerEggThrowEvent event) {
		Player player = event.getPlayer();

		Location loc = player.getTargetBlock(null, 0).getLocation();
		
		if (loc != null) {
			Sphere sphere = new Sphere();
			player.getWorld().createExplosion(loc, 4F);
			sphere.Draw(loc, 5F, Material.GLASS);
		}
	}
}*/
