package io.github.daomephsta.loottweaker.test.pool;

import static io.github.daomephsta.loottweaker.test.TestLootConditionAccessors.isInverted;
import static io.github.daomephsta.loottweaker.test.TestUtils.loadTable;
import static io.github.daomephsta.loottweaker.test.assertion.LootTweakerAssertions.assertThat;

import crafttweaker.api.data.IData;
import io.github.daomephsta.loottweaker.test.TestUtils;
import io.github.daomephsta.loottweaker.test.util.DataMapBuilder;
import io.github.daomephsta.saddle.engine.SaddleTest;
import io.github.daomephsta.saddle.engine.SaddleTest.LoadPhase;
import leviathan143.loottweaker.common.zenscript.LootTweakerContext;
import leviathan143.loottweaker.common.zenscript.factory.LootConditionFactory;
import leviathan143.loottweaker.common.zenscript.wrapper.ZenLootConditionWrapper;
import leviathan143.loottweaker.common.zenscript.wrapper.ZenLootPoolWrapper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.conditions.KilledByPlayer;


public class LootTableEntryAdditionTests
{
    private final LootTweakerContext context = TestUtils.context();

    @SaddleTest(loadPhase = LoadPhase.PRE_INIT)
    public void addLootTableEntry()
    {
        ResourceLocation fooId = new ResourceLocation("loottweaker", "foo");
        LootTable foo = loadTable(fooId);
        ZenLootPoolWrapper barTweaks = context.wrapPool("bar", fooId);
        barTweaks.addLootTableEntry("loottweaker:qux", 2, "corge");
        barTweaks.tweak(foo);

        assertThat(foo.getPool("bar"))
            .extractEntry("corge")
            .hasWeight(2)
            .hasNoLootConditions()
            .asLootTableEntry()
            .spawnsFromTable("loottweaker:qux");
    }

    @SaddleTest(loadPhase = LoadPhase.PRE_INIT)
    public void addLootTableEntryWithQuality()
    {
        ResourceLocation fooId = new ResourceLocation("loottweaker", "foo");
        LootTable foo = loadTable(fooId);
        ZenLootPoolWrapper barTweaks = context.wrapPool("bar", fooId);
        barTweaks.addLootTableEntry("loottweaker:qux", 2, 3, "corge");
        barTweaks.tweak(foo);

        assertThat(foo.getPool("bar"))
            .extractEntry("corge")
            .hasWeight(2)
            .hasQuality(3)
            .hasNoLootConditions()
            .asLootTableEntry()
            .spawnsFromTable("loottweaker:qux");
    }

    @SaddleTest(loadPhase = LoadPhase.PRE_INIT)
    public void addLootTableEntryWithCondition()
    {
        ResourceLocation fooId = new ResourceLocation("loottweaker", "foo");
        LootTable foo = loadTable(fooId);
        ZenLootPoolWrapper barTweaks = context.wrapPool("bar", fooId);
        barTweaks.addLootTableEntryHelper("loottweaker:qux", 2, 3,
            new ZenLootConditionWrapper[] {LootConditionFactory.killedByPlayer()},
            "corge");
        barTweaks.tweak(foo);

        assertThat(foo.getPool("bar"))
            .extractEntry("corge")
            .hasWeight(2)
            .hasQuality(3)
            .hasMatchingCondition(condition ->
                condition instanceof KilledByPlayer && !isInverted((KilledByPlayer) condition),
            "KilledByPlayer()")
            .asLootTableEntry()
            .spawnsFromTable("loottweaker:qux");
    }

    @SaddleTest(loadPhase = LoadPhase.PRE_INIT)
    public void addLootTableEntryJson()
    {
        ResourceLocation fooId = new ResourceLocation("loottweaker", "foo");
        LootTable foo = loadTable(fooId);
        ZenLootPoolWrapper barTweaks = context.wrapPool("bar", fooId);
        barTweaks.addLootTableEntryJson("loottweaker:qux", 2, 3,
            new IData[]
            {
                new DataMapBuilder()
                    .putString("condition", "minecraft:killed_by_player")
                    .build()
            },
            "corge");
        barTweaks.tweak(foo);

        assertThat(foo.getPool("bar"))
            .extractEntry("corge")
            .hasWeight(2)
            .hasQuality(3)
            .hasMatchingCondition(condition ->
                condition instanceof KilledByPlayer && !isInverted((KilledByPlayer) condition),
            "KilledByPlayer()")
            .asLootTableEntry()
            .spawnsFromTable("loottweaker:qux");
    }
}