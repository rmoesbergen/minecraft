package nl.rmoesbergen;

import java.util.logging.Level;
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

    private static final String METADATA_BOMB_RADIUS = "egg-bomb-radius";
    private static final String METADATA_JUMPING = "jumping";

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

            if (event.getCause() == DamageCause.FALL || event.getCause() == DamageCause.BLOCK_EXPLOSION) {
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

    private boolean increaseEggBombSize(Player player) {
        if (player.hasMetadata(METADATA_BOMB_RADIUS)) {
            double currentValue = player.getMetadata(METADATA_BOMB_RADIUS).get(0).asDouble();
            if (currentValue < 10F) {
                player.setMetadata(METADATA_BOMB_RADIUS, new FixedMetadataValue(this, currentValue + 1F));
                return true;
            }
        } else {
            player.setMetadata(METADATA_BOMB_RADIUS, new FixedMetadataValue(this, 6F));
            return true;
        }
        return false;
    }

    private double getEggBombSize(Player player) {
        if (player.hasMetadata(METADATA_BOMB_RADIUS)) {
            return player.getMetadata(METADATA_BOMB_RADIUS).get(0).asDouble();
        } else {
            return 5F;
        }
    }

    @EventHandler
    public void onEggHits(ProjectileHitEvent event) {

        World world = event.getEntity().getWorld();
        if (!(event.getEntityType() == EntityType.EGG)) return;

        Projectile egg = event.getEntity();
        if (!(egg.getShooter() instanceof Player)) return;

        Player player = (Player) egg.getShooter();
        if (!player.hasPermission("explosiveeggs.throw")) return;

        Location eggLocation = egg.getLocation();

        // Draw a glass sphere + explosion where the egg hits
        Sphere sphere = new Sphere();
        double radius = getEggBombSize(player);
        world.createExplosion(eggLocation, 4F);
        sphere.Draw(eggLocation, radius, BlockTypes.GLASS);
        sphere.CleanupAfter(this, 10);
        eggLocation.add(0, radius, 0);
        world.spawnEntity(eggLocation, EntityType.FIREWORK);

        for (Player hitPlayer: getServer().getOnlinePlayers()) {
            if (!hitPlayer.getWorld().getName().equals(player.getWorld().getName())) continue;
            if (hitPlayer.equals(player)) continue;

            double distance = eggLocation.distance(hitPlayer.getLocation());
            getServer().getLogger().log(Level.INFO, "Distance: " + distance);
            if (distance < (2 * radius)) {
                // Increase player's bomb size
                if (increaseEggBombSize(player)) {
                    player.sendTitle("You hit " + hitPlayer.getName() + "!", "Your bomb size has increased",10,30,20);
                } else {
                    player.sendTitle("You hit " + hitPlayer.getName() + "!", null,10,30,20);
                }

                // Reset hit player's bomb size
                hitPlayer.removeMetadata(METADATA_BOMB_RADIUS, this);
            }
        }
    }

    @EventHandler
    public void onJump(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("explosiveeggs.jump")) return;

        if (event.getFrom().getY() < event.getTo().getY() && !player.hasMetadata(METADATA_JUMPING)) {
            player.setVelocity(event.getPlayer().getVelocity().add(new Vector(0, 1, 0)));
            player.setMetadata(METADATA_JUMPING, new FixedMetadataValue(this, true));
        } else if (player.isOnGround()) {
            player.removeMetadata(METADATA_JUMPING, this);
        }
    }
}
