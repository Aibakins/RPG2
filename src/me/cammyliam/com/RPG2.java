//EDIT TEST
package me.cammyliam.com;

import java.io.File;
import java.util.ArrayList;
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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
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

	/*public float returnRage(Player player) {
		return player.getExp();
	}
	public void addRage(Player player, float x) {
		player.setTotalExperience(100);
		if (player.getExp() > x) {
			player.setExp(returnRage(player) + x);
		}
	}
	public void remRage(Player player, float x) {
		player.setTotalExperience(100);
		if (player.getExp() > x) {
			player.setExp(returnRage(player) - x);
		}
	}*/

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

	public int money(Player player) {
		int c = 0;
		for (ItemStack i : player.getInventory().getContents()) {
			if (i != null) {
				if (i.getType() == Material.GOLD_INGOT) {
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
				if (i.getType() == Material.GOLD_INGOT) {
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
		player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, xam));
	}

	public ItemStack returnItem(String name) {
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
			if (getConfig().getInt("Items." + name + ".Dura") != 0) {
				is.setDurability((short) (getConfig().getInt("Items." + name + ".Dura")));
			}
			if (d != "") {
				lore.add(d);
			}
			im.setLore(lore);
			is.setItemMeta(im);
		} else {
			try {
				is = new ItemStack(Material.getMaterial(name.toUpperCase()), 1);
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
		float s = 1;
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
			player.sendMessage(ChatColor.GOLD + "Health changed from " + ChatColor.RED + player.getHealth() + "/" + c + ChatColor.GOLD + " to " + ChatColor.RED + player.getHealth() + "/" + h + ChatColor.GOLD + ".");
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
		if (player.getFoodLevel()+(0.2 * s) > 20) {
			player.setFoodLevel(20);
		} else {
			player.setFoodLevel((int) (player.getFoodLevel()+(0.2 * s)));
		}
		if (d > 148) {
			d = 149;
		}
		upd("Players." + player.getName() + ".Dodge", d);
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
			this.upd("Options.Anvil.Price", 13);
			this.upd("Options.Anvil.DuraToAdd", 60);
			this.upd("Options.Tools.SafeDropToolID", 352);
			this.upd("Options.Tools.HearthstoneID", 399);
		}
		if (!getConfig().isSet("Loot")) {
			this.upd("Loot.defaultloot", "GOLD_INGOT,");
		}
		if (!getConfig().isSet("Drops")) {
			this.upd("Drops.ZOMBIE", "Regener,GOLD_INGOT,GOLD_INGOT"); //Example showing that you can use custom items if they're defined.
			this.upd("Drops.CREEPER", "GOLD_INGOT,GOLD_INGOT");
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
	}

	/*public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
		Entity en = e.getRightClicked();
		if (en instanceof Villager) {
			if (getConfig().isSet("Merchant.i" + en.getEntityId() + "i")) {
				String[] i = getConfig().getString("Merchant.i" + en.getEntityId() + "i.info").split("-");
				Villager v = (Villager) en;
				v.
			}
		}
	}*/

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		if (!getConfig().isSet("Players." + player.getName())) {
			player.teleport(new Location(Bukkit.getWorld("world"), -140, 4, -23));
			upd("Players." + player.getName() + ".dropto", "null");
			upd("Players." + player.getName() + ".Bank", getConfig().getInt("Options.default-bankslots"));
			upd("Players." + player.getName() + ".hearthstone", "null");
			ItemStack is;
			ItemMeta im;
			ArrayList<String> lore;
			is = new ItemStack(Material.getMaterial(getConfig().getInt("Options.Tools.SafeDropToolID")), 1);
			im = is.getItemMeta();
			im.setDisplayName("Safe-Drop Tool");
			lore = new ArrayList<String>();
			lore.add(ChatColor.GOLD + "Use this item to safe-drop. (Soulbound)");
			im.setLore(lore);
			is.setItemMeta(im);
			player.getInventory().addItem(is);
			is = new ItemStack(Material.getMaterial(getConfig().getInt("Options.Tools.HearthstoneID")), 1);
			im = is.getItemMeta();
			im.setDisplayName("Hearthstone");
			lore = new ArrayList<String>();
			lore.add(ChatColor.GOLD + "Use this item to return to the Hearthstone home. (Soulbound)");
			im.setLore(lore);
			is.setItemMeta(im);
			player.getInventory().addItem(is);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		event.setRespawnLocation(getRespawn());
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (event.getAction() == Action.RIGHT_CLICK_AIR) {
			if (player.getItemInHand().getItemMeta().hasDisplayName()) {
				if (player.getItemInHand().getItemMeta().getDisplayName().equals("Safe-Drop Tool")) {
					upd("Players." + player.getName() + ".dropto", "null");
					player.sendMessage("Drops set to: free-for-all");
				}
				else if (player.getItemInHand().getItemMeta().getDisplayName().equals("Hearthstone")) {
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
				event.getPlayer().getEnderChest().remove(Material.SIGN_POST);
				for (ItemStack i : event.getPlayer().getEnderChest().getContents()) {
					if (s >= getConfig().getInt("Players." + event.getPlayer().getName() + ".Bank")) {
						if (i != null && i.getType() != Material.SIGN_POST) {
							Bukkit.getWorld(event.getPlayer().getWorld().getName()).dropItem(event.getPlayer().getLocation(), i);
						}
						event.getPlayer().getEnderChest().setItem(s, new ItemStack(Material.SIGN_POST));
					}
					s++;
				}
				event.getPlayer().getInventory().remove(Material.SIGN_POST);
			}
			if (block.getType() == Material.CHEST) {
				if (!getConfig().isSet("Players." + player.getName() + ".Looted")) {
					upd("Players." + player.getName() + ".Looted", "0,0,0,world-");
				}
				Chest chest = (Chest) block.getState();
				Inventory inv = chest.getInventory();
				Location loc = block.getLocation();
				String blockloc = loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "," + loc.getWorld().getName();
				if (!getConfig().getString("Players." + player.getName() + ".Looted").contains(blockloc + "_")) {
					if (getConfig().isSet("Loot." + blockloc)) {
						for (String item : getConfig().getString("Loot." + blockloc).split(",")) {
							try {
								inv.addItem(returnItem(item));
							} catch(Exception e) {}
						}
					} else {
						for (String item : getConfig().getString("Loot.defaultloot").split(",")) {
							try {
								inv.addItem(returnItem(item));
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
							player.getInventory().addItem(returnItem(sign.getLine(1)));
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
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onInventoryClose(InventoryCloseEvent event) {
		int s = 0;
		for (ItemStack i : event.getPlayer().getEnderChest().getContents()) {
			if (s >= getConfig().getInt("Players." + event.getPlayer().getName() + ".Bank")) {
				if (i != null && i.getType() != Material.SIGN_POST) {
					Bukkit.getWorld(event.getPlayer().getWorld().getName()).dropItem(event.getPlayer().getLocation(), i);
				}
				event.getPlayer().getEnderChest().setItem(s, new ItemStack(Material.SIGN_POST));
			}
			s++;
		}
		event.getPlayer().getInventory().remove(Material.SIGN_POST);
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
				} else if (given.getItemInHand().getType() == Material.getMaterial(getConfig().getInt("Options.Tools.SafeDropToolID"))) {
					if (given.getItemInHand().getItemMeta().hasDisplayName()) {
						if (given.getItemInHand().getItemMeta().getDisplayName() == "Safe-Drop Tool") {
							if (event.getEntity() instanceof Player) {
								event.setCancelled(true);
								Damage = 0;
								Player taken = (Player) event.getEntity();
								given.sendMessage("Drops set to: " + taken.getName() + " only");
								upd("Players." + given.getName() + ".dropto", taken.getName());
							}
						}
					}
				}
			} catch (Exception e) {}
			if (getConfig().isSet("Mobs." + event.getEntity().getEntityId())) {
				event.setCancelled(true);
				event.setDamage(0);
			}
			if (Damage > 0) {
				/*addRage(given, 2);
				if (returnRage(given) > 96) {
					Damage = Damage * 2;
					given.sendMessage(ChatColor.DARK_RED + "-- Times two damage! --");
					remRage(given, 40);
				}*/
				given.sendMessage(ChatColor.MAGIC + "-- " + ChatColor.RED + "Damage given: " + Damage + ChatColor.WHITE + " " + ChatColor.MAGIC + "--");
			}
			event.setDamage(Damage);
		}
		if (event.getEntity() instanceof Player) {
			if (getConfig().isSet("Mobs." + event.getDamager().getEntityId())) {
				event.setCancelled(true);
				event.setDamage(0);
			}
			Player taken = (Player) event.getEntity();
			taken.setLevel(taken.getHealth());
			if (rand.nextInt(150 - returnCFGDodge(taken)) == 1) {
				event.setDamage(0);
			}
			if (event.getDamage() > 0) {
				taken.sendMessage(ChatColor.MAGIC + "-- " + ChatColor.RED + "Damage recieved: " + event.getDamage() + ChatColor.WHITE + " " + ChatColor.MAGIC + "--");
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onEntityDeath(EntityDeathEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			if (getConfig().isSet("Drops." + event.getEntityType().getName().toUpperCase())) {
				String[] d = getConfig().getString("Drops." + event.getEntityType().getName().toUpperCase()).split(",");
				try {
					event.getDrops().clear();
					event.getDrops().add(returnItem(d[rand.nextInt(d.length)]));
				} catch (Exception e) {} 
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
	public void onItemDrop(PlayerDropItemEvent event) {
		Player dropper = event.getPlayer();

		if (!(getConfig().getString("Players." + dropper.getName() + ".dropto") == "null")) {
			Player dropto = Bukkit.getPlayer(getConfig().getString("Players." + dropper.getName() + ".dropto"));
			dropto.getInventory().addItem(event.getItemDrop().getItemStack());
			event.getItemDrop().remove();
		} 
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getPlayer().isOp() == false) {
			event.setCancelled(true);
		} else {
			if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getPlayer().isOp() == false) {
			event.setCancelled(true);
		} else {
			if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
				event.setCancelled(true);
			}
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = (Player)sender;
		if (commandLabel.equalsIgnoreCase("it") && player.isOp()) {
			if (args.length == 0) {
				player.sendMessage("No item specified.");
				return true;
			} else {
				String name = args[0].replaceAll("_", " ");
				player.getInventory().addItem(returnItem(name));
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

		/*if (commandLabel.equalsIgnoreCase("test") && player.isOp()) {
			if (args.length == 0) {
				if (money(player) > 10) {
					rmoney(player, 10);
					player.sendMessage("10 gold removed.");
				} else {
					player.sendMessage("Not enough gold.");
				}
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

		return false;
	}
}
