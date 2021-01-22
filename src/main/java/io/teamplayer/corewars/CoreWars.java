package io.teamplayer.corewars;

import com.comphenix.packetwrapper.WrapperPlayServerEntityStatus;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.*;
import io.teamplayer.corewars.command.StartCommand;
import io.teamplayer.corewars.game.GameStateManager;
import io.teamplayer.corewars.listener.CancelListener;
import io.teamplayer.corewars.listener.JoinListener;
import io.teamplayer.corewars.listener.LeaveListener;
import io.teamplayer.corewars.lobby.CountdownManager;
import io.teamplayer.corewars.player.DeathHandler;
import io.teamplayer.corewars.scoreboard.LobbyFrame;
import io.teamplayer.teamcore.scoreboard.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class CoreWars extends JavaPlugin {

    public static final String NAME = "TreasureNab";
    /** Minimum amount of players to start a game */
    public static final short MIN_COUNT = 2;
    /** Maximum amount of players allowed in a game */
    public static final short MAX_COUNT = 24;

    public static final ChatColor PRI_COLOR = ChatColor.RED;
    public static final ChatColor SEC_COLOR = ChatColor.YELLOW;

    private static CoreWars instance;

    private ScoreboardManager scoreboardManager;
    private CountdownManager countdownManager;

    @Override
    public void onEnable() {
        instance = this;

        GameStateManager.getInstance(); //Creates a new instance of the GameManager
        scoreboardManager = new ScoreboardManager(this,
                Bukkit.getScoreboardManager().getMainScoreboard());

        countdownManager = new CountdownManager();

        Bukkit.getPluginManager().registerEvents(countdownManager, this);
        Bukkit.getPluginManager().registerEvents(new DeathHandler(), this);
        Bukkit.getPluginManager().registerEvents(new CancelListener(), this);
        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new LeaveListener(), this);

        Bukkit.getPluginCommand("start").setExecutor(new StartCommand());

        scoreboardManager.setGlobalFrame(new LobbyFrame());
    }

    /** Get the instance of the loaded JavaPlugin */
    public static CoreWars getInstance() {
        return instance;
    }

    /** Get the scoreboard manager associated with this game */
    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    /** Get the countdown manager associated with this game */
    public CountdownManager getCountdownManager() {
        return countdownManager;
    }
}
