package REALDrummer;

import org.bukkit.ChatColor;

public class myPluginWiki {
	// String[item I.D.][special data][all the names that could be applied to that item]
	// the special data works like this:
	// [0] is the name for the overall item, e.g. "logs" for any type of log, which all have the I.D. 17; the other indexes are [data+1], e.g. birch logs (I.D.
	// = 17 & data = 2) are found at [17][3]
	public static final String[][][] item_IDs = {
			{ { "air" } },
			{ { "stone", "rock", "smooth stone" } },
			{ { "grass", "grass blocks" } },
			{ { "dirt", "filth" } },
			{ { "cobblestone", "cobblies" } },
			{ { "wooden planks", "planks" }, { "oak planks", "oak wooden planks" }, { "spruce planks", "spruce wooden planks", "pine planks", "pine wooden planks" },
					{ "birch planks", "birch wooden planks" }, { "jungle planks", "jungle wooden planks" } },
			{ { "saplings" }, { "oak saplings", "oak tree saplings" }, { "spruce saplings", "spruce tree saplings", "pine tree saplings" },
					{ "birch saplings", "birch tree saplings" }, { "jungle saplings", "jungle tree saplings" } },
			{ { "bedrock" } },
			{ { "water" } },
			{ { "stationary water" } },
			{ { "lava" } },
			{ { "stationary lava" } },
			{ { "sand" } },
			{ { "gravel", "pebbles" } },
			{ { "gold ore", "golden ore" } },
			{ { "iron ore" } },
			{ { "coal ore" } },
			{ { "logs", "wood" }, { "oak logs", "oak wood" }, { "spruce logs", "spruce wood", "pine logs", "pine wood" }, { "birch logs", "birch wood" },
					{ "jungle logs", "jungle wood" } },
			{ { "leaves", "leaves blocks", "leafs blocks" }, { "oak leaves", "oak leaves blocks", "oak leafs blocks" },
					{ "birch leaves", "birch leaves blocks", "birch leafs blocks" },
					{ "spruce leaves", "spruce leaves blocks", "spruce leafs blocks", "pine needles", "pine leaves blocks", "pine leafs blocks" },
					{ "jungle leaves", "jungle leaves blocks", "jungle leafs blocks" } },
			{ { "sponges", "loofas" } },
			{ { "glass", "glass blocks", "glass cubes" } },
			{ { "lapis lazuli ore" } },
			{ { "lapis lazuli blocks", "blocks of lapis lazuli" } },
			{ { "dispensers", "shooters" } },
			{
					{ "sandstone", "sandbricks" },
					{ "regular sandstone", "regular sandbricks", "normal sandstone", "normal sandbricks", "natural sandstone", "natural sandbricks" },
					{ "chiseled sandstone", "chiseled sandbricks", "ancient sandstone", "ancient sandbricks", "pyramid sandstone", "pyramid sandbricks",
							"hieroglyphics sandstone", "hieroglyphics sandbricks" }, { "smooth sandstone", "smooth sandbricks", "clean sandstone", "clean sandbricks" } },
			{ { "note blocks", "music blocks", "sound blocks", "speakers" } },
			{ { "beds" } },
			{ { "powered rails", "powered redstone rails", "powered redstone tracks", "powered redstone railroad tracks" } },
			{ { "detector rails", "detection rails", "sensor rails", "sensory rails", "pressure rails", "pressure plate rails", "detector railroad tracks",
					"detection railroad tracks", "sensor railroad tracks", "sensory railroad tracks", "pressure railroad tracks", "pressure plate railroad tracks" } },
			{ { "sticky pistons", "slime pistons", "slimy pistons" } },
			{ { "cobwebs", "spider webs", "webs" } },
			{ { "tall grass and dead shrubs", "tall grass and dead shrubbery", "weeds" }, { "dead grass", "dead grass bushes", "dead grass shrubs", "dead grass shrubbery" },
					{ "tall grass", "high grass" }, { "ferns", "small plants" } },
			{ { "dead shrubs", "dead bushes", "dead shrubbery", "dried plants" } },
			{ { "pistons" } },
			{ { "piston extensions", "piston arm", "piston pusher" } },
			{ { "wool", "wool blocks" }, { "white wool", "white wool blocks" }, { "orange wool", "orange wool blocks" }, { "magenta wool", "magenta wool blocks" },
					{ "light blue wool", "light blue wool blocks" }, { "yellow wool", "yellow wool blocks" },
					{ "lime green wool", "lime green wool blocks", "green wool blocks", "light green wool blocks" }, { "pink wool", "pink wool blocks" },
					{ "gray wool", "dark gray wool blocks" }, { "light gray wool", "light gray wool blocks", "off white wool blocks" }, { "cyan wool", "cyan wool blocks" },
					{ "purple wool", "purple wool blocks" }, { "blue wool", "blue wool blocks" }, { "brown wool", "brown wool blocks" },
					{ "cactus green wool", "cactus green wool blocks", "dark green wool blocks" }, { "red wool", "red wool blocks" }, { "black wool", "black wool blocks" } },
			{ { "blocks moved by pistons" } },
			{ { "flowers", "yellow flowers", "dandelions" } },
			{ { "roses", "red flowers" } },
			{ { "brown mushrooms", "small brown mushrooms", "little brown mushrooms" } },
			{ { "red mushrooms", "small red mushrooms", "little red mushrooms" } },
			{ { "gold blocks", "blocks of gold" } },
			{ { "iron blocks", "blocks of iron" } },
			{
					{ "all double slab blocks", "all double slabs", "all stacked slabs" },
					{ "stone double slab blocks", "stone double slabs", "stone stacked slabs" },
					{ "sandstone double slab blocks", "sandstone double slabs", "sandstone stacked slabs", "sandbrick double slab blocks", "sandbrick double slabs",
							"sandbrick stacked slabs" }, { "cobblestone double slab blocks", "cobblestone double slabs", "cobblestone stacked slabs" },
					{ "brick double slab blocks", "clay brick stacked slabs", "clay brick double slab blocks", "clay brick double slabs" },
					{ "stone brick double slab blocks", "stone brick double slabs", "stone brick stacked slabs" },
					{ "Nether brick double slab blocks", "Nether brick double slabs", "Nether brick stacked slabs" },
					{ "Nether Quartz double slab blocks", "Nether Quartz double slabs", "Nether Quartz stacked slabs" } },
			{ { "all slabs", "all half blocks" }, { "stone slabs", "stone half blocks" },
					{ "sandstone slabs", "sandstone half blocks", "sandbrick slabs", "sandbrick half blocks" }, { "cobblestone slabs", "cobblestone half blocks" },
					{ "brick slabs", "clay brick slabs", "clay brick half blocks" }, { "stone brick slabs", "stone brick half blocks" },
					{ "Nether brick slabs", "Nether brick half blocks" }, { "Nether Quartz slabs", "Nether Quartz half blocks" } },
			{ { "bricks", "bricks blocks" } },
			{ { "T.N.T.", "TNT", "dynamite", "trinitrotoluene" } },
			{ { "bookcases", "bookshelves", "bookshelfs" } },
			{ { "mossy stone", "mossy cobblestone", "ancient cobblestone", "old cobblestone" } },
			{ { "obsidian", "volcanic glass" } },
			{ { "torches", "fire sticks" } },
			{ { "fire", "flames" } },
			{ { "monster spawners", "spawners" } },
			{ { "oak stairs", "oak wood stairs", "wooden stairs", "oak wood steps", "wooden steps" } },
			{ { "chests" } },
			{ { "redstone wire", "wire" } },
			{ { "diamond ore" } },
			{ { "diamond blocks", "blocks of diamonds" } },
			{ { "crafting tables", "crafting workbench" } },
			{ { "wheat", "crops" } },
			{ { "farmland blocks", "plowed tilled land soil blocks" } },
			{ { "furnaces", "ovens", "ranges" } },
			{ { "burning furnaces", "active burning ovens ranges furnaces" } },
			{ { "sign posts", "ground sign posts" } },
			{ { "wooden doors", "doors" } },
			{ { "ladders", "wooden rope ladders" } },
			{ { "rails", "normal regular unpowered iron rails railroad tracks" } },
			{ { "cobblestone stairs", "cobblestone steps" } },
			{ { "wall signs", "posted wall signs" } },
			{ { "levers", "switches" } },
			{ { "stone pressure plates", "stone foot switches plates" } },
			{ { "iron doors" } },
			{ { "wooden pressure plates", "wooden foot switches plates" } },
			{ { "redstone ore" } },
			{ { "glowing redstone ore", "luminescent redstone ore" } },
			{ { "inactive redstone torches" } },
			{ { "redstone torches" } },
			{ { "stone buttons", "buttons" } },
			{ { "natural snow on the ground", "snow", "snow on the ground", "fallen snow", "fresh snow", "ground snow" } },
			{ { "ice", "blocks of ice", "ice blocks" } },
			{ { "snow blocks", "blocks of snow" } },
			{ { "cacti", "cactuses", "saguaros", "saguaro cacti", "saguaro cactuses" } },
			{ { "clay blocks", "blocks of clay" } },
			{ { "sugar cane", "sugar canes", "sugarcanes" } },
			{ { "jukeboxes", "disc player", "music box", "slotted block", ".mp3 player" } },
			{ { "wooden fences", "wooden fence posts", "wooden railings" } },
			{ { "pumpkins", "unlit Jack-o'-Lanterns", "dark Jack-o'-Lanterns" } },
			{ { "Netherrack", "Nether rack", "Nether dirt" } },
			{ { "Soul Sand", "Nether sand", "quicksand", "quick sand" } },
			{ { "glowstone", "glowstone blocks", "blocks of glowstone" } },
			{ { "Nether portal blocks", "Nether portal swirly blocks" } },
			{ { "Jack-o'-Lanterns", "lit Jack-o'-Lanterns", "lit Jack o Lanterns", "lit JackoLanterns" } },
			{ { "cakes", "cake blocks" } },
			{ { "repeaters", "diodes", "delayers", "redstone repeaters", "redstone diodes", "redstone delayers" } },
			{ { "active repeaters", "active diodes", "active delayers", "active redstone repeaters", "active redstone diodes", "active redstone delayers" } },
			{ { "locked chests" } },
			{ { "trapdoors", "ground doors" } },
			{ { "silverfish spawners", "monster egg blocks", "monster eggs" }, { "silverfish stone", "silverfish smooth stone" }, { "silverfish cobblestone" },
					{ "silverfish stone bricks" } },
			{ { "stone bricks", "cobblestone bricks", "cobble bricks" }, { "mossy stone bricks", "mossy cobblestone bricks", "mossy cobble bricks" },
					{ "cracked stone bricks", "cracked cobblestone bricks", "cracked cobble bricks" },
					{ "chiseled stone bricks", "chiseled cobblestone bricks", "chiseled cobble bricks", "circle blocks" } },
			{ { "giant brown mushroom blocks", "huge brown mushrooms", "big brown mushrooms", "giant brown 'shroom blocks", "huge brown 'shrooms", "big brown shrooms",
					"giant brown shroom blocks", "huge brown shrooms", "big brown shrooms" } },
			{ { "giant red mushroom blocks", "huge red mushrooms", "big red mushrooms", "giant red 'shroom blocks", "huge red 'shrooms", "big red shrooms",
					"giant red shroom blocks", "huge red shrooms", "big red shrooms" } },
			{ { "iron bars", "wrought iron bars" } },
			{ { "glass panes", "windows", "window panes" } },
			{ { "melon blocks", "full melons", "watermelon blocks", "whole melons", "whole watermelons" } },
			{ { "pumpkin stems", "pumpkin stalks", "pumpkin vines" } },
			{ { "melon stems", "melon stalks", "melon vines", "watermelon stems", "watermelon stalks", "watermelon vines" } },
			{ { "vines", "jungle vines", "swamp vines" } },
			{ { "fence gates", "wooden gates", "wood gates" } },
			{ { "brick stairs", "brick steps", "clay brick stairs", "clay brick steps" } },
			{ { "stone brick stairs", "stone brick steps", "stone stairs", "stone steps" } },
			{ { "mycelium", "mushroom grass", "shroom grass", "mushroom biome grass" } },
			{ { "lily pads", "lilies", "pond lilies", "lilypads", "water lily", "water lilies" } },
			{ { "Nether bricks", "Nether fortress bricks blocks", "Nether dungeon bricks blocks" } },
			{ { "Nether brick fences", "Nether fortress fences", "Nether dungeon fences" } },
			{ { "Nether brick stairs", "Nether fortress stairs", "Nether dungeon stairs", "Nether brick steps", "Nether fortress steps", "Nether dungeon steps" } },
			{ { "Nether warts", "Nether mushrooms", "Nether 'shrooms", "Nether fungi" } },
			{ { "enchantment tables" } },
			{ { "brewing stands" } },
			{ { "cauldrons" } },
			{ { "End portal blocks" } },
			{ { "End portal frame blocks" } },
			{ { "End stone", "End blocks" } },
			{ { "dragon eggs", "Enderdragon eggs" } },
			{ { "redstone lamps", "glowstone lamps" } },
			{ { "active redstone lamps", "active glowstone lamps" } },
			{
					{ "wooden double slab blocks", "wooden double slabs", "wooden plank double slab blocks", "wooden plank double slabs", "wood double slab blocks",
							"wood double slabs", "wood plank double slab blocks", "wood plank double slabs" },
					{ "oak double slab blocks", "oak double slabs", "oak plank double slab blocks", "oak plank double slabs", "oak wood double slab blocks",
							"oak wood double slabs", "oak wood plank double slab blocks", "oak wood plank double slabs" },
					{ "spruce double slab blocks", "spruce double slabs", "spruce plank double slab blocks", "spruce plank double slabs", "spruce wood double slab blocks",
							"spruce wood double slabs", "spruce wood plank double slab blocks", "spruce wood plank double slabs", "pine double slab blocks",
							"pine double slabs", "pine plank double slab blocks", "pine plank double slabs", "pine wood double slab blocks", "pine wood double slabs",
							"pine wood plank double slab blocks", "pine wood plank double slabs" },
					{ "birch double slab blocks", "birch double slabs", "birch plank double slab blocks", "birch plank double slabs", "birch wood double slab blocks",
							"birch wood double slabs", "birch wood plank double slab blocks", "birch wood plank double slabs" }, },
			{
					{ "wooden slabs", "wooden half blocks", "wood slabs", "wood half blocks", "wooden plank slabs", "wooden plank half blocks", "wood plank slabs",
							"wood plank half blocks" },
					{ "oak slabs", "oak half blocks", "oak wood slabs", "oak wood half blocks", "oak plank slabs", "oak plank half blocks", "oak wood plank slabs",
							"oak wood plank half blocks" },
					{ "spruce slabs", "spruce half blocks", "spruce wood slabs", "spruce wood half blocks", "spruce plank slabs", "spruce plank half blocks",
							"spruce wood plank slabs", "spruce wood plank half blocks", "pine slabs", "pine half blocks", "pine wood slabs", "pine wood half blocks",
							"pine plank slabs", "pine plank half blocks", "pine wood plank slabs", "pine wood plank half blocks" },
					{ "birch slabs", "birch half blocks", "birch wood slabs", "birch wood half blocks", "birch plank slabs", "birch plank half blocks",
							"birch wood plank slabs", "birch wood plank half blocks" } },
			{ { "cocoa bean plants", "cocoa bean pods", "cocoa beans" } },
			{ { "sandstone stairs", "sandstone steps" } },
			{ { "emerald ore" } },
			{ { "Ender Chests", "Enderchests" } },
			{ { "tripwire hooks", "tripwire mechanisms", "trip wire hooks", "trip wire mechanisms" } },
			{ { "tripwire", "trip wire" } },
			{ { "emerald blocks", "blocks of emerald", "blocks of emeralds" } },
			{ { "spruce stairs", "spruce wood stairs", "spruce steps", "spruce wood steps" } },
			{ { "birch stairs", "birch wood stairs", "birch steps", "birch wood steps" } },
			{ { "jungle stairs", "jungle wood stairs", "jungle steps", "jungle wood steps" } },
			{ { "command blocks" } },
			{ { "beacons" } },
			{
					{ "cobblestone walls", "cobblestone fences" },
					{ "mossy cobblestone walls", "mossy cobblestone fences", "mossy stone walls", "mossy stone fences", "old cobblestone walls", "old cobblestone fences",
							"old stone walls", "old stone fences", "ancient cobblestone walls", "ancient cobblestone fences", "ancient stone walls", "ancient stone fences" } },
			{ { "flower pots", "pots", "clay pots" } },
			{ { "carrots" } },
			{ { "potatoes", "potatos" } },
			{ { "wooden buttons", "wood buttons" } },
			{ { "monster heads", "heads" }, { "skeleton skulls", "skeleton heads", "skele heads", "skele skulls" },
					{ "Wither skeleton skulls", "Wither skeleleton heads", "Wither skele skulls", "Wither skele heads" }, { "zombie heads" },
					{ "Steve heads", "Minecraft Steve heads", "guy heads", "man heads", "person heads", "human heads" }, { "creeper heads" } },
			{ { "anvils" } },
			{ { "trapped chests" } },
			{ { "light weighted pressure plates", "light weight pressure plates", "lightweight pressure plates", "golden pressure plates", "gold pressure plates" } },
			{ { "heavy weighted pressure plates", "heavy weight pressure plates", "heavyweight pressure plates", "iron pressure plates", "silver pressure plates" } },
			{ { "redstone comparators", "redstone comparers" } },
			{ { "active redstone comparators", "active redstone comparers" } },
			{ { "daylight sensors", "night sensors", "nighttime sensors", "day night sensors", "day/night sensors", "daytime nighttime sensors", "daytime/nighttime sensors",
					"solar panels" } },
			{ { "redstone blocks", "blocks of redstone dust" } },
			{ { "Nether Quartz ore", "raw Nether Quartz", "crude Nether Quartz", "unrefined Nether Quartz" } },
			{ { "hoppers", "funnels" } },
			{
					{ "Nether Quartz blocks", "blocks of Nether Quartz", "block of Nether Quartz" },
					{ "chiseled Nether Quartz blocks", "chiseled blocks of Nether Quartz", "chiseled block of Nether Quartz", "fancy Nether Quartz blocks",
							"fancy blocks of Nether Quartz", "fancy block of Nether Quartz" },
					{ "pillar Nether Quartz blocks", "pillar blocks of Nether Quartz", "pillar block of Nether Quartz", "pillar blocks", "pillars" } },
			{ { "Nether Quartz stairs", "Nether Quartz steps" } },
			{ { "activator rails", "T.N.T. activator rails", "TNT activator rails", "striker rails", "T.N.T. starter rails", "TNT starter rails" } },
			{ { "droppers", "lazy dispensers", "new dispensers" } },
			{ { "iron shovels", "iron spades" } },
			{ { "iron pickaxes", "iron picks" } },
			{ { "iron axes", "iron hatchets", "iron tree axe" } },
			{ { "flint and steel" } },
			{ { "apples", "red apples" } },
			{ { "bows", "bows and arrows", "longbows", "long bows", "shortbows", "short bows" } },
			{ { "arrows" } },
			{ { "coal" } },
			{ { "diamonds" } },
			{ { "iron", "iron ingots" } },
			{ { "gold", "gold ingots", "gold bars" } },
			{ { "iron swords" } },
			{ { "wooden swords", "wood swords" } },
			{ { "wooden shovels", "wood shovels", "wooden spades", "wood spades" } },
			{ { "wooden pickaxes", "wooden picks", "wood pickaxes", "wood picks" } },
			{ { "wooden axes", "wooden hatchets", "wooden tree axes", "wood axes", "wood hatchets", "wood tree axes" } },
			{ { "stone swords", "cobblestone swords", "cobble swords" } },
			{ { "stone shovels", "cobblestone shovels", "cobble shovels", "stone spades", "cobblestone spades", "cobble spades" } },
			{ { "stone pickaxes", "stone picks", "cobblestone pickaxes", "cobble pickaxes", "cobblestone picks", "cobble picks" } },
			{ { "stone axes", "stone hatchets", "stone tree axes", "cobblestone axes", "cobble axes", "cobblestone hatchets", "cobble hatchets", "cobblestone tree axes",
					"cobble tree axes" } },
			{ { "diamond swords" } },
			{ { "diamond shovels", "diamond spades" } },
			{ { "diamond pickaxes", "diamond picks" } },
			{ { "diamond axes", "diamond hatchets", "diamond tree axes" } },
			{ { "sticks", "twigs" } },
			{ { "bowls", "wooden bowls", "wood bowls", "soup bowls" } },
			{ { "mushroom stew", "mushroom soup", "mooshroom milk", "mooshroom cow milk" } },
			{ { "golden swords", "gold swords" } },
			{ { "golden shovels", "gold shovels", "golden spades", "gold spades" } },
			{ { "golden pickaxes", "golden picks", "gold pickaxes", "gold picks" } },
			{ { "golden axes", "golden hatchets", "golden tree axes", "gold axes", "gold hatchets", "gold tree axes" } },
			{ { "string" } },
			{ { "feathers" } },
			{ { "gunpowder", "sulfur", "sulphur" } },
			{ { "wooden hoes", "wood hoes" } },
			{ { "stone hoes", "cobblestone hoes", "cobble hoes" } },
			{ { "iron hoes" } },
			{ { "diamond hoes" } },
			{ { "golden hoes", "gold hoes" } },
			{ { "seeds", "seed packets" } },
			{ { "wheat", "crops" } },
			{ { "bread", "bread loaves" } },
			{ { "leather caps", "leather helmets", "leather helms" } },
			{ { "leather tunics", "leather shirts", "leather chestplates" } },
			{ { "leather pants", "leather leggings", "leather chaps" } },
			{ { "leather boots", "leather shoes" } },
			{ { "chainmail helmets", "chainmail caps", "chainmail helms", "chain helmets", "chain caps", "chain helms" } },
			{ { "chainmail chestplates", "chainmail tunics", "chainmail shirts", "chain chestplates", "chain tunics", "chain shirts" } },
			{ { "chainmail leggings", "chainmail pants", "chain leggings", "chain pants" } },
			{ { "chainmail boots", "chainmail shoes", "chain boots", "chain shoes" } },
			{ { "iron helmets", "iron caps", "iron helms" } },
			{ { "iron chestplates", "iron tunics", "iron shirts" } },
			{ { "iron leggings", "iron pants" } },
			{ { "iron boots", "iron shoes" } },
			{ { "diamond helmets", "diamond caps", "diamond helms" } },
			{ { "diamond chestplates", "diamond tunics", "diamond shirts" } },
			{ { "diamond leggings", "diamond pants" } },
			{ { "diamond boots", "diamond shoes" } },
			{ { "golden helmets", "golden caps", "golden helms", "gold helmets", "gold caps", "gold helms" } },
			{ { "golden chestplates", "gold chestplates", "golden tunics", "gold tunics", "golden shirts", "gold shirts" } },
			{ { "golden leggings", "gold leggings", "golden pants", "gold pants" } },
			{ { "golden boots", "gold boots", "golden shoes", "gold shoes" } },
			{ { "flint", "arrowheads" } },
			{ { "raw porkchops", "uncooked porkchops" } },
			{ { "cooked porkchops", "porkchops" } },
			{ { "paintings", "artwork" } },
			{
					{ "golden apples", "gold apples" },
					{ "enchanted golden apples", "enchanted gold apples", "magic golden apples", "magic gold apples", "shiny golden apples", "shiny gold apples",
							"shining golden apples", "shiny golden apples" } },
			{ { "signs", "sign posts", "posted signs", "wall signs" } },
			{ { "wooden doors", "wood doors" } },
			{ { "buckets", "pails" } },
			{ { "buckets of water", "water buckets" } },
			{ { "buckets of lava", "lava buckets" } },
			{ { "minecarts", "mine carts", "minecars", "mine cars", "rail cars" } },
			{ { "saddles", "pig saddles" } },
			{ { "iron doors", "metal doors" } },
			{ { "redstone dust", "redstone powder", "redstone" } },
			{ { "snowballs" } },
			{ { "boats", "wooden boats", "wood boats", "rafts", "wood rafts", "wooden rafts" } },
			{ { "leather", "cow hides", "cow skin", "cowskin" } },
			{ { "milk", "leche", "bucket of milk", "buckets of milk", "pail of milk", "pails of milk" } },
			{ { "bricks", "clay bricks" } },
			{ { "clay" } },
			{ { "sugarcane", "sugarcanes", "sugar canes" } },
			{ { "papers" } },
			{ { "books" } },
			{ { "slimeballs", "slime balls" } },
			{ { "storage minecarts", "storage minecars", "storage mine cars", "storage rail cars", "chest minecarts", "chest minecars", "chest mine cars", "chest rail cars",
					"minecarts with chests", "minecars with chests", "mine cars with chests", "rail cars with chests" } },
			{ { "powered minecarts", "powered minecars", "powered mine cars", "powered rail cars", "furnace minecarts", "furnace minecars", "furnace mine cars",
					"furnace rail cars", "minecarts with furnaces", "minecars with furnaces", "mine cars with furnaces", "rail cars with furnaces" } },
			{ { "eggs", "chicken eggs" } },
			{ { "compasses" } },
			{ { "fishing rods", "fishing poles" } },
			{ { "clocks", "watches", "pocketwatches", "pocket watches" } },
			{ { "glowstone dust" } },
			{ { "raw fish", "uncooked fish", "raw fish", "uncooked fish", "sushi" } },
			{ { "cooked fish", "fish" } },
			{
					{ "wool dyes", "dyes" },
					{ "ink sacs", "squid ink sacs", "squid ink sacks", "squid ink pods" },
					{ "red dye", "rose red dyes", "rose red wool dyes" },
					{ "cactus green dye", "cactus green dyes", "cactus green wool dyes" },
					{ "cocoa beans", "chocolate beans", "brown dyes", "brown wool dyes" },
					{ "lapis lazuli", "lapis lazuli dyes", "lapis lazuli wool dyes", "lapis dyes", "lapis wool dyes", "blue dyes", "blue wool dyes", "dark blue dyes",
							"dark blue wool dyes" },
					{ "purple dye", "purple dyes", "purple wool dyes" },
					{ "cyan dye", "cyan dyes", "cyan wool dyes" },
					{ "light gray dye", "light gray dyes", "light gray wool dyes", "light grey dyes", "light grey wool dyes" },
					{ "gray dye", "dark gray dyes", "dark gray wool dyes", "dark grey dyes", "dark grey wool dyes" },
					{ "pink dye", "pink dyes", "pink wool dyes", "light red dyes", "light red wool dyes" },
					{ "bright green dye", "bright green dyes", "lime dyes", "lime wool dyes", "lime green dyes", "lime green wool dyes", "green dyes", "green wool dyes" },
					{ "yellow dye", "yellow dyes", "yellow wool dyes", "yellow flower dyes", "yellow flower wool dyes", "dandelion yellow dyes", "dandelion yellow wool dyes",
							"yellow dandelion dyes", "yellow dandelion wool dyes" }, { "light blue dye", "light blue dyes", "light blue wool dyes" },
					{ "magenta dye", "magenta dyes", "magenta wool dyes" }, { "orange dye", "orange dyes", "orange wool dyes" },
					{ "bone meal", "bone meals", "bonemeals", "white dyes", "white wool dyes", "wool bleaches" } },
			{ { "bones" } },
			{ { "sugar", "processed sugar", "powdered sugar", "raw sugar", "baker's sugar" } },
			{ { "cakes", "birthday cakes" } },
			{ { "beds" } },
			{ { "repeaters", "diodes", "delayers", "redstone repeaters", "redstone diodes", "redstone delayers" } },
			{ { "cookies", "chocolate chip cookies", "oatmeal raisin cookies" } },
			{ { "maps", "atlases", "charts" } },
			{ { "shears", "clippers" } },
			{ { "melon slices", "slices of melon" } },
			{ { "pumpkin seeds" } },
			{ { "melon seeds" } },
			{ { "raw beef", "uncooked beef", "uncooked steak" } },
			{ { "cooked beef", "beef", "steak" } },
			{ { "raw chickens", "uncooked chickens" } },
			{ { "cooked chickens", "chickens" } },
			{ { "rotten flesh", "rotted flesh", "flesh", "zombie flesh", "zombie meat" } },
			{ { "Ender pearls", "Enderman pearls" } },
			{ { "Blaze rods", "glowsticks", "glow sticks" } },
			{ { "Ghast tears", "tears" } },
			{ { "gold nuggets", "golden nuggets", "gold pieces", "pieces of gold" } },
			{ { "Nether warts", "Nether mushrooms", "Nether 'shrooms", "Nether fungi" } },
			{
					{ "potions" },
					{ "water bottles", "canteens" },
					{ "The Awkward Potion", "useless potions", "lame potions" },
					{ "The Thick Potion", "pots potions of thickness" },
					{ "The Mundane Potion", "pots potions of mundaneness" },
					{ "The Potion of Regeneration", "pots potions of regeneration" },
					{ "The Potion of Swiftness", "pots potions of swiftness sprinting running fast" },
					{ "The Potion of Fire Resistance", "pots potions of no burning fire resistance lava swimming fireproof inflammable inflammability" },
					{ "The Potion of Poison", "pots potions of poison toxic toxins" },
					{ "The Potion of Healing", "pots potions of instant healing instaheal instahealth" },
					{ "The Potion of Night Vision", "pots potions of nightvision" },
					null,
					{ "The Potion of Weakness", "pots potions of weakness fraility frailness fragility fragile easily hurt" },
					{ "The Potion of Strength", "pots potions of super strength strong extra damage sword superman superwoman supergirl wonderwoman" },
					{ "The Potion of Slowness", "pots potions of slowness sloth laziness slow down slowing" },
					null,
					{ "The Potion of Harming", "pots potions of instaharming instahurting instapain instahealth loss" },
					null,
					{ "The Potion of Invisibility", "pots potions of invisibility invisible no see clear hiding hidden pranking" },
					{ "The Potion of Regeneration II", "pots potions of regeneration II 2 two" },
					{ "The Potion of Swiftness II", "pots potions of swiftness sprinting running fast II 2 two" },
					null,
					{ "The Potion of Poison II", "pots potions of poison toxic toxins II 2 two" },
					{ "The Potion of Healing II", "pots potions of instant healing instaheal instahealth II 2 two" },
					{ "The Potion of Strength II", "pots potions of super strength strong extra damage sword superman superwoman supergirl wonderwoman II 2 two" },
					null,
					null,
					{ "The Potion of Harming II", "pots potions of instaharming instahurting instapain instahealth loss II 2 two" },
					{ "The Potion of Regeneration (Extended)", "pots potions of regeneration (Extended)" },
					{ "The Potion of Swiftness (Extended)", "pots potions of swiftness sprinting running fast (Extended)" },
					{ "The Potion of Fire Resistance (Extended)", "pots potions of no burning fire resistance lava swimming fireproof inflammable inflammability (Extended)" },
					{ "The Potion of Poison (Extended)", "pots potions of poison toxic toxins (Extended)" },
					null,
					{ "The Potion of Night Vision (Extended)", "pots potions of nightvision (Extended)" },
					null,
					{ "The Potion of Weakness (Extended)", "pots potions of weakness fraility frailness fragility fragile easily hurt (Extended)" },
					{ "The Potion of Strength (Extended)", "pots potions of super strength strong extra damage sword superman superwoman supergirl wonderwoman (Extended)" },
					{ "The Potion of Slowness (Extended)", "pots potions of slowness sloth laziness slow down slowing (Extended)" },
					{ "The Potion of Invisibility (Extended)", "pots potions of invisibility invisible no see clear hiding hidden pranking (Extended)" },
					{ "The Splash Potion of Regeneration", "pots splash thrown potions of regeneration" },
					{ "The Splash Potion of Swiftness", "pots splash thrown potions of swiftness sprinting running fast" },
					{ "The Splash Potion of Fire Resistance", "pots splash thrown potions of no burning fire resistance lava swimming fireproof inflammable inflammability" },
					{ "The Splash Potion of Poison", "pots splash thrown potions of poison toxic toxins" },
					{ "The Splash Potion of Healing", "pots splash thrown potions of instant healing instaheal instahealth" },
					{ "The Splash Potion of Night Vision", "pots splash thrown potions of nightvision" },
					null,
					{ "The Splash Potion of Weakness", "pots splash thrown potions of weakness fraility frailness fragility fragile easily hurt" },
					{ "The Splash Potion of Strength", "pots splash thrown potions of super strength strong extra damage sword superman superwoman supergirl wonderwoman" },
					{ "The Splash Potion of Slowness", "pots splash thrown potions of slowness sloth laziness slow down slowing" },
					null,
					{ "The Splash Potion of Harming", "pots splash thrown potions of instaharming instahurting instapain instahealth loss" },
					null,
					{ "The Splash Potion of Invisibility", "pots splash thrown potions of invisibility invisible no see clear hiding hidden pranking" },
					{ "The Splash Potion of Regeneration II", "pots splash thrown potions of regeneration II 2 two" },
					{ "The Splash Potion of Swiftness II", "pots splash thrown potions of swiftness sprinting running fast II 2 two" },
					null,
					{ "The Splash Potion of Poison II", "pots splash thrown potions of poison toxic toxins II 2 two" },
					{ "The Splash Potion of Healing II", "pots splash thrown potions of instant healing instaheal instahealth II 2 two" },
					{ "The Splash Potion of Strength II",
							"pots splash thrown potions of super strength strong extra damage sword superman superwoman supergirl wonderwoman II 2 two" },
					null,
					null,
					{ "The Splash Potion of Harming II", "pots splash thrown potions of instaharming instahurting instapain instahealth loss II 2 two" },
					{ "The Splash Potion of Regeneration (Extended)", "pots splash thrown potions of regeneration (Extended)" },
					{ "The Splash Potion of Swiftness (Extended)", "pots splash thrown potions of swiftness sprinting running fast (Extended)" },
					{ "The Splash Potion of Fire Resistance (Extended)",
							"pots splash thrown potions of no burning fire resistance lava swimming fireproof inflammable inflammability (Extended)" },
					{ "The Splash Potion of Poison (Extended)", "pots splash thrown potions of poison toxic toxins (Extended)" },
					null,
					{ "The Splash Potion of Night Vision (Extended)", "pots splash thrown potions of nightvision (Extended)" },
					null,
					{ "The Splash Potion of Weakness (Extended)", "pots splash thrown potions of weakness fraility frailness fragility fragile easily hurt (Extended)" },
					{ "The Splash Potion of Strength (Extended)",
							"pots splash thrown potions of super strength strong extra damage sword superman superwoman supergirl wonderwoman (Extended)" },
					{ "The Splash Potion of Slowness (Extended)", "pots splash thrown potions of slowness sloth laziness slow down slowing (Extended)" },
					{ "The Splash Potion of Invisibility (Extended)", "pots splash thrown potions of invisibility invisible no see clear hiding hidden pranking (Extended)" } },
			{ { "glass bottles" } },
			{ { "spider eyes" } },
			{ { "fermented spider eyes" } },
			{ { "Blaze powder" } },
			{ { "Magma cream" } },
			{ { "brewing stands" } },
			{ { "cauldrons", "kettles" } },
			{ { "Eyes of Ender", "Endereyes" } },
			{ { "glistening melon", "glistening melon slices", "gold melon slices", "golden melon slices", "shining melon slices" } },
			{ { "spawn eggs", "spawner spawning eggs" }, { "creeper spawn eggs", "creeper spawner spawning eggs" },
					{ "skeleton spawn eggs", "skeleton spawner spawning eggs" }, { "spider spawn eggs", "spider spawner spawning eggs" }, null,
					{ "zombie spawn eggs", "human regular old zombies spawner spawning eggs" }, { "slime spawn eggs", "slime spawner spawning eggs" },
					{ "Ghast spawn eggs", "Ghast spawner spawning eggs" }, { "zombie pigman spawn eggs", "zombie zombified pigman pigmen spawner spawning eggs" },
					{ "Enderman spawn eggs", "Enderman Endermen spawner spawning eggs" }, { "cave spider spawn eggs", "cave spiders spawner spawning eggs" },
					{ "silverfish spawn eggs", "silverfish spawner spawning eggs" }, { "Blaze spawn eggs", "Blazes spawner spawning eggs" },
					{ "Magma Cube spawn eggs", "Magma Cubes spawner spawning eggs" }, null, null, { "bat spawn eggs", "bats spawner spawning eggs" },
					{ "witch spawn eggs", "witches spawner spawning eggs" }, { "pig spawn eggs", "pigs piggies spawner spawning eggs" },
					{ "sheep spawn eggs", "sheeps spawner spawning eggs" }, { "cow spawn eggs", "cows calfs calves spawner spawning eggs" },
					{ "chicken spawn eggs", "chickens chicks spawner spawning eggs" }, { "squid spawn eggs", "squids spawner spawning eggs" },
					{ "wolf spawn eggs", "wolfs wolves dogs spawner spawning eggs" }, { "mooshroom spawn eggs", "mooshrooms cows calfs calves spawner spawning eggs" }, null,
					{ "ocelot spawn eggs", "ocelots cats kitties kittens spawner spawning eggs" },
					{ "villager spawn eggs", "villagers N.P.C. Testificates spawner spawning eggs" } },
			{ { "Bottles o' Enchanting", "xp bottles", "exp bottles", "level botties", "experience bottles" } },
			{ { "fire charges", "fireballs", "cannonballs", "Ghast cannonballs", "Blaze cannonballs", "Ghast fireballs", "Blaze fireballs" } },
			{ { "books and quills", "book and quill" } },
			{ { "written-in books", "novels", "texts" } },
			{ { "emeralds" } },
			{ { "frames", "item frames" } },
			{ { "flower pots", "pots", "potted flowers plants" } },
			{ { "carrots" } },
			{ { "potatoes", "raw potatoes" } },
			{ { "baked potatoes", "cooked potatoes", "mashed potatoes" } },
			{ { "poisonous potatoes", "poison potatoes", "bad potatoes" } },
			{ { "maps", "charts", "atlases" } },
			{ { "golden carrots", "gold carrots", "glistening carrots", "shiny carrots" } },
			{ { "monster heads", "heads" }, { "skeleton skulls", "skeleton heads", "skele heads", "skele skulls" },
					{ "Wither skeleton skulls", "Wither skeleleton heads", "Wither skele skulls", "Wither skele heads" }, { "zombie heads" },
					{ "Steve heads", "Minecraft Steve heads", "guy heads", "man heads", "person heads", "human heads" }, { "creeper heads" } },
			{ { "carrots on sticks", "carrots on fishing rods", "carrots on fishing poles", "pig controller" } }, { { "Nether Stars" } }, { { "pumpkin pies" } },
			{ { "fireworks", "firework rockets" } }, { { "firework stars", "firework color effect balls" } }, { { "enchanted books", "magic spellbooks" } },
			{ { "redstone comparators", "redstone comparers" } }, { { "Nether bricks", "individual single singular Nether bricks" } }, { { "Nether Quartz", "Nether gems" } },
			{ { "T.N.T. minecarts", "TNT trinitrotoluene minecarts" } }, { { "hopper minecarts", "vacuum pickup minecarts" } },
			{ { "\"13\" music discs", "\"13\" disks", "\"13\" records", "\"13\" CDs" } }, { { "\"cat\" music discs", "\"cat\" disks", "\"cat\" records", "\"cat\" CDs" } },
			{ { "\"blocks\" music discs", "\"blocks\" disks", "\"blocks\" records", "\"blocks\" CDs" } },
			{ { "\"chirp\" music discs", "\"chirp\" disks", "\"chirp\" records", "\"chirp\" CDs" } },
			{ { "\"far\" music discs", "\"far\" disks", "\"far\" records", "\"far\" CDs" } },
			{ { "\"mall\" music discs", "\"mall\" disks", "\"mall\" records", "\"mall\" CDs" } },
			{ { "\"mellohi\" music discs", "\"mellohi\" disks", "\"mellohi\" records", "\"mellohi\" CDs" } },
			{ { "\"stal\" music discs", "\"stal\" disks", "\"stal\" records", "\"stal\" CDs" } },
			{ { "\"strad\" music discs", "\"strad\" disks", "\"strad\" records", "\"strad\" CDs" } },
			{ { "\"ward\" music discs", "\"ward\" disks", "\"ward\" records", "\"ward\" CDs" } },
			{ { "\"11\" music discs", "\"11\" disks", "\"11\" records", "\"11\" CDs" } }, { { "\"wait\" music discs", "\"wait\" disks", "\"wait\" records", "\"wait\" CDs" } } },
			entity_IDs = {
					null,
					{ { "dropped items", "miniblocks", "miniitems", "floating dropped thrown chucked tossed stuff things miniblocks miniitems" } },
					{ { "experience orbs", "experience balls" } },
					{ { "paintings", "wall framed paintings" } },
					{ { "flying arrows", "shot fired arrows" } },
					{ { "thrown snowballs", "chucked tossed thrown snowballs balls of snow" } },
					{ { "Ghast fireballs", "Ghast fired shot fireballs cannonballs explosive exploding fire charges" } },
					{ { "Blaze fireballs", "Ghast fired shot fireballs cannonballs explosive exploding fire charges" } },
					{ { "thrown Ender Pearls", "Enderman Endermen thrown chucked fired Ender Pearls Enderpearls" } },
					{ { "throw Eyes of Ender", "Enderman Endermen thrown chucked fired Eyes of Ender Endereyes" } },
					{ { "thrown splash potions", "thrown chucked fired used potions pots" } },
					{ { "thrown Bottles o' Enchanting", "xp bottle", "thrown chucked fired used Bottles glasses o' of Enchanting experience levels" } },
					{ { "item frames" } },
					{ { "Wither skull projectiles", "Wither bosses boss's skulls projectiles fired heads explosives exploding" } },
					{ { "primed T.N.T.", "lit T.N.T.", "activated T.N.T.", "primed TNT", "lit TNT", "activated TNT", "primed trinitrotoluene", "lit trinitrotoluene",
							"activated trinitrotoluene" } }, { { "falling blocks", "falling gravel", "falling sand", "falling anvils", "falling dragon eggs" } },
					{ { "fireworks", "firework rockets" } }, { { "boats", "dinghies", "dinghys", "ships" } }, { { "minecarts" } },
					{ { "storage minecarts", "minecrafts with storage", "minecarts with chests" } },
					{ { "powered minecarts", "gas powered minecarts", "coal powered minecarts", "furnace minecarts" } },
					{ { "T.N.T. minecarts", "explosive minecarts", "TNT minecarts", "trinitrotoluene minecarts" } },
					{ { "hopper minecarts", "minecarts with hoppers", "vacuum minecarts" } },
					{ { "spawner minecarts", "minecarts with spawners", "minecarts with monster spawners" } }, { { "generic mobs", "mobs" } },
					{ { "generic monsters", "monsters" } }, { { "creepers", "exploding green penis monsters", "explosive green penis monsters" } },
					{ { "skeletons", "skeles", "skeleton archers" }, { "Wither skeletons", "Wither skeles", "Wither skeletons", "swordsman skeletons", "swordsman skeles" } },
					{ { "spiders", "giant spiders" } }, { { "giants", "giant zombies" } }, { { "zombies" } }, { { "slimes", "slime cubes", "living slime" } },
					{ { "Ghasts", "giant floating jellyfish", "giant flying jellyfish", "giant floating squids", "giant flying squids" } },
					{ { "zombie pigmen", "zombie pigman", "pigman zombies" } }, { { "Endermen", "Enderman" } },
					{ { "cave spiders", "small spiders", "poisonous spiders", "venomous spiders" } },
					{ { "silverfish", "bugs", "stronghold silverfish", "stronghold bugs" } },
					{ { "Blazes", "Nether dungeon guards", "Nether stronghold guards", "Nether dungeon monsters", "Nether stronghold monsters" } },
					{ { "Magma Cubes", "lava cubes", "Nether slimes", "living magma", "living lava" } }, { { "The Ender Dragon", "the big black scary dragon" } },
					{ { "The Wither", "The Wither boss" } }, { { "bats" } }, { { "witches", "wicked witches" } }, { { "pigs", "piggies" } }, { { "sheep", "lambs" } },
					{ { "cows" } }, { { "chickens", "chicks" } }, { { "squid", "squids", "octopi", "octopuses" } }, { { "wolves", "dogs", "wolfs", "hounds" } },
					{ { "Mooshroom cows", "mooshrooms" } }, { { "snow golems", "living snowmen" } }, { { "ocelots", "jungle cats" } },
					{ { "iron golems", "N.P.C. village guards", "NPC village guards", "Testificates village guards" } },
					{ { "villagers", "N.P.C.s", "NPCs", "Testificates" } }, { { "Ender crystals", "Ender Dragon shield generators" } } };
	// there gap arrays are here to compensate for the gaps in I.D.s; for example, from the block I.D.s to the item I.D.s (starting at item #159 in this list
	// since 158 is the last block I.D.), there is a 98-number gap.
	// there are two numbers in each item in this list: the item I.D. of the last item before the gap and the item I.D. of the first item after the gap
	// the point of this is to avoid what I had originally, which was just an insanely long list of null values in item_IDs itself
	private static short[][] item_gaps = { { 158, 256 }, { 408, 2256 } }, entity_gaps = { { 2, 9 }, { 22, 41 }, { 66, 90 }, { 99, 120 }, { 120, 200 } }, potion_data_gaps = {
			{ 0, 16 }, { 16, 32 }, { 32, 64 }, { 64, 8193 }, { 8206, 8225 }, { 8229, 8233 }, { 8236, 8257 }, { 8266, 8270 }, { 8270, 16385 }, { 16398, 16417 },
			{ 16421, 16425 }, { 16428, 16449 }, { 16458, 16462 } }, spawn_egg_data_gaps = { { 0, 50 }, { 66, 90 }, { 98, 120 } };

