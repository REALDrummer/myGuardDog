package REALDrummer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

public class myGuardDog$1 implements Runnable {
	CommandSender sender = null;
	String method = null;
	Object[] os;
	boolean first_iteration = true;

	// for trackTNT()
	private Entity new_primed_TNT = null;
	// for trackLiquidRecession()
	private long delay;
	// for all the saveTheLogs[...]() methods
	private ArrayList<Event> events_to_save = new ArrayList<Event>();
	private int iterations = 0;
	private boolean display_message = true;
	private String[] parameters = null;
	// for saveTheLogsPartIChrono
	private File log_file = null;
	private int split_index = -1;
	// for rollBackPartI()
	private BufferedReader in = null;
	private int log_counter = 0, radius = -1;
	private ArrayList<String> causes = new ArrayList<String>(), actions = new ArrayList<String>(), objects = new ArrayList<String>();
	private ArrayList<File> relevant_log_files = new ArrayList<File>();
	private Location origin = null;
	// part 1 of the roll back events is just put straight into roll_back_events
	private ArrayList<Event> roll_back_events_part2 = new ArrayList<Event>(), roll_back_events_part3 = new ArrayList<Event>(),
			roll_back_events_part4 = new ArrayList<Event>();
	// for rollBackPart[III-V]()
	private int events_located_so_far = 0;
	private ArrayList<String> events_for_this_file = new ArrayList<String>();
	private boolean this_file_has_roll_back_events = false;
	// for rollBackPart[III-VI]()
	private HashMap<File, ArrayList<String>> updated_events = new HashMap<File, ArrayList<String>>();
	// for rollBackPartVI()
	private BufferedWriter out = null;
	// for all rollBackPart[#]() methods
	private ArrayList<Event> roll_back_events = new ArrayList<Event>();

	public myGuardDog$1(CommandSender my_sender, String my_method, Object... my_objects) {
		sender = my_sender;
		method = my_method;
		os = my_objects;
	}

	// run() doesn't actually perform any real action; it's an operator, by which I mean it takes the input and directs the processor to the appropriate method
	public void run() {
		if (method.equals("track T.N.T."))
			// the os for this are never changed through the course of this method, so we don't need to make initializer variables for them
			trackTNT((Location) os[0], (String) os[1]);
		else if (method.equals("track reaction breaks"))
			trackReactionBreaks((Event) os[0]);
		else if (method.equals("track Enderman placements"))
			trackEndermanPlacements((Block) os[0]);
		else if (method.equals("track liquid recession"))
			trackLiquidRecession((Event) os[0]);
		else if (method.equals("save the logs") || method.equals("hard save")) {
			first_iteration = true;
			display_message = (Boolean) os[0];
			if (method.equals("hard save")) {
				myGuardDog.hard_save = true;
				saveTheLogsPartIChrono();
			} else {
				myGuardDog.save_in_progress = true;
				method = "check for save";
				myGuardDog.server.getScheduler().scheduleSyncDelayedTask(myGuardDog.mGD, this, 3);
			}
		} else if (method.equals("roll back")) {
			first_iteration = true;
			display_message = false;
			parameters = (String[]) os[0];
			myGuardDog.roll_back_in_progress = true;
			method = "check for roll back";
			myGuardDog.server.getScheduler().scheduleSyncDelayedTask(myGuardDog.mGD, this, 3);
		} else if (method.equals("check for save")) {
			// oddly enough, because saving the logs constantly changes save_in_progress to false, if save_in_progress is true, it means a save is NOT in
			// progress (and the same applies to roll_back_in_progress with relation to roll backs)
			if (!myGuardDog.hard_save && !myGuardDog.save_in_progress)
				sender.sendMessage(ChatColor.RED
						+ "This is not insubordination, but I am afraid that a save is already in progress. Therefore, I cannot start a new one. This current save must terminate first.");
			// if it's a hard save and there's already a save in progress, myGuardDog.hard_save will already be set to true, which will make the rest of the
			// save a hard save anyway, so there's no need to do anything here
			else if (!myGuardDog.hard_save) {
				first_iteration = true;
				saveTheLogsPartIChrono();
			}
		} else if (method.equals("check for roll back")) {
			// oddly enough, because saving the logs constantly changes roll_back_in_progress to false, if roll_back_in_progress is true, it means a roll back
			// is NOT in progress (and the same applies to save_in_progress with relation to saving)
			if (!myGuardDog.roll_back_in_progress)
				sender.sendMessage(ChatColor.RED
						+ "This is not insubordination, but I am afraid that a roll back is already in progress. Therefore, I cannot start a new one. This current roll back must terminate first.");
			else
				saveTheLogsPartIChrono();
		} else if (method.equals("saveTheLogsPartIChrono"))
			saveTheLogsPartIChrono();
		else if (method.equals("saveTheLogsPartIIPos"))
			saveTheLogsPartIIPos();
		else if (method.equals("saveTheLogsPartIIICause"))
			saveTheLogsPartIIICause();
		else if (method.equals("rollBackPartIReadLogs"))
			rollBackPartIReadLogs();
		else if (method.equals("rollBackPartIIChangeWorld"))
			rollBackPartIIChangeWorld();
		else if (method.equals("rollBackPartIIIChrono"))
			rollBackPartIIIChrono();
		else if (method.equals("rollBackPartIVPos"))
			rollBackPartIVPos();
		else if (method.equals("rollBackPartVCause"))
			rollBackPartVCause();
		else if (method.equals("rollBackPartVIRewriteLogs"))
			rollBackPartVIRewriteLogs();
		else
			sender.sendMessage(ChatColor.DARK_RED + "What the hell is \"" + method + "\"? Recheck your method input for this TimedMethod.");
	}

	private void trackTNT(Location location, String cause) {
		for (Entity entity : location.getWorld().getEntities())
			// I chose 2 because the square root of 2 is the largest possible distance away from the block's location within a 1m radius
			if (entity.getType() == EntityType.PRIMED_TNT && entity.getLocation().distanceSquared(location) < 5
					&& (new_primed_TNT == null || new_primed_TNT.getLocation().distanceSquared(location) > entity.getLocation().distanceSquared(location)))
				new_primed_TNT = entity;
		if (new_primed_TNT != null)
			myGuardDog.TNT_causes.put(new_primed_TNT.getUniqueId(), cause);
	}

	private void trackEndermanPlacements(Block block) {
		myGuardDog.events.add(new Event("an Enderman", "placed", block, null));
	}

