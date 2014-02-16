package REALDrummer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;

@SuppressWarnings("unused")
public class myGuardDog$1 implements Runnable, Listener {
    CommandSender sender = null;
    String method = null;
    Object[] os;
    boolean first_iteration = true;

    // for trackTNT()
    private Entity new_primed_TNT = null;
    // // for all the rollBack[...]() methods
    // private int iterations = 0;
    // private String[] parameters = null;
    // // for rollBackPartIV-V()
    // private File log_file = null;
    // // for rollBackPartI()
    // private BufferedReader in = null;
    // private int log_counter = 0;
    private int radius = -1;
    private ArrayList<String> causes = new ArrayList<String>(), actions = new ArrayList<String>(), objects = new ArrayList<String>();
    private ArrayList<File> relevant_log_files = new ArrayList<File>();
    private Location origin = null;
    // // part 1 of the roll back events is just put straight into roll_back_events
    // private ArrayList<Event> roll_back_events_part2 = new ArrayList<Event>(), roll_back_events_part3 = new ArrayList<Event>(),
    // roll_back_events_part4 = new ArrayList<Event>();
    // // for rollBackPart[III-V]()
    // private int events_located_so_far = 0;
    // private ArrayList<String> events_for_this_file = new ArrayList<String>();
    // private boolean this_file_has_roll_back_events = false;
    // // for rollBackPart[III-VI]()
    // private HashMap<File, ArrayList<String>> updated_events = new HashMap<File, ArrayList<String>>();
    // // for rollBackPartVI()
    // private BufferedWriter out = null;
    // // for all rollBackPart[#]() methods
    // private ArrayList<Event> roll_back_events = new ArrayList<Event>();

    // for newRollBackPartIReadLogs() method
    private RandomAccessFile raf_in;
    private boolean track_causes = false;
    private String[] time_bounds = new String[2];
    private ArrayList<Integer> xs = new ArrayList<Integer>();
    private HashMap<Event, Integer> roll_back_events = new HashMap<Event, Integer>();

    // TODO: change the "roll_back_events[...]" to a single HashMap<Event event, Integer priority>

    // object constructor
    public myGuardDog$1(CommandSender my_sender, String my_method, Object... my_objects) {
        sender = my_sender;
        method = my_method;
        os = my_objects;
    }

