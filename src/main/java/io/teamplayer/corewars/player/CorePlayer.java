package io.teamplayer.corewars.player;

import com.comphenix.packetwrapper.WrapperPlayServerChat;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import io.teamplayer.corewars.CoreWars;
import io.teamplayer.corewars.game.GameStateManager;
import io.teamplayer.corewars.team.DroppedCore;
import io.teamplayer.corewars.team.RespawnCore;
import io.teamplayer.corewars.team.Team;
import io.teamplayer.corewars.util.ItemUtil;
import net.minecraft.server.v1_16_R3.ChatComponentText;
import net.minecraft.server.v1_16_R3.ChatMessageType;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class CorePlayer {

    private static final Map<UUID, CorePlayer> players = new HashMap<>();

    private final Player player;
    private final StatManager stats;

    private PlayerState state;
    private RespawnCore holdingCore;
    private boolean visible = true;
    private boolean frozen = false;

    private CorePlayer(Player player) {
        this.player = player;
        stats = new StatManager();

        state = GameStateManager.getInstance().getGameStage() == GameStateManager.GameStage.LOBBY
                ? PlayerState.ALIVE : PlayerState.SPECTATING;
    }

    /**
     * Get or create the CorePlayer associated with the player
     *
     * @param player Player of CorePlayer
     * @return CorePlayer related to the UUID
     */
    public static CorePlayer get(Player player) {
        return get(player.getUniqueId());
    }

    /**
     * Get or create the CorePlayer associated with the player's UUID
     *
     * @param uuid UUID of CorePlayer
     * @return CorePlayer related to the UUID
     */
    public static CorePlayer get(UUID uuid) {
        if (!players.containsKey(uuid)) {
            players.put(uuid, new CorePlayer(Bukkit.getPlayer(uuid)));
        }

        return players.get(uuid);
    }

    /** Get the bukkit player entity associated with this CorePlayer */
    public Player getPlayer() {
        return player;
    }

    /** Get this player's stat manager which contains the player's stats */
    public StatManager getStats() {
        return stats;
    }

    /**
     * Send a message to the player via their action bar
     *
     * @param message the message to send
     */
    public void sendActionBarMessage(String message) {
        final WrapperPlayServerChat packet = new WrapperPlayServerChat();
        packet.setChatType(EnumWrappers.ChatType.GAME_INFO);
        packet.setMessage(WrappedChatComponent.fromText(message));
        packet.sendPacket(player);
    }

    /**
     * Send a packet to the player
     *
     * @param packet a client bound packet
     */
    public void sendPacket(Packet packet) {
        ((CraftPlayer) getPlayer()).getHandle().playerConnection.sendPacket(packet);
    }


    /** Get the team this player belongs to */
    public Team getTeam() {
        return GameStateManager.getInstance().getTeamManager().getPlayerTeam(this).get();
    }

    public boolean hasTeam() {
        return GameStateManager.getInstance().getTeamManager().getPlayerTeam(this).isPresent();
    }

    /** Hide this player from all other players */
    public void hidePlayer() {
        if (!visible) return;
        visible = false;

        Bukkit.getOnlinePlayers().forEach(p -> p.hidePlayer(CoreWars.getInstance(), player));
    }

    /** Reveal this player to all other players if this player is hidden */
    public void showPlayer() {
        if (visible) return;
        visible = true;

        Bukkit.getOnlinePlayers().forEach(p -> p.showPlayer(CoreWars.getInstance(), player));
    }

    /** Whether or not this player can be seen by other players */
    public boolean isVisible() {
        return visible;
    }

    /** Makes the player unable to walk around or jump */
    public void freezePlayer() {
        if (frozen) return;
        frozen = true;

        player.setWalkSpeed(0); //Prevents walking
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 10000, 200));//Prevents jumps
    }

    /** Allow the player to walk around and jump after being frozen */
    public void unfreezePlayer() {
        if (!frozen) return;
        frozen = false;

        player.setWalkSpeed(getWalkSpeed());
        player.removePotionEffect(PotionEffectType.JUMP);
    }

    /** Whether or not the player can walk around and jump */
    public boolean isFrozen() {
        return frozen;
    }

    /** Get what this player's default walk speed should be */
    public float getWalkSpeed() {
        return 0.2F; //0.2 is the default minecraft walk speed
    }

    /** Get the player's current state */
    public PlayerState getState() {
        return state;
    }

    void setState(PlayerState state) {
        this.state = state;
    }

    /** Populate the player's inventory with what it should have */
    public void giveItems() {
        //Give them team colored armor.
        final ItemStack[] armor = new ItemStack[]{ //setArmorContents takes armor from the bottom up
                new ItemStack(Material.LEATHER_BOOTS),
                new ItemStack(Material.LEATHER_LEGGINGS),
                new ItemStack(Material.LEATHER_CHESTPLATE),
                new ItemStack(Material.LEATHER_HELMET),
        };

        for (ItemStack item : armor) {
            ItemUtil.colorLeatherArmor(item, getTeam().getType().getColor());
        }

        player.getInventory().setArmorContents(armor);

        //Give them a weapon
        player.getInventory().setItem(0, new ItemStack(Material.DIAMOND_SWORD));
    }

    /**
     * Give the player a respawn core and equip it
     *
     * @param newCore the core the player is taking
     */
    public void equipCore(RespawnCore newCore) {
        holdingCore = newCore;

        player.getInventory().setHelmet(new ItemStack(newCore.getOwningTeam().getType().getWoolType()));
    }

    /**
     * Take the respawn core away from the player and return the one they had equipped
     *
     * @return the core the player had equipped
     */
    public RespawnCore unequipCore() {
        final RespawnCore oldCore = holdingCore;
        holdingCore = null;

        player.getInventory().setHelmet(new ItemStack(Material.AIR));

        return oldCore;
    }

    /** Whether or not the player is currently holding a core */
    public boolean hasCore() {
        return holdingCore != null;
    }

    /** Get an optional containing the core the player has equipped if there is one */
    public Optional<RespawnCore> getEquippedCore() {
        return Optional.ofNullable(holdingCore);
    }

    /** Drop the core the player is currently holding onto the ground */
    public void dropCore() {
        new DroppedCore(unequipCore(), player.getLocation());
    }

    public enum PlayerState {
        ALIVE,
        DEAD,
        ELIMINATED,
        SPECTATING;

        /** Whether or not the player is still in the game */
        public boolean isActive() {
            return this != SPECTATING && this != ELIMINATED;
        }

        /** Whether or not the player is just a spectator and can no longer influence the game */
        public boolean isSpectating() {
            return !isActive();
        }

        /** Whether or not the player can interact with the world or be seen */
        public boolean canInteract() {
            return this == ALIVE;
        }
    }
}