	private void trackReactionBreaks(Event new_event) {
		// if the type I.D. of the block logged in the event before the break is different from that block's current type I.D., log it as a break
		if (myPluginWiki.getItemIdAndData(new_event.objects[0], false)[0] != new_event.location.getBlock().getTypeId())
			// if the block is locked and the person who broke it isn't the owner of the block or an admin, undo the original break
			if (myGuardDog.locked_blocks.containsKey(new_event.location.getBlock()) && !new_event.cause.equals(myGuardDog.locked_blocks.get(new_event.location.getBlock()))
					&& !myGuardDog.server.getPlayerExact(new_event.cause).hasPermission("myguarddog.admin")) {
				Integer[] id_and_data = myPluginWiki.getItemIdAndData(new_event.objects[0], false);
				if (id_and_data == null)
					myGuardDog.console.sendMessage(ChatColor.DARK_RED + "I couldn't identify the item I.D. of \"" + new_event.objects[0] + "\"!");
				else {
					new_event.location.getBlock().setTypeIdAndData(id_and_data[0], id_and_data[1].byteValue(), false);
					myGuardDog.server.getPlayerExact(new_event.cause).sendMessage(
							ChatColor.RED + "This is " + myGuardDog.locked_blocks.get(new_event.location.getBlock()) + "'s "
									+ myPluginWiki.getItemName(new_event.location.getBlock(), false, true, true) + " and you can't break it.");
				}
			} else
				myGuardDog.events.add(new_event);
	}

	private void trackLiquidRecession(Event new_event) {
		// if the type I.D. of the block logged in the event is air, log it as a removal
		if (new_event.location.getBlock().getType() == Material.AIR) {
			myGuardDog.events.add(new_event);
			for (int i = 0; i < 5; i++) {
				int x = new_event.location.getBlockX(), y = new_event.location.getBlockY(), z = new_event.location.getBlockZ();
				if (i == 0)
					x--;
				else if (i == 1)
					x++;
				else if (i == 2)
					y--;
				else if (i == 3)
					z--;
				else
					z++;
				Location location = new Location(new_event.location.getWorld(), x, y, z);
				if (location.getBlock().isLiquid()) {
					String object = myPluginWiki.getItemName(location.getBlock(), true, true, false);
					if (object == null)
						object = "something";
					else
						object = myGuardDog.replaceAll(object, "stationary ", "");
					myGuardDog.server.getScheduler().scheduleSyncDelayedTask(myGuardDog.mGD,
							new myGuardDog$1(sender, "track liquid recession", new Event(new_event.cause, "removed", object, location, new_event.in_Creative_Mode)), delay);
				}
			}
		}
	}

	private void saveTheLogsPartIChrono() {
		// the purpose of this line is to direct the run() operator to the statement where it does not reset the initializer variables (see the run() operator
		// section for this method to see what I mean)
		method = "saveTheLogsPartIChrono";
		if (myGuardDog.save_in_progress)
			myGuardDog.save_in_progress = false;
		if (myGuardDog.roll_back_in_progress && parameters != null)
			myGuardDog.roll_back_in_progress = false;
		try {
			if (first_iteration) {
				first_iteration = false;
				myGuardDog.logs_folder.mkdirs();
				myGuardDog.chrono_logs_folder.mkdirs();
				myGuardDog.position_logs_folder.mkdirs();
				myGuardDog.cause_logs_folder.mkdirs();
				// finalize the list of events to save so that new events won't get saved later in this process and not in earlier parts in chrono logs and such
				for (Event event : myGuardDog.events)
					events_to_save.add(event);
				myGuardDog.events = new ArrayList<Event>();
				// cancel the save if there are no events to save
				if (events_to_save.size() == 0) {
					if (display_message)
						if (sender instanceof Player) {
							sender.sendMessage(ChatColor.YELLOW + "Nothing new has happened since the last save. There's nothing to save.");
							myGuardDog.console.sendMessage(ChatColor.YELLOW + sender.getName()
									+ " tried to save the recent events, but nothing new has happened since the last save.");
						} else
							myGuardDog.console.sendMessage(ChatColor.YELLOW + "Nothing new has happened since the last save. There's nothing to save.");
					if (parameters != null) {
						first_iteration = true;
						rollBackPartIReadLogs();
					}
					return;
				}
				// save the chronologically-ordered logs first
				for (File a_log_file : myGuardDog.chrono_logs_folder.listFiles())
					if (a_log_file.getName().endsWith("now.txt")) {
						log_file = a_log_file;
						break;
					}
				if (log_file == null)
					log_file = new File(myGuardDog.chrono_logs_folder, events_to_save.get(0).getTime('\'') + " " + events_to_save.get(0).getDate('-') + " - now.txt");
				// this part attempts to make sure each log file has 250,000 events max, then splits into a new log file
				// the split index is the index of the last event that will be in the current log; it's -1 if there's no need to split the log file
				split_index = -1;
				// count the number of events already in the file
				int events_in_this_file = 0;
				if (log_file.exists()) {
					BufferedReader in = new BufferedReader(new FileReader(log_file));
					while (in.readLine() != null)
						events_in_this_file++;
					in.close();
				}
				if (events_in_this_file + events_to_save.size() > 250000) {
					split_index = 250000 - events_in_this_file;
					final File new_log_file =
							new File(myGuardDog.chrono_logs_folder, log_file.getName().split(" - ")[0] + " - " + events_to_save.get(split_index).getTime('\'') + " "
									+ events_to_save.get(split_index).getDate('-') + ".txt");
					if (log_file.exists())
						log_file.renameTo(new_log_file);
					log_file = new_log_file;
				}
				// the estimated time = 10 ticks for every 100 events (1 for chrono logging, 4 for position logging, and 5 for cause logging) converted to ms
				// minimum events to take 1 second = 1s * 20ticks/s * (100 events / 10 ticks) = 200 events
				if (events_to_save.size() > 200)
					sender.sendMessage(ChatColor.YELLOW + "All right. Just give me "
							+ myGuardDog.translateTimeInmsToString(10 * events_to_save.size() / 100 / 20 * 1000, true) + " to save your files.");
			}
			BufferedWriter out = new BufferedWriter(new FileWriter(log_file, true));
			for (int i = 100 * iterations; i < 100 * (iterations + 1); i++) {
				if (i == events_to_save.size()) {
					out.close();
					if (events_to_save.size() > 200)
						sender.sendMessage(ChatColor.YELLOW + "Making progress...");
					// reset iterations for saveTheLogsPartIIPos() to use
					first_iteration = true;
					saveTheLogsPartIIPos();
					return;
				}
				// if it's the first line of a file or the first
				out.write(events_to_save.get(i).save_line);
				// split the files if necessary
				if (i != split_index)
					out.newLine();
				else {
					out.close();
					if (events_to_save.size() - split_index <= 250000)
						log_file =
								new File(myGuardDog.chrono_logs_folder, events_to_save.get(i + 1).getTime('\'') + " " + events_to_save.get(i + 1).getDate('-') + " - now.txt");
					else {
						split_index = split_index + 250000;
						log_file =
								new File(myGuardDog.chrono_logs_folder, events_to_save.get(i + 1).getTime('\'') + " " + events_to_save.get(i + 1).getDate('-') + " - "
										+ events_to_save.get(split_index - 1).getTime('\'') + " " + events_to_save.get(split_index - 1).getDate('-') + ".txt");
					}
					out = new BufferedWriter(new FileWriter(log_file, true));
				}
			}
			out.close();
		} catch (IOException exception) {
			sender.sendMessage(ChatColor.DARK_RED + "I couldn't save the chronological log files! There was an IOException in the way!");
			exception.printStackTrace();
			return;
		}
		iterations++;
		if (!myGuardDog.hard_save)
			myGuardDog.server.getScheduler().scheduleSyncDelayedTask(myGuardDog.mGD, this, 1);
		else
			run();
	}

