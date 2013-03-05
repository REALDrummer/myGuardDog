package REALDrummer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.block.Block;
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
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class myGuardDog extends JavaPlugin implements Listener {
	public static Plugin mGD;
	public static Server server;
	public static ConsoleCommandSender console;
	public static ArrayList<Event> events = new ArrayList<Event>();
	public static String[] parameters, enable_messages = { "Server secured.", "BEWARE; MYGUARDDOG.", "Target: Griefers...TARGET LOCKED",
			"Anti-Griefing shields at full power, Captain. Awaiting orders...", "Hasta la vista, griefers." }, disable_messages = { "Until we meet again, pathetic griefers.",
			"Griefers have been successfully pwnd.", "Off duty? There is no such thing.", "Though I am disabled, I do not sleep. Your server is under my protection." },
			yeses = { "yes", "yeah", "yep", "ja", "sure", "why not", "okay", "do it", "fine", "whatever", "very well", "accept", "tpa", "cool", "hell yeah", "hells yeah",
					"hells yes", "come" }, nos = { "no", "nah", "nope", "no thanks", "no don't", "shut up", "ignore", "it's not", "its not", "creeper", "unsafe", "wait",
					"one ", "1 " }, wool_dye_colors = { "black", "red", "green", "brown", "blue", "purple", "cyan", "light gray", "gray", "pink", "lime", "yellow",
					"light blue", "magenta", "orange", "white" };
	public static File logs_folder, chrono_logs_folder, position_logs_folder, cause_logs_folder;
	public static boolean roll_back_in_progress = false, save_in_progress = false, hard_save = false;
	// player_to_inform_of_[...]: keys=player names and values=admin name or "console" who performed the command
	private static HashMap<String, GameMode> offline_player_gamemodes = new HashMap<String, GameMode>(), gamemodes_to_change = new HashMap<String, GameMode>();
	private static HashMap<String, String> players_to_inform_of_halting = new HashMap<String, String>(), players_to_inform_of_muting = new HashMap<String, String>();
	// players_questioned_about_rollback = new HashMap<player's name or "the console", parameters of the rollback>
	private static HashMap<String, String[]> players_questioned_about_rollback = new HashMap<String, String[]>();
	// inspecting_players=new HashMap<a player using the inspector, Object[] {Location of the last block clicked, int of the number of times the block}>
	private static HashMap<String, Object[]> inspecting_players = new HashMap<String, Object[]>();
	public static HashMap<UUID, String> primed_TNT_causes = new HashMap<UUID, String>();
	private static ArrayList<String> confirmed_gamemode_changers = new ArrayList<String>(), halted_players = new ArrayList<String>(), muted_players = new ArrayList<String>();

	// TODO: make an int[] of all the item I.D.s of items that break if the block attached to them breaks
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
		// done enabling
		String enable_message = enable_messages[(int) (Math.random() * enable_messages.length)];
		console.sendMessage(ChatColor.YELLOW + enable_message);
		for (Player player : server.getOnlinePlayers())
			if (player.isOp())
				player.sendMessage(ChatColor.YELLOW + enable_message);
	}

	public void onDisable() {
		// save the server data
		hard_save = true;
		new TimedMethod(console, "save the logs", true, null).run();
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
			if (!(sender instanceof Player) || sender.hasPermission("myguarddog.halt") || sender.hasPermission("myguarddog.admin"))
				if (parameters.length > 0) {
					for (Player player : server.getOnlinePlayers())
						if (player.getName().toLowerCase().startsWith(parameters[0].toLowerCase())
								&& ((!player.isOp() && !player.hasPermission("myguarddog.admin")) || (!(sender instanceof Player) || sender.hasPermission("myguarddog.admin")))) {
							halted_players.add(player.getName());
							sender.sendMessage(ChatColor.YELLOW + player.getName() + " has been halted.");
							if (sender instanceof Player)
								player.sendMessage(ChatColor.YELLOW + sender.getName() + " halted you. Don't move and don't try to commands.");
							else
								player.sendMessage(ChatColor.YELLOW + "Someone on the console halted you. Don't move and don't try to commands.");
							break;
						} else if (player.getName().toLowerCase().startsWith(parameters[0].toLowerCase()))
							sender.sendMessage(ChatColor.RED + "Hey! You can't halt another op!");
					for (OfflinePlayer player : server.getOfflinePlayers())
						if (player.getName().toLowerCase().startsWith(parameters[0].toLowerCase())
								&& (!player.isOp() || (!(sender instanceof Player) || sender.hasPermission("myguarddog.admin")))) {
							halted_players.add(player.getName());
							sender.sendMessage(ChatColor.YELLOW + player.getName() + " has been halted.");
							if (sender instanceof Player)
								players_to_inform_of_halting.put(player.getName(), ChatColor.YELLOW + sender.getName() + " halted you. Don't try to move or use commands.");
							else
								players_to_inform_of_halting.put(player.getName(), ChatColor.YELLOW + "Someone on the console halted you. Don't try to move or use commands.");
							break;
						} else if (player.getName().toLowerCase().startsWith(parameters[0].toLowerCase()))
							sender.sendMessage(ChatColor.RED + "Hey! You can't halt another op!");
					sender.sendMessage(ChatColor.RED + "Sorry, but I don't know who \"" + parameters[0] + "\" is.");
				} else
					sender.sendMessage(ChatColor.RED + "You forgot to tell me who to halt!");
			else
				sender.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to halt people.");
		} else if (command.equalsIgnoreCase("gamemode") || command.equalsIgnoreCase("gm")) {
			if (!(sender instanceof Player) || sender.isOp() || sender.hasPermission("myguarddog.admin"))
				changeGameMode(sender);
			else
				sender.sendMessage(ChatColor.RED + "Sorry, but you're not allowed to change gamemodes.");
			return true;
		} else if ((command.equalsIgnoreCase("mGD") || command.equalsIgnoreCase("myGuardDog")) && parameters.length > 1 && parameters[0].equalsIgnoreCase("save")
				&& (parameters[1].equalsIgnoreCase("logs") || (parameters.length > 2 && parameters[1].equalsIgnoreCase("the") && parameters[2].equalsIgnoreCase("logs")))) {
			if (!(sender instanceof Player) || sender.hasPermission("myguarddog.admin"))
				new TimedMethod(sender, "save the logs", true, null).run();
			else if (command.equalsIgnoreCase("myGuardDog"))
				sender.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to use " + ChatColor.GREEN + "/myGuardDog save" + ChatColor.RED + ".");
			else
				sender.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to use " + ChatColor.GREEN + "/mGD save" + ChatColor.RED + ".");
			return true;
		} else if ((command.equalsIgnoreCase("mGD") || command.equalsIgnoreCase("myGuardDog")) && parameters.length == 1 && parameters[0].equalsIgnoreCase("save")) {
			if (!(sender instanceof Player) || sender.hasPermission("myguarddog.admin")) {
				new TimedMethod(sender, "save the logs", true, null).run();
			} else if (command.equalsIgnoreCase("myGuardDog"))
				sender.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to use " + ChatColor.GREEN + "/myGuardDog save" + ChatColor.RED + ".");
			else
				sender.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to use " + ChatColor.GREEN + "/mGD save" + ChatColor.RED + ".");
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
				if (roll_back_in_progress) {
					sender.sendMessage(ChatColor.RED
							+ "Sorry, but there's already a roll back in progress. I'm afraid you'll have to wait until that one is done before you can start another roll back.");
					return true;
				}
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
				new TimedMethod(sender, "roll back", false, parameters).run();
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
	public static String getFullName(String name) {
		String full_name = null;
		for (Player possible_owner : server.getOnlinePlayers())
			// if this player's name also matches and it shorter, return it instead becuase if someone is using an autocompleted command, we need to make sure
			// to get the shortest name because if they meant to use the longer username, they can remedy this by adding more letters to the parameter; however,
			// if they meant to do a shorter username and the auto-complete finds the longer one first, they're screwed
			if (possible_owner.getName().toLowerCase().startsWith(name.toLowerCase()) && (full_name == null || full_name.length() > possible_owner.getName().length()))
				full_name = possible_owner.getName();
		for (OfflinePlayer possible_owner : server.getOfflinePlayers())
			if (possible_owner.getName().toLowerCase().startsWith(name.toLowerCase()) && (full_name == null || full_name.length() > possible_owner.getName().length()))
				full_name = possible_owner.getName();
		return full_name;
	}

	public static Boolean getResponse(CommandSender sender, String unformatted_response, String current_status_line, String current_status_is_true_message) {
		boolean said_yes = false, said_no = false;
		String formatted_response = unformatted_response;
		// elimiate unnecessary spaces and punctuation
		while (formatted_response.startsWith(" "))
			formatted_response = formatted_response.substring(1);
		while (formatted_response.endsWith(" "))
			formatted_response = formatted_response.substring(0, formatted_response.length() - 1);
		formatted_response = formatted_response.toLowerCase();
		// check their response
		for (String yes : yeses)
			if (formatted_response.startsWith(yes))
				said_yes = true;
		if (said_yes)
			return true;
		else {
			for (String no : nos)
				if (formatted_response.startsWith(no))
					said_no = true;
			if (said_no)
				return false;
			else if (current_status_line != null) {
				if (!formatted_response.equals("")) {
					if (unformatted_response.substring(0, 1).equals(" "))
						unformatted_response = unformatted_response.substring(1);
					sender.sendMessage(ChatColor.RED + "I don't know what \"" + unformatted_response + "\" means.");
				}
				while (current_status_line.startsWith(" "))
					current_status_line = current_status_line.substring(1);
				if (current_status_line.startsWith(current_status_is_true_message))
					return true;
				else
					return false;
			} else
				return null;
		}
	}

	public static String replaceAll(String to_return, String to_change, String to_change_to) {
		int index = 0;
		while (to_return.contains(to_change) && to_return.length() >= index + to_change.length()) {
			if (to_return.substring(index, index + to_change.length()).equals(to_change))
				to_return = to_return.substring(0, index) + to_change_to + to_return.substring(index + to_change.length());
			index++;
		}
		return to_return;
	}

	public static final String singularizeItemName(String plural) {
		String data_suffix = "";
		if (plural.endsWith("\\)")) {
			data_suffix = plural.substring(plural.indexOf("(") - 1);
			plural = plural.substring(0, plural.indexOf("(") - 1);
		}
		// for "cacti"
		if (plural.equals("cacti"))
			return "a cactus" + data_suffix;
		else if (plural.equals("leaves"))
			return "some leaves" + data_suffix;
		// for "es" pluralizations
		else if (plural.equals("dead bushes") || plural.equals("jukeboxes") || plural.equals("compasses") || plural.endsWith("potatoes") || plural.endsWith("torches"))
			if (!plural.startsWith("a") && !plural.startsWith("e") && !plural.startsWith("i") && !plural.startsWith("o") && !plural.startsWith("u"))
				return "a " + plural.substring(0, plural.length() - 2) + data_suffix;
			else
				return "an " + plural.substring(0, plural.length() - 2) + data_suffix;
		// for things that need to stay plural because they come in sets
		else if (plural.equals("iron bars") || plural.equals("Nether warts") || plural.equals("shears") || plural.endsWith("pants") || plural.endsWith("boots")
				|| plural.endsWith("seeds") || plural.endsWith("stairs") || plural.endsWith("bricks") || plural.endsWith("leggings"))
			return "some " + plural + data_suffix;
		// for things where the pluralization is not just at the end, e.g. "EYES of Ender"-->"AN EYE of Ender" or "CARROTS on STICKS"-->"A CARROT on A STICK"
		else if (plural.split(" ").length >= 3 && plural.split(" ")[0].endsWith("s") && !plural.split(" ")[0].equals("lapis")) {
			String complex_singular = plural.substring(0, plural.indexOf("s "));
			if (!complex_singular.toLowerCase().startsWith("a") && !complex_singular.toLowerCase().startsWith("e") && !complex_singular.toLowerCase().startsWith("i")
					&& !complex_singular.toLowerCase().startsWith("o") && !complex_singular.toLowerCase().startsWith("u"))
				complex_singular = "a " + complex_singular;
			else
				complex_singular = "an " + complex_singular;
			complex_singular = complex_singular + plural.substring(plural.indexOf(" "), plural.lastIndexOf(" ") + 1);
			String temp = plural.split(" ")[plural.split(" ").length - 1];
			if (temp.endsWith("s"))
				temp = "a " + temp.substring(0, temp.length() - 1);
			return complex_singular + temp + data_suffix;
		} else if (plural.endsWith("s") && !plural.endsWith("ss"))
			if (!plural.toLowerCase().startsWith("a") && !plural.toLowerCase().startsWith("e") && !plural.toLowerCase().startsWith("i")
					&& !plural.toLowerCase().startsWith("o") && !plural.toLowerCase().startsWith("u") && !plural.startsWith("\"11"))
				return "a " + plural.substring(0, plural.length() - 1) + data_suffix;
			else
				return "an " + plural.substring(0, plural.length() - 1) + data_suffix;
		else
			return "some " + plural + data_suffix;
	}

	public static final String pluralizeItemName(String singular) {
		String data_suffix = "";
		if (singular.endsWith("\\)")) {
			data_suffix = singular.substring(singular.indexOf("(") - 1);
			singular = singular.substring(0, singular.indexOf("(") - 1);
		}
		// remove the article at the beginning
		if (singular.startsWith("a "))
			singular = singular.substring(2);
		else if (singular.startsWith("an "))
			singular = singular.substring(3);
		else if (singular.startsWith("some "))
			return singular.substring(5);
		// for "cacti"
		if (singular.equals("cactus"))
			return "cacti" + data_suffix;
		// for "es" pluralizations
		else if (singular.equals("dead bush") || singular.equals("jukebox") || singular.equals("compass") || singular.endsWith("potato") || singular.endsWith("torch"))
			return singular + "es" + data_suffix;
		// for things that needed to stay singular because they came in sets
		else if (singular.endsWith("s"))
			return singular + data_suffix;
		// for things where the singularization is not just at the end
		else if (singular.split(" ").length >= 3 && (singular.contains(" of ") || singular.contains(" o' ") || singular.contains(" on ") || singular.contains(" and "))
				|| singular.contains(" by ")) {
			singular = singular.split(" ")[0] + "s" + singular.substring(singular.split(" ")[0].length());
			if (singular.contains(" a ") || singular.contains(" an "))
				singular = singular + "s";
			return singular.replaceAll(" a ", " ").replaceAll(" an ", " ").replaceAll(" the ", " ") + data_suffix;
		} else if (!singular.endsWith("ss"))
			return singular + "s" + data_suffix;
		else
			return singular + data_suffix;
	}

	public static int translateStringtoTimeInms(String written) {
		int time = 0;
		String[] temp = written.split(" ");
		ArrayList<String> words = new ArrayList<String>();
		for (String word : temp)
			if (!word.equalsIgnoreCase("and") && !word.equalsIgnoreCase("&"))
				words.add(word.toLowerCase().replaceAll(",", ""));
		while (words.size() > 0) {
			// for formats like "2 days 3 minutes 5.57 seconds" or "3 d 5 m 12 s"
			try {
				double amount = Double.parseDouble(words.get(0));
				if (words.get(0).contains("d") || words.get(0).contains("h") || words.get(0).contains("m") || words.get(0).contains("s"))
					throw new NumberFormatException();
				int factor = 0;
				if (words.size() > 1) {
					if (words.get(1).startsWith("d"))
						factor = 86400000;
					else if (words.get(1).startsWith("h"))
						factor = 3600000;
					else if (words.get(1).startsWith("m"))
						factor = 60000;
					else if (words.get(1).startsWith("s"))
						factor = 1000;
					if (factor > 0)
						// since a double of, say, 1.0 is actually 0.99999..., (int)ing it will reduce exact numbers by one, so I added 0.1 to it to avoid that.
						time = time + (int) (amount * factor + 0.1);
					words.remove(0);
					words.remove(0);
				} else
					words.remove(0);
			} catch (NumberFormatException exception) {
				// if there's no space between the time and units, e.g. "2h, 5m, 25s" or "4hours, 3min, 2.265secs"
				double amount = 0;
				int factor = 0;
				try {
					if (words.get(0).contains("d") && (!words.get(0).contains("s") || words.get(0).indexOf("s") > words.get(0).indexOf("d"))) {
						amount = Double.parseDouble(words.get(0).split("d")[0]);
						console.sendMessage("amount should=" + words.get(0).split("d")[0]);
						factor = 86400000;
					} else if (words.get(0).contains("h")) {
						amount = Double.parseDouble(words.get(0).split("h")[0]);
						factor = 3600000;
					} else if (words.get(0).contains("m")) {
						amount = Double.parseDouble(words.get(0).split("m")[0]);
						factor = 60000;
					} else if (words.get(0).contains("s")) {
						amount = Double.parseDouble(words.get(0).split("s")[0]);
						factor = 1000;
					}
					if (factor > 0)
						// since a double of, say, 1.0 is actually 0.99999..., (int)ing it will reduce exact numbers by one, so I added 0.1 to it to avoid that.
						time = time + (int) (amount * factor + 0.1);
				} catch (NumberFormatException exception2) {
				}
				words.remove(0);
			}
		}
		return time;
	}

	public static String translateTimeInmsToString(int time, boolean round_seconds) {
		// get the values (e.g. "2 days" or "55.7 seconds")
		ArrayList<String> values = new ArrayList<String>();
		if (time > 86400000) {
			values.add((int) (time / 86400000) + " days");
			time = time % 86400000;
		}
		if (time > 3600000) {
			values.add((int) (time / 3600000) + " hours");
			time = time % 3600000;
		}
		if (time > 60000) {
			values.add((int) (time / 60000) + " minutes");
			time = time % 60000;
		}
		// add a seconds value if there is still time remaining or if there are no other values
		if (time > 0 || values.size() == 0)
			// if you have partial seconds and !round_seconds, it's written as a double so it doesn't truncate the decimals
			if ((time / 1000.0) != (time / 1000) && !round_seconds)
				values.add((time / 1000.0) + " seconds");
			// if seconds are a whole number, just write it as a whole number (integer)
			else
				values.add(Math.round(time / 1000) + " seconds");
		// if there are two or more values, add an "and"
		if (values.size() >= 2)
			values.add(values.size() - 1, "and");
		// assemble the final String
		String written = "";
		for (int i = 0; i < values.size(); i++) {
			// add spaces as needed
			if (i > 0)
				written = written + " ";
			written = written + values.get(i);
			// add commas as needed
			if (values.size() >= 4 && i < values.size() - 1 && !values.get(i).equals("and"))
				written = written + ",";
		}
		if (!written.equals(""))
			return written;
		else
			return null;
	}

	// listeners
	@EventHandler
	public void recordThePlayersGameModeBeforeTheyLogOff(PlayerQuitEvent event) {
		offline_player_gamemodes.put(event.getPlayer().getName(), event.getPlayer().getGameMode());
	}

	@EventHandler
	public void checkForUnconfirmedGamemodeChanges(PlayerGameModeChangeEvent event) {
		if (confirmed_gamemode_changers.contains(event.getPlayer().getName()))
			confirmed_gamemode_changers.remove(event.getPlayer().getName());
		else {
			event.setCancelled(true);
			server.broadcastMessage(ChatColor.RED + "ALERT! ALERT! ALERT! " + event.getPlayer().getName() + " tried to change gamemodes without the use of this plugin! "
					+ event.getPlayer().getName() + " might be a griefer!");
			halted_players.add(event.getPlayer().getName());
			muted_players.add(event.getPlayer().getName());
			server.broadcastMessage(ChatColor.RED + "I've halted " + event.getPlayer().getName() + ". They can't move or use commands or talk. Admins, you can use "
					+ ChatColor.ITALIC + "/unmute" + ChatColor.RED + " to unmute them and " + ChatColor.ITALIC + "/unhalt" + ChatColor.RED + " to unhalt them.");
		}
	}

	@EventHandler
	public void informPlayersTheyHaveBeenMutedAndOrHalted(PlayerJoinEvent event) {
		if (players_to_inform_of_halting.containsKey(event.getPlayer().getName()))
			if (players_to_inform_of_muting.containsKey(event.getPlayer().getName()))
				if (players_to_inform_of_halting.get(event.getPlayer().getName()).equals(players_to_inform_of_muting.get(event.getPlayer().getName())))
					event.getPlayer().sendMessage(
							ChatColor.YELLOW + players_to_inform_of_halting.get(event.getPlayer().getName())
									+ " halted and muted you. Don't move, don't try to use commands, and don't try to talk.");
				else
					event.getPlayer().sendMessage(
							ChatColor.YELLOW + players_to_inform_of_halting.get(event.getPlayer().getName()) + " halted you and "
									+ players_to_inform_of_muting.get(event.getPlayer().getName())
									+ " muted you. Don't move, don't try to use commands, and don't try to talk.");
			else
				event.getPlayer().sendMessage(
						ChatColor.YELLOW + players_to_inform_of_halting.get(event.getPlayer().getName()) + " halted you. Don't move and don't try to use commands.");
		else if (players_to_inform_of_muting.containsKey(event.getPlayer().getName()))
			event.getPlayer().sendMessage(players_to_inform_of_muting.get(event.getPlayer().getName()) + " muted you. You're not allowed to speak for the time being.");
	}

	@EventHandler
	public void stopHaltedPlayersFromMoving(PlayerMoveEvent event) {
		if (halted_players.contains(event.getPlayer().getName()))
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
			Boolean accepted = getResponse(event.getPlayer(), event.getMessage(), null, null);
			if (accepted != null && accepted) {
				event.setCancelled(true);
				if (events.size() > 200)
					event.getPlayer().sendMessage(ChatColor.YELLOW + "One moment please. I need to save the logs before we start.");
				new TimedMethod(event.getPlayer(), "roll back", false, parameters).run();
			} else if (accepted != null) {
				event.setCancelled(true);
				players_questioned_about_rollback.remove(event.getPlayer().getName());
				event.getPlayer().sendMessage(ChatColor.YELLOW + "Got it. The rollback has been cancelled.");
			}
		}
	}

	@EventHandler
	public void saveTheLogsWhenTheWorldSaves(WorldSaveEvent event) {
		new TimedMethod(console, "save the logs", true, null).run();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void logBlockBreakAndInspect(BlockBreakEvent event) {
		// TODO: make sure that if someone breaks the block that this block is attached to, it logs them as the cause of this event, too.
		if (inspecting_players.containsKey(event.getPlayer().getName())) {
			event.setCancelled(true);
			inspect(event.getPlayer(), event.getBlock().getLocation());
			return;
		}
		events.add(new Event(event.getPlayer().getName(), "broke", event.getBlock(), event.getPlayer().getGameMode().equals(GameMode.CREATIVE)));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void logBlockPlaceAndInspect(BlockPlaceEvent event) {
		if (inspecting_players.containsKey(event.getPlayer().getName())) {
			event.setCancelled(true);
			inspect(event.getPlayer(), event.getBlock().getLocation());
			return;
		}
		if (event.getPlayer() != null)
			// log fire placement as ignition and don't log air placement because that makes no sense!
			if (event.getBlock().getTypeId() != 0 && event.getBlock().getTypeId() != 51)
				events.add(new Event(event.getPlayer().getName(), "placed", event.getBlock(), event.getPlayer().getGameMode().equals(GameMode.CREATIVE)));
			else if (event.getBlock().getTypeId() != 0 && event.getBlock().getTypeId() != 46)
				events.add(new Event(event.getPlayer().getName(), "set fire to", event.getBlock(), event.getPlayer().getGameMode().equals(GameMode.CREATIVE)));
			else if (event.getBlock().getTypeId() == 46)
				// oh, shit, T.N.T.!
				events.add(new Event(event.getPlayer().getName(), "lit", event.getBlock(), event.getPlayer().getGameMode().equals(GameMode.CREATIVE)));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void logWaterAndLavaSpreadAndBlocksBrokenByIt(BlockSpreadEvent event) {
		// TODO
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void logExplosions(EntityExplodeEvent event) {
		// identify the cause of the event
		String cause = "something", action = "blew up";
		if (event.getEntityType() == EntityType.CREEPER) {
			cause = "a creeper";
			double distance = 0;
			for (Entity entity : event.getEntity().getNearbyEntities(6, 6, 6))
				if (entity.getType() == EntityType.PLAYER
						&& (cause.equals("a creeper") || distance > Math.sqrt(Math.pow(event.getEntity().getLocation().getX() - entity.getLocation().getX(), 2)
								+ Math.pow(event.getEntity().getLocation().getY() - entity.getLocation().getY(), 2)
								+ Math.pow(event.getEntity().getLocation().getZ() - entity.getLocation().getZ(), 2)))) {
					cause = ((Player) entity).getName();
					action = "creeper'd";
					// distance = SQRT((SQRT((dx^2) + (dy^2)))^2 + dz^2) = SQRT(dx^2 + dy^2 + dz^2)
					distance =
							Math.sqrt(Math.pow(event.getEntity().getLocation().getX() - entity.getLocation().getX(), 2)
									+ Math.pow(event.getEntity().getLocation().getY() - entity.getLocation().getY(), 2)
									+ Math.pow(event.getEntity().getLocation().getZ() - entity.getLocation().getZ(), 2));
				}
		} else if (event.getEntityType() == EntityType.PRIMED_TNT) {
			cause = "T.N.T.";
			// try to find out who placed the T.N.T.
			// first, see if another explosion caused the ignition of this and if so, it would have been logged
			if (primed_TNT_causes.get(event.getEntity().getUniqueId()) != null) {
				cause = primed_TNT_causes.get(event.getEntity().getUniqueId());
				primed_TNT_causes.remove(event.getEntity().getUniqueId());
			}
			if (cause.equals("T.N.T."))
				// next, look through the events ArrayList for one where someone placed T.N.T. at or above the location of the explosion
				for (int i = events.size() - 1; i >= 0; i--)
					if (events.get(i).action.equals("placed") && events.get(i).objects[0].equals("T.N.T.") && events.get(i).x - 2 <= event.getLocation().getBlockX()
							&& events.get(i).x + 2 >= event.getLocation().getBlockX() && events.get(i).z - 2 <= event.getLocation().getBlockZ()
							&& events.get(i).z + 2 >= event.getLocation().getBlockZ()) {
						cause = events.get(i).cause;
						break;
					}
			if (cause.equals("T.N.T.")) {
				// if it wasn't a recent event, look through the logged events
				File log_file =
						new File(position_logs_folder, "(" + event.getLocation().getBlockX() + ", " + event.getLocation().getBlockZ() + ") "
								+ event.getLocation().getWorld().getWorldFolder().getName() + ".txt");
				if (log_file.exists()) {
					try {
						BufferedReader in = new BufferedReader(new FileReader(log_file));
						String save_line = in.readLine();
						while (save_line != null) {
							Event placement_event = new Event(save_line);
							if (placement_event.action.equals("placed") && placement_event.objects[0].equals("T.N.T.")
									&& placement_event.x - 2 <= event.getLocation().getBlockX() && placement_event.x + 2 >= event.getLocation().getBlockX()
									&& placement_event.z - 2 <= event.getLocation().getBlockZ() && placement_event.z + 2 >= event.getLocation().getBlockZ()) {
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
			if (!cause.equals("T.N.T."))
				action = "T.N.T.'d";
			else
				console.sendMessage(ChatColor.RED + "I couldn't find the person who caused this T.N.T. explosion!");
		} else if (event.getEntityType() == EntityType.GHAST)
			cause = "a Ghast";
		else if (event.getEntityType() == EntityType.ENDER_DRAGON) {
			cause = "the Ender Dragon";
			// check to see if the object that exploded was fire and if it is, ignore it because it makes no sense and the Enderdragon glitches and
			// "blows up fire" all the time
			if (event.blockList().get(0).getTypeId() == 51) {
				event.setCancelled(true);
				return;
			}
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
		for (Block block : event.blockList())
			// if "T.N.T. blew up T.N.T.", find the UUID of the primed T.N.T. created by this explosion so it can be tracked and the cause of
			// the first T.N.T. can be used as the cause of this newly primed T.N.T.
			if (block.getTypeId() != 46)
				events.add(new Event(cause, action, block, null));
			else
				// find the PRIMED_TNT Entity closest to the block
				server.getScheduler().scheduleSyncDelayedTask(mGD, new TimedMethod(console, "track T.N.T.", block.getLocation(), cause), 1);
		// through experimentation, I discovered that if an entity explodes and the explosion redirects a PRIMED_TNT Entity, it actually replaces the old
		// PRIMED_TNT Entity with a new one going a different direction. That's important because it changes the UUID of the PRIMED_TNT and I use that to track
		// the explosions with the primed_TNT_causes HashMap. Therefore, here, I need to make it track any PRIMED_TNT Entities inside the blast radius
		// find any PRIMED_TNT Entities within the blast radius (which maxes out at 7 for Minecraft T.N.T. in air)
		for (Entity entity : event.getLocation().getWorld().getEntities())
			// max distance = 7; max distance squared = 49
			if (entity.getType() == EntityType.PRIMED_TNT && entity.getLocation().distanceSquared(event.getLocation()) < 49)
				server.getScheduler().scheduleSyncDelayedTask(mGD, new TimedMethod(console, "track T.N.T.", entity.getLocation(), cause), 1);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void logNaturalIgnitions(BlockIgniteEvent event) {
		// don't log if the event is cancelled (obviously), if a player did it (because it will be logged more already and more accurately in
		// logBlockPlace() because apparently it considers someone lighting something on fire the same as someone placing fire), or if it was fire spread
		// (because the spread is unimportant--only the blocks it breaks are important, which are logged in logFireDamage())
		if (!event.isCancelled() && event.getPlayer() == null && !event.getCause().equals(IgniteCause.SPREAD)) {
			String cause;
			if (event.getCause().equals(IgniteCause.LAVA))
				cause = "some lava";
			else if (event.getCause().equals(IgniteCause.LIGHTNING))
				cause = "some lightning";
			else if (event.getCause().equals(IgniteCause.FIREBALL))
				cause = "a fireball";
			else
				cause = "something";
			events.add(new Event(cause, "set fire to", event.getBlock(), null));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void logFireDamage(BlockBurnEvent event) {
		String cause = null;
		for (int i = events.size() - 1; i > 0; i--)
			if (events.get(i).x <= event.getBlock().getX() + 1
					&& events.get(i).x >= event.getBlock().getX() - 1
					&& events.get(i).y <= event.getBlock().getY() + 1
					&& events.get(i).y >= event.getBlock().getY() - 1
					&& events.get(i).z <= event.getBlock().getZ() + 1
					&& events.get(i).z >= event.getBlock().getZ() - 1
					&& (events.get(i).action.equals("set fire to") || ((events.get(i).action.equals("spread to") || events.get(i).action.equals("placed")) && events.get(i).cause
							.equals("lava")))) {
				cause = events.get(i).cause;
				break;
			}
		if (cause != null)
			events.add(new Event(cause, "burned", event.getBlock(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void logWaterAndLavaPlacement(PlayerBucketEmptyEvent event) {
		// ids: 326=water bucket, 327=lava bucket, 335=milk bucket
		if (event.getBucket().getId() == 326)
			events.add(new Event(event.getPlayer().getName(), "placed", "water", event.getBlockClicked().getRelative(event.getBlockFace()).getLocation(), event.getPlayer()
					.getGameMode().equals(GameMode.CREATIVE)));
		else if (event.getBucket().getId() == 327)
			events.add(new Event(event.getPlayer().getName(), "placed", "lava", event.getBlockClicked().getRelative(event.getBlockFace()).getLocation(), event.getPlayer()
					.getGameMode().equals(GameMode.CREATIVE)));
		else if (event.getBucket().getId() == 335)
			events.add(new Event(event.getPlayer().getName(), "dumped out", "milk", event.getBlockClicked().getRelative(event.getBlockFace()).getLocation(), event.getPlayer()
					.getGameMode().equals(GameMode.CREATIVE)));
		else
			events.add(new Event(event.getPlayer().getName(), "dumped out", "something", event.getBlockClicked().getRelative(event.getBlockFace()).getLocation(), event
					.getPlayer().getGameMode().equals(GameMode.CREATIVE)));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void logPlayerInteractionsAndInspect(PlayerInteractEvent event) {
		// this part is for the inspector
		if ((event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK))
				&& inspecting_players.containsKey(event.getPlayer().getName())) {
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
			return;
		}
		String action = null, object = null;
		Block block = event.getClickedBlock();
		// switches: lever=69, stone pressure plate=70, wooden pressure plate=72, stone button=77, wooden button = 143
		// portals: wooden door=64, iron door=71, trapdoor=96, fence gate=107
		// this logs stepping on pressure plates
		if (event.getAction().equals(Action.PHYSICAL) && (block.getTypeId() == 70 || block.getTypeId() == 72)) {
			action = "stepped on";
			object = "pressure plates";
		} else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
			// these two log lever flipping and button pressing
			if (block.getTypeId() == 69) {
				action = "flipped";
				object = "levers";
			} else if (block.getTypeId() == 77 || event.getClickedBlock().getTypeId() == 143) {
				action = "pressed";
				object = "buttons";
				// this logs door and fence gate opening and closing
			} else if (block.getTypeId() == 64) {
				// if it's the top block of a door, data=8 or 9; if you're closing a door, data=4-7; if you're opening a door, data=0-3
				if (block.getData() >= 8)
					block = new Location(block.getLocation().getWorld(), block.getLocation().getX(), block.getLocation().getY() - 1, block.getLocation().getZ()).getBlock();
				if (block.getData() < 4)
					action = "opened";
				else
					action = "closed";
				object = "wooden doors";
			} else if (block.getTypeId() == 96) {
				// if you're closing a trapdoor, data=4-7 or 12-15; if you're opening a trapdoor, data=0-3 or 8-11
				if (block.getData() < 4 || (block.getData() >= 8 && block.getData() <= 11))
					action = "opened";
				else
					action = "closed";
				object = "trapdoors";
			} else if (block.getTypeId() == 107) {
				// if you're closing a fence gate, data=4-7; if you're opening a fence gate, data=0-3
				if (block.getData() < 4)
					action = "opened";
				else
					action = "closed";
				object = "fence gates";
			}
			// bonemeal=351:15, saplings=6, wheat=59, carrots=141, potatoes=142, brown mushroom=39, red mushroom=40, grass=2, cocoa beans=127, melon stems=105,
			// pumpkin stems=104, Nether warts=115
			// this logs bonemealing of plants
			else if (event.getPlayer().getItemInHand().getTypeId() == 351
					&& event.getPlayer().getItemInHand().getData().getData() == 15
					&& (block.getTypeId() == 6 || block.getTypeId() == 59 || block.getTypeId() == 141 || block.getTypeId() == 142 || block.getTypeId() == 39
							|| block.getTypeId() == 40 || block.getTypeId() == 2 || block.getTypeId() == 127 || block.getTypeId() == 105 || block.getTypeId() == 104 || block
							.getTypeId() == 115))
				events.add(new Event(event.getPlayer().getName(), "bonemealed", block, event.getPlayer().getGameMode().equals(GameMode.CREATIVE)));
		if (action != null && object != null)
			events.add(new Event(event.getPlayer().getName(), action, object, block.getLocation(), event.getPlayer().getGameMode().equals(GameMode.CREATIVE)));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void logEndermanBlockInteractions(EntityChangeBlockEvent event) {
		if (event.getEntityType() == EntityType.ENDERMAN)
			if (event.getBlock().getTypeId() != 0)
				events.add(new Event("an Enderman", "picked up", event.getBlock(), null));
			else
				server.getScheduler().scheduleSyncDelayedTask(this, new TimedMethod(console, "track Enderman placements", event.getBlock(), null), 1);
		else if (event.getEntityType() != EntityType.SHEEP)
			console.sendMessage("A " + event.getEntityType().getName() + " caused an unknown EntityBlockChangeEvent at (" + event.getBlock().getX() + ", "
					+ event.getBlock().getY() + ", " + event.getBlock().getZ() + ")!");
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void logTreeAndGiantMushroomGrowth(StructureGrowEvent event) {
		String cause = null;
		if (event.getPlayer() != null)
			cause = event.getPlayer().getName();
		else
			// if it doesn't log the player, find the player who planted it
			for (int i = events.size(); i >= 0; i--)
				if (events.get(i).action.equals("placed") && events.get(i).objects[0] != null && events.get(i).objects[0].equals("a sapling")
						&& event.getBlocks().get(0).getBlock().getLocation().equals(events.get(i).location))
					cause = events.get(i).cause;
		// make sure that it only logs growth of trees and giant mushrooms by making sure the number of blocks is >1
		if (cause != null && event.getBlocks().size() > 1) {
			String object = null;
			// logs as "a tree" for leaves or logs
			if (event.getBlocks().get(1).getBlock().getTypeId() == 17 || event.getBlocks().get(1).getBlock().getTypeId() == 18)
				object = "a tree";
			else if (event.getBlocks().get(1).getBlock().getTypeId() == 100)
				object = "a giant red mushroom";
			else if (event.getBlocks().get(1).getBlock().getTypeId() == 99)
				object = "a giant brown mushroom";
			if (object != null)
				for (int i = 0; i < event.getBlocks().size(); i++)
					events.add(new Event(cause, "grew", object, event.getBlocks().get(i).getBlock().getLocation(), event.getPlayer().getGameMode().equals(GameMode.CREATIVE)));
			else {
				console.sendMessage(ChatColor.RED + "There was an unidentified StructureGrowEvent at (" + event.getBlocks().get(0).getBlock().getX() + ", "
						+ event.getBlocks().get(0).getBlock().getY() + ", " + event.getBlocks().get(0).getBlock().getZ() + ").");
				console.sendMessage(ChatColor.WHITE + "event.getBlocks().get(0).getBlock().getTypeId() = " + event.getBlocks().get(0).getBlock().getTypeId());
			}
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void logLeafDecay(LeavesDecayEvent event) {
		for (Event mGD_event : events)
			if (mGD_event.location.equals(event.getBlock().getLocation()) && (mGD_event.action.equals("grew") || mGD_event.action.equals("bonemealed"))
					&& mGD_event.objects[0].equals("a tree")) {
				// TODO
			}
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

	@EventHandler(priority = EventPriority.HIGHEST)
	public void logSheepColorDye(PlayerInteractEntityEvent event) {
		// TODO make sure that if you right-click a sheep with dye that's already the same color as the sheep, it doesn't log it
		if (event.getRightClicked().getType() == EntityType.SHEEP && event.getPlayer().getItemInHand().getTypeId() == 351)
			events.add(new Event(event.getPlayer().getName(), "dyed " + wool_dye_colors[event.getPlayer().getItemInHand().getData().getData()], event.getRightClicked(), event
					.getPlayer().getGameMode().equals(GameMode.CREATIVE)));
	}

	// plugin commands
	public static void changeGameMode(CommandSender sender) {
		if (parameters.length == 0)
			if (sender instanceof Player)
				if (((Player) sender).getGameMode() == GameMode.CREATIVE) {
					confirmed_gamemode_changers.add(sender.getName());
					((Player) sender).setGameMode(GameMode.SURVIVAL);
					sender.sendMessage(ChatColor.YELLOW + "You're now in Survival Mode. Watch out for monsters.");
					console.sendMessage(ChatColor.YELLOW + sender.getName() + " changed to Survival Mode.");
				} else {
					confirmed_gamemode_changers.add(sender.getName());
					((Player) sender).setGameMode(GameMode.CREATIVE);
					sender.sendMessage(ChatColor.YELLOW + "You're now in Creative Mode. Go nuts. Have fun.");
					console.sendMessage(ChatColor.YELLOW + sender.getName() + " changed to Creative Mode.");
				}
			else
				sender.sendMessage(ChatColor.RED + "You forgot to tell me whose gamemode you want me to change! I can't exactly change yours, can I?");
		else if (parameters.length == 1)
			if (sender instanceof Player && ("creative".startsWith(parameters[0].toLowerCase()) || parameters[0].equals("1"))) {
				if (!((Player) sender).getGameMode().equals(GameMode.CREATIVE)) {
					confirmed_gamemode_changers.add(sender.getName());
					((Player) sender).setGameMode(GameMode.CREATIVE);
					sender.sendMessage(ChatColor.YELLOW + "You're now in Creative Mode. Go nuts. Have fun.");
					console.sendMessage(ChatColor.YELLOW + sender.getName() + " changed to Creative Mode.");
				} else
					sender.sendMessage(ChatColor.RED + "You're already in Creative Mode!");
			} else if (sender instanceof Player && ("survival".startsWith(parameters[0].toLowerCase()) || parameters[0].equals("0"))) {
				if (!((Player) sender).getGameMode().equals(GameMode.SURVIVAL)) {
					confirmed_gamemode_changers.add(sender.getName());
					((Player) sender).setGameMode(GameMode.SURVIVAL);
					sender.sendMessage(ChatColor.YELLOW + "You're now in Survival Mode. Watch out for monsters.");
					console.sendMessage(ChatColor.YELLOW + sender.getName() + " changed to Survival Mode.");
				} else
					sender.sendMessage(ChatColor.RED + "You're already in Survival Mode!");
			} else {
				for (Player player : server.getOnlinePlayers())
					if (player.getName().toLowerCase().startsWith(parameters[0].toLowerCase())) {
						if (player.isOp() && sender instanceof Player && !sender.hasPermission("myopaids.admin"))
							sender.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to change other ops' gamemodes.");
						else if (player.getGameMode().equals(GameMode.SURVIVAL)) {
							confirmed_gamemode_changers.add(player.getName());
							player.setGameMode(GameMode.CREATIVE);
							if (sender instanceof Player) {
								player.sendMessage(ChatColor.YELLOW + sender.getName() + " put you in Creative Mode.");
								sender.sendMessage(ChatColor.YELLOW + "You put " + player.getName() + " in Creative Mode.");
								console.sendMessage(ChatColor.YELLOW + sender.getName() + " put " + player.getName() + " in Creative Mode.");
							} else {
								player.sendMessage(ChatColor.YELLOW + "Someone put you in Creative Mode from the console.");
								console.sendMessage(ChatColor.YELLOW + "You put " + player.getName() + " in Creative Mode.");
							}
						} else {
							confirmed_gamemode_changers.add(player.getName());
							player.setGameMode(GameMode.SURVIVAL);
							if (sender instanceof Player) {
								player.sendMessage(ChatColor.YELLOW + sender.getName() + " put you in Survival Mode.");
								sender.sendMessage(ChatColor.YELLOW + "You put " + player.getName() + " in Survival Mode.");
								console.sendMessage(ChatColor.YELLOW + sender.getName() + " put " + player.getName() + " in Survival Mode.");
							} else {
								player.sendMessage(ChatColor.YELLOW + "Someone put you in Survival Mode from the console.");
								console.sendMessage(ChatColor.YELLOW + "You put " + player.getName() + " in Survival Mode.");
							}
						}
						return;
					}
				for (OfflinePlayer player : server.getOfflinePlayers())
					if (player.getName().toLowerCase().startsWith(parameters[0].toLowerCase())) {
						if (player.isOp() && sender instanceof Player && !sender.hasPermission("myopaids.admin"))
							sender.sendMessage(ChatColor.RED + "Sorry, but you're not allowed to change other people's gamemodes.");
						else if (offline_player_gamemodes.get(player.getName()) == null)
							sender.sendMessage(ChatColor.RED
									+ "Sorry, but I don't remember what "
									+ player.getName()
									+ "'s gamemode was before they logged off, so I'm not sure what to change their gamemode to. Can you please use two parameters and confirm what gamemode you want me to change them to when they come back on?");
						else if (offline_player_gamemodes.get(player.getName()).equals(GameMode.SURVIVAL)) {
							gamemodes_to_change.put(player.getName(), GameMode.CREATIVE);
							sender.sendMessage(ChatColor.YELLOW + "I'll put " + player.getName() + " in Creative Mode when they get back online.");
						} else {
							gamemodes_to_change.put(player.getName(), GameMode.SURVIVAL);
							sender.sendMessage(ChatColor.YELLOW + "I'll put " + player.getName() + " in Survival Mode when they get back online.");
						}
						return;
					}
				sender.sendMessage(ChatColor.RED + "Sorry, but I don't believe anyone named \"" + parameters[0] + "\" has ever played on this server.");
			}
		else {
			String player;
			GameMode new_gamemode = null;
			if (parameters[0].equals("1") || "creative".startsWith(parameters[0].toLowerCase())) {
				player = parameters[1];
				new_gamemode = GameMode.CREATIVE;
			} else if (parameters[0].equals("0") || "survival".startsWith(parameters[0].toLowerCase())) {
				player = parameters[1];
				new_gamemode = GameMode.SURVIVAL;
			} else if (parameters[1].equals("1") || "creative".startsWith(parameters[1].toLowerCase())) {
				player = parameters[0];
				new_gamemode = GameMode.CREATIVE;
			} else if (parameters[1].equals("0") || "survival".startsWith(parameters[1].toLowerCase())) {
				player = parameters[0];
				new_gamemode = GameMode.SURVIVAL;
			} else
				player = parameters[0];
			for (Player online_player : server.getOnlinePlayers())
				if (online_player.getName().toLowerCase().startsWith(player.toLowerCase())) {
					if (online_player.isOp() && sender instanceof Player && !sender.hasPermission("myopaids.admin"))
						sender.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to change other ops' gamemodes.");
					else {
						if (new_gamemode != null) {
							confirmed_gamemode_changers.add(online_player.getName());
							online_player.setGameMode(new_gamemode);
						} else if (online_player.getGameMode().equals(GameMode.SURVIVAL)) {
							confirmed_gamemode_changers.add(online_player.getName());
							online_player.setGameMode(GameMode.CREATIVE);
							new_gamemode = GameMode.CREATIVE;
						} else {
							confirmed_gamemode_changers.add(online_player.getName());
							online_player.setGameMode(GameMode.SURVIVAL);
							new_gamemode = GameMode.SURVIVAL;
						}
						if (sender instanceof Player) {
							online_player.sendMessage(ChatColor.YELLOW + sender.getName() + " put you in " + new_gamemode.toString().substring(0, 1)
									+ new_gamemode.toString().substring(1).toLowerCase() + " Mode.");
							sender.sendMessage(ChatColor.YELLOW + "You put " + online_player.getName() + " in " + new_gamemode.toString().substring(0, 1)
									+ new_gamemode.toString().substring(1).toLowerCase() + " Mode.");
							console.sendMessage(ChatColor.YELLOW + sender.getName() + " put " + online_player.getName() + " in " + new_gamemode.toString().substring(0, 1)
									+ new_gamemode.toString().substring(1).toLowerCase() + " Mode.");
						} else {
							online_player.sendMessage(ChatColor.YELLOW + "Someone put you in " + new_gamemode.toString().substring(0, 1)
									+ new_gamemode.toString().substring(1).toLowerCase() + " Mode from the console.");
							console.sendMessage(ChatColor.YELLOW + "You put " + online_player.getName() + " in " + new_gamemode.toString().substring(0, 1)
									+ new_gamemode.toString().substring(1).toLowerCase() + " Mode.");
						}
					}
					return;
				}
			for (OfflinePlayer offline_player : server.getOfflinePlayers())
				if (offline_player.getName().toLowerCase().startsWith(player)) {
					if (offline_player.isOp() && sender instanceof Player && !sender.hasPermission("myopaids.admin"))
						sender.sendMessage(ChatColor.RED + "Sorry, but you're not allowed to change other people's gamemodes.");
					else if (new_gamemode != null) {
						gamemodes_to_change.put(offline_player.getName(), new_gamemode);
						sender.sendMessage(ChatColor.YELLOW + "I'll put " + offline_player.getName() + " in " + new_gamemode.toString().substring(0, 1)
								+ new_gamemode.toString().substring(1).toLowerCase() + " Mode when they get back online.");
					} else if (offline_player_gamemodes.get(offline_player.getName()) == null)
						sender.sendMessage(ChatColor.RED
								+ "Sorry, but I don't remember what "
								+ offline_player.getName()
								+ "'s gamemode was before they logged off, so I'm not sure what to change their gamemode to. Can you please use two parameters and confirm what gamemode you want me to change them to when they come back on?");
					else if (offline_player_gamemodes.get(offline_player.getName()).equals(GameMode.SURVIVAL)) {
						gamemodes_to_change.put(offline_player.getName(), GameMode.CREATIVE);
						sender.sendMessage(ChatColor.YELLOW + "I'll put " + offline_player.getName() + " in Creative Mode when they get back online.");
					} else {
						gamemodes_to_change.put(offline_player.getName(), GameMode.SURVIVAL);
						sender.sendMessage(ChatColor.YELLOW + "I'll put " + offline_player.getName() + " in Survival Mode when they get back online.");
					}
					return;
				}
			sender.sendMessage(ChatColor.RED + "Sorry, but I don't believe anyone named \"" + parameters[0] + "\" has ever played on this server.");
		}
	}

	private void inspect(Player player, Location position) {
		// this checks the number of times this player has clicked the same block and if the block they're clicking now is the same one. This allows
		// players to click blocks multiple times to see more than just the past 4 events
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
		for (Event event : events)
			if (event.x == position.getBlockX() && event.y == position.getBlockY() && event.z == position.getBlockZ()) {
				if (other_relevant_events < 4 * times_clicked)
					other_relevant_events++;
				// if we already have five events that need displaying and we find another after it, we can end the search here and tell the inspector that they
				// can "Click again to see more!"
				else if (display_events.size() == 4) {
					player.sendMessage(ChatColor.YELLOW + "I see that...");
					for (Event display : display_events)
						player.sendMessage(ChatColor.WHITE + display.save_line);
					player.sendMessage(ChatColor.YELLOW + "Click again to see more!");
					inspecting_players.put(player.getName(), new Object[] { position, times_clicked + 1 });
					return;
				} else
					display_events.add(event);
			}
		File log_file =
				new File(position_logs_folder, "(" + position.getBlockX() + ", " + position.getBlockZ() + ") " + position.getWorld().getWorldFolder().getName() + ".txt");
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
				// since we're reading the position log, there's no need to check if the x and z are right
				if (event.y == position.getBlockY()) {
					if (other_relevant_events < 4 * times_clicked)
						other_relevant_events++;
					// if we already have five events that need displaying and we find another after it, we can end the search here and tell the inspector that
					// they can "Click again to see more!"
					else if (display_events.size() == 4) {
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