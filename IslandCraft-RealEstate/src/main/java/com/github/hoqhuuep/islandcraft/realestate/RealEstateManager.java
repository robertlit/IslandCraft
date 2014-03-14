package com.github.hoqhuuep.islandcraft.realestate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.github.hoqhuuep.islandcraft.core.Message;

public class RealEstateManager {
	private final RealEstateDatabase database;
	private final RealEstateConfig config;
	private final Map<String, IslandDeed> lastIsland;
	private final Map<String, Geometry> geometryMap;
	private final Set<SerializableLocation> loadedIslands;

	public RealEstateManager(final RealEstateDatabase database, final RealEstateConfig config) {
		this.database = database;
		this.config = config;
		lastIsland = new HashMap<String, IslandDeed>();
		geometryMap = new HashMap<String, Geometry>();
		loadedIslands = new HashSet<SerializableLocation>();
	}

	/**
	 * To be called when a chunk is loaded. Creates WorldGuard regions if they
	 * do not exist.
	 * 
	 * @param x
	 * @param z
	 */
	public void onLoad(final Location location, final long worldSeed) {
		final World world = location.getWorld();
		if (world == null) {
			// Not ready
			return;
		}
		final Geometry geometry = getGeometry(world.getName());
		if (geometry == null) {
			// Not an IslandCraft world
			return;
		}
		for (final SerializableLocation island : geometry.getOuterIslands(location)) {
			if (loadedIslands.contains(island)) {
				// Only load once, until server is rebooted
				continue;
			}
			IslandDeed deed = database.loadIsland(island);
			if (deed == null) {
				deed = new IslandDeed();
				deed.setId(new SerializableLocation(island.getWorld(), island.getX(), island.getY(), island.getZ()));
				deed.setInnerRegion(geometry.getInnerRegion(island));
				deed.setOuterRegion(geometry.getOuterRegion(island));
				deed.setOwner(null);
				deed.setTax(-1);
				if (geometry.isSpawn(island)) {
					deed.setStatus(IslandStatus.RESERVED);
					deed.setTitle(Message.ISLAND_TITLE_SPAWN.format());
				} else if (geometry.isResource(island, worldSeed)) {
					deed.setStatus(IslandStatus.RESOURCE);
					deed.setTitle(null);
				} else {
					deed.setStatus(IslandStatus.NEW);
					deed.setTitle(null);
				}
				database.saveIsland(deed);
			}
			loadedIslands.add(island);
			Bukkit.getPluginManager().callEvent(new IslandEvent(deed));
		}
	}

	/**
	 * To be called when a player tries to abandon the island at their current
	 * location.
	 * 
	 * @param player
	 */
	public final void onAbandon(final Player player) {
		final Geometry geometry = getGeometry(player.getWorld().getName());
		if (geometry == null) {
			Message.ISLAND_ABANDON_WORLD_ERROR.send(player);
			return;
		}
		final Location location = player.getLocation();
		final SerializableLocation island = geometry.getInnerIsland(location);
		if (geometry.isOcean(island)) {
			Message.ISLAND_ABANDON_OCEAN_ERROR.send(player);
			return;
		}
		final IslandDeed deed = database.loadIsland(island);
		if (deed.getStatus() != IslandStatus.PRIVATE || !StringUtils.equals(deed.getOwner(), player.getName())) {
			Message.ISLAND_ABANDON_OWNER_ERROR.send(player);
			return;
		}

		// Success
		deed.setStatus(IslandStatus.ABANDONED);
		deed.setTax(-1);
		database.saveIsland(deed);
		Message.ISLAND_ABANDON.send(player);
		onMove(player, player.getLocation());
		Bukkit.getPluginManager().callEvent(new IslandEvent(deed));
	}

