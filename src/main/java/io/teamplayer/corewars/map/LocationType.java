package io.teamplayer.corewars.map;

import java.util.Optional;

/**
 * The different types of locations present in a map
 */
public enum LocationType {
    BANNER("banner", true),
    SPAWN("spawn", true),
    FIREWORK("firework", true),
    COLORED("colored", true),
    CORE("core", true),
    BARRIER("barrier", true),
    STARTING("starting", true),
    CRYSTAL("crystal", false),
    CAPTURE_AREA("capture", true);

    private final String name;
    private final boolean teamSpecific;

    LocationType(String name, boolean teamSpecific) {
        this.name = name;
        this.teamSpecific = teamSpecific;
    }

    /**
     * Get a location type from a name
     *
     * @param name name to match the LocationType from
     * @return an optional containing the matching LocationType if there is one
     */
    public static Optional<LocationType> getFromName(String name) {
        LocationType returningType = null;

        for (LocationType type : values()) {
            if (type.name.equalsIgnoreCase(name)) {
                returningType = type;
                break;
            }
        }

        return Optional.ofNullable(returningType);
    }

    public boolean isTeamSpecific() {
        return teamSpecific;
    }
}
