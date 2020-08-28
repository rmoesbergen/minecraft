package nl.rmoesbergen;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class Scores {
    private static final Scoreboard KillsBoard = Bukkit.getScoreboardManager().getNewScoreboard();
    private static final Objective Hits = KillsBoard.registerNewObjective("Hits", "dummy", "Hits", RenderType.INTEGER);

    static {
        Hits.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public static void NewPlayer(Player player) {
        try {
            player.setScoreboard(KillsBoard);
            Score score = Hits.getScore(player.getName());
            score.setScore(0);
        } catch (IllegalStateException e) {
        }
    }

    public static void RemovePlayer(Player player) {
        if (player.getScoreboard().equals(KillsBoard)) {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
    }

    public static void AddHit(Player player) {
        Score score = Hits.getScore(player.getName());
        score.setScore(score.getScore() + 1);
    }
}
