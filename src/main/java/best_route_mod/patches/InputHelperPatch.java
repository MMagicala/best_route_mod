package best_route_mod.patches;

import basemod.helpers.RelicType;
import best_route_mod.ColorPathManager;
import best_route_mod.MapPath;
import best_route_mod.MapReader;
import best_route_mod.RoomClassManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.map.Legend;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.NeowsLament;

public class InputHelperPatch {
    private static boolean isMiddleButtonJustPressed = false;
    private static boolean isMiddleButtonPressedAfterFirstCycle = false;

    // Inject middle button just pressed code into InputHelper's update function
    @SpirePatch(
            clz= InputHelper.class,
            method="updateFirst"
    )
    public static class MiddleButtonJustPressedPatch {
        @SpirePostfixPatch
        public static void Postfix() {
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
        }
    }

    // So we can tell if middle button was just pressed outside of this class
    public static boolean getMiddleButtonJustPressed(){
        return isMiddleButtonJustPressed;
    }

    // TODO: Neow's lament
    @SpirePatch(
            clz=InputHelper.class,
            method="updateFirst"
    )
    public static class NeowsLamentHotkeyPatch {
        // TODO: Check for edge cases with this
        private static boolean factorNeowsLament = false;
        @SpirePostfixPatch
        public static void Postfix() {
            if(Gdx.input.isKeyJustPressed(Input.Keys.N) && AbstractDungeon.player.hasRelic(NeowsLament.ID)
                && getNeowsLamentCounter() > 0){
                factorNeowsLament = !factorNeowsLament;

                ColorPathManager.disableCurrentlyColoredPath();
                MapPath bestPath;
                if(factorNeowsLament) {
                    if (AbstractDungeon.firstRoomChosen) {
                        bestPath = MapReader.getBestPathFrom(AbstractDungeon.currMapNode, getNeowsLamentCounter());
                    } else {
                        bestPath = MapReader.getBestPathFrom(MapReader.getStartingNodes(), getNeowsLamentCounter());
                    }
                }else{
                    if(!RoomClassManager.allRoomClassesInActive()) {
                        if (AbstractDungeon.firstRoomChosen) {
                            bestPath = MapReader.getBestPathFrom(AbstractDungeon.currMapNode);
                        } else {
                            bestPath = MapReader.getBestPathFrom(MapReader.getStartingNodes());
                        }
                        ColorPathManager.colorPath(bestPath);
                    }
                }
            }
        }

        private static int getNeowsLamentCounter(){
            for(AbstractRelic relic: AbstractDungeon.player.relics){
                if(relic.relicId.equals(NeowsLament.ID)){
                    return relic.counter;
                }
            }
            return -1; // This should not happen
        }
    }
}