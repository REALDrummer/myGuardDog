package REALDrummer;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;

public class Wiki {
	// String[item I.D.][special data][all the names that could be applied to that item]
	// the special data works like this:
	// [0] is the name for the overall item, e.g. "logs" for any type of log, which all have the I.D. 17; the other indexes are [data+1], e.g. birch logs (I.D.
	// = 17 & data = 2) are found at [17][3]
	// in the dimension with all the names that could be applied to the item, [0] is the plural, [1] is the singular, and the others are just a list
	public static final String[][][] item_IDs = {
			{ { "air", "some air" } },
			{ { "stone", "some stone", "rock", "smooth stone" } },
			{ { "grass", "some grass", "grass blocks" } },
			{ { "dirt", "some dirt", "filth" } },
			{ { "cobblestone", "some cobblestone", "cobblies" } },
			{ { "wooden planks", "some wooden planks", "planks" }, { "oak planks", "some oak planks", "oak wooden planks" },
					{ "spruce planks", "some spruce planks", "spruce wooden planks", "pine planks", "pine wooden planks" },
					{ "birch planks", "some birch planks", "birch wooden planks" }, { "jungle planks", "some jungle planks", "jungle wooden planks" } },
			{ { "saplings", "some saplings" }, { "oak saplings", "some oak saplings", "oak tree saplings" },
					{ "spruce saplings", "some spruce saplings", "spruce tree saplings", "pine tree saplings" },
					{ "birch saplings", "some birch saplings", "birch tree saplings" }, { "jungle saplings", "some jungle saplings", "jungle tree saplings" } },
			{ { "bedrock", "some bedrock" } },
			{ { "water", "some water" } },
			{ { "stationary water", "some stationary water", "immobile water", "nonspreadable water" } },
			{ { "lava", "some lava" } },
			{ { "stationary lava", "some stationary lava", "immobile lava", "nonspreadable lava" } },
			{ { "sand", "some sand" } },
			{ { "gravel", "some gravel", "pebbles" } },
			{ { "gold ore", "some gold ore", "golden ore" } },
			{ { "iron ore", "some iron ore" } },
			{ { "coal ore", "some coal ore" } },
			{ { "logs", "a log", "wood" }, { "oak logs", "an oak log", "oak wood" }, { "spruce logs", "a spruce log", "spruce wood", "pine logs", "pine wood" },
					{ "birch logs", "a birch log", "birch wood" }, { "jungle logs", "a jungle log", "jungle wood" } },
			{ { "leaves", "some leaves", "leaves blocks", "leafs blocks" }, { "oak leaves", "some oak leaves", "oak leaves blocks", "oak leafs blocks" },
					{ "spruce leaves", "some spruce leaves", "spruce leaves blocks", "spruce leafs blocks", "pine needles", "pine leaves blocks", "pine leafs blocks" },
					{ "birch leaves", "some birch leaves", "birch leaves blocks", "birch leafs blocks" },
					{ "jungle leaves", "some jungle leaves", "jungle leaves blocks", "jungle leafs blocks" } },
			{ { "sponges", "a sponge", "loofas" } },
			{ { "glass", "some glass", "glass blocks", "glass cubes" } },
			{ { "lapis lazuli ore", "some lapis lazuli ore" } },
			{ { "lapis lazuli blocks", "a lapis lazuli block", "blocks of lapis lazuli" } },
			{ { "dispensers", "a dispenser", "shooters" } },
			{
					{ "sandstone", "some sandstone", "sandbricks" },
					{ "regular sandstone", "some regular sandstone", "regular sandbricks", "normal sandstone", "normal sandbricks", "natural sandstone", "natural sandbricks" },
					{ "chiseled sandstone", "some chiseled sandstone", "chiseled sandbricks", "ancient sandstone", "ancient sandbricks", "pyramid sandstone",
							"pyramid sandbricks", "hieroglyphics sandstone", "hieroglyphics sandbricks" },
					{ "smooth sandstone", "some smooth sandstone", "smooth sandbricks", "clean sandstone", "clean sandbricks" } },
			{ { "note blocks", "a note block", "music blocks", "sound blocks", "speakers" } },
			{ { "beds", "a bed" } },
			{ { "powered rails", "a powered rail", "powered redstone rails", "powered redstone tracks", "powered redstone railroad tracks" } },
			{ { "detector rails", "a detector rail", "detection rails", "sensor rails", "sensory rails", "pressure rails", "pressure plate rails", "detector railroad tracks",
					"detection railroad tracks", "sensor railroad tracks", "sensory railroad tracks", "pressure railroad tracks", "pressure plate railroad tracks" } },
			{ { "sticky pistons", "a sticky piston", "slime pistons", "slimy pistons" } },
			{ { "cobwebs", "a cobweb", "spider webs", "webs" } },
			{ { "tall grass and dead shrubs", "some tall grass or a dead shrub", "tall grass and dead shrubbery", "weeds" },
					{ "dead grass", "some dead grass", "dead grass bushes", "dead grass shrubs", "dead grass shrubbery" }, { "tall grass", "high grass" },
					{ "ferns", "a fern", "small plants" } },
			{ { "dead shrubs", "a dead shrub", "dead bushes", "dead shrubbery", "dried plants" } },
			{ { "pistons", "a piston" } },
			{ { "piston arms", "a piston arm", "piston extensions", "piston pusher" } },
			{ { "wool", "some wool", "wool blocks" }, { "white wool", "some white wool", "white wool blocks" }, { "orange wool", "some orange wool", "orange wool blocks" },
					{ "magenta wool", "some magenta wool", "magenta wool blocks" }, { "light blue wool", "some light blue wool", "light blue wool blocks" },
					{ "yellow wool", "some yellow wool", "yellow wool blocks" },
					{ "lime green wool", "some lime green wool", "lime green wool blocks", "green wool blocks", "light green wool blocks" },
					{ "pink wool", "some pink wool", "pink wool blocks" }, { "gray wool", "some gray wool", "dark gray wool blocks" },
					{ "light gray wool", "some light gray wool", "light gray wool blocks", "off white wool blocks" }, { "cyan wool", "some cyan wool", "cyan wool blocks" },
					{ "purple wool", "some purple wool", "purple wool blocks" }, { "blue wool", "some blue wool", "dark blue wool blocks" },
					{ "brown wool", "some brown wool", "brown wool blocks" },
					{ "cactus green wool", "some cactus green wool", "cactus green wool blocks", "dark green wool blocks" },
					{ "red wool", "some red wool", "red wool blocks" }, { "black wool", "some black wool", "black wool blocks" } },
			{ { "blocks moved by pistons", "a block moved by a piston" } },
			{ { "flowers", "a flower", "yellow flowers", "dandelions" } },
			{ { "roses", "a rose", "red flowers" } },
			{ { "brown mushrooms", "a brown mushroom", "small brown mushrooms", "little brown mushrooms" } },
			{ { "red mushrooms", "a red mushroom", "small red mushrooms", "little red mushrooms" } },
			{ { "gold blocks", "a gold block", "blocks of gold" } },
			{ { "iron blocks", "an iron block", "blocks of iron" } },
			{
					{ "all double slab blocks", "a double slab block", "all double slabs", "all stacked slabs" },
					{ "stone double slab blocks", "a stone double slab block", "stone double slabs", "stone stacked slabs" },
					{ "sandstone double slab blocks", "a sandstone double slab block", "sandstone double slabs", "sandstone stacked slabs", "sandbrick double slab blocks",
							"sandbrick double slabs", "sandbrick stacked slabs" },
					{ "cobblestone double slab blocks", "a cobblestone double slab block", "cobblestone double slabs", "cobblestone stacked slabs" },
					{ "brick double slab blocks", "a brick double slab block", "clay brick stacked slabs", "clay brick double slab blocks", "clay brick double slabs" },
					{ "stone brick double slab blocks", "a stone brick double slab block", "stone brick double slabs", "stone brick stacked slabs" },
					{ "Nether brick double slab blocks", "a Nether brick double slab block", "Nether brick double slabs", "Nether brick stacked slabs" },
					{ "Nether Quartz double slab blocks", "a Nether Quartz double slab block", "Nether Quartz double slabs", "Nether Quartz stacked slabs" } },
			{ { "all slabs", "a slab", "all half blocks" }, { "stone slabs", "a stone slab", "stone half blocks" },
					{ "sandstone slabs", "a sandstone slab", "sandstone half blocks", "sandbrick slabs", "sandbrick half blocks" },
					{ "cobblestone slabs", "a cobblestone slab", "cobblestone half blocks" }, { "brick slabs", "a brick slab", "clay brick slabs", "clay brick half blocks" },
					{ "stone brick slabs", "a stone brick slab", "stone brick half blocks" }, { "Nether brick slabs", "a Nether brick slab", "Nether brick half blocks" },
					{ "Nether Quartz slabs", "a Nether Quartz slab", "Nether Quartz half blocks" } },
			{ { "bricks", "some bricks", "bricks blocks" } },
			{ { "T.N.T.", "some T.N.T.", "TNT", "dynamite", "trinitrotoluene" } },
			{ { "bookcases", "a bookcase", "bookshelves", "bookshelfs" } },
			{ { "mossy stone", "some mossy stone", "mossy cobblestone", "ancient cobblestone", "old cobblestone" } },
			{ { "obsidian", "some obsidian", "volcanic glass" } },
			{ { "torches", "a torch", "fire sticks" } },
			{ { "fire", "a fire", "flames" } },
			{ { "monster spawners", "a monster spawner", "spawners" } },
			{ { "oak stairs", "some oak stairs", "oak wood stairs", "wooden stairs", "oak wood steps", "wooden steps" } },
			{ { "chests", "a chest" } },
			{ { "redstone wire", "a piece of redstone wire", "wire" } },
			{ { "diamond ore", "some diamond ore" } },
			{ { "diamond blocks", "a diamond block", "blocks of diamonds" } },
			{ { "crafting tables", "a crafting table", "crafting workbench" } },
			{ { "wheat", "some wheat", "crops" } },
			{ { "farmland blocks", "some farmland", "plowed tilled land soil blocks" } },
			{ { "furnaces", "a furnace", "ovens", "ranges" } },
			{ { "burning furnaces", "a burning furnace", "active burning ovens ranges furnaces" } },
			{ { "sign posts", "a sign post", "ground sign posts" } },
			{ { "wooden doors", "a wooden door", "doors" } },
			{ { "ladders", "a ladder", "wooden rope ladders" } },
			{ { "rails", "a rail", "normal regular unpowered iron rails railroad tracks" } },
			{ { "cobblestone stairs", "some cobblestone stairs", "cobblestone steps" } },
			{ { "wall signs", "a wall sign", "posted wall signs" } },
			{ { "levers", "a lever", "switches" } },
			{ { "stone pressure plates", "a stone pressure plate", "stone foot switches plates" } },
			{ { "iron doors", "an iron door" } },
			{ { "wooden pressure plates", "a wooden pressure plate", "wooden foot switches plates" } },
			{ { "redstone ore", "some redstone ore" } },
			{ { "glowing redstone ore", "some glowing redstone ore", "luminescent redstone ore" } },
			{ { "inactive redstone torches", "an inactive redstone torch" } },
			{ { "redstone torches", "a redstone torch" } },
			{ { "stone buttons", "a stone button", "buttons" } },
			{ { "snow on the ground", "some snow on the ground", "snow", "snow on the ground", "fallen snow", "fresh snow", "ground snow" } },
			{ { "ice", "some ice", "blocks of ice", "ice blocks" } },
			{ { "snow blocks", "a snow block", "blocks of snow" } },
			{ { "cacti", "a cactus", "cactuses", "saguaros", "saguaro cacti", "saguaro cactuses" } },
			{ { "clay blocks", "a clay block", "blocks of clay" } },
			{ { "sugar cane", "some sugar cane", "sugar canes", "sugarcanes" } },
			{ { "jukeboxes", "a jukebox", "disc player", "music box", "slotted block", ".mp3 player" } },
			{ { "wooden fences", "a wooden fence post", "wooden fence posts", "wooden railings" } },
			{ { "pumpkins", "a pumpkin", "unlit Jack-o'-Lanterns", "dark Jack-o'-Lanterns" } },
			{ { "Netherrack", "some Netherrack", "Nether rack", "Nether dirt" } },
			{ { "Soul Sand", "some Soul Sand", "Nether sand", "quicksand", "quick sand" } },
			{ { "glowstone", "some glowstone", "glowstone blocks", "blocks of glowstone" } },
			{ { "Nether portal blocks", "a Nether portal block", "Nether portal swirly blocks" } },
			{ { "Jack-o'-Lanterns", "a Jack-o'-Lantern", "lit Jack-o'-Lanterns", "lit Jack o Lanterns", "lit JackoLanterns" } },
			{ { "cakes", "a cake", "cake blocks" } },
			{ { "repeaters", "a repeater", "diodes", "delayers", "redstone repeaters", "redstone diodes", "redstone delayers" } },
			{ { "active repeaters", "an active repeater", "active diodes", "active delayers", "active redstone repeaters", "active redstone diodes",
					"active redstone delayers" } },
			{ { "locked chests", "a locked chest" } },
			{ { "trapdoors", "a trapdoor", "ground doors" } },
			{ { "monster egg blocks", "a monster egg block", "monster eggs", "silverfish spawners" },
					{ "monster egg stone blocks", "a monster egg stone block", "monster egg stone", "silverfish stone", "silverfish smooth stone" },
					{ "monster egg cobblestone blocks", "a monster egg cobblestone block", "silverfish cobblestone" },
					{ "monster egg stone brick blocks", "a monster egg stone brick block", "monster egg stone bricks", "silverfish stone bricks" } },
			{ { "stone bricks", "some stone brick", "cobblestone bricks", "cobble bricks" },
					{ "mossy stone bricks", "some mossy stone brick", "mossy cobblestone bricks", "mossy cobble bricks" },
					{ "cracked stone bricks", "some cracked stone brick", "cracked cobblestone bricks", "cracked cobble bricks" },
					{ "chiseled stone bricks", "some chiseled stone brick", "chiseled cobblestone bricks", "chiseled cobble bricks", "circle blocks" } },
			{ { "giant brown mushroom blocks", "a giant brown mushroom block", "huge brown mushrooms", "big brown mushrooms", "giant brown 'shroom blocks",
					"huge brown 'shrooms", "big brown shrooms", "giant brown shroom blocks", "huge brown shrooms", "big brown shrooms" } },
			{ { "giant red mushroom blocks", "a giant red mushroom block", "huge red mushrooms", "big red mushrooms", "giant red 'shroom blocks", "huge red 'shrooms",
					"big red shrooms", "giant red shroom blocks", "huge red shrooms", "big red shrooms" } },
			{ { "iron bars", "some iron bars", "wrought iron bars" } },
			{ { "glass panes", "a glass pane", "windows", "window panes" } },
			{ { "melon blocks", "a melon", "full melons", "watermelon blocks", "whole melons", "whole watermelons" } },
			{ { "pumpkin stems", "a pumpkin stem", "pumpkin stalks", "pumpkin vines" } },
			{ { "melon stems", "a melon stem", "melon stalks", "melon vines", "watermelon stems", "watermelon stalks", "watermelon vines" } },
			{ { "vines", "some vines", "jungle vines", "swamp vines" } },
			{ { "fence gates", "a fence gate", "wooden gates", "wood gates" } },
			{ { "brick stairs", "some brick stairs", "brick steps", "clay brick stairs", "clay brick steps" } },
			{ { "stone brick stairs", "some stone brick stairs", "stone brick steps", "stone stairs", "stone steps" } },
			{ { "mycelium", "some mycelium", "mushroom grass", "shroom grass", "mushroom biome grass" } },
			{ { "lily pads", "a lily pad", "lilies", "pond lilies", "lilypads", "water lily", "water lilies" } },
			{ { "Nether brick blocks", "some Nether brick", "Nether fortress bricks blocks", "Nether dungeon bricks blocks" } },
			{ { "Nether brick fences", "a Nether brick fence post", "Nether fortress fences", "Nether dungeon fences" } },
			{ { "Nether brick stairs", "some Nether brick stairs", "Nether fortress stairs", "Nether dungeon stairs", "Nether brick steps", "Nether fortress steps",
					"Nether dungeon steps" } },
			{ { "Nether warts", "some Nether warts", "Nether mushrooms", "Nether 'shrooms", "Nether fungi" } },
			{ { "enchantment tables", "an enchantment table" } },
			{ { "brewing stands", "a brewing stand" } },
			{ { "cauldrons", "a cauldron" } },
			{ { "End portal blocks", "an End portal block" } },
			{ { "End portal frame blocks", "an End portal frame block" } },
			{ { "End stone", "some End stone", "End blocks" } },
			{ { "dragon eggs", "a dragon egg", "Enderdragon eggs" } },
			{ { "redstone lamps", "a redstone lamp", "glowstone lamps" } },
			{ { "active redstone lamps", "an active redstone lamp", "active glowstone lamps" } },
			{
					{ "wooden double slab blocks", "a wooden double slab block", "wooden double slabs", "wooden plank double slab blocks", "wooden plank double slabs",
							"wood double slab blocks", "wood double slabs", "wood plank double slab blocks", "wood plank double slabs" },
					{ "oak double slab blocks", "an oak double slab block", "oak double slabs", "oak plank double slab blocks", "oak plank double slabs",
							"oak wood double slab blocks", "oak wood double slabs", "oak wood plank double slab blocks", "oak wood plank double slabs" },
					{ "spruce double slab blocks", "a spruce double slab block", "spruce double slabs", "spruce plank double slab blocks", "spruce plank double slabs",
							"spruce wood double slab blocks", "spruce wood double slabs", "spruce wood plank double slab blocks", "spruce wood plank double slabs",
							"pine double slab blocks", "pine double slabs", "pine plank double slab blocks", "pine plank double slabs", "pine wood double slab blocks",
							"pine wood double slabs", "pine wood plank double slab blocks", "pine wood plank double slabs" },
					{ "birch double slab blocks", "a birch double slab block", "birch double slabs", "birch plank double slab blocks", "birch plank double slabs",
							"birch wood double slab blocks", "birch wood double slabs", "birch wood plank double slab blocks", "birch wood plank double slabs" }, },
			{
					{ "wooden slabs", "a wooden slab", "wooden half blocks", "wood slabs", "wood half blocks", "wooden plank slabs", "wooden plank half blocks",
							"wood plank slabs", "wood plank half blocks" },
					{ "oak slabs", "an oak slab", "oak half blocks", "oak wood slabs", "oak wood half blocks", "oak plank slabs", "oak plank half blocks",
							"oak wood plank slabs", "oak wood plank half blocks" },
					{ "spruce slabs", "a spruce slab", "spruce half blocks", "spruce wood slabs", "spruce wood half blocks", "spruce plank slabs", "spruce plank half blocks",
							"spruce wood plank slabs", "spruce wood plank half blocks", "pine slabs", "pine half blocks", "pine wood slabs", "pine wood half blocks",
							"pine plank slabs", "pine plank half blocks", "pine wood plank slabs", "pine wood plank half blocks" },
					{ "birch slabs", "a birch slab", "birch half blocks", "birch wood slabs", "birch wood half blocks", "birch plank slabs", "birch plank half blocks",
							"birch wood plank slabs", "birch wood plank half blocks" } },
			{ { "cocoa bean plants", "a cocoa bean plant", "cocoa bean pods", "cocoa beans" } },
			{ { "sandstone stairs", "some sandstone stairs", "sandstone steps" } },
			{ { "emerald ore", "some emerald ore" } },
			{ { "Ender Chests", "an Ender Chest", "Enderchests" } },
			{ { "tripwire hooks", "a tripwire hook", "tripwire mechanisms", "trip wire hooks", "trip wire mechanisms" } },
			{ { "tripwire", "some tripwire", "trip wire" } },
			{ { "emerald blocks", "an emerald block", "blocks of emerald", "blocks of emeralds" } },
			{ { "spruce stairs", "some spruce stairs", "spruce wood stairs", "spruce steps", "spruce wood steps" } },
			{ { "birch stairs", "some birch stairs", "birch wood stairs", "birch steps", "birch wood steps" } },
			{ { "jungle stairs", "some jungle stairs", "jungle wood stairs", "jungle steps", "jungle wood steps" } },
			{ { "command blocks", "a command block" } },
			{ { "beacons", "a beacon" } },
			{
					{ "cobblestone walls", "a cobblestone wall", "cobblestone fences" },
					{ "mossy cobblestone walls", "a mossy cobblestone wall", "mossy cobblestone fences", "mossy stone walls", "mossy stone fences", "old cobblestone walls",
							"old cobblestone fences", "old stone walls", "old stone fences", "ancient cobblestone walls", "ancient cobblestone fences", "ancient stone walls",
							"ancient stone fences" } },
			{ { "flower pots", "a flower pot", "pots", "clay pots" } },
			{ { "carrots", "some carrots" } },
			{ { "potatoes", "some potatoes", "potatos" } },
			{ { "wooden buttons", "a wooden button", "wood buttons" } },
			{ { "monster heads", "a monster head", "heads" }, { "skeleton skulls", "a skeleton skull", "skeleton heads", "skele heads", "skele skulls" },
					{ "Wither skeleton skulls", "a Wither skeleton skull", "Wither skeleleton heads", "Wither skele skulls", "Wither skele heads" },
					{ "zombie heads", "a zombie head" }, { "Steve heads", "a Steve head", "Minecraft Steve heads", "guy heads", "man heads", "person heads", "human heads" },
					{ "creeper heads", "a creeper head" } },
			{ { "anvils", "an anvil" } },
			{ { "trapped chests", "a trapped chest" } },
			{ { "light weighted pressure plates", "a light weighted pressure plate", "light weight pressure plates", "lightweight pressure plates", "golden pressure plates",
					"gold pressure plates" } },
			{ { "heavy weighted pressure plates", "a heavy weighted pressure plate", "heavy weight pressure plates", "heavyweight pressure plates", "iron pressure plates",
					"silver pressure plates" } },
			{ { "redstone comparators", "a redstone comparator", "redstone comparers" } },
			{ { "active redstone comparators", "an active redstone comparator", "active redstone comparers" } },
			{ { "daylight sensors", "a daylight sensor", "night sensors", "nighttime sensors", "day night sensors", "day/night sensors", "daytime nighttime sensors",
					"daytime/nighttime sensors", "solar panels" } },
			{ { "redstone blocks", "a redstone block", "blocks of redstone dust" } },
			{ { "Nether Quartz ore", "some Nether Quartz ore", "raw Nether Quartz", "crude Nether Quartz", "unrefined Nether Quartz" } },
			{ { "hoppers", "a hopper", "funnels" } },
			{
					{ "Nether Quartz blocks", "a Nether Quartz block", "a Nether Quartz block", "blocks of Nether Quartz" },
					{ "chiseled Nether Quartz blocks", "a chiseled Nether Quartz block", "chiseled blocks of Nether Quartz", "fancy Nether Quartz blocks",
							"fancy blocks of Nether Quartz", "fancy block of Nether Quartz" },
					{ "pillar Nether Quartz blocks", "a pillar Nether Quartz block", "pillar blocks of Nether Quartz" } },
			{ { "Nether Quartz stairs", "some Nether Quartz stairs", "Nether Quartz steps" } },
			{ { "activator rails", "an activator rail", "T.N.T. activator rails", "TNT activator rails", "striker rails", "T.N.T. starter rails", "TNT starter rails" } },
			{ { "droppers", "a dropper", "lazy dispensers", "new dispensers" } },
			// block I.D.s --> item I.D.s
			{ { "iron shovels", "an iron shovel", "iron spades" } },
			{ { "iron pickaxes", "an iron pickaxe", "iron picks" } },
			{ { "iron axes", "an iron axe", "iron hatchets", "iron tree axe" } },
			{ { "flint and steel", "some flint and steel" } },
			{ { "apples", "an apple", "red apples" } },
			{ { "bows", "a bow", "bows and arrows", "longbows", "long bows", "shortbows", "short bows" } },
			{ { "arrows", "an arrow" } },
			{ { "coal", "a lump of coal" } },
			{ { "diamonds", "a diamond" } },
			{ { "iron", "an iron ingot", "iron ingots" } },
			{ { "gold", "a gold ingot", "gold ingots", "gold bars" } },
			{ { "iron swords", "an iron sword" } },
			{ { "wooden swords", "a woode sword", "wood swords" } },
			{ { "wooden shovels", "a wooden shovel", "wood shovels", "wooden spades", "wood spades" } },
			{ { "wooden pickaxes", "a wooden pickaxe", "wooden picks", "wood pickaxes", "wood picks" } },
			{ { "wooden axes", "a wooden axe", "wooden hatchets", "wooden tree axes", "wood axes", "wood hatchets", "wood tree axes" } },
			{ { "stone swords", "a stone sword", "cobblestone swords", "cobble swords" } },
			{ { "stone shovels", "a stone shovel", "cobblestone shovels", "cobble shovels", "stone spades", "cobblestone spades", "cobble spades" } },
			{ { "stone pickaxes", "a stone pickaxe", "stone picks", "cobblestone pickaxes", "cobble pickaxes", "cobblestone picks", "cobble picks" } },
			{ { "stone axes", "a stone axe", "stone hatchets", "stone tree axes", "cobblestone axes", "cobble axes", "cobblestone hatchets", "cobble hatchets",
					"cobblestone tree axes", "cobble tree axes" } },
			{ { "diamond swords", "a diamond sword" } },
			{ { "diamond shovels", "a diamond shovel", "diamond spades" } },
			{ { "diamond pickaxes", "a diamond pickaxe", "diamond picks" } },
			{ { "diamond axes", "a diamond axe", "diamond hatchets", "diamond tree axes" } },
			{ { "sticks", "a stick", "twigs" } },
			{ { "bowls", "a bowl", "wooden bowls", "wood bowls", "soup bowls" } },
			{ { "mushroom stew", "a bowl of mushroom stew", "mushroom soup", "mooshroom milk", "mooshroom cow milk" } },
			{ { "golden swords", "a golden sword", "gold swords" } },
			{ { "golden shovels", "a golden shovel", "gold shovels", "golden spades", "gold spades" } },
			{ { "golden pickaxes", "a golden pickaxe", "golden picks", "gold pickaxes", "gold picks" } },
			{ { "golden axes", "a golden axe", "golden hatchets", "golden tree axes", "gold axes", "gold hatchets", "gold tree axes" } },
			{ { "string", "some string" } },
			{ { "feathers", "a feather" } },
			{ { "gunpowder", "some gunpowder", "sulfur", "sulphur" } },
			{ { "wooden hoes", "a wooden hoe", "wood hoes" } },
			{ { "stone hoes", "a stone hoe", "cobblestone hoes", "cobble hoes" } },
			{ { "iron hoes", "an iron hoe" } },
			{ { "diamond hoes", "a diamond hoe" } },
			{ { "golden hoes", "a golden hoe", "gold hoes" } },
			{ { "seeds", "some seeds", "seed packets" } },
			{ { "wheat", "some wheat", "crops" } },
			{ { "bread", "a loaf of bread", "bread loaves" } },
			{ { "leather caps", "a leather cap", "leather helmets", "leather helms" } },
			{ { "leather tunics", "a leather tunic", "leather shirts", "leather chestplates" } },
			{ { "leather pants", "a pair of leather pants", "leather leggings", "leather chaps" } },
			{ { "leather boots", "a pair of leather boots", "leather shoes" } },
			{ { "chainmail helmets", "a chainmail helmet", "chainmail caps", "chainmail helms", "chain helmets", "chain caps", "chain helms" } },
			{ { "chainmail chestplates", "a chainmail chestplate", "chainmail tunics", "chainmail shirts", "chain chestplates", "chain tunics", "chain shirts" } },
			{ { "chainmail leggings", "some chainmail leggings", "chainmail pants", "chain leggings", "chain pants" } },
			{ { "chainmail boots", "a pair of chainmail boots", "chainmail shoes", "chain boots", "chain shoes" } },
			{ { "iron helmets", "an iron helmet", "iron caps", "iron helms" } },
			{ { "iron chestplates", "an iron chestplate", "iron tunics", "iron shirts" } },
			{ { "iron leggings", "some iron leggings", "iron pants" } },
			{ { "iron boots", "a pair of iron boots", "iron shoes" } },
			{ { "diamond helmets", "a diamond helmet", "diamond caps", "diamond helms" } },
			{ { "diamond chestplates", "a diamond chestplate", "diamond tunics", "diamond shirts" } },
			{ { "diamond leggings", "some diamond leggings", "diamond pants" } },
			{ { "diamond boots", "a pair of diamond boots", "diamond shoes" } },
			{ { "golden helmets", "a golden helmet", "golden caps", "golden helms", "gold helmets", "gold caps", "gold helms" } },
			{ { "golden chestplates", "a golden chestplate", "gold chestplates", "golden tunics", "gold tunics", "golden shirts", "gold shirts" } },
			{ { "golden leggings", "some golden leggings", "gold leggings", "golden pants", "gold pants" } },
			{ { "golden boots", "a pair of golden boots", "gold boots", "golden shoes", "gold shoes" } },
			{ { "flint", "a piece of flint", "arrowheads" } },
			{ { "raw porkchops", "a raw porkchop", "uncooked porkchops" } },
			{ { "cooked porkchops", "a cooked porkchop", "porkchops" } },
			{ { "paintings", "a painting", "artwork" } },
			{
					{ "golden apples", "a golden apple", "gold apples" },
					{ "enchanted golden apples", "an enchanted golden apple", "enchanted gold apples", "magic golden apples", "magic gold apples", "shiny golden apples",
							"shiny gold apples", "shining golden apples", "shiny golden apples" } },
			{ { "signs", "a sign", "sign posts", "posted signs", "wall signs" } },
			{ { "wooden doors", "a wooden door", "wood doors" } },
			{ { "buckets", "a bucket", "pails" } },
			{ { "buckets of water", "a bucket of water", "water buckets" } },
			{ { "buckets of lava", "a bucket of lava", "lava buckets" } },
			{ { "minecarts", "a minecart", "mine carts", "minecars", "mine cars", "rail cars" } },
			{ { "saddles", "a saddle", "pig saddles" } },
			{ { "iron doors", "an iron door", "metal doors" } },
			{ { "redstone dust", "some redstone dust", "redstone powder", "redstone" } },
			{ { "snowballs", "a snow ball" } },
			{ { "boats", "a boat", "wooden boats", "wood boats", "rafts", "wood rafts", "wooden rafts" } },
			{ { "leather", "some leather", "cow hides", "cow skin", "cowskin" } },
			{ { "milk", "some milk", "leche", "bucket of milk", "buckets of milk", "pail of milk", "pails of milk" } },
			{ { "bricks", "a brick", "clay bricks" } },
			{ { "clay", "a piece of clay" } },
			{ { "sugarcane", "some sugarcane", "sugarcanes", "sugar canes" } },
			{ { "papers", "a piece of paper" } },
			{ { "books", "a book" } },
			{ { "slimeballs", "a slimeball" } },
			{ { "storage minecarts", "a storage minecart", "storage minecars", "storage mine cars", "storage rail cars", "chest minecarts", "chest minecars",
					"chest mine cars", "chest rail cars", "minecarts with chests", "minecars with chests", "mine cars with chests", "rail cars with chests" } },
			{ { "powered minecarts", "a powered minecart", "powered minecars", "powered mine cars", "powered rail cars", "furnace minecarts", "furnace minecars",
					"furnace mine cars", "furnace rail cars", "minecarts with furnaces", "minecars with furnaces", "mine cars with furnaces", "rail cars with furnaces" } },
			{ { "eggs", "an egg", "chicken eggs" } },
			{ { "compasses", "a compass" } },
			{ { "fishing rods", "a fishing rod", "fishing poles" } },
			{ { "clocks", "a clock", "watches", "pocketwatches", "pocket watches" } },
			{ { "glowstone dust", "some glowstone dust" } },
			{ { "raw fish", "a raw fish", "uncooked fish", "raw fish", "uncooked fish", "sushi" } },
			{ { "cooked fish", "a piece of cooked fish", "fish" } },
			{
					{ "dyes", "some dye", "wool dyes" },
					{ "ink sacks", "an ink sack", "squid ink sacks", "squid ink sackks", "squid ink pods", "black wool dyes" },
					{ "red dye", "some red dye", "rose red wool dyes" },
					{ "cactus green dye", "some cactus green dye", "cactus green dyes", "cactus green wool dyes" },
					{ "cocoa beans", "some cocoa beans", "chocolate beans", "brown dyes", "brown wool dyes" },
					{ "lapis lazuli", "a piece of lapis lazuli", "lapis lazuli dyes", "lapis lazuli wool dyes", "lapis dyes", "lapis wool dyes", "blue dyes",
							"blue wool dyes", "dark blue dyes", "dark blue wool dyes" },
					{ "purple dye", "some purple dye", "purple dyes", "purple wool dyes" },
					{ "cyan dye", "some cyan dye", "cyan dyes", "cyan wool dyes" },
					{ "light gray dye", "some light gray dye", "light gray dyes", "light gray wool dyes", "light grey dyes", "light grey wool dyes" },
					{ "gray dye", "some gray dye", "dark gray dyes", "dark gray wool dyes", "dark grey dyes", "dark grey wool dyes" },
					{ "pink dye", "some pink dye", "pink dyes", "pink wool dyes", "light red dyes", "light red wool dyes" },
					{ "bright green dye", "some bright green dye", "bright green dyes", "lime dyes", "lime wool dyes", "lime green dyes", "lime green wool dyes",
							"green dyes", "green wool dyes" },
					{ "yellow dye", "some yellow dye", "yellow dyes", "yellow wool dyes", "yellow flower dyes", "yellow flower wool dyes", "dandelion yellow dyes",
							"dandelion yellow wool dyes", "yellow dandelion dyes", "yellow dandelion wool dyes" },
					{ "light blue dye", "light blue dyes", "light blue wool dyes" }, { "magenta dye", "some magenta dye", "magenta dyes", "magenta wool dyes" },
					{ "orange dye", "orange dyes", "orange wool dyes" },
					{ "bone meal", "some bone meal", "bone meals", "bonemeals", "white dyes", "white wool dyes", "wool bleaches" } },
			{ { "bones", "a bone" } },
			{ { "sugar", "some sugar", "processed sugar", "powdered sugar", "raw sugar", "baker's sugar" } },
			{ { "cakes", "a cake", "birthday cakes" } },
			{ { "beds", "a bed" } },
			{ { "repeaters", "a repeater", "diodes", "delayers", "redstone repeaters", "redstone diodes", "redstone delayers" } },
			{ { "cookies", "a cookie", "chocolate chip cookies", "oatmeal raisin cookies" } },
			{ { "maps", "a map", "atlases", "charts" } },
			{ { "shears", "some shears", "clippers" } },
			{ { "melon slices", "a melon slice", "slices of melon" } },
			{ { "pumpkin seeds", "some pumpkin seeds" } },
			{ { "melon seeds", "some melon seeds" } },
			{ { "raw beef", "a hunk of raw beef", "uncooked beef", "uncooked steak" } },
			{ { "steak", "a steak", "beef", "cooked beef" } },
			{ { "raw chickens", "a raw chicken", "uncooked chickens" } },
			{ { "cooked chickens", "a cooked chicken", "chickens" } },
			{ { "rotten flesh", "some rotten flesh", "rotted flesh", "flesh", "zombie flesh", "zombie meat" } },
			{ { "Ender Pearls", "an Ender Pearl", "Enderpearls", "Enderman Pearls" } },
			{ { "Blaze rods", "a Blaze rod", "glowsticks", "glow sticks" } },
			{ { "Ghast tears", "a Ghast tear", "tears" } },
			{ { "gold nuggets", "a gold nugget", "golden nuggets", "gold pieces", "pieces of gold" } },
			{ { "Nether warts", "some Nether warts", "Nether mushrooms", "Nether 'shrooms", "Nether fungi" } },
			{
					{ "potions", "a potion" },
					{ "water bottles", "a water bottle", "canteens" },
					{ "Awkward Potions", "an Awkward Potion", "useless potions", "lame potions" },
					{ "Thick Potions", "a Thick Potion", "pots potions of thickness" },
					{ "Mundane Potions", "a Mundane Potion", "pots potions of mundaneness" },
					{ "Potions of Regeneration", "a Potion of Regeneration", "pots potions of regeneration" },
					{ "Potions of Swiftness", "a Potion of Swiftness", "pots potions of swiftness sprinting running fast" },
					{ "Potions of Fire Resistance", "a Potion of Fire Resistance",
							"pots potions of no burning fire resistance lava swimming fireproof inflammable inflammability" },
					{ "Potions of Poison", "a Potion of Poison", "pots potions of poison toxic toxins" },
					{ "Potions of Healing", "a Potion of Healing", "pots potions of instant healing instaheal instahealth" },
					{ "Potions of Night Vision", "a Potion of Night Vision", "pots potions of nightvision" },
					null,
					{ "Potions of Weakness", "a Potion of Weakness", "pots potions of weakness fraility frailness fragility fragile easily hurt" },
					{ "Potions of Strength", "a Potion of Strength", "pots potions of super strength strong extra damage sword superman superwoman supergirl wonderwoman" },
					{ "Potions of Slowness", "a Potion of Slowness", "pots potions of slowness sloth laziness slow down slowing" },
					null,
					{ "Potions of Harming", "a Potion of Harming", "pots potions of instaharming instahurting instapain instahealth loss" },
					null,
					{ "Potions of Invisibility", "a Potion of Invisibility", "pots potions of invisibility invisible no see clear hiding hidden pranking" },
					{ "Potions of Regeneration II", "a Potion of Regeneration II", "pots potions of regeneration II 2 two" },
					{ "Potions of Swiftness II", "a Potion of Swiftness II", "pots potions of swiftness sprinting running fast II 2 two" },
					null,
					{ "Potions of Poison II", "a Potion of Poison II", "pots potions of poison toxic toxins II 2 two" },
					{ "Potions of Healing II", "a Potion of Healing II", "pots potions of instant healing instaheal instahealth II 2 two" },
					{ "Potions of Strength II", "a Potion of Strength II",
							"pots potions of super strength strong extra damage sword superman superwoman supergirl wonderwoman II 2 two" },
					null,
					null,
					{ "Potions of Harming II", "a Potion of Harming II", "pots potions of instaharming instahurting instapain instahealth loss II 2 two" },
					{ "Potions of Regeneration (Extended)", "a Potion of Regeneration (Extended)", "pots potions of regeneration (Extended)" },
					{ "Potions of Swiftness (Extended)", "a Potion of Swiftness (Extended)", "pots potions of swiftness sprinting running fast (Extended)" },
					{ "Potions of Fire Resistance (Extended)", "a Potion of Fire Resistance (Extended)",
							"pots potions of no burning fire resistance lava swimming fireproof inflammable inflammability (Extended)" },
					{ "Potions of Poison (Extended)", "a Potion of Poison (Extended)", "pots potions of poison toxic toxins (Extended)" },
					null,
					{ "Potions of Night Vision (Extended)", "a Potion of Night Vision (Extended)", "pots potions of nightvision (Extended)" },
					null,
					{ "Potions of Weakness (Extended)", "a Potion of Weakness (Extended)",
							"pots potions of weakness fraility frailness fragility fragile easily hurt (Extended)" },
					{ "Potions of Strength (Extended)", "a Potion of Strength (Extended)",
							"pots potions of super strength strong extra damage sword superman superwoman supergirl wonderwoman (Extended)" },
					{ "Potions of Slowness (Extended)", "a Potion of Slowness (Extended)", "pots potions of slowness sloth laziness slow down slowing (Extended)" },
					{ "Potions of Invisibility (Extended)", "a Potion of Invisibility (Extended)",
							"pots potions of invisibility invisible no see clear hiding hidden pranking (Extended)" },
					{ "Splash Potions of Regeneration", "a Splash Potion of Regeneration", "pots splash thrown potions of regeneration" },
					{ "Splash Potions of Swiftness", "a Splash Potion of Swiftness", "pots splash thrown potions of swiftness sprinting running fast" },
					{ "Splash Potions of Fire Resistance", "a Splash Potion of Fire Resistance",
							"pots splash thrown potions of no burning fire resistance lava swimming fireproof inflammable inflammability" },
					{ "Splash Potions of Poison", "a Splash Potion of Poison", "pots splash thrown potions of poison toxic toxins" },
					{ "Splash Potions of Healing", "a Splash Potion of Healing", "pots splash thrown potions of instant healing instaheal instahealth" },
					{ "Splash Potions of Night Vision", "a Splash Potion of Night Vision", "pots splash thrown potions of nightvision" },
					null,
					{ "Splash Potions of Weakness", "a Splash Potion of Weakness", "pots splash thrown potions of weakness fraility frailness fragility fragile easily hurt" },
					{ "Splash Potions of Strength", "a Splash Potion of Strength",
							"pots splash thrown potions of super strength strong extra damage sword superman superwoman supergirl wonderwoman" },
					{ "Splash Potions of Slowness", "a Splash Potion of Slowness", "pots splash thrown potions of slowness sloth laziness slow down slowing" },
					null,
					{ "Splash Potions of Harming", "a Splash Potion of Harming", "pots splash thrown potions of instaharming instahurting instapain instahealth loss" },
					null,
					{ "Splash Potions of Invisibility", "a Splash Potion of Invisibility",
							"pots splash thrown potions of invisibility invisible no see clear hiding hidden pranking" },
					{ "Splash Potions of Regeneration II", "a Splash Potion of Regeneration II", "pots splash thrown potions of regeneration II 2 two" },
					{ "Splash Potions of Swiftness II", "a Splash Potion of Swiftness II", "pots splash thrown potions of swiftness sprinting running fast II 2 two" },
					null,
					{ "Splash Potions of Poison II", "a Splash Potion of Poison II", "pots splash thrown potions of poison toxic toxins II 2 two" },
					{ "Splash Potions of Healing II", "a Splash Potion of Healing II", "pots splash thrown potions of instant healing instaheal instahealth II 2 two" },
					{ "Splash Potions of Strength II", "a Splash Potion of Strength II",
							"pots splash thrown potions of super strength strong extra damage sword superman superwoman supergirl wonderwoman II 2 two" },
					null,
					null,
					{ "Splash Potions of Harming II", "a Splash Potion of Harming II",
							"pots splash thrown potions of instaharming instahurting instapain instahealth loss II 2 two" },
					{ "Splash Potions of Regeneration (Extended)", "a Splash Potion of Regeneration (Extended)", "pots splash thrown potions of regeneration (Extended)" },
					{ "Splash Potions of Swiftness (Extended)", "a Splash Potion of Swiftness (Extended)",
							"pots splash thrown potions of swiftness sprinting running fast (Extended)" },
					{ "Splash Potions of Fire Resistance (Extended)", "a Splash Potion of Fire Resistance (Extended)",
							"pots splash thrown potions of no burning fire resistance lava swimming fireproof inflammable inflammability (Extended)" },
					{ "Splash Potions of Poison (Extended)", "a Splash Potion of Poison (Extended)", "pots splash thrown potions of poison toxic toxins (Extended)" },
					null,
					{ "Splash Potions of Night Vision (Extended)", "a Splash Potion of Night Vision (Extended)", "pots splash thrown potions of nightvision (Extended)" },
					null,
					{ "Splash Potions of Weakness (Extended)", "a Splash Potion of Weakness (Extended)",
							"pots splash thrown potions of weakness fraility frailness fragility fragile easily hurt (Extended)" },
					{ "Splash Potions of Strength (Extended)", "a Splash Potion of Strength (Extended)",
							"pots splash thrown potions of super strength strong extra damage sword superman superwoman supergirl wonderwoman (Extended)" },
					{ "Splash Potions of Slowness (Extended)", "a Splash Potion of Slowness (Extended)",
							"pots splash thrown potions of slowness sloth laziness slow down slowing (Extended)" },
					{ "Splash Potions of Invisibility (Extended)", "a Splash Potion of Invisibility (Extended)",
							"pots splash thrown potions of invisibility invisible no see clear hiding hidden pranking (Extended)" } },
			{ { "glass bottles", "a glass bottle" } },
			{ { "spider eyes", "a spider eye" } },
			{ { "fermented spider eyes", "a fermented spider eye" } },
			{ { "Blaze powder", "some Blaze powder" } },
			{ { "Magma Cream", "some Magma Cream" } },
			{ { "brewing stands", "a brewing stand" } },
			{ { "cauldrons", "a cauldron", "kettles" } },
			{ { "Eyes of Ender", "an Eye of Ender", "Endereyes" } },
			{ { "glistening melon", "a glistening melon", "glistening melon slices", "gold melon slices", "golden melon slices", "shining melon slices" } },
			{ { "spawn eggs", "a spawn egg", "spawner spawning eggs" }, { "creeper spawn eggs", "a creeper spawn egg", "creeper spawner spawning eggs" },
					{ "skeleton spawn eggs", "a skeleton spawn egg", "skeleton spawner spawning eggs" },
					{ "spider spawn eggs", "a spider spawn egg", "spider spawner spawning eggs" }, null,
					{ "zombie spawn eggs", "a zombie spawn egg", "human regular old zombies spawner spawning eggs" },
					{ "slime spawn eggs", "a slime spawn egg", "slime spawner spawning eggs" }, { "Ghast spawn eggs", "a Ghast spawn egg", "Ghast spawner spawning eggs" },
					{ "zombie pigman spawn eggs", "a zombie pigman spawn egg", "zombie zombified pigman pigmen spawner spawning eggs" },
					{ "Enderman spawn eggs", "an Enderman spawn egg", "Enderman Endermen spawner spawning eggs" },
					{ "cave spider spawn eggs", "a cave spider spawn egg", "cave spiders spawner spawning eggs" },
					{ "silverfish spawn eggs", "a silverfish spawn egg", "silverfish spawner spawning eggs" },
					{ "Blaze spawn eggs", "a Blaze spawn egg", "Blazes spawner spawning eggs" },
					{ "Magma Cube spawn eggs", "a Magma Cube spawn egg", "Magma Cubes spawner spawning eggs" }, null, null,
					{ "bat spawn eggs", "a bat spawn egg", "bats spawner spawning eggs" }, { "witch spawn eggs", "a witch spawn egg", "witches spawner spawning eggs" },
					{ "pig spawn eggs", "a pig spawn egg", "pigs piggies spawner spawning eggs" },
					{ "sheep spawn eggs", "a sheep spawn egg", "sheeps spawner spawning eggs" },
					{ "cow spawn eggs", "a cow spawn egg", "cows calfs calves spawner spawning eggs" },
					{ "chicken spawn eggs", "a chicken spawn egg", "chickens chicks spawner spawning eggs" },
					{ "squid spawn eggs", "a squid spawn egg", "squids spawner spawning eggs" },
					{ "wolf spawn eggs", "a wolf spawn egg", "wolfs wolves dogs spawner spawning eggs" },
					{ "mooshroom spawn eggs", "a mooshroom spawn egg", "mooshrooms cows calfs calves spawner spawning eggs" }, null,
					{ "ocelot spawn eggs", "an ocelot spawn egg", "ocelots cats kitties kittens spawner spawning eggs" },
					{ "villager spawn eggs", "a villager spawn egg", "villagers N.P.C. Testificates spawner spawning eggs" } },
			{ { "Bottles o' Enchanting", "a Bottle o' Enchanting", "xp bottles", "exp bottles", "level botties", "experience bottles" } },
			{ { "fire charges", "a fire charge", "fireballs", "cannonballs", "Ghast cannonballs", "Blaze cannonballs", "Ghast fireballs", "Blaze fireballs" } },
			{ { "books and quills", "a book and quill", "book and quill" } },
			{ { "written-in books", "a written-in book", "novels", "texts" } },
			{ { "emeralds", "an emerald" } },
			{ { "item frames", "an item frame", "frames" } },
			{ { "flower pots", "a flower pot", "pots", "potted flowers plants" } },
			{ { "carrots", "a carrot" } },
			{ { "potatoes", "a potato", "raw potatoes" } },
			{ { "baked potatoes", "a baked potato", "cooked potatoes", "mashed potatoes" } },
			{ { "poisonous potatoes", "a poisonous potato", "poison potatoes", "bad potatoes" } },
			{ { "maps", "a map", "charts", "atlases" } },
			{ { "golden carrots", "a golden carrot", "gold carrots", "glistening carrots", "shiny carrots" } },
			{ { "monster heads", "a monster head", "heads" }, { "skeleton skulls", "a skeleton skull", "skeleton heads", "skele heads", "skele skulls" },
					{ "Wither skeleton skulls", "a Wither skeleton skull", "Wither skeleleton heads", "Wither skele skulls", "Wither skele heads" },
					{ "zombie heads", "a zombie head" }, { "Steve heads", "a Steve head", "Minecraft Steve heads", "guy heads", "man heads", "person heads", "human heads" },
					{ "creeper heads", "a creeper head" } },
			{ { "carrots on sticks", "a carrot on a stick", "carrots on fishing rods", "carrots on fishing poles", "pig controller" } },
			{ { "Nether Stars", "a Nether star" } }, { { "pumpkin pies", "a pumpkin pie" } }, { { "fireworks", "a firework", "firework rockets" } },
			{ { "firework stars", "a firework star", "firework color effect balls" } }, { { "enchanted books", "an enchanted book", "magic spellbooks" } },
			{ { "redstone comparators", "a redstone comparator", "redstone comparers" } },
			{ { "Nether bricks", "some Nether bricks", "individual single singular Nether bricks" } }, { { "Nether Quartz", "some Nether Quartz", "Nether gems" } },
			{ { "T.N.T. minecarts", "a T.N.T. minecart", "TNT trinitrotoluene minecarts" } }, { { "hopper minecarts", "a hopper minecart", "vacuum pickup minecarts" } },
			{ { "\"13\" music discs", "a \"13\" music disc", "\"13\" disks", "\"13\" records", "\"13\" CDs" } },
			{ { "\"cat\" music discs", "\"cat\" disks", "\"cat\" records", "\"cat\" CDs" } },
			{ { "\"blocks\" music discs", "a \"blocks\" music disc", "\"blocks\" disks", "\"blocks\" records", "\"blocks\" CDs" } },
			{ { "\"chirp\" music discs", "\"chirp\" disks", "\"chirp\" records", "\"chirp\" CDs" } },
			{ { "\"far\" music discs", "a \"chirp\" music disc", "\"far\" disks", "\"far\" records", "\"far\" CDs" } },
			{ { "\"mall\" music discs", "a \"mall\" music disc", "\"mall\" disks", "\"mall\" records", "\"mall\" CDs" } },
			{ { "\"mellohi\" music discs", "a \"mellohi\" music disc", "\"mellohi\" disks", "\"mellohi\" records", "\"mellohi\" CDs" } },
			{ { "\"stal\" music discs", "a \"stal\" music disc", "\"stal\" disks", "\"stal\" records", "\"stal\" CDs" } },
			{ { "\"strad\" music discs", "a \"strad\" music disc", "\"strad\" disks", "\"strad\" records", "\"strad\" CDs" } },
			{ { "\"ward\" music discs", "a \"ward\" music disc", "\"ward\" disks", "\"ward\" records", "\"ward\" CDs" } },
			{ { "\"11\" music discs", "an \"11\" music disc", "\"11\" disks", "\"11\" records", "\"11\" CDs" } },
			{ { "\"wait\" music discs", "\"wait\" disks", "\"wait\" records", "\"wait\" CDs" } } },
			entity_IDs = {
					null,
					{ { "dropped items", "a dropped item", "miniblocks", "miniitems", "floating dropped thrown chucked tossed stuff things miniblocks miniitems" } },
					{ { "experience orbs", "an experience orb", "experience balls" } },
					{ { "paintings", "a painting", "wall framed paintings" } },
					{ { "flying arrows", "a flying arrow", "shot fired arrows" } },
					{ { "thrown snowballs", "a thrown snowball", "chucked tossed thrown snowballs balls of snow" } },
					{ { "Ghast fireballs", "a Ghast fireball", "Ghast fired shot fireballs cannonballs explosive exploding fire charges" } },
					{ { "Blaze fireballs", "a Blaze fireball", "Blaze fired shot fireballs cannonballs explosive exploding fire charges" } },
					{ { "thrown Ender Pearls", "a thrown Ender Pearl", "Enderman Endermen thrown chucked fired Ender Pearls Enderpearls" } },
					{ { "thrown Eyes of Ender", "a thrown Eye of Ender", "Enderman Endermen thrown chucked fired Eyes of Ender Endereyes" } },
					{ { "thrown splash potions", "a thrown splash potion", "thrown chucked fired used potions pots" } },
					{ { "thrown Bottles o' Enchanting", "a thrown Bottle o' Enchanting", "xp bottle",
							"thrown chucked fired used Bottles glasses o' of Enchanting experience levels" } },
					{ { "item frames", "an item frame" } },
					{ { "Wither skull projectiles", "a Wither skull projectile", "Wither bosses boss's skulls projectiles fired heads explosives exploding" } },
					{ { "T.N.T.", "some T.N.T.", "primed T.N.T.", "lit T.N.T.", "activated T.N.T.", "primed TNT", "lit TNT", "activated TNT", "primed trinitrotoluene",
							"lit trinitrotoluene", "activated trinitrotoluene" } },
					{ { "falling blocks", "a falling block", "falling gravel", "falling sand", "falling anvils", "falling dragon eggs" } },
					{ { "fireworks", "a firework", "firework rockets" } },
					{ { "boats", "a boat", "dinghies", "dinghys", "ships" } },
					{ { "minecarts", "a minecart" } },
					{ { "storage minecarts", "a storage minecart", "minecrafts with storage", "minecarts with chests" } },
					{ { "powered minecarts", "a powered minecart", "gas powered minecarts", "coal powered minecarts", "furnace minecarts" } },
					{ { "T.N.T. minecarts", "a T.N.T. minecart", "explosive minecarts", "TNT minecarts", "trinitrotoluene minecarts" } },
					{ { "hopper minecarts", "a hopper minecart", "minecarts with hoppers", "vacuum minecarts" } },
					{ { "spawner minecarts", "a spawner minecart", "minecarts with spawners", "minecarts with monster spawners" } },
					{ { "generic mobs", "a generic mob", "mobs" } },
					{ { "generic monsters", "a generic monster", "monsters" } },
					{
							{ "creepers", "a creeper", "exploding green penis monsters", "explosive green penis monsters" },
							{ "non-charged creepers", "a non-charged creeper", "unpowered regular normal average run-of-the-mill exploding explosive green penis monsters" },
							{ "charged creepers", "a charged creeper", "lightninged struck exploding green penis monsters",
									"lightninged struck explosive green penis monsters" } },
					{ { "skeletons", "a skeleton", "skeles", "skeleton archers" }, { "skeletons", "a skeleton", "skeles", "skeleton archers" },
							{ "Wither skeletons", "a Wither skeleton", "Wither skeles", "Wither skeletons", "swordsman skeletons", "swordsman skeles" } },
					{ { "spiders", "a spider", "giant spiders" } },
					{ { "giants", "a giant", "giant zombies" } },
					{ { "zombies", "a zombie" } },
					{ { "slimes", "a slime", "slime cubes", "living slime" } },
					{ { "Ghasts", "a Ghast", "giant floating jellyfish", "giant flying jellyfish", "giant floating squids", "giant flying squids" } },
					{ { "zombie pigmen", "a zombie pigman", "zombie pigman", "pigman zombies" } },
					{ { "Endermen", "Enderman" } },
					{ { "cave spiders", "a cave spider", "small spiders", "poisonous spiders", "venomous spiders" } },
					{ { "silverfish", "a silverfish", "bugs", "stronghold silverfish", "stronghold bugs" } },
					{ { "Blazes", "a Blaze", "Nether dungeon guards", "Nether stronghold guards", "Nether dungeon monsters", "Nether stronghold monsters" } },
					{ { "Magma Cubes", "a Magma Cube", "lava cubes", "Nether slimes", "living magma", "living lava" } },
					{ { "The Ender Dragon", "the big black scary dragon" } },
					{ { "The Wither", "The Wither", "The Wither boss" } },
					{ { "bats", "a bat" } },
					{ { "witches", "a witch", "wicked witches" } },
					{ { "pigs", "a pig", "piggies", "piglets" } },
					{ { "sheep", "a sheep", "lambs" }, { "white sheep", "a white sheep", "white lambs" }, { "orange sheep", "an orange sheep", "orange lambs" },
							{ "magenta sheep", "a magenta sheep", "magenta lambs" }, { "light blue sheep", "a light blue sheep", "light blue lambs" },
							{ "yellow sheep", "a yellow sheep", "yellow lambs" }, { "lime green sheep", "a lime green sheep", "lime green lambs" },
							{ "pink sheep", "a pink sheep", "pink lambs" },
							{ "gray sheep", "a gray sheep", "dark gray sheep", "dark grey sheep", "dark gray lambs", "dark grey lambs" },
							{ "light gray sheep", "a light gray sheep", "light gray lambs" }, { "cyan sheep", "a cyan sheep", "cyan lambs" },
							{ "purple sheep", "a purple sheep", "purple lambs" }, { "blue sheep", "a blue sheep", "dark blue sheep", "dark blue lambs" },
							{ "brown sheep", "a brown sheep", "brown lambs" },
							{ "cactus green sheep", "a cactus green sheep", "dark green sheep", "cactus green lambs", "dark green lambs" },
							{ "red sheep", "a red sheep", "red lambs" }, { "black sheep", "a black sheep", "black lambs", "nun sheep" } },
					{ { "cows", "a cow" } },
					{ { "chickens", "a chicken", "chicks" } },
					{ { "squid", "a squid", "squids", "octopi", "octopuses" } },
					{ { "wolves", "a wolf", "dogs", "wolfs", "hounds" } },
					{ { "mooshrooms", "a mooshroom", "mooshroom cows" } },
					{ { "snow golems", "a snow golem", "living snowmen" } },
					{ { "ocelots", "an ocelot", "jungle cats" } },
					{ { "iron golems", "an iron golem", "N.P.C. village guards", "NPC village guards", "Testificates village guards" } },
					{
							{ "villagers", "a villager", "N.P.C.s", "NPCs", "Testificates" },
							{ "farmer villagers", "a farmer villager", "farmers villagers", "farmers N.P.C.s", "farmers NPCs", "farmers Testificates" },
							{ "librarian villagers", "a librarian villager", "librarians villagers", "librarians N.P.C.s", "librarians NPCs", "librarians Testificates" },
							{ "priest villagers", "a priest villager", "Minecraft priests villagers", "Minecraft priests N.P.C.s", "Minecraft priests NPCs",
									"Minecraft priests Testificates" },
							{ "butcher villagers", "a butcher villager", "butchers villagers", "butcher N.P.C.s", "butcher NPCs", "butcher testificates" },
							{ "blacksmith villagers", "a blacksmith villager", "blacksmiths villagers", "blacksmiths N.P.C.s", "blacksmiths NPCs", "blacksmiths Testificates" },
							{ "zombie villagers", "a zombie villager", "zombies villagers", "zombies N.P.C.s", "zombies NPCs", "zombies Testificates", "zombified villagers",
									"zombified N.P.C.s", "zombified NPCs", "zombified Testificates" } },
					{ { "Ender crystals", "an Ender crystal", "Ender Dragon shield generators" } } };
	public static final String[][] recipes = {
			null,
			null,
			null,
			null,
			{ "Just craft any kind of logs. &b(x4)", "Just craft some oak logs. &b(x4)", "Just craft some spruce logs. &b(x4)", "Just craft some birch logs. &b(x4)",
					"Just craft some jungle logs. &b(x4)" },
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			{ "Melt sand in a furnace." },
			null,
			{ "&1lapis lazuli\nLLL\nLLL\nLLL" },
			{ "&8&lcobblestone&f, &e&m&obow, &4&oredstone dust\n&8&oCCC\nC&e&m&oB&8&oC\nC&4&oR&8&oC" },
			{ "&esand\n&0---\n-&eSS\n&0-&eSS", "&esand\n&0---\n-&eSS\n&0-&eSS", "&e&nsandstone slabs\n&0---\n-&e&nS&0-\n-&e&nS&0-",
					"&e&nsandstone\n&0---\n-&e&nS&0-&bx2\n&0-&e&nS&0-" },
			{ "&e&m&nwooden planks&f, &4&oredstone dust\n&e&m&nWWW\nW&4&oR&e&m&nW\nWWW" },
			{ "&f&lwool, &e&m&nwooden planks\n&0---\n&f&lWWW\n&e&m&nWWW" },
			{ "&6gold ingots&f, &e&msticks&f, &4&oredstone dust\n&6G&0-&6G\nG&f&e&mS&6G&bx6\n&6G&4&oR&6G" },
			{ "&7iron ingots&f, &8&nstone pressure plate&f, &4&oredstone dust\n&7I &0-&7I \nI &8&nS&7I\nI &4&oR&7I" },
			{ "&aslimeballs&f, &e&npistons\n&0---\n-&aS&0-\n-&e&nP&0-" },
			null,
			null,
			null,
			{ "&e&m&nwooden planks&f, &8&lcobblestone&f, &7iron ingots&f, &4&oredstone dust\n&e&m&nWWW\n&8&oC&7I &8&oC\nC&4&oR&8&oC" },
			null,
			{ "string\n&0---\n-&fSS\n&0-&fSS", "string\n&0---\n-&fSS\n&0-&fSS", "&f&lwhite wool &f(not colored wool), &6orange dye\n&0---\n-&f&lW&0-\n-&6O&0-",
					"&f&lwhite wool &f(not colored wool), &dmagenta dye\n&0---\n-&f&lW&0-\n-&dM&0-",
					"&f&lwhite wool &f(not colored wool), &blight blue dye\n&0---\n-&f&lW&0-\n-&6L&0-",
					"&f&lwhite wool &f(not colored wool), &edandelion yellow dye\n&0---\n-&f&lW&0-\n-&eD&0-",
					"&f&lwhite wool &f(not colored wool), &alime green dye\n&0---\n-&f&lW&0-\n-&aL&0-",
					"&f&lwhite wool &f(not colored wool), &dpink dye\n&0---\n-&f&lW&0-\n-&dP&0-",
					"&f&lwhite wool &f(not colored wool), &8gray dye\n&0---\n-&f&lW&0-\n-&8G&0-",
					"&f&lwhite wool &f(not colored wool), &7light gray dye\n&0---\n-&f&lW&0-\n-&7L&0-",
					"&f&lwhite wool &f(not colored wool), &9cyan dye\n&0---\n-&f&lW&0-\n-&9C&0-",
					"&f&lwhite wool &f(not colored wool), &5purple dye\n&0---\n-&f&lW&0-\n-&5P&0-",
					"&f&lwhite wool &f(not colored wool), &1lapis lazuli\n&0---\n-&f&lW&0-\n-&1L&0-",
					"&f&lwhite wool &f(not colored wool), &ccocoa beans\n&0---\n-&f&lW&0-\n-&cC&0-",
					"&f&lwhite wool &f(not colored wool), &2cactus green dye\n&0---\n-&f&lW&0-\n-&2C&0-",
					"&f&lwhite wool &f(not colored wool), &4rose red dye\n&0---\n-&f&lW&0-\n-&4R&0-",
					"&f&lwhite wool &f(not colored wool), &0ink sack\n&0---\n-&f&lW&0-\n-&0I &0-" },
			null,
			null,
			null,
			null,
			null,
			{ "&6gold ingots\nGGG\nGGG\nGGG" },
			{ "&7iron ingots\nI I I\nI I I\nI I I" },
			null,
			{ "&5&kM &fcan be cobblestone, stone, stone bricks, wooden planks, bricks, or sandstone\n&0---\n---&bx6\n&5&kMMM", "&8stone\n&0---\n---&bx6\n&8SSS",
					"&e&nsandstone\n&0---\n---&bx6\n&8SSS", "&e&m&nwooden planks\n&0---\n---&bx6\n&e&m&nWWW", "&8&lcobblestone\n&0---\n---&bx6\n&8&lCCC",
					"&c&nbrick blocks\n&0---\n---&bx6\n&c&nBBB", "&8&m&n\n&0---\n---&bx6\n&8&m&nSSS", "&4&m&nNether brick blocks\n&0---\n---&bx6\n&4&m&nNNN",
					"&f&nNether Quartz blocks\n&0---\n---&bx6\n&f&nNNN" },
			{ "&cbricks\n&0---\n-&cBB\n&0-&cBB" },
			{ "&esand&f, &8&ogunpowder\n&8&oG&eS&8&oG\n&eS&8&oG&eS\n&8&oG&eS&8&oG" },
			{ "&e&m&nwooden planks&f, &c&obooks\n&e&m&nWWW\n&c&oBBB\n&e&m&nWWW" },
			null,
			null,
			{ "&0coal or charcoal, &e&msticks\n&0---\n-&0C&0-&bx4\n-&e&mS&0-" },
			null,
			null,
			{ "&e&m&noak wood planks\n&0--&e&m&nW\n&0-&e&m&nWW&bx4\n&e&m&nWWW" },
			{ "&e&m&nwooden planks\nWWW\nW&0-&e&m&nW\nWWW" },
			null,
			null,
			{ "&bdiamonds\nDDD\nDDD\nDDD" },
			{ "&e&m&nwooden planks\n&0---\n-&e&m&nWW\n&0-&e&m&nWW" },
			null,
			null,
			{ "&8&lcobblestone\nCCC\nC&0-&8&oC\nCCC" },
			null,
			{ "&e&m&nwooden planks&f, &e&msticks\n&nWWW\nWWW&bx3\n&0-&e&mS&0-" },
			{ "&e&m&nwooden planks\n&0-&e&m&nWW\n&0-&e&m&nWW\n&0-&e&m&nWW" },
			{ "&e&msticks\nS&0-&e&mS\nSSS&bx3\n&e&mS&0-&e&mS" },
			{ "&7iron ingots&f, &e&msticks\n&7I &0-&7I\nI &e&mS&7I\nI &0- &7I" },
			{ "&8&lcobblestone\n&0--&8&oC\n&0-&8&oCC&bx4\n&8&oCCC" },
			{ "&e&m&nwooden planks&f, &e&msticks\n&nWWW\nWWW&bx3\n&0-&e&mS&0-" },
			{ "&e&msticks&f, &8&lcobblestone\n&0---\n-&e&mS&0-\n-&8&oC&0-" },
			{ "&8stone\n&0---\n---\n-&8SS" },
			{ "&7iron ingots\n&0-&7I I\n&0-&7I I\n&0-&7I I" },
			{ "&e&m&nwooden planks\n&0---\n---\n-&e&m&nWW" },
			null,
			null,
			{ "&4&oredstone dust&f, &e&msticks\n&0---\n-&4&oR&0-\n-&e&mS&0-" },
			null,
			{ "&8stone\n&0---\n-&8S&0-\n---" },
			null,
			null,
			{ "&fsnowballs\n&0---\n-&fSS\n&0-&fSS" },
			null,
			{ "&7&oclay\n&0---\n-&7&oCC\n&0-&7&oCC" },
			null,
			{ "&e&m&nwooden planks&f, &bdiamonds\n&e&m&nWWW\n&e&m&nW&bD&e&m&nW\n&e&m&nWWW" },
			{ "&e&msticks\n&0---\n&e&mSSS&bx2\n&e&m&nSSS" },
			null,
			null,
			null,
			{ "&6&oglowstone dust\n&0---\n-&6&oGG\n&0-&6&oGG" },
			null,
			{ "&6pumpkins&f, &mtorches\n&0---\n-&6P&0-\n-&f&mT&0-" },
			{ "&fmilk&f, &f&osugar&f, &eeggs&f, &e&m&n&owheat\n&fMMM\n&f&oS&eE&f&oS\n&e&m&n&oWWW" },
			{ "&4&oredstone dust&f, &4&mredstone torches&f, &8stone\n&0---\n&4&mR&4&oR&mR\n&8SSS" },
			null,
			null,
			{ "&e&m&nwooden planks\n&0---\n&e&m&nWWW&bx2\n&e&m&nWWW" },
			null,
			{ "&8stone\n&0---\n-&8SS\n&0-&8SS", "&8stone\n&0---\n-&8SS\n&0-&8SS", null, null, null },
			null,
			null,
			{ "&7iron ingots\n&0---\n&7I I I\nI I I" },
			{ "&fglass\n&0---\n&fGGG&bx16\n&fGGG" },
			null,
			null,
			null,
			null,
			{ "&e&msticks&f, &e&m&nwooden planks\n&0---\n&e&mS&nW&e&mS\nS&nW&e&m&nS" },
			{ "&c&nbrick blocks\n&0--&c&nB\n&0-&c&nBB\nBBB" },
			{ "&8&m&nstone bricks\n&0--&8&m&nS\n&0-&8&m&nSS\nSSS" },
			null,
			null,
			null,
			{ "&4&m&nNether brick blocks\n&0---\n&4&m&nNNN\nNNN" },
			{ "&4&m&nNether brick blocks\n&0--&4&m&nN\n&0-&4&m&nNN\nNNN" },
			null,
			{ "&c&obooks&f, &l&nobsidian&f, &bdiamonds\n&0-&c&oB&0-\n&bD&0&l&nO&bD\n&0&l&nOOO" },
			{ "&8&ocobbestone&f, &6&mBlaze rods\n&0---\n-&6&mB&0-\n&8&oCCC" },
			{ "&7iron ingots\nI &0-&7I\nI &0-&7I\nI I I" },
			null,
			null,
			null,
			null,
			{ "&6&nglowstone blocks&f, &4&oredstone dust\n&0-&4&oR&0-\n&4&oR&6&nG&4&oR\n&0-&4&oR&0-" },
			null,
			null,
			{ "&e&m&nwooden planks\n&0---\n---&bx6\n&e&m&nWWW", "&e&m&noak wooden planks\n&0---\n---&bx6\n&e&m&nWWW", "&e&m&nspruce wood planks\n&0---\n---&bx6\n&e&m&nWWW",
					"&e&m&nbirch wood planks\n&0---\n---&bx6\n&e&m&nWWW", "&e&m&njungle wood planks\n&0---\n---&bx6\n&e&m&nWWW" },
			null,
			{ "&e&nsandstone\n&0--&e&nS\n&0-&e&nSS&bx4\n&e&nSSS" },
			null,
			{ "&a&l&oEyes of Ender&f, &0&l&nobsidian\nOOO\nO&a&l&oE&0&l&nO\nOOO" },
			{ "&e&m&nwooden planks&f, &e&msticks&f, &7iron ingots\n&0-&7I &0-\n-&e&mS&0-&bx2\n&0-&e&m&nW&0-" },
			null,
			{ "&aemeralds\nEEE\nEEE\nEEE" },
			{ "&e&m&nspruce wood planks\n&0--&e&m&nW\n&0-&e&m&nWW&bx4\n&e&m&nWWW" },
			{ "&e&m&nbirch wood planks\n&0--&e&m&nW\n&0-&e&m&nWW&bx4\n&e&m&nWWW" },
			{ "&e&m&njungle wood planks\n&0--&e&m&nW\n&0-&e&m&nWW&bx4\n&e&m&nWWW" },
			null,
			{ "&b&lNether stars&f, glass, &0&l&nobsidian\n&fGGG\nG&b&lN&fG\n&0&lOOO" },
			{ "&8&lcobblestone\n&0---\n&8&lCCC&bx6\n&8&lCCC", "&8&lcobblestone\n&0---\n&8&lCCC&bx6\n&8&lCCC", "&8&l&omossy cobblestone\n&0---\n&8&l&oCCC&bx6\n&8&l&oCCC" },
			{ "&cbricks\n&0---\n&cB&0-&cB\n&0-&cB&0-" },
			null,
			null,
			{ "&e&m&nwooden planks\n&0---\n-&e&m&nW&0-\n---" },
			null,
			{ "&7&niron blocks&f, &7iron ingots\n&7&nI I I\n&0-&7I &0-\nI I I" },
			{ "&e&m&nchests&f, &e&mtripwire hooks\n&0---\n&0-&e&m&nC&0-\n-&e&mT&0-" },
			{ "&6gold ingots\n&0---\n---\n-&6GG" },
			{ "&7iron ingots\n&0---\n---\n-&7I I" },
			{ "&8stone&f, &4&mredstone torches&f, Nether Quartz\n&0-&4&mR&0-\n&4&mR&fN&4&mR\n&8SSS" },
			null,
			{ "&fglass, Nether Quartz, &e&m&nwooden plank slabs\n&fGGG\nNNN\n&e&m&nWWW" },
			null,
			{ "&4&oredstone dust\nRRR\nRRR\nRRR" },
			null,
			{ "&7iron ingots&f, &e&m&nchest\n&7I &0-&7I\nI &e&m&nC&7I\n&0-&7I &0-" },
			{ "&fNether Quartz\n&0---\n-&fNN\n&0-&fNN", "&fNether Quartz\n&0---\n-&fNN\n&0-&fNN", "&f&nNether Quartz slabs\n&0---\n-&f&nN&0-\n-&f&nN&0-",
					"&f&nNether Quartz blocks\n&0---\n-&f&nN&0-&bx2\n&0-&f&nN&0-" },
			{ "&f&nNether Quartz blocks\n&0--&f&nN\n&0-&f&nNN\nNNN" },
			{ "&7iron ingots&f, &e&msticks&f, &4&mredstone torches\n&7I &e&mS&7I\nI &4&mR&7I\nI &e&mS&7I" },
			{ "&8&lcobbestone&f, &4&oredstone dust&f, &8hopper\n&8&lCCC\nC&8H&lC\nC&4&oR&8&lC" },// block I.D.s --> item I.D.s
			{ "&7iron ingots&f, &e&msticks\n&0-&7I &0-\n-&e&mS&0-\n-&e&mS&0-" }, { "&7iron ingots&f, &e&msticks\n&7I I I\n&0-&e&mS&0-\n-&e&mS&0-" },
			{ "&7iron ingots&f, &e&msticks\n&7I I I\n&0-&e&mS&0-\n&0-&e&mS&0-" } };
	// there gap arrays are here to compensate for the gaps in I.D.s; for example, from the block I.D.s to the item I.D.s (starting at item #159 in this list
	// since 158 is the last block I.D.), there is a 98-number gap.
	// there are two numbers in each item in this list: the item I.D. of the last item before the gap and the item I.D. of the first item after the gap

