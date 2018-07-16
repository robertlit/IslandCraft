package com.github.hoqhuuep.islandcraft.nms.v1_13_R1;

import java.util.*;

import net.minecraft.server.v1_13_R1.*;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_13_R1.block.CraftBlock;

import com.github.hoqhuuep.islandcraft.api.ICBiome;
import com.github.hoqhuuep.islandcraft.nms.BiomeGenerator;

public class CustomWorldChunkManager extends WorldChunkManager {
    private static final Map<ICBiome, BiomeBase> biomeMap = new EnumMap<>(ICBiome.class);

    static {
        biomeMap.put(ICBiome.BEACH, CraftBlock.biomeToBiomeBase(Biome.BEACH));
        biomeMap.put(ICBiome.BIRCH_FOREST, CraftBlock.biomeToBiomeBase(Biome.BIRCH_FOREST));
        biomeMap.put(ICBiome.BIRCH_FOREST_HILLS, CraftBlock.biomeToBiomeBase(Biome.BIRCH_FOREST_HILLS));
        biomeMap.put(ICBiome.BIRCH_FOREST_HILLS_M, CraftBlock.biomeToBiomeBase(Biome.MUTATED_BIRCH_FOREST_HILLS));
        biomeMap.put(ICBiome.BIRCH_FOREST_M, CraftBlock.biomeToBiomeBase(Biome.MUTATED_BIRCH_FOREST));
        biomeMap.put(ICBiome.COLD_BEACH, CraftBlock.biomeToBiomeBase(Biome.COLD_BEACH));
        biomeMap.put(ICBiome.COLD_TAIGA, CraftBlock.biomeToBiomeBase(Biome.SNOWY_TAIGA));
        biomeMap.put(ICBiome.COLD_TAIGA_HILLS, CraftBlock.biomeToBiomeBase(Biome.SNOWY_TAIGA_HILLS));
        biomeMap.put(ICBiome.COLD_TAIGA_M, CraftBlock.biomeToBiomeBase(Biome.MUTATED_TAIGA_COLD));
        biomeMap.put(ICBiome.DEEP_OCEAN, CraftBlock.biomeToBiomeBase(Biome.DEEP_OCEAN));
        biomeMap.put(ICBiome.DESERT, CraftBlock.biomeToBiomeBase(Biome.DESERT));
        biomeMap.put(ICBiome.DESERT_HILLS, CraftBlock.biomeToBiomeBase(Biome.DESERT_HILLS));
        biomeMap.put(ICBiome.DESERT_M, CraftBlock.biomeToBiomeBase(Biome.MUTATED_DESERT));
        biomeMap.put(ICBiome.END, CraftBlock.biomeToBiomeBase(Biome.THE_END));
        biomeMap.put(ICBiome.EXTREME_HILLS, CraftBlock.biomeToBiomeBase(Biome.EXTREME_HILLS));
        biomeMap.put(ICBiome.EXTREME_HILLS_EDGE, CraftBlock.biomeToBiomeBase(Biome.SMALLER_EXTREME_HILLS));
        biomeMap.put(ICBiome.EXTREME_HILLS_M, CraftBlock.biomeToBiomeBase(Biome.MUTATED_EXTREME_HILLS));
        biomeMap.put(ICBiome.EXTREME_HILLS_PLUS, CraftBlock.biomeToBiomeBase(Biome.EXTREME_HILLS_WITH_TREES));
        biomeMap.put(ICBiome.EXTREME_HILLS_PLUS_M, CraftBlock.biomeToBiomeBase(Biome.MUTATED_EXTREME_HILLS_WITH_TREES));
        biomeMap.put(ICBiome.FLOWER_FOREST, CraftBlock.biomeToBiomeBase(Biome.MUTATED_FOREST));
        biomeMap.put(ICBiome.FOREST, CraftBlock.biomeToBiomeBase(Biome.FOREST));
        biomeMap.put(ICBiome.FOREST_HILLS, CraftBlock.biomeToBiomeBase(Biome.FOREST_HILLS));
        biomeMap.put(ICBiome.FROZEN_OCEAN, CraftBlock.biomeToBiomeBase(Biome.FROZEN_OCEAN));
        biomeMap.put(ICBiome.FROZEN_RIVER, CraftBlock.biomeToBiomeBase(Biome.FROZEN_RIVER));
        biomeMap.put(ICBiome.ICE_MOUNTAINS, CraftBlock.biomeToBiomeBase(Biome.ICE_MOUNTAINS));
        biomeMap.put(ICBiome.ICE_PLAINS, CraftBlock.biomeToBiomeBase(Biome.ICE_FLATS));
        biomeMap.put(ICBiome.ICE_PLAINS_SPIKES, CraftBlock.biomeToBiomeBase(Biome.ICE_SPIKES));
        biomeMap.put(ICBiome.JUNGLE, CraftBlock.biomeToBiomeBase(Biome.JUNGLE));
        biomeMap.put(ICBiome.JUNGLE_EDGE, CraftBlock.biomeToBiomeBase(Biome.JUNGLE_EDGE));
        biomeMap.put(ICBiome.JUNGLE_HILLS, CraftBlock.biomeToBiomeBase(Biome.JUNGLE_HILLS));
        biomeMap.put(ICBiome.JUNGLE_M, CraftBlock.biomeToBiomeBase(Biome.MUTATED_JUNGLE));
        biomeMap.put(ICBiome.JUNGLE_EDGE_M,CraftBlock.biomeToBiomeBase(Biome.MUTATED_JUNGLE_EDGE));
        biomeMap.put(ICBiome.MEGA_SPRUCE_TAIGA, CraftBlock.biomeToBiomeBase(Biome.MUTATED_REDWOOD_TAIGA));
        biomeMap.put(ICBiome.MEGA_SPRUCE_TAIGA_HILLS, CraftBlock.biomeToBiomeBase(Biome.MUTATED_REDWOOD_TAIGA_HILLS));
        biomeMap.put(ICBiome.MEGA_TAIGA, CraftBlock.biomeToBiomeBase(Biome.REDWOOD_TAIGA));
        biomeMap.put(ICBiome.MEGA_TAIGA_HILLS, CraftBlock.biomeToBiomeBase(Biome.REDWOOD_TAIGA_HILLS));
        biomeMap.put(ICBiome.MESA, CraftBlock.biomeToBiomeBase(Biome.MESA));
        biomeMap.put(ICBiome.MESA_BRYCE, CraftBlock.biomeToBiomeBase(Biome.MUTATED_MESA));
        biomeMap.put(ICBiome.MESA_PLATEAU, CraftBlock.biomeToBiomeBase(Biome.MESA_CLEAR_ROCK));
        biomeMap.put(ICBiome.MESA_PLATEAU_F, CraftBlock.biomeToBiomeBase(Biome.MESA_ROCK));
        biomeMap.put(ICBiome.MESA_PLATEAU_F_M, CraftBlock.biomeToBiomeBase(Biome.MUTATED_MESA_ROCK));
        biomeMap.put(ICBiome.MESA_PLATEAU_M, CraftBlock.biomeToBiomeBase(Biome.MUTATED_MESA_CLEAR_ROCK));
        biomeMap.put(ICBiome.MUSHROOM_ISLAND, CraftBlock.biomeToBiomeBase(Biome.MUSHROOM_ISLAND));
        biomeMap.put(ICBiome.MUSHROOM_ISLAND_SHORE, CraftBlock.biomeToBiomeBase(Biome.MUSHROOM_ISLAND_SHORE));
        biomeMap.put(ICBiome.NETHER, CraftBlock.biomeToBiomeBase(Biome.NETHER));
        biomeMap.put(ICBiome.OCEAN, CraftBlock.biomeToBiomeBase(Biome.OCEAN));
        biomeMap.put(ICBiome.PLAINS, CraftBlock.biomeToBiomeBase(Biome.PLAINS));
        biomeMap.put(ICBiome.RIVER, CraftBlock.biomeToBiomeBase(Biome.RIVER));
        biomeMap.put(ICBiome.ROOFED_FOREST, CraftBlock.biomeToBiomeBase(Biome.ROOFED_FOREST));
        biomeMap.put(ICBiome.ROOFED_FOREST_M, CraftBlock.biomeToBiomeBase(Biome.MUTATED_ROOFED_FOREST));
        biomeMap.put(ICBiome.SAVANNA, CraftBlock.biomeToBiomeBase(Biome.SAVANNA));
        biomeMap.put(ICBiome.SAVANNA_M, CraftBlock.biomeToBiomeBase(Biome.MUTATED_SAVANNA));
        biomeMap.put(ICBiome.SAVANNA_PLATEAU, CraftBlock.biomeToBiomeBase(Biome.SAVANNA_ROCK));
        biomeMap.put(ICBiome.SAVANNA_PLATEAU_M, CraftBlock.biomeToBiomeBase(Biome.MUTATED_SAVANNA_ROCK));
        biomeMap.put(ICBiome.STONE_BEACH, CraftBlock.biomeToBiomeBase(Biome.STONE_SHORE));
        biomeMap.put(ICBiome.SUNFLOWER_PLAINS, CraftBlock.biomeToBiomeBase(Biome.MUTATED_PLAINS));
        biomeMap.put(ICBiome.SWAMPLAND, CraftBlock.biomeToBiomeBase(Biome.SWAMP));
        biomeMap.put(ICBiome.SWAMPLAND_M, CraftBlock.biomeToBiomeBase(Biome.SWAMP_HILLS));
        biomeMap.put(ICBiome.TAIGA, CraftBlock.biomeToBiomeBase(Biome.TAIGA));
        biomeMap.put(ICBiome.TAIGA_HILLS, CraftBlock.biomeToBiomeBase(Biome.TAIGA_HILLS));
        biomeMap.put(ICBiome.TAIGA_M, CraftBlock.biomeToBiomeBase(Biome.TAIGA_MOUNTAINS));
        // TODO Biome.VOID -- new end biome???
    }

