package nl.rmoesbergen;

import org.bukkit.Location;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.world.block.BlockTypes;

// Draw a 3D sphere in MC
public class Sphere {

	public void Draw(Location loc, double radius) {
		BukkitWorld world = new BukkitWorld(loc.getWorld());
		double startX = loc.getX();
		double startY = loc.getY();
		double startZ = loc.getZ();

		EditSession session = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1);
		BlockVector3 we_loc = BlockVector3.at(startX, startY, startZ);

		try {
			session.makeSphere(we_loc, BlockTypes.GLASS.getDefaultState().toBaseBlock(), radius, false);
		} catch (MaxChangedBlocksException e) {
			e.printStackTrace();
		}
		session.commit();
		session.close();
	}
}