	// TODO: finish the recipes
	// the point of this is to avoid what I had originally, which was just an insanely long list of null values in item_IDs itself
	private static int[] must_be_attached_bottom_only_IDs = { 6, 26, 27, 28, 31, 32, 37, 38, 39, 40, 55, 59, 63, 64, 66, 70, 71, 72, 78, 81, 83, 93, 94, 104, 105, 111, 115,
			132, 140, 147, 148, 149, 150, 157 }, must_be_attached_can_be_sideways_IDs = { 50, 65, 68, 69, 75, 76, 77, 96, 106, 127, 131 };
	private static final short[][] item_gaps = { { 158, 256 }, { 408, 2256 } }, entity_gaps = { { 2, 9 }, { 22, 41 }, { 66, 90 }, { 99, 120 }, { 120, 200 } },
			potion_data_gaps = { { 0, 16 }, { 16, 32 }, { 32, 64 }, { 64, 8193 }, { 8206, 8225 }, { 8229, 8233 }, { 8236, 8257 }, { 8266, 8270 }, { 8270, 16385 },
					{ 16398, 16417 }, { 16421, 16425 }, { 16428, 16449 }, { 16458, 16462 } }, spawn_egg_data_gaps = { { -1, 50 }, { 66, 90 }, { 98, 120 } };