	private void saveTheLogsPartIIPos() {
		method = "saveTheLogsPartIIPos";
		if (first_iteration) {
			iterations = 0;
			first_iteration = false;
		}
		if (myGuardDog.save_in_progress)
			myGuardDog.save_in_progress = false;
		if (myGuardDog.roll_back_in_progress && parameters != null)
			myGuardDog.roll_back_in_progress = false;
		try {
			for (int i = 25 * iterations; i < 25 * (iterations + 1); i++) {
				if (i >= events_to_save.size()) {
					if (events_to_save.size() > 200)
						sender.sendMessage(ChatColor.YELLOW + "I'm over halfway there....");
					// reset iterations for saveTheLogsPartIIICause() to use
					first_iteration = true;
					saveTheLogsPartIIICause();
					return;
				}
				File log_file =
						new File(myGuardDog.position_logs_folder, "x = " + events_to_save.get(i).x + " " + events_to_save.get(i).world.getWorldFolder().getName() + ".txt");
				ArrayList<String> previous_data = new ArrayList<String>();
				if (!log_file.exists())
					log_file.createNewFile();
				// first, we need to read all the logs currently on this file and save them. Then, we can write out all the newest events, then add back
				// the ones that we read beforehand because for some stupid reason, you can't add text to the beginning of a text document; if you try
				// to add text to the beginning, it just deletes everything that was already there
				else {
					BufferedReader in = new BufferedReader(new FileReader(log_file));
					String save_line = in.readLine();
					while (save_line != null) {
						previous_data.add(save_line);
						save_line = in.readLine();
					}
					in.close();
				}
				BufferedWriter out = new BufferedWriter(new FileWriter(log_file));
				// write the new data
				out.write(events_to_save.get(i).save_line);
				out.newLine();
				// put back the old data
				for (String old_save_line : previous_data) {
					out.write(old_save_line);
					out.newLine();
				}
				out.close();
			}
		} catch (IOException exception) {
			sender.sendMessage(ChatColor.DARK_RED + "I couldn't save the position-oriented log files! There was an IOException in the way!");
			exception.printStackTrace();
			return;
		}
		iterations++;
		if (!myGuardDog.hard_save)
			myGuardDog.server.getScheduler().scheduleSyncDelayedTask(myGuardDog.mGD, this, 1);
		else
			run();
	}

	private void saveTheLogsPartIIICause() {
		method = "saveTheLogsPartIIICause";
		if (first_iteration) {
			iterations = 0;
			first_iteration = false;
		}
		if (myGuardDog.save_in_progress)
			myGuardDog.save_in_progress = false;
		if (myGuardDog.roll_back_in_progress && parameters != null)
			myGuardDog.roll_back_in_progress = false;
		try {
			for (int i = 20 * iterations; i < 20 * (iterations + 1); i++) {
				if (i >= events_to_save.size()) {
					// confirm that the saving is complete
					if (display_message) {
						if (events_to_save.size() == 1)
							sender.sendMessage(ChatColor.YELLOW + "The one event that has happened on your server has been saved.");
						else
							sender.sendMessage(ChatColor.YELLOW + "The " + events_to_save.size() + " events that have happened on your server have been saved.");
						if (sender instanceof Player)
							if (events_to_save.size() == 1)
								myGuardDog.console.sendMessage(ChatColor.YELLOW + sender.getName() + " saved the one event that has happened on your server.");
							else
								myGuardDog.console.sendMessage(ChatColor.YELLOW + sender.getName() + " saved the " + events_to_save.size()
										+ " events that have happened on your server.");
					}
					if (parameters != null) {
						first_iteration = true;
						rollBackPartIReadLogs();
					}
					return;
				}
				final String cause = events_to_save.get(i).cause;
				int relevant_log_files = 0;
				// locate the appropriate log file
				// here's how this works: it counts the number of "relevant" log files (meaning log files that log that cause), then finds the log file
				// that's marked by a number equal to the number of relevant log files. This works because when these log files split, it makes sure all
				// files with the same cause have a number in parentheses at the end of their name like this:
				// "a creeper (3).txt" <-- this would be the third file logging all the events caused by "a creeper" (well, events caused by all
				// creepers, really, not one single creeper)
				for (File a_log_file : myGuardDog.cause_logs_folder.listFiles())
					if (a_log_file.getName().startsWith(cause))
						relevant_log_files++;
				File log_file;
				if (relevant_log_files <= 1) {
					log_file = new File(myGuardDog.cause_logs_folder, cause + ".txt");
					if (!log_file.exists())
						log_file.createNewFile();
				} else
					log_file = new File(myGuardDog.cause_logs_folder, cause + " (" + relevant_log_files + ").txt");
				// like the position-oriented logs, these need to be saved from most recent to oldest, so we'll have to read all the previous data
				// first, then add the new stuff, then replace the old stuff
				ArrayList<String> previous_data = new ArrayList<String>();
				BufferedReader in = new BufferedReader(new FileReader(log_file));
				String save_line = in.readLine();
				while (save_line != null) {
					previous_data.add(save_line);
					save_line = in.readLine();
				}
				in.close();
				// events caused by the same person often come in groups, especially in cases like when there's only one person playing on the server
				// for a long time. Therefore, it's more efficient to find all the nearby events with the same cause as the one we're logging now and write them
				// all at once instead of just going one at a time and saving, erasing, then rewriting all the old data every single time
				ArrayList<Event> cause_roll_back_events = new ArrayList<Event>();
				for (; i < 20 * (iterations + 1) && i < events_to_save.size(); i++) {
					if (!events_to_save.get(i).cause.equals(cause)) {
						i--;
						break;
					}
					// adding the event at 0 reverses the order of the events, making them log properly in reverse chronological order
					cause_roll_back_events.add(0, events_to_save.get(i));
				}
				// if it looks like this file is going to contain more than 250,000 events, rename it if necessary to allow myGuardDog to split the logs
				// (i.e. change "creeper.txt", for example, to "creeper (1).txt"), then find out how many events will overflow into the new log file and
				// make that the split_index
				int split_index = -1;
				if (previous_data.size() + cause_roll_back_events.size() >= 250000) {
					if (!log_file.getName().contains("\\(") && !log_file.getName().contains("\\)"))
						log_file.renameTo(new File(myGuardDog.cause_logs_folder, cause + " (1).txt"));
					// remember that we're saving backwards, so the first events to be saved will actually be saved to the new log file, not the old one
					log_file = new File(myGuardDog.cause_logs_folder, cause + " (" + (relevant_log_files + 1) + ").txt");
					log_file.createNewFile();
					// remember that we're saving backwards, so the first events to be saved will actually be saved to the new log file, not the old one
					split_index = previous_data.size() + cause_roll_back_events.size() - 250000;
				}
				// this counter will coordinate with the split_index so that the logs save the right amount of info to the right file
				int counter = 0;
				BufferedWriter out = new BufferedWriter(new FileWriter(log_file, false));
				// write the new data
				for (Event event : cause_roll_back_events) {
					out.write(event.save_line);
					out.newLine();
					// update the counter and split if necessary
					counter++;
					if (counter == split_index) {
						// remember that we're saving backwards, so now we need to switch to OLDER log files at a split
						log_file = new File(myGuardDog.cause_logs_folder, cause + " (" + relevant_log_files + ").txt");
						log_file.createNewFile();
						out = new BufferedWriter(new FileWriter(log_file, false));
					}
				}
				// put back the old data
				for (String old_save_line : previous_data) {
					out.write(old_save_line);
					out.newLine();
					// we don't need to track how many of these we save or if we're going over 250,000 because these were all saved together before. It
					// wouldn't make much sense if they needed to be split now when they didn't need to before
				}
				out.close();
			}
		} catch (IOException exception) {
			sender.sendMessage(ChatColor.DARK_RED + "I couldn't save the cause-oriented log files! There was an IOException in the way!");
			exception.printStackTrace();
			return;
		}
		iterations++;
		if (!myGuardDog.hard_save)
			myGuardDog.server.getScheduler().scheduleSyncDelayedTask(myGuardDog.mGD, this, 1);
		else
			run();
	}