    private final BiomeCache biomeCache;
    private final BiomeGenerator biomeGenerator;

    public CustomWorldChunkManager(final BiomeGenerator biomeGenerator) {
        this.biomeGenerator = biomeGenerator;
        this.biomeCache = new BiomeCache(this);
    }

    /** Returns a list of biome's which are valid for spawn */
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List a() {
        return super.a();
    }



    /** Returns the biome at a position. Used for various things. */
    @Override
    public BiomeBase getBiome(BlockPosition blockPosition, BiomeBase biomeBase) {
        // Get from cache
        return this.biomeCache.a(blockPosition.getX(), blockPosition.getZ(), biomeBase);
    }

    @Override
    public BiomeBase[] getBiomes(int xMin, int zMin, int xSize, int zSize) {
        BiomeBase[] result = new BiomeBase[xSize * zSize];
        // 1 in every 4
        for (int i = 0; i < xSize * zSize; ++i) {
            final int x = (xMin + (i % xSize)) << 2;
            final int z = (zMin + (i / xSize)) << 2;
            final ICBiome biome = biomeGenerator.generateBiome(x, z);
            result[i] = biomeMap.get(biome);
        }
        return result;
    }

    @Override
    public Set<BiomeBase> a(int i, int i1, int i2) {
        return null;
    }