	// working methods
	/**
	 * This method returns a two-item Integer array or <b>null</b>. <tt>[0]</tt> is the I.D. of the item given by <tt>item_name</tt>. <tt>[1]</tt> is the data
	 * value of the item given, e.g. 2 for birch wood (because all logs have the I.D. 17, but a data value of 2 refers to birch wood specifically). If
	 * <tt><b>item_name</b></tt> specifies a general item name such as "logs", the data value returned will be -1.
	 * 
	 * @param item_name
	 *            is the name of the item or block type that was specified split into separate words.
	 * @param item_ID
	 *            is <b>true</b> if this method should only return item I.D.s and not block type I.D.s, <b>false</b> if this method should only return block
	 *            type I.D.s and not item I.D.s, or <b>null</b> if it should return either item I.D.s or block type I.D.s, in which case it will proritize item
	 *            I.D.s over block type I.D.s.
	 * @return the I.D. and numerical data value for the item given by name in <tt><b>item_name</tt></b> in a two-item Integer array or <tt><b>null</b></tt> if
	 *         <b>1)</b> the item specified does not exist or <b>2)</b> the object specified is an item, not a block type, and it was specified in
	 *         <tt><b>item_ID</b></tt> that this method should only return block types or vice versa.
	 * @NOTE This method returns both the I.D. and the data value of an item based on the item's name because it encourages the programmer to only use this
	 *       method once as necessary, not once to get the I.D. and again to get the data. It is a long, somewhat complex method and it must search through
	 *       hundreds and hundreds of Strings in the <tt>item_IDs</tt> array to find a match. This method should only be called when necessary and results
	 *       returned by this method should be stored in a variable if needed more than once; do not simply call this method a second time.
	 * @see {@link #getItemIdAndData(String, Boolean) getItemIdAndData(String, Boolean)}, {@link #getItemIdAndDataString(String[], Boolean)
	 *      getItemIdAndDataString(String[], Boolean)}, and {@link #getItemIdAndDataString(String, Boolean) getItemIdAndDataString(String, Boolean)}
	 */
	public static Integer[] getItemIdAndData(String[] item_name, Boolean item_ID) {
		Integer result_id = null, result_data = null, result_i = null;
		int start_id = 0;
		// if item_ID is true, we only want item I.D.s, so start searching at item I.D.s
		if (item_ID != null && item_ID)
			start_id = item_gaps[0][0] + 1;
		for (int check_id = start_id; check_id < item_IDs.length; check_id++) {
			if (item_IDs[check_id] != null)
				for (int check_data = 0; check_data < item_IDs[check_id].length; check_data++)
					if (item_IDs[check_id][check_data] != null)
						for (int i = 0; i < item_IDs[check_id][check_data].length; i++) {
							boolean contains_query = true;
							for (String word : item_name)
								// if word starts and ends with parentheses, it's a data suffix, so ignore it in the search; also ignore articles
								if (!(word.startsWith("(") && word.endsWith(")")) && !word.equalsIgnoreCase("a") && !word.equalsIgnoreCase("an")
										&& !word.equalsIgnoreCase("the") && !word.equalsIgnoreCase("some")
										&& !item_IDs[check_id][check_data][i].toLowerCase().contains(word.toLowerCase())) {
									contains_query = false;
									break;
								}
							// translation of this if statement: if the item contains the query and either we haven't found another result yet, the old result
							// has a longer name than this new one, or the length of the names is the same but this new result is an item I.D. while the old one
							// is a block I.D. and item_ID is null, then change the current result to this new item
							if (contains_query
									&& (result_id == null || item_IDs[result_id][result_data][result_i].length() > item_IDs[check_id][check_data][i].length() || (item_IDs[result_id][result_data][result_i]
											.equals(item_IDs[check_id][check_data][i])
											&& item_ID == null && result_id < check_id))) {
								result_id = check_id;
								result_data = check_data;
								result_i = i;
							}
						}
			// if item_IDs is false, we don't want item I.D.s, so stop checking after we have checked the first part, the block I.D.s
			if (check_id > item_gaps[0][0] && item_ID != null && !item_ID)
				break;
		}
		// if we returned no results, it's possible that the object was "something with the I.D. [id](":"[data])"
		if (result_id == null)
			if (item_name[0].equalsIgnoreCase("something") && item_name[1].equalsIgnoreCase("with") && item_name[2].equalsIgnoreCase("the")
					&& item_name[3].equalsIgnoreCase("I.D."))
				try {
					// try reading it as "something with the I.D. [id]"
					result_id = Integer.parseInt(item_name[4]);
					result_data = -1;
					return new Integer[] { result_id, result_data };
				} catch (NumberFormatException exception) {
					try {
						// try reading it as "something with the I.D. [id]:[data]"
						String[] id_and_data = item_name[4].split(":");
						if (id_and_data.length != 2) {
							myGuardDog.console.sendMessage(ChatColor.DARK_RED + "Aww! Something went wrong! I couldn't get the I.D. and data from this object name.");
							String item = "";
							for (String word : item_name)
								item += word + " ";
							// the substring() here eliminates the extra space at the end
							myGuardDog.console.sendMessage(ChatColor.WHITE + "\"" + item.substring(0, item.length() - 1) + "\"");
							return null;
						}
						result_id = Integer.parseInt(id_and_data[0]);
						result_data = Integer.parseInt(id_and_data[1]);
						return new Integer[] { result_id, result_data };
					} catch (NumberFormatException exception2) {
						myGuardDog.console.sendMessage(ChatColor.DARK_RED + "Darn! Something went wrong! I couldn't get the I.D. and data from this object name.");
						String item = "";
						for (String word : item_name)
							item += word + " ";
						// the substring() here eliminates the extra space at the end
						myGuardDog.console.sendMessage(ChatColor.WHITE + "\"" + item.substring(0, item.length() - 1) + "\"");
						return null;
					}
				}
			else
				return null;
		else {
			// subtract 1 from the data to get the real data (remember: [0] is the general name and [1] is data = 0)
			result_data -= 1;
			// now we need to adjust the final result based on the gaps in I.D.s
			// for the I.D. gaps
			for (short[] gap : item_gaps)
				if (result_id > gap[0])
					result_id += (gap[1] - gap[0] - 1);
				else
					break;
			// if the item name contained a data suffix, read the data suffix to get the real data value
			if (item_name.length > 1 && item_name[item_name.length - 1].startsWith("(") && item_name[item_name.length - 1].endsWith(")"))
				try {
					result_data = Integer.parseInt(item_name[item_name.length - 1].substring(1, item_name[item_name.length - 1].length() - 1));
				} catch (NumberFormatException exception) {
					myGuardDog.console.sendMessage(ChatColor.DARK_RED + "Oh, nos! I got an error trying to read the data suffix on this item name!");
					String item = "";
					for (String word : item_name)
						item += word + " ";
					// the substring() here eliminates the extra space at the end
					myGuardDog.console.sendMessage(ChatColor.WHITE + "\"" + item.substring(0, item.length() - 1) + "\"");
					myGuardDog.console.sendMessage(ChatColor.DARK_RED + "I read " + ChatColor.WHITE + "\""
							+ item_name[item_name.length - 1].substring(1, item_name[item_name.length - 1].length() - 1) + "\"" + ChatColor.DARK_RED
							+ " as the data value in the data suffix.");
					exception.printStackTrace();
				}
			// only adjust the result data if there was no data suffix to get the true data from
			else {
				// for the potion data values gaps
				if (result_id == 373)
					for (short[] gap : potion_data_gaps)
						if (result_data > gap[0])
							result_data += (gap[1] - gap[0] - 1);
						else
							break;
				// for the spawn egg data values gaps
				else if (result_id == 383)
					for (short[] gap : spawn_egg_data_gaps)
						if (result_data > gap[0])
							result_data += (gap[1] - gap[0] - 1);
						else
							break;
			}
		}
		return new Integer[] { result_id, result_data };
	}

