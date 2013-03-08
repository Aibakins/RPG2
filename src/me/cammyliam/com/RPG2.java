package me.cammyliam.com;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RPG2 extends JavaPlugin implements Listener {
	public Plugin plugin;
	public Random rand = new Random();

	@Override
	public void onDisable() {
		System.out.println("Plugin disabled.");
	}

	public int returnHealthP(String weapon) {
		return getConfig().getInt("Items." + weapon + ".HP");
	}

	public int returnDodgeP(String weapon) {
		return getConfig().getInt("Items." + weapon + ".Dodge");
	}

	public int returnCFGDodge(Player player) {
		return getConfig().getInt("Players." + player.getName() + ".Dodge");
	}

	public int returnHRegenP(String weapon) {
		return getConfig().getInt("Items." + weapon + ".HealthRegen");
	}

	public int returnSRegenP(String weapon) {
		return getConfig().getInt("Items." + weapon + ".Stamina");
	}

	public void upd(String loc, Object value) {
		getConfig().set(loc, value);
		saveConfig();
	}

	public int returnDamage(String weapon) {
		int ret = 0;
		if (getConfig().isSet("Items." + weapon)) {
			ret = getConfig().getInt("Items." + weapon + ".Damage");
		}
		return ret;
	}

	public int returnLifeSteal(String weapon) {
		int ret = 0;
		if (getConfig().isSet("Items." + weapon)) {
			ret = getConfig().getInt("Items." + weapon + ".LifeSteal");
		}
		return ret;
	}

	public void spawnPet(Player player, String type) {
		EntityType monster = EntityType.fromName(type.toUpperCase());
		if (monster == EntityType.WOLF) {
			Wolf mob = (Wolf) Bukkit.getWorld(player.getWorld().getName()).spawnEntity(player.getLocation(), monster);
			upd("Mobs." + mob.getEntityId(), player.getName());
			mob.setAngry(false);
			mob.setSitting(false);
			mob.setOwner(player);
			mob.setTarget(player);
			mob.setAgeLock(true);
			mob.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 72000, 1));
		} else if (monster == EntityType.ZOMBIE) {
			Zombie mob = (Zombie) Bukkit.getWorld(player.getWorld().getName()).spawnEntity(player.getLocation(), monster);
			upd("Mobs." + mob.getEntityId(), player.getName());
			mob.setTarget(player);
			mob.setBaby(true);
			mob.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 72000, 1));
		}
	}

	public void spawnBoss(Location location, String type) {
		if (getConfig().isSet("Bosses.Config." + type.toUpperCase())) {
			EntityType ent = EntityType.fromName(type.toUpperCase());
			if (ent == EntityType.SKELETON) {
				Skeleton mob = (Skeleton) Bukkit.getWorld(location.getWorld().getName()).spawnEntity(location, ent);
				mob.setMaxHealth(getConfig().getInt("Bosses.Config.SKELETON.Health"));
				mob.setHealth(mob.getMaxHealth());
				upd("Bosses.Entitys." + mob.getEntityId() + ".Alive", true);
			} else if (ent == EntityType.GIANT) {
				Giant mob = (Giant) Bukkit.getWorld(location.getWorld().getName()).spawnEntity(location, ent);
				mob.setMaxHealth(getConfig().getInt("Bosses.Config.GIANT.Health"));
				mob.setHealth(mob.getMaxHealth());
				upd("Bosses.Entitys." + mob.getEntityId() + ".Alive", true);
			} else if (ent == EntityType.WITCH) {
				Witch mob = (Witch) Bukkit.getWorld(location.getWorld().getName()).spawnEntity(location, ent);
				Bat bat = (Bat) Bukkit.getWorld(location.getWorld().getName()).spawnEntity(location, EntityType.BAT);
				mob.setMaxHealth(getConfig().getInt("Bosses.Config.WITCH.Health"));
				mob.setHealth(mob.getMaxHealth());
				bat.setMaxHealth(getConfig().getInt("Bosses.Config.WITCH.Health"));
				bat.setHealth(mob.getMaxHealth());
				bat.setPassenger(mob);
				bat.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 72000, 1));
				upd("Bosses.Entitys." + mob.getEntityId() + ".Alive", true);
			}
		}
	}

	public int money(Player player) {
		int c = 0;
		for (ItemStack i : player.getInventory().getContents()) {
			if (i != null) {
				if (i.getType() == Material.GOLD_NUGGET) {
					c = c + i.getAmount();
				}
			}
		}
		return c;
	}
	public void rmoney(Player player, int xam) {
		int rem = 0;
		for (ItemStack i : player.getInventory().getContents()) {
			if (i != null) {
				if (i.getType() == Material.GOLD_NUGGET) {
					if (rem < xam) { 
						if (i.getAmount() >= xam) {
							rem = xam;
							i.setAmount(i.getAmount() - xam);
						} else {
							rem = i.getAmount();
							i.setType(Material.AIR);
						}
					}
				}
			}
		}
	}
	public void amoney(Player player, int xam) {
		player.getInventory().addItem(new ItemStack(Material.GOLD_NUGGET, xam));
	}

	public double locDiff(Location a, Location b) {
		return ((a.distance(b))/10);
	}

	public ItemStack returnItem(String name, int amount, short dura) {
		ItemStack is = null;
		ItemMeta im;
		ArrayList<String> lore;
		String d = "";
		if (getConfig().isSet("Items." + name)) {
			is = new ItemStack(Material.getMaterial(getConfig().getInt("Items." + name + ".Actual-Item")), 1);
			im = is.getItemMeta();
			im.setDisplayName(name);
			lore = new ArrayList<String>();
			lore.add(ChatColor.BLUE + getConfig().getString("Items." + name + ".Lore"));
			if (getConfig().getInt("Items." + name + ".Damage") != 0) {
				lore.add(ChatColor.RED + "Damage: " + getConfig().getInt("Items." + name + ".Damage"));
			}
			if (getConfig().getInt("Items." + name + ".HP") != 0) {
				lore.add(ChatColor.GREEN + "Health+: " + getConfig().getInt("Items." + name + ".HP"));
			}
			if (getConfig().getInt("Items." + name + ".HealthRegen") != 0) {
				lore.add(ChatColor.DARK_PURPLE + "Health regen p/sec: " + getConfig().getInt("Items." + name + ".HealthRegen") + "%");
			}
			if (getConfig().getInt("Items." + name + ".Stamina") != 0) {
				lore.add(ChatColor.LIGHT_PURPLE + "Stamina regen p/sec: " + getConfig().getInt("Items." + name + ".Stamina") + "%");
			}
			if (getConfig().getInt("Items." + name + ".LifeSteal") != 0) {
				lore.add(ChatColor.DARK_RED + "Life Steal: " + getConfig().getInt("Items." + name + ".LifeSteal"));
			}
			if (getConfig().getInt("Items." + name + ".Dodge") != 0) {
				lore.add(ChatColor.DARK_GREEN + "Dodge+: " + getConfig().getInt("Items." + name + ".Dodge"));
			}
			if (dura == 0) {
				if (getConfig().getInt("Items." + name + ".Dura") != 0) {
					is.setDurability((short) (getConfig().getInt("Items." + name + ".Dura")));
				}
			} else {
				is.setDurability(dura);
			}

			if (d != "") {
				lore.add(d);
			}
			im.setLore(lore);
			is.setItemMeta(im);
		} else {
			try {
				is = new ItemStack(Material.getMaterial(name.toUpperCase()), amount);
			} catch (Exception e) {
				is = new ItemStack(Material.AIR);
			}
		}
		return is;
	}

	public Location getSpawn() {
		String[] i = getConfig().getString("Options.first-time-join").split(",");
		return new Location(Bukkit.getWorld(i[0]), Integer.parseInt(i[1]), Integer.parseInt(i[2]), Integer.parseInt(i[3]));
	}
	public Location getRespawn() {
		String[] i = getConfig().getString("Options.respawn").split(",");
		return new Location(Bukkit.getWorld(i[0]), Integer.parseInt(i[1]), Integer.parseInt(i[2]), Integer.parseInt(i[3]));
	}
	public Location getHearthstone(Player player) {
		String[] i = getConfig().getString("Players." + player.getName() + ".hearthstone").split(",");
		return new Location(Bukkit.getWorld(i[0]), Integer.parseInt(i[1]), Integer.parseInt(i[2]), Integer.parseInt(i[3]));
	}

	public void newHealth(Player player) {
		int h = getConfig().getInt("Options.default-health");
		int c = player.getMaxHealth();
		int r = 0;
		float s = 5;
		int d = 0;
		for (ItemStack item : player.getInventory().getContents()) {
			if (item != null) {
				if (item.getItemMeta().hasDisplayName()) {
					h = h + (returnHealthP(item.getItemMeta().getDisplayName()) * item.getAmount());
					r = r + (returnHealthP(item.getItemMeta().getDisplayName()) * item.getAmount());
					s = s + (returnSRegenP(item.getItemMeta().getDisplayName()) * item.getAmount());
					d = d + (returnDodgeP(item.getItemMeta().getDisplayName()) * item.getAmount());
				}
			}
		}
		ItemStack[] armor = {player.getInventory().getHelmet(), player.getInventory().getChestplate(), player.getInventory().getLeggings(), player.getInventory().getBoots()};
		for (ItemStack item : armor) {
			if (item != null) {
				if (item.getItemMeta().hasDisplayName()) {
					h = h + (returnHealthP(item.getItemMeta().getDisplayName()) * item.getAmount());
					r = r + (returnHealthP(item.getItemMeta().getDisplayName()) * item.getAmount());
					s = s + (returnSRegenP(item.getItemMeta().getDisplayName()) * item.getAmount());
					d = d + (returnDodgeP(item.getItemMeta().getDisplayName()) * item.getAmount());
				}
			}
		}
		if (h != c) {
			player.setMaxHealth(h);
		}
		player.setLevel(player.getHealth());

		try {
			if (player.getHealth()+((player.getMaxHealth()/player.getHealth())*r) > player.getMaxHealth()) {
				player.setHealth(player.getMaxHealth());
			} else {
				player.setHealth(player.getHealth()+((player.getMaxHealth()/player.getHealth())*r));
			}
		} catch(Exception e) {}
		if (player.getFoodLevel()+(0.1 * s) > 20) {
			player.setFoodLevel(20);
		} else {
			player.setFoodLevel((int) (player.getFoodLevel()+(0.1 * s)));
		}
		if (d > 148) {
			d = 149;
		}
		upd("Players." + player.getName() + ".Dodge", d);
		
		for (ItemStack i : player.getInventory().getContents()) { //This also updates the stats book now
			if (i != null) {
				if (i.getType() == Material.WRITTEN_BOOK) {
					BookMeta bm = (BookMeta) i.getItemMeta();
					if (bm.getTitle() == "Stats") {
						bm.setAuthor("Server");
						bm.setPages(line(" ") + line("Your stats..") + line("Max health: " + h) + line("Health regen: " + r) + line("Stamina regen: " + s) + line("Dodge rating: " + d));
						i.setItemMeta(bm);
					}
				}
			}
		}
	}
	
	public String line(String inp) {
		int c = 0;
		String o = "";
		for (char a : inp.toCharArray()) {
			o = o + a;
			c++;
		}
		while (c<=23) {
		    o = o + " ";
		    c++;
		}
		return o;
	}

	@Override
	public void onEnable() {
		System.out.println("Plugin enabled.");
		this.getServer().getPluginManager().registerEvents(this, this);
		if (!new File(getDataFolder(), "config.yml").exists()) {
			saveDefaultConfig();
		}

		if (!getConfig().isSet("Options")) {
			this.upd("Options.default-health", 20);
			this.upd("Options.default-bankslots", 9);
			this.upd("Options.first-time-join", "world,-140,4,-23");
			this.upd("Options.respawn", "world,-140,4,-23");
			this.upd("Options.building", false);
			this.upd("Options.EXPtoGold", true);
			this.upd("Options.Anvil.Price", 13);
			this.upd("Options.Anvil.DuraToAdd", 60);
			this.upd("Options.Tools.HearthstoneID", 399);
		}
		if (!getConfig().isSet("Loot")) {
			this.upd("Loot.defaultloot", "GOLD_NUGGET,");
		}
		if (!getConfig().isSet("Mobs")) {
			this.upd("Mobs.ChangeHealthByRange", true); //If false, it will use the config below instead
			this.upd("Mobs.ZOMBIE.Drops", "Regener,GOLD_NUGGET,GOLD_NUGGET"); //Example showing that you can use custom items if they're defined.
			this.upd("Mobs.CREEPER.Drops", "GOLD_NUGGET,GOLD_NUGGET");
			this.upd("Mobs.SPIDER.Drops", "GOLD_NUGGET,");
			this.upd("Mobs.SKELETON.Drops", "GOLD_NUGGET,");
			this.upd("Mobs.ZOMBIE.Health", 25); //This allows you to change the mobs health
			this.upd("Mobs.CREEPER.Health", 30);
			this.upd("Mobs.SPIDER.Health", 20);
			this.upd("Mobs.SKELETON.Health", 35);
		}
		if (!getConfig().isSet("Bosses")) {
			this.upd("Bosses.Config.SKELETON.Health", 200); //Boss health
			this.upd("Bosses.Config.SKELETON.Loot", "GOLD_NUGGET,GOLD_NUGGET,Regener"); //Boss health
			this.upd("Bosses.Config.GIANT.Health", 400); //Boss health
			this.upd("Bosses.Config.GIANT.Loot", "GOLD_NUGGET,GOLD_NUGGET"); //Boss health
			this.upd("Bosses.Config.GIANT.Damage", 10); //Boss health
			this.upd("Bosses.Config.WITCH.Health", 1000); //Boss health
			this.upd("Bosses.Config.WITCH.Loot", "GOLD_NUGGET,GOLD_NUGGET,Regener"); //Boss health
		}
		if (!getConfig().isSet("Items")) {
			this.upd("Items.Regener.Lore", "Scroll of Regeneration");
			this.upd("Items.Regener.Actual-Item", 276); //item texture
			this.upd("Items.Regener.Dura", 200); //Custom durability, 0 for default (broken)
			this.upd("Items.Regener.Value", 24); //Cost of item
			this.upd("Items.Regener.Damage", 20); //How much damage the weapon done
			this.upd("Items.Regener.HP", 10); //Health to add on the default
			this.upd("Items.Regener.Stamina", 8); //How much stamina it'll add in percentage p/sec
			this.upd("Items.Regener.HealthRegen", 8); //How much health it'll add in percentage p/sec
			this.upd("Items.Regener.LifeSteal", 4); //How much life you take from a player/mob and adds to yours
			this.upd("Items.Regener.Dodge", 0); //How much your dodge rating will increase
		}

		/*this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				float temp;
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (getConfig().isSet("Players." + player.getName())) {
						temp = returnCHealth(player) / returnMHealth(player);
						temp = temp * returnMHealth(player);
						temp = temp / (returnCHealth(player) / 20);
						//temp = (((returnCHealth(player) / returnMHealth(player)) * returnMHealth(player))/ (returnMHealth(player) / 20));
						player.setHealth((int)temp);
					}
				}
			}
		}, 10, 10);*/
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					newHealth(player);
				}
			}
		}, 20, 20);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		//remRage(player, rand.nextInt(2));
		if (player.isSprinting()) {
			if (player.getFoodLevel() > 3) {
				player.setFoodLevel(player.getFoodLevel() - rand.nextInt(2));
			}
		}
		for (Entity ent : player.getNearbyEntities(2, 3, 2)) {
			if (ent.getType() == EntityType.GIANT) {
				player.damage(getConfig().getInt("Bosses.Config.GIANT.Damage"));
			}
		}
	}

	/*public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Entity en = event.getRightClicked();
	}*/

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (!getConfig().isSet("Players." + player.getName())) {
			player.teleport(new Location(Bukkit.getWorld("world"), -140, 4, -23));
			upd("Players." + player.getName() + ".dropto", "null");
			upd("Players." + player.getName() + ".Looted", "0,0,0,world_");
			upd("Players." + player.getName() + ".Bank", getConfig().getInt("Options.default-bankslots"));
			upd("Players." + player.getName() + ".hearthstone", "null");
			ItemStack is;
			ItemMeta im;
			ArrayList<String> lore;
			is = new ItemStack(Material.getMaterial(getConfig().getInt("Options.Tools.HearthstoneID")), 1);
			im = is.getItemMeta();
			im.setDisplayName("Hearthstone");
			lore = new ArrayList<String>();
			lore.add(ChatColor.GOLD + "Use this item to return to the Hearthstone home.");
			im.setLore(lore);
			is.setItemMeta(im);
			player.getInventory().addItem(is);
			ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
			BookMeta meta = (BookMeta) book.getItemMeta();
			meta.setTitle("Stats");
			meta.setAuthor("Server");
			meta.addPage("HELLO!");
			book.setItemMeta(meta);
			player.getInventory().addItem(book);
		}
		if (player.isOp()) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.showPlayer(player);
			}
			player.sendMessage(ChatColor.YELLOW + "You are currently visable.");
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		event.setRespawnLocation(getRespawn());
	}

	@SuppressWarnings({ "unused", "deprecation" })
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack is;
		ItemMeta im;
		ArrayList<String> lore;

		if (event.getAction() == Action.RIGHT_CLICK_AIR) {
			if (player.getItemInHand().getItemMeta().hasDisplayName()) {
				if (player.getItemInHand().getItemMeta().getDisplayName().equals("Hearthstone")) {
					try {
						player.teleport(getHearthstone(player));
						player.sendMessage("You have been teleported to your Hearthstone's location.");
					} catch (Exception e) {
						player.sendMessage("You might not have set your Hearthstone location.");
					}
				}
			}
		}

		if (event.getClickedBlock() == null) return;
		Block block = event.getClickedBlock();

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (block.getType() == Material.ENDER_CHEST) {
				player.sendMessage("Bank chest opened.");
				int s = 0;
				String[] d;
				event.setCancelled(true);
				Inventory inv = Bukkit.createInventory(player, getConfig().getInt("Options.default-bankslots"), player.getName() + "'s Bank");
				if (getConfig().isSet("Players." + event.getPlayer().getName() + ".bankinv")) {
					for (ItemStack i : inv.getContents()) {
						if (getConfig().getString("Players." + event.getPlayer().getName() + ".bankinv.slot" + s) != "nothing") {
							d = getConfig().getString("Players." + event.getPlayer().getName() + ".bankinv.slot" + s).split(",");
							if (d[0].startsWith("Bank Cheque")) {
								is = new ItemStack(Material.PAPER, Integer.parseInt(d[1]));
								im = is.getItemMeta();
								im.setDisplayName(d[0]);
								lore = new ArrayList<String>();
								lore.add(ChatColor.GOLD + "This can be cashed in for Gold");
								lore.add(ChatColor.GOLD + "by left clicking on a bank sign.");
								im.setLore(lore);
								is.setItemMeta(im);
								inv.setItem(s, is);
							} else {
								inv.setItem(s, returnItem(d[0], Integer.parseInt(d[1]), Short.parseShort(d[2])));
							}
							s++;
						}
					}
				}
				player.openInventory(inv);
			}
			if (block.getType() == Material.CHEST) {
				if (!getConfig().isSet("Players." + player.getName() + ".Looted")) {
					upd("Players." + player.getName() + ".Looted", "0,0,0,world_");
				}
				Chest chest = (Chest) block.getState();
				Inventory inv = chest.getInventory();
				Location loc = block.getLocation();
				String blockloc = loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "," + loc.getWorld().getName();
				if (!getConfig().getString("Players." + player.getName() + ".Looted").contains(blockloc + "_")) {
					if (getConfig().isSet("Loot." + blockloc)) {
						for (String item : getConfig().getString("Loot." + blockloc).split(",")) {
							try {
								inv.addItem(returnItem(item, 1, Short.parseShort("0")));
							} catch(Exception e) {}
						}
					} else {
						for (String item : getConfig().getString("Loot.defaultloot").split(",")) {
							try {
								inv.addItem(returnItem(item, 1, Short.parseShort("0")));
							} catch(Exception e) {}
						}
					}
					upd("Players." + player.getName() + ".Looted", getConfig().getString("Players." + player.getName() + ".Looted") + blockloc + "_");
				}
			}
			if (block.getType() == Material.ANVIL) {
				event.setCancelled(true);
				if (money(player) >= getConfig().getInt("Options.Anvil.Price")) {
					rmoney(player, getConfig().getInt("Options.Anvil.Price"));
					player.getItemInHand().setDurability((short) (player.getItemInHand().getDurability() - getConfig().getInt("Options.Anvil.DuraToAdd")));
					player.sendMessage("Weapon durability increase by " + getConfig().getInt("Options.Anvil.DuraToAdd") + " for " + getConfig().getInt("Options.Anvil.Price") + " Gold.");
				} else {
					player.sendMessage("This weapon can't be fixed because you don't have enough Gold.");
					player.sendMessage("It costs " + getConfig().getInt("Options.Anvil.Price") + " Gold to repair.");
				}
			}
			if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) {
				Sign sign = (Sign) block.getState();
				if (sign.getLine(0).equalsIgnoreCase("[buy]")) {
					if (money(player) >= Integer.parseInt(sign.getLine(2))) {
						try {
							rmoney(player, Integer.parseInt(sign.getLine(2)));
							player.getInventory().addItem(returnItem(sign.getLine(1), 1, Short.parseShort("0")));
							player.sendMessage("You bought " + sign.getLine(1) + " for " + sign.getLine(2) + " Gold.");
						} catch(Exception e) {
							player.sendMessage("There was problem trying to buy this item.");
						}
					} else {
						player.sendMessage("You don't have enough to buy this item.");
					}
				}
				if (sign.getLine(0).equalsIgnoreCase("[hearthstone]")) {
					try {
						player.sendMessage("You set Hearthstone to " + sign.getLine(1) + ".");
						Location loc = player.getLocation();
						String loca = loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
						upd("Players." + player.getName() + ".hearthstone", loca);
					} catch (Exception e) {
						player.sendMessage("There was a problem setting your Hearthstone here.");
					}
				}
				if (sign.getLine(0).equalsIgnoreCase("[bank]")) {
					if (sign.getLine(1).equalsIgnoreCase("bank cheque")) {
						if (money(player) >= Integer.parseInt(sign.getLine(2))) {
							rmoney(player, Integer.parseInt(sign.getLine(2)));
							is = new ItemStack(Material.PAPER, 1);
							im = is.getItemMeta();
							im.setDisplayName("Bank Cheque of " + sign.getLine(2) + " Gold");
							lore = new ArrayList<String>();
							lore.add(ChatColor.GOLD + "This can be cashed in for Gold");
							lore.add(ChatColor.GOLD + "by left clicking on a bank sign.");
							im.setLore(lore);
							is.setItemMeta(im);
							player.getInventory().addItem(is);
							player.sendMessage("Bank Cheque created and stored in your inventory.");
							player.updateInventory();
						} else {
							player.sendMessage("You don't have " + sign.getLine(2) + " Gold, so you can't create a Cheque.");
						}
					}
				}
			}
		}

		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) {
				Sign sign = (Sign) block.getState();
				if (sign.getLine(0).equalsIgnoreCase("[bank]")) {
					if (sign.getLine(1).equalsIgnoreCase("bank cheque")) {
						if (player.getItemInHand() != null) {
							try {
								if (player.getItemInHand().getItemMeta().hasDisplayName()) {
									if (player.getItemInHand().getItemMeta().getDisplayName().startsWith("Bank Cheque")) {
										String[] d = player.getItemInHand().getItemMeta().getDisplayName().split(" ");
										try {
											amoney(player, Integer.parseInt(d[3]));
											if (player.getItemInHand().getAmount() > 1) {
												player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
											} else {
												player.setItemInHand(new ItemStack(Material.AIR));
											}
											player.updateInventory();
											player.sendMessage("Thank you cashing this cheque.");
										} catch (Exception e) {
											player.sendMessage("There was an error trying to cash your cheque.");
										}
									}
								}
							} catch (Exception e) {}
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onInventoryClose(InventoryCloseEvent event) {
		if (event.getInventory().getTitle().equals(event.getPlayer().getName() + "'s Bank")){
			int s = 0;
			for (ItemStack i : event.getInventory().getContents()) {
				upd("Players." + event.getPlayer().getName() + ".bankinv.slot" + s, "nothing");
				if (i != null) {
					if (i.getItemMeta().hasDisplayName()) {
						upd("Players." + event.getPlayer().getName() + ".bankinv.slot" + s, i.getItemMeta().getDisplayName() + "," + i.getAmount() + "," + i.getDurability());
					} else {
						upd("Players." + event.getPlayer().getName() + ".bankinv.slot" + s, i.getType().name().toUpperCase() + "," + i.getAmount() + "," + i.getDurability());
					}
				}
				s++;
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player given = (Player) event.getDamager();
			int Damage = event.getDamage();
			try {
				if (given.getItemInHand().getItemMeta().hasDisplayName()) {
					try {
						Damage = returnDamage(given.getItemInHand().getItemMeta().getDisplayName()) - rand.nextInt(returnDamage(given.getItemInHand().getItemMeta().getDisplayName()) / 3);
					} catch (Exception e) {Damage = event.getDamage();}
					if (returnLifeSteal(given.getItemInHand().getItemMeta().getDisplayName()) != 0) {
						Damage = Damage + returnLifeSteal(given.getItemInHand().getItemMeta().getDisplayName());
						given.setHealth(given.getHealth() + returnLifeSteal(given.getItemInHand().getItemMeta().getDisplayName()));
					}
				}
			} catch (Exception e) {}
			event.setDamage(Damage);
			if (Damage > 0) {
				given.sendMessage(ChatColor.MAGIC + "-- " + ChatColor.RED + "Damage given: " + Damage + ChatColor.WHITE + " " + ChatColor.MAGIC + "--");
			}
		}
		if (event.getEntity() instanceof Player) {
			int Damage = event.getDamage();
			Player taken = (Player) event.getEntity();
			taken.setLevel(taken.getHealth());
			if (rand.nextInt(150 - returnCFGDodge(taken)) == 1) {
				Damage = 0;
			}
			event.setDamage(Damage);
			if (Damage > 0) {
				taken.sendMessage(ChatColor.MAGIC + "-- " + ChatColor.RED + "Damage recieved: " + event.getDamage() + ChatColor.WHITE + " " + ChatColor.MAGIC + "--");
			}
		}
		if (event.getEntity() instanceof Skeleton) {
			Entity ent = event.getEntity();
			if (getConfig().isSet("Bosses.Entitys." + ent.getEntityId())) {
				if (getConfig().getBoolean("Bosses.Entitys." + ent.getEntityId() + ".Alive") == true) {
					Bukkit.getWorld(ent.getLocation().getWorld().getName()).spawnEntity(ent.getLocation(), EntityType.SILVERFISH);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onEntityDeath(EntityDeathEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			String[] d;
			if (getConfig().getBoolean("Options.EXPtoGold")) {
				event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), new ItemStack(Material.GOLD_NUGGET, event.getDroppedExp()));
				event.setDroppedExp(0);
			}
			if (getConfig().isSet("Bosses.Entitys." + event.getEntity().getEntityId())) {
				upd("Bosses.Entitys." + event.getEntity().getEntityId() + ".Alive", false);
				d = getConfig().getString("Bosses.Config." + event.getEntityType().name().toUpperCase() + ".Loot").split(",");
				try {
					event.getDrops().clear();
					event.getDrops().add(returnItem(d[rand.nextInt(d.length)], 1, Short.parseShort("0")));
				} catch (Exception e) {} 
			} 
			else if (getConfig().isSet("Mobs." + event.getEntityType().getName().toUpperCase() + ".Drops")) {
				d = getConfig().getString("Mobs." + event.getEntityType().getName().toUpperCase() + ".Drops").split(",");
				try {
					event.getDrops().clear();
					event.getDrops().add(returnItem(d[rand.nextInt(d.length)], 1, Short.parseShort("0")));
				} catch (Exception e) {} 
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onEntitySpawn(CreatureSpawnEvent event) {
		Entity ent = event.getEntity();
		if (getConfig().isSet("Mobs." + event.getEntityType().getName().toUpperCase())) {
			if (event.getEntityType() == EntityType.ZOMBIE) {
				Zombie mob = (Zombie) ent;
				if (getConfig().getBoolean("Mobs.ChangeHealthByRange") && ent.getWorld() == getSpawn().getWorld()) {
					mob.setMaxHealth((int) locDiff(ent.getLocation(), getSpawn()));
				} else {
					mob.setMaxHealth(getConfig().getInt("Mobs." + event.getEntityType().getName().toUpperCase() + ".Health"));
				}
				mob.setHealth(mob.getMaxHealth());
			} else if (event.getEntityType() == EntityType.SKELETON) {
				Skeleton mob = (Skeleton) ent;
				if (getConfig().getBoolean("Mobs.ChangeHealthByRange") && ent.getWorld() == getSpawn().getWorld()) {
					mob.setMaxHealth((int) locDiff(ent.getLocation(), getSpawn()));
				} else {
					mob.setMaxHealth(getConfig().getInt("Mobs." + event.getEntityType().getName().toUpperCase() + ".Health"));
				}
				mob.setHealth(mob.getMaxHealth());
			} else if (event.getEntityType() == EntityType.SPIDER) {
				Spider mob = (Spider) ent;
				if (getConfig().getBoolean("Mobs.ChangeHealthByRange") && ent.getWorld() == getSpawn().getWorld()) {
					mob.setMaxHealth((int) locDiff(ent.getLocation(), getSpawn()));
				} else {
					mob.setMaxHealth(getConfig().getInt("Mobs." + event.getEntityType().getName().toUpperCase() + ".Health"));
				}
				mob.setHealth(mob.getMaxHealth());
			} else if (event.getEntityType() == EntityType.CREEPER) {
				Creeper mob = (Creeper) ent;
				if (getConfig().getBoolean("Mobs.ChangeHealthByRange") && ent.getWorld() == getSpawn().getWorld()) {
					mob.setMaxHealth((int) locDiff(ent.getLocation(), getSpawn()));
				} else {
					mob.setMaxHealth(getConfig().getInt("Mobs." + event.getEntityType().getName().toUpperCase() + ".Health"));
				}
				mob.setHealth(mob.getMaxHealth());
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onTargetChange(EntityTargetEvent event) {
		if (getConfig().isSet("Mobs." + event.getEntity().getEntityId())) {
			event.setTarget(Bukkit.getPlayer(getConfig().getString("Mobs." + event.getEntity().getEntityId())));
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onSignChange(SignChangeEvent event) {
		if (event.getLine(0).equalsIgnoreCase("[buy]")) {
			if (event.getPlayer().isOp()) {
				event.setLine(0, "[Buy]");
			} else {
				event.setCancelled(true);
				event.getPlayer().sendMessage("You cannot create shops.");
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (getConfig().getBoolean("Options.building")) {
			if (event.getPlayer().isOp() == false) {
				event.setCancelled(true);
			} else {
				if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onBlockBreak(BlockBreakEvent event) {
		if (getConfig().getBoolean("Options.building")) {
			if (event.getPlayer().isOp() == false) {
				event.setCancelled(true);
			} else {
				if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
					event.setCancelled(true);
				}
			}
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = (Player)sender;
		ItemStack is;
		ItemMeta im;
		ArrayList<String> lore;
		if (commandLabel.equalsIgnoreCase("it") && player.isOp()) {
			if (args.length == 0) {
				player.sendMessage("No item specified.");
				return true;
			} else {
				String name = args[0].replaceAll("_", " ");
				player.getInventory().addItem(returnItem(name, 1, Short.parseShort("0")));
				player.sendMessage("Added.");
				return true;
			}
		}

		if (commandLabel.equalsIgnoreCase("pet")) {
			if (args.length == 0) {
				player.sendMessage("No pet specified.");
				return true;
			} else {
				spawnPet(player, args[0]);
				player.sendMessage("Spawned.");
				return true;
			}
		}

		/*if (commandLabel.equalsIgnoreCase("trader") && player.isOp()) {
			if (args.length < 2) {
				player.sendMessage("No item specified.");
				return true;
			} else {
				Villager v = (Villager) Bukkit.getWorld(player.getWorld().getName()).spawnEntity(player.getLocation(), EntityType.VILLAGER);
				upd("Merchant.i" + v.getEntityId() + "i.info", args[0] + "-" + args[1]);
				player.sendMessage("Spawned.");
				return true;
			}
		}*/

		if (commandLabel.equalsIgnoreCase("loot") && player.isOp()) {
			if (args.length == 0) {
				player.sendMessage("No loot specified.");
				return true;
			} else {
				Block target = player.getTargetBlock(null, 200);
				Location loc = target.getLocation();
				String blockloc = loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "," + loc.getWorld().getName();
				upd("Loot." + blockloc, args[0].replaceAll("_", " "));
				player.sendMessage("Chest defined.");
				return true;
			}
		}

		if (commandLabel.equalsIgnoreCase("boss") && player.isOp()) {
			if (args.length == 0) {
				player.sendMessage("No boss specified.");
				return true;
			} else {
				spawnBoss(player.getLocation(), args[0]);
				player.sendMessage("Boss spawned.");
				return true;
			}
		}
		
		if (commandLabel.equalsIgnoreCase("cheque") && player.isOp()) {
			if (args.length == 0) {
				player.sendMessage("No amount specified.");
				return true;
			} else {
				is = new ItemStack(Material.PAPER, 1);
				im = is.getItemMeta();
				im.setDisplayName("Bank Cheque of " + args[0] + " Gold");
				lore = new ArrayList<String>();
				lore.add(ChatColor.GOLD + "This can be cashed in for Gold");
				lore.add(ChatColor.GOLD + "by left clicking on a bank sign.");
				im.setLore(lore);
				is.setItemMeta(im);
				player.getInventory().addItem(is);
				player.sendMessage("Cheque added to your inventory.");
				return true;
			}
		}

		if (commandLabel.equalsIgnoreCase("hs")) {
			player.sendMessage("Here, have a Hearthstone.");
			is = new ItemStack(Material.getMaterial(getConfig().getInt("Options.Tools.HearthstoneID")), 1);
			im = is.getItemMeta();
			im.setDisplayName("Hearthstone");
			lore = new ArrayList<String>();
			lore.add(ChatColor.GOLD + "Use this item to return to the Hearthstone home.");
			im.setLore(lore);
			is.setItemMeta(im);
			player.getInventory().addItem(is);
			return true;
		}
		

		if (commandLabel.equalsIgnoreCase("sb")) {
			player.sendMessage("Here, have a stats book.");
			ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
			BookMeta meta = (BookMeta) book.getItemMeta();
			meta.setTitle("Stats");
			meta.setAuthor("Server");
			meta.addPage("HELLO!");
			book.setItemMeta(meta);
			player.getInventory().addItem(book);
			return true;
		}

		if (commandLabel.equalsIgnoreCase("vis") && player.isOp()) {
			if (args.length == 0) {
				player.sendMessage("No argument specified.");
				return true;
			} else {
				if (args[0].equalsIgnoreCase("hide")) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						if (p.isOp() == false) {
							p.hidePlayer(player);
						}
					}
					player.sendMessage("You are now hidden to non-OPs.");
					return true;
				} else if (args[0].equalsIgnoreCase("show")) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						p.showPlayer(player);
					}
					player.sendMessage("You are now visable.");
					return true;
				} else {
					player.sendMessage("Wrong argument specified.");
					return true;
				}
			}
		}

		return false;
	}
}
