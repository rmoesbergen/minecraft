package nl.rmoesbergen;

import java.util.logging.Logger;

import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public final class ExplosiveEggs extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Logger logger = getLogger();
        logger.info("ExplosiveEggs Enabled");

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        Logger logger = getLogger();
        logger.info("ExplosiveEggs Disabled");
    }

	/*
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;

			if (!player.hasPermission("explosiveeggs.godmode"))
				return false;

			if (cmd.getName().equalsIgnoreCase("devil")) {
				Collection<? extends Player> players = this.getServer().getOnlinePlayers();

				if (args.length > 0) {
					// Username achter command getikt
					for (Player p : players) {
						if (p.getName().equalsIgnoreCase(args[0])) {
							player.sendMessage("You set devil mode for " + p.getName());
							player = p;
							break;
						}
					}
				}

				boolean devilEnabled = player.hasMetadata("devil");

				if (player.hasMetadata("god"))
					return false;

				devilEnabled = !devilEnabled;
				player.sendMessage("Devil mode for " + player.getName() + " is now " + devilEnabled);
				if (devilEnabled)
					player.setMetadata("devil", new FixedMetadataValue(this, devilEnabled));
				else
					player.removeMetadata("devil", this);
				return true;
			}

			if (cmd.getName().equalsIgnoreCase("god")) {
				Collection<? extends Player> players = this.getServer().getOnlinePlayers();

				if (args.length > 0) {
					// Username achter command getikt
					for (Player p : players) {
						if (p.getName().equalsIgnoreCase(args[0])) {
							player.sendMessage("You set god mode for " + p.getName());
							player = p;
							break;
						}
					}
				}

				boolean godEnabled = player.hasMetadata("god");

				if (player.hasMetadata("devil"))
					return false;

				godEnabled = !godEnabled;
				player.sendMessage("God mode for " + player.getName() + " is now " + godEnabled);
				if (godEnabled)
					player.setMetadata("god", new FixedMetadataValue(this, godEnabled));
				else
					player.removeMetadata("god", this);
				return true;
			}

		}
		return false;
	}
	*/

    /* Disable player fall damage */
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            Player player = (Player) event.getEntity();
            if (!player.hasPermission("explosiveeggs.jump")) return;

            if (event.getCause() == DamageCause.FALL) {
                event.setCancelled(true);
            }
        }
    }

	/*
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {

		Player player = event.getPlayer();

		if (player.hasMetadata("frozen"))
			event.setCancelled(true);

		ChangeWorldTask task = new ChangeWorldTask(player);
		task.runTask(this);
	}
	*/

    @EventHandler
    public void onEggHits(ProjectileHitEvent event) {

        World world = event.getEntity().getWorld();

        if (event.getEntityType() == EntityType.EGG) {

            Projectile egg = event.getEntity();
            if (!(egg.getShooter() instanceof Player)) return;

            Player player = (Player) egg.getShooter();
            if (!player.hasPermission("explosiveeggs.throw")) return;

            Location loc = event.getEntity().getLocation();

            Sphere sphere = new Sphere();
            world.createExplosion(loc, 4F);
            sphere.Draw(loc, 5F, BlockTypes.GLASS);
            loc.add(0, 5, 0);
            for (int count = 0; count < 3; count++) {
                world.spawnEntity(loc, EntityType.FIREWORK);
            }
        }
    }

    @EventHandler
    public void onJump(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("explosiveeggs.jump")) return;

        if (event.getFrom().getY() < event.getTo().getY() && !player.hasMetadata("jumping")) {
            player.setVelocity(event.getPlayer().getVelocity().add(new Vector(0, 1, 0)));
            player.setMetadata("jumping", new FixedMetadataValue(this, true));
        } else if (player.isOnGround()) {
            player.removeMetadata("jumping", this);
        }
    }
}
