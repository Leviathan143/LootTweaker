package leviathan143.loottweaker.common.zenscript;

import leviathan143.loottweaker.common.ErrorHandler;
import leviathan143.loottweaker.common.zenscript.factory.LootConditionFactoryImpl;
import leviathan143.loottweaker.common.zenscript.factory.LootFunctionFactoryImpl;
import leviathan143.loottweaker.common.zenscript.wrapper.ZenLootPoolWrapper;
import leviathan143.loottweaker.common.zenscript.wrapper.ZenLootTableWrapper;
import net.minecraft.util.ResourceLocation;

public class LootTweakerContext
{
    private final ErrorHandler errorHandler;

    public LootTweakerContext(ErrorHandler errorHandler)
    {
        this.errorHandler = errorHandler;
    }

    public ErrorHandler getErrorHandler()
    {
        return errorHandler;
    }

    public LootTableTweakManager createLootTableTweakManager()
    {
        return new LootTableTweakManager(this);
    }

    public LootConditionFactoryImpl createLootConditionFactory()
    {
        return new LootConditionFactoryImpl(errorHandler);
    }

    public LootFunctionFactoryImpl createLootFunctionFactory()
    {
        return new LootFunctionFactoryImpl(errorHandler);
    }

    public ZenLootTableWrapper wrapLootTable(ResourceLocation id)
    {
        return new ZenLootTableWrapper(this, id);
    }

    public ZenLootPoolWrapper wrapPool(String poolName, ResourceLocation parentId)
    {
        return new ZenLootPoolWrapper(errorHandler, poolName, parentId);
    }

    public ZenLootPoolWrapper createPoolWrapper(String poolName, ResourceLocation parentId, int minRolls, int maxRolls, int minBonusRolls, int maxBonusRolls)
    {
        return new ZenLootPoolWrapper(errorHandler, poolName, parentId, minRolls, maxRolls, minBonusRolls, maxBonusRolls);
    }
}