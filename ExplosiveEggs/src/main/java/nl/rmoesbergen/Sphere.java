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

	public double Radius;
	public Location Location;
	private BlockVector3 savedWorldeditLoc;

	public void Draw(Location loc, double radius, BlockType blockType) {
		BukkitWorld world = new BukkitWorld(loc.getWorld());
		Location = loc;
		Radius = radius;

		EditSession session = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1);
		savedWorldeditLoc = BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());

		try {
			session.makeSphere(savedWorldeditLoc, blockType.getDefaultState().toBaseBlock(), radius, false);
		} catch (MaxChangedBlocksException e) {
			e.printStackTrace();
		}
		session.commit();
		session.close();
	}

	public void Cleanup() {
		BukkitWorld world = new BukkitWorld(this.Location.getWorld());

		EditSession session = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1);
		try {
			session.makeSphere(savedWorldeditLoc, BlockTypes.AIR.getDefaultState().toBaseBlock(), Radius, false);
		} catch (MaxChangedBlocksException e) {
			e.printStackTrace();
		}
		session.commit();
		session.close();
	}


	public void CleanupAfter(Plugin plugin, int seconds) {
		SphereCleaner cleaner = new SphereCleaner(this);
		cleaner.runTaskLater(plugin, seconds * 20);
	}
}
