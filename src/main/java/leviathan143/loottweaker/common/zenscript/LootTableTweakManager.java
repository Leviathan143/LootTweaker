package leviathan143.loottweaker.common.zenscript;

import java.io.File;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import crafttweaker.CraftTweakerAPI;
import leviathan143.loottweaker.common.lib.LootTableDumper;
import leviathan143.loottweaker.common.lib.LootTableFinder;
import leviathan143.loottweaker.common.mutable_loot.MutableLootTable;
import leviathan143.loottweaker.common.zenscript.wrapper.ZenLootTableWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;

public class LootTableTweakManager
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<ResourceLocation, ZenLootTableWrapper> tweakedTables = new HashMap<>();
    private final Collection<ZenLootTableWrapper> tableBuilders = new HashSet<>();
    private final LootTweakerContext context;

    LootTableTweakManager(LootTweakerContext context)
    {
        this.context = context;
    }

	public ZenLootTableWrapper getTable(String tableName)
	{
	    return getTableInternal(tableName);
	}

	public ZenLootTableWrapper getTableUnchecked(String tableName)
	{
	    return getTable(tableName);
	}

	private ZenLootTableWrapper getTableInternal(String tableName)
    {
        ResourceLocation tableId = new ResourceLocation(tableName);
        ZenLootTableWrapper wrapper = tweakedTables.get(tableId);
        if (wrapper == null)
        {
            wrapper = context.wrapLootTable(tableId);
            if (!wrapper.isValid())
                context.getErrorHandler().error("No loot table with name %s exists!", tableName);
            else
                tweakedTables.put(tableId, wrapper);
        }
        return wrapper;
    }

    public ZenLootTableWrapper newTable(String id)
    {
        ResourceLocation tableId = new ResourceLocation(id);
        if (LootTableFinder.DEFAULT.exists(tableId))
        {
            context.getErrorHandler().error("Table id '%s' already in use", id);
            //Gotta return something non-null. This won't do anything because it'll never be applied.
            return context.wrapLootTable(tableId);
        }
        ZenLootTableWrapper builder = context.wrapLootTable(tableId);
        tableBuilders.add(builder);
        CraftTweakerAPI.logInfo("Created new table '" + id + "'");
        return builder;
    }

    public void onServerStart(MinecraftServer server)
    {
        File worldLootTables = server.getActiveAnvilConverter().getFile(server.getFolderName(), "data/loot_tables");
        LootTableDumper dumper = new LootTableDumper(worldLootTables);
        for (ZenLootTableWrapper builder : tableBuilders)
        {
            MutableLootTable mutableTable = new MutableLootTable(builder.getId(), new HashMap<>());
            builder.applyTweakers(mutableTable);
            dumper.dump(mutableTable.toImmutable(), builder.getId());
        }
    }

    public LootTable tweakTable(ResourceLocation tableId, LootTable table)
    {
        if (tweakedTables.containsKey(tableId))
        {
            if (table.isFrozen())
            {
                LOGGER.debug("Skipped modifying loot table {} because it is frozen", tableId);
                return table;
            }
            MutableLootTable mutableTable = new MutableLootTable(table, tableId);
            tweakedTables.get(tableId).applyTweakers(mutableTable);
            return mutableTable.toImmutable();
        }
        return table;
    }
}
