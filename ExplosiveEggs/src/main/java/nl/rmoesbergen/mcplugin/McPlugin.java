package nl.rmoesbergen.mcplugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import nl.rmoesbergen.mcplugin.Sphere;

import java.util.Collection;
import java.util.logging.Logger;

public final class McPlugin extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		Logger logger = getLogger();
		logger.info("McPlugin Enabled");

		getServer().getPluginManager().registerEvents(this, this);
		this.registerRecipe();
	}

	@Override
	public void onDisable() {
		Logger logger = getLogger();
		logger.info("McPlugin Disabled");
	}

	private void registerRecipe() {
		ItemStack camera = new ItemStack(Material.SKULL_ITEM);
		ItemMeta meta = camera.getItemMeta();
		meta.setDisplayName("Security Camera");
		camera.setItemMeta(meta);
		NamespacedKey key = new NamespacedKey(this, "camera");
		ShapedRecipe recipe = new ShapedRecipe(key, camera);
		recipe.shape(" G ", " G ", " R ");
		recipe.setIngredient('G', Material.GLASS);
		recipe.setIngredient('R', Material.REDSTONE);
		this.getServer().addRecipe(recipe);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;

			if (cmd.getName().equalsIgnoreCase("unfreeze")) {
				player.removeMetadata("frozen", this);
				player.setGameMode(GameMode.SURVIVAL);
				player.setGravity(true);
				return true;
			}

			if (!player.isOp())
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

				devilEnabled = !devilEnabled;
				player.sendMessage("Devil mode for " + player.getName() + " is now " + devilEnabled);
				if (devilEnabled)
					player.setMetadata("devil", new FixedMetadataValue(this, devilEnabled));
				else
					player.removeMetadata("devil", this);
				return true;
			}
		}
		return false;
	}

	/* Disable player fall damage */
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

		if (!player.hasMetadata("devil"))
			return;

		ChangeWorldTask task = new ChangeWorldTask(player);
		task.runTask(this);
	}

	@EventHandler
	public void onEggHits(ProjectileHitEvent event) {

		World world = event.getEntity().getWorld();

		if (event.getEntityType() == EntityType.EGG) {

			Projectile egg = event.getEntity();
			if (egg.getShooter() instanceof Player) {
				Player player = (Player) egg.getShooter();

				if (!player.hasMetadata("devil")) {
					return;
				}
			}

			Location loc = event.getEntity().getLocation();

			Sphere sphere = new Sphere();
			world.createExplosion(loc, 4F);
			sphere.Draw(loc, 5F, Material.GLASS);
			loc.add(0, 5, 0);
			for (int count = 0; count < 10; count++) {
				world.spawnEntity(loc, EntityType.FIREWORK);
				// fireball = (Fireball) world.spawnEntity(loc, EntityType.FIREBALL);
				// fireball.setDirection(new Vector(loc.getX(), loc.getY() + 10F, loc.getZ()));
			}
		}
	}

	@EventHandler
	public void onJump(PlayerMoveEvent event) {
		Player player = event.getPlayer();

		if (player.hasMetadata("frozen"))
			event.setCancelled(true);

		if (event.getFrom().getY() < event.getTo().getY() && !player.hasMetadata("jumping")) {
			player.setVelocity(event.getPlayer().getVelocity().add(new Vector(0, 1, 0)));
			player.setMetadata("jumping", new FixedMetadataValue(this, true));
		} else if (player.isOnGround()) {
			player.removeMetadata("jumping", this);
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Location loc = event.getPlayer().getTargetBlock(null, 0).getLocation();
			World world = event.getPlayer().getWorld();

			int radius = 5;
			int y = loc.getBlockY();
			for (int x = loc.getBlockX() - radius; x <= loc.getBlockX() + radius; x++)
				for (int z = loc.getBlockZ() - radius; z <= loc.getBlockZ() + radius; z++) {
					if (x == loc.getBlockX() - radius || z == loc.getBlockZ() - radius || x == loc.getBlockX() + radius
							|| z == loc.getBlockZ() + radius) {
						Block block = world.getBlockAt(x, y, z);
						block.setType(Material.FENCE);

						// Location curloc = new Location(world, x, y , z);
						// world.spawnFallingBlock(curloc, Material.FENCE, (byte)0);
					}
				}
		}
	}

	@EventHandler
	public void onBlockPlaced(BlockPlaceEvent event) {
		Block block = event.getBlockPlaced();

		if (block.getType() == Material.SKULL) {
			Player player = event.getPlayer();
			player.teleport(block.getLocation());

			ItemStack item = player.getInventory().getItemInMainHand();
			if (item != null & item.hasItemMeta()) {
				ItemMeta meta = item.getItemMeta();
				if (meta.getDisplayName().equalsIgnoreCase("Security Camera")) {
					player.setMetadata("frozen", new FixedMetadataValue(this, true));
					player.setGameMode(GameMode.SPECTATOR);
				}
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