	private void rollBackPartIReadLogs() {
		method = "rollBackPartIReadLogs";
		if (myGuardDog.roll_back_in_progress)
			myGuardDog.roll_back_in_progress = false;
		if (first_iteration) {
			first_iteration = false;
			sender.sendMessage(ChatColor.YELLOW + "All right. Let me retrieve all the events within your search parameters. This may take a minute.");
			// read all the parameters
			radius = -1;
			for (int i = 0; i < parameters.length; i++) {
				if (!(sender instanceof Player)
						&& (parameters[i].toLowerCase().startsWith("r:") || parameters[i].toLowerCase().startsWith("rad:") || parameters[i].toLowerCase()
								.startsWith("radius:"))) {
					sender.sendMessage(ChatColor.RED + "How can you specify an area around you to roll back? You have no position! You're a console!");
					return;
				}
				if (parameters[i].toLowerCase().startsWith("by:")) {
					if (parameters[i].length() == 3) {
						sender.sendMessage(ChatColor.RED + "You forgot to list what causes you want me to roll back!");
						return;
					}
					String[] my_causes = parameters[i].substring(3).split(",");
					for (String search : my_causes) {
						// autocomplete the names of the causes
						String autocompleted = null;
						if (search.contains("\\")) {
							search = search.replaceAll("\\\\", "");
							// check through the possible non-player event causes
							for (String non_player_cause : new String[] { "a creeper", "a Ghast", "the Ender Dragon", "an Enderman", "some lava", "some lightning",
									"a fireball", "something", "T.N.T.", "myGroundsKeeper" }) {
								String without_article = non_player_cause;
								if (non_player_cause.split(" ").length == 2)
									without_article = non_player_cause.split(" ")[1];
								else if (non_player_cause.split(" ").length == 3)
									// The Ender Dragon is the only thing listed that has more than one space
									without_article = "Ender Dragon";
								if (without_article.toLowerCase().contains(search.toLowerCase())
										&& (autocompleted == null || non_player_cause.indexOf(search) < autocompleted.indexOf(search)))
									autocompleted = non_player_cause;
							}
						} else
							autocompleted = myGuardDog.getFullName(search);
						if (autocompleted == null) {
							sender.sendMessage(ChatColor.RED + "Who's \"" + search + "\"?");
							sender.sendMessage(ChatColor.RED
									+ "If you want to search for a non-player cause like creepers, you need to put a \"\\\" in the object's name somewhere so I know not to look for a player.");
							return;
						} else
							causes.add(autocompleted);
					}
				} else if (parameters[i].toLowerCase().startsWith("a:")) {
					if (parameters[i].length() == 2) {
						sender.sendMessage(ChatColor.RED + "You forgot to list what actions you want me to roll back!");
						return;
					}
					for (String action : parameters[i].substring(2).split(","))
						actions.add(action);
				} else if (parameters[i].toLowerCase().startsWith("action:")) {
					if (parameters[i].length() == 7) {
						sender.sendMessage(ChatColor.RED + "You forgot to list what actions you want me to roll back!");
						return;
					}
					for (String action : parameters[i].substring(7).split(","))
						actions.add(action);
				} else if (parameters[i].toLowerCase().startsWith("actions:")) {
					if (parameters[i].length() == 8) {
						sender.sendMessage(ChatColor.RED + "You forgot to list what actions you want me to roll back!");
						return;
					}
					for (String action : parameters[i].substring(8).split(","))
						actions.add(action);
				} else if (parameters[i].toLowerCase().startsWith("o:")) {
					if (parameters[i].length() == 2) {
						sender.sendMessage(ChatColor.RED + "You forgot to list what objects you want me to roll back!");
						return;
					}
					for (String object : parameters[i].substring(2).split(","))
						objects.add(object);
				} else if (parameters[i].toLowerCase().startsWith("object:")) {
					if (parameters[i].length() == 2) {
						sender.sendMessage(ChatColor.RED + "You forgot to list what objects you want me to roll back!");
						return;
					}
					for (String object : parameters[i].substring(7).split(","))
						objects.add(object);
				} else if (parameters[i].toLowerCase().startsWith("objects:")) {
					if (parameters[i].length() == 2) {
						sender.sendMessage(ChatColor.RED + "You forgot to list what objects you want me to roll back!");
						return;
					}
					for (String object : parameters[i].substring(8).split(","))
						objects.add(object);
				} else if (parameters[i].toLowerCase().startsWith("r:") || parameters[i].toLowerCase().startsWith("rad:") || parameters[i].toLowerCase().startsWith("radius:"))
					try {
						if (parameters[i].toLowerCase().startsWith("r:")) {
							if (parameters[i].length() == 2) {
								sender.sendMessage(ChatColor.RED + "You forgot to tell me the radius of the area you want me to roll back!");
								return;
							}
							radius = Integer.parseInt(parameters[i].substring(2));
						} else if (parameters[i].toLowerCase().startsWith("rad:")) {
							if (parameters[i].length() == 4) {
								sender.sendMessage(ChatColor.RED + "You forgot to tell me the radius of the area you want me to roll back!");
								return;
							}
							radius = Integer.parseInt(parameters[i].substring(4));
						} else if (parameters[i].toLowerCase().startsWith("radius:")) {
							if (parameters[i].length() == 7) {
								sender.sendMessage(ChatColor.RED + "You forgot to tell me the radius of the area you want me to roll back!");
								return;
							}
							radius = Integer.parseInt(parameters[i].substring(7));
						}
						if (radius < 0) {
							sender.sendMessage(ChatColor.RED + "A negative radius...right...okay, um...can I get a positive radius, please? Thanks.");
							return;
						}
					} catch (NumberFormatException exception) {
						sender.sendMessage(ChatColor.RED + "Oh, yeah. \"" + parameters[i].substring(2)
								+ "\" comes right after 3, right? Oh, wait. No it doesn't. In fact, it's not an integer at all. Try again.");
						return;
					}
				else {
					sender.sendMessage(ChatColor.RED + "I'm not sure that I understand what \"" + parameters[i] + "\" means.");
					return;
				}
			}
			// find all the relevant log files
			if (causes.size() > 0)
				for (String cause : causes) {
					// find all the relevant log files
					// start by looking for a file with no numbers (which occur when there are few enough events to write them all in one log file)
					File log_file = new File(myGuardDog.cause_logs_folder, cause + ".txt");
					if (log_file.exists())
						relevant_log_files.add(log_file);
					else {
						// if the non-numbered file doesn't exist, look for a bunch of numbered files
						int counter = 1;
						log_file = new File(myGuardDog.cause_logs_folder, cause + " (1).txt");
						while (log_file.exists()) {
							relevant_log_files.add(log_file);
							counter++;
							log_file = new File(myGuardDog.cause_logs_folder, cause + " (" + counter + ").txt");
						}
					}
				}
			// if a cause wasn't declared, just get all the chronological logs
			else
				for (File log_file : myGuardDog.chrono_logs_folder.listFiles())
					relevant_log_files.add(log_file);
			if (relevant_log_files.size() == 0) {
				sender.sendMessage(ChatColor.RED + "No events have occurred that fit your parameters!");
				return;
			}
			// read and gather all the relevant events from the log files, allowing a 100ms space between every 50 events
			if (sender instanceof Player)
				origin = ((Player) sender).getLocation();
			else
				origin = null;
			try {
				in = new BufferedReader(new FileReader(relevant_log_files.get(0)));
			} catch (FileNotFoundException e) {
				sender.sendMessage(ChatColor.RED + "I couldn't find the first relevant log file!");
				e.printStackTrace();
				return;
			}
		}
		try {
			String save_line;
			for (int i = 0; i < 100; i++) {
				save_line = in.readLine();
				if (save_line == null) {
					in.close();
					// if we're not done, move on to the next relevant log file
					if (log_counter < relevant_log_files.size() - 1) {
						log_counter++;
						in = new BufferedReader(new FileReader(relevant_log_files.get(log_counter)));
						save_line = in.readLine();
					} // if we are done, go to part II of the roll back process
					else {
						first_iteration = true;
						rollBackPartIIChangeWorld();
						return;
					}
				}
				Event event = new Event(save_line);
				boolean causes_satisfied = false, actions_satisfied = false, objects_satisfied = false, radius_satisfied = false;
				// check the causes
				if (causes.size() == 0 || causes.contains(event.cause))
					causes_satisfied = true;
				// check the actions
				if (actions.size() == 0)
					actions_satisfied = true;
				else
					for (String action : actions)
						if (event.action.replaceAll(" ", "").contains(action)) {
							actions_satisfied = true;
							break;
						}
				// check the objects
				if (objects.size() == 0)
					objects_satisfied = true;
				else
					for (String object : objects)
						for (String event_object : event.objects) {
							Integer event_id = myPluginWiki.getItemIdAndData(event_object, null)[0], object_id = myPluginWiki.getItemIdAndData(object, null)[0];
							if (event_id == null && object_id == null) {
								event_id = myPluginWiki.getEntityIdAndData(event_object)[0];
								object_id = myPluginWiki.getEntityIdAndData(object)[0];
							}
							if (event_id != null && event_id == object_id) {
								objects_satisfied = true;
								break;
							}
						}
				// check the radius
				if (radius == -1)
					radius_satisfied = true;
				else if (origin != null && origin.getX() - radius <= event.x && origin.getX() + radius >= event.x && origin.getY() - radius <= event.y
						&& origin.getY() + radius >= event.y && origin.getZ() - radius <= event.z && origin.getZ() + radius >= event.z
						&& origin.getWorld().equals(event.world))
					radius_satisfied = true;
				// set this event up for rollback
				if (causes_satisfied && actions_satisfied && objects_satisfied && radius_satisfied && !event.rolled_back && event.canBeRolledBack()) {
					// if that event was not the last roll-back-able event to occur at that block, cancel it
					BufferedReader in =
							new BufferedReader(new FileReader(new File(myGuardDog.position_logs_folder, "x = " + event.x + " " + event.world.getWorldFolder().getName()
									+ ".txt")));
					String my_save_line = in.readLine();
					Event my_event = new Event(my_save_line);
					// do not roll back this event if there is an event at the same location that was a more recent, opposite event by someone whose
					// actions aren't being rolled back
					while (my_save_line != null
							&& !my_event.equals(event)
							&& (!my_event.location.equals(event.location) || (!(my_event.isPlacement() && event.isRemoval() || my_event.isRemoval() && event.isPlacement()) && !causes
									.contains(my_event.cause)))) {
						my_save_line = in.readLine();
						my_event = new Event(my_save_line);
					}
					if (!my_event.equals(event))
						continue;
					// ORDER: remove all liquids (part 1), then remove all blocks that must be attached to something (part 1), then roll back solid
					// blocks with sand and gravel organized from bottom to top for replacement (part 2) and top to bottom for removal (part 3), then
					// replace blocks that must be attached to something (part 4), then replace liquids (part 4)
					Boolean must_be_attached = myPluginWiki.mustBeAttached(event.objects[0], null);
					if (must_be_attached == null) {
						myGuardDog.console.sendMessage(ChatColor.RED + "I couldn't find the block that goes with \"" + event.objects[0] + "\"!");
					} else if (event.isPlacement() && event.objects != null && (event.objects[0].equals("lava") || event.objects[0].equals("water")))
						roll_back_events.add(0, event);
					else if (event.isPlacement() && event.objects != null && must_be_attached)
						roll_back_events.add(event);
					else if (event.isRemoval() && event.objects != null && !must_be_attached && !(event.objects[0].equals("lava") || event.objects[0].equals("water"))) {
						boolean added = false;
						// sort from bottom to top
						for (int j = 0; j < roll_back_events_part2.size(); j++)
							if (roll_back_events_part2.get(j).y >= event.y) {
								roll_back_events_part2.add(j, event);
								added = true;
								break;
							}
						// if it's still not added, add it to the end
						if (!added)
							roll_back_events_part2.add(event);
					} else if (event.isPlacement() && event.objects != null && !must_be_attached && !(event.objects[0].equals("lava") || event.objects[0].equals("water"))) {
						boolean added = false;
						// sort from top to bottom
						for (int j = 0; j < roll_back_events_part3.size(); j++)
							if (roll_back_events_part3.get(j).y <= event.y) {
								roll_back_events_part3.add(j, event);
								added = true;
								break;
							} // if it's still not added, add it to the end
						if (!added)
							roll_back_events_part3.add(event);
					} else if (event.isRemoval() && event.objects != null && must_be_attached)
						roll_back_events_part4.add(0, event);
					else if (event.isRemoval() && event.objects != null && (event.objects[0].equals("lava") || event.objects[0].equals("water")))
						roll_back_events_part4.add(event);
					else if (!event.action.equals("killed")) {
						myGuardDog.console.sendMessage(ChatColor.DARK_RED
								+ "Hey. There was this weird event. I didn't know where to insert it in the ordered roll back, so I put it at the end.");
						myGuardDog.console.sendMessage(ChatColor.WHITE + event.save_line);
						roll_back_events_part4.add(event);
					}
				} // TODO EXT TEMP
				else {
					myGuardDog.console.sendMessage(ChatColor.WHITE + save_line);
					if (!radius_satisfied)
						myGuardDog.console.sendMessage(ChatColor.RED + "The radius wasn't satisfied!");
					else if (!causes_satisfied) {
						myGuardDog.console.sendMessage(ChatColor.RED + "The causes weren't satisfied!");
						myGuardDog.console.sendMessage(ChatColor.YELLOW + "cause = \"" + causes.get(0) + "\"");
					}
				}
				// TODO END TEMP
			}
		} catch (IOException exception) {
			sender.sendMessage(ChatColor.DARK_RED + "Great...IOException while reading the log files for a rollback...");
			exception.printStackTrace();
			return;
		}
		// set up the next event
		myGuardDog.server.getScheduler().scheduleSyncDelayedTask(myGuardDog.mGD, this, 1);
	}