	/**
	 * To be called when a player tries to examine the island at their current
	 * location.
	 * 
	 * @param player
	 */
	public final void onExamine(final Player player) {
		final Geometry geometry = getGeometry(player.getWorld().getName());
		if (geometry == null) {
			Message.ISLAND_EXAMINE_WORLD_ERROR.send(player);
			return;
		}
		final Location location = player.getLocation();
		final SerializableLocation island = geometry.getInnerIsland(location);
		if (geometry.isOcean(island)) {
			Message.ISLAND_EXAMINE_OCEAN_ERROR.send(player);
			return;
		}
		final IslandDeed deed = database.loadIsland(island);
		final IslandStatus status = deed.getStatus();
		final String title = deed.getTitleWithDefault();
		final String owner = deed.getOwner();
		final int tax = deed.getTax();
		if (status == IslandStatus.RESOURCE) {
			Message.ISLAND_EXAMINE_RESOURCE.send(player, title, status);
		} else if (status == IslandStatus.RESERVED) {
			Message.ISLAND_EXAMINE_RESERVED.send(player, title, status);
		} else if (status == IslandStatus.NEW) {
			Message.ISLAND_EXAMINE_NEW.send(player, title, status);
		} else if (status == IslandStatus.ABANDONED) {
			Message.ISLAND_EXAMINE_ABANDONED.send(player, title, status, owner);
		} else if (status == IslandStatus.REPOSSESSED) {
			Message.ISLAND_EXAMINE_REPOSSESSED.send(player, title, status, owner);
		} else if (status == IslandStatus.PRIVATE) {
			Message.ISLAND_EXAMINE_PRIVATE.send(player, title, status, owner, tax);
		}
	}

	/**
	 * To be called when a player tries to purchase the island at their current
	 * location.
	 * 
	 * @param player
	 */
	public final void onPurchase(final Player player) {
		final Geometry geometry = getGeometry(player.getWorld().getName());
		if (geometry == null) {
			Message.ISLAND_PURCHASE_WORLD_ERROR.send(player);
			return;
		}
		final Location location = player.getLocation();
		final SerializableLocation island = geometry.getInnerIsland(location);
		if (geometry.isOcean(island)) {
			Message.ISLAND_PURCHASE_OCEAN_ERROR.send(player);
			return;
		}

		final IslandDeed deed = database.loadIsland(island);
		final IslandStatus status = deed.getStatus();
		final String name = player.getName();

		if (IslandStatus.RESERVED == status) {
			Message.ISLAND_PURCHASE_RESERVED_ERROR.send(player);
			return;
		}
		if (IslandStatus.RESOURCE == status) {
			Message.ISLAND_PURCHASE_RESOURCE_ERROR.send(player);
			return;
		}
		if (IslandStatus.PRIVATE == status) {
			final String owner = deed.getOwner();
			if (StringUtils.equals(owner, name)) {
				Message.ISLAND_PURCHASE_SELF_ERROR.send(player);
			} else {
				Message.ISLAND_PURCHASE_OTHER_ERROR.send(player);
			}
			return;
		}
		// if config.MAX_ISLANDS_PER_PLAYER is -1 then infinite
		if (config.MAX_ISLANDS_PER_PLAYER > 0 && islandCount(name) >= config.MAX_ISLANDS_PER_PLAYER) {
			Message.ISLAND_PURCHASE_MAX_ERROR.send(player);
			return;
		}

		final int cost = calculatePurchaseCost(name);

		if (!takeItems(player, config.PURCHASE_COST_ITEM, cost)) {
			// Insufficient funds
			Message.ISLAND_PURCHASE_FUNDS_ERROR.send(player, cost, config.PURCHASE_COST_ITEM);
			return;
		}

		// Success
		deed.setStatus(IslandStatus.PRIVATE);
		deed.setOwner(name);
		deed.setTitle(null);
		deed.setTax(config.TAX_DAYS_INITIAL);
		database.saveIsland(deed);
		Message.ISLAND_PURCHASE.send(player);
		onMove(player, player.getLocation());
		Bukkit.getPluginManager().callEvent(new IslandEvent(deed));
	}

	public void onTax(final Player player) {
		final Geometry geometry = getGeometry(player.getWorld().getName());
		if (geometry == null) {
			Message.ISLAND_TAX_WORLD_ERROR.send(player);
			return;
		}
		final Location location = player.getLocation();
		final SerializableLocation island = geometry.getInnerIsland(location);
		if (geometry.isOcean(island)) {
			Message.ISLAND_TAX_OCEAN_ERROR.send(player);
			return;
		}
		final String name = player.getName();
		final IslandDeed deed = database.loadIsland(island);
		if (deed.getStatus() != IslandStatus.PRIVATE || !deed.getOwner().equals(name)) {
			Message.ISLAND_TAX_OWNER_ERROR.send(player);
			return;
		}

		final int newTax = deed.getTax() + config.TAX_DAYS_INCREASE;
		if (newTax > config.TAX_DAYS_MAX) {
			Message.ISLAND_TAX_MAX_ERROR.send(player);
			return;
		}

		final int cost = calculateTaxCost(name);

		if (!takeItems(player, config.TAX_COST_ITEM, cost)) {
			// Insufficient funds
			Message.ISLAND_TAX_FUNDS_ERROR.send(player, cost, config.TAX_COST_ITEM);
			return;
		}

		// Success
		deed.setTax(newTax);
		database.saveIsland(deed);
		Message.ISLAND_TAX.send(player);
		Bukkit.getPluginManager().callEvent(new IslandEvent(deed));
	}

