package REALDrummer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;

public class Event {
	public static final GregorianCalendar calendar = new GregorianCalendar();
	public String action, cause, save_line;
	public String[] objects;
	public byte month, day, hour, minute, second;
	public short year;
	public Location location;
	public int x, y, z;
	public World world;
	public Boolean in_Creative_Mode;
	public boolean rolled_back;

	public Event(String cause, String action, Block object, Boolean in_Creative_Mode) {
		objects = new String[] { Wiki.getItemName(object.getTypeId(), object.getData(), true, true, false) };
		initializeOtherVariables(cause, action, object.getLocation(), in_Creative_Mode);
	}

	public Event(String cause, String action, Entity object, Boolean in_Creative_Mode) {
		objects = new String[1];
		// try getting the item name with the I.D. and data provided
		if (object instanceof Villager)
			// villager = 120
			objects[0] = Wiki.getEntityName(120, (byte) ((Villager) object).getProfession().getId(), true, true);
		else
			objects[0] = Wiki.getEntityName(object.getType().getTypeId(), (byte) -1, true, true);
		initializeOtherVariables(cause, action, object.getLocation(), in_Creative_Mode);
	}

	public Event(String cause, String action, ItemStack[] items, Location location, Boolean in_Creative_Mode) {
		objects = new String[items.length];
		// derive the names of the items
		for (int i = 0; i < items.length; i++) {
			// if items[i].getAmount == 1, we should get the singular item name
			objects[i] = Wiki.getItemName(items[i].getTypeId(), items[i].getData().getData(), true, items[i].getAmount() == 1, false);
		}
		initializeOtherVariables(cause, action, location, in_Creative_Mode);
	}

	public Event(String cause, String action, String object, Location location, Boolean in_Creative_Mode) {
		objects = new String[] { object };
		initializeOtherVariables(cause, action, location, in_Creative_Mode);
	}

	// within each Event constructor, we only initialize the objects because they can be of various types; the rest of the variables for any kind of Event
	// (except one read from a save line) are initialized using this method because they are the same no matter the objects
	private void initializeOtherVariables(String my_cause, String my_action, Location my_location, Boolean my_in_Creative_Mode) {
		// an event can't be rolled back when it happens!
		rolled_back = false;
		// establish initiator variables
		action = my_action;
		cause = my_cause;
		location = my_location;
		x = location.getBlockX();
		y = location.getBlockY();
		z = location.getBlockZ();
		world = location.getWorld();
		in_Creative_Mode = my_in_Creative_Mode;
		// get the date of the event
		month = (byte) (calendar.get(2) + 1);
		day = (byte) calendar.get(5);
		year = (short) calendar.get(1);
		// get the time of the event
		String[] time = new SimpleDateFormat("HH:mm:ss").format(new Date()).split(":");
		try {
			hour = Byte.parseByte(time[0]);
			minute = Byte.parseByte(time[1]);
			second = Byte.parseByte(time[2]);
		} catch (NumberFormatException exception) {
			myGuardDog.console.sendMessage(ChatColor.RED + "Oh no! There was an issue getting the time when " + cause + " " + action + " something!");
		}
		// construct the save line
		// save line format:
		// On [month]/[day]/[year] at [hour]:[minute]:[second], [cause] [action] [objects] at ([x], [y], [z]) in
		// "[world]"(" while in ["Creative"/"Survival"] Mode").
		save_line = "On " + getDate('/') + " at " + getTime(':');
		String formatted_action = action, formatted_object = myGuardDog.arrayToList(objects);
		// special action: ...dyed a [color] sheep [new color]...
		if (action.startsWith("dyed") && objects != null && objects.length == 1) {
			formatted_action = "dyed";
			formatted_object = objects[0] + action.substring(5);
		}
		save_line += ", " + cause + " " + formatted_action + " " + formatted_object + " at (" + x + ", " + y + ", " + z + ") in \"" + world.getName() + "\"";
		if (in_Creative_Mode == null)
			save_line += ".";
		else if (in_Creative_Mode)
			save_line += " while in Creative Mode.";
		else
			save_line += " while in Survival Mode.";
		// myGuardDog.console.sendMessage(save_line);
	}

