package best_route_mod.patches;

import best_route_mod.BestRouteMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.TheEnding;

public class MapGenerationPatch {
    // Reset mod settings whenever the map loads (generateMap loads the same map when continuing games since the seed
    // is the same)
    @SpirePatch(
            clz= AbstractDungeon.class,
            method="generateMap"
    )
    public static class RegularGenerationPatch {
        @SpirePostfixPatch
        public static void Postfix() {
            BestRouteMod.resetMod();
        }
    }

    @SpirePatch(
            clz= TheEnding.class,
            method="generateSpecialMap"
    )
    public static class SpecialGenerationPatch {
        @SpirePostfixPatch
        public static void Postfix() {
            BestRouteMod.resetMod();
        }
    }
}
