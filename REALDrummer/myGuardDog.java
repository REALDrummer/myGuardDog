package REALDrummer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("deprecation")
public class myGuardDog extends JavaPlugin implements Listener, ActionListener {
    public static Plugin mGD;
    public static Server server;
    public static ConsoleCommandSender console;
    public static final ChatColor COLOR = ChatColor.YELLOW;
    public static File logs_folder, chrono_logs_folder, position_logs_folder, cause_logs_folder, latest_chrono_log;
    public static ArrayList<Event> event_waiting_list = new ArrayList<Event>();
    public static boolean auto_update = true, roll_back_in_progress = false, hard_save = false, prevent_Enderman_interactions = true;
    // player_to_inform_of_[...]: keys=player names and values=admin name or "console" who performed the command
    private static HashMap<String, String> players_to_inform_of_halting = new HashMap<String, String>();
    // players_questioned_about_rollback = new HashMap<player's name or "the console", parameters of the rollback>
    private static HashMap<String, String[]> players_questioned_about_rollback = new HashMap<String, String[]>();
    public static HashMap<String, ArrayList<String>> trust_lists = new HashMap<String, ArrayList<String>>(), info_messages = new HashMap<String, ArrayList<String>>();
    // inspecting_players=new HashMap<a player using the inspector, Object[] {Location of the last block clicked, int of the number of times the block}>
    private static HashMap<String, Object[]> inspecting_players = new HashMap<String, Object[]>();
    public static HashMap<UUID, String> TNT_causes = new HashMap<UUID, String>(), falling_block_causes = new HashMap<UUID, String>();
    private static ArrayList<String> halted_players = new ArrayList<String>(), muted_players = new ArrayList<String>(), debugging_players = new ArrayList<String>();
    public static HashMap<Block, String> locked_blocks = new HashMap<Block, String>();

    // TODO ALL PLUGINS: improve debugging by adding a second String parameter to debug() to briefly describe the command or system associated with the
    // debugging message and add a /mGD debug [system/command] variation on /mGD debug
    // TODO ALL PLUGINS: change the CommandSender sender parameter for all Player-only command methods to Player and cast the CommandSender to Player when
    // calling the method
    // TODO ALL PLUGINS: replace "console.sendMessage(" methods with "tellOps(" methods wherever possible
    // TODO ALL PLUGINS: surround the contents of methods with a "try" and a "catch (Excpetion excpetion) { myPluginUtils.processException([...]) }"
    // TODO ALL PLUGINS: change all the confirmation messages for saving and loading to displaySaveConfirmation() and displayLoadConfirmation() methods
    // TODO ALL PLUGINS: remove all "parameters" instance variables and make it a simple local variable in onCommand and arguments in plugin command methods
    // TODO ALL PLUGINS: change the names of all Exceptions from "exception" to "e"

    // TODO: make this plugin keep track of other info that can narrow down log searching during roll backs
    // TODO: don't let water or lava break locked blocks
    // TODO: fix door opening/closing logging (log for both top and bottom block)
    // TODO: fix anvil falling situation in EntityBlockChangeEvent
    // TODO: make /busy top (#) show the busiest people on the server by comparing the sizes of the cause log files
    // TODO: make a turn on PvP command for specific players
    // TODO: make sure players_to_inform HashMaps are saved in temp data as well as offline_player_gamemodes and gamemodes_to_change
    // TODO: /slap ("left"/"right"), /drop (height), and /punch
    // TODO: /set clock would sync your computer's clock with this plugin's clock and calendar by reading the time and date given in the command, logging the
    // difference between that and the server's current time and date, and adding or substracting the appropriate amount of time in the Event.class
    // TODO: check xp changes like gamemode changes

    // DONE: fixed bug that could complicate /rb if two players use it almost at the same time

    // plugin enable/disable and the command operator
    @Override
    public void onEnable() {
        mGD = this;
        server = getServer();
        console = server.getConsoleSender();
        // check for myPluginWiki
        if (server.getPluginManager().getPlugin("myPluginWiki") == null) {
            tellOps(ChatColor.RED + "I cannot perform my duties without myPluginWiki! You must retrieve myPluginWiki from the BukkitDev base and enable it immediately!", true);
            // I made one listener in myGuardDog$1 to listen for myPluginWiki's enabling. I put in myGuardDog$1 so that I could avoid registering myGuardDog's
            // main class as a listener if myPluginWiki wasn't available. It was a lot better than putting if statements at the beginning of every myGuardDog
            // listener to check for myPluginWiki.
            new myGuardDog$1(null, "register as listener").run();
            return;
        }
        server.getPluginManager().registerEvents(this, this);
        logs_folder = new File(getDataFolder(), "/the logs");
        chrono_logs_folder = new File(logs_folder, "/chronologically");
        position_logs_folder = new File(logs_folder, "/by position");
        cause_logs_folder = new File(logs_folder, "/by cause");
        logs_folder.mkdirs();
        chrono_logs_folder.mkdirs();
        position_logs_folder.mkdirs();
        cause_logs_folder.mkdirs();
        // locate the latest chronological log file
        latest_chrono_log = null;
        for (File log_file : chrono_logs_folder.listFiles())
            if (log_file.getName().endsWith("- now.mGDlog")) {
                latest_chrono_log = log_file;
                break;
            }
        loadTheLockedBlocks(console);
        if (auto_update)
            checkForUpdates(console);
        // done enabling
        String[] enable_messages =
                { "Server secured.", "BEWARE; MYGUARDDOG.", "Target: Griefers...TARGET LOCKED", "Anti-Griefing shields at full power, Captain. Awaiting orders...",
                        "Hasta la vista, griefers." };
        tellOps(COLOR + enable_messages[(int) (Math.random() * enable_messages.length)], true);
    }

