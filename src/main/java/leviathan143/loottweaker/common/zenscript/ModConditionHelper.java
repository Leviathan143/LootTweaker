package leviathan143.loottweaker.common.zenscript;

import crafttweaker.annotations.ZenRegister;
import leviathan143.loottweaker.common.LootTweakerMain;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass(LootTweakerMain.MODID + ".vanilla.loot.ModConditions")
public class ModConditionHelper 
{
	public static final ModConditionHelper INSTANCE = new ModConditionHelper();
}
