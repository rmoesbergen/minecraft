package nl.rmoesbergen.mcplugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ChangeWorldTask extends BukkitRunnable {

	public final Player player;

	public ChangeWorldTask(Player player) {
		this.player = player;
	}

	@Override
	public void run() {
		World world = player.getWorld();
		Location loc = player.getLocation();
		double startX = loc.getX();
		double startY = loc.getY();
		double startZ = loc.getZ();
		double radius = 20;
		
		
		for (double X = startX - radius; X < startX + radius; X++)
			for (double Y = startY - radius; Y < startY + radius; Y++)
				for (double Z = startZ - radius; Z < startZ + radius; Z++) {
					Location blockloc = new Location(world, X, Y, Z);
					Block block = world.getBlockAt(blockloc);

					if (block.getType() == Material.WATER)
						block.setType(Material.LAVA);
					if (block.getType() == Material.STATIONARY_WATER)
						block.setType(Material.STATIONARY_LAVA);
					if (block.getType() == Material.OBSIDIAN)
						block.setType(Material.STATIONARY_LAVA);
/*					if (block.getType() == Material.GRASS)
						block.setType(Material.TNT);
*/				}
	}

}