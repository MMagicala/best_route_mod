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
        // TODO: work on this
        private static boolean isMiddleButtonJustPressed = false;
        private static boolean isMiddleButtonPressedAfterFirstCycle = false;
        @SpirePostfixPatch
        public static void Postfix(Legend __instance) {
            // Middle button just pressed code

            // Disable middle button just pressed if it was true last cycle
            if(Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)){
                if(!isMiddleButtonJustPressed && !isMiddleButtonPressedAfterFirstCycle){
                    isMiddleButtonJustPressed = true;
                }else{
                    isMiddleButtonJustPressed = false;
                    isMiddleButtonPressedAfterFirstCycle = true;
                }
            }else{
                isMiddleButtonJustPressed = false;
                isMiddleButtonPressedAfterFirstCycle = false;
            }

            for (int i = 0; i < __instance.items.size(); i++) {
                Class<?> roomClass = BestRouteMod.getRoomClassByLegendIndex(i);
                if (AbstractDungeon.dungeonMapScreen.map.legend.items.get(i).hb.hovered) {
                    // Check for key presses
                    boolean signInverted = false;
                    char newSign = BestRouteMod.getSignOfRoomClass(roomClass) == '>' ? '<' : '>';
                    if(isMiddleButtonJustPressed){
                        BestRouteMod.setSignOfRoomClass(roomClass, newSign);
                        signInverted = true;
                    }

                    // Check for mouse clicks
                    boolean priorityChanged = false;
                    // Middle button also left clicks so check for that
                    if (InputHelper.justClickedLeft && !Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
                        priorityChanged = BestRouteMod.raiseRoomClassPriority(roomClass);
                    }else if(InputHelper.justClickedRight){
                        priorityChanged = BestRouteMod.lowerRoomClassPriority(roomClass);
                    }
                    if(priorityChanged || signInverted){
                        // Disable highlighted path if all the priority indices are zero
                        if(BestRouteMod.allPriorityIndicesAreZero()){
                            BestRouteMod.disableCurrentBestPath();
                            continue;
                        }
                        // Regenerate new best path
                        if (AbstractDungeon.firstRoomChosen) {
                            BestRouteMod.generateAndShowBestPathFromCurrentNode();
                        } else {
                            BestRouteMod.generateAndShowBestPathFromStartingNodes();
                        }
                    }
                }

                // Update legend item text
                String labelString = (String) ReflectionHacks.getPrivate(__instance.items.get(i), LegendItem.class, "label");
                int priorityIndex = BestRouteMod.getPriorityIndexOfRoomClass(roomClass);
                char sign = BestRouteMod.getSignOfRoomClass(roomClass);
                // Clear any existing changes made to the label
                if(labelString.endsWith(")")) {
                    int leftParenIndex = labelString.lastIndexOf(" (");
                    labelString = labelString.substring(0, leftParenIndex);
                    ReflectionHacks.setPrivate(__instance.items.get(i), LegendItem.class, "label", labelString);
                }
                // Print the priority index and sign
                String newLabelString = labelString + " (" + priorityIndex + ", " + sign + ")";
                ReflectionHacks.setPrivate(__instance.items.get(i), LegendItem.class, "label", newLabelString);
            }
        }
    }
}
