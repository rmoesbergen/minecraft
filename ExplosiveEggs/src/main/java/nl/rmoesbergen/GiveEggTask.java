package nl.rmoesbergen;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class GiveEggTask extends BukkitRunnable {

    private final Player player;

    public GiveEggTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (!player.isOnline()) return;
        player.getInventory().addItem(new ItemStack(Material.EGG, 1));
    }
}
