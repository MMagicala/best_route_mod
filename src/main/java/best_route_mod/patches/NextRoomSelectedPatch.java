package best_route_mod.patches;

import best_route_mod.ColorPathManager;
import best_route_mod.MapPath;
import best_route_mod.MapReader;
import best_route_mod.RoomClassManager;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.map.DungeonMap;
import com.megacrit.cardcrawl.map.MapRoomNode;
import javassist.CtBehavior;

import java.util.ArrayList;

public class NextRoomSelectedPatch {
    // Update the best path when the player clicks on a new node (not the boss)
    @SpirePatch(
            clz= MapRoomNode.class,
            method="update"
    )
    public static class NodeSelectedPatch{
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(MapRoomNode __instance){
            if(!RoomClassManager.allRoomClassesInActive()) {
                ColorPathManager.disableCurrentlyColoredPath();
                MapPath bestPath = MapReader.getBestPathFrom(__instance);
                ColorPathManager.colorPath(bestPath);
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher matcher = new Matcher.MethodCallMatcher(MapRoomNode.class, "playNodeSelectedSound");
                return LineFinder.findAllInOrder(ctBehavior, new ArrayList<>(), matcher);
            }
        }
    }


    // Disables the best path when clicking on the boss node
    @SpirePatch(
            clz= DungeonMap.class,
            method="update"
    )
    public static class BossNodeSelectedPatch{
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(DungeonMap __instance){
            // Player can jump from a node to boss in some events, so we need to check if the path was colored
            if(ColorPathManager.isPathColored()){
                ColorPathManager.disableCurrentlyColoredPath();
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher matcher = new Matcher.FieldAccessMatcher(InputHelper.class, "justClickedLeft");
                int[] results = LineFinder.findInOrder(ctBehavior, new ArrayList<>(), matcher);
                results[0]++;
                return results;
            }
        }
    }
}