	public Event(String my_save_line) {
		save_line = my_save_line;
		// On [month]/[day]/[year] at [hour]:[minute]:[second], [cause] [action] [objects] at ([x], [y], [z]) in "[world]" while in ["Creative"/"Survival"]
		// Mode. ("[rolled back]")
		String[] temp = save_line.split("/");
		try {
			month = Byte.parseByte(temp[0].substring(3));
			day = Byte.parseByte(temp[1]);
			year = Short.parseShort(temp[2].substring(0, 4));
		} catch (NumberFormatException exception) {
			myGuardDog.console.sendMessage(ChatColor.DARK_RED + "I couldn't read the date on this event!");
		}
		temp = save_line.split(":");
		try {
			hour = Byte.parseByte(temp[0].substring(temp[0].length() - 2, temp[0].length()));
			minute = Byte.parseByte(temp[1]);
			second = Byte.parseByte(temp[2].substring(0, 2));
		} catch (NumberFormatException exception) {
			myGuardDog.console.sendMessage(ChatColor.DARK_RED + "I couldn't read the time on this event!");
		}
		temp = save_line.split(", ")[1].split(" ");
		if (!temp[0].equals("a") && !temp[0].equals("an") && !temp[0].equals("the") && !temp[0].equals("some")) {
			cause = temp[0];
			action = temp[1];
		} else if (!temp[0].equals("the")) {
			cause = temp[0] + " " + temp[1];
			action = temp[2];
		} else {
			cause = "the Ender Dragon";
			action = temp[3];
		}
		// if the action is more than one word, account for it
		if (action.equals("blew"))
			action = "blew up";
		else if (action.equals("set"))
			action = "set fire to";
		else if (action.equals("dumped"))
			action = "dumped out";
		else if (action.equals("stepped"))
			action = "stepped on";
		else if (action.equals("picked"))
			action = "picked up";
		else if (action.equals("took"))
			action = "took down";
		// for three-item object lists:
		// the "+ 6" = 4 for the seconds time and the comma and space that follow, 1 for the space after the cause, and one for the space after the action
		objects = myGuardDog.listToArray(save_line.split(" at ")[1].split(":")[2].substring(cause.length() + action.length() + 6));
		// special action: ...dyed a [color] sheep [new color]...
		if (action.equals("dyed") && objects.length == 1) {
			String wool_color = null;
			for (String color : myGuardDog.wool_dye_colors)
				if (objects[0].endsWith(color)) {
					wool_color = color;
					break;
				}
			if (wool_color == null) {
				myGuardDog.console.sendMessage(ChatColor.DARK_RED + "I couldn't find the new color of the sheep on this event!");
				myGuardDog.console.sendMessage(ChatColor.DARK_RED + "save line: \"" + ChatColor.WHITE + save_line + ChatColor.DARK_RED + "\"");
				wool_color = "????";
			}
			objects[0] = objects[0].substring(0, objects[0].length() - wool_color.length() - 1);
			action = "dyed " + wool_color;
		}
		temp = save_line.split(", ");
		try {
			x = Integer.parseInt(temp[1].substring(temp[1].lastIndexOf("(") + 1));
			y = Integer.parseInt(temp[2]);
			z = Integer.parseInt(temp[3].substring(0, temp[3].indexOf(")")));
		} catch (NumberFormatException exception) {
			myGuardDog.console.sendMessage(ChatColor.DARK_RED + "I couldn't read the coordinates on this event!");
			myGuardDog.console.sendMessage(ChatColor.DARK_RED + "save line: \"" + ChatColor.WHITE + save_line + ChatColor.DARK_RED + "\"");
		}
		temp = save_line.split("\"");
		if (temp[temp.length - 1].contains("Creative"))
			in_Creative_Mode = true;
		else if (temp[temp.length - 1].contains("Survival"))
			in_Creative_Mode = false;
		else
			in_Creative_Mode = null;
		world = myGuardDog.server.getWorld(save_line.split("\"")[1]);
		if (world == null) {
			myGuardDog.console.sendMessage(ChatColor.DARK_RED + "I couldn't find the world in this event!");
			myGuardDog.console.sendMessage(ChatColor.DARK_RED + "save line: \"" + ChatColor.WHITE + save_line + ChatColor.DARK_RED + "\"");
			myGuardDog.console.sendMessage(ChatColor.DARK_RED + "This is what I read as the world name: " + ChatColor.WHITE + "\"" + save_line.split("\"")[1] + "\"");
		} else
			location = new Location(world, x, y, z);
		if (save_line.endsWith("[rolled back]"))
			rolled_back = true;
		else
			rolled_back = false;
		// String message = ChatColor.WHITE + "date: " + getDate('/') + "; time: " + getTime(':') + "; cause: " + cause + "; action: " + action + "; objects: ";
		// if (objects != null)
		// for (String object : objects)
		// message += "\"" + object + "\" ";
		// message +=
		// "; x=" + x + "; y=" + y + "; z=" + z + "; world: " + world.getWorldFolder().getName() + "; in Creative Mode=" + in_Creative_Mode + "; rolled back="
		// + rolled_back;
		// myGuardDog.console.sendMessage(message);
	}

	// returns a simple answer to say whether an event is a placement event, a removal event, or neither
	public boolean isPlacement() {
		if (action.equals("placed") || action.equals("hung") || action.equals("grew") || action.equals("bonemealed") || action.equals("relocated") || action.equals("spread"))
			return true;
		else
			return false;
	}

	public boolean isRemoval() {
		if (action.equals("broke") || action.equals("blew up") || action.equals("creeper'd") || action.equals("picked up") || action.equals("burned")
				|| action.equals("took down") || action.equals("dropped") || action.equals("covered") || action.equals("removed") || action.equals("decayed")
				|| action.equals("T.N.T.'d"))
			return true;
		else
			return false;
	}

	// designates whether the Event can be rolled back (like placement events) or not (like stepping on a pressure plate)
	public boolean canBeRolledBack() {
		if (action.equals("pressed") || action.equals("stepped on") || action.equals("opened") || action.equals("closed") || action.equals("flipped")
				|| action.equals("set fire to"))
			return false;
		return true;
	}

	// time and date constructors
	public String getDate(char separator) {
		return "" + month + separator + day + separator + year;
	}

	public String getTime(char separator) {
		String time;
		if (hour < 10)
			time = "0" + hour + separator;
		else
			time = "" + hour + separator;
		if (minute < 10)
			time = time + "0" + minute + separator;
		else
			time = time + minute + separator;
		if (second < 10)
			time = time + "0" + second;
		else
			time = time + second;
		return time;
	}

	// setting this event's rolled back status
	public void setRolledBack(boolean new_rolled_back) {
		if (rolled_back != new_rolled_back) {
			rolled_back = new_rolled_back;
			if (!rolled_back)
				save_line = save_line.substring(save_line.length() - 14);
			else
				save_line += " [rolled back]";
		}
	}

	// overrides
	@Override
	public boolean equals(Object event) {
		if (!(event instanceof Event))
			return false;
		else if (save_line.equals(((Event) event).save_line))
			return true;
		else
			return false;
	}

	@Override
	public String toString() {
		return save_line;
	}
}
