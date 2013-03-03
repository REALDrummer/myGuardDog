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
		objects = new String[1];
		// derive the name of the object
		try {
			// try getting the item name with the I.D. and data provided
			String item_name = myPluginWiki.getItemName(object.getTypeId(), object.getData());
			// if that doesn't work, try subtracting 4 from the data until we can't any more (using %) and add "([data])" to the end of the name
			if (item_name == null) {
				item_name = myPluginWiki.getItemName(object.getTypeId(), object.getData() % 4);
				if (item_name != null)
					item_name += " (" + object.getData() + ")";
				// if that doesn't work, just use 0 for the data to find the name and add "([data])" to the end of the name
				else {
					item_name = myPluginWiki.getItemName(object.getTypeId(), 0);
					if (item_name != null)
						item_name += " (" + object.getData() + ")";
					// finally, if that doesn't work, make the name "something with the I.D. [id]:(data)"
					else {
						item_name = "something with the I.D. " + object.getTypeId();
						if (object.getData() != 0)
							item_name += ":" + object.getData();
					}
				}
			}
			objects = new String[] { item_name };
		} catch (ArrayIndexOutOfBoundsException exception) {
			objects = new String[] { "something with the I.D. " + object.getTypeId() };
			if (object.getData() != 0)
				objects[0] += ":" + object.getData();
		}
		// with Block or Entity objects, we can get the Location form the object, so just initialize that here and don't initialize it later
		location = object.getLocation();
		initializeOtherVariables(cause, action, null, in_Creative_Mode);
	}

	public Event(String cause, String action, Entity object, Boolean in_Creative_Mode) {
		objects = new String[1];
		// derive the name of the object
		try {
			// try getting the item name with the I.D. and data provided
			String entity_name = null;
			if (object instanceof Villager)
				// villager = 120
				entity_name = myPluginWiki.getEntityName(120, (byte) ((Villager) object).getProfession().getId());
			else
				entity_name = myPluginWiki.getEntityName(object.getType().getTypeId(), (byte) -1);
			// if that doesn't work, make the name "something with the I.D. [id]:(data)"
			if (entity_name == null)
				entity_name = "something with the I.D. " + object.getType().getTypeId();
			// if it did work, replace the
			objects = new String[] { entity_name };
		} catch (ArrayIndexOutOfBoundsException exception) {
			objects = new String[] { "something with the I.D. " + object.getType().getTypeId() };
		}
		// with Block or Entity objects, we can get the Location form the object, so just initialize that here and don't initialize it later
		location = object.getLocation();
		initializeOtherVariables(cause, action, null, in_Creative_Mode);
	}

	public Event(String cause, String action, ItemStack[] items, Location location, Boolean in_Creative_Mode) {
		objects = new String[items.length];
		// derive the names of the items
		for (int i = 0; i < items.length; i++) {
			// try getting the item name with the I.D. and data provided
			// this time, we need to keep the data suffix ("([data])") separate so we can singularize the item name at the end if there is only one
			String item_name = myPluginWiki.getItemName(items[i].getTypeId(), items[i].getData().getData()), data_suffix = null;
			// if that doesn't work, try subtracting 4 from the data until we can't any more (using %) and add "([data])" to the end of the name
			if (item_name == null) {
				item_name = myPluginWiki.getItemName(items[i].getTypeId(), items[i].getData().getData() % 4);
				if (item_name != null)
					data_suffix += " (" + items[i].getData().getData() + ")";
				// if that doesn't work, just use 0 for the data to find the name and add "([data])" to the end of the name
				else {
					item_name = myPluginWiki.getItemName(items[i].getTypeId(), 0);
					if (item_name != null)
						data_suffix += " (" + items[i].getData().getData() + ")";
					// finally, if that doesn't work, make the name "something with the I.D. [id]:(data)"
					else {
						item_name = "something with the I.D. " + items[i].getTypeId();
						if (items[i].getData().getData() != 0)
							item_name += ":" + items[i].getData().getData();
					}
				}
			}
			if (items[i].getAmount() == 1)
				objects[i] = myGuardDog.singularizeItemName(item_name);
			else if (items[i].getAmount() > 1)
				objects[i] = items[i].getAmount() + " " + item_name;
			if (data_suffix != null)
				objects[i] += data_suffix;
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
		// if my_location is null, that means location was already initialized earlier using a Block or Entity object in one of the first two Event constructors
		if (my_location != null)
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
		String formatted_action = action, formatted_object = objects[0];
		// special action: ...dyed a [color] sheep [new color]...
		if (action.startsWith("dyed") && objects != null && objects.length == 1) {
			formatted_action = "dyed";
			formatted_object = objects[0] + action.substring(4);
		}
		save_line = save_line + ", " + cause + " " + formatted_action;
		if (objects != null) {
			// start with formatted_object, which is objects[0] potentially modified for "dyed" instances
			try {
				Integer.parseInt(formatted_object);
				save_line = save_line + " something with the I.D. " + formatted_object;
			} catch (NumberFormatException exception) {
				if (!action.equals("killed") && !action.equals("shot") && !action.equals("hit") && !action.equals("grew") && !formatted_action.equals("dyed"))
					save_line = save_line + " " + myGuardDog.singularizeItemName(formatted_object);
				else
					save_line = save_line + " " + formatted_object;
			}
			// then do the rest of the objects if there are any others
			if (objects.length > 1)
				for (int i = 1; i < objects.length; i++) {
					if (objects.length == 2)
						save_line = save_line + " and ";
					try {
						Integer.parseInt(objects[i]);
						save_line = save_line + " something with the I.D. " + objects[i];
					} catch (NumberFormatException exception) {
						if (!action.equals("killed") && !action.equals("shot") && !action.equals("hit") && !action.equals("grew") && !formatted_action.equals("dyed"))
							save_line = save_line + " " + myGuardDog.singularizeItemName(objects[i]);
						else
							save_line = save_line + " " + objects[i];
					}
					if (objects.length >= 3)
						if (i == objects.length - 1)
							save_line = save_line + ", and ";
						else
							save_line = save_line + ", ";
				}
		}
		save_line = save_line + " at (" + x + ", " + y + ", " + z + ") in \"" + world.getName();
		if (in_Creative_Mode == null)
			save_line = save_line + ".\"";
		else if (in_Creative_Mode)
			save_line = save_line + "\" while in Creative Mode.";
		else
			save_line = save_line + "\" while in Survival Mode.";
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
		else if (action.equals("spread"))
			action = "spread to";
		else if (action.equals("dumped"))
			action = "dumped out";
		else if (action.equals("stepped"))
			action = "stepped on";
		else if (action.equals("picked"))
			action = "picked up";
		// for three-item object lists:
		// the "+ 6" = 4 for the seconds time and the comma and space that follow, 1 for the space after the cause, and one for the space after the action
		objects = new String[1];
		String object_list = save_line.split(" at ")[1].split(":")[2].substring(cause.length() + action.length() + 6);
		if (object_list.contains(", and ")) {
			// for a list of three or more items
			objects = object_list.split(", ");
			// eliminate the extra "and " in the last item
			objects[objects.length - 1] = objects[objects.length - 1].substring(4);
		}
		// for two-item object lists:
		// it's a two-item list if it has (1) an " and " with no "flint and steel" or "book and a quill", (2) it has both "flint and steel" and
		// "book and a quill", or (3) it has one of those and two or more "and"s
		// option (1)
		else if (object_list.contains(" and ") && !object_list.contains("flint and steel") && !object_list.contains("book and a quill"))
			objects = object_list.split(" and ");
		// option (2)
		else if (object_list.contains("flint and steel") && object_list.contains("book and a quill"))
			objects = new String[] { "flint and steel", "books and quills" };
		// option (3)
		else if (object_list.contains("flint and steel") || object_list.contains("book and a quill") && object_list.split(" and ").length > 2) {
			temp = object_list.split(" and ");
			if ((temp[0] + " and " + temp[1]).equals("a book and a quill") || (temp[0] + " and " + temp[1]).equals("books and quills")
					|| (temp[0] + " and " + temp[1]).equals("flint steel"))
				objects = new String[] { temp[0] + " and " + temp[1], temp[2] };
			else
				objects = new String[] { temp[0], temp[1] + " and " + temp[2] };
		} else if (object_list.startsWith("something with the I.D. "))
			objects[0] = object_list.substring(24);
		else if (!action.equals("killed") && !action.equals("hurt") && !action.equals("hit") && !action.equals("grew"))
			objects[0] = myGuardDog.pluralizeItemName(object_list);
		// exclude events with no objects
		else if (!object_list.equals(""))
			objects[0] = object_list;
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
		String world_name = save_line.split("\"")[1];
		// if in_Creative_Mode is null, that means the period in the quotes comes from the end of the sentence and is not part of the world's name
		if (world_name.endsWith(".") && in_Creative_Mode == null)
			world_name = world_name.substring(0, world_name.length() - 1);
		world = myGuardDog.server.getWorld(world_name);
		if (world == null) {
			myGuardDog.console.sendMessage(ChatColor.DARK_RED + "I couldn't find the world in this event!");
			myGuardDog.console.sendMessage(ChatColor.DARK_RED + "save line: \"" + ChatColor.WHITE + save_line + ChatColor.DARK_RED + "\"");
			myGuardDog.console.sendMessage(ChatColor.DARK_RED + "This is what I read as the world name: " + ChatColor.WHITE + "\"" + world_name + "\"");
		} else
			location = new Location(world, x, y, z);
		if (save_line.endsWith("[rolled back]"))
			rolled_back = true;
		else
			rolled_back = false;
		/**/
		String message = ChatColor.WHITE + "date: " + getDate('/') + "; time: " + getTime(':') + "; cause: " + cause + "; action: " + action + "; objects: ";
		if (objects != null)
			for (String object : objects)
				message += object + ", ";
		message +=
				"; x=" + x + "; y=" + y + "; z=" + z + "; world: " + world.getWorldFolder().getName() + "; in Creative Mode=" + in_Creative_Mode + "; rolled back="
						+ rolled_back;
		myGuardDog.console.sendMessage(message);
		/**/
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
				save_line = save_line + " [rolled back]";
		}
	}
}
