package best_route_mod.patches;

import best_route_mod.BestRouteMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

@SpirePatch(
        clz= AbstractDungeon.class,
        method="generateMap"
)
public class MapGenerationPatch {
    @SpirePostfixPatch
    // Reset mod settings whenever the map loads (generateMap loads the same map when continuing games since the seed
    // is the same)
    public static void Postfix(){
        BestRouteMod.resetMod();
    }
}
