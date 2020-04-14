package io.github.daomephsta.loottweaker.test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.daomephsta.loottweaker.test.TestErrorHandler.LootTweakerException;
import io.github.daomephsta.saddle.engine.SaddleTest;
import io.github.daomephsta.saddle.engine.SaddleTest.LoadPhase;
import leviathan143.loottweaker.common.zenscript.LootTableTweakManager;
import leviathan143.loottweaker.common.zenscript.LootTweakerContext;
import net.minecraft.util.ResourceLocation;

public class LootTableTweakManagerTests
{
    private final LootTweakerContext context = TestUtils.context();

    @SaddleTest(loadPhase = LoadPhase.PRE_INIT)
    public void getTableCheckExisting()
    {
        ResourceLocation existingTableId = new ResourceLocation("loottweaker", "bar");
        LootTableTweakManager tableTweakManager = context.createLootTableTweakManager();
        tableTweakManager.getTable(existingTableId.toString());
    }

    @SaddleTest(loadPhase = LoadPhase.PRE_INIT)
    public void getTableCheckNonExistent()
    {
        ResourceLocation nonExistentTableId = new ResourceLocation("loottweaker", "non_existent_table");
        LootTableTweakManager tableTweakManager = context.createLootTableTweakManager();
        assertThatThrownBy(() -> tableTweakManager.getTable(nonExistentTableId.toString()))
            .isInstanceOf(LootTweakerException.class)
            .hasMessage("No loot table with name %s exists!", nonExistentTableId);
    }
}
