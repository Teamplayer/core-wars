package io.teamplayer.corewars.team;

import io.teamplayer.corewars.player.CorePlayer;
import io.teamplayer.corewars.util.BlockFindingUtil;
import io.teamplayer.corewars.util.LocationArea;
import io.teamplayer.corewars.util.LocationUtil;
import io.teamplayer.teamcore.util.ColoredBlockUtil;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The data for the state of each team
 */
public class Team {

    public static final byte STARTING_CORES = 3;

    private final TeamType teamType;
    private final List<CorePlayer> players = new ArrayList<>();
    private final TeamData teamData;
    private final CorePedestal[] pedestals = new CorePedestal[5];
    private final LocationArea spawnArea;

    private final StealDetectionArea stealDetectionArea;
    private final CaptureDetectionArea returnArea;

    private boolean coreStolen = false;
    private boolean teamEnabled = true;

    Team(TeamType type, Collection<CorePlayer> teamMembers, TeamData teamData) {
        this.teamType = type;
        this.teamData = teamData;
        players.addAll(teamMembers);

        //Setup Core Pedestals
        byte i = 0;
        for (Block block : teamData.getPedestals()) {
            if (i < STARTING_CORES) {
                pedestals[i] = new CorePedestal(LocationUtil.blockCenter(block), new RespawnCore
                        (this));
            } else {
                pedestals[i] = new CorePedestal(LocationUtil.blockCenter(block));
            }

            i++;
        }

        //Set pretty colored blocks
        teamData.getColoredBlocks().stream()
                .map(BlockFindingUtil::findExact)
                .flatMap(Collection::stream)
                .forEach(b ->
                    b.setType(ColoredBlockUtil.transformMaterialColor(b.getType(), teamType.getDyeColor())
                            .orElse(Material.WHITE_WOOL)));

        stealDetectionArea = new StealDetectionArea(teamData.getCaptureArea(), this);
        returnArea = new CaptureDetectionArea(teamData.getCaptureArea(), this);
        spawnArea = new LocationArea(teamData.getSpawn(), 1.5);
    }

    public TeamType getType() {
        return teamType;
    }

    /**
     * Get one of the team's spawns
     *
     * @return a team spawn point
     */
    public Location getSpawn() {
        return spawnArea.getLocation();
    }

    public void disableTeam() {
        teamEnabled = false;
    }

    public void enableTeam() {
        teamEnabled = true;
    }

    /**
     * Get a collection of player who've not been eliminated from the game
     *
     * @return a collection of active players
     */
    public Collection<CorePlayer> getActivePlayers() {
        return players.stream()
                .filter(p -> p.getPlayer().isOnline())
                .filter(p -> p.getState().isActive())
                .collect(Collectors.toList());
    }

    public boolean isEliminated() {
        return getActivePlayers().size() <= 0;
    }

    public int getAmountOfActiveCores() {
        return (int) Arrays.stream(pedestals)
                .map(CorePedestal::getEquippedCore)
                .filter(Optional::isPresent)
                .count();
    }

    public Optional<RespawnCore> getCore(int index) {
        if (index >= pedestals.length) {
            return Optional.empty();
        }

        return pedestals[index].getEquippedCore();
    }

    @Deprecated
    public boolean canRespawn() {
        return teamEnabled;
    }

    public boolean isTeamEnabled() {
        return teamEnabled;
    }

    /**
     * Get the amount of players that have not been eliminated
     */
    public byte playersActive() {
        return (byte) getActivePlayers().size();
    }

    public byte getEmptyPedestalAmount() {
        return (byte) (pedestals.length - getAmountOfActiveCores());
    }

    public boolean isCoreStolen() {
        return coreStolen;
    }

    public Optional<RespawnCore> getStolenCore() {
        if (coreStolen) {
            return Optional.ofNullable(getFirstEmpty().getLastHeld());
        } else {
            return Optional.empty();
        }

    }

    /**
     * Take a RespawnCore away from this team
     */
    RespawnCore stealCore() {
        final RespawnCore stolenCore = takeCore();
        coreStolen = true;

        //More cores cannot be stolen/captured while a team has a core stolen
        stealDetectionArea.disable();
        returnArea.disable();

        return stolenCore;
    }

    public RespawnCore takeCore() {
        final RespawnCore coreTaken = getLastFilled().unequipCore();

        if (getAmountOfActiveCores() <= 0) disableTeam();

        return coreTaken;
    }

    /**
     * Add a new RespawnCore to this team
     * @param newCore the core core to give to this team
     */
    void giveCore(RespawnCore newCore) {
        if (!teamEnabled) enableTeam();

        newCore.getLastPossessor().finalizeSteal();
        newCore.setLastPossessor(this);

        getFirstEmpty().equipCore(newCore);
    }

    /**
     * Notify this team that it's previously owned RespawnCore has been fully stolen
     */
    private void finalizeSteal() {
        coreStolen = false;

        //Cores can now be stolen/captured from/by this team again
        stealDetectionArea.enable();
        returnArea.enable();
    }

    private CorePedestal getFirstEmpty() {
        return Arrays.stream(pedestals)
                .filter(c -> !c.hasCore())
                .findFirst()
                .get();
    }

    private CorePedestal getLastFilled() {
        final CorePedestal[] pedestals = this.pedestals.clone();
        ArrayUtils.reverse(pedestals);

        return Arrays.stream(pedestals)
                .filter(CorePedestal::hasCore)
                .findFirst()
                .get();
    }

    /**
     * Get rid of the barriers that block players into the boat
     */
    public void dropBarriers() {
        teamData.getStartingBarriers().stream()
                .map(BlockFindingUtil::findExact)
                .flatMap(Collection::stream)
                .forEach(b -> b.setType(Material.AIR));
    }
}
