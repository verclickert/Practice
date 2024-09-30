package rip.crystal.practice.match.listeners.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import rip.crystal.practice.cPractice;
import rip.crystal.practice.match.Match;
import rip.crystal.practice.player.profile.Profile;
import rip.crystal.practice.player.profile.ProfileState;
import rip.crystal.practice.utilities.PlayerUtil;

public class MatchMoveListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        cPractice.get().getLogger().info("onPlayerMove(); called");
        /*Player player = event.getPlayer();
        Profile profile = Profile.get(player.getUniqueId());
        Match match = profile.getMatch();
        if (profile.getState() == ProfileState.FIGHTING) {
            if (match.getKit().getGameRules().isBridge() || match.getKit().getGameRules().isBedFight()) {
                if (player.getLocation().getBlockY() <= 30) {
                    Player killer = PlayerUtil.getLastAttacker(event.getPlayer());
                    match.sendDeathMessage(event.getPlayer(), killer);
                    profile.getMatch().onDeath(player);
                }
            }
        }*/
    }
}