	private void rollBackPartIIChangeWorld() {
		method = "rollBackPartIIChangeWorld";
		if (myGuardDog.roll_back_in_progress)
			myGuardDog.roll_back_in_progress = false;
		if (first_iteration) {
			first_iteration = false;
			iterations = 0;
			// compile the roll_back_events list
			for (Event event : roll_back_events_part2)
				roll_back_events.add(event);
			for (Event event : roll_back_events_part3)
				roll_back_events.add(event);
			for (Event event : roll_back_events_part4)
				roll_back_events.add(event);
			if (roll_back_events.size() == 0) {
				sender.sendMessage(ChatColor.RED + "I don't think anything has happened yet that matches those criteria.");
				return;
			} else if (roll_back_events.size() == 1)
				sender.sendMessage(ChatColor.YELLOW + "I only found one event that matches your criteria. This ought to be easy.");
			// at 50ms per event rolled back, I calculate it would take 100 events 5 seconds to be rolled back. If it's going to take less than five
			// seconds, don't bother with giving an estimated time of completion
			else if (roll_back_events.size() < 100)
				sender.sendMessage(ChatColor.YELLOW + "I found " + roll_back_events.size() + " events that fit your criteria. This will take no time at all. Rolling back...");
			else
				// give the command sender an estimated time of completion if there are more than 100 to roll back (it will take 50ms to roll back
				// one event)
				sender.sendMessage(ChatColor.YELLOW + "I found " + roll_back_events.size() + " events that fit your criteria. This will take about "
						+ myGuardDog.translateTimeInmsToString(roll_back_events.size() * 50, true) + " to roll back. Here we go.");
		}
		Event event = roll_back_events.get(iterations);
		// this part restores mobs (but only non-hostile animals, of course)
		if (event.action.equals("killed")) {
			Integer[] id_and_data = myPluginWiki.getEntityIdAndData(event.objects[0]);
			if (id_and_data == null)
				sender.sendMessage(ChatColor.DARK_RED + "What the heck is \"" + event.objects[0] + "\"?");
			EntityType entity = EntityType.fromId(id_and_data[0]);
			if (entity == null)
				sender.sendMessage(ChatColor.DARK_RED + "What the heck is \"" + event.objects[0] + "\"?");
			// ids from 90-120 are non-hostile mobs (except bats, which are 65)
			else if (entity.isSpawnable() && (entity == EntityType.BAT || (id_and_data[0] >= 90 && id_and_data[0] <= 120))) {
				Entity new_entity = event.world.spawnEntity(event.location, entity);
				// if the new entity is a villager, be sure to give them the correct profession
				if (entity == EntityType.VILLAGER && id_and_data[1] > 0)
					((Villager) new_entity).setProfession(Villager.Profession.getProfession(id_and_data[1]));
			}
		} // this part restores broken or destroyed blocks
		else if (event.action.equals("broke") || event.action.equals("burned") || event.action.equals("creeper'd") || event.action.equals("T.N.T.'d")
				|| event.action.equals("blew up")) {
			Integer[] id_and_data = myPluginWiki.getItemIdAndData(event.objects[0], false);
			if (id_and_data != null) {
				event.location.getBlock().setTypeId(id_and_data[0]);
				// to convert an Integer to a Byte, I need to convert the Integer to a String, then to a Byte
				if (id_and_data[1] > 0)
					event.location.getBlock().setData(Byte.valueOf(String.valueOf(id_and_data[1])));
			} else {
				sender.sendMessage(ChatColor.RED + "I couldn't find the item I.D. of the object in this event!");
				sender.sendMessage(ChatColor.RED + "The item was called \"" + event.objects[0] + "\".");
				sender.sendMessage(ChatColor.RED + "The save line said \"" + ChatColor.WHITE + event.save_line + ChatColor.RED + "\".");
			}
		} // this part removes placed blocks
		else if (event.action.equals("placed") || event.action.equals("grew"))
			event.location.getBlock().setTypeId(0);
		iterations++;
		if (iterations > roll_back_events.size() - 1) {
			if (roll_back_events.size() > 100)
				sender.sendMessage(ChatColor.YELLOW + "Almost done. I just need to update the log files now.");
			first_iteration = true;
			rollBackPartIIIChrono();
			return;
		} // I chose 80 events for the cutoff because these messages are supposed to appear every quarter of the way through. 80 events means each
			// quarter happens after 20 events, which means that there will be at least one second in between each progress message
		else if (roll_back_events.size() > 80)
			if (iterations / roll_back_events.size() >= 0.75 && (iterations - 1) / roll_back_events.size() < 0.75)
				// we're 75% done
				sender.sendMessage(ChatColor.YELLOW + "Almost there...");
			else if (iterations / roll_back_events.size() >= 0.5 && (iterations - 1) / roll_back_events.size() < 0.5)
				// we're 50% done
				sender.sendMessage("I'm about halfway done with that roll back.");
			else if (iterations / roll_back_events.size() >= 0.25 && (iterations - 1) / roll_back_events.size() < 0.25)
				// we're 25% done
				sender.sendMessage("Making progress...");
		// schedule the next event
		myGuardDog.server.getScheduler().scheduleSyncDelayedTask(myGuardDog.mGD, this, 1);
	}

