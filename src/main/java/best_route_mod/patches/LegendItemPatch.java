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
                if (AbstractDungeon.dungeonMapScreen.map.legend.items.get(i).hb.hovered && InputHelper.justClickedLeft) {
                    BestRouteMod.selectedRoomIndex = i;
                    System.out.println("Clicked on " + BestRouteMod.roomClasses[i].getName());
                    BestRouteMod.setRoomClass(BestRouteMod.roomClasses[i]);
                    // Regenerate new best path
                    if (!BestRouteMod.currMapNodeAtWhale()) {
                        BestRouteMod.generateAndShowBestPathFromCurrentNode();
                    } else {
                        BestRouteMod.generateAndShowBestPathFromStartingNodes();
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
            System.out.println(BestRouteMod.selectedRoomIndex);
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