	/**
	 * This method returns the name of the item specified by the item or block type I.D. and data given.
	 * 
	 * @param id
	 *            is the item or block type I.D.
	 * @param data
	 *            is the numerical data value for the item or block.
	 * @param give_data_suffix
	 *            specifies whether or not the name of the item should include the numerical data value at the end of the item name in parentheses (e.g.
	 *            "a trapdoor <b>(16)</b>"). For logging purposes in myGuardDog, for example, we should be as specific as possible on information about the
	 *            item, so this argument should be <b>true</b>. However, for messages to users for commands like <i>/id</i>, the data suffix is not helpful and
	 *            looks awkward, so this argument should be <b>false</b>.
	 * @param singular
	 *            specifies whether the item name returned should be returned in the singular form (e.g. "a lever") or in the plural form (e.g. "levers").
	 *            Singular forms include an article at the beginning. Non-countable items like grass are the same as their plural forms, but with "some" at the
	 *            beginning ("grass" --> "some grass").
	 * @return the name of the item specified by the item or block type I.D. and data given.
	 * @see {@link #getItemName(Block, boolean, boolean) getItemName(Block, boolean, boolean)} and {@link #getItemName(ItemStack, boolean, boolean)
	 *      getItemName(ItemStack, boolean, boolean)}
	 */
	public static String getItemName(int id, int data, boolean give_data_suffix, boolean singular) {
		// return null if the potion data is inside a gap
		if (id == 373)
			for (short[] gap : potion_data_gaps)
				if (data > gap[0] && data < gap[1])
					return null;
		// return null if the spawn egg data is inside a gap
		if (id == 383)
			for (short[] gap : spawn_egg_data_gaps)
				if (data > gap[0] && data < gap[1])
					return null;
		// return null if the I.D. is inside a gap
		for (short[] gap : item_gaps)
			if (id > gap[0] && id < gap[1])
				return null;
		// we need to adjust the query I.D.s based on the gaps in I.D.s for the potion data values gaps
		if (id == 373)
			for (int i = potion_data_gaps.length - 1; i >= 0; i--)
				if (data >= potion_data_gaps[i][1])
					data -= (potion_data_gaps[i][1] - potion_data_gaps[i][0] - 1);
		// for the spawn egg data values gaps
		if (id == 383)
			for (int i = spawn_egg_data_gaps.length - 1; i >= 0; i--)
				if (data >= spawn_egg_data_gaps[i][1])
					data -= (spawn_egg_data_gaps[i][1] - spawn_egg_data_gaps[i][0] - 1);
		// for the item gaps
		for (int i = item_gaps.length - 1; i >= 0; i--)
			if (id >= item_gaps[i][1])
				id -= (item_gaps[i][1] - item_gaps[i][0] - 1);
		int sing_plur = 0;
		if (singular)
			sing_plur = 1;
		String item = null;
		// the Exceptions in this block of code can be ArrayIndexOutOfBoundsExceptions or NullPointerExceptions
		try {
			// try using the data and I.D. given
			item = item_IDs[id][data + 1][sing_plur];
		} catch (ArrayIndexOutOfBoundsException exception) {
			try {
				// if that doesn't work, try substracting 4 from the data until we can't any more and try again
				item = item_IDs[id][data % 4 + 1][sing_plur];
				if (item != null && give_data_suffix && data > 0)
					item += " (" + data + ")";
			} catch (Exception exception2) {
				try {
					// if that doesn't work, try giving the general name for the item with the I.D.
					item = item_IDs[id][0][sing_plur];
					if (item != null && give_data_suffix && data > 0)
						item += " (" + data + ")";
				} catch (Exception exception3) {
				}
			}
		} catch (NullPointerException exception) {
		}
		return item;
	}

