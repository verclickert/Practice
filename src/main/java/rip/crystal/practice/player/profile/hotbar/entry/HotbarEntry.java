package rip.crystal.practice.player.profile.hotbar.entry;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public class HotbarEntry {

    public ItemStack itemStack;
    private int slot;

}
