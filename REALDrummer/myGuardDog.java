package REALDrummer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import javax.swing.Timer;

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

public class myGuardDog extends JavaPlugin implements Listener, ActionListener {
	public static Plugin mGD;
	public static Server server;
	public static ConsoleCommandSender console;
	public static ArrayList<Event> events = new ArrayList<Event>();
	public static String[] parameters, enable_messages = { "Server secured.", "BEWARE; MYGUARDDOG.", "Target: Griefers...TARGET LOCKED",
			"Anti-Griefing shields at full power, Captain. Awaiting orders...", "Hasta la vista, griefers." }, disable_messages = { "Until we meet again, pathetic griefers.",
			"Griefers have been successfully pwnd.", "Off duty? There is no such thing.", "Though I am disabled, I do not sleep. Your server is under my protection." };
	public static File logs_folder, chrono_logs_folder, position_logs_folder, cause_logs_folder;
	public static boolean roll_back_in_progress = false, save_in_progress = false, hard_save = false;
	// player_to_inform_of_[...]: keys=player names and values=admin name or "console" who performed the command
	private static HashMap<String, String> players_to_inform_of_halting = new HashMap<String, String>();
	// players_questioned_about_rollback = new HashMap<player's name or "the console", parameters of the rollback>
	private static HashMap<String, String[]> players_questioned_about_rollback = new HashMap<String, String[]>();
	public static HashMap<String, ArrayList<String>> trust_list = new HashMap<String, ArrayList<String>>(), info_messages = new HashMap<String, ArrayList<String>>();
	// inspecting_players=new HashMap<a player using the inspector, Object[] {Location of the last block clicked, int of the number of times the block}>
	private static HashMap<String, Object[]> inspecting_players = new HashMap<String, Object[]>();
	public static HashMap<UUID, String> TNT_causes = new HashMap<UUID, String>();
	private static ArrayList<String> halted_players = new ArrayList<String>(), muted_players = new ArrayList<String>();
	private static Timer autosave_timer;
	public static HashMap<Block, String> locked_blocks = new HashMap<Block, String>();

	// TODO: make sure arrayToList() and listToArray() are only used on lists of items and blocks!!
	// TODO ALL PLUGINS: replace "console.sendMessage(" methods with "tellOps(" methods wherever possible
	// TODO: change all the parts where cause can be "something" or "some lava" to not logging
	// TODO: make /busy top (#) show the busiest people on the server by comparing the sizes of the cause log files
	// TODO: make a turn on PvP command for specific players
	// TODO: make sure players_to_inform HashMaps are saved in temp data as well as offline_player_gamemodes and gamemodes_to_change
	// TODO: /slap ("left"/"right"), /drop (height), and /punch
	// TODO: /set clock would sync your computer's clock with this plugin's clock and calendar by reading the time and date given in the command, logging the
	// difference between that and the server's current time and date, and adding or substracting the appropriate amount of time in the Event.class
	// TODO: check xp changes like gamemode changes

	// plugin enable/disable and the command operator
	public void onEnable() {
		mGD = this;
		server = getServer();
		console = server.getConsoleSender();
		server.getPluginManager().registerEvents(this, this);
		logs_folder = new File(getDataFolder(), "/the logs");
		chrono_logs_folder = new File(logs_folder, "/chronologically");
		position_logs_folder = new File(logs_folder, "/by position");
		cause_logs_folder = new File(logs_folder, "/by cause");
		// 5 minutes = 300,000ms
		autosave_timer = new Timer(300000, this);
		autosave_timer.start();
		loadTheLockedBlocks(console);
		// done enabling
		String enable_message = enable_messages[(int) (Math.random() * enable_messages.length)];
		console.sendMessage(ChatColor.YELLOW + enable_message);
		for (Player player : server.getOnlinePlayers())
			if (player.isOp())
				player.sendMessage(ChatColor.YELLOW + enable_message);
	}

