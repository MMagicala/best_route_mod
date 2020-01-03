package best_route_mod.patches;

import best_route_mod.BestRouteMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.Legend;
import com.megacrit.cardcrawl.map.MapRoomNode;

@SpirePatch(
        clz= AbstractDungeon.class,
        method="setCurrMapNode"
)
public class SetCurrNodePatch {
    @SpirePostfixPatch
    // When player moves up the map
    public static void Postfix() {
        if (AbstractDungeon.firstRoomChosen) {
            BestRouteMod.generateAndShowBestPathFromNode(AbstractDungeon.currMapNode);
        }
    }
}