	public void onDawn(final String world) {
		final Geometry geometry = getGeometry(world);
		if (geometry == null) {
			// Not an IslandCraft world
			return;
		}
		final List<IslandDeed> deeds = database.loadIslandsByWorld(world);
		for (final IslandDeed deed : deeds) {
			final int tax = deed.getTax();
			if (tax > 0) {
				// Decrement tax
				deed.setTax(tax - 1);
				database.saveIsland(deed);
				Bukkit.getPluginManager().callEvent(new IslandEvent(deed));
			} else if (tax == 0) {
				final IslandStatus status = deed.getStatus();
				if (status == IslandStatus.PRIVATE) {
					// Repossess island
					deed.setStatus(IslandStatus.REPOSSESSED);
					deed.setTax(-1);
					database.saveIsland(deed);
					Bukkit.getPluginManager().callEvent(new IslandEvent(deed));
				} else {
					// TODO regenerate island
					if (status == IslandStatus.REPOSSESSED || status == IslandStatus.ABANDONED) {
						deed.setStatus(IslandStatus.NEW);
						deed.setOwner(null);
						deed.setTitle(null);
						deed.setTax(-1);
						database.saveIsland(deed);
						Bukkit.getPluginManager().callEvent(new IslandEvent(deed));
					}
				}
			}
			// tax < 0 => infinite
		}
	}

	/**
	 * To be called when the player tries to rename the island at their current
	 * location.
	 * 
	 * @param player
	 * @param title
	 */
	public final void onRename(final Player player, final String title) {
		final Geometry geometry = getGeometry(player.getWorld().getName());
		if (geometry == null) {
			Message.ISLAND_RENAME_WORLD_ERROR.send(player);
			return;
		}
		final Location location = player.getLocation();
		final SerializableLocation island = geometry.getInnerIsland(location);
		if (geometry.isOcean(island)) {
			Message.ISLAND_RENAME_OCEAN_ERROR.send(player);
			return;
		}
		final IslandDeed deed = database.loadIsland(island);
		if (deed.getStatus() != IslandStatus.PRIVATE || !StringUtils.equals(deed.getOwner(), player.getName())) {
			Message.ISLAND_RENAME_OWNER_ERROR.send(player);
			return;
		}

		// Success
		deed.setTitle(title);
		database.saveIsland(deed);
		Message.ISLAND_RENAME.send(player);
		onMove(player, player.getLocation());
		Bukkit.getPluginManager().callEvent(new IslandEvent(deed));
	}

	// public void onWarp(final Player player) {
	// final List<IslandInfo> islands = database.loadIslands();
	// Collections.shuffle(islands);
	// for (final IslandInfo island : islands) {
	// final IslandStatus type = island.getStatus();
	// if (type == IslandStatus.NEW || type == IslandStatus.ABANDONED || type ==
	// IslandStatus.REPOSSESSED) {
	// final Location islandLocation = island.getLocation();
	// player.teleport(islandLocation);
	// player.sendMessage(config.M_ISLAND_WARP);
	// return;
	// }
	// }
	// player.sendMessage(config.M_ISLAND_WARP_ERROR);
	// }

	private int calculatePurchaseCost(final String player) {
		return config.PURCHASE_COST_AMOUNT + islandCount(player) * config.PURCHASE_COST_INCREASE;
	}

	private int calculateTaxCost(final String player) {
		return config.TAX_COST_AMOUNT + (islandCount(player) - 1) * config.TAX_COST_INCREASE;
	}

