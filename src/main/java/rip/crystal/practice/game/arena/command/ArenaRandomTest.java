package rip.crystal.practice.game.arena.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.crystal.practice.api.command.BaseCommand;
import rip.crystal.practice.api.command.Command;
import rip.crystal.practice.api.command.CommandArgs;
import rip.crystal.practice.game.arena.Arena;
import rip.crystal.practice.game.kit.Kit;

public class ArenaRandomTest extends BaseCommand {

    @Command(name = "arena.randomtest", permission = "cpractice.arena.admin")
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();


        Kit kit = Kit.getByName(args[0]);
        if (kit == null) {
            player.sendMessage(ChatColor.RED + "A kit with that name does not exist.");
            return;
        }

        Arena arena = Arena.getRandomArena(kit);

        player.sendMessage("Test command reached end, for debug check latest.log");
    }
}