    // the run() operator
    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        try {
            myGuardDog.debug("thread run \"" + method + "\"");
            if (method.equals("register as listener"))
                myGuardDog.server.getPluginManager().registerEvents(this, myGuardDog.mGD);
            else if (method.equals("retry save"))
                myGuardDog.saveEvent((Event) os[0]);
            else if (method.equals("retry inspection"))
                myGuardDog.inspect((Player) sender, (Location) os[0]);
            else if (method.equals("track T.N.T."))
                // the os for this are never changed through the course of this method, so we don't need to make initializer variables for them
                trackTNT((Location) os[0], (String) os[1]);
            else if (method.equals("track reaction breaks"))
                trackReactionBreaks((Event) os[0], (ArrayList<BlockState>) os[1]);
            else if (method.equals("track Enderman placements"))
                trackEndermanPlacements((Block) os[0]);
            else if (method.equals("roll back")) {
                first_iteration = true;
                relevant_log_files = (ArrayList<File>) os[0];
                causes = (ArrayList<String>) os[1];
                actions = (ArrayList<String>) os[2];
                objects = (ArrayList<String>) os[3];
                radius = (Integer) os[4];
                newRollBackPartIReadLogs();
            } else if (method.equals("rollBackPartIReadLogs"))
                newRollBackPartIReadLogs();
            else
                sender.sendMessage(ChatColor.DARK_RED + "What the hell is \"" + method + "\"? Recheck your method input for this TimedMethod.");
        } catch (Exception e) {
            myPluginUtils.processException(myGuardDog.mGD, "There was a malfunction while running the thread \"" + method + "\".", e);
        }
    }

    // a special listener
    @EventHandler
    public void listenForMyPluginWiki(PluginEnableEvent event) {
        if (!event.getPlugin().getName().equals("myPluginWiki"))
            myGuardDog
                    .tellOps(
                            myGuardDog.COLOR
                                    + "It would be more efficient and advantageous tactically if you were to add myPluginWiki to your server now while you are managing your plugin configuration. Your server will be insecure until myPluginWiki is put on your server and enabled!",
                            true);
        else {
            myGuardDog.tellOps(myGuardDog.COLOR + "Men, our tech has arrived! Man your stations and prepare to battle griefers!", true);
            myGuardDog.mGD.getPluginLoader().enablePlugin(myGuardDog.mGD);
        }
    }

    // timed methods
    private void trackTNT(Location location, String cause) {
        for (Entity entity : location.getWorld().getEntities())
            // I chose 2 because the square root of 2 is the largest possible distance away from the block's location within a 1m radius
            if (entity.getType() == EntityType.PRIMED_TNT && entity.getLocation().distanceSquared(location) < 2
                    && (new_primed_TNT == null || new_primed_TNT.getLocation().distanceSquared(location) > entity.getLocation().distanceSquared(location)))
                new_primed_TNT = entity;
        if (new_primed_TNT != null)
            myGuardDog.TNT_causes.put(new_primed_TNT.getUniqueId(), cause);
    }

    private void trackEndermanPlacements(Block block) {
        myGuardDog.saveEvent(new Event("an Enderman", "placed", block, null));
    }

    @SuppressWarnings("deprecation")
    private void trackReactionBreaks(Event original_break, ArrayList<BlockState> checks) {
        // unbroken blocks can't be removed from checks inside the loop that uses checks, so we need to track them now and remove them afterward
        ArrayList<BlockState> unbroken_blocks = new ArrayList<BlockState>();
        for (BlockState state : checks)
            if (state.getTypeId() == state.getBlock().getTypeId())
                unbroken_blocks.add(state);
        // remove blocks that weren't broken
        for (BlockState state : unbroken_blocks)
            if (state.getTypeId() == state.getBlock().getTypeId())
                checks.remove(state);
        // make sure that no locked blocks were broken illegally in the reaction and remove any blocks that weren't broken from checks
        for (BlockState state : checks)
            // if the player who caused the first break isn't allowed to break another block broken in the reaction, undo all events
            if (state.getTypeId() != state.getBlock().getTypeId()
                    && (myGuardDog.server.getPlayerExact(original_break.cause) == null || !myGuardDog.isPermittedToUnlock(myGuardDog.server
                            .getPlayerExact(original_break.cause), state.getBlock()))) {
                myGuardDog.debug("reaction broke locked block; cancelling event");
                // remove all the drops; the entities must be tracked by their unique I.D.s because when you use Entity.remove(), it doesn't work immediately,
                // so you must make sure that you don't remove the same entity multiple times by keeping track of the entities you remove by unique I.D.
                ArrayList<UUID> removed_entities = new ArrayList<UUID>();
                for (BlockState my_state : checks)
                    for (Entity entity : my_state.getWorld().getEntities())
                        if (entity.getType() == EntityType.DROPPED_ITEM && !removed_entities.contains(entity.getUniqueId())
                                && entity.getLocation().getBlockX() <= my_state.getX() + 1 && entity.getLocation().getBlockX() >= my_state.getX() - 1
                                && entity.getLocation().getBlockY() <= my_state.getY() + 1 && entity.getLocation().getBlockY() >= my_state.getY() - 1
                                && entity.getLocation().getBlockZ() <= my_state.getZ() + 1 && entity.getLocation().getBlockZ() >= my_state.getZ() - 1) {
                            removed_entities.add(entity.getUniqueId());
                            entity.remove();
                            myGuardDog.debug("removed drop");
                            break;
                        }
                // undo the original break before the reaction breaks (because, of course, if you undid one of the reaction breaks before the original break,
                // the reaction break would just happen again)
                BlockState original_break_state = null;
                for (BlockState my_state : checks)
                    if (my_state.getLocation().equals(original_break.location)) {
                        my_state.getBlock().setTypeId(my_state.getTypeId());
                        my_state.getBlock().setData(my_state.getData().getData());
                        original_break_state = my_state;
                        break;
                    }
                // remove the original break state from checks because it has already been undone
                if (original_break_state != null)
                    checks.remove(original_break_state);
                else
                    myGuardDog.tellOps(ChatColor.DARK_RED
                            + "My troops were unable to identify the original event in a reaction break and cancel it properly. Please inform General REALDrummer.", true);
                // undo the reaction breaks
                for (BlockState my_state : checks) {
                    my_state.getBlock().setTypeId(my_state.getTypeId());
                    my_state.getBlock().setData(my_state.getData().getData());
                    // if the reaction break was a door, undo the breaking of the top half as well
                    if ((my_state.getType() == Material.WOODEN_DOOR || my_state.getType() == Material.IRON_DOOR_BLOCK) && my_state.getData().getData() < 8) {
                        my_state.getBlock().getRelative(BlockFace.UP).setTypeId(my_state.getTypeId());
                        my_state.getBlock().getRelative(BlockFace.UP).setData(myPluginUtils.getDoorTopData(my_state.getBlock()));
                    }
                }
                // inform the player of the cancellation
                myGuardDog.server.getPlayerExact(original_break.cause).sendMessage(
                        ChatColor.RED + "The " + myPluginWiki.getItemName(state.getBlock(), false, true, true) + " attached to the block you tried to break is locked by "
                                + myGuardDog.locked_blocks.get(state.getBlock()) + " and you're not allowed to break it.");
                return;
            }
        // save the original event
        // myGuardDog.saveEvent(original_break);
        // save the reaction break events using all the checks left; remember that checked BlockStates that did not change were already removed from checks
        for (BlockState state : checks) {
            myGuardDog.debug("tracked reaction break:");
            myGuardDog.saveEvent(new Event(original_break.cause, "broke", myPluginWiki.getItemName(state.getTypeId(), state.getData().getData(), true, true, false), state
                    .getLocation(), original_break.in_Creative_Mode));
            myGuardDog.unlock(state.getBlock(), myGuardDog.server.getPlayerExact(original_break.cause), myPluginWiki.getItemName(state.getTypeId(), state.getData().getData(),
                    false, true, true));
        }
    }

    private void newRollBackPartIReadLogs() {
        method = "rollBackPartIReadLogs";
        try {
            if (first_iteration) {
                first_iteration = false;
                // get the origin to compare to the search radius if sender is a Player
                if (sender instanceof Player)
                    origin = ((Player) sender).getLocation();
                // if causes were not declared, set track_causes to true to tell this method to keep track of all the events' causes through this process to
                // make rewriting the cause logs more efficient later
                if (causes.size() == 0)
                    track_causes = true;
                // establish the first RandomAccessFile reader
                raf_in = new RandomAccessFile(relevant_log_files.get(0), "rw");
            }
            // if causes are not established,

        } catch (IOException exception) {
            myPluginUtils.processException(myGuardDog.mGD, "There was an IOException in part I of the roll back!", exception);
            myGuardDog.roll_back_in_progress = false;
            return;
        }
    }

    // @Deprecated
    // private void rollBackPartIReadLogs() {
    // method = "rollBackPartIReadLogs";
    // if (first_iteration) {
    // first_iteration = false;
    // sender.sendMessage(myGuardDog.COLOR + "All right. Let me retrieve all the events within your search parameters. This may take a minute.");
    // // read all the parameters
    // radius = -1;
    // for (int i = 0; i < parameters.length; i++) {
    // if (!(sender instanceof Player)
    // && (parameters[i].toLowerCase().startsWith("r:") || parameters[i].toLowerCase().startsWith("rad:") || parameters[i].toLowerCase()
    // .startsWith("radius:"))) {
    // sender.sendMessage(ChatColor.RED + "How can you specify an area around you to roll back? You have no position! You're a console!");
    // myGuardDog.roll_back_in_progress = false;
    // return;
    // }
    // if (parameters[i].toLowerCase().startsWith("by:")) {
    // if (parameters[i].length() == 3) {
    // sender.sendMessage(ChatColor.RED + "You forgot to list what causes you want me to roll back!");
    // myGuardDog.roll_back_in_progress = false;
    // return;
    // }
    // String[] my_causes = parameters[i].substring(3).split(",");
    // for (String search : my_causes) {
    // // autocomplete the names of the causes
    // String autocompleted = null;
    // if (search.contains("\\")) {
    // search = search.replaceAll("\\\\", "");
    // // check through the possible non-player event causes
    // for (String non_player_cause : new String[] { "a creeper", "a Ghast", "the Ender Dragon", "an Enderman", "some lava", "some lightning",
    // "a fireball", "something", "T.N.T.", "myGroundsKeeper" }) {
    // String without_article = non_player_cause;
    // if (non_player_cause.split(" ").length == 2)
    // without_article = non_player_cause.split(" ")[1];
    // else if (non_player_cause.split(" ").length == 3)
    // // The Ender Dragon is the only thing listed that has more than one space
    // without_article = "Ender Dragon";
    // if (without_article.toLowerCase().contains(search.toLowerCase())
    // && (autocompleted == null || non_player_cause.indexOf(search) < autocompleted.indexOf(search)))
    // autocompleted = non_player_cause;
    // }
    // } else
    // autocompleted = myPluginUtils.getFullName(search);
    // if (autocompleted == null) {
    // sender.sendMessage(ChatColor.RED + "Who's \"" + search + "\"?");
    // sender.sendMessage(ChatColor.RED
    // +
    // "If you want to search for a non-player cause like creepers, you need to put a \"\\\" in the object's name somewhere so I know not to look for a player.");
    // myGuardDog.roll_back_in_progress = false;
    // return;
    // } else
    // causes.add(autocompleted);
    // }
    // } else if (parameters[i].toLowerCase().startsWith("a:")) {
    // if (parameters[i].length() == 2) {
    // sender.sendMessage(ChatColor.RED + "You forgot to list what actions you want me to roll back!");
    // myGuardDog.roll_back_in_progress = false;
    // return;
    // }
    // for (String action : parameters[i].substring(2).split(","))
    // actions.add(action);
    // } else if (parameters[i].toLowerCase().startsWith("action:")) {
    // if (parameters[i].length() == 7) {
    // sender.sendMessage(ChatColor.RED + "You forgot to list what actions you want me to roll back!");
    // myGuardDog.roll_back_in_progress = false;
    // return;
    // }
    // for (String action : parameters[i].substring(7).split(","))
    // actions.add(action);
    // } else if (parameters[i].toLowerCase().startsWith("actions:")) {
    // if (parameters[i].length() == 8) {
    // sender.sendMessage(ChatColor.RED + "You forgot to list what actions you want me to roll back!");
    // myGuardDog.roll_back_in_progress = false;
    // return;
    // }
    // for (String action : parameters[i].substring(8).split(","))
    // actions.add(action);
    // } else if (parameters[i].toLowerCase().startsWith("o:")) {
    // if (parameters[i].length() == 2) {
    // sender.sendMessage(ChatColor.RED + "You forgot to list what objects you want me to roll back!");
    // myGuardDog.roll_back_in_progress = false;
    // return;
    // }
    // for (String object : parameters[i].substring(2).split(","))
    // objects.add(object);
    // } else if (parameters[i].toLowerCase().startsWith("object:")) {
    // if (parameters[i].length() == 2) {
    // sender.sendMessage(ChatColor.RED + "You forgot to list what objects you want me to roll back!");
    // myGuardDog.roll_back_in_progress = false;
    // return;
    // }
    // for (String object : parameters[i].substring(7).split(","))
    // objects.add(object);
    // } else if (parameters[i].toLowerCase().startsWith("objects:")) {
    // if (parameters[i].length() == 2) {
    // sender.sendMessage(ChatColor.RED + "You forgot to list what objects you want me to roll back!");
    // myGuardDog.roll_back_in_progress = false;
    // return;
    // }
    // for (String object : parameters[i].substring(8).split(","))
    // objects.add(object);
    // } else if (parameters[i].toLowerCase().startsWith("r:") || parameters[i].toLowerCase().startsWith("rad:") ||
    // parameters[i].toLowerCase().startsWith("radius:"))
    // try {
    // if (parameters[i].toLowerCase().startsWith("r:")) {
    // if (parameters[i].length() == 2) {
    // sender.sendMessage(ChatColor.RED + "You forgot to tell me the radius of the area you want me to roll back!");
    // myGuardDog.roll_back_in_progress = false;
    // return;
    // }
    // radius = Integer.parseInt(parameters[i].substring(2));
    // } else if (parameters[i].toLowerCase().startsWith("rad:")) {
    // if (parameters[i].length() == 4) {
    // sender.sendMessage(ChatColor.RED + "You forgot to tell me the radius of the area you want me to roll back!");
    // myGuardDog.roll_back_in_progress = false;
    // return;
    // }
    // radius = Integer.parseInt(parameters[i].substring(4));
    // } else if (parameters[i].toLowerCase().startsWith("radius:")) {
    // if (parameters[i].length() == 7) {
    // sender.sendMessage(ChatColor.RED + "You forgot to tell me the radius of the area you want me to roll back!");
    // myGuardDog.roll_back_in_progress = false;
    // return;
    // }
    // radius = Integer.parseInt(parameters[i].substring(7));
    // }
    // if (radius < 0) {
    // sender.sendMessage(ChatColor.RED + "A negative radius...right...okay, um...can I get a positive radius, please? Thanks.");
    // myGuardDog.roll_back_in_progress = false;
    // return;
    // }
    // } catch (NumberFormatException exception) {
    // sender.sendMessage(ChatColor.RED + "Oh, yeah. \"" + parameters[i].substring(2)
    // + "\" comes right after 3, right? Oh, wait. No it doesn't. In fact, it's not an integer at all. Try again.");
    // myGuardDog.roll_back_in_progress = false;
    // return;
    // }
    // else {
    // sender.sendMessage(ChatColor.RED + "I'm not sure that I understand what \"" + parameters[i] + "\" means.");
    // myGuardDog.roll_back_in_progress = false;
    // return;
    // }
    // }
    // // find all the relevant log files
    // if (causes.size() > 0)
    // for (String cause : causes) {
    // // find all the relevant log files
    // // start by looking for a file with no numbers (which occur when there are few enough events to write them all in one log file)
    // File log_file = new File(myGuardDog.cause_logs_folder, cause + ".mGDlog");
    // if (log_file.exists())
    // relevant_log_files.add(log_file);
    // else {
    // // if the non-numbered file doesn't exist, look for a bunch of numbered files
    // int counter = 1;
    // log_file = new File(myGuardDog.cause_logs_folder, cause + " (1).mGDlog");
    // while (log_file.exists()) {
    // relevant_log_files.add(log_file);
    // counter++;
    // log_file = new File(myGuardDog.cause_logs_folder, cause + " (" + counter + ").mGDlog");
    // }
    // }
    // }
    // // if a cause wasn't declared, just get all the chronological logs
    // else
    // for (File log_file : myGuardDog.chrono_logs_folder.listFiles())
    // relevant_log_files.add(log_file);
    // if (relevant_log_files.size() == 0) {
    // sender.sendMessage(ChatColor.RED + "No events have occurred that fit your parameters!");
    // myGuardDog.roll_back_in_progress = false;
    // return;
    // }
    // // read and gather all the relevant events from the log files, allowing a 100ms space between every 50 events
    // if (sender instanceof Player)
    // origin = ((Player) sender).getLocation();
    // else
    // origin = null;
    // try {
    // in = new BufferedReader(new FileReader(relevant_log_files.get(0)));
    // } catch (FileNotFoundException exception) {
    // myPluginUtils.processException(myGuardDog.mGD, "I couldn't find the first relevant log file!", exception);
    // myGuardDog.roll_back_in_progress = false;
    // return;
    // }
    // }
    // try {
    // String save_line;
    // for (int i = 0; i < 100; i++) {
    // save_line = in.readLine();
    // if (save_line == null) {
    // in.close();
    // // if we're not done, move on to the next relevant log file
    // if (log_counter < relevant_log_files.size() - 1) {
    // log_counter++;
    // in = new BufferedReader(new FileReader(relevant_log_files.get(log_counter)));
    // save_line = in.readLine();
    // } // if we are done, go to part II of the roll back process
    // else {
    // first_iteration = true;
    // rollBackPartIIChangeWorld();
    // return;
    // }
    // }
    // Event event = new Event(save_line);
    // boolean causes_satisfied = false, actions_satisfied = false, objects_satisfied = false, radius_satisfied = false;
    // // check the causes
    // if (causes.size() == 0 || causes.contains(event.cause))
    // causes_satisfied = true;
    // // check the actions
    // if (actions.size() == 0)
    // actions_satisfied = true;
    // else
    // for (String action : actions)
    // if (event.action.replaceAll(" ", "").contains(action)) {
    // actions_satisfied = true;
    // break;
    // }
    // // check the objects
    // if (objects.size() == 0)
    // objects_satisfied = true;
    // else
    // for (String object : objects)
    // for (String event_object : event.objects) {
    // Integer event_id = myPluginWiki.getItemIdAndData(event_object, null)[0], object_id = myPluginWiki.getItemIdAndData(object, null)[0];
    // if (event_id == null && object_id == null) {
    // event_id = myPluginWiki.getEntityIdAndData(event_object)[0];
    // object_id = myPluginWiki.getEntityIdAndData(object)[0];
    // }
    // if (event_id != null && event_id == object_id) {
    // objects_satisfied = true;
    // break;
    // }
    // }
    // // check the radius
    // if (radius == -1)
    // radius_satisfied = true;
    // else if (origin != null && origin.getX() - radius <= event.x && origin.getX() + radius >= event.x && origin.getY() - radius <= event.y
    // && origin.getY() + radius >= event.y && origin.getZ() - radius <= event.z && origin.getZ() + radius >= event.z
    // && origin.getWorld().equals(event.world))
    // radius_satisfied = true;
    // // set this event up for rollback
    // if (causes_satisfied && actions_satisfied && objects_satisfied && radius_satisfied && !event.rolled_back && event.canBeRolledBack()) {
    // // ORDER: remove all liquids (part 1), then remove all blocks that must be attached to something (part 1), then roll back solid
    // // blocks with sand and gravel organized from bottom to top for replacement (part 2) and top to bottom for removal (part 3), then
    // // replace blocks that must be attached to something (part 4), then replace liquids (part 4)
    // Boolean must_be_attached = myPluginWiki.mustBeAttached(event.objects[0], null);
    // if (must_be_attached == null)
    // myGuardDog.tellOps(ChatColor.RED + "I couldn't find the block that goes with \"" + event.objects[0] + "\"!", true);
    // else if (event.isPlacement() && event.objects != null && (event.objects[0].equals("lava") || event.objects[0].equals("water")))
    // roll_back_events.add(0, event);
    // else if (event.isPlacement() && event.objects != null && must_be_attached)
    // roll_back_events.add(event);
    // else if (event.isRemoval() && event.objects != null && !must_be_attached && !(event.objects[0].equals("lava") || event.objects[0].equals("water"))) {
    // boolean added = false;
    // // sort from bottom to top
    // for (int j = 0; j < roll_back_events_part2.size(); j++)
    // if (roll_back_events_part2.get(j).y >= event.y) {
    // roll_back_events_part2.add(j, event);
    // added = true;
    // break;
    // }
    // // if it's still not added, add it to the end
    // if (!added)
    // roll_back_events_part2.add(event);
    // } else if (event.isPlacement() && event.objects != null && !must_be_attached && !(event.objects[0].equals("lava") || event.objects[0].equals("water"))) {
    // boolean added = false;
    // // sort from top to bottom
    // for (int j = 0; j < roll_back_events_part3.size(); j++)
    // if (roll_back_events_part3.get(j).y <= event.y) {
    // roll_back_events_part3.add(j, event);
    // added = true;
    // break;
    // } // if it's still not added, add it to the end
    // if (!added)
    // roll_back_events_part3.add(event);
    // } else if (event.isRemoval() && event.objects != null && must_be_attached)
    // roll_back_events_part4.add(0, event);
    // else if (event.isRemoval() && event.objects != null && (event.objects[0].equals("lava") || event.objects[0].equals("water")))
    // roll_back_events_part4.add(event);
    // else if (!event.action.equals("killed")) {
    // myGuardDog.debug(ChatColor.DARK_RED
    // + "Hey. There was this weird event. I didn't know where to insert it in the ordered roll back, so I put it at the end.");
    // myGuardDog.debug(ChatColor.WHITE + event.save_line);
    // roll_back_events_part4.add(event);
    // }
    // }
    // }
    // } catch (IOException exception) {
    // myPluginUtils.processException(myGuardDog.mGD, "Great...IOException while reading the log files for a rollback...", exception);
    // myGuardDog.roll_back_in_progress = false;
    // return;
    // }
    // // set up the next event
    // myGuardDog.server.getScheduler().scheduleSyncDelayedTask(myGuardDog.mGD, this, 1);
    // }
    //
    // @Deprecated
    // private void rollBackPartIIChangeWorld() {
    // method = "rollBackPartIIChangeWorld";
    // if (myGuardDog.roll_back_in_progress)
    // myGuardDog.roll_back_in_progress = false;
    // if (first_iteration) {
    // first_iteration = false;
    // iterations = 0;
    // // compile the roll_back_events list
    // for (Event event : roll_back_events_part2)
    // roll_back_events.add(event);
    // for (Event event : roll_back_events_part3)
    // roll_back_events.add(event);
    // for (Event event : roll_back_events_part4)
    // roll_back_events.add(event);
    // if (roll_back_events.size() == 0) {
    // sender.sendMessage(ChatColor.RED + "I don't think anything has happened yet that matches those criteria.");
    // myGuardDog.roll_back_in_progress = false;
    // return;
    // } else if (roll_back_events.size() == 1)
    // sender.sendMessage(myGuardDog.COLOR + "I only found one event that matches your criteria. This ought to be easy.");
    // // at 50ms per event rolled back, I calculate it would take 100 events 5 seconds to be rolled back. If it's going to take less than five
    // // seconds, don't bother with giving an estimated time of completion
    // else if (roll_back_events.size() < 100)
    // sender.sendMessage(myGuardDog.COLOR + "I found " + roll_back_events.size() +
    // " events that fit your criteria. This will take no time at all. Rolling back...");
    // else
    // // give the command sender an estimated time of completion if there are more than 100 to roll back (it will take 50ms to roll back
    // // one event)
    // sender.sendMessage(myGuardDog.COLOR + "I found " + roll_back_events.size() + " events that fit your criteria. This will take about "
    // + myPluginUtils.translateTimeInmsToString(roll_back_events.size() * 50, true) + " to roll back. Here we go.");
    // }
    // Event event = roll_back_events.get(iterations);
    // // this part restores mobs (but only non-hostile animals, of course)
    // if (event.action.equals("killed")) {
    // Integer[] id_and_data = myPluginWiki.getEntityIdAndData(event.objects[0]);
    // EntityType entity = null;
    // if (id_and_data == null)
    // sender.sendMessage(ChatColor.DARK_RED + "What the heck is \"" + event.objects[0] + "\"?");
    // else {
    // entity = EntityType.fromId(id_and_data[0]);
    // if (entity == null)
    // sender.sendMessage(ChatColor.DARK_RED + "What the heck is \"" + event.objects[0] + "\"?");
    // // ids from 90-120 are non-hostile mobs (except bats, which are 65)
    // else if (entity.isSpawnable() && (entity == EntityType.BAT || (id_and_data[0] >= 90 && id_and_data[0] <= 120))) {
    // Entity new_entity = event.world.spawnEntity(event.location, entity);
    // // if the new entity is a villager, be sure to give them the correct profession
    // if (entity == EntityType.VILLAGER && id_and_data[1] > 0)
    // ((Villager) new_entity).setProfession(Villager.Profession.getProfession(id_and_data[1]));
    // }
    // }
    // } // this part restores broken or destroyed blocks
    // else if (event.action.equals("broke") || event.action.equals("burned") || event.action.equals("creeper'd") || event.action.equals("T.N.T.'d")
    // || event.action.equals("blew up")) {
    // Integer[] id_and_data = myPluginWiki.getItemIdAndData(event.objects[0], false);
    // if (id_and_data != null) {
    // event.location.getBlock().setTypeId(id_and_data[0]);
    // // to convert an Integer to a Byte, I need to convert the Integer to a String, then to a Byte
    // if (id_and_data[1] > 0)
    // event.location.getBlock().setData(Byte.valueOf(String.valueOf(id_and_data[1])));
    // } else {
    // sender.sendMessage(ChatColor.RED + "I couldn't find the item I.D. of the object in this event!");
    // sender.sendMessage(ChatColor.RED + "The item was called \"" + event.objects[0] + "\".");
    // sender.sendMessage(ChatColor.RED + "The save line said \"" + ChatColor.WHITE + event.save_line + ChatColor.RED + "\".");
    // }
    // } // this part removes placed blocks
    // else if (event.action.equals("placed") || event.action.equals("grew"))
    // event.location.getBlock().setTypeId(0);
    // iterations++;
    // if (iterations > roll_back_events.size() - 1) {
    // if (roll_back_events.size() > 100)
    // sender.sendMessage(myGuardDog.COLOR + "Almost done. I just need to update the log files now.");
    // first_iteration = true;
    // rollBackPartIIIChrono();
    // return;
    // } // I chose 80 events for the cutoff because these messages are supposed to appear every quarter of the way through. 80 events means each
    // // quarter happens after 20 events, which means that there will be at least one second in between each progress message
    // else if (roll_back_events.size() > 80)
    // if (iterations / roll_back_events.size() >= 0.75 && (iterations - 1) / roll_back_events.size() < 0.75)
    // // we're 75% done
    // sender.sendMessage(myGuardDog.COLOR + "Almost there...");
    // else if (iterations / roll_back_events.size() >= 0.5 && (iterations - 1) / roll_back_events.size() < 0.5)
    // // we're 50% done
    // sender.sendMessage("I'm about halfway done with that roll back.");
    // else if (iterations / roll_back_events.size() >= 0.25 && (iterations - 1) / roll_back_events.size() < 0.25)
    // // we're 25% done
    // sender.sendMessage("Making progress...");
    // // schedule the next event
    // myGuardDog.server.getScheduler().scheduleSyncDelayedTask(myGuardDog.mGD, this, 1);
    // }
    //
    // @Deprecated
    // private void rollBackPartIIIChrono() {
    // method = "rollBackPartIIIChrono";
    // if (myGuardDog.roll_back_in_progress)
    // myGuardDog.roll_back_in_progress = false;
    // try {
    // if (first_iteration) {
    // // if it's the first iteration, make sure all the variables needed start out blank
    // relevant_log_files = new ArrayList<File>();
    // events_for_this_file = new ArrayList<String>();
    // log_counter = 0;
    // events_located_so_far = 0;
    // this_file_has_roll_back_events = false;
    // in = new BufferedReader(new FileReader(myGuardDog.chrono_logs_folder.listFiles()[log_counter]));
    // first_iteration = false;
    // }
    // String save_line;
    // // it needs to search through roll_back_events for every item on the logs, so it makes sense that it would read less if there are more roll back
    // // events since it needs more time to comapre each save line to the roll back events
    // // the +1 ensures that it reads at least one event at every iteration
    // for (int i = 0; i < 100; i++) {
    // save_line = in.readLine();
    // if (save_line == null) {
    // if (this_file_has_roll_back_events)
    // updated_events.put(myGuardDog.chrono_logs_folder.listFiles()[log_counter], events_for_this_file);
    // this_file_has_roll_back_events = false;
    // events_for_this_file = new ArrayList<String>();
    // log_counter++;
    // in.close();
    // if (events_located_so_far == roll_back_events.size()) {
    // in.close();
    // first_iteration = true;
    // if (roll_back_events.size() > 100)
    // sender.sendMessage(myGuardDog.COLOR + "Making progress...");
    // rollBackPartIVPos();
    // return;
    // }
    // try {
    // in = new BufferedReader(new FileReader(myGuardDog.chrono_logs_folder.listFiles()[log_counter]));
    // } catch (ArrayIndexOutOfBoundsException exception) {
    // // this is for if we somehow run out of log files to check without finding all the roll back events
    // myGuardDog.tellOps(ChatColor.DARK_RED + "I couldn't locate all the rolled back events in the chronological logs!", true);
    // in.close();
    // if (roll_back_events.size() > 100)
    // sender.sendMessage(myGuardDog.COLOR + "Making progress...");
    // first_iteration = true;
    // rollBackPartIVPos();
    // return;
    // }
    // }
    // // if it's one of the events that was rolled back, add "[rolled back]" to the end of the save line and record the event
    // for (Event event : roll_back_events)
    // if (event.save_line.equals(save_line)) {
    // events_located_so_far++;
    // this_file_has_roll_back_events = true;
    // save_line = save_line + " [rolled back]";
    // break;
    // }
    // events_for_this_file.add(save_line);
    // }
    // } catch (IOException exception) {
    // myPluginUtils.processException(myGuardDog.mGD, "Great...IOException while updating the chronological log files for a rollback...", exception);
    // myGuardDog.roll_back_in_progress = false;
    // return;
    // }
    // myGuardDog.server.getScheduler().scheduleSyncDelayedTask(myGuardDog.mGD, this, 1);
    // }
    //
    // @Deprecated
    // private void rollBackPartIVPos() {
    // method = "rollBackPartIVPos";
    // if (myGuardDog.roll_back_in_progress)
    // myGuardDog.roll_back_in_progress = false;
    // try {
    // if (first_iteration) {
    // // if it's the first iteration, make sure all the variables needed start out blank
    // this_file_has_roll_back_events = false;
    // events_located_so_far = 0;
    // log_file =
    // new File(myGuardDog.position_logs_folder, "x = " + roll_back_events.get(0).x + " " + roll_back_events.get(0).world.getWorldFolder().getName()
    // + ".mGDlog");
    // if (!log_file.exists())
    // log_file.createNewFile();
    // in = new BufferedReader(new FileReader(log_file));
    // iterations = 0;
    // first_iteration = false;
    // }
    // String save_line = null;
    // for (int i = 50 * iterations; i < 50 * (iterations + 1); i++) {
    // // if we've reached the end of the roll back events, move on to the next step
    // if (i == roll_back_events.size()) {
    // in.close();
    // myGuardDog.tellOps(ChatColor.DARK_RED + "I couldn't find all the roll back events in the position logs!", true);
    // if (this_file_has_roll_back_events)
    // updated_events.put(log_file, events_for_this_file);
    // first_iteration = true;
    // if (roll_back_events.size() > 100)
    // sender.sendMessage(myGuardDog.COLOR + "Almost there....");
    // rollBackPartVRewriteLogs();
    // return;
    // }
    // if (save_line == null) {
    // if (this_file_has_roll_back_events)
    // updated_events.put(log_file, events_for_this_file);
    // events_for_this_file = new ArrayList<String>();
    // log_file =
    // new File(myGuardDog.position_logs_folder, "x = " + roll_back_events.get(i).x + " " + roll_back_events.get(i).world.getWorldFolder().getName()
    // + ".mGDlog");
    // // when this method gets a position log file, it updates ALL the events in that file as rolled back that were rolled back, not just the one
    // // that was used to find that file. Therefore, we need to search through the list until we find an event with a location that coordinates
    // // with a position log file that has not been updated yet.
    // while (updated_events.containsKey(log_file)) {
    // i++;
    // // if we've been through all of the roll_back_events, move on to the next part of the roll back
    // if (i == roll_back_events.size()) {
    // myGuardDog.tellOps(ChatColor.DARK_RED + "I couldn't find all the roll back events in the position logs!", true);
    // if (this_file_has_roll_back_events)
    // updated_events.put(log_file, events_for_this_file);
    // in.close();
    // first_iteration = true;
    // if (roll_back_events.size() > 100)
    // sender.sendMessage(myGuardDog.COLOR + "Almost there....");
    // rollBackPartVRewriteLogs();
    // return;
    // } // if we reach the end of the portion to read for this iteration, break this loop and the larger for loop
    // else if (i >= 50 * (iterations + 1))
    // break;
    // else
    // log_file =
    // new File(myGuardDog.position_logs_folder, "x = " + roll_back_events.get(i).x + " "
    // + roll_back_events.get(i).world.getWorldFolder().getName() + ".mGDlog");
    // }
    // if (i >= 50 * (iterations + 1))
    // break;
    // in.close();
    // in = new BufferedReader(new FileReader(log_file));
    // save_line = in.readLine();
    // }
    // for (Event event : roll_back_events)
    // if (event.save_line.equals(save_line)) {
    // events_located_so_far++;
    // this_file_has_roll_back_events = true;
    // save_line = save_line + " [rolled back]";
    // break;
    // }
    // events_for_this_file.add(save_line);
    // if (events_located_so_far == roll_back_events.size()) {
    // if (this_file_has_roll_back_events)
    // updated_events.put(log_file, events_for_this_file);
    // in.close();
    // first_iteration = true;
    // if (roll_back_events.size() > 100)
    // sender.sendMessage(myGuardDog.COLOR + "Almost there....");
    // rollBackPartVRewriteLogs();
    // return;
    // }
    // save_line = in.readLine();
    // }
    // } catch (IOException exception) {
    // myPluginUtils.processException(myGuardDog.mGD, "Great...IOException while updating the position log files for a rollback...", exception);
    // myGuardDog.roll_back_in_progress = false;
    // return;
    // }
    // iterations++;
    // myGuardDog.server.getScheduler().scheduleSyncDelayedTask(myGuardDog.mGD, this, 1);
    // }
    //
    // @Deprecated
    // private void rollBackPartVRewriteLogs() {
    // method = "rollBackPartVIRewriteLogs";
    // if (myGuardDog.roll_back_in_progress)
    // myGuardDog.roll_back_in_progress = false;
    // try {
    // if (first_iteration) {
    // log_counter = 0;
    // log_file = (File) updated_events.keySet().toArray()[0];
    // if (!log_file.exists())
    // log_file.createNewFile();
    // // the purpose of this next segment is to delete the contents of the existing file; apparently deleting the file and creating a new one
    // // doesn't do the trick
    // out = new BufferedWriter(new FileWriter(log_file));
    // out.write("");
    // // now we can make the writer we're actually going to be using for this file
    // out = new BufferedWriter(new FileWriter(log_file, true));
    // iterations = 0;
    // first_iteration = false;
    // }
    // for (int i = 0; i < 100; i++) {
    // // if all the events for this file have been written,...
    // if (iterations == updated_events.get(log_file).size()) {
    // out.close();
    // // ...move on to the next file in the list or if those are all done...
    // if (log_counter + 1 < updated_events.keySet().size()) {
    // log_counter++;
    // log_file = (File) updated_events.keySet().toArray()[log_counter];
    // if (!log_file.exists())
    // log_file.createNewFile();
    // // the purpose of this next segment is to delete the contents of the existing file; apparently deleting the file and creating a new one
    // // doesn't do the trick
    // out = new BufferedWriter(new FileWriter(log_file));
    // out.write("");
    // // now we can make the writer that we're actually going to be using for this file
    // out = new BufferedWriter(new FileWriter(log_file, true));
    // iterations = 0;
    // } // ...then we're finally done!
    // else {
    // sender.sendMessage(myGuardDog.COLOR + "Es todo. All done.");
    // myGuardDog.tellOps(myGuardDog.COLOR + sender.getName() + "'s roll back is done.", sender instanceof Player, sender.getName());
    // myGuardDog.roll_back_in_progress = false;
    // return;
    // }
    // }
    // out.write(updated_events.get(log_file).get(iterations));
    // out.newLine();
    // iterations++;
    // }
    // } catch (IOException exception) {
    // myPluginUtils.processException(myGuardDog.mGD, "Great...IOException while rewriting the log files for a rollback...", exception);
    // myGuardDog.roll_back_in_progress = false;
    // return;
    // }
    // myGuardDog.server.getScheduler().scheduleSyncDelayedTask(myGuardDog.mGD, this, 1);
    // }
}
