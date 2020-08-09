package nl.rmoesbergen;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class ExplosiveEggs extends JavaPlugin implements Listener {

    private static final String METADATA_BOMB_RADIUS = "egg-bomb-radius";
    private static final String METADATA_JUMPING = "jumping";
    private static final double INITIAL_BOMB_SIZE = 5.0;
    private static final double MAX_BOMB_SIZE = 10.0;
    private static final String PERMISSION_THROW = "explosiveeggs.throw";
    private static final String PERMISSION_JUMP = "explosiveeggs.jump";
    private static final int EGG_COOLDOWN_TIME = 5 * 20;
    private static final int BOMB_CLEANUP_TIME = 10 * 20;

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

    @EventHandler public void onPlayerRespawn(PlayerRespawnEvent event) {
        playerSpawn(event.getPlayer());
    }

    @EventHandler public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        playerSpawn(event.getPlayer());
    }

    @EventHandler
    public void onPlayerSpawnLocation(PlayerSpawnLocationEvent event) {
        playerSpawn(event.getPlayer());
    }

    private void playerSpawn(Player player) {
        if (!player.hasPermission(PERMISSION_THROW)) return;

        player.setGameMode(GameMode.ADVENTURE);

        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        inventory.addItem(new ItemStack(Material.EGG, 3));
    }

    /* Disable player fall damage */
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            Player player = (Player) event.getEntity();
            if (!player.hasPermission(PERMISSION_JUMP)) return;

            if (event.getCause() == DamageCause.FALL || event.getCause() == DamageCause.BLOCK_EXPLOSION) {
                event.setCancelled(true);
            }
        }
    }

    private boolean increaseEggBombSize(Player player) {
        double currentValue = getEggBombSize(player);
        if (currentValue <= MAX_BOMB_SIZE) {
            player.setMetadata(METADATA_BOMB_RADIUS, new FixedMetadataValue(this, currentValue + 1));
            return true;
        }
        return false;
    }

    private double getEggBombSize(Player player) {
        if (player.hasMetadata(METADATA_BOMB_RADIUS)) {
            if (player.getMetadata(METADATA_BOMB_RADIUS).size() > 0) {
                return player.getMetadata(METADATA_BOMB_RADIUS).get(0).asDouble();
            }
        }
        player.setMetadata(METADATA_BOMB_RADIUS, new FixedMetadataValue(this, INITIAL_BOMB_SIZE));
        return INITIAL_BOMB_SIZE;
    }

    @EventHandler
    public void onEggHits(ProjectileHitEvent event) {

        World world = event.getEntity().getWorld();
        if (!(event.getEntityType() == EntityType.EGG)) return;

        Projectile egg = event.getEntity();
        if (!(egg.getShooter() instanceof Player)) return;

        Player player = (Player) egg.getShooter();
        if (!player.hasPermission(PERMISSION_THROW)) return;

        // Give a new egg after cooldown time
        GiveEggTask give_egg = new GiveEggTask(player);
        give_egg.runTaskLater(this, EGG_COOLDOWN_TIME);

        Location eggLocation = egg.getLocation();

        // Draw a glass sphere + explosion where the egg hits
        double radius = getEggBombSize(player);
        getLogger().info("Radius: " + radius);
        Sphere sphere = new Sphere(eggLocation, radius);
        sphere.Draw(false);
        sphere.CleanupAfter(this, BOMB_CLEANUP_TIME);

        world.createExplosion(eggLocation, 4F);
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
        if (!player.hasPermission(PERMISSION_JUMP)) return;

        if (event.getFrom().getY() < event.getTo().getY() && !player.hasMetadata(METADATA_JUMPING)) {
            player.setVelocity(event.getPlayer().getVelocity().add(new Vector(0, 1, 0)));
            player.setMetadata(METADATA_JUMPING, new FixedMetadataValue(this, true));
        } else if (player.isOnGround()) {
            player.removeMetadata(METADATA_JUMPING, this);
        }
    }
}
