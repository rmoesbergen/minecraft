package nl.rmoesbergen;

import java.util.Collection;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public final class ExplosiveEggs extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		Logger logger = getLogger();
		logger.info("ExplosiveEggs Enabled");

		getServer().getPluginManager().registerEvents(this, this);
		//this.registerRecipe();
	}

	@Override
	public void onDisable() {
		Logger logger = getLogger();
		logger.info("ExplosiveEggs Disabled");
	}

	/*
	private void registerRecipe() {
		ItemStack camera = new ItemStack(Material.LEGACY_SKULL_ITEM);
		ItemMeta meta = camera.getItemMeta();
		meta.setDisplayName("Security Camera");
		camera.setItemMeta(meta);
		NamespacedKey key = new NamespacedKey(this, "camera");
		ShapedRecipe recipe = new ShapedRecipe(key, camera);
		recipe.shape(" G ", " G ", "IRI");
		recipe.setIngredient('G', Material.GLASS);
		recipe.setIngredient('R', Material.REDSTONE);
		recipe.setIngredient('I', Material.IRON_INGOT);
		this.getServer().addRecipe(recipe);
	}
	*/

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

	/* Disable player fall damage */
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntityType() == EntityType.PLAYER) {
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
			sphere.Draw(loc, 5F);
			loc.add(0, 5, 0);
			for (int count = 0; count < 10; count++) {
				world.spawnEntity(loc, EntityType.FIREWORK);
			}
		}
	}

	/*
	@EventHandler
	public void onJump(PlayerMoveEvent event) {
		Player player = event.getPlayer();

		if (event.getFrom().getY() < event.getTo().getY() && !player.hasMetadata("jumping")) {
			player.setVelocity(event.getPlayer().getVelocity().add(new Vector(0, 1, 0)));
			player.setMetadata("jumping", new FixedMetadataValue(this, true));
		} else if (player.isOnGround()) {
			player.removeMetadata("jumping", this);
		}
	}
	*/

	/*
	 * @EventHandler public void onInteract(PlayerInteractEvent event) {
	 * 
	 * if (event.getAction() == Action.RIGHT_CLICK_BLOCK) { Location loc =
	 * event.getPlayer().getTargetBlock(null, 0).getLocation(); World world =
	 * event.getPlayer().getWorld();
	 * 
	 * int radius = 5; int y = loc.getBlockY(); for (int x = loc.getBlockX() -
	 * radius; x <= loc.getBlockX() + radius; x++) for (int z = loc.getBlockZ() -
	 * radius; z <= loc.getBlockZ() + radius; z++) { if (x == loc.getBlockX() -
	 * radius || z == loc.getBlockZ() - radius || x == loc.getBlockX() + radius || z
	 * == loc.getBlockZ() + radius) { Block block = world.getBlockAt(x, y, z);
	 * block.setType(Material.FENCE); } } } }
	 * 
	 */

	/*
	ArrayList<Location> cameraList = new ArrayList<Location>();

	@EventHandler
	public void onBlockPlaced(BlockPlaceEvent event) {
		Block block = event.getBlockPlaced();

		if (block.getType() == Material.SKELETON_SKULL) {
			Player player = event.getPlayer();

			ItemStack item = player.getInventory().getItemInMainHand();
			if (item != null & item.hasItemMeta()) {
				ItemMeta meta = item.getItemMeta();
				if (meta.getDisplayName().equalsIgnoreCase("Security Camera")) {
					cameraList.add(block.getLocation());
					player.setMetadata("currentcam", new FixedMetadataValue(this, 0));
				}
			}
		}
	}
	*/
}