	private int islandCount(final String player) {
		final List<IslandDeed> deeds = database.loadIslandsByOwner(player);
		int count = 0;
		for (final IslandDeed deed : deeds) {
			if (deed.getStatus() == IslandStatus.PRIVATE) {
				++count;
			}
		}
		return count;
	}

	private static final Integer FIRST = new Integer(0);

	private boolean takeItems(final Player player, final Material item, final int amount) {
		final PlayerInventory inventory = player.getInventory();
		if (!inventory.containsAtLeast(new ItemStack(item), amount)) {
			// Not enough
			return false;
		}
		final Map<Integer, ItemStack> result = inventory.removeItem(new ItemStack(item, amount));
		if (!result.isEmpty()) {
			// Something went wrong, refund
			final int missing = result.get(FIRST).getAmount();
			inventory.addItem(new ItemStack(item, amount - missing));
			return false;
		}
		// Success
		return true;
	}

	public void onMove(final Player player, final Location to) {
		final String name = player.getName();
		if (to == null) {
			lastIsland.remove(name);
			return;
		}
		final Geometry geometry = getGeometry(to.getWorld().getName());
		final IslandDeed toIsland;
		if (geometry != null) {
			final SerializableLocation toIslandLocation = geometry.getInnerIsland(to);
			if (toIslandLocation != null) {
				toIsland = database.loadIsland(toIslandLocation);
			} else {
				toIsland = null;
			}
		} else {
			toIsland = null;
		}
		final IslandDeed fromIsland = lastIsland.get(name);
		if (fromIsland != null) {
			if (toIsland == null || !equals(toIsland.getTitleWithDefault(), fromIsland.getTitleWithDefault()) || !equals(toIsland.getOwner(), fromIsland.getOwner())) {
				leaveIsland(player, fromIsland);
			}
		}
		if (toIsland != null) {
			if (fromIsland == null || !equals(toIsland.getTitleWithDefault(), fromIsland.getTitleWithDefault()) || !equals(toIsland.getOwner(), fromIsland.getOwner())) {
				enterIsland(player, toIsland);
			}
		}
		lastIsland.put(name, toIsland);
	}

	private void enterIsland(final Player player, final IslandDeed deed) {
		final IslandStatus status = deed.getStatus();
		String title = deed.getTitleWithDefault();
		final String owner = deed.getOwner();
		if (status == IslandStatus.RESOURCE) {
			Message.ISLAND_ENTER_RESOURCE.send(player, title);
		} else if (status == IslandStatus.RESERVED) {
			Message.ISLAND_ENTER_RESERVED.send(player, title);
		} else if (status == IslandStatus.NEW) {
			Message.ISLAND_ENTER_NEW.send(player, title);
		} else if (status == IslandStatus.ABANDONED) {
			Message.ISLAND_ENTER_ABANDONED.send(player, title, owner);
		} else if (status == IslandStatus.REPOSSESSED) {
			Message.ISLAND_ENTER_REPOSSESSED.send(player, title, owner);
		} else if (status == IslandStatus.PRIVATE) {
			Message.ISLAND_ENTER_PRIVATE.send(player, title, owner);
		}
	}

	private void leaveIsland(final Player player, final IslandDeed deed) {
		final IslandStatus status = deed.getStatus();
		String title = deed.getTitleWithDefault();
		final String owner = deed.getOwner();
		if (status == IslandStatus.RESOURCE) {
			Message.ISLAND_LEAVE_RESOURCE.send(player, title);
		} else if (status == IslandStatus.RESERVED) {
			Message.ISLAND_LEAVE_RESERVED.send(player, title);
		} else if (status == IslandStatus.NEW) {
			Message.ISLAND_LEAVE_NEW.send(player, title);
		} else if (status == IslandStatus.ABANDONED) {
			Message.ISLAND_LEAVE_ABANDONED.send(player, title, owner);
		} else if (status == IslandStatus.REPOSSESSED) {
			Message.ISLAND_LEAVE_REPOSSESSED.send(player, title, owner);
		} else if (status == IslandStatus.PRIVATE) {
			Message.ISLAND_LEAVE_PRIVATE.send(player, title, owner);
		}
	}

	private boolean equals(final Object a, final Object b) {
		return (a == null && b == null) || (a != null && b != null && a.equals(b));
	}

	public void addGeometry(final String world, final Geometry geometry) {
		geometryMap.put(world, geometry);
	}