	// TODO: test

	public static Integer[] getItemIdAndData(String[] item_name) {
		Integer result_id = null, result_data = null, result_i = null;
		for (int id = 0; id < item_IDs.length; id++)
			if (item_IDs[id] != null)
				for (int data = 0; data < item_IDs[id].length; data++)
					if (item_IDs[id][data] != null)
						for (int i = 0; i < item_IDs[id][data].length; i++) {
							boolean contains_query = true;
							for (String word : item_name)
								if (!item_IDs[id][data][i].toLowerCase().contains(word.toLowerCase())) {
									contains_query = false;
									break;
								}
							// translation of this if statement: if the item contains the query and either we haven't found another result yet, the old result
							// has a longer name than this new one, or the length of the names is the same but this new result is an item I.D. while the old one
							// is a block I.D., then change the current result to this new item
							if (contains_query
									&& (result_id == null || item_IDs[result_id][result_data][result_i].length() > item_IDs[id][data][i].length() || (item_IDs[result_id][result_data][result_i]
											.length() == item_IDs[id][data][i].length()
											&& result_id < 256 && id >= 256))) {
								result_id = id;
								result_data = data;
								result_i = i;
							}
						}
		if (result_id == null)
			return null;
		// subtract 1 from the data to get the real data (remember: [0] is the general name and [1] is data = 0)
		result_data -= 1;
		// now we need to adjust the final result based on the gaps in I.D.s
		if (result_id != null) {
			// for the I.D. gaps
			for (short[] gap : item_gaps)
				if (result_id > gap[0])
					result_id += (gap[1] - gap[0]);
				else
					break;
			// for the potion data values gaps
			if (result_id == 373)
				for (short[] gap : potion_data_gaps)
					if (result_data > gap[0])
						result_data += (gap[1] - gap[0]);
					else
						break;
			// for the spawn egg data values gaps
			else if (result_id == 383)
				for (short[] gap : spawn_egg_data_gaps)
					if (result_data > gap[0])
						result_data += (gap[1] - gap[0]);
					else
						break;
		}
		return new Integer[] { result_id, result_data };
	}

