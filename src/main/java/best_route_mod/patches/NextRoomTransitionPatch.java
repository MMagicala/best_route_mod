package best_route_mod.patches;

import best_route_mod.BestRouteMod;
import best_route_mod.ColorPathManager;
import best_route_mod.MapReader;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.map.DungeonMap;
import com.megacrit.cardcrawl.relics.NeowsLament;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import javassist.CtBehavior;

import java.util.ArrayList;

@SpirePatch(
    clz= AbstractDungeon.class,
    method="nextRoomTransition",
    paramtypez = {SaveFile.class}
)
public class NextRoomTransitionPatch {
    @SpirePostfixPatch
    public static void Postfix(AbstractDungeon __instance, SaveFile saveFile){
        if(saveFile == null) {
            int neowsLamentCounter = BestRouteMod.getNeowsLamentCounter();
            if(InputHelperPatch.isNeowsLamentFactored && MapReader.nodeContainsCombatRoom(AbstractDungeon.currMapNode)){
                if(neowsLamentCounter == 1) {
                    InputHelperPatch.isNeowsLamentFactored = false;
                    InputHelperPatch.lockNeowsLamentHotKey = true;
                }
            }
            if(InputHelperPatch.isEmeraldKeyRequired && !MapReader.isEmeraldKeyReachableFrom(AbstractDungeon.currMapNode)){
                InputHelperPatch.isEmeraldKeyRequired = false;
            }
            // Calculate neows lament counter to pass into function
            BestRouteMod.reRenderPath(AbstractDungeon.currMapNode, true);
        }
    }
}
