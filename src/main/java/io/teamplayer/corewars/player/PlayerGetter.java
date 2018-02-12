package io.teamplayer.corewars.player;

import org.bukkit.Bukkit;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * A utility class for getting collections of players
 */
public final class PlayerGetter {

    private PlayerGetter() {
        throw new UnsupportedOperationException("This is a utility class");
    }

    /**
     * Return all the PiratePlayer objects for all online players
     */
    public static Collection<CorePlayer> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().stream()
                .map(CorePlayer::get)
                .collect(Collectors.toList());
    }

    /**
     * Returns a collection of all non-spectator players
     */
    public static Collection<CorePlayer> getActivePlayers() {
        return Bukkit.getOnlinePlayers().stream()
                .map(CorePlayer::get)
                .filter(p -> p.getState().isActive())
                .collect(Collectors.toList());
    }

    /**
     * Returns a collection of all players that are alive
     */
    public static Collection<CorePlayer> getAlivePlayers() {
        return Bukkit.getOnlinePlayers().stream()
                .map(CorePlayer::get)
                .filter(p -> p.getState() == CorePlayer.PlayerState.ALIVE)
                .collect(Collectors.toList());
    }

    /**
     * Returns a collection of all players that are spectating
     */
    public static Collection<CorePlayer> getSpectatorPlayers() {
        return Bukkit.getOnlinePlayers().stream()
                .map(CorePlayer::get)
                .filter(p -> p.getState().isSpectating())
                .collect(Collectors.toList());
    }
}
