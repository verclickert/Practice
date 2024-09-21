package rip.crystal.practice.essentials.command.player;

import org.bukkit.entity.Player;
import rip.crystal.practice.api.command.BaseCommand;
import rip.crystal.practice.api.command.Command;
import rip.crystal.practice.api.command.CommandArgs;
import rip.crystal.practice.game.kit.menu.KitEditorSelectKitMenu;
import rip.crystal.practice.player.profile.Profile;

public class AfkCommand extends BaseCommand {

    @Command(name = "afk")
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        Profile profile = Profile.get(player.getUniqueId());
        profile.setStatufAfk(!profile.isStatufAfk());
        if (profile.isStatufAfk()) {
            player.sendMessage("Activated AFK");
        } else {
            player.sendMessage("Deactivated AFK");
        }
    }
}
