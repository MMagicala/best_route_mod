package best_route_mod.patches;

import basemod.ReflectionHacks;
import best_route_mod.BestRouteMod;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.map.Legend;
import com.megacrit.cardcrawl.map.LegendItem;

public class LegendItemPatch {

    @SpirePatch(
            clz= Legend.class,
            method="update"
    )
    public static class LeftClickLegendItemPatch {
        @SpirePostfixPatch
        public static void Postfix(Legend __instance) {
            for (int i = 0; i < __instance.items.size(); i++) {
                if (AbstractDungeon.dungeonMapScreen.map.legend.items.get(i).hb.hovered) {
                    if (InputHelper.justClickedLeft) {
                        // Raise priority of a room comparison by providing the room class of the comparison
                        BestRouteMod.raiseComparisonPriority(i);
                        // Regenerate new best path
                        // TODO: don't keep order in a separate list, use ReflectionHacks instead
                        if (!BestRouteMod.atBeginningOfAct()) {
                            BestRouteMod.generateAndShowBestPathFromCurrentNode();
                        } else {
                            BestRouteMod.generateAndShowBestPathFromStartingNodes();
                        }
                    }else if(InputHelper.justClickedRight){
                        // Lower priority of a room being compared
                    }
                }
            }
        }
    }

    @SpirePatch(
            clz=LegendItem.class,
            method="render"
    )
    public static class LegendItemRenderPatch{
        @SpirePostfixPatch
        public static void Postfix(LegendItem __instance, SpriteBatch sb, Color c){
            int index = (int) ReflectionHacks.getPrivate(__instance, LegendItem.class, "index");
            String labelString = (String) ReflectionHacks.getPrivate(__instance, LegendItem.class, "label");
            if(index != BestRouteMod.selectedRoomIndex){
                if(labelString.charAt(0) == '>') ReflectionHacks.setPrivate(__instance, LegendItem.class, "label", labelString.substring(2));
                return;
            }
            if(labelString.charAt(0) != '>') ReflectionHacks.setPrivate(__instance, LegendItem.class, "label", "> " + labelString);
        }
    }
}
