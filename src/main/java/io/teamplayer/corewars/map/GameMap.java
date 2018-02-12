package io.teamplayer.corewars.map;

import com.google.common.collect.*;
import io.teamplayer.corewars.team.TeamData;
import io.teamplayer.corewars.util.SignFindingUtil;
import io.teamplayer.teamcore.immutable.ImmutableLocation;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents all of the data that's in the map for configing
 */
public class GameMap {

    /** A map containing all of the ConfigSigns in the map, ordered by type of config sign */
    private final ImmutableMultimap<LocationType, ConfigSign> signMap;
    /** The corresponding bukkit world */
    private final World bukkitWorld;
    /** All of the team data for the teams in the map */
    private final ImmutableCollection<TeamData> teamData;


    public GameMap(World bukkitWorld) {
        this.bukkitWorld = bukkitWorld;

        final ImmutableListMultimap.Builder<LocationType, ConfigSign> builder = ImmutableListMultimap.builder();

        for (Block block : SignFindingUtil.getLoadedSigns(bukkitWorld)) {
            String firstLine = ((Sign) block.getState()).getLine(0);

            if (firstLine.matches("\\[\\S+\\]")) { //Ensure first line of sign looks like '[this]'
                firstLine = firstLine.replaceAll("[\\[\\]]", ""); //Remove the brackets

                LocationType.getFromName(firstLine)
                        .ifPresent(t -> builder.put(t, ConfigSign.from(block)));

                block.setType(Material.AIR);
            }
        }

        signMap = builder.build();
        teamData = getTeam();
    }

    private ImmutableCollection<TeamData> getTeam() {
        final int teamAmount = signMap.entries().stream()
                .filter(e -> e.getKey().isTeamSpecific())
                .map(Map.Entry::getValue)
                .map(c -> c.getLine((byte) 1))
                .filter(StringUtils::isNumeric)
                .mapToInt(Integer::valueOf)
                .max()
                .getAsInt();

        final List<Multimap<LocationType, ConfigSign>> signs = new ArrayList<>();

        for (int i = 0; i < teamAmount + 1; i++) {
            signs.add(HashMultimap.create());
        }

        for (LocationType type : LocationType.values()) {
            if (!type.isTeamSpecific()) continue;

            for (ConfigSign sign : signMap.get(type)) {
                final String teamID = sign.getLine((byte) 1);

                if (!StringUtils.isNumeric(teamID)) {
                    Bukkit.getLogger().warning("The Sign:'" + sign.getLocation().toString() +
                            "' doesn't have a team ID");
                    continue;
                }

                signs.get(Integer.valueOf(teamID)).put(type, sign);
            }

        }

        final ImmutableList.Builder<TeamData> teamData = ImmutableList.builder();

        for (Multimap<LocationType, ConfigSign> signMultimap : signs) {

            final Function<LocationType, ImmutableLocation> toLocation = type ->
                    ImmutableLocation.from(Iterables.getOnlyElement(signMultimap.get(type))
                            .getLocation());

            final Function<LocationType, Set<ImmutableLocation>> toLocations = type ->
                    signMultimap.get(type).stream()
                            .map(c -> ImmutableLocation.from(c.getLocation()))
                            .collect(Collectors.toSet());

            final Function<LocationType, Set<Block>> toBlocks = type ->
                signMultimap.get(type).stream()
                        .map(ConfigSign::getBlock)
                        .collect(Collectors.toSet());

            final Function<LocationType, Set<Block>> toAttached = type ->
                    signMultimap.get(type).stream()
                            .map(ConfigSign::getAttachedBlock)
                            .collect(Collectors.toSet());

            final ImmutableLocation spawnLocation = toLocation.apply(LocationType.SPAWN);
            final ImmutableLocation startingHolo = toLocation.apply(LocationType.STARTING);
            final ImmutableLocation captureArea = toLocation.apply(LocationType.CAPTURE_AREA);

            final Set<ImmutableLocation> fireworkLocations = toLocations.apply(LocationType.FIREWORK);

            final Set<Block> chest = toBlocks.apply(LocationType.CORE);
            final Set<Block> bannerBlocks = toBlocks.apply(LocationType.BANNER);

            final Set<Block> coloredBlocks = toAttached.apply(LocationType.COLORED);
            final Set<Block> startingBarriers = toAttached.apply(LocationType.BARRIER);

            teamData.add(new TeamData(spawnLocation,coloredBlocks,chest, fireworkLocations, startingHolo,
                    startingBarriers, bannerBlocks,captureArea));
        }

        return teamData.build();
    }

    public World getWorld() {
        return bukkitWorld;
    }

    /**
     * Get all ConfigSigns of a certain type from the ConfigMap
     *
     * @param type type of config signs you want
     * @return an immutable collection of all of the config signs of that type
     */
    public ImmutableCollection<ConfigSign> getSigns(LocationType type) {
        return signMap.get(type);
    }

    public List<TeamData> getTeamData() {
        return new ArrayList<>(teamData);
    }
}
