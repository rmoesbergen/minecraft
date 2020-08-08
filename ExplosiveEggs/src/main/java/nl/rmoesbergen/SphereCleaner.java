package nl.rmoesbergen;

import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.scheduler.BukkitRunnable;

public class SphereCleaner extends BukkitRunnable {

    private final Sphere Sphere;

    public SphereCleaner(Sphere sphere) {
        this.Sphere = sphere;
    }

    @Override
    public void run() {
        this.Sphere.Cleanup();
    }

}