	public Geometry getGeometry(final String world) {
		return geometryMap.get(world);
	}

	public void setTax(final Player player, final int tax) {
		final Geometry geometry = getGeometry(player.getWorld().getName());
		if (geometry == null) {
			Message.ISLAND_UPDATE_WORLD_ERROR.send(player);
			return;
		}
		final Location location = player.getLocation();
		final SerializableLocation island = geometry.getInnerIsland(location);
		if (geometry.isOcean(island)) {
			Message.ISLAND_UPDATE_OCEAN_ERROR.send(player);
			return;
		}
		final IslandDeed deed = database.loadIsland(island);

		// Success
		deed.setTax(tax);
		database.saveIsland(deed);
		Message.ISLAND_UPDATE.send(player);
		onMove(player, player.getLocation());
		Bukkit.getPluginManager().callEvent(new IslandEvent(deed));
	}

	public void setTitle(final Player player, final String title) {
		final Geometry geometry = getGeometry(player.getWorld().getName());
		if (geometry == null) {
			Message.ISLAND_UPDATE_WORLD_ERROR.send(player);
			return;
		}
		final Location location = player.getLocation();
		final SerializableLocation island = geometry.getInnerIsland(location);
		if (geometry.isOcean(island)) {
			Message.ISLAND_UPDATE_OCEAN_ERROR.send(player);
			return;
		}
		final IslandDeed deed = database.loadIsland(island);

		// Success
		deed.setTitle(title);
		database.saveIsland(deed);
		Message.ISLAND_UPDATE.send(player);
		onMove(player, player.getLocation());
		Bukkit.getPluginManager().callEvent(new IslandEvent(deed));
	}

	public void setOwner(final Player player, final String owner) {
		final Geometry geometry = getGeometry(player.getWorld().getName());
		if (geometry == null) {
			Message.ISLAND_UPDATE_WORLD_ERROR.send(player);
			return;
		}
		final Location location = player.getLocation();
		final SerializableLocation island = geometry.getInnerIsland(location);
		if (geometry.isOcean(island)) {
			Message.ISLAND_UPDATE_OCEAN_ERROR.send(player);
			return;
		}
		final IslandDeed deed = database.loadIsland(island);

		// Success
		deed.setOwner(owner);
		database.saveIsland(deed);
		Message.ISLAND_UPDATE.send(player);
		onMove(player, player.getLocation());
		Bukkit.getPluginManager().callEvent(new IslandEvent(deed));
	}

	public void setStatus(final Player player, final IslandStatus status) {
		final Geometry geometry = getGeometry(player.getWorld().getName());
		if (geometry == null) {
			Message.ISLAND_UPDATE_WORLD_ERROR.send(player);
			return;
		}
		final Location location = player.getLocation();
		final SerializableLocation island = geometry.getInnerIsland(location);
		if (geometry.isOcean(island)) {
			Message.ISLAND_UPDATE_OCEAN_ERROR.send(player);
			return;
		}
		final IslandDeed deed = database.loadIsland(island);

		// Success
		deed.setStatus(status);
		database.saveIsland(deed);
		Message.ISLAND_UPDATE.send(player);
		onMove(player, player.getLocation());
		Bukkit.getPluginManager().callEvent(new IslandEvent(deed));
	}

	// private void regenerateRegion(final IslandDeed island, final Geometry
	// geometry) {
	// final Long oldSeed = null; // database.loadSeed(island.getId());
	// final SerializableRegion region = island.getInnerRegion();
	// final int minX = region.getMinX() >> 4;
	// final int minZ = region.getMinZ() >> 4;
	// final int maxX = region.getMaxX() >> 4;
	// final int maxZ = region.getMaxZ() >> 4;
	// if (null != oldSeed) {
	// database.saveSeed(island.getId(), new Long(new
	// Random(oldSeed.longValue()).nextLong()));
	// final World w2 = Bukkit.getWorld(region.getWorld());
	// for (int x = minX; x < maxX; ++x) {
	// for (int z = minZ; z < maxZ; ++z) {
	// w2.unloadChunk(x, z);
	// }
	// }
	// for (int x = minX; x < maxX; ++x) {
	// for (int z = minZ; z < maxZ; ++z) {
	// w2.regenerateChunk(x, z);
	// }
	// }
	// }
	// }
}