	/**
	 * This method returns a two-item Integer array or <b>null</b>. <tt>[0]</tt> is the I.D. of the entity given by <tt>entity_name</tt>. <tt>[1]</tt> is the
	 * data value of the item given, e.g. 1 for charged creepers (because all creepers have the I.D. 50, but a data value of 1 refers to charged creepers
	 * specifically). If <tt><b>entity_name</b></tt> specifies a general item name such as "creepers", the data value returned will be -1.
	 * 
	 * @param entity_name
	 *            is <b>true</b> if this method should only return item I.D.s and not block type I.D.s, <b>false</b> if this method should only return block
	 *            type I.D.s and not item I.D.s, or <b>null</b> if it should return either item I.D.s or block type I.D.s, in which case it will proritize item
	 *            I.D.s over block type I.D.s.
	 * @return the I.D. and numerical data value for the item given by name in <tt><b>entity_name</tt></b> in a two-item Integer array or <tt><b>null</b></tt>
	 *         if the item specified does not exist.
	 * @see {@link #getEntityIdAndData(String) getEntityIdAndData(String)}, {@link #getEntityIdAndDataString(String[]) getEntityIdAndDataString(String[])}, and
	 *      {@link #getEntityIdAndDataString(String) getEntityIdAndDataString(String)}
	 * @NOTE This method returns both the I.D. and the data value of an entity based on the entity's name because it encourages the programmer to only use this
	 *       method once as necessary, not once to get the I.D. and again to get the data. It is a long, somewhat complex method and it must search through
	 *       hundreds of Strings in the <tt>entity_IDs</tt> array to find a match. This method should only be called when necessary and results returned by this
	 *       method should be stored in a variable if needed more than once; do not simply call this method a second time.
	 */
	public static Integer[] getEntityIdAndData(String[] entity_name) {
		Integer result_id = null, result_data = null, result_i = null;
		for (int id = 0; id < entity_IDs.length; id++)
			if (entity_IDs[id] != null)
				for (int data = 0; data < entity_IDs[id].length; data++)
					if (entity_IDs[id][data] != null)
						for (int i = 0; i < entity_IDs[id][data].length; i++) {
							boolean contains_query = true;
							for (String word : entity_name)
								// if word starts and ends with parentheses, it's a data suffix, so ignore it in the search; also ignore articles
								if (!(word.startsWith("(") && word.endsWith(")")) && !word.equalsIgnoreCase("a") && !word.equalsIgnoreCase("an")
										&& !word.equalsIgnoreCase("the") && !word.equalsIgnoreCase("some")
										&& !entity_IDs[id][data][i].toLowerCase().contains(word.toLowerCase())) {
									contains_query = false;
									break;
								}
							// translation of this if statement: if the entity contains the query and either we haven't found another result yet, the old result
							// has a longer name than this new one, or the length of the names is the same but this new result is an entity I.D. while the old
							// one is a block I.D., then change the current result to this new entity
							if (contains_query
									&& (result_id == null || entity_IDs[result_id][result_data][result_i].length() > entity_IDs[id][data][i].length() || (entity_IDs[result_id][result_data][result_i]
											.length() == entity_IDs[id][data][i].length()
											&& result_id < 256 && id >= 256))) {
								result_id = id;
								result_data = data;
								result_i = i;
							}
						}
		if (result_id == null)
			if (entity_name[0].equalsIgnoreCase("something") && entity_name[1].equalsIgnoreCase("with") && entity_name[2].equalsIgnoreCase("the")
					&& entity_name[3].equalsIgnoreCase("I.D."))
				try {
					// try reading it as "something with the I.D. [id]"
					result_id = Integer.parseInt(entity_name[4]);
					result_data = 0;
					return new Integer[] { result_id, result_data };
				} catch (NumberFormatException exception) {
					try {
						// try reading it as "something with the I.D. [id]:[data]"
						String[] id_and_data = entity_name[4].split(":");
						if (id_and_data.length != 2) {
							myGuardDog.console.sendMessage(ChatColor.DARK_RED + "Aww! Something went wrong! I couldn't get the I.D. and data from this object name.");
							String entity = "";
							for (String word : entity_name)
								entity += word + " ";
							// the substring() here eliminates the extra space at the end
							myGuardDog.console.sendMessage(ChatColor.WHITE + "\"" + entity.substring(0, entity.length() - 1) + "\"");
							return null;
						}
						result_id = Integer.parseInt(id_and_data[0]);
						result_data = Integer.parseInt(id_and_data[1]);
						return new Integer[] { result_id, result_data };
					} catch (NumberFormatException exception2) {
						myGuardDog.console.sendMessage(ChatColor.DARK_RED + "Darn! Something went wrong! I couldn't get the I.D. and data from this object name.");
						String entity = "";
						for (String word : entity_name)
							entity += word + " ";
						// the substring() here eliminates the extra space at the end
						myGuardDog.console.sendMessage(ChatColor.WHITE + "\"" + entity.substring(0, entity.length() - 1) + "\"");
						return null;
					}
				}
			else
				return null;
		else {
			// subtract 1 from the data to get the real data (remember: [0] is the general name and [1] is data = 0)
			result_data -= 1;
			// if the entity name contained a data suffix, read the data suffix to get the real data value
			if (entity_name.length > 1 && entity_name[entity_name.length - 1].startsWith("(") && entity_name[entity_name.length - 1].endsWith(")"))
				try {
					result_data = Integer.parseInt(entity_name[entity_name.length - 1].substring(1, entity_name[entity_name.length - 1].length() - 1));
				} catch (NumberFormatException exception) {
					myGuardDog.console.sendMessage(ChatColor.DARK_RED + "Oh, nos! I got an error trying to read the data suffix on this item name!");
					String entity = "";
					for (String word : entity_name)
						entity += word + " ";
					// the substring() here eliminates the extra space at the end
					myGuardDog.console.sendMessage(ChatColor.WHITE + "\"" + entity.substring(0, entity.length() - 1) + "\"");
					myGuardDog.console.sendMessage(ChatColor.DARK_RED + "I read " + ChatColor.WHITE + "\""
							+ entity_name[entity_name.length - 1].substring(1, entity_name[entity_name.length - 1].length() - 1) + "\"" + ChatColor.DARK_RED
							+ " as the data value in the data suffix.");
					exception.printStackTrace();
				}
			// now we need to adjust the final result based on the gaps in I.D.s
			for (short[] gap : entity_gaps)
				if (result_id > gap[0])
					result_id += (gap[1] - gap[0] - 1);
				else
					break;
		}
		return new Integer[] { result_id, result_data };
	}