    @Override
    public boolean a(StructureGenerator<?> structureGenerator) {
        return false;
    }


    /** Used in chunk creation */
    @Override
    public BiomeBase[] getBiomeBlock(final int x, final int z, final int xSize, final int zSize) {
        return a(x, z, xSize, zSize, true);
    }

    /** Only used in above method... and getWetness */
    @Override
    public BiomeBase[] a(final int xMin, final int zMin, final int xSize, final int zSize, final boolean useCache) {
        // Create result array if given one is insufficient
            // Happens for nether, end, and flat worlds...
            BiomeBase[] result = new BiomeBase[xSize * zSize];
        // More efficient handling of whole chunk
        if (xSize == 16 && zSize == 16 && (xMin & 0xF) == 0 && (zMin & 0xF) == 0) {
            if (useCache) {
                // Happens most of the time
                final BiomeBase[] biomes = this.biomeCache.b(xMin, zMin);
                System.arraycopy(biomes, 0, result, 0, xSize * zSize);
                return result;
            }
            // This only happens in getWetness above
            final ICBiome[] temp = biomeGenerator.generateChunkBiomes(xMin, zMin);
            for (int i = 0; i < xSize * zSize; ++i) {
                result[i] = biomeMap.get(temp[i]);
            }
            return result;
        }
        // In reality this never happens...
        for (int x = 0; x < xSize; ++x) {
            for (int z = 0; z < zSize; ++z) {
                final ICBiome temp = biomeGenerator.generateBiome(xMin + x, zMin + z);
                result[x + z * xSize] = biomeMap.get(temp);
            }
        }
        return result;
    }


    /**
     * Returns random position within biome if it can be found in given area.
     * Otherwise null. Used for initial guess at spawn point and stronghold
     * locations.
     */
    @Override
    public BlockPosition a(final int x, final int z, final int radius, @SuppressWarnings("rawtypes") final List allowedBiomes, final Random random) {
        // Convert center and radius to minimum and size
        final int xMin = (x - radius) >> 2;
        final int zMin = (z - radius) >> 2;
        final int xMax = (x + radius) >> 2;
        final int zMax = (z + radius) >> 2;
        final int xSize = xMax - xMin + 1;
        final int zSize = zMax - zMin + 1;
        // Generate biome's
        final BiomeBase[] biomes = getBiomes(xMin, zMin, xSize, zSize);
        BlockPosition result = null;
        int count = 0;
        for (int i = 0; i < xSize * zSize; i++) {
            final int xPosition = (xMin + (i % xSize)) << 2;
            final int zPosition = (zMin + (i / xSize)) << 2;
            if (allowedBiomes.contains(biomes[i]) && (result == null || random.nextInt(count + 1) == 0)) {
                result = new BlockPosition(xPosition, 0, zPosition);
                count++;
            }
        }
        return result;
    }

    /** Cleans up biomeCache, called in tick */
    @Override
    public void X_() {
        // Clean up biomeCache
        biomeCache.a();
        biomeGenerator.cleanupCache();
    }

    // todo old

    /**
     * Returns true if all biome's in area are in allowedBiomes. x, z and radius
     * are in blocks. Used for checking where a village can go.
     */
    @Override
    public boolean a(final int x, final int z, final int radius,final List<BiomeBase> allowedBiomes) {
        // Convert center and radius to minimum and size
        final int xMin = (x - radius) >> 2;
        final int zMin = (z - radius) >> 2;
        final int xMax = (x + radius) >> 2;
        final int zMax = (z + radius) >> 2;
        final int xSize = xMax - xMin + 1;
        final int zSize = zMax - zMin + 1;
        // Generate biome's
        final BiomeBase[] biomes = getBiomes(xMin, zMin, xSize, zSize);
        // Make sure all biomes are allowed
        for (int i = 0; i < xSize * zSize; i++) {
            if (!allowedBiomes.contains(biomes[i])) {
                return false;
            }
        }
        return true;
    }



}