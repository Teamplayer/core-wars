package io.teamplayer.corewars.player;

/**
 * The different stats a player can have for a game
 */
public enum StatType {
    KILLS("Kills"),
    FINAL_KILLS("Final Kills"),
    DEATHS("Deaths"),
    STEALS("Steals"),
    CAPTURES("Captures");

    private final String simpleName;

    StatType(String simpleName) {
        this.simpleName = simpleName;
    }

    public String getSimpleName() {
        return simpleName;
    }
}