	/**
	 * This method returns the name of the entity specified by the I.D. and data given.
	 * 
	 * @param id
	 *            is the entity type I.D.
	 * @param data
	 *            is the numerical data value for the entity. Data is only used for creepers states (charged vs. non-charged), villagers (professions), and
	 *            sheep (colors).
	 * @param give_data_suffix
	 *            specifies whether or not the name of the entity should include the numerical data value at the end of the item name in parentheses (e.g.
	 *            "a trapdoor <b>(16)</b>"). For logging purposes in myGuardDog, for example, we should be as specific as possible on information about the
	 *            item, so this argument should be <b>true</b>. However, for messages to users for commands like <i>/eid</i>, the data suffix is not helpful and
	 *            looks awkward, so this argument should be <b>false</b>.
	 * @param singular
	 *            specifies whether the entity name returned should be returned in the singular form (e.g. "a creeper") or in the plural form (e.g. "creepers").
	 *            Singular forms include an article at the beginning.
	 * @return the name of the entity specified by the I.D. and data given.
	 * @see {@link #getEntityName(Entity, boolean, boolean) getEntityName(Entity, boolean, boolean)}
	 */
	public static String getEntityName(int id, int data, boolean give_data_suffix, boolean singular) {
		// return null if the I.D. is inside a gap
		for (short[] gap : entity_gaps)
			if (id > gap[0] && id < gap[1])
				return null;
		// for the entity gaps
		for (int i = entity_gaps.length - 1; i >= 0; i--)
			if (id >= entity_gaps[i][1])
				id -= (entity_gaps[i][1] - entity_gaps[i][0] - 1);
		int sing_plur = 0;
		if (singular)
			sing_plur = 1;
		String entity = null;
		// the Exceptions in this block of code can be ArrayIndexOutOfBoundsExceptions or NullPointerExceptions
		try {
			// try using the data and I.D. given
			entity = entity_IDs[id][data + 1][sing_plur];
		} catch (ArrayIndexOutOfBoundsException exception) {
			try {
				// if that doesn't work, try substracting 4 from the data until we can't any more and try again
				entity = entity_IDs[id][data % 4 + 1][sing_plur];
				if (entity != null && give_data_suffix && data > 0)
					entity += " (" + data + ")";
			} catch (Exception exception2) {
				try {
					// if that doesn't work, try giving the general name for the entity with the I.D.
					entity = entity_IDs[id][0][sing_plur];
					if (entity != null && give_data_suffix && data > 0)
						entity += " (" + data + ")";
				} catch (Exception exception3) {
				}
			}
		} catch (NullPointerException exception) {
		}
		return entity;
	}

