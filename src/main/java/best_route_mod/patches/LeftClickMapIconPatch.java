package best_route_mod.patches;

import best_route_mod.BestRouteMod;
import best_route_mod.RoomComparison;
import best_route_mod.SignOperator;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.map.Legend;
import com.megacrit.cardcrawl.map.LegendItem;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.*;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

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
        System.out.println(__instance.items.size());
        for(int i = 0; i < __instance.items.size(); i++){
            // TODO: fix clicking
            if(AbstractDungeon.dungeonMapScreen.map.legend.items.get(i).hb.clicked){
                System.out.println(i + " clicked!");
                if(BestRouteMod.comparisonExistsOnTop()) BestRouteMod.removeComparisonOnTop();
                // Create new RoomComparison
                RoomComparison roomComparison = new RoomComparison(roomClasses[i], SignOperator.GREATER);
                BestRouteMod.addComparisonOnTop(roomComparison);
                // Regenerate new best path
                BestRouteMod.generateAndShowBestPathFromCurrentNode();
            }
        }
    }
}