package nl.rmoesbergen;

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