	/**
	 * This method returns a four-line String describing the recipe for crafting the specified item indicated by the provided I.D. This String can be color
	 * coded and displayed in the Minecraft chat or console to describe how to craft the item indicated.
	 * 
	 * @param id
	 *            is the I.D. of the item or block for which the recipe was requested.
	 * @param data
	 *            is the numerical data value for the item or block.
	 * @return a four-line String describing the recipe for crafting the specified item indicated by the provided I.D.
	 * @see {@link #getRecipe(String) getRecipe(String)}
	 */
	public static String getRecipe(int id, int data) {
		// TODO
		return ChatColor.GOLD + "Coming soon to a server near you!";
	}

	/**
	 * This method will tell whether or not a certain block will break if the block that it is attached to is broken. No data value is required as input for
	 * this method because items with the same I.D. consistently have this property in common.
	 * 
	 * @param id
	 *            is the I.D. of the block which needs to be checked for the "must be attached" property.
	 * @param bottom_only
	 *            indicates whether the method should return true only <b>1)</b> if the item is one that must be attached on the bottom only like redstone wire
	 *            or a lily pad (indicated by a <b>true</b> value), <b>2)</b> if the item is one that can be attached sideways like a torch or a wall sign
	 *            (indicated by a <b>false</b> value), or <b>3)</b> if the item needs to be attached on the bottom or sideways (indicated by a <b>null</b>
	 *            value).
	 * @return <b>true</b> if the block given by the I.D. will break if the block it is attached to breaks and it attaches in the way indicated by <b>
	 *         <tt>bottom_only</tt></b>, <b>false</b> if the block does not need to be attached to another block or not in the way specified by
	 *         <tt><b>bottom_only</tt></b>, and <b>null</b> if the I.D. given does not apply to a block at all.
	 */
	public static Boolean mustBeAttached(int id, Boolean bottom_only) {
		if (getItemName(id, -1, false, false) == null || id >= 256)
			return null;
		if (bottom_only == null || bottom_only)
			for (int i = 0; i < must_be_attached_bottom_only_IDs.length; i++)
				if (must_be_attached_bottom_only_IDs[i] == id)
					return true;
		if (bottom_only == null || !bottom_only)
			for (int i = 0; i < must_be_attached_can_be_sideways_IDs.length; i++)
				if (must_be_attached_can_be_sideways_IDs[i] == id)
					return true;
		return false;
	}