	public static String getItemName(int id, int data, boolean give_data_suffix) {
		// we need to adjust the query I.D.s based on the gaps in I.D.s
		// for the potion data values gaps
		if (id == 373)
			for (int i = potion_data_gaps.length - 1; i > 0; i--)
				if (data > potion_data_gaps[i][1])
					data -= (potion_data_gaps[i][1] - potion_data_gaps[i][0]);
				else
					break;
		// for the spawn egg data values gaps
		else if (id == 383)
			for (int i = spawn_egg_data_gaps.length - 1; i > 0; i--)
				if (data > spawn_egg_data_gaps[i][1])
					data -= (spawn_egg_data_gaps[i][1] - spawn_egg_data_gaps[i][0]);
				else
					break;
		// for the item gaps
		for (int i = item_gaps.length - 1; i > 0; i--)
			if (id > item_gaps[i][1])
				id -= (item_gaps[i][1] - item_gaps[i][0]);
			else
				break;
		String item = null;
		try {
			item = item_IDs[id][data + 1][0];
		} catch (NumberFormatException exception) {
			try {
				item = item_IDs[id][data % 4][0];
			} catch (NumberFormatException exception2) {
				item = item_IDs[id][0][0];
			}
		}
		if (item != null && give_data_suffix && data != -1)
			item += " (" + data + ")";
		else if (item == null) {
			item = "something with the I.D. " + id;
			if (give_data_suffix && data > 0)
				item += ":" + data;
		}
		return item;
	}

