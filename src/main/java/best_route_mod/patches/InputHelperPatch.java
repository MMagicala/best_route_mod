package best_route_mod.patches;

import basemod.BaseMod;
import basemod.DevConsole;
import best_route_mod.ColorPathManager;
import best_route_mod.MapPath;
import best_route_mod.MapReader;
import best_route_mod.RoomClassManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.map.Legend;

public class InputHelperPatch {
    private static boolean isMiddleButtonJustPressed = false;
    private static boolean isMiddleButtonPressedAfterFirstCycle = false;
    private static boolean _isEmeraldKeyRequired = false;
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

    @SpirePatch(
            clz= InputHelper.class,
            method="updateFirst"
    )
    public static class RequireEmeraldHotKeyPatch{
        @SpirePostfixPatch
        public static void Postfix() {
            if(AbstractDungeon.currMapNode != null) { // Make sure the map exists before checking for hotkey input
                if (Gdx.input.isKeyJustPressed(Input.Keys.Q)
                        && AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MAP
                        && MapReader.emeraldKeyExists()
                        && (!AbstractDungeon.firstRoomChosen
                        || MapReader.isEmeraldKeyReachableFrom(AbstractDungeon.currMapNode))
                        && !DevConsole.visible) {
                    _isEmeraldKeyRequired = !_isEmeraldKeyRequired;
                    if (!RoomClassManager.allRoomClassesInActive()) {
                        reRenderPath();
                    }
                }
            }
        }

        private static void reRenderPath(){
            ColorPathManager.disableCurrentlyColoredPath();
            MapPath bestPath;
            if(AbstractDungeon.firstRoomChosen){
                bestPath = MapReader.getBestPathFrom(AbstractDungeon.currMapNode, _isEmeraldKeyRequired);
            }else{
                bestPath = MapReader.getBestPathFrom(MapReader.getStartingNodes(), _isEmeraldKeyRequired);
            }
            ColorPathManager.colorPath(bestPath);
        }
    }

    public static boolean isEmeraldKeyRequired(){
        return _isEmeraldKeyRequired;
    }

    public static void disableEmeraldKeyRequirement(){
        _isEmeraldKeyRequired = false;
    }
}