	private void rollBackPartIIIChrono() {
		method = "rollBackPartIIIChrono";
		if (myGuardDog.roll_back_in_progress)
			myGuardDog.roll_back_in_progress = false;
		try {
			if (first_iteration) {
				// if it's the first iteration, make sure all the variables needed start out blank
				relevant_log_files = new ArrayList<File>();
				events_for_this_file = new ArrayList<String>();
				log_counter = 0;
				events_located_so_far = 0;
				this_file_has_roll_back_events = false;
				in = new BufferedReader(new FileReader(myGuardDog.chrono_logs_folder.listFiles()[log_counter]));
				first_iteration = false;
			}
			String save_line;
			// it needs to search through roll_back_events for every item on the logs, so it makes sense that it would read less if there are more roll back
			// events since it needs more time to comapre each save line to the roll back events
			// the +1 ensures that it reads at least one event at every iteration
			for (int i = 0; i < 100; i++) {
				save_line = in.readLine();
				if (save_line == null) {
					if (this_file_has_roll_back_events)
						updated_events.put(myGuardDog.chrono_logs_folder.listFiles()[log_counter], events_for_this_file);
					this_file_has_roll_back_events = false;
					events_for_this_file = new ArrayList<String>();
					log_counter++;
					in.close();
					if (events_located_so_far == roll_back_events.size()) {
						first_iteration = true;
						if (roll_back_events.size() > 100)
							sender.sendMessage(ChatColor.YELLOW + "Making progress...");
						rollBackPartIVPos();
						return;
					}
					try {
						in = new BufferedReader(new FileReader(myGuardDog.chrono_logs_folder.listFiles()[log_counter]));
					} catch (ArrayIndexOutOfBoundsException exception) {
						// this is for if we somehow run out of log files to check without finding all the roll back events
						myGuardDog.console.sendMessage(ChatColor.DARK_RED + "I couldn't locate all the rolled back events in the chronological logs!");
						if (roll_back_events.size() > 100)
							sender.sendMessage(ChatColor.YELLOW + "Making progress...");
						first_iteration = true;
						rollBackPartIVPos();
						return;
					}
				}
				// if it's one of the events that was rolled back, add "[rolled back]" to the end of the save line and record the event
				for (Event event : roll_back_events)
					if (event.save_line.equals(save_line)) {
						events_located_so_far++;
						this_file_has_roll_back_events = true;
						save_line = save_line + " [rolled back]";
						break;
					}
				events_for_this_file.add(save_line);
			}
		} catch (IOException exception) {
			sender.sendMessage(ChatColor.DARK_RED + "Great...IOException while updating the chronological log files for a rollback...");
			exception.printStackTrace();
			return;
		}
		myGuardDog.server.getScheduler().scheduleSyncDelayedTask(myGuardDog.mGD, this, 1);
	}

