import loottweaker.LootTweaker;
import loottweaker.LootTable;

lootTweakerApi.tweakLootTable("loottweaker:foo", function(foo)
{
    val bar = foo.getPool("bar");
    foo.removePool("bar");
    foo.addPool("qux", 0, 1);
    foo.addPool("quuz", 1, 2, 0, 1);
    foo.removeAllPools();
});
lootTweakerApi.tweakLootTable("loottweaker:baz", function(baz)
{
    val poolsIter = baz.poolsIterator();
    for pool in poolsIter
    {
        poolsIter.removeCurrent();
    }
});