package best_route_mod.patches;

import best_route_mod.BestRouteMod;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.map.Legend;
import com.megacrit.cardcrawl.map.LegendItem;
import com.megacrit.cardcrawl.rooms.*;

import static best_route_mod.BestRouteMod.roomClasses;
import static best_route_mod.BestRouteMod.selectedRoomIndex;

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
                    selectedRoomIndex = i;
                    System.out.println("Clicked on " + roomClasses[i].getName());
                    BestRouteMod.setRoomClass(roomClasses[i]);
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
        @SpirePrefixPatch
        // TODO fix patch
        // Use index in LegendItem class
        public static void Prefix(SpriteBatch sb, Color c, Color ___c, int ___index){
            if(selectedRoomIndex == ___index) ___c = Color.RED;
        }
    }
}