	private void rollBackPartIVPos() {
		method = "rollBackPartIVPos";
		if (myGuardDog.roll_back_in_progress)
			myGuardDog.roll_back_in_progress = false;
		try {
			if (first_iteration) {
				// if it's the first iteration, make sure all the variables needed start out blank
				this_file_has_roll_back_events = false;
				events_located_so_far = 0;
				log_file =
						new File(myGuardDog.position_logs_folder, "x = " + roll_back_events.get(0).x + " " + roll_back_events.get(0).world.getWorldFolder().getName() + ".txt");
				if (!log_file.exists())
					log_file.createNewFile();
				in = new BufferedReader(new FileReader(log_file));
				iterations = 0;
				first_iteration = false;
			}
			String save_line = null;
			for (int i = 50 * iterations; i < 50 * (iterations + 1); i++) {
				// if we've reached the end of the roll back events, move on to the next step
				if (i == roll_back_events.size()) {
					in.close();
					myGuardDog.console.sendMessage(ChatColor.DARK_RED + "I couldn't find all the roll back events in the position logs!");
					if (this_file_has_roll_back_events)
						updated_events.put(log_file, events_for_this_file);
					first_iteration = true;
					if (roll_back_events.size() > 100)
						sender.sendMessage(ChatColor.YELLOW + "I'm over halfway there....");
					rollBackPartVCause();
					return;
				}
				if (save_line == null) {
					if (this_file_has_roll_back_events)
						updated_events.put(log_file, events_for_this_file);
					events_for_this_file = new ArrayList<String>();
					log_file =
							new File(myGuardDog.position_logs_folder, "x = " + roll_back_events.get(i).x + " " + roll_back_events.get(i).world.getWorldFolder().getName()
									+ ".txt");
					// when this method gets a position log file, it updates ALL the events in that file as rolled back that were rolled back, not just the one
					// that was used to find that file. Therefore, we need to search through the list until we find an event with a location that coordinates
					// with a position log file that has not been updated yet.
					while (updated_events.containsKey(log_file)) {
						i++;
						// if we've been through all of the roll_back_events, move on to the next part of the roll back
						if (i == roll_back_events.size()) {
							myGuardDog.console.sendMessage(ChatColor.DARK_RED + "I couldn't find all the roll back events in the position logs!");
							if (this_file_has_roll_back_events)
								updated_events.put(log_file, events_for_this_file);
							first_iteration = true;
							if (roll_back_events.size() > 100)
								sender.sendMessage(ChatColor.YELLOW + "I'm over halfway there....");
							rollBackPartVCause();
							return;
						} // if we reach the end of the portion to read for this iteration, break this loop and the larger for loop
						else if (i >= 50 * (iterations + 1))
							break;
						else
							log_file =
									new File(myGuardDog.position_logs_folder, "x = " + roll_back_events.get(i).x + " "
											+ roll_back_events.get(i).world.getWorldFolder().getName() + ".txt");
					}
					if (i >= 50 * (iterations + 1))
						break;
					in.close();
					in = new BufferedReader(new FileReader(log_file));
					save_line = in.readLine();
				}
				for (Event event : roll_back_events)
					if (event.save_line.equals(save_line)) {
						events_located_so_far++;
						this_file_has_roll_back_events = true;
						save_line = save_line + " [rolled back]";
						break;
					}
				events_for_this_file.add(save_line);
				if (events_located_so_far == roll_back_events.size()) {
					if (this_file_has_roll_back_events)
						updated_events.put(log_file, events_for_this_file);
					first_iteration = true;
					if (roll_back_events.size() > 100)
						sender.sendMessage(ChatColor.YELLOW + "I'm over halfway there....");
					rollBackPartVCause();
					return;
				}
				save_line = in.readLine();
			}
		} catch (IOException exception) {
			sender.sendMessage(ChatColor.DARK_RED + "Great...IOException while updating the position log files for a rollback...");
			exception.printStackTrace();
			return;
		}
		iterations++;
		myGuardDog.server.getScheduler().scheduleSyncDelayedTask(myGuardDog.mGD, this, 1);
	}

