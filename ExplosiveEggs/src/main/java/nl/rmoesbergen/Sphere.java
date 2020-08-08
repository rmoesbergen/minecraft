package nl.rmoesbergen;

import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.Location;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import org.bukkit.plugin.Plugin;

// Draw a 3D sphere in MC
public class Sphere {

	private final double radius;
	private final BukkitWorld world;
	private final BlockVector3 location;

	public Sphere(Location location, double radius) {
		this.location = BlockVector3.at(location.getX(), location.getY(), location.getZ());
		this.radius = radius;
		this.world = new BukkitWorld(location.getWorld());
	}

	public void Draw(BlockType blockType) {
		EditSession session = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1);
		try {
			session.makeSphere(location, blockType.getDefaultState().toBaseBlock(), radius, true);
		} catch (MaxChangedBlocksException e) {
			e.printStackTrace();
		}
		session.commit();
		session.close();
	}

	public void Cleanup() {
		Draw(BlockTypes.AIR);
	}


	public void CleanupAfter(Plugin plugin, int seconds) {
		SphereCleaner cleaner = new SphereCleaner(this);
		cleaner.runTaskLater(plugin, seconds * 20);
	}
}
