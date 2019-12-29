package best_route_mod.patches;

import best_route_mod.BestRouteMod;
import best_route_mod.SignOperator;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.map.Legend;
import com.megacrit.cardcrawl.rooms.*;

@SpirePatch(
        clz= Legend.class,
        method="update"
)
public class LeftClickMapIconPatch {
    // Create this list since we can't read which legend item's class is
    static Class[] roomClasses = {EventRoom.class, ShopRoom.class, TreasureRoom.class, RestRoom.class, RestRoom.class, MonsterRoom.class,
    MonsterRoomElite.class};
    @SpirePostfixPatch
    public static void Postfix(Legend __instance) {
        for(int i = 0; i < __instance.items.size(); i++){
            if(AbstractDungeon.dungeonMapScreen.map.legend.items.get(i).hb.hovered && InputHelper.justClickedLeft){
                BestRouteMod.clearComparisonsAtIndex(0);
                // Create new RoomComparison
                RoomComparison roomComparison = new RoomComparison(roomClasses[i], SignOperator.GREATER);
                BestRouteMod.addComparisonAtIndex(roomComparison, 0);
                // Regenerate new best path
                if(!BestRouteMod.currMapNodeAtWhale()){
                    System.out.println("CurrMapNode at whale");
                    BestRouteMod.generateAndShowBestPathFromCurrentNode();
                }else{
                    System.out.println("CurrMapNode not at whale");
                    BestRouteMod.generateAndShowBestPathFromStartingNodes();
                }
            }
        }
    }
}