	public static Integer[] getEntityIdAndData(String[] item_name) {
		Integer result_id = null, result_data = null, result_i = null;
		for (int id = 0; id < entity_IDs.length; id++)
			if (entity_IDs[id] != null)
				for (int data = 0; data < entity_IDs[id].length; data++)
					if (entity_IDs[id][data] != null)
						for (int i = 0; i < entity_IDs[id][data].length; i++) {
							boolean contains_query = true;
							for (String word : item_name)
								if (!entity_IDs[id][data][i].toLowerCase().contains(word.toLowerCase())) {
									contains_query = false;
									break;
								}
							// translation of this if statement: if the item contains the query and either we haven't found another result yet, the old result
							// has a longer name than this new one, or the length of the names is the same but this new result is an item I.D. while the old one
							// is a block I.D., then change the current result to this new item
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
			return null;
		result_data -= 1;
		// now we need to adjust the final result based on the gaps in I.D.s
		if (result_id != null)
			for (short[] gap : item_gaps)
				if (result_id > gap[0])
					result_id += (gap[1] - gap[0]);
				else
					break;
		return new Integer[] { result_id, result_data };
	}

	public static String getEntityName(int id, int data, boolean give_data_suffix) {
		// we need to adjust the query I.D.s based on the gaps in I.D.s for the entity I.D. gaps
		for (int i = entity_gaps.length - 1; i > 0; i--)
			if (id > entity_gaps[i][1])
				id -= (entity_gaps[i][1] - entity_gaps[i][0]);
			else
				break;
		String entity = null;
		try {
			entity = entity_IDs[id][data + 1][0];
		} catch (NumberFormatException exception) {
			entity = entity_IDs[id][0][0];
		}
		if (entity != null && give_data_suffix && data != -1)
			entity += " (" + data + ")";
		else if (entity == null) {
			entity = "something with the I.D. " + id;
			if (give_data_suffix && data > 0)
				entity += ":" + data;
		}
		return entity;
	}

	public static String getRecipe(int id, int data) {
		// TODO
		return ChatColor.GOLD + "Coming soon to a server near you!";
	}
}