	// alternate input or output methods
	/**
	 * This method returns a two-item Integer array or <b>null</b>. <tt>[0]</tt> is the I.D. of the item given by <tt>item_name</tt>. <tt>[1]</tt> is the data
	 * value of the item given, e.g. 2 for birch wood (because all logs have the I.D. 17, but a data value of 2 refers to birch wood specifically). If
	 * <tt><b>item_name</b></tt> specifies a general item name such as "logs", the data value returned will be -1.
	 * 
	 * @param item_name
	 *            is the name of the item or block type that was specified split into separate words.
	 * @param item_ID
	 *            is <b>true</b> if this method should only return item I.D.s and not block type I.D.s, <b>false</b> if this method should only return block
	 *            type I.D.s and not item I.D.s, or <b>null</b> if it should return either item I.D.s or block type I.D.s, in which case it will proritize item
	 *            I.D.s over block type I.D.s.
	 * @return the I.D. and numerical data value for the item given by name in <tt><b>item_name</tt></b> in a two-item Integer array or <tt><b>null</b></tt> if
	 *         <b>1)</b> the item specified does not exist or <b>2)</b> the object specified is an item, not a block type, and it was specified in
	 *         <tt><b>item_ID</b></tt> that this method should only return block types or vice versa.
	 * @NOTE This method returns both the I.D. and the data value of an item based on the item's name because it encourages the programmer to only use this
	 *       method once as necessary, not once to get the I.D. and again to get the data. It is a long, somewhat complex method and it must search through
	 *       hundreds and hundreds of Strings in the <tt>item_IDs</tt> array to find a match. This method should only be called when necessary and results
	 *       returned by this method should be stored in a variable if needed more than once; do not simply call this method a second time.
	 * @see {@link #getItemIdAndData(String[], Boolean) getItemIdAndData(String[], Boolean)}, {@link #getItemIdAndDataString(String[], Boolean)
	 *      getItemIdAndDataString(String[], Boolean)}, and {@link #getItemIdAndDataString(String, Boolean) getItemIdAndDataString(String, Boolean)}
	 */
	public static Integer[] getItemIdAndData(String item_name, Boolean item_ID) {
		return getItemIdAndData(item_name.split(" "), item_ID);
	}

	/**
	 * This method returns a String describing the id and data of the item or <b>null</b>. The String is equivalent to <tt>String.valueOf(</tt>the I.D. of the
	 * item or block specified<tt>)</tt> if data < 1, but if data > 0, the String is formatted as "[id]:[data]". If <tt><b>item_name</b></tt> specifies a
	 * general item name such as "logs", the data value returned will be -1; therefore, no data will be included in the String returned.
	 * 
	 * @param item_name
	 *            is the name of the item or block type that was specified split into separate words.
	 * @param item_ID
	 *            is <b>true</b> if this method should only return item I.D.s and not block type I.D.s, <b>false</b> if this method should only return block
	 *            type I.D.s and not item I.D.s, or <b>null</b> if it should return either item I.D.s or block type I.D.s, in which case it will proritize item
	 *            I.D.s over block type I.D.s.
	 * @return the I.D. and numerical data value for the item given by name in <tt><b>item_name</tt></b> in a String formatted as "[id]" if data < 1 or
	 *         "[id]:[data]" otherwise or <tt><b>null</b></tt> if <b>1)</b> the item specified does not exist or <b>2)</b> the object specified is an item, not
	 *         a block type, and it was specified in <tt><b>item_ID</b></tt> that this method should only return block types or vice versa.
	 * @see {@link #getItemIdAndData(String[], Boolean) getItemIdAndData(String[], Boolean)}, {@link #getItemIdAndData(String, Boolean) getItemIdAndData(String,
	 *      Boolean)}, and {@link #getItemIdAndDataString(String, Boolean) getItemIdAndDataString(String, Boolean)}
	 */
	public static String getItemIdAndDataString(String[] item_name, Boolean item_ID) {
		Integer[] id_and_data = getItemIdAndData(item_name, item_ID);
		if (id_and_data == null)
			return null;
		String result = String.valueOf(id_and_data[0]);
		if (id_and_data[1] > 0)
			result += ":" + id_and_data[1];
		return result;
	}

	/**
	 * This method returns a String describing the id and data of the item or <b>null</b>. The String is equivalent to <tt>String.valueOf(</tt>the I.D. of the
	 * item or block specified<tt>)</tt> if data < 1, but if data > 0, the String is formatted as "[id]:[data]". If <tt><b>item_name</b></tt> specifies a
	 * general item name such as "logs", the data value returned will be -1; therefore, no data will be included in the String returned.
	 * 
	 * @param item_name
	 *            is the name of the item or block type that was specified split into separate words.
	 * @param item_ID
	 *            is <b>true</b> if this method should only return item I.D.s and not block type I.D.s, <b>false</b> if this method should only return block
	 *            type I.D.s and not item I.D.s, or <b>null</b> if it should return either item I.D.s or block type I.D.s, in which case it will proritize item
	 *            I.D.s over block type I.D.s.
	 * @return the I.D. and numerical data value for the item given by name in <tt><b>item_name</tt></b> in a String formatted as "[id]" if data < 1 or
	 *         "[id]:[data]" otherwise or <tt><b>null</b></tt> if <b>1)</b> the item specified does not exist or <b>2)</b> the object specified is an item, not
	 *         a block type, and it was specified in <tt><b>item_ID</b></tt> that this method should only return block types or vice versa.
	 * @see {@link #getItemIdAndData(String[], Boolean) getItemIdAndData(String[], Boolean)}, {@link #getItemIdAndData(String, Boolean) getItemIdAndData(String,
	 *      Boolean)}, and {@link #getItemIdAndDataString(String[], Boolean) getItemIdAndDataString(String[], Boolean)}
	 */
	public static String getItemIdAndDataString(String item_name, Boolean item_ID) {
		return getItemIdAndDataString(item_name.split(" "), item_ID);
	}

	public static String getItemName(ItemStack item, boolean give_data_suffix, boolean singular) {
		return getItemName(item.getTypeId(), item.getData().getData(), give_data_suffix, singular);
	}

	public static String getItemName(Block block, boolean give_data_suffix, boolean singular) {
		return getItemName(block.getTypeId(), block.getData(), give_data_suffix, singular);
	}

	public static Integer[] getEntityIdAndData(String entity_name) {
		return getEntityIdAndData(entity_name.split(" "));
	}

	public static String getEntityIdAndDataString(String[] entity_name) {
		Integer[] id_and_data = getEntityIdAndData(entity_name);
		if (id_and_data == null)
			return null;
		String result = String.valueOf(id_and_data[0]);
		if (id_and_data[1] > 0)
			result += ":" + id_and_data[1];
		return result;
	}

	public static String getEntityIdAndDataString(String entity_name) {
		return getEntityIdAndDataString(entity_name.split(" "));
	}

	public static String getEntityName(Entity entity, boolean give_data_suffix, boolean singular) {
		int data = -1;
		if (entity.getType() == EntityType.VILLAGER)
			data = ((Villager) entity).getProfession().getId();
		else if (entity.getType() == EntityType.CREEPER)
			if (((Creeper) entity).isPowered())
				data = 0;
			else
				data = 1;
		else if (entity.getType() == EntityType.SHEEP)
			// the data for the sheep is organized in the same way as the wool data; dye data goes in the opposite direction
			data = ((Sheep) entity).getColor().getWoolData();
		else if (entity.getType() == EntityType.PAINTING)
			data = ((Painting) entity).getAttachedFace().ordinal();
		else if (entity.getType() == EntityType.ITEM_FRAME)
			data = ((ItemFrame) entity).getAttachedFace().ordinal();
		return getEntityName(entity.getType().getTypeId(), data, give_data_suffix, singular);
	}

	public static String getRecipe(String item_name) {
		Integer[] id_and_data = getItemIdAndData(item_name, null);
		if (id_and_data == null)
			return null;
		return getRecipe(id_and_data[0], id_and_data[1]);
	}

	public static Boolean mustBeAttached(Block block, Boolean bottom_only) {
		return mustBeAttached(block.getTypeId(), bottom_only);
	}
}