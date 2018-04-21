package nl.rmoesbergen.mcplugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

// Draw a 3D sphere in MC
public class Sphere {

	public void Draw(Location loc, double radius, Material material) {
		World world = loc.getWorld();
		double startX = loc.getX();
		double startY = loc.getY();
		double startZ = loc.getZ();

		for (double X = startX - radius; X < startX + radius; X++)
			for (double Y = startY - radius; Y < startY + radius; Y++)
				for (double Z = startZ - radius; Z < startZ + radius; Z++)

					if ((X - startX) * (X - startX) + (Y - startY) * (Y - startY)
							+ (Z - startZ) * (Z - startZ) <= (radius * radius)) {
						Location loc2 = new Location(world, X, Y, Z);
						world.getBlockAt(loc2).setType(material);
					}
	}
}