    @Override
    public void onDisable() {
        // save any remaining events on event_waiting_list
        for (int i = event_waiting_list.size() - 1; i >= 0; i--)
            saveEvent(event_waiting_list.get(0));
        saveTheLockedBlocks(console, true);
        // done disabling
        String[] disable_messages =
                { "Until we meet again, pathetic griefers.", "Griefers have been successfully pwnd.", "Off duty? There is no such thing.",
                        "Though I am disabled, I do not sleep. Your server is under my protection." };
        tellOps(COLOR + disable_messages[(int) (Math.random() * disable_messages.length)], true);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String command, String[] parameters) {
        // cancel the command if myPluginWiki isn't available
        if (server.getPluginManager().getPlugin("myPluginWiki") == null) {
            sender.sendMessage(ChatColor.RED
                    + "My apologies, Commander, but I require myPluginWiki to perform any of my functions. Please retrieve said plugin from BukkitDev and I will be happy to aid you in the war against griefing.");
            return true;
        }
        if (command.equalsIgnoreCase("halt")) {
            if (sender instanceof Player && !sender.hasPermission("myguarddog.halt") && !sender.hasPermission("myguarddog.admin"))
                sender.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to halt people.");
            else if (parameters.length == 0)
                sender.sendMessage(ChatColor.RED + "You forgot to tell me who to halt!");
            else {
                Player target = server.getPlayerExact(myPluginUtils.getFullName(parameters[0]));
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "I couldn't find anyone named \"" + parameters[0] + "\"!");
                    return true;
                } else if (halted_players.contains(target.getName())) {
                    sender.sendMessage(ChatColor.RED + "Someone already halted " + target.getName() + ".");
                    return true;
                }
                halted_players.add(target.getName());
                sender.sendMessage(COLOR + "I halted " + target.getName() + ".");
                String sender_name = "someone on the console";
                if (sender instanceof Player)
                    sender_name = sender.getName();
                target.sendMessage(COLOR + "Don't move or try to use commands; " + sender_name + " halted you.");
                tellOps(COLOR + sender.getName().substring(0, 1).toUpperCase() + sender.getName().substring(1) + " halted " + target.getName() + ".",
                        sender instanceof Player, sender.getName(), target.getName());
            }
            return true;
        } else if (command.equalsIgnoreCase("unhalt")) {
            if (sender instanceof Player && !sender.hasPermission("myguarddog.unhalt") && !sender.hasPermission("myguarddog.admin"))
                sender.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to unhalt people.");
            else if (parameters.length == 0)
                sender.sendMessage(ChatColor.RED + "You forgot to tell me who to unhalt!");
            else {
                Player target = server.getPlayerExact(myPluginUtils.getFullName(parameters[0]));
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "I couldn't find anyone named \"" + parameters[0] + "\"!");
                    return true;
                } else if (!halted_players.contains(target.getName())) {
                    sender.sendMessage(ChatColor.RED + "No one has halted " + target.getName() + ".");
                    return true;
                }
                sender.sendMessage(COLOR + "Very well. I will unhalt " + target.getName() + ".");
                halted_players.remove(target.getName());
                String sender_name = "someone on the console";
                if (sender instanceof Player)
                    sender_name = sender.getName();
                target.sendMessage(COLOR + "You're free to go; " + sender_name + " unhalted you.");
                tellOps(COLOR + sender.getName().substring(0, 1).toUpperCase() + sender.getName().substring(1) + " unhalted " + target.getName() + ".",
                        sender instanceof Player, sender.getName(), target.getName());
            }
            return true;
        } else if (command.equalsIgnoreCase("convert")) {
            if (sender instanceof Player && !sender.hasPermission("myguarddog.admin"))
                sender.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to use " + COLOR + "/convert" + ChatColor.RED + ".");
            else if (parameters.length == 0)
                sender.sendMessage(ChatColor.RED + "You forgot to tell me the name of the log file you want me to convert!");
            else
                convert(sender, parameters);
            return true;
        } else if ((command.equalsIgnoreCase("mGD") || command.equalsIgnoreCase("myGuardDog")) && parameters.length == 1 && parameters[0].equalsIgnoreCase("save")) {
            if (sender instanceof Player && !sender.hasPermission("myguarddog.admin"))
                if (command.equalsIgnoreCase("myGuardDog"))
                    sender.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to use " + COLOR + "/myGuardDog save" + ChatColor.RED + ".");
                else
                    sender.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to use " + COLOR + "/mGD save" + ChatColor.RED + ".");
            else
                saveTheLockedBlocks(sender, true);
            return true;
        } else if ((command.equalsIgnoreCase("mGD") || command.equalsIgnoreCase("myGuardDog")) && parameters.length > 0 && parameters[0].toLowerCase().startsWith("debug")) {
            if (sender instanceof Player && !sender.hasPermission("myguarddog.admin"))
                if (command.equalsIgnoreCase("myGuardDog"))
                    sender.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to use " + COLOR + "/myGuardDog debug" + ChatColor.RED + ".");
                else
                    sender.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to use " + COLOR + "/mGD debug" + ChatColor.RED + ".");
            else if (server.getPluginManager().getPlugin("myPluginWiki") == null)
                sender.sendMessage(COLOR
                        + "My apologies, Commander, but I require myPluginWiki to perform any of my functions. Please retrieve said plugin from BukkitDev and I will be happy to aid you in the war against griefing.");
            else {
                String sender_name = "console";
                if (sender instanceof Player)
                    sender_name = ((Player) sender).getName();
                if (debugging_players.contains(sender_name)) {
                    debugging_players.remove(sender_name);
                    sender.sendMessage(COLOR + "Bugs terminated!");
                } else {
                    debugging_players.add(sender_name);
                    sender.sendMessage(COLOR + "Operation NEUTRALIZE BUGS is a go!");
                }
            }
            return true;
        } else if ((command.equalsIgnoreCase("myGuardDog") || command.equalsIgnoreCase("mGD")) && parameters.length >= 1 && parameters[0].toLowerCase().startsWith("update")) {
            if (sender instanceof Player && !sender.isOp())
                if (command.equalsIgnoreCase("myGuardDog"))
                    sender.sendMessage(ChatColor.RED + "I did not give you permission to use " + COLOR + "/myGuardDog update" + ChatColor.RED + ".");
                else
                    sender.sendMessage(ChatColor.RED + "I did not give you permission to use " + COLOR + "/mGD update" + ChatColor.RED + ".");
            else if (parameters.length == 1)
                checkForUpdates(sender);
            else if (parameters[1].equalsIgnoreCase("on"))
                if (auto_update)
                    sender.sendMessage(ChatColor.RED + "The update tracker device is already active.");
                else {
                    auto_update = true;
                    sender.sendMessage(COLOR + "The update tracker device is active.");
                }
            else if (parameters[1].equalsIgnoreCase("off"))
                if (!auto_update)
                    sender.sendMessage(ChatColor.RED + "The update tracker device is already off. General REALDrummer has advised that the tracker device be activated.");
                else {
                    auto_update = false;
                    sender.sendMessage(COLOR
                            + "The update tracker device has been deactivated. However, General REALDrummer advises that the device remains on as long as possible to avoid letting updates slip through the system.");
                }
            else
                return false;
            return true;
        } else if (command.toLowerCase().startsWith("insp")) {
            if (sender instanceof Player && !sender.hasPermission("myguarddog.inspect") && !sender.hasPermission("myguarddog.admin"))
                sender.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to use " + COLOR + "/" + command.toLowerCase() + ChatColor.RED + ".");
            else if (!(sender instanceof Player))
                sender.sendMessage(ChatColor.RED + "You're a console! You don't even have an eye to spy with!");
            else if (inspecting_players.containsKey(sender.getName())) {
                inspecting_players.remove(sender.getName());
                sender.sendMessage(COLOR + "Good detective work, Inspector " + sender.getName() + " Holmes.");
            } else {
                inspecting_players.put(sender.getName(), null);
                sender.sendMessage(COLOR + "I spy with my little eye...A GRIEFER!!");
            }
            return true;
        } else if (command.equalsIgnoreCase("rollback") || command.equalsIgnoreCase("rb")) {
            if (sender instanceof Player && !sender.hasPermission("myguarddog.rollback") && !sender.hasPermission("myguarddog.admin"))
                sender.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to use " + COLOR + "/" + command.toLowerCase() + ChatColor.RED + ".");
            else if (roll_back_in_progress)
                sender.sendMessage(ChatColor.RED
                        + "My apologies, Commander, but there is already a roll back in progress. We must terminate see the first roll back through before beginning a new one.");
            else {
                // TODO uncomment this when the roll back is completed
                // // if there are no parameters, that's supposed to mean that you want to roll back ALL events
                // // however, since that would be a really, REALLY big change, we want to make sure someone didn't just put no parameters by accident.
                // if (parameters.length == 0) {
                // sender.sendMessage(COLOR + "You didn't put any parameters. Do you want to roll back everything that has ever happened on this server?");
                // if (sender instanceof Player)
                // players_questioned_about_rollback.put(sender.getName(), parameters);
                // else
                // players_questioned_about_rollback.put("the console", parameters);
                // return true;
                // }
                // new myGuardDog$1(sender, "roll back", parameters, null).run();
                // // if they used /rollback again after you asked them to confirm their other /rollback command, clearly they want to make changes, so we
                // should
                // // ignore the first command by cancelling the question concerning it
                // if (sender instanceof Player && players_questioned_about_rollback.containsKey(sender.getName()))
                // players_questioned_about_rollback.remove(sender.getName());
                // else if (!(sender instanceof Player) && players_questioned_about_rollback.containsKey("the console"))
                // players_questioned_about_rollback.remove("the console");
                sender.sendMessage(ChatColor.RED + "My sincerest apologies, but the roll back is currently out of service.");
            }
            return true;
        } else if (command.equalsIgnoreCase("trust") || command.equals("friend") || command.equals("befriend")) {
            if (!(sender instanceof Player))
                sender.sendMessage(ChatColor.RED + "You're a console. You can't lock anything, so you can't use " + COLOR + "/" + command.toLowerCase() + ChatColor.RED + ".");
            else if (sender instanceof Player && !sender.hasPermission("myguarddog.trust") && !sender.hasPermission("myguarddog.user")
                    && !sender.hasPermission("myguarddog.admin"))
                sender.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to use " + COLOR + "/" + command.toLowerCase() + ChatColor.RED + ".");
            else if (parameters.length == 0)
                sender.sendMessage(ChatColor.RED + "You forgot to tell me who you want to trust!");
            else
                trust((Player) sender, parameters[0]);
            return true;
        } else if (command.equalsIgnoreCase("mistrust") || command.equalsIgnoreCase("untrust") || command.equals("unfriend")) {
            if (!(sender instanceof Player))
                sender.sendMessage(ChatColor.RED + "You're a console. You can't lock anything, so you can't use " + COLOR + "/" + command.toLowerCase() + ChatColor.RED + ".");
            else if (sender instanceof Player && !sender.hasPermission("myguarddog.trust") && !sender.hasPermission("myguarddog.user")
                    && !sender.hasPermission("myguarddog.admin"))
                sender.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to use " + COLOR + "/" + command.toLowerCase() + ChatColor.RED + ".");
            else if (parameters.length == 0)
                sender.sendMessage(ChatColor.RED + "You forgot to tell me who you want to trust!");
            else {
                ArrayList<String> trusted_players = trust_lists.get(sender.getName());
                if (trusted_players == null)
                    trusted_players = new ArrayList<String>();
                if (trusted_players.contains(myPluginUtils.getFullName(parameters[0]))) {
                    trusted_players.remove(myPluginUtils.getFullName(parameters[0]));
                    trust_lists.put(((Player) sender).getName(), trusted_players);
                    sender.sendMessage(COLOR + "Acknowledged. From now on, " + myPluginUtils.getFullName(parameters[0]) + " will " + ChatColor.ITALIC + "not" + COLOR
                            + " have access to all of your locked blocks.");
                } else
                    sender.sendMessage(ChatColor.RED + "Private " + myPluginUtils.getFullName(parameters[0]) + " is not present on your trust list.");
            }
            return true;
        }
        return false;
    }

    // intra-command methods
    /** This method sends a given message to every operator currently on the server as well as to the console.
     * 
     * @param message
     *            is the message that will be sent to all operators and the console. <b><tt>Message</b></tt> will be color coded using myPluginUtils's
     *            {@link #colorCode(String) colorCode(String)} method.
     * @param also_tell_console
     *            indicates whether or not <b><tt>message</b></tt> will also be sent to the console.
     * @param exempt_ops
     *            is an optional parameter in which you may list any ops by exact username that should not receive <b><tt>message</b></tt>. */
    public static void tellOps(String message, boolean also_tell_console, String... exempt_ops) {
        // capitalize the first letters of sentences
        if (message.length() > 1)
            message = message.substring(0, 1).toUpperCase() + message.substring(1);
        for (Player player : server.getOnlinePlayers()) {
            boolean is_exempt = false;
            if (exempt_ops.length > 0)
                for (String exempt_op : exempt_ops)
                    if (exempt_op.equals(player.getName())) {
                        is_exempt = true;
                        break;
                    }
            if (player.isOp() && !is_exempt)
                player.sendMessage(message);
        }
        if (also_tell_console)
            console.sendMessage(message);
    }

    public static String findCause(Location location, int search_radius, Object... filters) {
        debug("finding cause (search radius=" + search_radius + "; filters: \"" + ChatColor.WHITE + myPluginUtils.writeArray(filters) + COLOR + "\")...");
        // the filters should come in pairs (even #s are actions; odd #s are objects)
        if (filters.length % 2 == 1) {
            tellOps(ChatColor.DARK_RED + "There are an odd number of filters on this findCause instance! Tell REALDrummer!", true);
            tellOps(ChatColor.WHITE + "finding cause (search radius=" + search_radius + "; filters: \"" + myPluginUtils.writeArray(filters) + "\")...", true);
            return null;
        }
        for (int x = location.getBlockX() - search_radius; x <= location.getBlockX() + search_radius; x++)
            try {
                File file = new File(position_logs_folder, "x = " + x + " " + location.getWorld().getWorldFolder().getName() + ".mGDlog");
                if (!file.exists())
                    continue;
                RandomAccessFile in = new RandomAccessFile(file, "r");
                ArrayList<String> save_lines = new ArrayList<String>();
                try {
                    while (true)
                        save_lines.add(in.readUTF());
                } catch (EOFException e) {
                    // the reading has finished
                    in.close();
                }
                // read the save lines in reverse to see the most recent events first
                for (int i = save_lines.size() - 1; i >= 0; i--) {
                    Event event = new Event(save_lines.get(i));
                    // check the y and z coordinates; the world and x-coordinates do not need to be checked because we are already in the log file for
                    // the appropriate world and x-coordinate
                    if (event.y >= location.getBlockY() - search_radius && event.y <= location.getBlockY() + search_radius && event.z >= location.getBlockZ() - search_radius
                            && event.z <= location.getBlockZ() + search_radius) {
                        if (filters.length == 0)
                            return event.cause;
                        else
                            for (int j = 0; j < filters.length - 1; j += 2)
                                // check the given action(s)
                                if (filters[j] == null || filters[j] instanceof Boolean
                                        && ((Boolean) filters[j] && event.isPlacement() || !(Boolean) filters[j] && event.isRemoval()) || filters[j] instanceof String
                                        && event.action.equals((String) filters[j]))
                                    // check the given object(s)
                                    for (String object : event.objects)
                                        if (filters[j + 1] != null && filters[j + 1] instanceof String[])
                                            for (String filter_word : (String[]) filters[j + 1]) {
                                                if (object.startsWith(filter_word))
                                                    return event.cause;
                                            }
                                        else if (filters[j + 1] == null || filters[j + 1] instanceof String && object.startsWith((String) filters[j + 1]))
                                            return event.cause;
                    }
                }
            } catch (IOException e) {
                myPluginUtils.processException(mGD, ChatColor.DARK_RED + "I got an IOException while trying to find the cause of a recent \"" + filters[0] + " " + filters[1]
                        + "\" (or other actions and objects) event!", e);
                return null;
            }

        return null;
    }

    public static boolean isPermittedToUnlock(Player player, Block block) {
        if (player.hasPermission("myguarddog.admin") || !locked_blocks.containsKey(block) || locked_blocks.get(block).equals(player.getName())
                || (trust_lists.get(locked_blocks.get(block)) != null && trust_lists.get(locked_blocks.get(block)).contains(player.getName())))
            return true;
        return false;
    }

    public static void unlock(Block block, Player player, String... alternate_name) {
        if (!locked_blocks.containsKey(block))
            return;
        debug("unlocking block");
        String block_name;
        if (alternate_name.length == 0)
            block_name = myPluginWiki.getItemName(block, false, true, true);
        else
            block_name = alternate_name[0];
        // if the owner is not the one unlocking the locked block, inform the owner
        if (!locked_blocks.get(block).equals(player.getName())) {
            Player owner = server.getPlayerExact(locked_blocks.get(block));
            String message = "Hey, " + player.getName() + " broke your " + block_name + " at " + myPluginUtils.writeLocation(block) + ".";
            if (owner != null && owner.isOnline()) {
                owner.sendMessage(COLOR + message);
                debug("owner online; informed");
            } else {
                ArrayList<String> messages = info_messages.get(locked_blocks.get(block));
                if (messages == null)
                    messages = new ArrayList<String>();
                messages.add("&e" + message);
                info_messages.put(locked_blocks.get(block), messages);
                debug("owner offline; will be informed");
            }
            // send a confirmation message
            player.sendMessage(COLOR + "You broke " + locked_blocks.get(block) + "'s " + block_name + ".");
        } else
            player.sendMessage(COLOR + "You broke your " + block_name + ".");
        locked_blocks.remove(block);
    }

    public static void inspect(Player player, Location position) {
        // TODO: test inspector fix w/ empty event list inspections
        // this checks the number of times this player has clicked the same block and if the block they're clicking now is the same one. This allows
        // players to click blocks multiple times to see more than just the past 2 events
        int times_clicked = 0;
        if (inspecting_players.get(player.getName()) != null && inspecting_players.get(player.getName())[0].equals(position)) {
            times_clicked = (Integer) inspecting_players.get(player.getName())[1];
            // if this position was the last position that the inspector had inspected, but times_clicked = 0, that must mean that they reached the end of the
            // list of events that have happened at that position the last time they inspected
            if (times_clicked == 0)
                player.sendMessage(COLOR + "We're back at the beginning!");
        }
        ArrayList<String> events = new ArrayList<String>();
        File log_file = new File(position_logs_folder, "x = " + position.getBlockX() + " " + position.getWorld().getWorldFolder().getName() + ".mGDlog");
        if (!log_file.exists()) {
            player.sendMessage(COLOR + "Nothing has happened here at (" + position.getBlockX() + ", " + position.getBlockY() + ", " + position.getBlockZ() + ") yet!");
            return;
        }
        try {
            RandomAccessFile in = new RandomAccessFile(log_file, "r");
            try {
                while (true) {
                    Event event = new Event(in.readUTF());
                    // since we're reading the position log, there's no need to check if the x is right
                    if (event.y == position.getBlockY() && event.z == position.getBlockZ())
                        // add the event to the beginning to make the events list in reverse chronological order
                        events.add(0, event.save_line);
                }
            } catch (EOFException e) {
                in.close();
            }
        } catch (IOException exception) {
            // if an IOException happens here, it means that myGuardDog is likely saving events to this file while we're trying to read it. If that's the case,
            // try to inspect this location again in 2 ticks.
            server.getScheduler().scheduleSyncDelayedTask(mGD, new myGuardDog$1(player, "retry inspection", position), 2);
        }
        if (events.size() > 0) {
            if (times_clicked * 2 >= events.size())
                times_clicked = 0;
            else {
                if (times_clicked > 0)
                    // if times_clicked>0, remove two events from the beginning of the list for every time clicked. That way, the two events at the beginning of
                    // the list will be the ones we want to display
                    for (int i = 0; i < times_clicked; i++) {
                        events.remove(0);
                        events.remove(0);
                    }
                times_clicked++;
            }
            player.sendMessage(COLOR + "I see that...");
            for (int i = 0; i < 2; i++)
                player.sendMessage(ChatColor.WHITE + events.get(i));
            if (events.size() > 2)
                player.sendMessage(COLOR + "Click again to see more!");
            inspecting_players.put(player.getName(), new Object[] { position, times_clicked });
        } else {
            player.sendMessage(COLOR + "Nothing has happened here at (" + position.getBlockX() + ", " + position.getBlockY() + ", " + position.getBlockZ() + ") yet!");
            inspecting_players.put(player.getName(), new Object[] { position, 0 });
        }
    }

    public static boolean checkForReactionBreaks(Event break_event) {
        return checkForReactionBreaks(break_event, null);
    }

    public static boolean checkForReactionBreaks(Event break_event, ArrayList<Block> exempt_blocks) {
        // checks = new HashMap<[BlockState of a block that might be broken], [the items the block should drop if it breaks]>()
        ArrayList<BlockState> checks = new ArrayList<BlockState>();
        checks.add(break_event.location.getBlock().getState());
        // check the sides of the broken block for possible reaction breaks
        Block block;
        for (int i = 0; i < 4; i++) {
            int x = break_event.x, z = break_event.z;
            if (i == 0)
                x--;
            else if (i == 1)
                x++;
            else if (i == 2)
                z--;
            else
                z++;
            block = new Location(break_event.world, x, break_event.y, z).getBlock();
            if (myPluginWiki.mustBeAttached(block, false) == null)
                tellOps(ChatColor.DARK_RED + "Hey! For some reason, myPluginWiki can't find info on an item with the I.D. " + block.getTypeId()
                        + "! Is myPluginWiki up to date?", true);
            else if (myPluginWiki.mustBeAttached(block, false) && (exempt_blocks == null || !exempt_blocks.contains(block)))
                checks.add(block.getState());
        }
        // check the top of the broken block for possible reaction breaks
        block = new Location(break_event.world, break_event.x, break_event.y + 1, break_event.z).getBlock();
        if (myPluginWiki.mustBeAttached(block, null) == null)
            tellOps(ChatColor.DARK_RED + "Hey! For some reason, myPluginWiki can't find info on an item with the I.D. " + block.getTypeId() + "! Is myPluginWiki up to date?",
                    true);
        else if (myPluginWiki.mustBeAttached(block, null) && (exempt_blocks == null || !exempt_blocks.contains(block))) {
            checks.add(block.getState());
            // if the block being tracked is the bottom of a door, also track the top of the door
            if ((block.getType() == Material.WOODEN_DOOR || block.getType() == Material.IRON_DOOR_BLOCK) && block.getData() < 8)
                checks.add(block.getRelative(BlockFace.UP).getState());
        }
        if (checks.size() > 1) {
            server.getScheduler().scheduleSyncDelayedTask(mGD, new myGuardDog$1(console, "track reaction breaks", break_event, checks), 1);
            return true;
        } else
            saveEvent(break_event);
        return false;
    }

    public static void debug(String message) {
        if (debugging_players.size() == 0)
            return;
        if (debugging_players.contains("console")) {
            console.sendMessage(COLOR + message);
            if (debugging_players.size() == 1)
                return;
        }
        for (Player player : server.getOnlinePlayers())
            if (debugging_players.contains(player.getName()))
                player.sendMessage(COLOR + message);
    }

    // listeners
    @Override
    public void actionPerformed(ActionEvent event) {
        new myGuardDog$1(console, "save the logs", true, null).run();
    }

    @EventHandler
    public void informPlayersTheyHaveBeenHalted(PlayerJoinEvent event) {
        // remind debugging admins that they're debugging
        if (debugging_players.contains(event.getPlayer().getName()))
            event.getPlayer().sendMessage(COLOR + "Your debugging messages are still on for myGuardDog!");
        if (players_to_inform_of_halting.containsKey(event.getPlayer().getName()))
            event.getPlayer().sendMessage(COLOR + players_to_inform_of_halting.get(event.getPlayer().getName()) + " halted you. Don't move and don't try to use commands.");
    }

    @EventHandler
    public void stopHaltedPlayersFromMoving(PlayerMoveEvent event) {

        if (halted_players.contains(event.getPlayer().getName())
        // only cancel movement events that change the player's coordinates; don't cancel looking movements
                && !(event.getFrom().getX() == event.getTo().getX() && event.getFrom().getY() == event.getTo().getY() && event.getFrom().getZ() == event.getTo().getZ()))
            event.setCancelled(true);
    }

    @EventHandler
    public void stopHaltedPlayersFromUsingCommands(PlayerCommandPreprocessEvent event) {

        if (halted_players.contains(event.getPlayer().getName()))
            event.setCancelled(true);
    }

    @EventHandler
    public void stopMutedPlayersFromTalkingAndRecieveRollbackQuestionResponses(AsyncPlayerChatEvent event) {
        if (muted_players.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
            if (event.getMessage().contains("stfu") || event.getMessage().contains("shut up"))
                event.getPlayer().sendMessage(
                        ChatColor.DARK_RED + "" + ChatColor.ITALIC + "No, " + ChatColor.BOLD + "you " + ChatColor.DARK_RED + "" + ChatColor.ITALIC + "shut up, b"
                                + ChatColor.MAGIC + "itch" + ChatColor.DARK_RED + "" + ChatColor.ITALIC + "!");
            else
                event.getPlayer().sendMessage(COLOR + "Be quiet. You're not allowed to speak.");
        } else if (players_questioned_about_rollback.containsKey(event.getPlayer().getName())) {
            Boolean accepted = myPluginUtils.getResponse(event.getMessage());
            if (accepted != null && accepted) {
                event.setCancelled(true);
                new myGuardDog$1(event.getPlayer(), "roll back", false, players_questioned_about_rollback.get(event.getPlayer().getName())).run();
            } else if (accepted != null) {
                event.setCancelled(true);
                players_questioned_about_rollback.remove(event.getPlayer().getName());
                event.getPlayer().sendMessage(COLOR + "Got it. The rollback has been cancelled.");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void trackTNTMinecartActivations(VehicleMoveEvent event) {
        if (event.getVehicle().getType() == EntityType.MINECART_TNT && !TNT_causes.containsKey(event.getVehicle().getUniqueId())
                && event.getTo().getBlock().getType() == Material.ACTIVATOR_RAIL && event.getTo().getBlock().isBlockIndirectlyPowered()) {
            String cause = findCause(event.getTo(), 0, "placed", "an activator rail");
            if (cause != null)
                TNT_causes.put(event.getVehicle().getUniqueId(), cause);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void logBlockBreakAndInspect(BlockBreakEvent event) {
        String description = event.getEventName() + " (" + event.getPlayer().getName() + "; " + event.getBlock().getType().name() + ":" + event.getBlock().getData() + ")";
        if (event.isCancelled()) {
            debug(ChatColor.STRIKETHROUGH + description);
            return;
        } else
            debug(description);
        Block block = event.getBlock();
        if ((block.getType() == Material.WOODEN_DOOR || block.getType() == Material.IRON_DOOR) && block.getData() >= 8) {
            block = block.getRelative(BlockFace.DOWN);
            debug("check one block down; target is door");
        }
        // inspect
        if (inspecting_players.containsKey(event.getPlayer().getName())) {
            event.setCancelled(true);
            debug("inspection");
            inspect(event.getPlayer(), event.getBlock().getLocation());
        } // cancel the event if the block is locked and the player isn't allowed to break it
        else if (!isPermittedToUnlock(event.getPlayer(), block)) {
            event.setCancelled(true);
            debug("cancelled; illegal locked block breakage");
            event.getPlayer().sendMessage(
                    ChatColor.RED + "This is " + locked_blocks.get(block) + "'s " + myPluginWiki.getItemName(block, false, true, true) + " and you can't break it.");
        } else {
            unlock(block, event.getPlayer());
            checkForReactionBreaks(new Event(event.getPlayer().getName(), "broke", block, event.getPlayer().getGameMode() == GameMode.CREATIVE));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void logBlockPlaceAndInspect(BlockPlaceEvent event) {
        String description = event.getEventName() + " (" + event.getPlayer().getName() + "; " + event.getBlock().getType().name() + ":" + event.getBlock().getData() + ")";
        if (event.isCancelled()) {
            debug(ChatColor.STRIKETHROUGH + description);
            return;
        } else
            debug(description);
        // inspect
        if (inspecting_players.containsKey(event.getPlayer().getName())) {
            event.setCancelled(true);
            debug("inspection");
            inspect(event.getPlayer(), event.getBlock().getLocation());
        } // cancel someone trying to place a hopper below a locked block with an inventory
          // translation: if the player is placing a hopper, the block above the hopper is a lockable container owned by someone else, the player placing the
          // hopper isn't on the container owner's trust list, and the player placing the hopper isn't an admin
        else if (event.getBlock().getType() == Material.HOPPER && !isPermittedToUnlock(event.getPlayer(), event.getBlock().getRelative(BlockFace.UP))) {
            event.setCancelled(true);
            debug("cancelled; unauthorized hopper below container");
            event.getPlayer().sendMessage(
                    ChatColor.RED + "Ha ha! You make me laugh! I'm not that stupid. You can't put a hopper below someone else's locked container and steal all their stuff.");
        } else {
            // lock lockable blocks automatically
            if (myPluginWiki.isLockable(event.getBlock())) {
                locked_blocks.put(event.getBlock(), event.getPlayer().getName());
                event.getPlayer().sendMessage(COLOR + "I locked your " + myPluginWiki.getItemName(event.getBlock(), false, true, true) + ".");
                debug("locked successfully");
            }
            // log placements
            // log fire placement as ignition and don't log air placement because that makes no sense!
            if (event.getBlock().getType() != Material.AIR && event.getBlock().getType() != Material.FIRE) {
                saveEvent(new Event(event.getPlayer().getName(), "placed", event.getBlock(), event.getPlayer().getGameMode() == GameMode.CREATIVE));
                // track the top of door placements
                if (event.getBlock().getType() == Material.WOODEN_DOOR || event.getBlock().getType() == Material.IRON_DOOR_BLOCK)
                    saveEvent(new Event(event.getPlayer().getName(), "placed", myPluginWiki.getItemName(event.getBlock(), false, true, false) + " ("
                            + myPluginUtils.getDoorTopData(event.getBlock()) + ")", event.getBlock().getRelative(BlockFace.UP).getLocation(),
                            event.getPlayer().getGameMode() == GameMode.CREATIVE));
                // track liquid and other replacement by the placed block
                if (event.getBlockReplacedState().getType() != Material.AIR)
                    saveEvent(new Event(event.getPlayer().getName(), "covered", myPluginWiki.getItemName(event.getBlockReplacedState().getTypeId(), event
                            .getBlockReplacedState().getData().getData(), true, true, false), event.getBlock().getLocation(),
                            event.getPlayer().getGameMode() == GameMode.CREATIVE));
            } // consider "placing fire" the same as "setting fire to" something, but don't bother logging T.N.T. ignition
            else if (event.getBlock().getType() == Material.FIRE && event.getBlockReplacedState().getType() != Material.TNT)
                saveEvent(new Event(event.getPlayer().getName(), "set fire to", event.getBlock(), event.getPlayer().getGameMode() == GameMode.CREATIVE));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void logPlayerInteractionsAndInspect(PlayerInteractEvent event) {
        Block target_block = event.getClickedBlock();
        if (target_block == null)
            target_block = event.getPlayer().getTargetBlock(null, 1024);
        String description =
                event.getEventName() + " (" + event.getPlayer().getName() + "; " + event.getAction().name() + "; " + target_block.getType().name() + ":"
                        + target_block.getData() + ")";
        if (event.isCancelled()) {
            debug(ChatColor.STRIKETHROUGH + description);
            return;
        } else
            debug(description);
        // inspect
        if ((event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) && inspecting_players.containsKey(event.getPlayer().getName())) {
            event.setCancelled(true);
            debug("inspection");
            Location position = null;
            if (event.getClickedBlock() != null)
                position = event.getClickedBlock().getLocation();
            else
                position = event.getPlayer().getTargetBlock(null, 1024).getLocation();
            if (position.getBlock().getTypeId() == 0) {
                event.getPlayer().sendMessage(ChatColor.RED + "Sorry, but I can't see that far!");
                debug("block too far to see");
            } else
                inspect(event.getPlayer(), position);
        } // (un)lock lockable items
        else if (event.getAction() == Action.LEFT_CLICK_BLOCK && event.getPlayer().getItemInHand().getType() == Material.IRON_INGOT
                && myPluginWiki.isLockable(event.getClickedBlock())) {
            event.setCancelled(true);
            debug("(un)locking");
            Block block = event.getClickedBlock();
            // if the block is a wooden door, make sure to check the bottom block of the door
            if ((block.getType() == Material.WOODEN_DOOR || block.getType() == Material.IRON_DOOR) && block.getData() >= 8) {
                block = event.getClickedBlock().getRelative(BlockFace.DOWN);
                debug("check block below; target is door");
            }
            // if the thing isn't already locked, try to lock it
            if (!locked_blocks.containsKey(block)) {
                locked_blocks.put(block, event.getPlayer().getName());
                Block other_chest_half = myPluginWiki.getOtherHalfOfLargeChest(block);
                if (other_chest_half != null) {
                    locked_blocks.put(other_chest_half, event.getPlayer().getName());
                    saveEvent(new Event(event.getPlayer().getName(), "locked", other_chest_half, event.getPlayer().getGameMode() == GameMode.CREATIVE));
                }
                event.getPlayer().sendMessage(COLOR + "*click* Your " + myPluginWiki.getItemName(block, false, true, true) + " is now locked.");
                saveEvent(new Event(event.getPlayer().getName(), "locked", block, event.getPlayer().getGameMode() == GameMode.CREATIVE));
            } // if the thing is already locked, try to unlock it
            else if (locked_blocks.get(block).equals(event.getPlayer().getName()) || trust_lists.get(locked_blocks.get(block)) != null
                    && trust_lists.get(locked_blocks.get(block)).contains(event.getPlayer().getName()) || event.getPlayer().hasPermission("myguarddog.admin")) {
                // if the owner is not the one unlocking the locked block, inform the owner
                if (!locked_blocks.get(block).equals(event.getPlayer().getName())) {
                    debug("unlocker not owner");
                    Player owner = server.getPlayerExact(locked_blocks.get(block));
                    String message =
                            "Hey, " + event.getPlayer().getName() + " unlocked your " + myPluginWiki.getItemName(block, false, true, true) + " at "
                                    + myPluginUtils.writeLocation(block) + ".";
                    if (owner != null && owner.isOnline()) {
                        owner.sendMessage(COLOR + message);
                        debug("owner online; informed");
                    } else {
                        ArrayList<String> messages = info_messages.get(locked_blocks.get(block));
                        if (messages == null)
                            messages = new ArrayList<String>();
                        messages.add("&e" + message);
                        info_messages.put(locked_blocks.get(block), messages);
                        debug("owner offline; will be informed");
                    }
                    // send a confirmation message
                    event.getPlayer().sendMessage(COLOR + "You unlocked " + locked_blocks.get(block) + "'s " + myPluginWiki.getItemName(block, false, true, true) + ".");
                } else
                    event.getPlayer().sendMessage(COLOR + "You unlocked your " + myPluginWiki.getItemName(block, false, true, true) + ".");
                // unlock the block (and the other half of a chest if necessary)
                saveEvent(new Event(event.getPlayer().getName(), "unlocked", locked_blocks.get(block) + "'s " + myPluginWiki.getItemName(block, false, true, true), block
                        .getLocation(), event.getPlayer().getGameMode() == GameMode.CREATIVE));
                locked_blocks.remove(block);
                Block other_chest_half = myPluginWiki.getOtherHalfOfLargeChest(block);
                if (other_chest_half != null) {
                    saveEvent(new Event(event.getPlayer().getName(), "unlocked", locked_blocks.get(other_chest_half) + "'s "
                            + myPluginWiki.getItemName(other_chest_half, false, true, true), other_chest_half.getLocation(),
                            event.getPlayer().getGameMode() == GameMode.CREATIVE));
                    locked_blocks.remove(other_chest_half);
                }
            } // if the thing is already locked and this player can't unlock it, cancel the event
            else {
                event.setCancelled(true);
                event.getPlayer().sendMessage(
                        ChatColor.RED + "Sorry, but this " + myPluginWiki.getItemName(block, false, true, true) + " belongs to " + locked_blocks.get(block)
                                + " and you're not allowed to unlock it.");
                debug("unlock rejected");
            }
        } // log switch usage and prevent the use of locked items
        else {
            Block block = event.getClickedBlock();
            String action = null;
            if (event.getAction() == Action.PHYSICAL && (block.getType() == Material.STONE_PLATE || block.getType() == Material.WOOD_PLATE))
                action = "stepped on";
            else if (event.getAction() == Action.RIGHT_CLICK_BLOCK && !event.getPlayer().isSneaking())
                if (block.getType() == Material.LEVER)
                    action = "flipped";
                else if (block.getType() == Material.STONE_BUTTON || block.getType() == Material.WOOD_BUTTON)
                    action = "pressed";
                else if (block.getType() == Material.WOODEN_DOOR || block.getType() == Material.TRAP_DOOR || block.getType() == Material.FENCE_GATE) {
                    // if it's the top block of a door, data=8 or 9
                    if (block.getType() == Material.WOODEN_DOOR && block.getData() >= 8) {
                        block = block.getRelative(BlockFace.DOWN);
                        debug("check block below; target is door");
                    }
                    // if you're closing a door, data=4-7 (or 12-15 for trapdoors); if you're opening a door, data=0-3 (or 8-11 for trapdoors)
                    if (block.getData() < 4 || (block.getType() == Material.TRAP_DOOR && block.getData() > 7 && block.getData() < 12))
                        action = "opened";
                    else
                        action = "closed";
                } // log plant bonemealing (bonemeal=351:15)
                else if (event.getPlayer().getItemInHand().getTypeId() == 351
                        && event.getPlayer().getItemInHand().getData().getData() == 15
                        && (block.getType() == Material.SAPLING || block.getType() == Material.WHEAT || block.getType() == Material.CARROT
                                || block.getType() == Material.POTATO || block.getType() == Material.BROWN_MUSHROOM || block.getType() == Material.RED_MUSHROOM
                                || block.getType() == Material.GRASS || block.getType() == Material.COCOA || block.getType() == Material.MELON_STEM
                                || block.getType() == Material.PUMPKIN_STEM || event.getClickedBlock().getType() == Material.NETHER_WARTS))
                    action = "bonemealed";
                // log chest opening and closing
                else if ((block.getType() == Material.CHEST || block.getType() == Material.LOCKED_CHEST || block.getType() == Material.TRAPPED_CHEST || block.getType() == Material.ENDER_CHEST)
                        && !event.getPlayer().isSneaking())
                    action = "opened";
            if (locked_blocks.containsKey(block) && !locked_blocks.get(block).equals(event.getPlayer().getName())
                    && !(trust_lists.get(locked_blocks.get(block)) != null && trust_lists.get(locked_blocks.get(block)).contains(event.getPlayer().getName()))
                    && !event.getPlayer().hasPermission("myguarddog.admin")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(
                        ChatColor.RED + "Sorry, but this " + myPluginWiki.getItemName(block, false, true, true) + " belongs to " + locked_blocks.get(block)
                                + " and you're not allowed to use it.");
                debug("denied access");
            } else if (action != null)
                saveEvent(new Event(event.getPlayer().getName(), action, block, event.getPlayer().getGameMode() == GameMode.CREATIVE));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void logItemFrameAndPaintingBreaking(HangingBreakByEntityEvent event) {
        String description = event.getEventName() + " (" + event.getRemover().getType().name() + "; " + event.getEntity().getType().name() + ")";
        if (event.isCancelled()) {
            debug(ChatColor.STRIKETHROUGH + description);
            return;
        } else
            debug(description);
        String cause;
        Boolean in_Creative_Mode = null;
        if (event.getRemover() instanceof Player) {
            cause = ((Player) event.getRemover()).getName();
            in_Creative_Mode = ((Player) event.getRemover()).getGameMode() == GameMode.CREATIVE;
        } else
            cause = myPluginWiki.getEntityName(event.getRemover(), true, true, false);
        Event break_event = new Event(cause, "took down", event.getEntity(), in_Creative_Mode);
        saveEvent(break_event);
        debug(ChatColor.WHITE + break_event.save_line);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void logItemFrameAndPaintingPlacing(HangingPlaceEvent event) {
        String description = event.getEventName() + " (" + event.getPlayer().getName() + "; " + event.getEntity().getType().name() + ")";
        if (event.isCancelled()) {
            debug(ChatColor.STRIKETHROUGH + description);
            return;
        } else
            debug(description);
        Event place_event = new Event(event.getPlayer().getName(), "hung", event.getEntity(), event.getPlayer().getGameMode() == GameMode.CREATIVE);
        saveEvent(place_event);
        debug(ChatColor.WHITE + place_event.save_line);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void logExplosions(EntityExplodeEvent event) {
        if (event.getEntity() == null)
            return;
        String description = event.getEventName() + " (" + event.getEntity().getType().name() + ")";
        if (event.isCancelled()) {
            debug(ChatColor.STRIKETHROUGH + description);
            return;
        } else
            debug(description);
        // identify the cause of the event
        String cause = myPluginWiki.getEntityName(event.getEntity(), true, true, false), action = "blew up";
        if (event.getEntityType() == EntityType.CREEPER) {
            double distance = 10000;
            for (Entity entity : event.getEntity().getNearbyEntities(6, 6, 6))
                if (entity.getType() == EntityType.PLAYER
                        && distance > Math.sqrt(Math.pow(event.getEntity().getLocation().getX() - entity.getLocation().getX(), 2)
                                + Math.pow(event.getEntity().getLocation().getY() - entity.getLocation().getY(), 2)
                                + Math.pow(event.getEntity().getLocation().getZ() - entity.getLocation().getZ(), 2))) {
                    cause = ((Player) entity).getName();
                    action = "creeper'd";
                    // distance = SQRT((SQRT((dx^2) + (dy^2)))^2 + dz^2) = SQRT(dx^2 + dy^2 + dz^2)
                    distance =
                            Math.sqrt(Math.pow(event.getEntity().getLocation().getX() - entity.getLocation().getX(), 2)
                                    + Math.pow(event.getEntity().getLocation().getY() - entity.getLocation().getY(), 2)
                                    + Math.pow(event.getEntity().getLocation().getZ() - entity.getLocation().getZ(), 2));
                }
            if (!action.equals("creeper'd"))
                tellOps(ChatColor.DARK_RED + "I couldn't find the cause of the recent creeper explosion!", true);
        } else if (event.getEntityType() == EntityType.PRIMED_TNT) {
            // try to find out who placed the T.N.T.
            // first, see if another explosion caused the ignition of this one
            if (TNT_causes.get(event.getEntity().getUniqueId()) != null) {
                cause = TNT_causes.get(event.getEntity().getUniqueId());
                TNT_causes.remove(event.getEntity().getUniqueId());
            } else {
                cause = findCause(event.getLocation(), 2, "placed", "some T.N.T.");
                if (cause == null) {
                    cause = "some T.N.T.";
                    tellOps(ChatColor.DARK_RED + "I couldn't find the cause of the recent T.N.T. explosion!", true);
                }
            }
        } else if (event.getEntityType() == EntityType.MINECART_TNT) {
            // try to find out who placed the activator rail. When a T.N.T. minecart is activated, it's recorded in TNT_causes.
            if (TNT_causes.get(event.getEntity().getUniqueId()) != null) {
                cause = TNT_causes.get(event.getEntity().getUniqueId());
                TNT_causes.remove(event.getEntity().getUniqueId());
            } else
                tellOps(ChatColor.DARK_RED + "I couldn't find the cause of this T.N.T. minecart explosion!", true);
        }
        // organize the block list so that it saves the events from top to bottom so that when you roll it back, it will repair the bottom first, which is
        // important with falling blocks
        int lowest_y = -1;
        ArrayList<Block> block_list = new ArrayList<Block>();
        for (Block block : event.blockList())
            if (lowest_y == -1 || lowest_y > block.getY())
                lowest_y = block.getY();
        for (int i = lowest_y; i < event.getLocation().getWorld().getMaxHeight(); i++)
            for (int j = 0; j < event.blockList().size(); j++)
                if (event.blockList().get(j).getY() == i) {
                    block_list.add(event.blockList().get(j));
                    if (block_list.size() == event.blockList().size()) {
                        i = event.getLocation().getWorld().getMaxHeight();
                        break;
                    }
                }
        for (Block block : block_list)
            // if "T.N.T. blew up T.N.T.", find the UUID of the primed T.N.T. created by this explosion so it can be tracked and the cause of
            // the first T.N.T. can be used as the cause of this newly primed T.N.T.
            if (block.getType() != Material.TNT && block.getType() != Material.FIRE)
                checkForReactionBreaks(new Event(cause, action, block, null), block_list);
            else if (block.getType() == Material.TNT) {
                // find the PRIMED_TNT Entity closest to the block
                debug("explosion triggered more T.N.T.; tracking...");
                server.getScheduler().scheduleSyncDelayedTask(mGD, new myGuardDog$1(console, "track T.N.T.", block.getLocation(), cause), 1);
            }
        // through experimentation, I discovered that if an entity explodes and the explosion redirects a PRIMED_TNT Entity, it actually replaces the old
        // PRIMED_TNT Entity with a new one going a different direction. That's important because it changes the UUID of the PRIMED_TNT and I use that to track
        // the explosions with the TNT_causes HashMap. Therefore, here, I need to make it track any PRIMED_TNT Entities inside the blast radius
        // find any PRIMED_TNT Entities within the blast radius (which maxes out at 7 for Minecraft T.N.T. in air)
        for (Entity entity : event.getLocation().getWorld().getEntities())
            // max distance = 7; max distance squared = 49
            if (entity.getType() == EntityType.PRIMED_TNT && entity.getLocation().distanceSquared(event.getLocation()) < 49) {
                debug("explosion changed primed T.N.T.; tracking...");
                server.getScheduler().scheduleSyncDelayedTask(mGD, new myGuardDog$1(console, "track T.N.T.", entity.getLocation(), cause), 1);
            }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void logNaturalIgnitions(BlockIgniteEvent event) {
        String description = event.getEventName() + " (" + event.getCause().name() + "; " + event.getBlock().getType().name() + ":" + event.getBlock().getData() + ")";
        if (event.isCancelled()) {
            debug(ChatColor.STRIKETHROUGH + description);
            return;
        } else
            debug(description);
        // don't log if a player did it (because it will be logged more already and more accurately in logBlockPlace() because apparently it considers someone
        // lighting something on fire the same as someone placing fire), or if it was fire spread (because the spread is unimportant--only the blocks it breaks
        // are important, which are logged in logFireDamage())
        if (event.getPlayer() == null && event.getCause() != IgniteCause.FLINT_AND_STEEL) {
            String cause = null;
            if (event.getCause() == IgniteCause.LAVA)
                cause = "some lava";
            else if (event.getCause() == IgniteCause.LIGHTNING)
                cause = "some lightning";
            else if (event.getCause() == IgniteCause.FIREBALL)
                cause = "a fireball";
            else {
                debug("fire spread; no log");
                return;
            }
            Event ignition_event = new Event(cause, "set fire to", event.getBlock(), null);
            saveEvent(ignition_event);
            debug(ChatColor.WHITE + ignition_event.save_line);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void logFireDamage(BlockBurnEvent event) {
        String description = event.getEventName() + " (" + event.getBlock().getType().name() + ":" + event.getBlock().getData() + ")";
        if (event.isCancelled()) {
            debug(ChatColor.STRIKETHROUGH + description);
            return;
        } else
            debug(description);
        // try checking for fire-setting events
        String cause = findCause(event.getBlock().getLocation(), 1, "set fire to", null, "burned", null, "placed", "some lava", "spread", "some lava");
        if (cause == null) {
            debug("natural causes; no log");
            return;
        }
        checkForReactionBreaks(new Event(cause, "burned", event.getBlock(), null), null);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void logWaterAndLavaPlacement(PlayerBucketEmptyEvent event) {
        // TODO track lava/water mixing to form cobblestone, stone, or obsidian
        String description = event.getEventName() + " (" + event.getPlayer().getName() + "; " + event.getBucket().name() + ")";
        if (event.isCancelled()) {
            debug(ChatColor.STRIKETHROUGH + description);
            return;
        } else
            debug(description);
        String action = "dumped out", object = "something";
        if (event.getBucket() == Material.WATER_BUCKET) {
            action = "placed";
            object = "some water";
        } else if (event.getBucket() == Material.LAVA_BUCKET) {
            action = "placed";
            object = "some lava";
        } else
            tellOps(ChatColor.DARK_RED + "Someone emptied a bucket with something with the I.D. " + object + ". I've never heard of anything with the I.D. " + object
                    + ". Can you make sure myPluginWiki is up to date?", true);
        Event bucket_event =
                new Event(event.getPlayer().getName(), action, object, event.getBlockClicked().getRelative(event.getBlockFace()).getLocation(), event.getPlayer()
                        .getGameMode() == GameMode.CREATIVE);
        saveEvent(bucket_event);
        debug(ChatColor.WHITE + bucket_event.save_line);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void logWaterAndLavaRemoval(PlayerBucketFillEvent event) {
        String description = event.getEventName() + " (" + event.getPlayer().getName() + "; " + event.getBucket().name() + ")";
        if (event.isCancelled()) {
            debug(ChatColor.STRIKETHROUGH + description);
            return;
        } else
            debug(description);
        Location event_location = event.getBlockClicked().getRelative(event.getBlockFace()).getLocation();
        String object = myPluginWiki.getItemName(event_location.getBlock(), true, true, false);
        if (object == null) {
            object = "something with the I.D. " + event_location.getBlock().getTypeId();
            if (event_location.getBlock().getData() > 0)
                object += ":" + event_location.getBlock().getData();
            tellOps(ChatColor.DARK_RED + "Someone filled a bucket with " + object
                    + ". I've never heard of anything with that I.D. Can you make sure myPluginWiki is up to date?", true);
        }
        Event bucket_event = new Event(event.getPlayer().getName(), "removed", object, event_location, event.getPlayer().getGameMode() == GameMode.CREATIVE);
        saveEvent(bucket_event);
        debug(ChatColor.WHITE + bucket_event.save_line);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void logEndermanBlockInteractionsAndSandAndGravelFalling(EntityChangeBlockEvent event) {
        String description =
                event.getEventName() + " (" + event.getEntityType().name() + "; " + event.getBlock().getType().name() + ":" + event.getBlock().getData() + " >> "
                        + event.getTo().name() + ")";
        if (event.isCancelled()) {
            debug(ChatColor.STRIKETHROUGH + description);
            return;
        } else
            debug(description);
        if (event.getEntityType() == EntityType.ENDERMAN) {
            if (prevent_Enderman_interactions) {
                event.setCancelled(true);
                debug("prevented Enderman interaction");
                return;
            }
            if (event.getBlock().getTypeId() != 0) {
                Event pickup_event = new Event("an Enderman", "picked up", event.getBlock(), null);
                saveEvent(pickup_event);
                debug(ChatColor.WHITE + pickup_event.save_line);
            } else
                server.getScheduler().scheduleSyncDelayedTask(this, new myGuardDog$1(console, "track Enderman placements", event.getBlock(), null), 1);
        } else if (event.getEntityType() == EntityType.FALLING_BLOCK) {
            String cause = null, action, object;
            // figure out whether this was sand or gravel
            if (event.getTo() == Material.SAND || event.getBlock().getType() == Material.SAND)
                object = "some sand";
            else
                object = "some gravel";
            debug("The event involved " + object + ".");
            // figure out whether this event is a landing or a dropping
            if (event.getTo() == Material.SAND || event.getTo() == Material.GRAVEL) {
                cause = falling_block_causes.get(event.getEntity().getUniqueId());
                action = "relocated";
                debug("\"relocated\"");
            } else {
                action = "dropped";
                debug("\"dropped\"");
                // figure out the cause
                cause = findCause(new Location(event.getBlock().getWorld(), event.getBlock().getX(), event.getBlock().getY() - 1, event.getBlock().getZ()), 0, false, null);
                if (cause == null)
                    cause = findCause(event.getBlock().getLocation(), 0, "placed", object);
                if (cause != null)
                    falling_block_causes.put(event.getEntity().getUniqueId(), cause);
            }
            if (cause == null) {
                debug("natural causes; no log");
                return;
            }
            Event gravity_event = new Event(cause, action, object, event.getBlock().getLocation(), null);
            saveEvent(gravity_event);
            debug(ChatColor.WHITE + gravity_event.save_line);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void logTreeAndGiantMushroomGrowth(StructureGrowEvent event) {
        String player_name = "[null Player]";
        if (event.getPlayer() != null)
            player_name = event.getPlayer().getName();
        String description = event.getEventName() + " (" + player_name + "; " + event.getBlocks().get(0).getType().name() + ":" + event.getBlocks().get(0).getData() + ")";
        if (event.isCancelled()) {
            debug(ChatColor.STRIKETHROUGH + description);
            return;
        } else
            debug(description);
        // make sure that it only logs growth of trees and giant mushrooms by making sure the number of blocks is >1
        if (event.getBlocks().size() <= 1) {
            debug("<2-block structure; no log");
            return;
        }
        String cause;
        if (event.getPlayer() != null)
            cause = event.getPlayer().getName();
        else
            // if it doesn't log the player, find the player who planted it
            cause =
                    findCause(event.getBlocks().get(1).getLocation(), 0, "placed", new String[] { "an oak sapling", "a birch sapling", "a spruce sapling", "a jungle sapling",
                            "a brown mushroom", "a red mushroom" });
        if (cause == null) {
            debug("natural causes; no log");
            return;
        }
        String object = null;
        // logs as "a tree" for leaves or logs
        if (event.getBlocks().get(1).getType() == Material.LOG || event.getBlocks().get(1).getType() == Material.LEAVES)
            object = "a tree";
        // 100 = giant red mushroom
        else if (event.getBlocks().get(1).getTypeId() == 100)
            object = "a giant red mushroom";
        // 99 = giant brown mushroom
        else if (event.getBlocks().get(1).getTypeId() == 99)
            object = "a giant brown mushroom";
        else {
            tellOps(ChatColor.DARK_RED + "There was an unidentified StructureGrowEvent at " + myPluginUtils.writeLocation(event.getBlocks().get(1).getBlock())
                    + ".\ntype I.D. = " + ChatColor.WHITE + event.getBlocks().get(1).getTypeId(), true);
            return;
        }
        for (BlockState block : event.getBlocks()) {
            // differentiate between different parts of the structure so events involving it can be rolled back or restored
            object += " (" + myPluginWiki.getItemName(block.getTypeId(), block.getData().getData(), true, true, false) + ")";
            Event grow_event = new Event(cause, "grew", object, block.getBlock().getLocation(), event.getPlayer().getGameMode() == GameMode.CREATIVE);
            saveEvent(grow_event);
            debug(ChatColor.WHITE + grow_event.save_line);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void logLeafDecay(LeavesDecayEvent event) {
        String description = event.getEventName() + " (" + event.getBlock().getType().name() + ":" + event.getBlock().getData() + ")";
        if (event.isCancelled()) {
            debug(ChatColor.STRIKETHROUGH + description);
            return;
        } else
            debug(description);
        String cause =
                findCause(event.getBlock().getLocation(), 4, false, new String[] { "an oak log", "a birch log", "a spruce log", "a jungle log", "some oak leaves",
                        "some birch leaves", "some spruce leaves", "some jungle leaves" });
        if (cause == null) {
            debug("natural causes; no log");
            return;
        }
        Event decay_event = new Event(cause, "decayed", event.getBlock(), null);
        saveEvent(decay_event);
        debug(ChatColor.WHITE + decay_event.save_line);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void logMobKillings(EntityDeathEvent event) {
        debug(event.getEventName() + " (" + event.getEntity().getType().name() + ")");
        if (event.getEntity().getKiller() == null) {
            debug("natural causes; no log");
            return;
        }
        String target;
        if (event.getEntity() instanceof Player) {
            target = ((Player) event.getEntity()).getName();
            debug("name = \"" + ((Player) event.getEntity()).getName() + "\"");
        } else
            target = myPluginWiki.getEntityName(event.getEntity(), true, true, false);
        Event kill_event =
                new Event(event.getEntity().getKiller().getName(), "killed", target, event.getEntity().getLocation(),
                        event.getEntity().getKiller().getGameMode() == GameMode.CREATIVE);
        saveEvent(kill_event);
        debug(ChatColor.WHITE + kill_event.save_line);
    }

    // loading
    private void loadTheLockedBlocks(CommandSender sender) {
        locked_blocks = new HashMap<Block, String>();
        File locked_blocks_file = new File(getDataFolder(), "locked blocks.txt");
        // read the locked blocks.txt file
        try {
            if (!locked_blocks_file.exists()) {
                getDataFolder().mkdir();
                sender.sendMessage(COLOR + "I couldn't find a locked blocks.txt file. I'll make a new one.");
                locked_blocks_file.createNewFile();
                return;
            }
            BufferedReader in = new BufferedReader(new FileReader(locked_blocks_file));
            String save_line = in.readLine();
            boolean passed_border = false;
            while (save_line != null && !save_line.trim().equals("")) {
                save_line = save_line.trim();
                debug("\"" + ChatColor.WHITE + save_line + COLOR + "\"");

                // check for the border
                if (myPluginUtils.isBorder(save_line)) {
                    passed_border = true;
                    save_line = in.readLine();
                    continue;
                }

                // read the save line
                if (!passed_border) {
                    // [player] locked [block type] at [location].
                    // get the location
                    Location location = myPluginUtils.readLocation(save_line.substring(save_line.indexOf('('), save_line.length() - 1));
                    if (location == null) {
                        tellOps(ChatColor.DARK_RED + "I couldn't read the location on this locked block save line:\n\"" + ChatColor.WHITE + save_line + ChatColor.DARK_RED
                                + "\"\nI read this as the location:\n\"" + ChatColor.WHITE + save_line.substring(save_line.indexOf('('), save_line.length() - 1)
                                + ChatColor.DARK_RED + "\"", true);
                        continue;
                    }

                    locked_blocks.put(location.getBlock(), save_line.split(" locked ")[0]);
                } else {
                    // [player] trusts ["no one"/list of players].
                    String list = save_line.substring(save_line.indexOf(' ') + 8, save_line.length() - 1);
                    ArrayList<String> trusted_players = new ArrayList<String>();
                    if (!list.equals("no one"))
                        trusted_players = myPluginUtils.readArrayList(list);
                    trust_lists.put(save_line.split(" ")[0], trusted_players);
                }
                save_line = in.readLine();
            }
            in.close();
            saveTheLockedBlocks(sender, false);
        } catch (IOException exception) {
            tellOps(ChatColor.DARK_RED + "I got an IOException while trying to save your locked blocks.", true);
            exception.printStackTrace();
            return;
        }
        // send the sender a confirmation message
        myPluginUtils.displayLoadConfirmation(sender, COLOR, locked_blocks.size(), "locked block");
        myPluginUtils.displayLoadConfirmation(sender, COLOR, trust_lists.size(), "trust list");
    }

    // saving
    @Deprecated
    public static synchronized void oldSaveEvent(Event event) {
        logs_folder.mkdirs();
        chrono_logs_folder.mkdirs();
        position_logs_folder.mkdirs();
        cause_logs_folder.mkdirs();
        // don't attempt to save ANYTHING if a roll back is in progress
        if (roll_back_in_progress) {
            debug("roll back in progress; event waiting list size = " + event_waiting_list.size());
            event_waiting_list.add(event);
            return;
        }
        // if there are other events on the waiting list, place event at the end of the line and reassign event to the first event on the waiting list
        if (event_waiting_list.size() > 0) {
            debug("event waiting list size = " + event_waiting_list.size());
            event_waiting_list.add(event);
            event = event_waiting_list.get(0);
        }
        try {
            // save up to 5 events at once from the waiting list (if that many are on the waiting list)
            for (int i = 0; i < 5; i++) {
                debug("saving " + (i + 1) + "...");
                // save to the chronological logs
                BufferedWriter out;
                if (latest_chrono_log != null) {
                    out = new BufferedWriter(new FileWriter(latest_chrono_log, true));
                    out.newLine();
                } else {
                    debug("creating new chrono log file...");
                    latest_chrono_log = new File(chrono_logs_folder, event.getDate('-') + " " + event.getTime('\'') + " - now.mGDlog");
                    latest_chrono_log.createNewFile();
                    out = new BufferedWriter(new FileWriter(latest_chrono_log, true));
                }
                out.write(event.save_line);
                out.close();
                // split the chrono logs if necessary (23592960B = 22.5MB = max chrono log file size =~ 250,000 events)
                // TODO TEMP: "5000" normally "23592960"
                if (latest_chrono_log.length() > 5000) {
                    if (latest_chrono_log.renameTo(new File(chrono_logs_folder, latest_chrono_log.getName().split(" - ")[0] + " - " + event.getDate('-') + " "
                            + event.getTime('\'') + ".mGDlog")))
                        debug("renamed chrono log for splitting");
                    else
                        tellOps(ChatColor.DARK_RED + "There was an issue renaming this chrono log!", true);
                    // setting latest_chrono_log to null here will make it create an appropriately named new chrono log file the next time this method is run
                    latest_chrono_log = null;
                }
                // save to the position logs
                File position_log = new File(position_logs_folder, "x = " + event.x + " " + event.world.getWorldFolder().getName() + ".mGDlog");
                // we have to turn off "append" in the writer if the line being written is the first line; otherwise, there will be an empty line at the
                // beginning of the file that we don't want
                out = new BufferedWriter(new FileWriter(position_log, true));
                if (!position_log.exists()) {
                    debug("no position log; creating new file...");
                    position_log.createNewFile();
                } else
                    out.newLine();
                out.write(event.save_line);
                out.close();
                // save to the cause logs
                File cause_log = new File(cause_logs_folder, event.cause + ".mGDlog");
                out = new BufferedWriter(new FileWriter(cause_log, true));
                if (!cause_log.exists()) {
                    debug("no cause log; creating new file...");
                    cause_log.createNewFile();
                } else
                    out.newLine();
                out.write(event.save_line);
                out.close();
                if (event_waiting_list.size() > 0) {
                    event_waiting_list.remove(0);
                    if (event_waiting_list.size() > 0)
                        event = event_waiting_list.get(0);
                    else {
                        debug("event waiting list followed through");
                        break;
                    }
                } else
                    break;
            }
        } catch (IOException exception) {
            // an IOException can happen if another part of this plugin (like the rollback) is already using the file we're trying to write in. In such a case,
            // add the currently saving event to the event waiting list and schedule an event to try to save it again in 2 ticks
            debug("interference with save; will reattempt in 0.1s");
            server.getScheduler().scheduleSyncDelayedTask(mGD, new myGuardDog$1(null, "retry save", event), 2);
            return;
        }
    }

    public static synchronized void saveEvent(Event event) {
        logs_folder.mkdirs();
        chrono_logs_folder.mkdirs();
        position_logs_folder.mkdirs();
        cause_logs_folder.mkdirs();
        // don't attempt to save ANYTHING if a roll back is in progress
        if (roll_back_in_progress) {
            debug("roll back in progress; event waiting list size = " + event_waiting_list.size());
            event_waiting_list.add(event);
            return;
        }
        // if there are other events on the waiting list, place event at the end of the line and reassign event to the first event on the waiting list
        if (event_waiting_list.size() > 0) {
            debug("event waiting list size = " + event_waiting_list.size());
            event_waiting_list.add(event);
            event = event_waiting_list.get(0);
        }
        try {
            // save up to 5 events at once from the waiting list (if that many are on the waiting list)
            for (int i = 0; i < 5; i++) {
                debug("saving " + (i + 1) + "...");
                // save to the chronological logs
                if (latest_chrono_log == null) {
                    debug("making new chrono log file...");
                    latest_chrono_log = new File(chrono_logs_folder, event.getDate('-') + " " + event.getTime('\'') + " - now.mGDlog");
                    latest_chrono_log.createNewFile();
                }
                RandomAccessFile out = new RandomAccessFile(latest_chrono_log, "rw");
                // seek to the end of the file to write to append the new event save line
                out.seek(out.length());
                out.writeUTF(event.save_line);
                // retrieve and store the length here so that we can close the in, then rename the log file if needed
                long file_length = out.length();
                out.close();
                // split the chrono logs if necessary (23592960B = 22.5MB = max chrono log file size =~ 250,000 events)
                if (file_length >= 23592960) {
                    if (latest_chrono_log.renameTo(new File(chrono_logs_folder, latest_chrono_log.getName().split(" - ")[0] + " - " + event.getDate('-') + " "
                            + event.getTime('\'') + ".mGDlog")))
                        debug("renamed chrono log for splitting");
                    else
                        tellOps(ChatColor.DARK_RED + "There was an issue renaming this chrono log!", true);
                    // setting latest_chrono_log to null here will make it create an appropriately named new chrono log file the next time this method is run
                    latest_chrono_log = null;
                }
                // save to the position logs
                File position_log = new File(position_logs_folder, "x = " + event.x + " " + event.world.getWorldFolder().getName() + ".mGDlog");
                if (!position_log.exists())
                    position_log.createNewFile();
                out = new RandomAccessFile(position_log, "rw");
                // seek to the end of the file to write to append the new event save line
                out.seek(out.length());
                out.writeUTF(event.save_line);
                out.close();
                File cause_log = new File(cause_logs_folder, event.cause + ".mGDlog");
                if (!cause_log.exists())
                    cause_log.createNewFile();
                out = new RandomAccessFile(cause_log, "rw");
                // seek to the end of the file to write to append the new event save line
                out.seek(out.length());
                out.writeUTF(event.save_line);
                out.close();
                // continue through event_waiting_list if there are more events waiting; otherwise, break the loop
                if (event_waiting_list.size() > 0) {
                    event_waiting_list.remove(0);
                    if (event_waiting_list.size() > 0)
                        event = event_waiting_list.get(0);
                    else {
                        debug("event waiting list followed through");
                        break;
                    }
                } else
                    break;
            }
        } catch (IOException e) {
            // an IOException can happen if another part of this plugin is already using the file we're trying to write in. In such a case, add the currently
            // saving event to the event waiting list and schedule an event to try to save it again in 2 ticks
            debug("interference with save; will reattempt in 0.1s");
            server.getScheduler().scheduleSyncDelayedTask(mGD, new myGuardDog$1(null, "retry save", event), 2);
        }
    }

    private void saveTheLockedBlocks(CommandSender sender, boolean display_message) {
        File locked_blocks_file = new File(getDataFolder(), "locked blocks.txt");
        // save the locked blocks
        try {
            if (!locked_blocks_file.exists()) {
                getDataFolder().mkdir();
                sender.sendMessage(COLOR + "I couldn't find a locked blocks.txt file. I'll make a new one.");
                locked_blocks_file.createNewFile();
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(locked_blocks_file));
            for (int i = 0; i < locked_blocks.size(); i++) {
                Block block = (Block) locked_blocks.keySet().toArray()[i];
                // [player] locked [block type] at [location].
                out.write(locked_blocks.get(block) + " locked " + myPluginWiki.getItemName(block, false, true, false) + " at " + myPluginUtils.writeLocation(block) + ".");
                out.newLine();
            }
            out.write(myPluginUtils.border());
            out.newLine();
            for (int i = 0; i < trust_lists.size(); i++) {
                String name = (String) trust_lists.keySet().toArray()[i], list;
                // [player] trusts ["no one"/list of players].
                if (trust_lists.get(name).size() == 0)
                    list = "no one";
                else
                    list = myPluginUtils.writeArrayList(trust_lists.get(name));
                out.write(name + " trusts " + list + ".");
                if (i < trust_lists.size() - 1)
                    out.newLine();
            }
            out.close();
        } catch (IOException exception) {
            sender.sendMessage(ChatColor.DARK_RED + "I got an IOException while trying to save your locked blocks.");
            exception.printStackTrace();
            return;
        }
        if (display_message) {
            myPluginUtils.displaySaveConfirmation(sender, COLOR, locked_blocks.size(), "locked block");
            myPluginUtils.displaySaveConfirmation(sender, COLOR, trust_lists.size(), "trust list");
        }
    }

    // plugin commands
    @SuppressWarnings("resource")
    private void checkForUpdates(CommandSender sender) {
        URL url = null;
        try {
            url = new URL("http://dev.bukkit.org/server-mods/realdrummers-myguarddog/files.rss/");
        } catch (MalformedURLException exception) {
            sender.sendMessage(ChatColor.DARK_RED + "This unit's U.R.L. is unacceptable!");
        }
        if (url != null) {
            String new_version_name = null, new_version_link = null;
            try {
                // Set header values intial to the empty string
                String title = "";
                String link = "";
                // First create a new XMLInputFactory
                XMLInputFactory inputFactory = XMLInputFactory.newInstance();
                // Setup a new eventReader
                InputStream in = null;
                try {
                    in = url.openStream();
                } catch (IOException e) {
                    sender.sendMessage(ChatColor.DARK_RED + "BukkitDev is remaining radio silent.");
                    return;
                }
                XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
                // Read the XML document
                while (eventReader.hasNext()) {
                    XMLEvent event = eventReader.nextEvent();
                    if (event.isStartElement()) {
                        if (event.asStartElement().getName().getLocalPart().equals("title")) {
                            event = eventReader.nextEvent();
                            title = event.asCharacters().getData();
                            continue;
                        }
                        if (event.asStartElement().getName().getLocalPart().equals("link")) {
                            event = eventReader.nextEvent();
                            link = event.asCharacters().getData();
                            continue;
                        }
                    } else if (event.isEndElement()) {
                        if (event.asEndElement().getName().getLocalPart().equals("item")) {
                            new_version_name = title;
                            new_version_link = link;
                            // All done, we don't need to know about older
                            // files.
                            break;
                        }
                    }
                }
            } catch (XMLStreamException exception) {
                sender.sendMessage(ChatColor.DARK_RED + "The enemy has detonated an XMLStreamException in our trenches!");
                return;
            }
            boolean new_version_is_out = false;
            String version = getDescription().getVersion(), newest_online_version = "";
            if (new_version_name == null) {
                tellOps(ChatColor.DARK_RED + "I am afraid that there were complications while attempting to retrieve the name of the new version of ", true);
                return;
            }
            if (new_version_name.split("v").length == 2) {
                newest_online_version = new_version_name.split("v")[new_version_name.split("v").length - 1].split(" ")[0];
                // get the newest file's version number
                if (!version.contains("-DEV") && !version.contains("-PRE") && !version.equalsIgnoreCase(newest_online_version))
                    try {
                        if (Double.parseDouble(version) < Double.parseDouble(newest_online_version))
                            new_version_is_out = true;
                    } catch (NumberFormatException exception) {
                        //
                    }
            } else
                sender.sendMessage(ChatColor.RED + "Get General REALDrummer on the line! This plugin is missing its version in the title!");
            if (new_version_is_out) {
                String fileLink = null;
                try {
                    // Open a connection to the page
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(new_version_link).openConnection().getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null)
                        // Search for the download link
                        if (line.contains("<li class=\"user-action user-action-download\">"))
                            // Get the raw link
                            fileLink = line.split("<a href=\"")[1].split("\">Download</a>")[0];
                    reader.close();
                    reader = null;
                } catch (Exception exception) {
                    sender.sendMessage(ChatColor.DARK_RED + "BukkitDev is remaining radio silent.");
                    exception.printStackTrace();
                    return;
                }
                if (fileLink != null) {
                    if (!new File(this.getDataFolder(), "jar").exists()) {
                        BufferedInputStream in = null;
                        FileOutputStream fout = null;
                        try {
                            getDataFolder().mkdirs();
                            // download the file
                            url = new URL(fileLink);
                            in = new BufferedInputStream(url.openStream());
                            fout = new FileOutputStream(this.getDataFolder().getAbsolutePath() + "/jar");
                            byte[] data = new byte[1024];
                            int count;
                            while ((count = in.read(data, 0, 1024)) != -1)
                                fout.write(data, 0, count);
                            myPluginUtils
                                    .tellOps(
                                            COLOR
                                                    + ""
                                                    + ChatColor.UNDERLINE
                                                    + "Operation myGuardDog has moved on to stage v"
                                                    + newest_online_version
                                                    + ". At your discretion, please unload the obsolete myGuardDog (or halt your current operations) and replace your old weaponry with the new one in your myGuardDog data folder.",
                                            true);
                        } catch (Exception ex) {
                            sender.sendMessage(ChatColor.DARK_RED
                                    + "Mission failure: myGuardDog v"
                                    + newest_online_version
                                    + " has been released; however, the download has been hijacked. Now, it seems, that you are the only one who can infiltrate BukkitDev and acquire the new tech. God speed, soldier.");
                        } finally {
                            try {
                                if (in != null)
                                    in.close();
                                if (fout != null)
                                    fout.close();
                            } catch (Exception ex) {
                                //
                            }
                        }
                    } else
                        sender.sendMessage(ChatColor.RED
                                + "Soldier! Why is our new weaponry still lying useless in the myGuardDog data folder?! Step to, maggot! I want that new weaponry on this server five minutes ago!");
                }
            } else
                sender.sendMessage(COLOR + "Our myGuardDog tools are still the newest tech available.");
        }
    }

    private void convert(CommandSender sender, String[] parameters) {
        String log_name = myPluginUtils.combine(parameters).toLowerCase();
        // if the log name contains a file extension, remove it
        if (log_name.contains("."))
            log_name = log_name.substring(0, log_name.lastIndexOf('.'));
        debug("log name: \"" + log_name + "\"");
        File folder = null, log_file = null;
        if (log_name.startsWith("x=")) {
            debug("searching position logs...");
            folder = position_logs_folder;
        } else if (log_name.contains("'") || log_name.contains("-") || log_name.startsWith("0") || log_name.startsWith("1") || log_name.startsWith("2")
                || log_name.startsWith("3") || log_name.startsWith("4") || log_name.startsWith("5") || log_name.startsWith("6") || log_name.startsWith("7")
                || log_name.startsWith("8") || log_name.startsWith("9")) {
            debug("searching chrono logs...");
            folder = chrono_logs_folder;
        } else {
            String completed_log_name = myPluginUtils.getFullName(log_name);
            if (completed_log_name != null)
                log_name = completed_log_name;
            debug("searching cause logs...");
            folder = cause_logs_folder;
        }
        for (File file : folder.listFiles())
            if (myPluginUtils.replaceAll(file.getName().toLowerCase(), " ", "").startsWith(log_name.toLowerCase())) {
                log_file = file;
                break;
            }
        if (log_file == null) {
            sender.sendMessage(ChatColor.RED + "I was unable to locate any log files designated as \"" + log_name + "\".");
            return;
        }
        try {
            RandomAccessFile in = new RandomAccessFile(log_file, "r");
            File converted_file = new File(mGD.getDataFolder(), log_file.getName().substring(0, log_file.getName().lastIndexOf('.')) + ".txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(converted_file));
            if (!converted_file.exists())
                converted_file.createNewFile();
            try {
                // this loop is terminated by an EOFException
                while (true) {
                    out.write(in.readUTF());
                    out.newLine();
                }
            } catch (EOFException e) {
                in.close();
                out.close();
            }
            sender.sendMessage(ChatColor.YELLOW + "Mission complete; a .txt version of " + log_file.getName() + " has been created in the myGuardDog plugin data folder.");
        } catch (IOException e) {
            myPluginUtils.processException(mGD, "I got an IOException while trying to convert " + log_file.getName() + " to a .txt!", e);
            if (sender instanceof Player && !sender.isOp())
                sender.sendMessage(ChatColor.RED + "An error occurred while attempting to complete this mission!");
        }
    }

    @SuppressWarnings("unused")
    private void rollBack(CommandSender sender, String[] parameters) {
        // this method reads the roll back parameters and figures out which logs should be searched for events; the rest is handled in the different roll back
        // parts in myGuardDog$1.class
        myGuardDog.roll_back_in_progress = true;
        // TODO: put a confirmation of start message somewhere in here after we have the number of log files that we need to look through
        // read all the parameters
        int radius = -1;
        ArrayList<String> causes = new ArrayList<String>(), actions = new ArrayList<String>(), objects = new ArrayList<String>();
        ArrayList<File> relevant_log_files = new ArrayList<File>();
        for (int i = 0; i < parameters.length; i++) {
            if (!(sender instanceof Player)
                    && (parameters[i].toLowerCase().startsWith("r:") || parameters[i].toLowerCase().startsWith("rad:") || parameters[i].toLowerCase().startsWith("radius:"))) {
                sender.sendMessage(ChatColor.RED + "How can you specify an area around you to roll back? You have no position! You're a console!");
                myGuardDog.roll_back_in_progress = false;
                return;
            } else if (parameters[i].contains(":"))
                if ("causes".startsWith(parameters[i].toLowerCase()) || parameters[i].toLowerCase().startsWith("by:")) {
                    if (parameters[i].indexOf(':') == parameters[i].length() - 1) {
                        sender.sendMessage(ChatColor.RED + "You forgot to list what causes you want me to roll back!");
                        myGuardDog.roll_back_in_progress = false;
                        return;
                    }
                    for (String search : parameters[i].substring(parameters[i].indexOf(':') + 1).split(",")) {
                        // autocomplete the names of the causes
                        String autocompleted = null;
                        if (search.contains("\\")) {
                            search = myPluginUtils.replaceAll(search, "\\", "");
                            // check through the possible non-player event causes
                            for (String non_player_cause : new String[] { "a creeper", "a Ghast", "the Ender Dragon", "an Enderman", "some lava", "some lightning",
                                    "a fireball", "something", "T.N.T.", "myGroundsKeeper" }) {
                                if (non_player_cause.toLowerCase().contains(search.toLowerCase())
                                        && (autocompleted == null || non_player_cause.indexOf(search) < autocompleted.indexOf(search)))
                                    autocompleted = non_player_cause;
                            }
                        } else
                            autocompleted = myPluginUtils.getFullName(search);
                        if (autocompleted == null) {
                            sender.sendMessage(ChatColor.RED
                                    + "Who's \""
                                    + search
                                    + "\"?\nIf you want to search for a non-player cause like creepers, you need to put a \"\\\" in the object's name somewhere so I know not to look for a player.");
                            myGuardDog.roll_back_in_progress = false;
                            return;
                        } else
                            causes.add(autocompleted);
                    }
                } else if ("actions".startsWith(parameters[i].toLowerCase())) {
                    if (parameters[i].indexOf(':') == parameters[i].length() - 1) {
                        sender.sendMessage(ChatColor.RED + "You forgot to list what actions you want me to roll back!");
                        myGuardDog.roll_back_in_progress = false;
                        return;
                    }
                    for (String action : parameters[i].substring(parameters[i].indexOf(':') + 1).split(","))
                        actions.add(action);
                } else if ("objects".startsWith(parameters[i].toLowerCase())) {
                    if (parameters[i].indexOf(':') == parameters[i].length() - 1) {
                        sender.sendMessage(ChatColor.RED + "You forgot to list what objects you want me to roll back!");
                        myGuardDog.roll_back_in_progress = false;
                        return;
                    }
                    for (String object : parameters[i].substring(parameters[i].indexOf(':') + 1).split(","))
                        objects.add(object);
                } else if ("radius".startsWith(parameters[i].toLowerCase()) || "area".startsWith(parameters[i].toLowerCase()))
                    try {
                        if (parameters[i].indexOf(':') == parameters[i].length() - 1) {
                            sender.sendMessage(ChatColor.RED + "You forgot to tell me the radius of the area you want me to roll back!");
                            myGuardDog.roll_back_in_progress = false;
                            return;
                        }
                        radius = Math.abs(Integer.parseInt(parameters[i].substring(parameters[i].indexOf(':') + 1)));
                    } catch (NumberFormatException exception) {
                        sender.sendMessage(ChatColor.RED + "I am being told that \"" + parameters[i].substring(parameters[i].indexOf(':') + 1) + "\" is not a number.");
                        myGuardDog.roll_back_in_progress = false;
                        return;
                    }
                else {
                    sender.sendMessage(ChatColor.RED + "I don't understand \"" + parameters[i] + "\". Please ensure that none of your parameters contain spaces.");
                    myGuardDog.roll_back_in_progress = false;
                    return;
                }
        }
        // find all the relevant log files
        if (causes.size() > 0)
            for (String cause : causes) {
                // start by looking for a file with no numbers (which occur when there are few enough events to write them all in one log file)
                File log_file = new File(myGuardDog.cause_logs_folder, cause + ".mGDlog");
                if (log_file.exists())
                    relevant_log_files.add(log_file);
                else {
                    // if the non-numbered file doesn't exist, look for a bunch of numbered files
                    int counter = 1;
                    log_file = new File(myGuardDog.cause_logs_folder, cause + " (1).mGDlog");
                    while (log_file.exists()) {
                        relevant_log_files.add(log_file);
                        counter++;
                        log_file = new File(myGuardDog.cause_logs_folder, cause + " (" + counter + ").mGDlog");
                    }
                }
            }
        // if a cause wasn't declared, just get all the chronological logs
        else
            for (File log_file : myGuardDog.chrono_logs_folder.listFiles())
                relevant_log_files.add(log_file);
        if (relevant_log_files.size() == 0) {
            sender.sendMessage(ChatColor.RED + "No events have occurred that fit your parameters!");
            myGuardDog.roll_back_in_progress = false;
        } else {
            // TODO: estimate the time it will take to read through the logs and send sender a confirmation message with the number of relevant logs and the
            // estimated time of reading completion

            // estimate the time it will take to read through the logs
            new myGuardDog$1(sender, "roll back", relevant_log_files, causes, actions, objects, radius).run();
        }
    }

    private void trust(Player player, String target) {
        // auto-complete the target's name
        String name = myPluginUtils.getFullName(target);
        if (name == null) {
            player.sendMessage(ChatColor.RED + "I am not familiar with a \"" + target + "\", sir.");
            return;
        }

        // get the player's current trusted players list
        ArrayList<String> trusted_players = trust_lists.get(player.getName());
        if (trusted_players == null)
            trusted_players = new ArrayList<String>();

        if (!trusted_players.contains(name)) {
            // add the target to the player's trusted players list
            trusted_players.add(name);
            trust_lists.put(player.getName(), trusted_players);
            player.sendMessage(COLOR + "Acknowledged. From now on, " + name + " will have access to all of your locked blocks.");
        } else
            // inform the player that their target is already trusted
            player.sendMessage(ChatColor.RED + "You have already declared your trust for " + name + ".");
    }
}