	public void onDisable() {
		// save the server data
		new myGuardDog$1(console, "hard save", true, null).run();
		saveTheLockedBlocks(console, true);
		autosave_timer.stop();
		// done disabling
		String disable_message = disable_messages[(int) (Math.random() * disable_messages.length)];
		console.sendMessage(ChatColor.YELLOW + disable_message);
		for (Player player : server.getOnlinePlayers())
			if (player.isOp())
				player.sendMessage(ChatColor.YELLOW + disable_message);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String command, String[] my_parameters) {
		parameters = my_parameters;
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
				sender.sendMessage(ChatColor.YELLOW + "I halted " + target.getName() + ".");
				String sender_name = "someone on the console";
				if (sender instanceof Player)
					sender_name = sender.getName();
				target.sendMessage(ChatColor.YELLOW + "Don't move or try to use commands; " + sender_name + " halted you.");
				myPluginUtils.tellOps(ChatColor.YELLOW + sender.getName().substring(0, 1).toUpperCase() + sender.getName().substring(1) + " halted " + target.getName() + ".",
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
				sender.sendMessage(ChatColor.YELLOW + "Very well. I will unhalt " + target.getName() + ".");
				halted_players.remove(target.getName());
				String sender_name = "someone on the console";
				if (sender instanceof Player)
					sender_name = sender.getName();
				target.sendMessage(ChatColor.YELLOW + "You're free to go; " + sender_name + " unhalted you.");
				myPluginUtils.tellOps(ChatColor.YELLOW + sender.getName().substring(0, 1).toUpperCase() + sender.getName().substring(1) + " unhalted " + target.getName()
						+ ".", sender instanceof Player, sender.getName(), target.getName());
			}
			return true;
		} else if ((command.equalsIgnoreCase("mGD") || command.equalsIgnoreCase("myGuardDog")) && parameters.length > 1 && parameters[0].equalsIgnoreCase("save")
				&& (parameters[1].equalsIgnoreCase("logs") || (parameters.length > 2 && parameters[1].equalsIgnoreCase("the") && parameters[2].equalsIgnoreCase("logs")))) {
			if (!(sender instanceof Player) || sender.hasPermission("myguarddog.admin")) {
				save_in_progress = true;
				new myGuardDog$1(sender, "save the logs", true, null).run();
			} else if (command.equalsIgnoreCase("myGuardDog"))
				sender.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to use " + ChatColor.YELLOW + "/myGuardDog save" + ChatColor.RED + ".");
			else
				sender.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to use " + ChatColor.YELLOW + "/mGD save" + ChatColor.RED + ".");
			return true;
		} else if ((command.equalsIgnoreCase("mGD") || command.equalsIgnoreCase("myGuardDog")) && parameters.length == 1 && parameters[0].equalsIgnoreCase("save")) {
			if (!(sender instanceof Player) || sender.hasPermission("myguarddog.admin")) {
				new myGuardDog$1(sender, "save the logs", true, null).run();
			} else if (command.equalsIgnoreCase("myGuardDog"))
				sender.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to use " + ChatColor.YELLOW + "/myGuardDog save" + ChatColor.RED + ".");
			else
				sender.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to use " + ChatColor.YELLOW + "/mGD save" + ChatColor.RED + ".");
			return true;
		} else if (command.toLowerCase().startsWith("insp")) {
			if (sender instanceof Player && (sender.hasPermission("myguarddog.inspect") || sender.hasPermission("myguarddog.admin")))
				if (inspecting_players.containsKey(sender.getName())) {
					inspecting_players.remove(sender.getName());
					sender.sendMessage(ChatColor.YELLOW + "Good detective work, Inspector " + sender.getName() + " Holmes.");
				} else {
					inspecting_players.put(sender.getName(), null);
					sender.sendMessage(ChatColor.YELLOW + "I spy with my little eye...A GRIEFER!!");
				}
			else if (!(sender instanceof Player))
				sender.sendMessage(ChatColor.RED + "You're a console! You don't even have an eye to spy with!");
			else
				sender.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to use " + ChatColor.YELLOW + "/" + command.toLowerCase() + ChatColor.RED + ".");
			return true;
		} else if (command.equalsIgnoreCase("rollback") || command.equalsIgnoreCase("rb")) {
			if (!(sender instanceof Player) || sender.hasPermission("myguarddog.rollback") || sender.hasPermission("myguarddog.admin")) {
				// if there are no parameters, that's supposed to mean that you want to roll back ALL events
				// however, since that would be a really, REALLY big change, we want to make sure someone didn't just put no parameters by accident.
				if (parameters.length == 0) {
					sender.sendMessage(ChatColor.YELLOW + "You didn't put any parameters. Do you want to roll back everything that has ever happened on this server?");
					if (sender instanceof Player)
						players_questioned_about_rollback.put(sender.getName(), parameters);
					else
						players_questioned_about_rollback.put("the console", parameters);
					return true;
				}
				if (events.size() > 200)
					sender.sendMessage(ChatColor.YELLOW + "One moment please. I need to save the logs before we start.");
				new myGuardDog$1(sender, "roll back", parameters, null).run();
				// if they used /rollback again after you asked them to confirm their other /rollback command, clearly they want to make changes, so we should
				// ignore the first command by cancelling the question concerning it
				if ((sender instanceof Player && players_questioned_about_rollback.containsKey(sender.getName())))
					players_questioned_about_rollback.remove(sender.getName());
				else if (!(sender instanceof Player) && players_questioned_about_rollback.containsKey("the console"))
					players_questioned_about_rollback.remove("the console");
			} else
				sender.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to use " + ChatColor.YELLOW + "/" + command.toLowerCase() + ChatColor.RED + ".");
			return true;
		}
		return false;
	}

	// intra-command methods
	public static String findCause(String[] actions, String[] objects, Location location) {
		// first, read through the recent events saved in events
		// make sure to read them backwards since the most recent events are at the end
		for (int i = events.size() - 1; i >= 0; i--)
			for (int j = 0; j < actions.length; j++)
				if (events.get(i).action.equals(actions[j]) && (objects[j] == null || events.get(i).objects[0].toLowerCase().startsWith(objects[j].toLowerCase()))
						&& events.get(i).location.equals(location))
					return events.get(i).cause;
		// if you couldn't find the recent event in events, check the logs
		try {
			File file = new File(position_logs_folder, "x = " + location.getBlockX() + " " + location.getWorld().getWorldFolder().getName() + ".txt");
			if (!file.exists())
				return null;
			BufferedReader in = new BufferedReader(new FileReader(file));
			String save_line = in.readLine();
			while (save_line != null) {
				Event event = new Event(save_line);
				for (int j = 0; j < actions.length; j++)
					if (event.action.equals(actions[j]) && (objects[j] == null || event.objects[0].equals(objects[j])) && event.location.equals(location)) {
						in.close();
						return event.cause;
					}
				save_line = in.readLine();
			}
			in.close();
		} catch (IOException exception) {
			console.sendMessage(ChatColor.DARK_RED + "I got an IOException while trying to find the cause of a recent \"" + objects[0] + " " + actions[0]
					+ "\" (or other actions and objects) event!");
			exception.printStackTrace();
			return null;
		}
		// if you couldn't find anything at all, return null
		return null;
	}

	public static String findCause(String action, String object, Location location) {
		// first, read through the recent events saved in events
		// make sure to read them backwards since the most recent events are at the end
		for (int i = events.size() - 1; i >= 0; i--)
			if (events.get(i).action.equals(action) && (object == null || events.get(i).objects[0].toLowerCase().startsWith(object.toLowerCase()))
					&& events.get(i).location.equals(location))
				return events.get(i).cause;
		// if you couldn't find the recent event in events, check the logs
		try {
			File file = new File(position_logs_folder, "x = " + location.getBlockX() + " " + location.getWorld().getWorldFolder().getName() + ".txt");
			if (!file.exists())
				return null;
			BufferedReader in = new BufferedReader(new FileReader(file));
			String save_line = in.readLine();
			while (save_line != null) {
				Event event = new Event(save_line);
				if (event.action.equals(action) && (object == null || event.objects[0].equals(object)) && event.location.equals(location)) {
					in.close();
					return event.cause;
				}
				save_line = in.readLine();
			}
			in.close();
		} catch (IOException exception) {
			console.sendMessage(ChatColor.DARK_RED + "I got an IOException while trying to find the cause of a recent \"" + object + " " + action + "\" event!");
			exception.printStackTrace();
			return null;
		}
		// if you couldn't find anything at all, return null
		return null;
	}

	public static String findCause(boolean placement, String object, Location location) {
		// first, read through the recent events saved in events
		// make sure to read them backwards since the most recent events are at the end
		for (int i = events.size() - 1; i >= 0; i--)
			if ((placement && events.get(i).isPlacement() || !placement && events.get(i).isRemoval())
					&& (object == null || events.get(i).objects[0].toLowerCase().startsWith(object.toLowerCase())) && events.get(i).location.equals(location))
				return events.get(i).cause;
		// if you couldn't find the recent event in events, check the logs
		try {
			File file = new File(position_logs_folder, "x = " + location.getBlockX() + " " + location.getWorld().getWorldFolder().getName() + ".txt");
			if (!file.exists())
				return null;
			BufferedReader in = new BufferedReader(new FileReader(file));
			String save_line = in.readLine();
			while (save_line != null) {
				Event event = new Event(save_line);
				if ((placement && event.isPlacement() || !placement && event.isRemoval()) && (object == null || event.objects[0].equals(object))
						&& event.location.equals(location)) {
					in.close();
					return event.cause;
				}
				save_line = in.readLine();
			}
			in.close();
		} catch (IOException exception) {
			String action = "placement";
			if (!placement)
				action = "removal";
			console.sendMessage(ChatColor.DARK_RED + "I got an IOException while trying to find the cause of a recent \"" + object + " " + action + "\" event!");
			exception.printStackTrace();
			return null;
		}
		// if you couldn't find anything at all, return null
		return null;
	}

	public static void checkForReactionBreaks(Event break_event, ArrayList<Block> exempt_blocks) {
		// check the sides of the broken block for possible reaction breaks
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
			Location location = new Location(break_event.world, x, break_event.y, z);
			if (myPluginWiki.mustBeAttached(location.getBlock(), false) == null) {
				console.sendMessage(ChatColor.DARK_RED + "Hey! For some reason, myPluginWiki can't find info on an item with the I.D. " + location.getBlock().getTypeId()
						+ "! Is myPluginWiki up to date?");
				continue;
			} else if (myPluginWiki.mustBeAttached(location.getBlock(), false) && (exempt_blocks == null || !exempt_blocks.contains(location.getBlock())))
				server.getScheduler().scheduleSyncDelayedTask(
						mGD,
						new myGuardDog$1(console, "track reaction breaks", new Event(break_event.cause, "broke", myPluginWiki.getItemName(location.getBlock(), true, true,
								false), location, break_event.in_Creative_Mode)), 1);
		}
		// check the top of the broken block for possible reaction breaks
		Location location = new Location(break_event.world, break_event.x, break_event.y + 1, break_event.z);
		if (myPluginWiki.mustBeAttached(location.getBlock(), null) && (exempt_blocks == null || !exempt_blocks.contains(location.getBlock())))
			server.getScheduler().scheduleSyncDelayedTask(
					mGD,
					new myGuardDog$1(console, "track reaction breaks", new Event(break_event.cause, "broke", myPluginWiki.getItemName(location.getBlock(), true, true, false),
							location, break_event.in_Creative_Mode)), 1);
	}

	// listeners
	@Override
	public void actionPerformed(ActionEvent event) {
		new myGuardDog$1(console, "save the logs", true, null).run();
	}

	@EventHandler
	public void informPlayersTheyHaveBeenHalted(PlayerJoinEvent event) {

		if (!trust_list.containsKey(event.getPlayer().getName()))
			trust_list.put(event.getPlayer().getName(), new ArrayList<String>());
		if (players_to_inform_of_halting.containsKey(event.getPlayer().getName()))
			event.getPlayer().sendMessage(
					ChatColor.YELLOW + players_to_inform_of_halting.get(event.getPlayer().getName()) + " halted you. Don't move and don't try to use commands.");
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
				event.getPlayer().sendMessage(ChatColor.YELLOW + "Be quiet. You're not allowed to speak.");
		} else if (players_questioned_about_rollback.containsKey(event.getPlayer().getName())) {
			Boolean accepted = myPluginUtils.getResponse(event.getPlayer(), event.getMessage(), null, null);
			if (accepted != null && accepted) {
				event.setCancelled(true);
				if (events.size() > 200)
					event.getPlayer().sendMessage(ChatColor.YELLOW + "One moment please. I need to save the logs before we start.");
				new myGuardDog$1(event.getPlayer(), "roll back", false, parameters).run();
			} else if (accepted != null) {
				event.setCancelled(true);
				players_questioned_about_rollback.remove(event.getPlayer().getName());
				event.getPlayer().sendMessage(ChatColor.YELLOW + "Got it. The rollback has been cancelled.");
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void trackTNTMinecartActivations(VehicleMoveEvent event) {
		if (event.getVehicle().getType() == EntityType.MINECART_TNT && !TNT_causes.containsKey(event.getVehicle().getUniqueId())
				&& event.getTo().getBlock().getType() == Material.ACTIVATOR_RAIL && event.getTo().getBlock().isBlockIndirectlyPowered()) {
			String cause = findCause("placed", "an activator rail", event.getTo());
			if (cause != null)
				TNT_causes.put(event.getVehicle().getUniqueId(), cause);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void logBlockBreakAndInspect(BlockBreakEvent event) {
		// TODO make sure that people can't break locked blocks through reaction blocks
		if (event.isCancelled())
			return;
		// inspect
		if (inspecting_players.containsKey(event.getPlayer().getName())) {
			event.setCancelled(true);
			inspect(event.getPlayer(), event.getBlock().getLocation());
		} // cancel the event if
		else if (locked_blocks.containsKey(event.getBlock()) && !event.getPlayer().getName().equals(locked_blocks.get(event.getBlock()))
				&& !event.getPlayer().hasPermission("myguarddog.admin")) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(
					ChatColor.RED + "This is " + locked_blocks.get(event.getBlock()) + "'s " + myPluginWiki.getItemName(event.getBlock(), false, true, true)
							+ " and you can't break it.");
		} else {
			Event break_event = new Event(event.getPlayer().getName(), "broke", event.getBlock(), event.getPlayer().getGameMode() == GameMode.CREATIVE);
			events.add(break_event);
			checkForReactionBreaks(break_event, null);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void logBlockPlaceAndInspect(BlockPlaceEvent event) {
		if (event.isCancelled())
			return;
		// inspect
		if (inspecting_players.containsKey(event.getPlayer().getName())) {
			event.setCancelled(true);
			inspect(event.getPlayer(), event.getBlock().getLocation());
		} // cancel someone trying to place a hopper below a locked block with an inventory
			// translation: if the player is placing a hopper, the block above the hopper is a lockable container owned by someone else, the player placing the
			// hopper isn't on the container owner's trust list, and the player placing the hopper isn't an admin
		else if (event.getBlock().getType() == Material.HOPPER && myPluginWiki.isLockable(event.getBlock().getRelative(BlockFace.UP), true)
				&& locked_blocks.containsKey(event.getBlock().getRelative(BlockFace.UP))
				&& !event.getPlayer().getName().equals(locked_blocks.get(event.getBlock().getRelative(BlockFace.UP)))
				&& trust_list.get(locked_blocks.get(event.getBlock().getRelative(BlockFace.UP))).contains(event.getPlayer().getName())
				&& !event.getPlayer().hasPermission("myguarddog.admin")) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(
					ChatColor.RED + "Ha ha! You make me laugh! I'm not that stupid. You can't put a hopper below someone else's locked container and steal all their stuff.");
		} else if (event.getPlayer() != null) {
			// lock lockable blocks automatically
			if (myPluginWiki.isLockable(event.getBlock(), null)) {
				locked_blocks.put(event.getBlock(), event.getPlayer().getName());
				event.getPlayer().sendMessage(ChatColor.YELLOW + "I locked your " + myPluginWiki.getItemName(event.getBlock(), false, true, true) + ".");
			}
			// log placements
			// log fire placement as ignition and don't log air placement because that makes no sense!
			if (event.getBlock().getType() != Material.AIR && event.getBlock().getType() != Material.FIRE) {
				events.add(new Event(event.getPlayer().getName(), "placed", event.getBlock(), event.getPlayer().getGameMode().equals(GameMode.CREATIVE)));
				if (event.getBlockReplacedState().getType() != Material.AIR)
					events.add(new Event(event.getPlayer().getName(), "covered", myPluginWiki.getItemName(event.getBlockReplacedState().getTypeId(), event
							.getBlockReplacedState().getData().getData(), true, true, false), event.getBlock().getLocation(),
							event.getPlayer().getGameMode() == GameMode.CREATIVE));
			} // consider "placing fire" the same as "setting fire to" something, but don't bother logging T.N.T. ignition
			else if (event.getBlock().getType() == Material.FIRE && event.getBlockReplacedState().getType() != Material.TNT)
				events.add(new Event(event.getPlayer().getName(), "set fire to", event.getBlock(), event.getPlayer().getGameMode().equals(GameMode.CREATIVE)));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void logPlayerInteractionsAndInspect(PlayerInteractEvent event) {
		if (event.isCancelled())
			return;
		// inspect
		if ((event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) && inspecting_players.containsKey(event.getPlayer().getName())) {
			event.setCancelled(true);
			Location position = null;
			if (event.getClickedBlock() != null)
				position = event.getClickedBlock().getLocation();
			else
				position = event.getPlayer().getTargetBlock(null, 1024).getLocation();
			if (position.getBlock().getTypeId() == 0)
				event.getPlayer().sendMessage(ChatColor.RED + "Sorry, but I can't see that far!");
			else
				inspect(event.getPlayer(), position);
		} // (un)lock lockable items
		else if (event.getAction() == Action.LEFT_CLICK_BLOCK && event.getPlayer().getItemInHand().getType() == Material.IRON_INGOT
				&& myPluginWiki.isLockable(event.getClickedBlock(), null)) {
			event.setCancelled(true);
			Block block = event.getClickedBlock();
			// if the block is a wooden door, make sure to check the bottom block of the door
			if (event.getClickedBlock().getType() == Material.WOODEN_DOOR && event.getClickedBlock().getRelative(BlockFace.DOWN).getType() == Material.WOODEN_DOOR)
				block = event.getClickedBlock().getRelative(BlockFace.DOWN);
			// if the thing isn't already locked, try to lock it
			if (!locked_blocks.containsKey(block)) {
				locked_blocks.put(block, event.getPlayer().getName());
				event.getPlayer().sendMessage(ChatColor.YELLOW + "*click* Your " + myPluginWiki.getItemName(block, false, true, true) + " is now locked.");
				// if the thing is already locked, try to unlock it
			} else if (locked_blocks.get(block).equals(event.getPlayer().getName()) || trust_list.get(locked_blocks.get(block)).contains(event.getPlayer().getName())
					|| event.getPlayer().hasPermission("myguarddog.admin")) {
				// if the owner is not the one unlocking the locked block, inform the owner
				if (!locked_blocks.get(block).equals(event.getPlayer().getName())) {
					Player owner = server.getPlayerExact(locked_blocks.get(block));
					String message =
							"Hey, " + event.getPlayer().getName() + " unlocked your " + myPluginWiki.getItemName(block, false, true, true) + " at (" + block.getX() + ", "
									+ block.getY() + ", " + block.getZ() + ") in \"" + block.getWorld().getWorldFolder().getName() + "\".";
					if (owner.isOnline())
						owner.sendMessage(ChatColor.YELLOW + message);
					else {
						ArrayList<String> messages = info_messages.get(owner.getName());
						if (messages == null)
							messages = new ArrayList<String>();
						messages.add("&e" + message);
						info_messages.put(owner.getName(), messages);
					}
					// send a confirmation message
					event.getPlayer().sendMessage(
							ChatColor.YELLOW + "You unlocked " + locked_blocks.get(block) + "'s " + myPluginWiki.getItemName(block, false, true, true) + ".");
				} else
					event.getPlayer().sendMessage(ChatColor.YELLOW + "You unlocked your " + myPluginWiki.getItemName(block, false, true, true) + ".");
				// unlock the block
				locked_blocks.remove(block);
			} // if the thing is already locked and this player can't unlock it, cancel the event
			else {
				event.setCancelled(true);
				event.getPlayer().sendMessage(
						ChatColor.RED + "Sorry, but this " + myPluginWiki.getItemName(event.getClickedBlock(), false, true, true) + " belongs to "
								+ locked_blocks.get(event.getClickedBlock()) + " and you're not allowed to unlock it.");
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
					if (block.getType() == Material.WOODEN_DOOR && block.getData() >= 8)
						block =
								new Location(block.getLocation().getWorld(), block.getLocation().getX(), block.getLocation().getY() - 1, block.getLocation().getZ())
										.getBlock();
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
					&& !(trust_list.get(locked_blocks.get(block)) != null && trust_list.get(locked_blocks.get(block)).contains(event.getPlayer().getName()))
					&& !event.getPlayer().hasPermission("myguarddog.admin")) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(
						ChatColor.RED + "Sorry, but this " + myPluginWiki.getItemName(block, false, true, true) + " belongs to " + locked_blocks.get(block)
								+ " and you're not allowed to use it.");
			} else if (action != null)
				events.add(new Event(event.getPlayer().getName(), action, block, event.getPlayer().getGameMode() == GameMode.CREATIVE));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void logItemFrameAndPaintingBreaking(HangingBreakByEntityEvent event) {
		if (event.isCancelled())
			return;
		String cause;
		Boolean in_Creative_Mode = null;
		if (event.getRemover() instanceof Player) {
			cause = ((Player) event.getRemover()).getName();
			in_Creative_Mode = ((Player) event.getRemover()).getGameMode().equals(GameMode.CREATIVE);
		} else
			cause = myPluginWiki.getEntityName(event.getRemover(), true, true, false);
		events.add(new Event(cause, "took down", event.getEntity(), in_Creative_Mode));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void logItemFrameAndPaintingPlacing(HangingPlaceEvent event) {
		if (event.isCancelled())
			return;
		events.add(new Event(event.getPlayer().getName(), "hung", event.getEntity(), event.getPlayer().getGameMode().equals(GameMode.CREATIVE)));
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void logExplosions(EntityExplodeEvent event) {
		if (event.isCancelled())
			return;
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
		} else if (event.getEntityType() == EntityType.PRIMED_TNT) {
			// try to find out who placed the T.N.T.
			// first, see if another explosion caused the ignition of this one
			if (TNT_causes.get(event.getEntity().getUniqueId()) != null) {
				cause = TNT_causes.get(event.getEntity().getUniqueId());
				TNT_causes.remove(event.getEntity().getUniqueId());
			} else
				// next, look through the events ArrayList for one where someone placed T.N.T. at or above the location of the explosion
				for (int i = events.size() - 1; i >= 0; i--)
					if (events.get(i).action.equals("placed") && events.get(i).objects[0].equals("some T.N.T.") && events.get(i).x - 2 <= event.getLocation().getBlockX()
							&& events.get(i).x + 2 >= event.getLocation().getBlockX() && events.get(i).z - 2 <= event.getLocation().getBlockZ()
							&& events.get(i).z + 2 >= event.getLocation().getBlockZ() && events.get(i).world.equals(event.getLocation().getWorld())) {
						cause = events.get(i).cause;
						break;
					}
			if (cause.equals("some T.N.T.")) {
				// if it wasn't a recent event, look through the logged events
				File log_file =
						new File(position_logs_folder, "x = " + event.getLocation().getBlockX() + " " + event.getLocation().getWorld().getWorldFolder().getName() + ".txt");
				if (log_file.exists()) {
					try {
						BufferedReader in = new BufferedReader(new FileReader(log_file));
						String save_line = in.readLine();
						while (save_line != null) {
							Event placement_event = new Event(save_line);
							if (placement_event.action.equals("placed") && placement_event.objects[0].equals("some T.N.T.")
									&& placement_event.x - 2 <= event.getLocation().getBlockX() && placement_event.x + 2 >= event.getLocation().getBlockX()
									&& placement_event.z - 2 <= event.getLocation().getBlockZ() && placement_event.z + 2 >= event.getLocation().getBlockZ()
									&& placement_event.world.equals(event.getLocation().getWorld())) {
								cause = placement_event.cause;
								break;
							}
							save_line = in.readLine();
						}
					} catch (IOException exception) {
						console.sendMessage(ChatColor.DARK_RED + "I got an IOException while trying to read the " + log_file.getName()
								+ " file to find the cause of the recent T.N.T. explosion!");
						exception.printStackTrace();
						// make sure to still log the event even if there's trouble finding the cause so it can be rolled back
						// therefore, no "return;"
					}
				}
			}
			if (!cause.equals("some T.N.T."))
				action = "blew up";
			else
				console.sendMessage(ChatColor.RED + "I couldn't find the person who caused this T.N.T. explosion!");
		} else if (event.getEntityType() == EntityType.MINECART_TNT) {
			// try to find out who placed the activator rail. When a T.N.T. minecart is activated, it's recorded in TNT_causes.
			if (TNT_causes.get(event.getEntity().getUniqueId()) != null) {
				cause = TNT_causes.get(event.getEntity().getUniqueId());
				TNT_causes.remove(event.getEntity().getUniqueId());
				action = "blew up";
			} else
				console.sendMessage(ChatColor.RED + "I couldn't find the person who caused this T.N.T. minecart explosion!");
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
			if (block.getType() != Material.TNT && block.getType() != Material.FIRE) {
				events.add(new Event(cause, action, block, null));
				checkForReactionBreaks(new Event(cause, action, block, null), block_list);
			} else if (block.getType() == Material.TNT)
				// find the PRIMED_TNT Entity closest to the block
				server.getScheduler().scheduleSyncDelayedTask(mGD, new myGuardDog$1(console, "track T.N.T.", block.getLocation(), cause), 1);
		// through experimentation, I discovered that if an entity explodes and the explosion redirects a PRIMED_TNT Entity, it actually replaces the old
		// PRIMED_TNT Entity with a new one going a different direction. That's important because it changes the UUID of the PRIMED_TNT and I use that to track
		// the explosions with the TNT_causes HashMap. Therefore, here, I need to make it track any PRIMED_TNT Entities inside the blast radius
		// find any PRIMED_TNT Entities within the blast radius (which maxes out at 7 for Minecraft T.N.T. in air)
		for (Entity entity : event.getLocation().getWorld().getEntities())
			// max distance = 7; max distance squared = 49
			if (entity.getType() == EntityType.PRIMED_TNT && entity.getLocation().distanceSquared(event.getLocation()) < 49)
				server.getScheduler().scheduleSyncDelayedTask(mGD, new myGuardDog$1(console, "track T.N.T.", entity.getLocation(), cause), 1);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void logNaturalIgnitions(BlockIgniteEvent event) {
		if (event.isCancelled())
			return;
		// don't log if a player did it (because it will be logged more already and more accurately in logBlockPlace() because apparently it considers someone
		// lighting something on fire the same as someone placing fire), or if it was fire spread (because the spread is unimportant--only the blocks it breaks
		// are important, which are logged in logFireDamage())
		if (event.getPlayer() == null && !event.getCause().equals(IgniteCause.SPREAD)) {
			String cause = null;
			if (event.getCause() == IgniteCause.LAVA) {
				// try to find out if the lava that caused the fire was put there by someone
				for (int x = event.getBlock().getX() - 1; x <= event.getBlock().getX() + 1; x++)
					for (int y = event.getBlock().getY() - 1; y <= event.getBlock().getY() + 1; y++)
						for (int z = event.getBlock().getZ() - 1; z <= event.getBlock().getZ() + 1; z++)
							if (new Location(event.getBlock().getWorld(), x, y, z).getBlock().getType() == Material.STATIONARY_LAVA
									|| new Location(event.getBlock().getWorld(), x, y, z).getBlock().getType() == Material.LAVA) {
								String possible_cause = findCause(true, "some lava", new Location(event.getBlock().getWorld(), x, y, z));
								if (possible_cause != null) {
									cause = possible_cause;
									break;
								}
							}
				if (cause == null)
					return;
			} else if (event.getCause() == IgniteCause.LIGHTNING)
				cause = "some lightning";
			else if (event.getCause() == IgniteCause.FIREBALL)
				cause = "a fireball";
			else
				return;
			events.add(new Event(cause, "set fire to", event.getBlock(), null));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void logFireDamage(BlockBurnEvent event) {
		if (event.isCancelled())
			return;
		String cause = null;
		for (int x = event.getBlock().getX() - 1; x <= event.getBlock().getX() + 1; x++)
			for (int y = event.getBlock().getY() - 1; y <= event.getBlock().getY() + 1; y++)
				for (int z = event.getBlock().getZ() - 1; z <= event.getBlock().getZ() + 1; z++) {
					// try checking for fire-setting events
					String possible_cause =
							findCause(new String[] { "set fire to", "burned", "placed", "spread" }, new String[] { null, null, "some lava", "some lava" }, new Location(event
									.getBlock().getWorld(), x, y, z));
					if (possible_cause != null) {
						cause = possible_cause;
						break;
					}
				}
		if (cause == null)
			return;
		events.add(new Event(cause, "burned", event.getBlock(), null));
		checkForReactionBreaks(new Event(cause, "burned", event.getBlock(), null), null);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void logWaterAndLavaPlacement(PlayerBucketEmptyEvent event) {
		// TODO track lava/water mixing to form cobblestone, stone, or obsidian
		if (event.isCancelled())
			return;
		if (event.getBucket() == Material.WATER_BUCKET)
			events.add(new Event(event.getPlayer().getName(), "placed", "some water", event.getBlockClicked().getRelative(event.getBlockFace()).getLocation(), event
					.getPlayer().getGameMode().equals(GameMode.CREATIVE)));
		else if (event.getBucket() == Material.LAVA_BUCKET)
			events.add(new Event(event.getPlayer().getName(), "placed", "some lava", event.getBlockClicked().getRelative(event.getBlockFace()).getLocation(), event
					.getPlayer().getGameMode().equals(GameMode.CREATIVE)));
		else
			events.add(new Event(event.getPlayer().getName(), "dumped out", "something", event.getBlockClicked().getRelative(event.getBlockFace()).getLocation(), event
					.getPlayer().getGameMode().equals(GameMode.CREATIVE)));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void logWaterAndLavaRemoval(PlayerBucketFillEvent event) {
		if (event.isCancelled())
			return;
		Location event_location = event.getBlockClicked().getRelative(event.getBlockFace()).getLocation();
		String object = myPluginWiki.getItemName(event_location.getBlock(), true, true, false);
		if (object == null) {
			object = String.valueOf(event_location.getBlock().getTypeId());
			if (event_location.getBlock().getData() > 0)
				object += ":" + event_location.getBlock().getData();
			myPluginUtils.tellOps(ChatColor.DARK_RED + "Someone filled a bucket with something with the I.D. " + object + ". I've never heard of anything with the I.D. "
					+ object + ". Can you make sure myPluginWiki is up to date?", true);
		}
		events.add(new Event(event.getPlayer().getName(), "removed", object, event_location, event.getPlayer().getGameMode() == GameMode.CREATIVE));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void logEndermanBlockInteractionsAndSandAndGravelFalling(EntityChangeBlockEvent event) {
		if (event.isCancelled())
			return;
		if (event.getEntityType() == EntityType.ENDERMAN)
			if (event.getBlock().getTypeId() != 0)
				events.add(new Event("an Enderman", "picked up", event.getBlock(), null));
			else
				server.getScheduler().scheduleSyncDelayedTask(this, new myGuardDog$1(console, "track Enderman placements", event.getBlock(), null), 1);
		else if (event.getEntityType() == EntityType.FALLING_BLOCK) {
			String cause = null, action, object;
			// figure out whether this was sand or gravel
			if (event.getTo() == Material.SAND || event.getBlock().getType() == Material.SAND)
				object = "some sand";
			else
				object = "some gravel";
			// figure out whether this event is a landing or a dropping
			if (event.getTo() == Material.SAND || event.getTo() == Material.GRAVEL) {
				action = "relocated";
				// figure out the cause
				for (int y = event.getBlock().getY(); y <= event.getBlock().getWorld().getMaxHeight(); y++) {
					cause = findCause("dropped", object, new Location(event.getBlock().getWorld(), event.getBlock().getX(), y, event.getBlock().getZ()));
					if (cause != null)
						break;
				}
			} else {
				action = "dropped";
				// figure out the cause
				cause =
						findCause(new String[] { "broke", "dropped" }, new String[] { null, null }, new Location(event.getBlock().getWorld(), event.getBlock().getX(), event
								.getBlock().getY() - 1, event.getBlock().getZ()));
				if (cause == null)
					cause = findCause("placed", object, event.getBlock().getLocation());
			}
			if (cause == null)
				return;
			events.add(new Event(cause, action, object, event.getBlock().getLocation(), null));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void logTreeAndGiantMushroomGrowth(StructureGrowEvent event) {
		if (event.isCancelled())
			return;
		if (event.getBlocks().size() <= 1)
			return;
		String cause = null;
		if (event.getPlayer() != null)
			cause = event.getPlayer().getName();
		else
			// if it doesn't log the player, find the player who planted it
			cause =
					findCause(new String[] { "an oak sapling", "a birch sapling", "a spruce sapling", "a jungle sapling" }, new String[] { "placed", "placed", "placed",
							"placed" }, event.getBlocks().get(0).getLocation());
		// make sure that it only logs growth of trees and giant mushrooms by making sure the number of blocks is >1
		if (cause != null) {
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
				console.sendMessage(ChatColor.RED + "There was an unidentified StructureGrowEvent at (" + event.getBlocks().get(1).getBlock().getX() + ", "
						+ event.getBlocks().get(1).getBlock().getY() + ", " + event.getBlocks().get(1).getBlock().getZ() + ").");
				console.sendMessage(ChatColor.WHITE + "event.getBlocks().get(1).getBlock().getTypeId() = " + event.getBlocks().get(1).getTypeId());
				return;
			}
			for (BlockState block : event.getBlocks()) {
				// differentiate between different parts of the tree so events involving it can be rolled back or restored
				if (object.equals("a tree"))
					object = "a tree (" + myPluginWiki.getItemName(block.getTypeId(), block.getData().getData(), true, true, false) + ")";
				events.add(new Event(cause, "grew", object, block.getBlock().getLocation(), event.getPlayer().getGameMode().equals(GameMode.CREATIVE)));
			}
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void logLeafDecay(LeavesDecayEvent event) {
		if (event.isCancelled())
			return;
		double distance = 10000;
		String cause = null;
		for (int x = event.getBlock().getX() - 4; x <= event.getBlock().getX() + 4; x++)
			for (int y = event.getBlock().getY() - 4; y <= event.getBlock().getY() + 4; y++)
				for (int z = event.getBlock().getZ() - 4; z <= event.getBlock().getZ() + 4; z++) {
					String temp =
							findCause(new String[] { "broke", "broke", "broke", "broke", "broke", "broke", "broke", "broke", "decayed", "decayed", "decayed", "decayed" },
									new String[] { "an oak log", "a birch log", "a spruce log", "a jungle log", "some oak leaves", "some birch leaves", "some spruce leaves",
											"some jungle leaves", "some oak leaves", "some birch leaves", "some spruce leaves", "some jungle leaves" }, new Location(event
											.getBlock().getWorld(), x, y, z));
					if (temp != null && new Location(event.getBlock().getWorld(), x, y, z).distanceSquared(event.getBlock().getLocation()) < distance) {
						cause = temp;
						distance = new Location(event.getBlock().getWorld(), x, y, z).distanceSquared(event.getBlock().getLocation());
					}
				}
		if (cause == null)
			return;
		events.add(new Event(cause, "decayed", event.getBlock(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void logMobKillings(EntityDeathEvent event) {
		if (event.getEntity().getKiller() != null) {
			String object = "a " + event.getEntityType().getName(), killer = event.getEntity().getKiller().getName();
			if (event.getEntityType().getName().toLowerCase().startsWith("a") || event.getEntityType().getName().toLowerCase().startsWith("e")
					|| event.getEntityType().getName().toLowerCase().startsWith("i") || event.getEntityType().getName().toLowerCase().startsWith("o")
					|| event.getEntityType().getName().toLowerCase().startsWith("u"))
				object = "an " + event.getEntityType().getName();
			if (!object.equals("an Enderman") && !object.equals("a Blaze") && !object.equals("a Ghast"))
				object = object.toLowerCase();
			if (!(event.getEntity().getKiller() instanceof Player)) {
				if (!killer.equals("Enderman") && !killer.equals("Ghast") && !killer.equals("Blaze"))
					killer = killer.toLowerCase();
				if (killer.toLowerCase().startsWith("a") || killer.toLowerCase().startsWith("e") || killer.toLowerCase().startsWith("i")
						|| killer.toLowerCase().startsWith("o") || killer.toLowerCase().startsWith("u"))
					killer = "an " + killer;
				else
					killer = "a " + killer;
			}
			Boolean in_Creative_Mode = null;
			if (event.getEntity().getKiller() instanceof Player)
				in_Creative_Mode = event.getEntity().getKiller().getGameMode().equals(GameMode.CREATIVE);
			events.add(new Event(event.getEntity().getKiller().getName(), "killed", object, event.getEntity().getLocation(), in_Creative_Mode));
		}
	}

	// loading
	public void loadTheLockedBlocks(CommandSender sender) {
		locked_blocks = new HashMap<Block, String>();
		File locked_blocks_file = new File(getDataFolder(), "locked blocks.txt");
		// read the locked blocks.txt file
		try {
			if (!locked_blocks_file.exists()) {
				getDataFolder().mkdir();
				console.sendMessage(ChatColor.YELLOW + "I couldn't find a locked blocks.txt file. I'll make a new one.");
				locked_blocks_file.createNewFile();
				return;
			}
			BufferedReader in = new BufferedReader(new FileReader(locked_blocks_file));
			String save_line = in.readLine();
			while (save_line != null) {
				if (!save_line.equals("")) {
					// save line format:
					// [player] locked [block type] at ([x], [y], [z]) in "[world]".
					String[] coordinates = save_line.substring(save_line.indexOf("(") + 1, save_line.indexOf(")")).split(", ");
					try {
						locked_blocks.put(new Location(server.getWorld(save_line.substring(save_line.indexOf("\"") + 1, save_line.length() - 2)), Integer
								.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]), Integer.parseInt(coordinates[2])).getBlock(), save_line.split(" locked ")[0]);
					} catch (NumberFormatException exception) {
						console.sendMessage(ChatColor.DARK_RED + "I got an error trying to read this save line for a locked block!");
						console.sendMessage(ChatColor.WHITE + "\"" + save_line + "\"");
						console.sendMessage(ChatColor.DARK_RED + "I read these as the block's coordinates: " + ChatColor.WHITE + "\"" + coordinates[0] + "\", \""
								+ coordinates[1] + "\", \"" + coordinates[2] + "\"");
					}
				}
				save_line = in.readLine();
			}
			in.close();
			saveTheLockedBlocks(sender, false);
		} catch (IOException exception) {
			console.sendMessage(ChatColor.DARK_RED + "I got an IOException while trying to save your locked blocks.");
			exception.printStackTrace();
			return;
		}
		// send the sender a confirmation message
		if (locked_blocks.size() > 1)
			sender.sendMessage(ChatColor.YELLOW + "Your " + locked_blocks.size() + " locked blocks have been loaded.");
		else if (locked_blocks.size() == 1)
			sender.sendMessage(ChatColor.YELLOW + "Your 1 locked block has been loaded.");
		else
			sender.sendMessage(ChatColor.YELLOW + "You have no locked blocks to load!");
		if (sender instanceof Player)
			if (locked_blocks.size() > 1)
				console.sendMessage(ChatColor.YELLOW + ((Player) sender).getName() + " loaded " + locked_blocks.size() + " locked blocks from file.");
			else if (locked_blocks.size() == 1)
				console.sendMessage(ChatColor.YELLOW + ((Player) sender).getName() + " loaded the server's 1 locked block from file.");
			else
				console.sendMessage(ChatColor.YELLOW + ((Player) sender).getName() + " loaded the server's locked blocks from file, but there were no locked blocks on file.");
	}

	// saving
	public void saveTheLockedBlocks(CommandSender sender, boolean display_message) {
		// check the warps file
		File locked_blocks_file = new File(getDataFolder(), "locked blocks.txt");
		if (!locked_blocks_file.exists()) {
			getDataFolder().mkdir();
			try {
				sender.sendMessage(ChatColor.YELLOW + "I couldn't find a locked blocks.txt file. I'll make a new one.");
				locked_blocks_file.createNewFile();
			} catch (IOException exception) {
				sender.sendMessage(ChatColor.DARK_RED + "I couldn't create a locked blocks.txt file! Oh nos!");
				exception.printStackTrace();
				return;
			}
		}
		// save the locked blocks
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(locked_blocks_file));
			for (int i = 0; i < locked_blocks.size(); i++) {
				Block block = (Block) locked_blocks.keySet().toArray()[i];
				// save line format:
				// [player] locked [block type] at ([x], [y], [z]) in "[world]".
				out.write(locked_blocks.get(block) + " locked " + myPluginWiki.getItemName(block, false, true, false) + " at (" + block.getX() + ", " + block.getY() + ", "
						+ block.getZ() + ") in \"" + block.getWorld().getWorldFolder().getName() + "\".");
				if (i < locked_blocks.size() - 1)
					out.newLine();
			}
			out.close();
		} catch (IOException exception) {
			sender.sendMessage(ChatColor.DARK_RED + "I got an IOException while trying to save your locked blocks.");
			exception.printStackTrace();
			return;
		}
		if (display_message) {
			if (locked_blocks.size() > 1)
				sender.sendMessage(ChatColor.YELLOW + "Your " + locked_blocks.size() + " locked blocks have been saved.");
			else if (locked_blocks.size() == 1)
				sender.sendMessage(ChatColor.YELLOW + "Your 1 locked block has been saved.");
			else
				sender.sendMessage(ChatColor.YELLOW + "You have no locked blocks to save!");
			if (sender instanceof Player)
				if (locked_blocks.size() > 1)
					console.sendMessage(ChatColor.YELLOW + ((Player) sender).getName() + " saved " + locked_blocks.size() + " locked blocks to file.");
				else if (locked_blocks.size() == 1)
					console.sendMessage(ChatColor.YELLOW + ((Player) sender).getName() + " saved the server's 1 locked block to file.");
				else
					console.sendMessage(ChatColor.YELLOW + ((Player) sender).getName()
							+ " tried to save the server's locked blocks to file, but there were no locked blocks on the server to save.");
		}
	}

	// plugin commands
	private void inspect(Player player, Location position) {
		// this checks the number of times this player has clicked the same block and if the block they're clicking now is the same one. This allows
		// players to click blocks multiple times to see more than just the past 2 events
		int times_clicked = 0;
		if (inspecting_players.get(player.getName()) != null && inspecting_players.get(player.getName())[0].equals(position)) {
			times_clicked = (Integer) inspecting_players.get(player.getName())[1];
			// if this position was the last position that the inspector had inspected, but times_clicked = 0, that must mean that they reached the end of the
			// list of events that have happened at that position the last time they inspected
			if (times_clicked == 0)
				player.sendMessage(ChatColor.YELLOW + "We're back at the beginning!");
		}
		// so, this is where it gets a little tricky...
		// I made display_events to keep track of the events whose save lines will be displayed to the inspector
		// however, other_relevant_events is also necessary as a counter to keep track of any events we bypass for the times_clicked system
		// see, myGuardDog displays five events at a time, but often at the end, it will say "Click again to see more!" to say that there are more than five
		// events that happened here and that you can click that same position again to see the next five events (in reverse chronological order)
		// therefore, we need to not just find the first five events at that location and send their save lines, but we need to skip over (but also track) the
		// events that we have already shown the inspector when they clicked previously
		// we can determine how many events that occurred at that location need to be skipped with 4*times_clicked
		int other_relevant_events = 0;
		ArrayList<Event> display_events = new ArrayList<Event>();
		for (int i = events.size() - 1; i >= 0; i--)
			if (events.get(i).x == position.getBlockX() && events.get(i).y == position.getBlockY() && events.get(i).z == position.getBlockZ()) {
				if (other_relevant_events < 2 * times_clicked)
					other_relevant_events++;
				// if we already have five events that need displaying and we find another after it, we can end the search here and tell the inspector that they
				// can "Click again to see more!"
				else if (display_events.size() == 2) {
					player.sendMessage(ChatColor.YELLOW + "I see that...");
					for (Event display : display_events)
						player.sendMessage(ChatColor.WHITE + display.save_line);
					player.sendMessage(ChatColor.YELLOW + "Click again to see more!");
					inspecting_players.put(player.getName(), new Object[] { position, times_clicked + 1 });
					return;
				} else
					display_events.add(events.get(i));
			}
		File log_file = new File(position_logs_folder, "x = " + position.getBlockX() + " " + position.getWorld().getWorldFolder().getName() + ".txt");
		if (!log_file.exists()) {
			if (display_events.size() > 0) {
				player.sendMessage(ChatColor.YELLOW + "I see that...");
				for (Event display : display_events)
					player.sendMessage(ChatColor.WHITE + display.save_line);
				player.sendMessage(ChatColor.YELLOW + "Click again to see more!");
				inspecting_players.put(player.getName(), new Object[] { position, times_clicked + 1 });
			} else
				player.sendMessage(ChatColor.YELLOW + "Nothing has happened here at (" + position.getBlockX() + ", " + position.getBlockY() + ", " + position.getBlockZ()
						+ ") yet!");
			return;
		}
		try {
			BufferedReader in = new BufferedReader(new FileReader(log_file));
			String save_line = in.readLine();
			while (save_line != null) {
				Event event = new Event(save_line);
				// since we're reading the position log, there's no need to check if the x is right
				if (event.y == position.getBlockY() && event.z == position.getBlockZ()) {
					if (other_relevant_events < 2 * times_clicked)
						other_relevant_events++;
					// if we already have five events that need displaying and we find another after it, we can end the search here and tell the inspector that
					// they can "Click again to see more!"
					else if (display_events.size() == 2) {
						player.sendMessage(ChatColor.YELLOW + "I see that...");
						for (Event display : display_events)
							player.sendMessage(ChatColor.WHITE + display.save_line);
						player.sendMessage(ChatColor.YELLOW + "Click again to see more!");
						inspecting_players.put(player.getName(), new Object[] { position, times_clicked + 1 });
						return;
					} else
						display_events.add(event);
				}
				save_line = in.readLine();
			}
		} catch (IOException exception) {
			player.sendMessage(ChatColor.DARK_RED + "I got an IOException while trying to save your log files.");
			exception.printStackTrace();
			return;
		}
		// if we reach this point, it means that there weren't enough events at that position to get five to show the inspector AND one extra to confirm that
		// there are others left, which means that we have reached the end of the list of events that have occurred at this position
		if (display_events.size() > 0) {
			player.sendMessage(ChatColor.YELLOW + "I see that...");
			for (Event display : display_events)
				player.sendMessage(ChatColor.WHITE + display.save_line);
		} else
			player.sendMessage(ChatColor.YELLOW + "Nothing has happened here at (" + position.getBlockX() + ", " + position.getBlockY() + ", " + position.getBlockZ()
					+ ") yet!");
		inspecting_players.put(player.getName(), new Object[] { position, 0 });
	}
}