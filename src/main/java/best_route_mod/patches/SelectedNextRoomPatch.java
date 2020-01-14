package best_route_mod.patches;

import basemod.interfaces.StartGameSubscriber;
import best_route_mod.BestRouteMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import javassist.CtBehavior;

import java.util.ArrayList;

// Update the best path when the player selects a new room on the map (except boss)
@SpirePatch(
        clz= AbstractDungeon.class,
        method="nextRoomTransition",
        paramtypez = {SaveFile.class}
)
public class SelectedNextRoomPatch {
    private static boolean changedRooms = false;
    public static void resetChangedRooms(){
        changedRooms = false;
    }

    @SpireInsertPatch(
            locator = Locator.class
    )
    public static void Insert(AbstractDungeon __instance, SaveFile saveFile) {
        // We have moved to a new room
        if (changedRooms) {
            System.out.println("New room clicked on");
            // TODO: add payload
            // Disable highlighted paths if there are any
            BestRouteMod.
            // If moved to a boss node, don't generate and show route
            if(!currMapNodeEqualTo(-1, 15)){

            }
        }
        if(!changedRooms) changedRooms = true;
    }

    // Check if currMapNode is equal to x, y
    private static boolean currMapNodeEqualTo(int x, int y){
        return AbstractDungeon.currMapNode.x == x && AbstractDungeon.currMapNode.y == y;
    }

    private static class Locator extends SpireInsertLocator{
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws Exception {
            Matcher matcher = new Matcher.MethodCallMatcher(AbstractDungeon.class, "setCurrMapNode");
            int[] matchingLines =  LineFinder.findInOrder(ctBehavior, new ArrayList<>(), matcher);
            // Get the line after the current node was set
            matchingLines[0]++;
            return matchingLines;
        }
    }
}
