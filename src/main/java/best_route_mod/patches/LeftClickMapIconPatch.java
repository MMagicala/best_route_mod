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
    static RoomComparison[] roomClasses =
    {
            new RoomComparison(EventRoom.class),
            new RoomComparison(ShopRoom.class),
            new RoomComparison(TreasureRoom.class),
            new RoomComparison(RestRoom.class),
            new RoomComparison(RestRoom.class),
            new RoomComparison(MonsterRoom.class),
            new RoomComparison(MonsterRoomElite.class)
    };
    @SpirePostfixPatch
    public static void Postfix(Legend __instance) {
        for(int i = 0; i < __instance.items.size(); i++){
            // TODO: fix this part
            if(AbstractDungeon.dungeonMapScreen.map.legend.items.get(i).hb.hovered){
                if(InputHelper.justClickedLeft){

                }else if(InputHelper.justClickedRight){

                }

                // Regenerate new best path
                if(!BestRouteMod.currMapNodeAtWhale()){
                    BestRouteMod.generateAndShowBestPathFromCurrentNode();
                }else{
                    BestRouteMod.generateAndShowBestPathFromStartingNodes();
                }
            }
        }
    }
}