package nl.rmoesbergen;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.selector.SphereRegionSelector;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.Collections;

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

	public void Draw(boolean clear) {
		EditSession session = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1);

		SphereRegionSelector selection = new SphereRegionSelector(world);
		selection.selectPrimary(location, null);
		selection.selectSecondary(location.add(0,(int) radius,0), null);

		BaseBlock fromBlock, toBlock;

		if (clear) {
			fromBlock = BlockTypes.GLASS.getDefaultState().toBaseBlock();
			toBlock = BlockTypes.AIR.getDefaultState().toBaseBlock();
		} else {
			fromBlock = BlockTypes.AIR.getDefaultState().toBaseBlock();
			toBlock = BlockTypes.GLASS.getDefaultState().toBaseBlock();		}

		try {
			session.replaceBlocks(selection.getRegion(), Collections.singleton(fromBlock), toBlock);
		} catch (IncompleteRegionException | MaxChangedBlocksException e) {
			e.printStackTrace();
		}

		session.commit();
		session.close();
	}

	public void Cleanup() {
		Draw(true);
	}


	public void CleanupAfter(Plugin plugin, int seconds) {
		SphereCleaner cleaner = new SphereCleaner(this);
		cleaner.runTaskLater(plugin, seconds * 20);
	}
}
