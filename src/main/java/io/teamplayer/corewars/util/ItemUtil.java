package io.teamplayer.corewars.util;

import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 * A utility class for manipulating ItemStacks
 */
public final class ItemUtil {

    private ItemUtil() {
        throw new UnsupportedOperationException("This is a utility class");
    }

    public static void colorLeatherArmor(ItemStack leatherArmor, Color color) {
        Validate.isTrue(leatherArmor.getType().name().contains("LEATHER_"),
                "Item must be leather armor to be colored");

        final LeatherArmorMeta meta = ((LeatherArmorMeta) leatherArmor.getItemMeta());
        meta.setColor(color);

        leatherArmor.setItemMeta(meta);
    }

}
