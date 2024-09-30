package rip.crystal.practice.match.command;

import com.comphenix.protocol.PacketType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import rip.crystal.practice.api.command.BaseCommand;
import rip.crystal.practice.api.command.Command;
import rip.crystal.practice.api.command.CommandArgs;
import rip.crystal.practice.cPractice;
import rip.crystal.practice.player.profile.Profile;
import rip.crystal.practice.player.profile.ProfileState;
import rip.crystal.practice.player.profile.hotbar.Hotbar;
import rip.crystal.practice.player.profile.visibility.VisibilityLogic;
import rip.crystal.practice.utilities.chat.CC;

public class LeaveMatchCommand extends BaseCommand {

    @Command(name = "leave", aliases = {"l"})
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();

        Profile profile = Profile.get(player.getUniqueId());

        if(profile.getMatch() == null) {
            if(profile.getState() == ProfileState.SPECTATING || profile.getState() == ProfileState.FIGHTING || profile.getState() == ProfileState.EVENT || profile.getState() == ProfileState.FFA) {
                leaveSpectate(profile, player);
                return;
            }

            cPractice.get().getEssentials().teleportToSpawn(player);
            player.sendMessage(CC.GREEN + "You teleported to this world's spawn.");
            return;
        }


        for (String message : cPractice.get().getLang().getStringList("MATCH.CANCELLED")) {
            profile.getMatch().sendMessage(CC.translate(message.replaceAll("<cancelled_by>", player.getName())));
        }

        profile.getMatch().onDeath(player, true);
        //leaveGame(player);
        player.sendMessage(CC.translate("&cYou left your match."));
    }

    private void leaveSpectate(Profile profile, Player player) {
        if (profile.getState() == ProfileState.FIGHTING && profile.getMatch().getGamePlayer(player).isDead()) {
            profile.getMatch().getGamePlayer(player).setDisconnected(true);
            profile.setState(ProfileState.LOBBY);
            profile.setMatch(null);
        } else if (profile.getState() == ProfileState.SPECTATING) {
            profile.getMatch().removeSpectator(player);
        } else {
            player.sendMessage(CC.RED + "You are not spectating a match.");
        }
    }

    private void leaveGame(Player player) {
        VisibilityLogic.handle(player);
        Hotbar.giveHotbarItems(player);
        cPractice.get().getEssentials().teleportToSpawn(player);
    }
}