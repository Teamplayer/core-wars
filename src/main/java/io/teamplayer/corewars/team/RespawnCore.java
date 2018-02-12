package io.teamplayer.corewars.team;

/**
 * The data belonging to a respawn core
 */
public class RespawnCore {

    private final Team originalTeam;

    private Team lastPossessor;

    RespawnCore(Team team) {
        this.originalTeam = team;
        lastPossessor = originalTeam;
    }

    private Team getOriginalTeam() {
        return originalTeam;
    }

    /** The team who own's the core in the event it is returned **/
    public Team getOwningTeam() {
        return getOriginalTeam();
    }

    /** The team who's color this respawn core will have **/
    public Team getVisualTeam() {
        return getOriginalTeam();
    }

    public Team getLastPossessor() {
        return lastPossessor;
    }

    void setLastPossessor(Team lastPossessor) {
        this.lastPossessor = lastPossessor;
    }
}
