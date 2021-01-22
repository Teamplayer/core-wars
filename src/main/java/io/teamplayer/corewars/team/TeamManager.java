package io.teamplayer.corewars.team;

import io.teamplayer.corewars.player.CorePlayer;
import io.teamplayer.corewars.player.PlayerGetter;
import io.teamplayer.corewars.util.ArrayUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages the assignment of players to their teams in the beginning of the game
 */
public class TeamManager {

    private final Map<TeamType, Queue<CorePlayer>> queues = new HashMap<>(4);
    private final Map<CorePlayer, TeamType> playerTeam = new HashMap<>();

    private final Map<TeamType, Team> teamFromType = new HashMap<>(4);
    private final List<Team> teams = new ArrayList<>();

    public TeamManager() {
        resizeTeams((byte) 4);
    }

    /**
     * Change the amount of teams that will be playing in the game
     *
     * @param amount new amount of teams
     */
    public void resizeTeams(byte amount) {

        if (!queues.isEmpty()) {
            emptyQueues();
            queues.clear();
        }

        TeamType[] newTeams = TeamType.values();
        ArrayUtil.shuffle(newTeams);

        for (int i = 0; i < amount; i++) {
            queues.put(newTeams[i], new LinkedList<>());
        }
    }

    /**
     * Remove a player from a certain team's member queue
     *
     * @param player the player to remove from the team
     */
    public void unqueuePlayer(CorePlayer player) {
        Optional<TeamType> team = getQueuedTeam(player);

        if (team.isPresent()) {
            queues.get(team.get()).remove(player);
            playerTeam.remove(player);
        }
    }

    /**
     * Queue a player up to be a member of a certain team
     *
     * @param team   the team to queue the player into
     * @param player the player to put in the team queue
     */
    public void queuePlayer(TeamType team, CorePlayer player) {
        queues.get(team).add(player);
        playerTeam.put(player, team);
    }

    /**
     * Get the team a player is queued for
     *
     * @param player player to get the team for
     * @return optional containing the team the player is queued for
     */
    private Optional<TeamType> getQueuedTeam(CorePlayer player) {
        return Optional.ofNullable(playerTeam.get(player));
    }

    /**
     * Get the team a player is a member of
     *
     * @param player the player to get the team of
     * @return an optional containing the team a player is on, if they're on one yet
     */
    public Optional<Team> getPlayerTeam(CorePlayer player) {
        return Optional.ofNullable(teamFromType.get(playerTeam.get(player)));
    }

    /**
     * Puts everyone on a team and creates the Team objects
     *
     * @param teamData the team data for all of the teams on the map
     */
    public void finalizeTeams(List<TeamData> teamData) {
        short extraPlayers =
                (short) (PlayerGetter.getActivePlayers().size() % getAmountOfTeams());

        final Queue<CorePlayer> unassigned =
                PlayerGetter.getActivePlayers()
                        .stream()
                        .filter(p -> !playerTeam.containsKey(p))
                        .collect(Collectors.toCollection(LinkedList::new));

        final List<TeamType> teams = new ArrayList<>(getAvailableTeams());
        teams.sort(
                Comparator.comparingInt(
                        t -> queues.get(t).size())); //Sort teams by amount of people queued for
        // them so that more desirable teams get to use the "extra" players

        Collections.shuffle(teamData);

        byte teamsLeft = getAmountOfTeams();

        for (TeamType teamType : teams) {
            final Queue<CorePlayer> teamQueue = queues.get(teamType);
            final List<CorePlayer> members = new LinkedList<>();
            short teamSize = getMinTeamPlayerCount();

            if (extraPlayers > 0 && (teamQueue.size() > teamSize || (extraPlayers >= teamsLeft))) {
                //Take one of the "extra" player slots if there is enough to fill the team or if
                teamSize++;
                extraPlayers--;
            }

            //Add as many people from queue as possible
            members.addAll(
                    teamQueue.stream()
                    .limit(teamSize)
                    .collect(Collectors.toList()));

            //Fill the teams unused slots with unassigned players
            while (members.size() < teamSize) {
                members.add(unassigned.poll());
            }

            final Team team = new Team(teamType, members, teamData.get(teamsLeft - 1));

            for (CorePlayer player : members) {
                playerTeam.put(player, teamType);
            }

            this.teams.add(team);
            teamFromType.put(teamType, team);

            teamsLeft--;
        }
    }

    /**
     * Unqueue every player from every team
     */
    private void emptyQueues() {
        for (TeamType team : queues.keySet()) {
            for (CorePlayer player : queues.get(team)) {
                unqueuePlayer(player);
            }
        }
    }

    /**
     * Get a collection of the TeamTypes that are available to be joined
     *
     * @return the TeamTypes that can be joined
     */
    public Collection<TeamType> getAvailableTeams() {
        return queues.keySet();
    }

    /**
     * Get a collection of all the finalized teams. This will return an empty collection if the
     * teams haven't been finalized yet
     *
     * @return a collection of all the teams
     */
    public Collection<Team> getAllTeams() {
        return new ArrayList<>(teams);
    }

    /**
     * Get a collection of all teams the are currently active, meaning the team is not eliminated
     * and has a chance to win
     *
     * @return collection of active teams
     */
    public Collection<Team> getActiveTeams() {
        return teams.stream()
                .filter(t -> !t.isEliminated())
                .collect(Collectors.toList());
    }

    /**
     * Get the amount of teams active in the game
     *
     * @return the amount of teams active in the game
     */
    public byte getAmountOfTeams() {
        if (queues.size() > 0) { //If there are team queues then teams have not yet been finalized
            return (byte) queues.keySet().size();
        } else {
            return (byte) teams.size();
        }
    }

    /**
     * Get the maximum amount of players that can be on one team at the current time based on the
     * current amount of players
     *
     * @return max amount of players allowed per team
     */
    public short getMaxTeamPlayerCount() {
        return (short) Math.ceil(PlayerGetter.getActivePlayers().size() / getAmountOfTeams());
    }

    /**
     * Get the minimum amount of players that can be on one team at the current time based on the
     * current amount of players
     *
     * @return minimum amount of players allowed per team
     */
    public short getMinTeamPlayerCount() {
        return (short) Math.floor(PlayerGetter.getActivePlayers().size() / getAmountOfTeams());
    }
}
