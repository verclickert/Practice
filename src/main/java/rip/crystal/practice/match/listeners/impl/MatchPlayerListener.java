package rip.crystal.practice.match.listeners.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.TrapDoor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.crystal.practice.Locale;
import rip.crystal.practice.game.kit.KitLoadout;
import rip.crystal.practice.match.Match;
import rip.crystal.practice.match.MatchState;
import rip.crystal.practice.match.impl.BasicTeamMatch;
import rip.crystal.practice.match.menu.ViewInventoryMenu;
import rip.crystal.practice.player.profile.Profile;
import rip.crystal.practice.player.profile.ProfileState;
import rip.crystal.practice.player.profile.hotbar.Hotbar;
import rip.crystal.practice.player.profile.hotbar.impl.HotbarItem;
import rip.crystal.practice.utilities.KitUtils;
import rip.crystal.practice.utilities.MessageFormat;
import rip.crystal.practice.utilities.TimeUtil;
import rip.crystal.practice.utilities.chat.CC;

import java.util.regex.Matcher;

public class MatchPlayerListener implements Listener {

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent playerInteractEvent) {
        Profile profile;
        Player player = playerInteractEvent.getPlayer();
        ItemStack itemStack = playerInteractEvent.getItem();
        if ((playerInteractEvent.getAction() == Action.RIGHT_CLICK_AIR || playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK) && (profile = Profile.get(player.getUniqueId())).getState() == ProfileState.FIGHTING) {
            Match match = profile.getMatch();
            if (itemStack != null) {System.out.println("MPL:L39");
                if (Hotbar.fromItemStack(itemStack) == HotbarItem.SPECTATE_STOP) {
                    match.onDisconnect(player);
                    return;
                }
                if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
                    Matcher matcher;
                    matcher = HotbarItem.KIT_SELECTION.getPattern().matcher(itemStack.getItemMeta().getDisplayName());
                    ItemStack itemStack2 = Hotbar.getItems().get(HotbarItem.KIT_SELECTION).getItemStack();
                    System.out.println("itemStack1 Type: " + itemStack.getType());
                    System.out.println("itemStack1 Durability: " + itemStack.getDurability());
                    System.out.println("itemStack1 Display Name: " + itemStack.getItemMeta().getDisplayName());
                    System.out.println("matcher.find(): " + matcher.find());
                    System.out.println(matcher);

                    System.out.println("itemStack2 Type: " + itemStack2.getType());
                    System.out.println("itemStack2 Durability: " + itemStack2.getDurability());
                    System.out.println("itemStack2 Display Name: " + itemStack2.getItemMeta().getDisplayName());
                    if (itemStack.getType() == itemStack2.getType() && itemStack.getDurability() == itemStack2.getDurability() && matcher.find()) {
                        System.out.println("MPL:L48");
                        String kitName = matcher.group(2);
                        KitLoadout kitLoadout = null;
                        System.out.println("MPL:L51:" + kitName);
                        if (kitName.equalsIgnoreCase("Default")) {
                            kitLoadout = match.getKit().getKitLoadout();
                        } else {
                            for (KitLoadout kitLoadout2 : profile.getKitData().get(match.getKit()).getLoadouts()) {
                                if (kitLoadout2 == null || !kitLoadout2.getCustomName().equals(kitName)) continue;
                                kitLoadout = kitLoadout2;
                            }
                        }
                        System.out.println("MPL:L59");
                        if (kitLoadout != null) {
                            System.out.println("MPL:L62");
                            new MessageFormat(Locale.MATCH_GIVE_KIT.format(profile.getLocale())).add("<kit_name>", kitLoadout.getCustomName()).send(player);
                            if (match.getKit().getGameRules().isBridge() || match.getKit().getGameRules().isHcftrap()) {
                                if(match.getKit().getGameRules().isBridge()) {
                                    player.getInventory().setContents(kitLoadout.getContents());
                                    KitUtils.giveBridgeKit(player);
                                    profile.setSelectedKit(kitLoadout);
                                } else {
                                    player.getInventory().setContents(kitLoadout.getContents());
                                    KitUtils.giveBaseRaidingKit(player);
                                    profile.setSelectedKit(kitLoadout);
                                }
                            } else {
                                player.getInventory().setArmorContents(kitLoadout.getArmor());
                                player.getInventory().setContents(kitLoadout.getContents());
                            }

                            player.updateInventory();
                            playerInteractEvent.setCancelled(true);
                        }
                    }
                }
                if (itemStack.getType() == Material.ENDER_PEARL) {
                    if (match.getState() != MatchState.PLAYING_ROUND) {
                        player.sendMessage(CC.RED + "You can't throw pearls right now!");
                        playerInteractEvent.setCancelled(true);
                        return;
                    }
                }

                if (playerInteractEvent.getAction() != Action.RIGHT_CLICK_BLOCK && playerInteractEvent.getAction() != Action.RIGHT_CLICK_AIR || !playerInteractEvent.hasItem()) {
                    return;
                }

                if (playerInteractEvent.getItem().getType() == Material.ENDER_PEARL) {
                    if (!profile.getEnderpearlCooldown().hasExpired()) {
                        playerInteractEvent.setCancelled(true);
                        String time = TimeUtil.millisToSeconds(profile.getEnderpearlCooldown().getRemaining());
                        new MessageFormat(Locale.MATCH_ENDERPEARL_COOLDOWN.format(profile.getLocale())).add("<context>", (time.equalsIgnoreCase("1.0") ? "" : "s")).add("<time>", time).send(player);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteractHCFTrap(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.get(player.getUniqueId());
        Match match = profile.getMatch();

        if (match == null) return;
        if (profile.getState() == ProfileState.SPECTATING) event.setCancelled(true);

        if (match instanceof BasicTeamMatch) {
            BasicTeamMatch teamMatch = (BasicTeamMatch) match;
            if (teamMatch.getKit().getGameRules().isHcftrap()) {

                if (teamMatch.getParticipantB().containsPlayer(player.getUniqueId())) {
                    if (event.getPlayer().isSneaking() && event.getItem().getType() == Material.ENDER_PEARL && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType().name().contains("FENCE")) {
                        return;
                    }
                }
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK && (event.getClickedBlock().getType().name().contains("FENCE") || event.getClickedBlock().getState() instanceof TrapDoor || event.getClickedBlock().getType().name().contains("TRAP") || event.getClickedBlock().getType().name().contains("CHEST") || event.getClickedBlock().getType().name().contains("DOOR") || event.getClickedBlock().getType().equals(Material.BEACON) || event.getClickedBlock().getType().equals(Material.FURNACE) || event.getClickedBlock().getType().equals(Material.WORKBENCH) || event.getClickedBlock().getType().equals(Material.NOTE_BLOCK) || event.getClickedBlock().getType().equals(Material.JUKEBOX) || event.getClickedBlock().getType().equals(Material.ANVIL) || event.getClickedBlock().getType().equals(Material.HOPPER) || event.getClickedBlock().getType().equals(Material.BED_BLOCK) || event.getClickedBlock().getType().equals(Material.DROPPER) || event.getClickedBlock().getType().equals(Material.BREWING_STAND))) {
                    if (teamMatch.getParticipantA().containsPlayer(player.getUniqueId())) {
                        if (teamMatch.getChangedBlocks().stream().noneMatch(blockState -> blockState.getBlock().getLocation().equals(event.getClickedBlock().getLocation()))) {
                            teamMatch.getChangedBlocks().add(event.getClickedBlock().getState());
                        }
                    } else {
                        player.sendMessage(CC.translate("&cYou cannot interact as a raider."));
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Profile profile = Profile.get(event.getPlayer().getUniqueId());
        Player player = event.getPlayer();

        if (profile.getState() == ProfileState.FIGHTING) {
            Match match = profile.getMatch();

            if (match.getState() == MatchState.STARTING_ROUND || match.getState() == MatchState.PLAYING_ROUND) {
                profile.getMatch().onDisconnect(player);
            }
        }
    }

    @EventHandler
    public void onPlayerKickEvent(PlayerKickEvent event) {
        Profile profile = Profile.get(event.getPlayer().getUniqueId());

        if (profile.getState() == ProfileState.FIGHTING) {
            Match match = profile.getMatch();

            if (match.getState() == MatchState.STARTING_ROUND || match.getState() == MatchState.PLAYING_ROUND) {
                profile.getMatch().onDisconnect(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.get(player.getUniqueId());

        if (profile.getState() == ProfileState.SPECTATING && event.getRightClicked() instanceof Player &&
                player.getItemInHand() != null) {
            Player target = (Player) event.getRightClicked();

            if (Hotbar.fromItemStack(player.getItemInHand()) == HotbarItem.VIEW_INVENTORY) {
                new ViewInventoryMenu(target).openMenu(player);
            }
        }
    }

    @EventHandler
    public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.get(player.getUniqueId());
        if (profile.getState() == ProfileState.FIGHTING &&  profile.getMatch().getKit().getGameRules().isBridge()) {
            if (event.getItem().getType() == Material.GOLDEN_APPLE) {
                if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().getDisplayName().contains("Bridge Apple")) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
                    player.setHealth(20);
                }
            }
        }
        if (event.getItem().getType() == Material.GOLDEN_APPLE) {
            if (event.getItem().hasItemMeta() &&
                    event.getItem().getItemMeta().getDisplayName().contains("Golden Head")) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
                player.setFoodLevel(Math.min(player.getFoodLevel() + 6, 20));
            }
        }
    }
}