	private void rollBackPartVCause() {
		method = "rollBackPartVCause";
		if (myGuardDog.roll_back_in_progress)
			myGuardDog.roll_back_in_progress = false;
		try {
			if (first_iteration) {
				events_for_this_file = new ArrayList<String>();
				this_file_has_roll_back_events = false;
				events_located_so_far = 0;
				log_file = new File(myGuardDog.cause_logs_folder, roll_back_events.get(0).cause + ".txt");
				if (!log_file.exists())
					log_file = new File(myGuardDog.cause_logs_folder, roll_back_events.get(0).cause + " (1).txt");
				in = new BufferedReader(new FileReader(log_file));
				iterations = 0;
				first_iteration = false;
			}
			String save_line = in.readLine();
			for (int i = 50 * iterations; i < 50 * (iterations + 1); i++) {
				// if we've reached the end of the roll back events, move on to the next step
				if (i == roll_back_events.size()) {
					if (this_file_has_roll_back_events)
						updated_events.put(log_file, events_for_this_file);
					if (roll_back_events.size() > 100)
						sender.sendMessage(ChatColor.YELLOW + "Almost there...");
					first_iteration = true;
					rollBackPartVIRewriteLogs();
					return;
				}
				if (save_line == null) {
					if (this_file_has_roll_back_events) {
						updated_events.put(log_file, events_for_this_file);
						this_file_has_roll_back_events = false;
					}
					events_for_this_file = new ArrayList<String>();
					if (!log_file.getName().contains("(") || !log_file.getName().contains(")"))
						log_file = new File(myGuardDog.cause_logs_folder, roll_back_events.get(i).cause + ".txt");
					else {
						int old_log_number;
						try {
							old_log_number = Integer.parseInt(log_file.getName().substring(log_file.getName().indexOf('(') + 1, log_file.getName().indexOf(')')));
						} catch (NumberFormatException exception) {
							sender.sendMessage(ChatColor.DARK_RED + "Uh...I was trying to find the number I.D. of this cause log, but I read \""
									+ log_file.getName().substring(log_file.getName().indexOf('(') + 1, log_file.getName().indexOf(')'))
									+ "\", which isn't really an integer.");
							exception.printStackTrace();
							return;
						}
						log_file = new File(myGuardDog.cause_logs_folder, log_file.getName().replaceAll(String.valueOf(old_log_number), String.valueOf(old_log_number + 1)));
					}
					while (updated_events.containsKey(log_file)) {
						i++;
						if (i == roll_back_events.size()) {
							in.close();
							first_iteration = true;
							if (roll_back_events.size() > 100)
								sender.sendMessage(ChatColor.YELLOW + "Almost there...");
							rollBackPartVIRewriteLogs();
							return;
						}
						if (!log_file.getName().contains("(") || !log_file.getName().contains(")"))
							log_file = new File(myGuardDog.cause_logs_folder, roll_back_events.get(i).cause + ".txt");
						else {
							int old_log_number;
							try {
								old_log_number = Integer.parseInt(log_file.getName().substring(log_file.getName().indexOf('(') + 1, log_file.getName().indexOf(')')));
							} catch (NumberFormatException exception) {
								sender.sendMessage(ChatColor.DARK_RED + "Uh...I was trying to find the number I.D. of this cause log, but I read \""
										+ log_file.getName().substring(log_file.getName().indexOf('(') + 1, log_file.getName().indexOf(')'))
										+ "\", which isn't really an integer.");
								exception.printStackTrace();
								return;
							}
							log_file =
									new File(myGuardDog.cause_logs_folder, log_file.getName().replaceAll(String.valueOf(old_log_number), String.valueOf(old_log_number + 1)));
						}
					}
					in.close();
					in = new BufferedReader(new FileReader(log_file));
					save_line = in.readLine();
				}
				for (Event event : roll_back_events)
					if (event.save_line.equals(save_line)) {
						events_located_so_far++;
						this_file_has_roll_back_events = true;
						save_line = save_line + " [rolled back]";
						break;
					}
				events_for_this_file.add(save_line);
				if (events_located_so_far == roll_back_events.size()) {
					if (this_file_has_roll_back_events)
						updated_events.put(log_file, events_for_this_file);
					first_iteration = true;
					if (roll_back_events.size() > 100)
						sender.sendMessage(ChatColor.YELLOW + "Almost there...");
					rollBackPartVIRewriteLogs();
					return;
				}
				save_line = in.readLine();
			}
		} catch (IOException exception) {
			sender.sendMessage(ChatColor.DARK_RED + "Great...IOException while updating the cause log files for a rollback...");
			exception.printStackTrace();
			return;
		}
		iterations++;
		myGuardDog.server.getScheduler().scheduleSyncDelayedTask(myGuardDog.mGD, this, 1);
	}

	private void rollBackPartVIRewriteLogs() {
		method = "rollBackPartVIRewriteLogs";
		if (myGuardDog.roll_back_in_progress)
			myGuardDog.roll_back_in_progress = false;
		try {
			if (first_iteration) {
				log_counter = 0;
				log_file = (File) updated_events.keySet().toArray()[0];
				if (!log_file.exists())
					log_file.createNewFile();
				// the purpose of this next segment is to delete the contents of the existing file; apparently deleting the file and creating a new one
				// doesn't do the trick
				out = new BufferedWriter(new FileWriter(log_file));
				out.write("");
				// now we can make the writer we're actually going to be using for this file
				out = new BufferedWriter(new FileWriter(log_file, true));
				iterations = 0;
				first_iteration = false;
				// TODO TEMP
				// for (File log : updated_events.keySet()) {
				// myGuardDog.console.sendMessage(ChatColor.YELLOW + log.getName());
				// for (String line : updated_events.get(log))
				// myGuardDog.console.sendMessage(ChatColor.WHITE + line);
				// }
			}
			for (int i = 0; i < 100; i++) {
				// if all the events for this file have been written,...
				if (iterations == updated_events.get(log_file).size()) {
					out.close();
					// ...move on to the next file in the list or if those are all done...
					if (log_counter + 1 < updated_events.keySet().size()) {
						log_counter++;
						log_file = (File) updated_events.keySet().toArray()[log_counter];
						if (!log_file.exists())
							log_file.createNewFile();
						// the purpose of this next segment is to delete the contents of the existing file; apparently deleting the file and creating a new one
						// doesn't do the trick
						out = new BufferedWriter(new FileWriter(log_file));
						out.write("");
						// now we can make the writer that we're actually going to be using for this file
						out = new BufferedWriter(new FileWriter(log_file, true));
						iterations = 0;
					} // ...then we're finally done!
					else {
						sender.sendMessage(ChatColor.YELLOW + "Es todo. All done.");
						if (sender instanceof Player)
							myGuardDog.console.sendMessage(ChatColor.YELLOW + sender.getName() + "'s roll back is done.");
						return;
					}
				}
				out.write(updated_events.get(log_file).get(iterations));
				out.newLine();
				iterations++;
			}
		} catch (IOException exception) {
			sender.sendMessage(ChatColor.DARK_RED + "Great...IOException while rewriting the log files for a rollback...");
			exception.printStackTrace();
			return;
		}
		myGuardDog.server.getScheduler().scheduleSyncDelayedTask(myGuardDog.mGD, this, 1);
	}
}
