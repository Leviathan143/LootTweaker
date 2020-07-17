package leviathan143.loottweaker.common.zenscript.impl;

import leviathan143.loottweaker.common.zenscript.api.LootTableLoadCraftTweakerEvent;
import leviathan143.loottweaker.common.zenscript.api.LootTableRepresentation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;

public class LootTableLoadCraftTweakerEventImpl implements LootTableLoadCraftTweakerEvent
{
    private final ResourceLocation tableId;
    private final LootTable table;
    private final LootTweakerContext context;
    private MutableLootTable mutableLootTable;

    public LootTableLoadCraftTweakerEventImpl(ResourceLocation tableId, LootTable table, LootTweakerContext context)
    {
        this.tableId = tableId;
        this.table = table;
        this.context = context;
    }

    @Override
    public String getTableId()
    {
        return tableId.toString();
    }

    @Override
    public LootTableRepresentation getTable()
    {
        if (mutableLootTable == null)
            mutableLootTable = new MutableLootTable(table, tableId, context);
        return mutableLootTable;
    }

    public boolean wasTableModified()
    {
        return mutableLootTable != null;
    }

    public LootTable getModifiedTable()
    {
        assert wasTableModified(): "Table not modified";
        return mutableLootTable.toImmutable();
    }
}
