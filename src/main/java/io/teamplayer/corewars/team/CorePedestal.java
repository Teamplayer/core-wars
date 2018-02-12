package io.teamplayer.corewars.team;

import io.teamplayer.teamcore.immutable.ImmutableLocation;
import org.bukkit.Location;

import java.util.Optional;

/**
 * The pedestals that teams have which hold their captured and owned RespawnCores
 */
class CorePedestal {

    private final ImmutableLocation location;

    private FloatingCore floatingCore;
    private RespawnCore holdingCore;
    private RespawnCore lastHeld;

    CorePedestal(Location location) {
        this.location = ImmutableLocation.from(location);
    }

    CorePedestal(Location location, RespawnCore holdingCore) {
        this(location);
        equipCore(holdingCore);
    }

    public ImmutableLocation getLocation() {
        return location;
    }

    public void equipCore(RespawnCore newCore) {
        holdingCore = newCore;
        lastHeld = newCore;

        final Location floatingLoc = location.mutable();
        floatingLoc.add(0,.5,0);
        floatingCore = new FloatingCore(floatingLoc, newCore);
    }

    public RespawnCore unequipCore() {
        final RespawnCore oldCore = holdingCore;
        holdingCore = null;

        floatingCore.destroy();

        return oldCore;
    }

    public boolean hasCore() {
        return holdingCore != null;
    }

    public Optional<RespawnCore> getEquippedCore() {
        return Optional.ofNullable(holdingCore);
    }

    RespawnCore getLastHeld() {
        return lastHeld;
    }

}
