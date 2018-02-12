package io.teamplayer.corewars.game;

import com.google.common.collect.Iterables;
import io.teamplayer.corewars.CoreWars;
import io.teamplayer.corewars.map.GameMap;
import io.teamplayer.corewars.player.CorePlayer;
import io.teamplayer.corewars.player.PlayerGetter;
import io.teamplayer.corewars.scoreboard.GameFrame;
import io.teamplayer.corewars.team.Team;
import io.teamplayer.corewars.team.TeamManager;
import io.teamplayer.corewars.util.task.TaskTimer;
import io.teamplayer.corewars.util.task.TimeRemainingLine;
import io.teamplayer.teamcore.message.Title;
import io.teamplayer.teamcore.util.TeamRunnable;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controls the game's changes from lobby, inprogrsss, to ending
 */
public final class GameStateManager {

    private static GameStateManager instance;

    private final TimeRemainingLine countdownLine = new TimeRemainingLine();
    private TaskTimer gameTask;

    private final TeamManager teamManager = new TeamManager();

    private GameMap gameMap;
    private GameStage gameStage = GameStage.LOBBY;

    private GameStateManager() {
    }


    public void startGame() {
        Validate.notNull(gameMap, "Map has not been loaded");

        teamManager.finalizeTeams(gameMap.getTeamData());

        for (CorePlayer player : PlayerGetter.getActivePlayers()) {
            player.getPlayer().getInventory().clear();
            player.giveItems();

            if (player.hasTeam()) {
                player.getPlayer().teleport(player.getTeam().getSpawn());
            }
        }

        final Title title = new Title()
                .setDuration((int) (20 * 1.5))
                .setFadeOut(5)
                .setTitle(ChatColor.GREEN + "Start!");

        for (Player player : Bukkit.getOnlinePlayers()) {
            title.send(player);
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1, 1);
        }

        gameStage = GameStage.IN_PROGRESS;

        CoreWars.getInstance().getScoreboardManager().setGlobalFrame(new GameFrame());

        gameTask = new ClaimTask();
    }

    /** Check to see if all but one team is eliminated */
    public void checkForWin() {
        if (teamManager.getActiveTeams().size() == 1) {
            endGame(Iterables.getOnlyElement(teamManager.getActiveTeams()));
        }
    }

    private void endGame(Team winningTeam) {
        gameStage = GameStage.ENDING;

        for (CorePlayer player : PlayerGetter.getOnlinePlayers()) {
            if (player.hasTeam()) {
                final Team playerTeam = player.getTeam();

                String message = playerTeam.equals(winningTeam) ? "You won! Hooray!" :
                        "You lost." + " Sad.";

                player.getPlayer().sendTitle(message,
                        winningTeam.getType().name() + " TEAM " + "WON", 0, 5 * 20, 0);
            }
        }

        ((TeamRunnable) Bukkit::shutdown).runTaskLater(CoreWars.getInstance(), 10 * 20);

    }

    /** Decides what the next main game task is */
    protected void nextTask() {
        final List<Team> teamsWithCores = teamManager.getActiveTeams().stream()
                .filter(Team::isTeamEnabled)
                .collect(Collectors.toList());

        if (teamsWithCores.size() > 0) {
            gameTask = new ClaimTask();
        }
    }

    /** Get the scoreboard line that's linked to the current gametask */
    public TimeRemainingLine getCountdownLine() {
        return countdownLine;
    }

    /**
     * Get the single instance of the GameManager by getting the current one or by creating a new
     * one if one doesn't already exist
     *
     * @return a new or the current GameManager
     */
    public static GameStateManager getInstance() {
        if (instance == null) instance = new GameStateManager();

        return instance;
    }

    /** Get the current stage the game is in */
    public GameStage getGameStage() {
        return gameStage;
    }

    /** Get the current TeamManager for the game */
    public TeamManager getTeamManager() {
        return teamManager;
    }

    /** Load all of the map locations */
    public void loadMap() {
        gameMap = new GameMap(Bukkit.getWorlds().get(0));
    }

    /** Get the current game map */
    public GameMap getGameMap() {
        return gameMap;
    }

    public enum GameStage {
        LOBBY(true), IN_PROGRESS(true), ENDING(false);

        public final boolean joinable;

        GameStage(boolean joinable) {
            this.joinable = joinable;
        }
    }

}
