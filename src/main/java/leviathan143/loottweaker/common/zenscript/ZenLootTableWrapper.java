package leviathan143.loottweaker.common.zenscript;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import leviathan143.loottweaker.common.LootTweakerMain.Constants;
import leviathan143.loottweaker.common.darkmagic.CommonMethodHandles;
import leviathan143.loottweaker.common.lib.IDelayedTweak;
import leviathan143.loottweaker.common.lib.LootUtils;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass(Constants.MODID + ".vanilla.loot.LootTable")
public class ZenLootTableWrapper 
{
    private ResourceLocation name;
    private final LootTable backingTable;
    private final List<IDelayedTweak<LootTable, ZenLootTableWrapper>> delayedTweaks = Lists.newArrayList();
    private final Map<String, ZenLootPoolWrapper> wrapperCache = Maps.newHashMap();
    //If true the table is wiped
    private boolean clear;

    public ZenLootTableWrapper(LootTable table, ResourceLocation name) 
    {
	this.backingTable = table;
	this.name = name;
    }

    @ZenMethod
    public void clear()
    {
	clear = true;
    }

    @ZenMethod
    public ZenLootPoolWrapper addPool(String poolName, int minRolls, int maxRolls, int minBonusRolls, int maxBonusRolls)
    {
	LootPool pool = LootUtils.createPool(poolName, minRolls, maxRolls, minBonusRolls, maxBonusRolls);
	MineTweakerAPI.apply(new AddPool(this, name, pool));
	ZenLootPoolWrapper wrapper = new ZenLootPoolWrapper(pool);
	wrapperCache.put(poolName, wrapper);
	return wrapper;
    }

    @ZenMethod
    public void removePool(String poolName)
    {
	MineTweakerAPI.apply(new RemovePool(this, name, poolName));
    }

    @ZenMethod
    public ZenLootPoolWrapper getPool(String poolName)
    {
	LootPool pool = null;
	//Create a temporary pool if the pool is a default pool
	if(LootUtils.DEFAULT_POOL_REGEX.matcher(poolName).find())
	{
	    pool = LootUtils.createTemporaryPool(poolName);
	    backingTable.addPool(pool);
	    delayedTweaks.add(new TweakPool(poolName));
	} 
	if(!wrapperCache.containsKey(poolName))
	    wrapperCache.put(poolName, new ZenLootPoolWrapper(pool));
	return wrapperCache.get(poolName);
    }
    
    public void applyLootTweaks(LootTable table)
    {
	if(clear) CommonMethodHandles.getPoolsFromTable(table).clear();
	for(IDelayedTweak<LootTable, ZenLootTableWrapper> tweak : delayedTweaks)
	{
	    tweak.applyTweak(table, this);
	}
    }

    private static class TweakPool implements IDelayedTweak<LootTable, ZenLootTableWrapper>
    {
	private String poolName;
	
	public TweakPool(String poolName)
	{
	    this.poolName = poolName;
	}
	
	@Override
	public void applyTweak(LootTable table, ZenLootTableWrapper wrapper)
	{
	    wrapper.wrapperCache.get(poolName).applyLootTweaks(table.getPool(poolName));
	}
    }
    
    private static class AddPool implements IUndoableAction, IDelayedTweak<LootTable, ZenLootTableWrapper>
    {
	private ZenLootTableWrapper wrapper;
	private ResourceLocation tableName;
	private LootPool pool;

	public AddPool(ZenLootTableWrapper wrapper, ResourceLocation name, LootPool pool) 
	{
	    this.wrapper = wrapper;
	    this.tableName = name;
	    this.pool = pool;
	}

	@Override
	public void apply() 
	{
	    wrapper.delayedTweaks.add(this);
	}
	
	@Override
	public void applyTweak(LootTable table, ZenLootTableWrapper wrapper)
	{
	    table.addPool(pool);
	    wrapper.wrapperCache.get(pool.getName()).applyLootTweaks(pool);
	}

	@Override
	public boolean canUndo()
	{
	    return true;
	}

	@Override
	public void undo() 
	{
	    wrapper.delayedTweaks.remove(this);
	}

	@Override
	public String describe() 
	{
	    return String.format("Adding pool %s to table %s", pool.getName(), tableName);
	}

	@Override
	public String describeUndo() 
	{
	    return String.format("Removing pool %s from table %s", pool.getName(), tableName);
	}

	@Override
	public Object getOverrideKey() 
	{
	    return null;
	}
    }

    private static class RemovePool implements IUndoableAction, IDelayedTweak<LootTable, ZenLootTableWrapper>
    {
	private ZenLootTableWrapper wrapper;
	private ResourceLocation tableName;
	private String poolName;

	public RemovePool(ZenLootTableWrapper wrapper, ResourceLocation tableName, String poolName) 
	{
	    this.wrapper = wrapper;
	    this.tableName = tableName;
	    this.poolName = poolName;
	}

	@Override
	public void apply() 
	{
	    wrapper.delayedTweaks.add(this);
	}

	@Override
	public void applyTweak(LootTable table, ZenLootTableWrapper wrapper)
	{
	    if(table.getPool(poolName) == null)
	    {
		MineTweakerAPI.logError(String.format("No loot pool with name %s exists!", poolName));
		return;
	    }
	    table.removePool(poolName);
	}

	@Override
	public boolean canUndo()
	{
	    return true;
	}

	@Override
	public void undo() 
	{
	    wrapper.delayedTweaks.remove(this);
	}

	@Override
	public String describe() 
	{
	    return String.format("Adding pool %s to table %s", poolName, tableName);
	}

	@Override
	public String describeUndo() 
	{
	    return String.format("Removing pool %s from table %s", poolName, tableName);
	}

	@Override
	public Object getOverrideKey() 
	{
	    return null;
	}
    }
}
