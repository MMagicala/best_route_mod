package best_route_mod.patches;

import basemod.ReflectionHacks;
import best_route_mod.BestRouteMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputAction;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
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
                    Class<?> roomClass = BestRouteMod.getRoomClassByLegendIndex(i);
                    // Check for key presses
                    boolean signInverted = false;
                    char newSign = BestRouteMod.getSignOfRoomClass(roomClass) == '>' ? '<' : '>';
                    if(Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)){
                        BestRouteMod.setSignOfRoomClass(roomClass, newSign);
                        signInverted = true;
                    }

                    // Check for mouse clicks
                    boolean priorityChanged = false;
                    if (InputHelper.justClickedLeft) {
                        priorityChanged = BestRouteMod.raiseRoomClassPriority(roomClass);
                    }else if(InputHelper.justClickedRight){
                        priorityChanged = BestRouteMod.lowerRoomClassPriority(roomClass);
                    }
                    if(priorityChanged || signInverted){
                        // Regenerate new best path
                        if (!BestRouteMod.atBeginningOfAct()) {
                            BestRouteMod.generateAndShowBestPathFromCurrentNode();
                        } else {
                            BestRouteMod.generateAndShowBestPathFromStartingNodes();
                        }
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
        public static void Postfix(LegendItem __instance, SpriteBatch sb, Color c) {
            int legendIndex = (int) ReflectionHacks.getPrivate(__instance, LegendItem.class, "index");
            String labelString = (String) ReflectionHacks.getPrivate(__instance, LegendItem.class, "label");
            Class<?> roomClass = BestRouteMod.getRoomClassByLegendIndex(legendIndex);
            int priorityIndex = BestRouteMod.getPriorityIndexOfRoomClass(roomClass);
            char sign = BestRouteMod.getSignOfRoomClass(roomClass);

            if (priorityIndex > 0 && !labelString.endsWith(")")) {
                String newLabelString = labelString + " (" + priorityIndex + ", " + sign + ")";
                ReflectionHacks.setPrivate(__instance, LegendItem.class, "label", newLabelString);
            }else if(priorityIndex == 0 && labelString.endsWith(")")){
                int leftParenIndex = labelString.lastIndexOf('(');
                ReflectionHacks.setPrivate(__instance, LegendItem.class, "label", labelString.substring(0, leftParenIndex));
            }
        }
    }
}
